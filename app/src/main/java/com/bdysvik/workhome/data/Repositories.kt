package com.bdysvik.workhome.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface AuthRepository {
    val authState: Flow<FirebaseUser?>
    suspend fun signIn(email: String, password: String)
    suspend fun signUp(email: String, password: String)
    fun signOut()
}

class FirebaseAuthRepository(
    private val auth: FirebaseAuth,
) : AuthRepository {
    override val authState: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { authState ->
            trySend(authState.currentUser)
        }
        auth.addAuthStateListener(listener)
        trySend(auth.currentUser)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.distinctUntilChanged { old, new -> old?.uid == new?.uid }

    override suspend fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email.trim(), password).await()
    }

    override suspend fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email.trim(), password).await()
    }

    override fun signOut() {
        auth.signOut()
    }
}

interface FamilyRepository {
    fun observeUser(userId: String): Flow<AppUser?>
    fun observeUsers(): Flow<List<AppUser>>
    fun observePendingUsers(): Flow<List<PendingUser>>
    fun observeChoreTemplates(): Flow<List<ChoreTemplate>>
    fun observeChores(currentUser: AppUser): Flow<List<Chore>>
    fun observeCompletedChores(): Flow<List<CompletedChore>>
    suspend fun bootstrapUserProfile(userId: String, email: String?)
    suspend fun addPendingUser(name: String, email: String, role: UserRole)
    suspend fun removePendingUser(emailKey: String)
    suspend fun removeUser(userId: String)
    suspend fun addChoreTemplate(title: String, reward: Long, createdBy: String)
    suspend fun updateChoreTemplate(templateId: String, title: String, reward: Long)
    suspend fun deleteChoreTemplate(templateId: String)
    suspend fun activateChoreTemplate(template: ChoreTemplate, createdBy: String)
    suspend fun addChore(
        title: String,
        reward: Long,
        createdBy: String,
        assignedToUser: AppUser? = null,
        markCompleted: Boolean = false,
    )
    suspend fun assignChore(choreId: String, user: AppUser)
    suspend fun resetChoreAssignment(choreId: String)
    suspend fun deleteChore(choreId: String)
    suspend fun completeChore(chore: Chore, user: AppUser)
    suspend fun resetRewards(admin: AppUser)
}

