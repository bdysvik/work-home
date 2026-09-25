package com.bdysvik.workhome.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdysvik.workhome.data.AppUser
import com.bdysvik.workhome.data.AuthRepository
import com.bdysvik.workhome.data.Chore
import com.bdysvik.workhome.data.ChoreTemplate
import com.bdysvik.workhome.data.CompletedChore
import com.bdysvik.workhome.data.FamilyRepository
import com.bdysvik.workhome.data.InputValidators
import com.bdysvik.workhome.data.PendingUser
import com.bdysvik.workhome.data.UserRole
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SessionUiState(
    val isLoading: Boolean = true,
    val user: com.google.firebase.auth.FirebaseUser? = null,
    val profile: AppUser? = null,
    val bootstrapError: String? = null,
)

class SessionViewModel(
    private val authRepository: AuthRepository,
    private val familyRepository: FamilyRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.authState.collectLatest { firebaseUser ->
                if (firebaseUser == null) {
                    _uiState.value = SessionUiState(isLoading = false)
                } else {
                    val bootstrapError = runCatching {
                        familyRepository.bootstrapUserProfile(firebaseUser.uid, firebaseUser.email)
                    }.exceptionOrNull()?.localizedMessage
                    familyRepository.observeUser(firebaseUser.uid)
                        .catch { e ->
                            _uiState.value = SessionUiState(
                                isLoading = false,
                                user = firebaseUser,
                                bootstrapError = e.localizedMessage,
                            )
                        }
                        .collect { profile ->
                            _uiState.value = SessionUiState(
                                isLoading = false,
                                user = firebaseUser,
                                profile = profile,
                                bootstrapError = bootstrapError,
                            )
                        }
                }
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val createAccount: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null,
)

class LoginViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun updateEmail(value: String) = _uiState.update { it.copy(email = value, error = null) }
    fun updatePassword(value: String) = _uiState.update { it.copy(password = value, error = null) }
    fun toggleMode() = _uiState.update { it.copy(createAccount = !it.createAccount, error = null) }

    fun submit() {
        val state = _uiState.value
        val validationError = InputValidators.validateCredentials(state.email, state.password, state.createAccount)
        if (validationError != null) {
            _uiState.update { it.copy(error = validationError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            val result = runCatching {
                if (state.createAccount) {
                    authRepository.signUp(state.email, state.password)
                } else {
                    authRepository.signIn(state.email, state.password)
                }
            }
            val exception = result.exceptionOrNull()
            val errorMessage = when {
                exception == null -> null
                state.createAccount && exception is FirebaseAuthUserCollisionException ->
                    "This email is already registered in Firebase. Switch to 'Sign in' below and enter your password."
                else -> exception.localizedMessage
            }
            _uiState.update {
                it.copy(
                    loading = false,
                    error = errorMessage,
                )
            }
        }
    }
}

data class ChoresUiState(
    val currentUser: AppUser,
    val choreTemplates: List<ChoreTemplate> = emptyList(),
    val chores: List<Chore> = emptyList(),
    val completedChores: List<CompletedChore> = emptyList(),
    val usersList: List<AppUser> = emptyList(),
    val usersByAuthUid: Map<String, String> = emptyMap(),
    val choreTitleInput: String = "",
    val choreDescriptionInput: String = "",
    val choreRewardInputText: String = "",
    val selectedAssigneeUser: AppUser? = null,
    val markCompletedInput: Boolean = false,
    val createChoreSubmitting: Boolean = false,
    val templateTitle: String = "",
    val templateRewardText: String = "",
    val templateRepeatIntervalDays: Int? = null,
    val editingTemplateId: String? = null,
    val templateFormSubmitting: Boolean = false,
    val busyTemplateActions: Map<String, TemplateRowAction> = emptyMap(),
    val busyChoreActions: Map<String, ChoreRowAction> = emptyMap(),
    val message: String? = null,
)

enum class TemplateRowAction {
    ACTIVATE,
    DELETE,
}

enum class ChoreRowAction {
    ASSIGN,
    COMPLETE,
    APPROVE,
    RESET,
    DELETE,
}

class ChoresViewModel(
    private val familyRepository: FamilyRepository,
    currentUser: AppUser,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChoresUiState(currentUser = currentUser))
    val uiState: StateFlow<ChoresUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching { familyRepository.generateScheduledChores() }
        }
        if (currentUser.isAdmin) {
            viewModelScope.launch {
                familyRepository.observeChoreTemplates()
                    .catch { e -> _uiState.update { it.copy(message = e.localizedMessage) } }
                    .collect { templates ->
                        _uiState.update { it.copy(choreTemplates = templates) }
                    }
            }
        }
        viewModelScope.launch {
            familyRepository.observeChores(currentUser)
                .catch { e -> _uiState.update { it.copy(message = e.localizedMessage) } }
                .collect { chores ->
                    _uiState.update { it.copy(chores = chores) }
                }
        }
        viewModelScope.launch {
            familyRepository.observeCompletedChores()
                .catch { e -> _uiState.update { it.copy(message = e.localizedMessage) } }
                .collect { completed ->
                    _uiState.update { it.copy(completedChores = completed) }
                }
        }
        viewModelScope.launch {
            familyRepository.observeUsers()
                .catch { e -> _uiState.update { it.copy(message = e.localizedMessage) } }
                .collect { users ->
                    _uiState.update {
                        it.copy(
                            usersList = users,
                            usersByAuthUid = users.associate { user -> user.authUid to user.name },
                        )
                    }
                }
        }
    }

    fun updateChoreTitleInput(value: String) = _uiState.update { it.copy(choreTitleInput = value) }
    fun updateChoreDescriptionInput(value: String) = _uiState.update { it.copy(choreDescriptionInput = value) }
    fun updateChoreRewardInput(value: String) = _uiState.update { it.copy(choreRewardInputText = value) }
    fun selectAssigneeUser(user: AppUser?) = _uiState.update { it.copy(selectedAssigneeUser = user) }
    fun toggleMarkCompleted(value: Boolean) = _uiState.update { it.copy(markCompletedInput = value) }

    fun createChore() {
        val state = _uiState.value
        if (state.createChoreSubmitting) return
        val error = InputValidators.validateChore(state.choreTitleInput, state.choreRewardInputText)
        if (error != null) {
            _uiState.update { it.copy(message = error) }
            return
        }
        val reward = InputValidators.parseReward(state.choreRewardInputText) ?: return

        val targetAssignee = state.selectedAssigneeUser
            ?: if (!state.currentUser.isAdmin && state.markCompletedInput) state.currentUser else null

        val needsApproval = !state.currentUser.isAdmin && state.markCompletedInput
        val markCompleted = state.markCompletedInput && (state.currentUser.isAdmin || targetAssignee != null)

        viewModelScope.launch {
            _uiState.update { it.copy(createChoreSubmitting = true, message = null) }
            val result = runCatching {
                familyRepository.addChore(
                    title = state.choreTitleInput,
                    reward = reward,
                    createdBy = state.currentUser.authUid,
                    assignedToUser = targetAssignee,
                    markCompleted = markCompleted,
                    description = state.choreDescriptionInput,
                    needsApproval = needsApproval,
                )
            }
            _uiState.update {
                it.copy(
                    choreTitleInput = if (result.isSuccess) "" else it.choreTitleInput,
                    choreDescriptionInput = if (result.isSuccess) "" else it.choreDescriptionInput,
                    choreRewardInputText = if (result.isSuccess) "" else it.choreRewardInputText,
                    selectedAssigneeUser = if (result.isSuccess) null else it.selectedAssigneeUser,
                    markCompletedInput = if (result.isSuccess) false else it.markCompletedInput,
                    createChoreSubmitting = false,
                    message = result.exceptionOrNull()?.localizedMessage
                        ?: if (result.isSuccess) {
                            if (needsApproval) {
                                "Custom chore submitted and awaiting approval!"
                            } else if (markCompleted) {
                                "Chore created and marked completed!"
                            } else if (targetAssignee != null) {
                                "Chore created and assigned!"
                            } else {
                                "Chore created!"
                            }
                        } else null,
                )
            }
        }
    }

    fun updateTemplateTitle(value: String) = _uiState.update { it.copy(templateTitle = value) }
    fun updateTemplateReward(value: String) = _uiState.update { it.copy(templateRewardText = value) }
    fun updateTemplateRepeatInterval(days: Int?) = _uiState.update { it.copy(templateRepeatIntervalDays = days) }
    fun clearMessage() = _uiState.update { it.copy(message = null) }
    fun cancelTemplateEdit() {
        if (
            !_uiState.value.currentUser.isAdmin ||
            _uiState.value.templateFormSubmitting ||
            _uiState.value.busyTemplateActions.isNotEmpty()
        ) return
        _uiState.update {
            it.copy(
                templateTitle = "",
                templateRewardText = "",
                templateRepeatIntervalDays = null,
                editingTemplateId = null,
            )
        }
    }

    fun editTemplate(template: ChoreTemplate) {
        if (
            !_uiState.value.currentUser.isAdmin ||
            _uiState.value.templateFormSubmitting ||
            _uiState.value.busyTemplateActions.isNotEmpty()
        ) return
        _uiState.update {
            it.copy(
                templateTitle = template.title,
                templateRewardText = template.reward.toString(),
                templateRepeatIntervalDays = template.repeatIntervalDays,
                editingTemplateId = template.id,
            )
        }
    }

    fun saveTemplate() {
        val state = _uiState.value
        if (!state.currentUser.isAdmin || state.templateFormSubmitting || state.busyTemplateActions.isNotEmpty()) return
        val error = InputValidators.validateChore(state.templateTitle, state.templateRewardText)
        if (error != null) {
            _uiState.update { it.copy(message = error) }
            return
        }
        val reward = InputValidators.parseReward(state.templateRewardText) ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(templateFormSubmitting = true, message = null) }
            val result = runCatching {
                if (state.editingTemplateId == null) {
                    familyRepository.addChoreTemplate(
                        state.templateTitle,
                        reward,
                        state.currentUser.authUid,
                        state.templateRepeatIntervalDays,
                    )
                } else {
                    familyRepository.updateChoreTemplate(
                        state.editingTemplateId,
                        state.templateTitle,
                        reward,
                        state.templateRepeatIntervalDays,
                    )
                }
            }
            _uiState.update {
                it.copy(
                    templateTitle = if (result.isSuccess) "" else it.templateTitle,
                    templateRewardText = if (result.isSuccess) "" else it.templateRewardText,
                    templateRepeatIntervalDays = if (result.isSuccess) null else it.templateRepeatIntervalDays,
                    editingTemplateId = if (result.isSuccess) null else it.editingTemplateId,
                    templateFormSubmitting = false,
                    message = result.exceptionOrNull()?.localizedMessage ?: if (result.isSuccess) {
                        if (state.editingTemplateId == null) "Template saved." else "Template updated."
                    } else {
                        null
                    },
                )
            }
        }
    }

    fun activateTemplate(template: ChoreTemplate) {
        if (
            !_uiState.value.currentUser.isAdmin ||
            _uiState.value.templateFormSubmitting ||
            _uiState.value.busyTemplateActions[template.id] != null
        ) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    busyTemplateActions = it.busyTemplateActions + (template.id to TemplateRowAction.ACTIVATE),
                    message = null,
                )
            }
            val result = runCatching { familyRepository.activateChoreTemplate(template, _uiState.value.currentUser.authUid) }
            _uiState.update {
                it.copy(
                    busyTemplateActions = it.busyTemplateActions - template.id,
                    message = result.exceptionOrNull()?.localizedMessage ?: if (result.isSuccess) "Chore activated." else null,
                )
            }
        }
    }

    fun deleteTemplate(template: ChoreTemplate) {
        if (
            !_uiState.value.currentUser.isAdmin ||
            _uiState.value.templateFormSubmitting ||
            _uiState.value.busyTemplateActions[template.id] != null
        ) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    busyTemplateActions = it.busyTemplateActions + (template.id to TemplateRowAction.DELETE),
                    message = null,
                )
            }
            val result = runCatching { familyRepository.deleteChoreTemplate(template.id) }
            _uiState.update {
                it.copy(
                    templateTitle = if (result.isSuccess && it.editingTemplateId == template.id) "" else it.templateTitle,
                    templateRewardText = if (result.isSuccess && it.editingTemplateId == template.id) "" else it.templateRewardText,
                    editingTemplateId = if (result.isSuccess && it.editingTemplateId == template.id) null else it.editingTemplateId,
                    busyTemplateActions = it.busyTemplateActions - template.id,
                    message = result.exceptionOrNull()?.localizedMessage ?: if (result.isSuccess) "Template deleted." else null,
                )
            }
        }
    }

    fun assignChore(chore: Chore) {
        if (_uiState.value.busyChoreActions[chore.id] != null) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    busyChoreActions = it.busyChoreActions + (chore.id to ChoreRowAction.ASSIGN),
                    message = null,
                )
            }
            val result = runCatching { familyRepository.assignChore(chore.id, _uiState.value.currentUser) }
            _uiState.update {
                it.copy(
                    busyChoreActions = it.busyChoreActions - chore.id,
                    message = result.exceptionOrNull()?.localizedMessage ?: if (result.isSuccess) "Chore assigned." else null,
                )
            }
        }
    }

    fun completeChore(chore: Chore) {
        if (_uiState.value.busyChoreActions[chore.id] != null) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    busyChoreActions = it.busyChoreActions + (chore.id to ChoreRowAction.COMPLETE),
                    message = null,
                )
            }
            val result = runCatching { familyRepository.completeChore(chore, _uiState.value.currentUser) }
            _uiState.update {
                it.copy(
                    busyChoreActions = it.busyChoreActions - chore.id,
                    message = result.exceptionOrNull()?.localizedMessage ?: if (result.isSuccess) "Minutes added." else null,
                )
            }
        }
    }

    fun approveChore(chore: Chore) {
        if (!_uiState.value.currentUser.isAdmin || _uiState.value.busyChoreActions[chore.id] != null) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    busyChoreActions = it.busyChoreActions + (chore.id to ChoreRowAction.APPROVE),
                    message = null,
                )
            }
            val result = runCatching { familyRepository.approveChore(chore, _uiState.value.currentUser) }
            _uiState.update {
                it.copy(
                    busyChoreActions = it.busyChoreActions - chore.id,
                    message = result.exceptionOrNull()?.localizedMessage
                        ?: if (result.isSuccess) "Chore approved and minutes added!" else null,
                )
            }
        }
    }

    fun deleteChore(chore: Chore) {
        if (_uiState.value.busyChoreActions[chore.id] != null) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    busyChoreActions = it.busyChoreActions + (chore.id to ChoreRowAction.DELETE),
                    message = null,
                )
            }
            val result = runCatching { familyRepository.deleteChore(chore.id) }
            _uiState.update {
                it.copy(
                    busyChoreActions = it.busyChoreActions - chore.id,
                    message = result.exceptionOrNull()?.localizedMessage ?: if (result.isSuccess) "Chore deleted." else null,
                )
            }
        }
    }

    fun resetChoreAssignment(chore: Chore) {
        if (_uiState.value.busyChoreActions[chore.id] != null) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    busyChoreActions = it.busyChoreActions + (chore.id to ChoreRowAction.RESET),
                    message = null,
                )
            }
            val result = runCatching { familyRepository.resetChoreAssignment(chore.id) }
            _uiState.update {
                it.copy(
                    busyChoreActions = it.busyChoreActions - chore.id,
                    message = result.exceptionOrNull()?.localizedMessage ?: if (result.isSuccess) "Chore assignment reset." else null,
                )
            }
        }
    }
}

