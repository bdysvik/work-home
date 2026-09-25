package com.bdysvik.workhome.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DryCleaning
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bdysvik.workhome.AppContainer
import com.bdysvik.workhome.data.AppUser
import com.bdysvik.workhome.data.Chore
import com.bdysvik.workhome.data.ChoreTemplate
import com.bdysvik.workhome.data.CompletedChore
import com.bdysvik.workhome.data.PendingUser
import com.bdysvik.workhome.data.UserRole
import com.bdysvik.workhome.ui.theme.WorkHomeColors
import com.bdysvik.workhome.ui.theme.WorkHomeDimens
import com.bdysvik.workhome.ui.theme.WorkHomeShapes
import com.bdysvik.workhome.viewmodel.ChoreRowAction
import com.bdysvik.workhome.viewmodel.ChoresUiState
import com.bdysvik.workhome.viewmodel.ChoresViewModel
import com.bdysvik.workhome.viewmodel.LoginUiState
import com.bdysvik.workhome.viewmodel.LoginViewModel
import com.bdysvik.workhome.viewmodel.RewardsUiState
import com.bdysvik.workhome.viewmodel.RewardsViewModel
import com.bdysvik.workhome.viewmodel.SessionViewModel
import com.bdysvik.workhome.viewmodel.TemplateRowAction
import com.bdysvik.workhome.viewmodel.UsersUiState
import com.bdysvik.workhome.viewmodel.UsersViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime
import kotlinx.coroutines.delay

