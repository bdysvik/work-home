package com.bdysvik.workhome.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
    fun observeChores(): Flow<List<Chore>>
    suspend fun bootstrapUserProfile(userId: String, email: String?)
    suspend fun addPendingUser(name: String, email: String, role: UserRole)
    suspend fun removePendingUser(emailKey: String)
    suspend fun removeUser(userId: String)
    suspend fun addChoreTemplate(title: String, reward: Long, createdBy: String)
    suspend fun updateChoreTemplate(templateId: String, title: String, reward: Long)
    suspend fun deleteChoreTemplate(templateId: String)
    suspend fun activateChoreTemplate(template: ChoreTemplate, createdBy: String)
    suspend fun addChore(title: String, reward: Long, createdBy: String)
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

    override fun observeChores(): Flow<List<Chore>> = collectionFlow(
        chores.whereEqualTo("active", true)
    ) { documents ->
        documents.mapNotNull { it.toChore() }.sortedBy { it.title }
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

    override suspend fun addChore(title: String, reward: Long, createdBy: String) {
        chores.add(choreData(title, reward, createdBy)).await()
    }

    override suspend fun deleteChore(choreId: String) {
        chores.document(choreId).delete().await()
    }

    override suspend fun completeChore(chore: Chore, user: AppUser) {
        val periodId = currentPeriodId()
        val completionRef = completions.document("${periodId}_${user.id}_${chore.id}")
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

            val userSnapshot = transaction.get(userRef)
            val currentRewardTotal = userSnapshot.getLong("currentRewardTotal") ?: 0L
            transaction.set(
                completionRef,
                mapOf(
                    "userId" to user.id,
                    "choreId" to chore.id,
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
        return AppUser(
            id = id,
            name = name,
            email = email,
            role = UserRole.from(getString("role")),
            authUid = authUid,
            currentRewardTotal = getLong("currentRewardTotal") ?: 0L,
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
): String? = (title ?: description)?.trim()?.takeIf { it.isNotEmpty() }

internal fun choreData(
    title: String,
    reward: Long,
    createdBy: String,
): Map<String, Any> = choreFields(title, reward, createdBy) + ("active" to true)

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