data class UsersUiState(
    val users: List<AppUser> = emptyList(),
    val pendingUsers: List<PendingUser> = emptyList(),
    val name: String = "",
    val email: String = "",
    val role: UserRole = UserRole.MEMBER,
    val submitting: Boolean = false,
    val message: String? = null,
)

class UsersViewModel(
    private val familyRepository: FamilyRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UsersUiState())
    val uiState: StateFlow<UsersUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            familyRepository.observeUsers()
                .catch { e -> _uiState.update { it.copy(message = e.localizedMessage) } }
                .collect { users ->
                    _uiState.update { it.copy(users = users) }
                }
        }
        viewModelScope.launch {
            familyRepository.observePendingUsers()
                .catch { e -> _uiState.update { it.copy(message = e.localizedMessage) } }
                .collect { pendingUsers ->
                    _uiState.update { it.copy(pendingUsers = pendingUsers) }
                }
        }
    }

    fun updateName(value: String) = _uiState.update { it.copy(name = value) }
    fun updateEmail(value: String) = _uiState.update { it.copy(email = value) }
    fun updateRole(value: UserRole) = _uiState.update { it.copy(role = value) }
    fun clearMessage() = _uiState.update { it.copy(message = null) }

    fun addPendingUser() {
        val state = _uiState.value
        val error = InputValidators.validatePendingUser(state.name, state.email)
        if (error != null) {
            _uiState.update { it.copy(message = error) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(submitting = true, message = null) }
            val result = runCatching { familyRepository.addPendingUser(state.name, state.email, state.role) }
            _uiState.update {
                it.copy(
                    name = if (result.isSuccess) "" else it.name,
                    email = if (result.isSuccess) "" else it.email,
                    role = if (result.isSuccess) UserRole.MEMBER else it.role,
                    submitting = false,
                    message = result.exceptionOrNull()?.localizedMessage ?: if (result.isSuccess) "Invite added." else null,
                )
            }
        }
    }

    fun removeUser(user: AppUser) {
        viewModelScope.launch {
            _uiState.update { it.copy(submitting = true, message = null) }
            val result = runCatching { familyRepository.removeUser(user.id) }
            _uiState.update {
                it.copy(
                    submitting = false,
                    message = result.exceptionOrNull()?.localizedMessage ?: if (result.isSuccess) "User profile removed." else null,
                )
            }
        }
    }

    fun removePendingUser(user: PendingUser) {
        viewModelScope.launch {
            _uiState.update { it.copy(submitting = true, message = null) }
            val result = runCatching { familyRepository.removePendingUser(user.emailKey) }
            _uiState.update {
                it.copy(
                    submitting = false,
                    message = result.exceptionOrNull()?.localizedMessage ?: if (result.isSuccess) "Invite removed." else null,
                )
            }
        }
    }

    fun setUserGoal(user: AppUser, goal: Long?) {
        viewModelScope.launch {
            _uiState.update { it.copy(submitting = true, message = null) }
            val result = runCatching { familyRepository.setUserGoal(user.id, goal) }
            _uiState.update {
                it.copy(
                    submitting = false,
                    message = result.exceptionOrNull()?.localizedMessage
                        ?: if (result.isSuccess) "Goal updated for ${user.name}." else null,
                )
            }
        }
    }
}

