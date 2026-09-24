package com.bdysvik.workhome.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.text.KeyboardOptions
import com.bdysvik.workhome.AppContainer
import com.bdysvik.workhome.data.AppUser
import com.bdysvik.workhome.data.Chore
import com.bdysvik.workhome.data.ChoreTemplate
import com.bdysvik.workhome.data.PendingUser
import com.bdysvik.workhome.data.UserRole
import com.bdysvik.workhome.viewmodel.ChoresUiState
import com.bdysvik.workhome.viewmodel.ChoresViewModel
import com.bdysvik.workhome.viewmodel.LoginUiState
import com.bdysvik.workhome.viewmodel.LoginViewModel
import com.bdysvik.workhome.viewmodel.RewardsUiState
import com.bdysvik.workhome.viewmodel.RewardsViewModel
import com.bdysvik.workhome.viewmodel.SessionViewModel
import com.bdysvik.workhome.viewmodel.UsersUiState
import com.bdysvik.workhome.viewmodel.UsersViewModel

private object Routes {
    const val Chores = "chores"
    const val Rewards = "rewards"
    const val Users = "users"
}

@Composable
fun WorkHomeApp(
    appContainer: AppContainer?,
    firebaseConfigured: Boolean,
) {
    if (!firebaseConfigured || appContainer == null) {
        SetupRequiredScreen()
        return
    }

    val sessionViewModel: SessionViewModel = viewModel(factory = simpleFactory {
        SessionViewModel(appContainer.authRepository, appContainer.familyRepository)
    })
    val sessionState by sessionViewModel.uiState.collectAsStateWithLifecycle()

    val profile = sessionState.profile
    when {
        sessionState.isLoading -> LoadingScreen()
        sessionState.user == null -> {
            val loginViewModel: LoginViewModel = viewModel(factory = simpleFactory {
                LoginViewModel(appContainer.authRepository)
            })
            val loginState by loginViewModel.uiState.collectAsStateWithLifecycle()
            LoginScreen(
                state = loginState,
                onEmailChange = loginViewModel::updateEmail,
                onPasswordChange = loginViewModel::updatePassword,
                onModeToggle = loginViewModel::toggleMode,
                onSubmit = loginViewModel::submit,
            )
        }
        profile == null -> MissingProfileScreen(
            errorMessage = sessionState.bootstrapError,
            onSignOut = sessionViewModel::signOut,
        )
        else -> HomeScaffold(
            currentUser = profile,
            appContainer = appContainer,
            onSignOut = sessionViewModel::signOut,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScaffold(
    currentUser: AppUser,
    appContainer: AppContainer,
    onSignOut: () -> Unit,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Routes.Chores
    val navItems = buildList {
        add(Routes.Chores to "Chores")
        add(Routes.Rewards to "Rewards")
        if (currentUser.isAdmin) add(Routes.Users to "Users")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(navItems.firstOrNull { it.first == currentRoute }?.second ?: "WorkHome") },
                actions = {
                    TextButton(onClick = onSignOut) {
                        Text("Sign out")
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                navItems.forEach { (route, label) ->
                    val selected = backStackEntry?.destination?.hierarchy?.any { it.route == route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                navController.navigate(route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                }
                            }
                        },
                        icon = { Text(label.take(1)) },
                        label = { Text(label) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Chores,
            modifier = Modifier.padding(padding),
        ) {
            composable(Routes.Chores) {
                val vm: ChoresViewModel = viewModel(factory = simpleFactory {
                    ChoresViewModel(appContainer.familyRepository, currentUser)
                })
                val state by vm.uiState.collectAsStateWithLifecycle()
                ChoresScreen(
                    currentUser = currentUser,
                    state = state,
                    onTemplateTitleChange = vm::updateTemplateTitle,
                    onTemplateRewardChange = vm::updateTemplateReward,
                    onSaveTemplate = vm::saveTemplate,
                    onEditTemplate = vm::editTemplate,
                    onCancelTemplateEdit = vm::cancelTemplateEdit,
                    onActivateTemplate = vm::activateTemplate,
                    onDeleteTemplate = vm::deleteTemplate,
                    onCompleteChore = vm::completeChore,
                    onDeleteChore = vm::deleteChore,
                    onClearMessage = vm::clearMessage,
                )
            }
            composable(Routes.Rewards) {
                val vm: RewardsViewModel = viewModel(factory = simpleFactory {
                    RewardsViewModel(appContainer.familyRepository, currentUser)
                })
                val state by vm.uiState.collectAsStateWithLifecycle()
                RewardsScreen(state, vm::resetRewards, vm::clearMessage)
            }
            if (currentUser.isAdmin) {
                composable(Routes.Users) {
                    val vm: UsersViewModel = viewModel(factory = simpleFactory {
                        UsersViewModel(appContainer.familyRepository)
                    })
                    val state by vm.uiState.collectAsStateWithLifecycle()
                    UsersScreen(
                        state = state,
                        onNameChange = vm::updateName,
                        onEmailChange = vm::updateEmail,
                        onRoleChange = vm::updateRole,
                        onAddUser = vm::addPendingUser,
                        onRemoveUser = vm::removeUser,
                        onRemovePendingUser = vm::removePendingUser,
                        onClearMessage = vm::clearMessage,
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun SetupRequiredScreen() {
    CenteredMessage(
        title = "Firebase setup required",
        body = "Copy your Firebase app config into app/google-services.json, then rebuild the app.",
    )
}

@Composable
private fun MissingProfileScreen(
    errorMessage: String?,
    onSignOut: () -> Unit,
) {
    CenteredMessage(
        title = "Profile not found",
        body = errorMessage
            ?: "Your Firebase account is signed in, but no matching Firestore family profile exists yet. Ask an admin to add an invite for your email, or manually create the first admin profile in Firestore.",
        action = {
            Button(onClick = onSignOut) {
                Text("Sign out")
            }
        },
    )
}

@Composable
private fun CenteredMessage(
    title: String,
    body: String,
    action: @Composable (() -> Unit)? = null,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                action?.invoke()
            }
        }
    }
}

@Composable
private fun LoginScreen(
    state: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onModeToggle: () -> Unit,
    onSubmit: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "WorkHome",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = if (state.createAccount) "Create invited account" else "Sign in",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                OutlinedTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                )
                state.error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Button(onClick = onSubmit, enabled = !state.loading, modifier = Modifier.fillMaxWidth()) {
                    Text(if (state.loading) "Working..." else if (state.createAccount) "Create invited account" else "Sign in")
                }
                TextButton(onClick = onModeToggle, enabled = !state.loading) {
                    Text(if (state.createAccount) "Already have an account? Sign in" else "Need to claim an invite? Create account")
                }
            }
        }
    }
}

@Composable
private fun ChoresScreen(
    currentUser: AppUser,
    state: ChoresUiState,
    onTemplateTitleChange: (String) -> Unit,
    onTemplateRewardChange: (String) -> Unit,
    onSaveTemplate: () -> Unit,
    onEditTemplate: (ChoreTemplate) -> Unit,
    onCancelTemplateEdit: () -> Unit,
    onActivateTemplate: (ChoreTemplate) -> Unit,
    onDeleteTemplate: (ChoreTemplate) -> Unit,
    onCompleteChore: (Chore) -> Unit,
    onDeleteChore: (Chore) -> Unit,
    onClearMessage: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var choreToDelete by remember { mutableStateOf<Chore?>(null) }
    var templateToDelete by remember { mutableStateOf<ChoreTemplate?>(null) }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            onClearMessage()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (currentUser.isAdmin) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(if (state.editingTemplateId == null) "Save chore template" else "Edit chore template", fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = state.templateTitle,
                                onValueChange = onTemplateTitleChange,
                                label = { Text("Title") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !state.templateFormSubmitting,
                            )
                            OutlinedTextField(
                                value = state.templateRewardText,
                                onValueChange = onTemplateRewardChange,
                                label = { Text("Reward") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                enabled = !state.templateFormSubmitting,
                            )
                            Button(onClick = onSaveTemplate, enabled = !state.templateFormSubmitting) {
                                Text(if (state.templateFormSubmitting) "Saving..." else if (state.editingTemplateId == null) "Save template" else "Update template")
                            }
                            if (state.editingTemplateId != null) {
                                OutlinedButton(onClick = onCancelTemplateEdit, enabled = !state.templateFormSubmitting) {
                                    Text("Cancel edit")
                                }
                            }
                            if (state.templateFormSubmitting) {
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                }

                if (state.choreTemplates.isNotEmpty()) {
                    item {
                        Text("Templates", fontWeight = FontWeight.Bold)
                    }
                    items(state.choreTemplates, key = { it.id }) { template ->
                        ChoreTemplateRow(
                            template = template,
                            submitting = state.templateActionTemplateId != null,
                            isWorking = state.templateActionTemplateId == template.id,
                            onEditTemplate = onEditTemplate,
                            onActivateTemplate = onActivateTemplate,
                            onDeleteTemplate = { templateToDelete = it },
                        )
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Your total: ${currentUser.currentRewardTotal}")
                    Text("Active chores", fontWeight = FontWeight.Bold)
                }
            }

            items(state.chores, key = { it.id }) { chore ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(chore.description, fontWeight = FontWeight.Bold)
                        Text("Reward: ${chore.reward}")
                        Button(onClick = { onCompleteChore(chore) }, enabled = !state.choreSubmitting) {
                            Text("Complete for me")
                        }
                        if (currentUser.isAdmin) {
                            OutlinedButton(onClick = { choreToDelete = chore }, enabled = !state.choreSubmitting) {
                                Text("Delete chore")
                            }
                        }
                    }
                }
            }
        }
    }

    choreToDelete?.let { chore ->
        AlertDialog(
            onDismissRequest = { choreToDelete = null },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteChore(chore)
                    choreToDelete = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { choreToDelete = null }) {
                    Text("Cancel")
                }
            },
            title = { Text("Delete chore?") },
            text = { Text(chore.description) },
        )
    }

    templateToDelete?.let { template ->
        AlertDialog(
            onDismissRequest = { templateToDelete = null },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteTemplate(template)
                    templateToDelete = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { templateToDelete = null }) {
                    Text("Cancel")
                }
            },
            title = { Text("Delete template?") },
            text = { Text(template.title) },
        )
    }
}