private object Routes {
    const val Chores = "chores"
    const val CreateChore = "create_chore"
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
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val context = LocalContext.current
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = {},
        )
        LaunchedEffect(Unit) {
            val isGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
            if (!isGranted) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Routes.Chores
    val navItems = buildList {
        add(Routes.Chores to "Chores")
        add(Routes.CreateChore to "Add chore")
        add(Routes.Rewards to "Minutes")
        if (currentUser.isAdmin) add(Routes.Users to "Users")
    }

    Scaffold(
        containerColor = WorkHomeColors.Background,
        contentColor = WorkHomeColors.PrimaryText,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = navItems.firstOrNull { it.first == currentRoute }?.second ?: "WorkHome",
                        fontWeight = FontWeight.Bold,
                        color = WorkHomeColors.PrimaryText,
                    )
                },
                actions = {
                    IconButton(onClick = onSignOut) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Sign out",
                            tint = WorkHomeColors.SecondaryText,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WorkHomeColors.Background,
                    titleContentColor = WorkHomeColors.PrimaryText,
                    actionIconContentColor = WorkHomeColors.SecondaryText,
                ),
            )
        },
        bottomBar = {
            Surface(
                shape = WorkHomeShapes.BottomNavShape,
                color = WorkHomeColors.CardBackground.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, WorkHomeColors.NavBorderBrush),
                shadowElevation = 8.dp,
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp,
                ) {
                    navItems.forEach { (route, label) ->
                        val selected = backStackEntry?.destination?.hierarchy?.any { it.route == route } == true
                        val iconVector = when (route) {
                            Routes.Chores -> Icons.Filled.TaskAlt
                            Routes.CreateChore -> Icons.Filled.AddCircle
                            Routes.Rewards -> Icons.Filled.Schedule
                            Routes.Users -> Icons.Filled.Group
                            else -> Icons.Filled.Star
                        }
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
                            icon = {
                                Icon(
                                    imageVector = iconVector,
                                    contentDescription = label,
                                )
                            },
                            label = {
                                Text(
                                    label,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = WorkHomeColors.CyanAccent,
                                selectedTextColor = WorkHomeColors.CyanAccent,
                                indicatorColor = Color(0xFF142B50),
                                unselectedIconColor = WorkHomeColors.SecondaryText.copy(alpha = 0.7f),
                                unselectedTextColor = WorkHomeColors.SecondaryText.copy(alpha = 0.7f),
                            ),
                        )
                    }
                }
            }
        },
    ) { padding ->
        val choresViewModel: ChoresViewModel = viewModel(
            key = currentUser.id,
            factory = simpleFactory {
                ChoresViewModel(appContainer.familyRepository, currentUser)
            },
        )
        val choresState by choresViewModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(currentUser) {
            choresViewModel.updateCurrentUser(currentUser)
        }

        NavHost(
            navController = navController,
            startDestination = Routes.Chores,
            modifier = Modifier.padding(padding),
        ) {
            composable(Routes.Chores) {
                ChoresScreen(
                    currentUser = currentUser,
                    state = choresState,
                    onTemplateTitleChange = choresViewModel::updateTemplateTitle,
                    onTemplateRewardChange = choresViewModel::updateTemplateReward,
                    onTemplateRepeatIntervalChange = choresViewModel::updateTemplateRepeatInterval,
                    onSaveTemplate = choresViewModel::saveTemplate,
                    onEditTemplate = choresViewModel::editTemplate,
                    onCancelTemplateEdit = choresViewModel::cancelTemplateEdit,
                    onActivateTemplate = choresViewModel::activateTemplate,
                    onDeleteTemplate = choresViewModel::deleteTemplate,
                    onAssignChore = choresViewModel::assignChore,
                    onCompleteChore = choresViewModel::completeChore,
                    onApproveChore = { chore -> choresViewModel.approveChore(chore, currentUser) },
                    onResetAssignment = choresViewModel::resetChoreAssignment,
                    onDeleteChore = choresViewModel::deleteChore,
                    onClearMessage = choresViewModel::clearMessage,
                )
            }
            composable(Routes.CreateChore) {
                CreateChoreScreen(
                    currentUser = currentUser,
                    state = choresState,
                    onChoreTitleChange = choresViewModel::updateChoreTitleInput,
                    onChoreRewardChange = choresViewModel::updateChoreRewardInput,
                    onSelectAssignee = choresViewModel::selectAssigneeUser,
                    onToggleMarkCompleted = choresViewModel::toggleMarkCompleted,
                    onCreateChore = choresViewModel::createChore,
                    onClearMessage = choresViewModel::clearMessage,
                )
            }
            composable(Routes.Rewards) {
                val vm: RewardsViewModel = viewModel(
                    key = currentUser.id,
                    factory = simpleFactory {
                        RewardsViewModel(appContainer.familyRepository, currentUser)
                    },
                )
                val state by vm.uiState.collectAsStateWithLifecycle()
                RewardsScreen(
                    state = state,
                    onResetRewards = vm::resetRewards,
                    onSetUserGoal = vm::setUserGoal,
                    onClearMessage = vm::clearMessage,
                )
            }
            if (currentUser.isAdmin) {
                composable(Routes.Users) {
                    val vm: UsersViewModel = viewModel(
                        key = currentUser.id,
                        factory = simpleFactory {
                            UsersViewModel(appContainer.familyRepository)
                        },
                    )
                    val state by vm.uiState.collectAsStateWithLifecycle()
                    UsersScreen(
                        state = state,
                        onNameChange = vm::updateName,
                        onEmailChange = vm::updateEmail,
                        onRoleChange = vm::updateRole,
                        onAddUser = vm::addPendingUser,
                        onSetUserGoal = vm::setUserGoal,
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
    onTemplateRepeatIntervalChange: (Int?) -> Unit,
    onSaveTemplate: () -> Unit,
    onEditTemplate: (ChoreTemplate) -> Unit,
    onCancelTemplateEdit: () -> Unit,
    onActivateTemplate: (ChoreTemplate) -> Unit,
    onDeleteTemplate: (ChoreTemplate) -> Unit,
    onAssignChore: (Chore) -> Unit,
    onCompleteChore: (Chore) -> Unit,
    onApproveChore: (Chore) -> Unit,
    onResetAssignment: (Chore) -> Unit,
    onDeleteChore: (Chore) -> Unit,
    onClearMessage: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var choreToDelete by remember { mutableStateOf<Chore?>(null) }
    var templateToDelete by remember { mutableStateOf<ChoreTemplate?>(null) }
    val currentDate by produceState(initialValue = LocalDate.now()) {
        while (true) {
            val now = ZonedDateTime.now()
            value = now.toLocalDate()
            delay(
                Duration.between(now, now.toLocalDate().plusDays(1).atStartOfDay(now.zone))
                    .toMillis()
                    .coerceAtLeast(1L),
            )
        }
    }
    val daysLeftInMonth = daysLeftInCurrentMonth(currentDate)

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            onClearMessage()
        }
    }

    LaunchedEffect(templateToDelete?.id, state.choreTemplates) {
        val templateId = templateToDelete?.id ?: return@LaunchedEffect
        if (state.choreTemplates.none { it.id == templateId }) {
            templateToDelete = null
        }
    }

    LaunchedEffect(choreToDelete?.id, state.chores) {
        val choreId = choreToDelete?.id ?: return@LaunchedEffect
        if (state.chores.none { it.id == choreId }) {
            choreToDelete = null
        }
    }

    val templateFormEnabled = !state.templateFormSubmitting && state.busyTemplateActions.isEmpty()

    Scaffold(
        containerColor = WorkHomeColors.Background,
        contentColor = WorkHomeColors.PrimaryText,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = WorkHomeDimens.ScreenHorizontalPadding),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(WorkHomeDimens.SpacingBetweenCards),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(WorkHomeDimens.SpacingBetweenCards),
                ) {
                    // Card 1: Total reward
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = WorkHomeShapes.CardShape,
                        color = WorkHomeColors.CardBackground,
                        border = BorderStroke(1.dp, WorkHomeColors.CardBorderBrush),
                        shadowElevation = 2.dp,
                    ) {
                        Box(
                            modifier = Modifier
                                .background(WorkHomeColors.CardGradient)
                                .padding(WorkHomeDimens.CardPadding),
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(
                                                color = WorkHomeColors.RewardStar.copy(alpha = 0.15f),
                                                shape = WorkHomeShapes.BadgeShape,
                                            ),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Schedule,
                                            contentDescription = null,
                                            tint = WorkHomeColors.RewardStar,
                                            modifier = Modifier.size(18.dp),
                                        )
                                    }
                                    Text(
                                        text = "Total minutes",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = WorkHomeColors.SecondaryText,
                                    )
                                }
                                Text(
                                    text = currentUser.rewardProgressText(),
                                    style = if (currentUser.rewardGoal != null && currentUser.rewardGoal > 0) {
                                        MaterialTheme.typography.titleLarge
                                    } else {
                                        MaterialTheme.typography.headlineMedium
                                    },
                                    fontWeight = FontWeight.Bold,
                                    color = WorkHomeColors.PrimaryText,
                                )
                            }
                        }
                    }

                    // Card 2: Days left
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = WorkHomeShapes.CardShape,
                        color = WorkHomeColors.CardBackground,
                        border = BorderStroke(1.dp, WorkHomeColors.CardBorderBrush),
                        shadowElevation = 2.dp,
                    ) {
                        Box(
                            modifier = Modifier
                                .background(WorkHomeColors.CardGradient)
                                .padding(WorkHomeDimens.CardPadding),
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(
                                                color = WorkHomeColors.CyanAccent.copy(alpha = 0.15f),
                                                shape = WorkHomeShapes.BadgeShape,
                                            ),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.CalendarMonth,
                                            contentDescription = null,
                                            tint = WorkHomeColors.CyanAccent,
                                            modifier = Modifier.size(18.dp),
                                        )
                                    }
                                    Text(
                                        text = "Days left",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = WorkHomeColors.SecondaryText,
                                    )
                                }
                                Text(
                                    text = "$daysLeftInMonth",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = WorkHomeColors.PrimaryText,
                                )
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Active chores",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = WorkHomeColors.PrimaryText,
                    )
                    Surface(
                        shape = WorkHomeShapes.BadgeShape,
                        color = Color(0xFF0F264A),
                        border = BorderStroke(1.dp, WorkHomeColors.CardBorder),
                    ) {
                        Text(
                            text = "${state.chores.size} ${if (state.chores.size == 1) "task" else "tasks"}",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = WorkHomeColors.CyanAccent,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            if (currentUser.isAdmin) {

                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = WorkHomeShapes.CardShape,
                        color = WorkHomeColors.CardBackground,
                        border = BorderStroke(1.dp, WorkHomeColors.CardBorderBrush),
                        shadowElevation = 2.dp,
                    ) {
                        Box(
                            modifier = Modifier
                                .background(WorkHomeColors.CardGradient)
                                .padding(WorkHomeDimens.CardPadding),
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = if (state.editingTemplateId == null) "Save chore template" else "Edit chore template",
                                    fontWeight = FontWeight.Bold,
                                    color = WorkHomeColors.PrimaryText,
                                )
                                OutlinedTextField(
                                    value = state.templateTitle,
                                    onValueChange = onTemplateTitleChange,
                                    label = { Text("Title") },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = templateFormEnabled,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = WorkHomeColors.CyanAccent,
                                        unfocusedBorderColor = WorkHomeColors.CardBorderBlue,
                                        focusedLabelColor = WorkHomeColors.CyanAccent,
                                        unfocusedLabelColor = WorkHomeColors.SecondaryText,
                                        focusedTextColor = WorkHomeColors.PrimaryText,
                                        unfocusedTextColor = WorkHomeColors.PrimaryText,
                                        cursorColor = WorkHomeColors.CyanAccent,
                                    ),
                                )
                                OutlinedTextField(
                                    value = state.templateRewardText,
                                    onValueChange = onTemplateRewardChange,
                                    label = { Text("Minutes") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    enabled = templateFormEnabled,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = WorkHomeColors.CyanAccent,
                                        unfocusedBorderColor = WorkHomeColors.CardBorderBlue,
                                        focusedLabelColor = WorkHomeColors.CyanAccent,
                                        unfocusedLabelColor = WorkHomeColors.SecondaryText,
                                        focusedTextColor = WorkHomeColors.PrimaryText,
                                        unfocusedTextColor = WorkHomeColors.PrimaryText,
                                        cursorColor = WorkHomeColors.CyanAccent,
                                    ),
                                )
                                Text(
                                    text = "Auto-creation schedule",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = WorkHomeColors.SecondaryText,
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    val options = listOf(
                                        null to "Manual",
                                        1 to "Every day",
                                        2 to "Every 2 days",
                                        3 to "Every 3 days",
                                        7 to "Weekly",
                                    )
                                    options.forEach { (days, label) ->
                                        FilterChip(
                                            selected = state.templateRepeatIntervalDays == days,
                                            onClick = { onTemplateRepeatIntervalChange(days) },
                                            label = { Text(label) },
                                            enabled = templateFormEnabled,
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = WorkHomeColors.CyanAccent.copy(alpha = 0.2f),
                                                selectedLabelColor = WorkHomeColors.CyanAccent,
                                                labelColor = WorkHomeColors.SecondaryText,
                                            ),
                                        )
                                    }
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Button(
                                        onClick = onSaveTemplate,
                                        enabled = templateFormEnabled,
                                        shape = WorkHomeShapes.PillShape,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = WorkHomeColors.PrimaryBlue,
                                            contentColor = Color.White,
                                        ),
                                    ) {
                                        Text(if (state.editingTemplateId == null) "Save template" else "Update template")
                                    }
                                    if (state.editingTemplateId != null) {
                                        OutlinedButton(
                                            onClick = onCancelTemplateEdit,
                                            enabled = templateFormEnabled,
                                            shape = WorkHomeShapes.PillShape,
                                            border = BorderStroke(1.dp, WorkHomeColors.CardBorder),
                                        ) {
                                            Text("Cancel edit")
                                        }
                                    }
                                }
                                if (state.templateFormSubmitting) {
                                    Text("Saving template...", color = WorkHomeColors.SecondaryText, style = MaterialTheme.typography.bodySmall)
                                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = WorkHomeColors.CyanAccent)
                                }
                            }
                        }
                    }
                }

                if (state.choreTemplates.isNotEmpty()) {
                    item {
                        Text(
                            text = "Templates",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = WorkHomeColors.PrimaryText,
                        )
                    }
                    items(state.choreTemplates, key = { it.id }) { template ->
                        ChoreTemplateRow(
                            template = template,
                            templateFormSubmitting = state.templateFormSubmitting,
                            busyAction = state.busyTemplateActions[template.id],
                            onEditTemplate = onEditTemplate,
                            onActivateTemplate = onActivateTemplate,
                            onDeleteTemplate = { templateToDelete = it },
                        )
                    }
                }
            }

            if (state.chores.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = WorkHomeShapes.CardShape,
                        color = WorkHomeColors.CardBackground,
                        border = BorderStroke(1.dp, WorkHomeColors.CardBorderBrush),
                    ) {
                        Box(
                            modifier = Modifier
                                .background(WorkHomeColors.CardGradient)
                                .padding(24.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No active chores right now.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = WorkHomeColors.SecondaryText,
                            )
                        }
                    }
                }
            }

            items(state.chores, key = { it.id }) { chore ->
                val busyAction = state.busyChoreActions[chore.id]
                val isOpen = chore.assignedToUserId.isBlank()
                val isAssignedToCurrentUser = chore.assignedToUserId == currentUser.authUid
                val assigneeName = state.usersByAuthUid[chore.assignedToUserId]
                val isCompletedThisMonth = state.completedChores.any {
                    it.choreId == chore.id && it.userId == currentUser.authUid
                }
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = WorkHomeShapes.CardShape,
                    color = WorkHomeColors.CardBackground,
                    border = BorderStroke(1.dp, WorkHomeColors.CardBorderBrush),
                    shadowElevation = 2.dp,
                ) {
                    Box(
                        modifier = Modifier
                            .background(WorkHomeColors.CardGradient)
                            .padding(WorkHomeDimens.CardPadding),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(
                                            color = Color(0xFF10284D),
                                            shape = WorkHomeShapes.IconContainerShape,
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = WorkHomeColors.CardBorder,
                                            shape = WorkHomeShapes.IconContainerShape,
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = choreIconForTitle(chore.title),
                                        contentDescription = null,
                                        tint = WorkHomeColors.CyanAccent,
                                        modifier = Modifier.size(22.dp),
                                    )
                                }
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Text(
                                        text = chore.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = WorkHomeColors.PrimaryText,
                                    )
                                    if (chore.description.isNotBlank()) {
                                        Text(
                                            text = chore.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = WorkHomeColors.SecondaryText,
                                        )
                                    }
                                    if (chore.awaitingApproval) {
                                        Surface(
                                            shape = WorkHomeShapes.BadgeShape,
                                            color = Color(0xFF3E2723),
                                            border = BorderStroke(1.dp, Color(0xFFFFB74D)),
                                        ) {
                                            Text(
                                                text = "Awaiting approval",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFFFFB74D),
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Schedule,
                                            contentDescription = "Minutes",
                                            tint = WorkHomeColors.RewardStar,
                                            modifier = Modifier.size(14.dp),
                                        )
                                        Text(
                                            text = "Minutes: ${chore.reward}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = WorkHomeColors.SecondaryText,
                                        )
                                    }
                                    val assignmentText = when {
                                        isOpen -> "Unassigned"
                                        isAssignedToCurrentUser -> "Assigned to you"
                                        currentUser.isAdmin -> "Assigned to ${assigneeName ?: "another user"}"
                                        else -> "Assigned"
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Person,
                                            contentDescription = "Assignee",
                                            tint = if (isAssignedToCurrentUser) WorkHomeColors.CyanAccent else WorkHomeColors.SecondaryText,
                                            modifier = Modifier.size(14.dp),
                                        )
                                        Text(
                                            text = assignmentText,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isAssignedToCurrentUser) WorkHomeColors.CyanAccent else WorkHomeColors.SecondaryText,
                                        )
                                    }
                                }
                            }

                            when {
                                chore.awaitingApproval -> {
                                    if (currentUser.isAdmin) {
                                        Button(
                                            onClick = { onApproveChore(chore) },
                                            enabled = busyAction == null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(48.dp),
                                            shape = WorkHomeShapes.PillShape,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF2E7D32),
                                                contentColor = Color.White,
                                            ),
                                        ) {
                                            Text(
                                                text = if (busyAction == ChoreRowAction.APPROVE) "Approving..." else "Approve chore",
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                    }
                                }

                                isOpen -> {
                                    Button(
                                        onClick = { onAssignChore(chore) },
                                        enabled = busyAction == null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp),
                                        shape = WorkHomeShapes.PillShape,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = WorkHomeColors.PrimaryBlue,
                                            contentColor = Color.White,
                                        ),
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.PersonAdd,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp),
                                            )
                                            Text(
                                                text = if (busyAction == ChoreRowAction.ASSIGN) "Assigning..." else "Assign to me",
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                    }
                                }

                                isAssignedToCurrentUser -> {
                                    if (isCompletedThisMonth) {
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = WorkHomeShapes.PillShape,
                                            color = Color(0xFF0E2E44),
                                            border = BorderStroke(1.dp, WorkHomeColors.CyanAccent.copy(alpha = 0.5f)),
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.CheckCircle,
                                                    contentDescription = null,
                                                    tint = WorkHomeColors.CyanAccent,
                                                    modifier = Modifier.size(18.dp),
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Completed for this month",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = WorkHomeColors.CyanAccent,
                                                    fontWeight = FontWeight.SemiBold,
                                                )
                                            }
                                        }
                                    } else {
                                        Button(
                                            onClick = { onCompleteChore(chore) },
                                            enabled = busyAction == null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp),
                                            shape = WorkHomeShapes.PillShape,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.Transparent,
                                                disabledContainerColor = Color(0x332979FF),
                                            ),
                                            contentPadding = PaddingValues(),
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .then(
                                                        if (busyAction == null) {
                                                            Modifier.background(
                                                                brush = WorkHomeColors.CompleteButtonGradient,
                                                                shape = WorkHomeShapes.PillShape,
                                                            )
                                                        } else {
                                                            Modifier.background(
                                                                color = Color(0x442979FF),
                                                                shape = WorkHomeShapes.PillShape,
                                                            )
                                                        }
                                                    ),
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Check,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(20.dp),
                                                    )
                                                    Text(
                                                        text = if (busyAction == ChoreRowAction.COMPLETE) "Completing..." else "Complete for me",
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                currentUser.isAdmin -> {
                                    // Admin viewing someone else's chore
                                }
                            }

                            if (currentUser.isAdmin && !isOpen && !chore.awaitingApproval) {
                                OutlinedButton(
                                    onClick = { onResetAssignment(chore) },
                                    enabled = busyAction == null,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = WorkHomeShapes.PillShape,
                                    border = BorderStroke(1.dp, WorkHomeColors.CardBorder),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = WorkHomeColors.SecondaryText,
                                    ),
                                ) {
                                    Text(if (busyAction == ChoreRowAction.RESET) "Resetting..." else "Reset assignment")
                                }
                            }
                            if (currentUser.isAdmin) {
                                OutlinedButton(
                                    onClick = { choreToDelete = chore },
                                    enabled = busyAction == null,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = WorkHomeShapes.PillShape,
                                    border = BorderStroke(1.dp, Color(0x33FF5252)),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFFFF6E6E),
                                    ),
                                ) {
                                    Text(if (busyAction == ChoreRowAction.DELETE) "Deleting..." else "Delete chore")
                                }
                            }
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Completed chores",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = WorkHomeColors.PrimaryText,
                    )
                    if (state.completedChores.isEmpty()) {
                        Text(
                            text = "No completed chores yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WorkHomeColors.SecondaryText,
                        )
                    }
                }
            }

            if (state.completedChores.isNotEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = WorkHomeShapes.CardShape,
                        color = WorkHomeColors.CardBackground,
                        border = BorderStroke(1.dp, WorkHomeColors.CardBorderBrush),
                        shadowElevation = 2.dp,
                    ) {
                        Column(
                            modifier = Modifier
                                .background(WorkHomeColors.CardGradient)
                                .fillMaxWidth()
                        ) {
                            state.completedChores.forEachIndexed { index, completed ->
                                if (index > 0) {
                                    HorizontalDivider(
                                        color = WorkHomeColors.CardBorderBlue.copy(alpha = 0.5f),
                                    )
                                }
                                CompletedChoreRow(
                                    completed = completed,
                                    currentUser = currentUser,
                                    assigneeName = completed.userName.ifBlank { state.usersByAuthUid[completed.userId] },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    choreToDelete?.let { chore ->
        val choreDeleteBusy = state.busyChoreActions[chore.id] == ChoreRowAction.DELETE
        AlertDialog(
            onDismissRequest = {
                if (!choreDeleteBusy) {
                    choreToDelete = null
                }
            },
            containerColor = WorkHomeColors.CardBackground,
            titleContentColor = WorkHomeColors.PrimaryText,
            textContentColor = WorkHomeColors.SecondaryText,
            confirmButton = {
                TextButton(
                    onClick = { onDeleteChore(chore) },
                    enabled = !choreDeleteBusy,
                ) {
                    Text("Delete", color = Color(0xFFFF6E6E))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { choreToDelete = null },
                    enabled = !choreDeleteBusy,
                ) {
                    Text("Cancel", color = WorkHomeColors.SecondaryText)
                }
            },
            title = { Text("Delete chore?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(chore.title)
                    if (choreDeleteBusy) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = WorkHomeColors.CyanAccent)
                    }
                }
            },
        )
    }

    templateToDelete?.let { template ->
        val templateDeleteBusy = state.busyTemplateActions[template.id] == TemplateRowAction.DELETE
        AlertDialog(
            onDismissRequest = {
                if (!templateDeleteBusy) {
                    templateToDelete = null
                }
            },
            containerColor = WorkHomeColors.CardBackground,
            titleContentColor = WorkHomeColors.PrimaryText,
            textContentColor = WorkHomeColors.SecondaryText,
            confirmButton = {
                TextButton(
                    onClick = { onDeleteTemplate(template) },
                    enabled = !templateDeleteBusy,
                ) {
                    Text("Delete", color = Color(0xFFFF6E6E))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { templateToDelete = null },
                    enabled = !templateDeleteBusy,
                ) {
                    Text("Cancel", color = WorkHomeColors.SecondaryText)
                }
            },
            title = { Text("Delete template?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(template.title)
                    if (templateDeleteBusy) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = WorkHomeColors.CyanAccent)
                    }
                }
            },
        )
    }
}