data class RewardsUiState(
    val currentUser: AppUser,
    val users: List<AppUser> = emptyList(),
    val resetting: Boolean = false,
    val message: String? = null,
)

class RewardsViewModel(
    private val familyRepository: FamilyRepository,
    currentUser: AppUser,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RewardsUiState(currentUser = currentUser))
    val uiState: StateFlow<RewardsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            familyRepository.observeUsers()
                .catch { e -> _uiState.update { it.copy(message = e.localizedMessage) } }
                .collect { users ->
                    _uiState.update { it.copy(users = users.sortedByDescending(AppUser::currentRewardTotal)) }
                }
        }
    }

    fun clearMessage() = _uiState.update { it.copy(message = null) }

    fun resetRewards() {
        viewModelScope.launch {
            _uiState.update { it.copy(resetting = true, message = null) }
            val result = runCatching { familyRepository.resetRewards(_uiState.value.currentUser) }
            _uiState.update {
                it.copy(
                    resetting = false,
                    message = result.exceptionOrNull()?.localizedMessage ?: if (result.isSuccess) "Minutes reset and archived." else null,
                )
            }
        }
    }

    fun setUserGoal(user: AppUser, goal: Long?) {
        viewModelScope.launch {
            val result = runCatching { familyRepository.setUserGoal(user.id, goal) }
            _uiState.update {
                it.copy(
                    message = result.exceptionOrNull()?.localizedMessage
                        ?: if (result.isSuccess) "Goal updated for ${user.name}." else null,
                )
            }
        }
    }
}