@Composable
private fun ChoreTemplateRow(
    template: ChoreTemplate,
    submitting: Boolean,
    isWorking: Boolean,
    onEditTemplate: (ChoreTemplate) -> Unit,
    onActivateTemplate: (ChoreTemplate) -> Unit,
    onDeleteTemplate: (ChoreTemplate) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(template.title, fontWeight = FontWeight.Bold)
            Text("Reward: ${template.reward}")
            Button(
                onClick = { onActivateTemplate(template) },
                enabled = !submitting,
                modifier = Modifier.semantics { contentDescription = "Activate template ${template.title}" },
            ) {
                Text(if (isWorking) "Working..." else "Activate")
            }
            OutlinedButton(
                onClick = { onEditTemplate(template) },
                enabled = !submitting,
                modifier = Modifier.semantics { contentDescription = "Edit template ${template.title}" },
            ) {
                Text("Edit")
            }
            OutlinedButton(
                onClick = { onDeleteTemplate(template) },
                enabled = !submitting,
                modifier = Modifier.semantics { contentDescription = "Delete template ${template.title}" },
            ) {
                Text("Delete")
            }
        }
    }
}

@Composable
private fun UsersScreen(
    state: UsersUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onRoleChange: (UserRole) -> Unit,
    onAddUser: () -> Unit,
    onRemoveUser: (AppUser) -> Unit,
    onRemovePendingUser: (PendingUser) -> Unit,
    onClearMessage: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var userToRemove by remember { mutableStateOf<AppUser?>(null) }
    var pendingToRemove by remember { mutableStateOf<PendingUser?>(null) }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            onClearMessage()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Invite family member", fontWeight = FontWeight.Bold)
                        OutlinedTextField(value = state.name, onValueChange = onNameChange, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = state.email, onValueChange = onEmailChange, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                        Text("Role")
                        RowRoles(selectedRole = state.role, onRoleChange = onRoleChange)
                        Button(onClick = onAddUser, enabled = !state.submitting) {
                            Text(if (state.submitting) "Saving..." else "Add invite")
                        }
                        Text("Password workflow: invite the user here, then have them use Create invited account on the login screen. Passwords stay in Firebase Auth and are never stored in Firestore.")
                    }
                }
            }

            item { Text("Active users", fontWeight = FontWeight.Bold) }
            items(state.users, key = { it.id }) { user ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(user.name, fontWeight = FontWeight.Bold)
                        Text(user.email)
                        Text("Role: ${user.role.value}")
                        OutlinedButton(onClick = { userToRemove = user }, enabled = !state.submitting) {
                            Text("Remove profile")
                        }
                    }
                }
            }

            item { Text("Pending invites", fontWeight = FontWeight.Bold) }
            items(state.pendingUsers, key = { it.emailKey }) { pendingUser ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(pendingUser.name, fontWeight = FontWeight.Bold)
                        Text(pendingUser.email)
                        Text("Role: ${pendingUser.role.value}")
                        OutlinedButton(onClick = { pendingToRemove = pendingUser }, enabled = !state.submitting) {
                            Text("Remove invite")
                        }
                    }
                }
            }
        }
    }

    userToRemove?.let { user ->
        ConfirmDialog(
            title = "Remove user profile?",
            body = "This deletes the Firestore profile for ${user.email}. Delete the Firebase Auth user separately from the Firebase Console or a trusted backend if needed.",
            onConfirm = {
                onRemoveUser(user)
                userToRemove = null
            },
            onDismiss = { userToRemove = null },
        )
    }

    pendingToRemove?.let { pendingUser ->
        ConfirmDialog(
            title = "Remove invite?",
            body = "This removes the pending invite for ${pendingUser.email}.",
            onConfirm = {
                onRemovePendingUser(pendingUser)
                pendingToRemove = null
            },
            onDismiss = { pendingToRemove = null },
        )
    }
}