internal fun daysLeftInCurrentMonth(date: LocalDate = LocalDate.now()): Int = date.lengthOfMonth() - date.dayOfMonth

private fun choreIconForTitle(title: String): ImageVector {
    val lower = title.lowercase()
    return when {
        lower.contains("clean") || lower.contains("wash") || lower.contains("mop") || lower.contains("sweep") || lower.contains("broom") -> Icons.Filled.CleaningServices
        lower.contains("table") || lower.contains("dish") || lower.contains("dinner") || lower.contains("lunch") || lower.contains("breakfast") || lower.contains("food") || lower.contains("kitchen") -> Icons.Filled.Restaurant
        lower.contains("vacuum") || lower.contains("dust") || lower.contains("laundry") || lower.contains("fold") || lower.contains("clothes") -> Icons.Filled.DryCleaning
        lower.contains("trash") || lower.contains("garbage") || lower.contains("bin") -> Icons.Filled.DeleteOutline
        lower.contains("bed") || lower.contains("bedroom") || lower.contains("room") -> Icons.Filled.Bed
        lower.contains("dog") || lower.contains("cat") || lower.contains("pet") -> Icons.Filled.Pets
        lower.contains("garden") || lower.contains("yard") || lower.contains("lawn") || lower.contains("plant") -> Icons.Filled.Yard
        else -> Icons.Filled.TaskAlt
    }
}

