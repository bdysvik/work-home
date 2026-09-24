package com.bdysvik.workhome.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdysvik.workhome.data.AppUser
import com.bdysvik.workhome.data.AuthRepository
import com.bdysvik.workhome.data.Chore
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
    val chores: List<Chore> = emptyList(),
    val usersByAuthUid: Map<String, String> = emptyMap(),
    val description: String = "",
    val rewardText: String = "",
    val submitting: Boolean = false,
    val message: String? = null,
)

class ChoresViewModel(
    private val familyRepository: FamilyRepository,
    currentUser: AppUser,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChoresUiState(currentUser = currentUser))
    val uiState: StateFlow<ChoresUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            familyRepository.observeChores(currentUser)
                .catch { e -> _uiState.update { it.copy(message = e.localizedMessage) } }
                .collect { chores ->
                    _uiState.update { it.copy(chores = chores) }
                }
        }
        viewModelScope.launch {
            familyRepository.observeUsers()
                .catch { e -> _uiState.update { it.copy(message = e.localizedMessage) } }
                .collect { users ->
                    _uiState.update {
                        it.copy(usersByAuthUid = users.associate { user -> user.authUid to user.name })
                    }
                }
        }
    }

    fun updateDescription(value: String) = _uiState.update { it.copy(description = value) }
    fun updateReward(value: String) = _uiState.update { it.copy(rewardText = value) }
    fun clearMessage() = _uiState.update { it.copy(message = null) }

    fun assignChore(chore: Chore) {
        viewModelScope.launch {
            _uiState.update { it.copy(submitting = true, message = null) }
            val result = runCatching { familyRepository.assignChore(chore.id, _uiState.value.currentUser) }
            _uiState.update {
                it.copy(
                    submitting = false,
                    message = result.exceptionOrNull()?.localizedMessage ?: if (result.isSuccess) "Chore assigned." else null,
                )
            }
        }
    }

    fun addChore() {
        val state = _uiState.value
        val error = InputValidators.validateChore(state.description, state.rewardText)
        if (error != null) {
            _uiState.update { it.copy(message = error) }
            return
        }
        val reward = InputValidators.parseReward(state.rewardText) ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(submitting = true, message = null) }
            val result = runCatching {
                familyRepository.addChore(state.description, reward, state.currentUser.authUid)
            }
            _uiState.update {
                it.copy(
                    description = if (result.isSuccess) "" else it.description,
                    rewardText = if (result.isSuccess) "" else it.rewardText,
                    submitting = false,
                    message = result.exceptionOrNull()?.localizedMessage ?: if (result.isSuccess) "Chore added." else null,
                )
            }
        }
    }

    fun completeChore(chore: Chore) {
        viewModelScope.launch {
            _uiState.update { it.copy(submitting = true, message = null) }
            val result = runCatching { familyRepository.completeChore(chore, _uiState.value.currentUser) }
            _uiState.update {
                it.copy(
                    submitting = false,
                    message = result.exceptionOrNull()?.localizedMessage ?: if (result.isSuccess) "Reward added." else null,
                )
            }
        }
    }

    fun deleteChore(chore: Chore) {
        viewModelScope.launch {
            _uiState.update { it.copy(submitting = true, message = null) }
            val result = runCatching { familyRepository.deleteChore(chore.id) }
            _uiState.update {
                it.copy(
                    submitting = false,
                    message = result.exceptionOrNull()?.localizedMessage ?: if (result.isSuccess) "Chore deleted." else null,
                )
            }
        }
    }

    fun resetChoreAssignment(chore: Chore) {
        viewModelScope.launch {
            _uiState.update { it.copy(submitting = true, message = null) }
            val result = runCatching { familyRepository.resetChoreAssignment(chore.id) }
            _uiState.update {
                it.copy(
                    submitting = false,
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
            familyRepository.observeUsers().collect { users ->
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
                    message = result.exceptionOrNull()?.localizedMessage ?: if (result.isSuccess) "Rewards reset and archived." else null,
                )
            }
        }
    }
}