@Composable
private fun RowRoles(
    selectedRole: UserRole,
    onRoleChange: (UserRole) -> Unit,
) {
    androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf(UserRole.MEMBER, UserRole.ADMIN).forEach { role ->
            androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = selectedRole == role, onClick = { onRoleChange(role) })
                Text(role.value)
            }
        }
    }
}

@Composable
private fun RewardsScreen(
    state: RewardsUiState,
    onResetRewards: () -> Unit,
    onClearMessage: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showResetConfirmation by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            onClearMessage()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            if (state.currentUser.isAdmin) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Admin controls", fontWeight = FontWeight.Bold)
                            Text("Reset archives the current totals into rewardHistory before setting every user total back to zero.")
                            Button(onClick = { showResetConfirmation = true }, enabled = !state.resetting) {
                                Text(if (state.resetting) "Resetting..." else "Reset rewards")
                            }
                        }
                    }
                }
            }

            items(state.users, key = { it.id }) { user ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(user.name, fontWeight = FontWeight.Bold)
                        Text("Total rewards: ${user.currentRewardTotal}")
                    }
                }
            }
        }
    }

    if (showResetConfirmation) {
        ConfirmDialog(
            title = "Reset all rewards?",
            body = "This archives the current totals and zeroes out every family member's running total.",
            onConfirm = {
                onResetRewards()
                showResetConfirmation = false
            },
            onDismiss = { showResetConfirmation = false },
        )
    }
}

@Composable
private fun ConfirmDialog(
    title: String,
    body: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text(title) },
        text = { Text(body) },
    )
}

private fun <T : ViewModel> simpleFactory(creator: () -> T): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = creator() as VM
    }