class FirebaseFamilyRepository(
    private val firestore: FirebaseFirestore,
) : FamilyRepository {
    private val users = firestore.collection("users")
    private val chores = firestore.collection("chores")
    private val choreTemplates = firestore.collection("choreTemplates")
    private val rewardHistory = firestore.collection("rewardHistory")
    private val completions = firestore.collection("completions")
    private val pendingUsers = firestore.collection("pendingUsers")

    override fun observeUser(userId: String): Flow<AppUser?> = documentFlow(users.document(userId)) { snapshot ->
        snapshot?.toAppUser()
    }

    override fun observeUsers(): Flow<List<AppUser>> = collectionFlow(
        users.orderBy("name", Query.Direction.ASCENDING)
    ) { documents ->
        documents.mapNotNull { it.toAppUser() }
    }

    override fun observePendingUsers(): Flow<List<PendingUser>> = collectionFlow(
        pendingUsers.orderBy("email", Query.Direction.ASCENDING)
    ) { documents ->
        documents.mapNotNull { it.toPendingUser() }
    }

    override fun observeChoreTemplates(): Flow<List<ChoreTemplate>> = collectionFlow(
        choreTemplates.orderBy("title", Query.Direction.ASCENDING)
    ) { documents ->
        documents.mapNotNull { it.toChoreTemplate() }
    }

    override fun observeChores(currentUser: AppUser): Flow<List<Chore>> = collectionFlow(
        chores.whereEqualTo("active", true)
    ) { documents ->
        documents.mapNotNull { it.toChore() }
            .filter { chore ->
                currentUser.isAdmin || chore.assignedToUserId.isBlank() || chore.assignedToUserId == currentUser.authUid
            }
            .sortedBy { it.title }
    }

    override fun observeCompletedChores(): Flow<List<CompletedChore>> = collectionFlow(
        completions.orderBy("completedAt", Query.Direction.DESCENDING)
    ) { documents ->
        documents.mapNotNull { it.toCompletedChore() }
    }

    override suspend fun bootstrapUserProfile(userId: String, email: String?) {
        val normalizedEmail = email?.let(InputValidators::normalizeEmailKey) ?: return
        val userRef = users.document(userId)
        if (runCatching { userRef.get().await().exists() }.getOrDefault(false)) return

        val pendingRef = pendingUsers.document(normalizedEmail)
        val pendingSnapshot = runCatching { pendingRef.get().await() }.getOrNull()
        val pendingUser = pendingSnapshot?.takeIf { it.exists() }?.toPendingUser()

        val profileData = mapOf(
            "name" to (pendingUser?.name ?: email.substringBefore("@").replaceFirstChar { it.uppercase() }),
            "email" to (pendingUser?.email ?: email.trim().lowercase()),
            "role" to (pendingUser?.role?.value ?: UserRole.MEMBER.value),
            "authUid" to userId,
            "currentRewardTotal" to 0L,
            "lastCompletionId" to "",
        )

        userRef.set(profileData).await()
        if (pendingUser != null) {
            runCatching { pendingRef.delete().await() }
        }
    }

    override suspend fun addPendingUser(name: String, email: String, role: UserRole) {
        val normalizedEmail = InputValidators.normalizeEmailKey(email)
        pendingUsers.document(normalizedEmail).set(
            mapOf(
                "name" to name.trim(),
                "email" to email.trim().lowercase(),
                "role" to role.value,
                "createdAt" to FieldValue.serverTimestamp(),
            ),
        ).await()
    }

    override suspend fun removePendingUser(emailKey: String) {
        pendingUsers.document(emailKey).delete().await()
    }

    override suspend fun removeUser(userId: String) {
        users.document(userId).delete().await()
    }

    override suspend fun addChoreTemplate(title: String, reward: Long, createdBy: String) {
        choreTemplates.add(choreTemplateData(title, reward, createdBy)).await()
    }

    override suspend fun updateChoreTemplate(templateId: String, title: String, reward: Long) {
        choreTemplates.document(templateId).update(
            mapOf(
                "title" to title.trim(),
                "reward" to reward,
                "updatedAt" to FieldValue.serverTimestamp(),
            ),
        ).await()
    }

    override suspend fun deleteChoreTemplate(templateId: String) {
        choreTemplates.document(templateId).delete().await()
    }

    override suspend fun activateChoreTemplate(template: ChoreTemplate, createdBy: String) {
        addChore(title = template.title, reward = template.reward, createdBy = createdBy)
    }

    override suspend fun addChore(
        title: String,
        reward: Long,
        createdBy: String,
        assignedToUser: AppUser?,
        markCompleted: Boolean,
    ) {
        if (assignedToUser != null && markCompleted) {
            val periodId = currentPeriodId()
            val choreRef = chores.document()
            val completionRef = completions.document("${periodId}_${assignedToUser.authUid}_${choreRef.id}")
            val userRef = users.document(assignedToUser.id)

            firestore.runTransaction { transaction ->
                val userSnapshot = transaction.get(userRef)
                val currentRewardTotal = userSnapshot.getLong("currentRewardTotal") ?: 0L

                transaction.set(
                    choreRef,
                    mapOf(
                        "title" to title.trim(),
                        "reward" to reward,
                        "createdBy" to createdBy,
                        "active" to false,
                        "assignedTo" to assignedToUser.authUid,
                    ),
                )
                transaction.set(
                    completionRef,
                    mapOf(
                        "userId" to assignedToUser.authUid,
                        "choreId" to choreRef.id,
                        "choreTitle" to title.trim(),
                        "title" to title.trim(),
                        "userName" to assignedToUser.name,
                        "periodId" to periodId,
                        "reward" to reward,
                        "completedAt" to FieldValue.serverTimestamp(),
                    ),
                )
                transaction.update(
                    userRef,
                    mapOf(
                        "currentRewardTotal" to currentRewardTotal + reward,
                        "lastCompletionId" to completionRef.id,
                        "lastCompletedAt" to FieldValue.serverTimestamp(),
                    ),
                )
                null
            }.await()
        } else {
            chores.add(
                mapOf(
                    "title" to title.trim(),
                    "reward" to reward,
                    "createdBy" to createdBy,
                    "active" to true,
                    "assignedTo" to (assignedToUser?.authUid ?: ""),
                ),
            ).await()
        }
    }

    override suspend fun assignChore(choreId: String, user: AppUser) {
        val choreRef = chores.document(choreId)

        firestore.runTransaction { transaction ->
            val choreSnapshot = transaction.get(choreRef)
            val active = choreSnapshot.getBoolean("active") ?: false
            if (!active) {
                throw IllegalStateException("This chore is no longer active.")
            }

            val assignedTo = choreSnapshot.getString("assignedTo").orEmpty()
            if (assignedTo.isNotBlank()) {
                throw IllegalStateException("This chore is already assigned.")
            }

            transaction.update(choreRef, "assignedTo", user.authUid)
            null
        }.await()
    }

    override suspend fun resetChoreAssignment(choreId: String) {
        val choreRef = chores.document(choreId)

        firestore.runTransaction { transaction ->
            val choreSnapshot = transaction.get(choreRef)
            val active = choreSnapshot.getBoolean("active") ?: false
            if (!active) {
                throw IllegalStateException("This chore is no longer active.")
            }
            transaction.update(choreRef, "assignedTo", "")
            null
        }.await()
    }

    override suspend fun deleteChore(choreId: String) {
        chores.document(choreId).delete().await()
    }

    override suspend fun completeChore(chore: Chore, user: AppUser) {
        val periodId = currentPeriodId()
        val completionRef = completions.document("${periodId}_${user.authUid}_${chore.id}")
        val userRef = users.document(user.id)
        val choreRef = chores.document(chore.id)

        firestore.runTransaction { transaction ->
            val completionSnapshot = transaction.get(completionRef)
            if (completionSnapshot.exists()) {
                throw IllegalStateException("This chore is already completed for the current month.")
            }

            val choreSnapshot = transaction.get(choreRef)
            val active = choreSnapshot.getBoolean("active") ?: false
            if (!active) {
                throw IllegalStateException("This chore is no longer active.")
            }
            val assignedTo = choreSnapshot.getString("assignedTo").orEmpty()
            if (assignedTo != user.authUid) {
                throw IllegalStateException("Assign this chore to yourself before completing it.")
            }

            val userSnapshot = transaction.get(userRef)
            val currentRewardTotal = userSnapshot.getLong("currentRewardTotal") ?: 0L
            transaction.set(
                completionRef,
                mapOf(
                    "userId" to user.authUid,
                    "choreId" to chore.id,
                    "choreTitle" to chore.title,
                    "title" to chore.title,
                    "userName" to user.name,
                    "periodId" to periodId,
                    "reward" to chore.reward,
                    "completedAt" to FieldValue.serverTimestamp(),
                ),
            )
            transaction.update(
                userRef,
                mapOf(
                    "currentRewardTotal" to currentRewardTotal + chore.reward,
                    "lastCompletionId" to completionRef.id,
                    "lastCompletedAt" to FieldValue.serverTimestamp(),
                ),
            )
            null
        }.await()
    }

    override suspend fun resetRewards(admin: AppUser) {
        val periodId = currentPeriodId()
        val historyId = rewardHistoryId(periodId, System.currentTimeMillis())
        val userSnapshots = users.get().await().documents

        firestore.runTransaction { transaction ->
            val totals = userSnapshots.associate { snapshot ->
                snapshot.id to (snapshot.getLong("currentRewardTotal") ?: 0L)
            }

            transaction.set(
                rewardHistory.document(historyId),
                mapOf(
                    "periodId" to periodId,
                    "resetAt" to FieldValue.serverTimestamp(),
                    "resetBy" to admin.authUid,
                    "totals" to totals,
                ),
            )
            userSnapshots.forEach { snapshot ->
                transaction.update(
                    snapshot.reference,
                    mapOf(
                        "currentRewardTotal" to 0L,
                        "lastCompletionId" to "",
                    ),
                )
            }
            null
        }.await()
    }

    private fun <T> documentFlow(
        reference: com.google.firebase.firestore.DocumentReference,
        mapper: (com.google.firebase.firestore.DocumentSnapshot?) -> T,
    ): Flow<T> = callbackFlow {
        val registration = reference.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
            } else {
                trySend(mapper(snapshot))
            }
        }
        awaitClose { registration.remove() }
    }

    private fun <T> collectionFlow(
        query: Query,
        mapper: (List<com.google.firebase.firestore.DocumentSnapshot>) -> T,
    ): Flow<T> = callbackFlow {
        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
            } else {
                trySend(mapper(snapshot?.documents.orEmpty()))
            }
        }
        awaitClose { registration.remove() }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toAppUser(): AppUser? {
        val email = getString("email") ?: return null
        val name = getString("name") ?: return null
        val authUid = getString("authUid") ?: id
        val lastCompletedAtMillis = getTimestamp("lastCompletedAt")?.toDate()?.time
            ?: getLong("lastCompletedAt")
        return AppUser(
            id = id,
            name = name,
            email = email,
            role = UserRole.from(getString("role")),
            authUid = authUid,
            currentRewardTotal = getLong("currentRewardTotal") ?: 0L,
            lastCompletedAtMillis = lastCompletedAtMillis,
        )
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toPendingUser(): PendingUser? {
        val email = getString("email") ?: return null
        val name = getString("name") ?: return null
        return PendingUser(
            emailKey = id,
            name = name,
            email = email,
            role = UserRole.from(getString("role")),
        )
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toChoreTemplate(): ChoreTemplate? {
        val title = choreTitle(getString("title"), null) ?: return null
        return ChoreTemplate(
            id = id,
            title = title,
            reward = getLong("reward") ?: 0L,
            createdBy = getString("createdBy") ?: "",
        )
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toChore(): Chore? {
        val title = choreTitle(getString("title"), getString("description")) ?: return null
        return Chore(
            id = id,
            title = title,
            reward = getLong("reward") ?: 0L,
            createdBy = getString("createdBy") ?: "",
            active = getBoolean("active") ?: true,
            assignedToUserId = getString("assignedTo").orEmpty(),
        )
    }

    private fun DocumentSnapshot.toCompletedChore(): CompletedChore? {
        val choreId = getString("choreId") ?: return null
        val userId = getString("userId") ?: return null
        val title = choreTitle(
            getString("choreTitle") ?: getString("title"),
            getString("description"),
        ) ?: "Completed chore"
        val reward = getLong("reward") ?: 0L
        val userName = getString("userName").orEmpty()
        val completedAtMillis = getTimestamp("completedAt")?.toDate()?.time
            ?: getLong("completedAt")
        return CompletedChore(
            id = id,
            choreId = choreId,
            title = title,
            reward = reward,
            userId = userId,
            userName = userName,
            completedAtMillis = completedAtMillis,
        )
    }

    private fun currentPeriodId(): String =
        SimpleDateFormat("yyyy-MM", Locale.US).format(Date())
}

internal fun bootstrapProfileData(
    userId: String,
    pendingUser: PendingUser?,
): Map<String, Any>? = pendingUser?.let {
    mapOf(
        "name" to it.name,
        "email" to it.email,
        "role" to it.role.value,
        "authUid" to userId,
        "currentRewardTotal" to 0L,
        "lastCompletionId" to "",
    )
}

internal fun choreTitle(
    title: String?,
    description: String?,
): String? = title?.trim()?.takeIf { it.isNotEmpty() }
    ?: description?.trim()?.takeIf { it.isNotEmpty() }

internal fun choreData(
    title: String,
    reward: Long,
    createdBy: String,
): Map<String, Any> = choreFields(title, reward, createdBy) + mapOf(
    "active" to true,
    "assignedTo" to "",
)

internal fun choreTemplateData(
    title: String,
    reward: Long,
    createdBy: String,
): Map<String, Any> = choreFields(title, reward, createdBy) + ("updatedAt" to FieldValue.serverTimestamp())

private fun choreFields(
    title: String,
    reward: Long,
    createdBy: String,
): Map<String, Any> = mapOf(
    "title" to title.trim(),
    "reward" to reward,
    "createdBy" to createdBy,
)

internal fun rewardHistoryId(
    periodId: String,
    timestampMillis: Long,
): String = "$periodId-$timestampMillis"