@Composable
private fun CompletedChoreRow(
    completed: CompletedChore,
    currentUser: AppUser,
    assigneeName: String?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = completed.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = WorkHomeColors.PrimaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val displayName = assigneeName?.ifBlank { null }
            val subtitle = buildString {
                append(completed.formattedDate())
                if (displayName != null) {
                    append(" • ")
                    append(if (completed.userId == currentUser.authUid) "you" else displayName)
                }
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = WorkHomeColors.SecondaryText,
            )
            if (completed.description.isNotBlank()) {
                Text(
                    text = completed.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = WorkHomeColors.SecondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(start = 8.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Schedule,
                contentDescription = null,
                tint = WorkHomeColors.RewardStar,
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = "+${completed.reward} min",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = WorkHomeColors.RewardStar,
            )
        }
    }
}

@Composable
private fun ChoreTemplateRow(
    template: ChoreTemplate,
    templateFormSubmitting: Boolean,
    busyAction: TemplateRowAction?,
    onEditTemplate: (ChoreTemplate) -> Unit,
    onActivateTemplate: (ChoreTemplate) -> Unit,
    onDeleteTemplate: (ChoreTemplate) -> Unit,
) {
    val isBusy = templateFormSubmitting || busyAction != null
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = WorkHomeShapes.CardShape,
        color = WorkHomeColors.CardBackground,
        border = BorderStroke(1.dp, WorkHomeColors.CardBorderBrush),
        shadowElevation = 2.dp,
    ) {
        Box(
            modifier = Modifier
                .background(WorkHomeColors.CardGradient)
                .padding(WorkHomeDimens.CardPadding),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = template.title,
                    fontWeight = FontWeight.Bold,
                    color = WorkHomeColors.PrimaryText,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = "Minutes",
                            tint = WorkHomeColors.RewardStar,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = "Minutes: ${template.reward}",
                            color = WorkHomeColors.SecondaryText,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Text(
                        text = "• Schedule: ${template.recurrenceLabel()}",
                        color = if (template.repeatIntervalDays != null) WorkHomeColors.CyanAccent else WorkHomeColors.SecondaryText,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = { onActivateTemplate(template) },
                        enabled = !isBusy,
                        shape = WorkHomeShapes.PillShape,
                        colors = ButtonDefaults.buttonColors(containerColor = WorkHomeColors.PrimaryBlue),
                    ) {
                        Text(if (busyAction == TemplateRowAction.ACTIVATE) "Activating..." else "Activate")
                    }
                    OutlinedButton(
                        onClick = { onEditTemplate(template) },
                        enabled = !isBusy,
                        shape = WorkHomeShapes.PillShape,
                        border = BorderStroke(1.dp, WorkHomeColors.CardBorder),
                    ) {
                        Text("Edit")
                    }
                    OutlinedButton(
                        onClick = { onDeleteTemplate(template) },
                        enabled = !isBusy,
                        shape = WorkHomeShapes.PillShape,
                        border = BorderStroke(1.dp, Color(0x33FF5252)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6E6E)),
                    ) {
                        Text(if (busyAction == TemplateRowAction.DELETE) "Deleting..." else "Delete")
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateChoreScreen(
    currentUser: AppUser,
    state: ChoresUiState,
    onChoreTitleChange: (String) -> Unit,
    onChoreRewardChange: (String) -> Unit,
    onSelectAssignee: (AppUser?) -> Unit,
    onToggleMarkCompleted: (Boolean) -> Unit,
    onCreateChore: () -> Unit,
    onClearMessage: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            onClearMessage()
        }
    }

    Scaffold(
        containerColor = WorkHomeColors.Background,
        contentColor = WorkHomeColors.PrimaryText,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = WorkHomeDimens.ScreenHorizontalPadding),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(WorkHomeDimens.SpacingBetweenCards),
        ) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = WorkHomeShapes.CardShape,
                    color = WorkHomeColors.CardBackground,
                    border = BorderStroke(1.dp, WorkHomeColors.CardBorderBrush),
                    shadowElevation = 2.dp,
                ) {
                    Box(
                        modifier = Modifier
                            .background(WorkHomeColors.CardGradient)
                            .padding(WorkHomeDimens.CardPadding),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Create custom chore",
                                fontWeight = FontWeight.Bold,
                                color = WorkHomeColors.PrimaryText,
                                style = MaterialTheme.typography.titleLarge,
                            )
                            OutlinedTextField(
                                value = state.choreTitleInput,
                                onValueChange = onChoreTitleChange,
                                label = { Text("Title") },
                                placeholder = { Text("e.g. Wash dishes") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !state.createChoreSubmitting,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = WorkHomeColors.CyanAccent,
                                    unfocusedBorderColor = WorkHomeColors.CardBorderBlue,
                                    focusedLabelColor = WorkHomeColors.CyanAccent,
                                    unfocusedLabelColor = WorkHomeColors.SecondaryText,
                                    focusedTextColor = WorkHomeColors.PrimaryText,
                                    unfocusedTextColor = WorkHomeColors.PrimaryText,
                                    cursorColor = WorkHomeColors.CyanAccent,
                                ),
                            )
                            OutlinedTextField(
                                value = state.choreRewardInputText,
                                onValueChange = onChoreRewardChange,
                                label = { Text("Minutes") },
                                placeholder = { Text("e.g. 15") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                enabled = !state.createChoreSubmitting,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = WorkHomeColors.CyanAccent,
                                    unfocusedBorderColor = WorkHomeColors.CardBorderBlue,
                                    focusedLabelColor = WorkHomeColors.CyanAccent,
                                    unfocusedLabelColor = WorkHomeColors.SecondaryText,
                                    focusedTextColor = WorkHomeColors.PrimaryText,
                                    unfocusedTextColor = WorkHomeColors.PrimaryText,
                                    cursorColor = WorkHomeColors.CyanAccent,
                                ),
                            )
                            if (currentUser.isAdmin) {
                                Text(
                                    text = "Assign to (optional):",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = WorkHomeColors.SecondaryText,
                                )
                                Row(
                                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    FilterChip(
                                        selected = state.selectedAssigneeUser == null,
                                        onClick = { onSelectAssignee(null) },
                                        label = { Text("Unassigned") },
                                        enabled = !state.createChoreSubmitting,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFF142B50),
                                            selectedLabelColor = WorkHomeColors.CyanAccent,
                                            containerColor = Color(0xFF08162B),
                                            labelColor = WorkHomeColors.SecondaryText,
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = !state.createChoreSubmitting,
                                            selected = state.selectedAssigneeUser == null,
                                            borderColor = if (state.selectedAssigneeUser == null) WorkHomeColors.CyanAccent else WorkHomeColors.CardBorderBlue,
                                        ),
                                    )
                                    state.usersList.forEach { user ->
                                        FilterChip(
                                            selected = state.selectedAssigneeUser?.id == user.id,
                                            onClick = { onSelectAssignee(user) },
                                            label = { Text(user.name) },
                                            enabled = !state.createChoreSubmitting,
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Color(0xFF142B50),
                                                selectedLabelColor = WorkHomeColors.CyanAccent,
                                                containerColor = Color(0xFF08162B),
                                                labelColor = WorkHomeColors.SecondaryText,
                                            ),
                                            border = FilterChipDefaults.filterChipBorder(
                                                enabled = !state.createChoreSubmitting,
                                                selected = state.selectedAssigneeUser?.id == user.id,
                                                borderColor = if (state.selectedAssigneeUser?.id == user.id) WorkHomeColors.CyanAccent else WorkHomeColors.CardBorderBlue,
                                            ),
                                        )
                                    }
                                }
                                if (state.selectedAssigneeUser != null) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        Checkbox(
                                            checked = state.markCompletedInput,
                                            onCheckedChange = onToggleMarkCompleted,
                                            enabled = !state.createChoreSubmitting,
                                        )
                                        Text(
                                            text = "Mark as completed immediately",
                                            color = WorkHomeColors.PrimaryText,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                    }
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Checkbox(
                                        checked = state.markCompletedInput,
                                        onCheckedChange = onToggleMarkCompleted,
                                        enabled = !state.createChoreSubmitting,
                                    )
                                    Text(
                                        text = "Set to complete (awaits admin approval)",
                                        color = WorkHomeColors.PrimaryText,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            }
                            Button(
                                onClick = onCreateChore,
                                enabled = !state.createChoreSubmitting,
                                modifier = Modifier.fillMaxWidth(),
                                shape = WorkHomeShapes.PillShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = WorkHomeColors.PrimaryBlue,
                                    contentColor = Color.White,
                                ),
                            ) {
                                Text(
                                    text = if (state.createChoreSubmitting) "Creating..." else "Create chore",
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
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
    onSetUserGoal: (AppUser, Long?) -> Unit,
    onRemoveUser: (AppUser) -> Unit,
    onRemovePendingUser: (PendingUser) -> Unit,
    onClearMessage: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var userToRemove by remember { mutableStateOf<AppUser?>(null) }
    var pendingToRemove by remember { mutableStateOf<PendingUser?>(null) }
    var userForGoalSetting by remember { mutableStateOf<AppUser?>(null) }

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
                        Text("Goal: ${user.rewardGoal?.let { "$it" } ?: "Not set"}")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { userForGoalSetting = user }, enabled = !state.submitting) {
                                Text("Set goal")
                            }
                            OutlinedButton(onClick = { userToRemove = user }, enabled = !state.submitting) {
                                Text("Remove profile")
                            }
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

    userForGoalSetting?.let { user ->
        SetGoalDialog(
            user = user,
            onConfirm = { goal ->
                onSetUserGoal(user, goal)
                userForGoalSetting = null
            },
            onDismiss = { userForGoalSetting = null },
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
    onSetUserGoal: (AppUser, Long?) -> Unit = { _, _ -> },
    onClearMessage: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showResetConfirmation by rememberSaveable { mutableStateOf(false) }
    var userForGoalSetting by remember { mutableStateOf<AppUser?>(null) }

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
                            Text("Reset archives the current totals into history before setting every user total back to zero.")
                            Button(onClick = { showResetConfirmation = true }, enabled = !state.resetting) {
                                Text(if (state.resetting) "Resetting..." else "Reset minutes")
                            }
                        }
                    }
                }
            }

            items(state.users, key = { it.id }) { user ->
                val daysSince = user.daysSinceLastCompleted()
                val daysText = when (daysSince) {
                    null -> "None"
                    0L -> "0 (Today)"
                    1L -> "1 day ago"
                    else -> "$daysSince days ago"
                }
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(user.name, fontWeight = FontWeight.Bold)
                            if (state.currentUser.isAdmin) {
                                TextButton(onClick = { userForGoalSetting = user }) {
                                    Text("Set goal")
                                }
                            }
                        }
                        Text("Total minutes: ${user.rewardProgressText()}")
                        Text("Days since last completed chore: $daysText")
                    }
                }
            }
        }
    }

    if (showResetConfirmation) {
        ConfirmDialog(
            title = "Reset all minutes?",
            body = "This archives the current totals and zeroes out every family member's running minutes.",
            onConfirm = {
                onResetRewards()
                showResetConfirmation = false
            },
            onDismiss = { showResetConfirmation = false },
        )
    }

    userForGoalSetting?.let { user ->
        SetGoalDialog(
            user = user,
            onConfirm = { goal ->
                onSetUserGoal(user, goal)
                userForGoalSetting = null
            },
            onDismiss = { userForGoalSetting = null },
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

@Composable
private fun SetGoalDialog(
    user: AppUser,
    onConfirm: (Long?) -> Unit,
    onDismiss: () -> Unit,
) {
    var goalText by remember { mutableStateOf(user.rewardGoal?.toString().orEmpty()) }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set minutes goal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Set target minutes goal for ${user.name}:")
                OutlinedTextField(
                    value = goalText,
                    onValueChange = { input ->
                        goalText = input.filter { it.isDigit() }
                        errorText = null
                    },
                    label = { Text("Minutes goal") },
                    placeholder = { Text("e.g. 500") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = errorText != null,
                    supportingText = {
                        Text(errorText ?: "Leave empty to clear goal")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (goalText.isBlank()) {
                        onConfirm(null)
                    } else {
                        val parsed = goalText.toLongOrNull()
                        if (parsed == null || parsed <= 0) {
                            errorText = "Enter a valid positive number"
                        } else {
                            onConfirm(parsed)
                        }
                    }
                },
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

private fun <T : ViewModel> simpleFactory(creator: () -> T): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = creator() as VM
    }
