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
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
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
import com.bdysvik.workhome.localization.AppLanguage
import com.bdysvik.workhome.localization.LanguagePreference
import com.bdysvik.workhome.localization.LocalAppLanguage
import com.bdysvik.workhome.localization.LocalAppStrings
import com.bdysvik.workhome.localization.getStrings
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
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.delay

private object Routes {
    const val Chores = "chores"
    const val CreateChore = "create_chore"
    const val Rewards = "rewards"
    const val Users = "users"
}

private val GoldCoinsIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "GoldCoins",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        // === LEFT STACK (2 coins) ===
        // Bottom left coin edge
        path(fill = SolidColor(Color(0xFFFFB300))) {
            moveTo(2f, 14f)
            lineTo(2f, 17f)
            curveTo(2f, 18.8f, 12f, 18.8f, 12f, 17f)
            lineTo(12f, 14f)
            curveTo(12f, 15.8f, 2f, 15.8f, 2f, 14f)
            close()
        }
        // Bottom left coin top
        path(fill = SolidColor(Color(0xFFFFD700))) {
            moveTo(7f, 12.5f)
            curveTo(9.8f, 12.5f, 12f, 13.2f, 12f, 14.2f)
            curveTo(12f, 15.2f, 9.8f, 16f, 7f, 16f)
            curveTo(4.2f, 16f, 2f, 15.2f, 2f, 14.2f)
            curveTo(2f, 13.2f, 4.2f, 12.5f, 7f, 12.5f)
            close()
        }
        // Top left coin edge
        path(fill = SolidColor(Color(0xFFFFB300))) {
            moveTo(2f, 10f)
            lineTo(2f, 13f)
            curveTo(2f, 14.8f, 12f, 14.8f, 12f, 13f)
            lineTo(12f, 10f)
            curveTo(12f, 11.8f, 2f, 11.8f, 2f, 10f)
            close()
        }
        // Top left coin top
        path(fill = SolidColor(Color(0xFFFFE57F))) {
            moveTo(7f, 8.5f)
            curveTo(9.8f, 8.5f, 12f, 9.2f, 12f, 10.2f)
            curveTo(12f, 11.2f, 9.8f, 12f, 7f, 12f)
            curveTo(4.2f, 12f, 2f, 11.2f, 2f, 10.2f)
            curveTo(2f, 9.2f, 4.2f, 8.5f, 7f, 8.5f)
            close()
        }

        // === RIGHT STACK (3 coins) ===
        // Bottom right coin edge
        path(fill = SolidColor(Color(0xFFFFB300))) {
            moveTo(12f, 13f)
            lineTo(12f, 16f)
            curveTo(12f, 17.8f, 22f, 17.8f, 22f, 16f)
            lineTo(22f, 13f)
            curveTo(22f, 14.8f, 12f, 14.8f, 12f, 13f)
            close()
        }
        // Bottom right coin top
        path(fill = SolidColor(Color(0xFFFFD700))) {
            moveTo(17f, 11.5f)
            curveTo(19.8f, 11.5f, 22f, 12.2f, 22f, 13.2f)
            curveTo(22f, 14.2f, 19.8f, 15f, 17f, 15f)
            curveTo(14.2f, 15f, 12f, 14.2f, 12f, 13.2f)
            curveTo(12f, 12.2f, 14.2f, 11.5f, 17f, 11.5f)
            close()
        }
        // Middle right coin edge
        path(fill = SolidColor(Color(0xFFFFB300))) {
            moveTo(12f, 9f)
            lineTo(12f, 12f)
            curveTo(12f, 13.8f, 22f, 13.8f, 22f, 12f)
            lineTo(22f, 9f)
            curveTo(22f, 10.8f, 12f, 10.8f, 12f, 9f)
            close()
        }
        // Middle right coin top
        path(fill = SolidColor(Color(0xFFFFD700))) {
            moveTo(17f, 7.5f)
            curveTo(19.8f, 7.5f, 22f, 8.2f, 22f, 9.2f)
            curveTo(22f, 10.2f, 19.8f, 11f, 17f, 11f)
            curveTo(14.2f, 11f, 12f, 10.2f, 12f, 9.2f)
            curveTo(12f, 8.2f, 14.2f, 7.5f, 17f, 7.5f)
            close()
        }
        // Top right coin edge
        path(fill = SolidColor(Color(0xFFFFB300))) {
            moveTo(12f, 5f)
            lineTo(12f, 8f)
            curveTo(12f, 9.8f, 22f, 9.8f, 22f, 8f)
            lineTo(22f, 5f)
            curveTo(22f, 6.8f, 12f, 6.8f, 12f, 5f)
            close()
        }
        // Top right coin top
        path(fill = SolidColor(Color(0xFFFFE57F))) {
            moveTo(17f, 3.5f)
            curveTo(19.8f, 3.5f, 22f, 4.2f, 22f, 5.2f)
            curveTo(22f, 6.2f, 19.8f, 7f, 17f, 7f)
            curveTo(14.2f, 7f, 12f, 6.2f, 12f, 5.2f)
            curveTo(12f, 4.2f, 14.2f, 3.5f, 17f, 3.5f)
            close()
        }

        // === FRONT CENTER COIN ===
        // Front coin edge
        path(fill = SolidColor(Color(0xFFE69100))) {
            moveTo(6f, 17f)
            lineTo(6f, 20f)
            curveTo(6f, 22f, 18f, 22f, 18f, 20f)
            lineTo(18f, 17f)
            curveTo(18f, 19f, 6f, 19f, 6f, 17f)
            close()
        }
        // Front coin top
        path(fill = SolidColor(Color(0xFFFFF099))) {
            moveTo(12f, 15f)
            curveTo(15.3f, 15f, 18f, 15.9f, 18f, 17f)
            curveTo(18f, 18.1f, 15.3f, 19f, 12f, 19f)
            curveTo(8.7f, 19f, 6f, 18.1f, 6f, 17f)
            curveTo(6f, 15.9f, 8.7f, 15f, 12f, 15f)
            close()
        }
        // Front coin inner detail ring
        path(fill = SolidColor(Color(0xFFFFC107))) {
            moveTo(12f, 15.8f)
            curveTo(14.5f, 15.8f, 16.5f, 16.3f, 16.5f, 17f)
            curveTo(16.5f, 17.7f, 14.5f, 18.2f, 12f, 18.2f)
            curveTo(9.5f, 18.2f, 7.5f, 17.7f, 7.5f, 17f)
            curveTo(7.5f, 16.3f, 9.5f, 15.8f, 12f, 15.8f)
            close()
        }
    }.build()
}

@Composable
fun WorkHomeApp(
    appContainer: AppContainer?,
    firebaseConfigured: Boolean,
) {
    val context = LocalContext.current
    var currentLanguage by remember {
        mutableStateOf(LanguagePreference.getLanguage(context))
    }
    val onLanguageSelected: (AppLanguage) -> Unit = { newLang ->
        currentLanguage = newLang
        LanguagePreference.setLanguage(context, newLang)
    }

    CompositionLocalProvider(
        LocalAppLanguage provides currentLanguage,
        LocalAppStrings provides getStrings(currentLanguage),
    ) {
        if (!firebaseConfigured || appContainer == null) {
            SetupRequiredScreen()
            return@CompositionLocalProvider
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
                    currentLanguage = currentLanguage,
                    onLanguageSelected = onLanguageSelected,
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
                currentLanguage = currentLanguage,
                onLanguageSelected = onLanguageSelected,
                onSignOut = sessionViewModel::signOut,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScaffold(
    currentUser: AppUser,
    appContainer: AppContainer,
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
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

    val strings = LocalAppStrings.current
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Routes.Chores
    val navItems = buildList {
        add(Routes.Chores to strings.navChores)
        add(Routes.CreateChore to strings.navAddChore)
        add(Routes.Rewards to strings.navMinutes)
        if (currentUser.isAdmin) add(Routes.Users to strings.navUsers)
    }

    Scaffold(
        containerColor = WorkHomeColors.Background,
        contentColor = WorkHomeColors.PrimaryText,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = navItems.firstOrNull { it.first == currentRoute }?.second ?: strings.appName,
                        fontWeight = FontWeight.Bold,
                        color = WorkHomeColors.PrimaryText,
                    )
                },
                actions = {
                    LanguageSelector(
                        currentLanguage = currentLanguage,
                        onLanguageSelected = onLanguageSelected,
                    )
                    IconButton(onClick = onSignOut) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = strings.signOut,
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
                    onSetUserGoal = choresViewModel::setUserGoal,
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
    val strings = LocalAppStrings.current
    CenteredMessage(
        title = strings.setupRequiredTitle,
        body = strings.setupRequiredBody,
    )
}

@Composable
private fun MissingProfileScreen(
    errorMessage: String?,
    onSignOut: () -> Unit,
) {
    val strings = LocalAppStrings.current
    CenteredMessage(
        title = strings.profileNotFoundTitle,
        body = errorMessage
            ?: strings.profileNotFoundDefaultBody,
        action = {
            Button(onClick = onSignOut) {
                Text(strings.signOut)
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
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onModeToggle: () -> Unit,
    onSubmit: () -> Unit,
) {
    val strings = LocalAppStrings.current
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = strings.appName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    LanguageSelector(
                        currentLanguage = currentLanguage,
                        onLanguageSelected = onLanguageSelected,
                    )
                }
                Text(
                    text = if (state.createAccount) strings.createInvitedAccount else strings.signIn,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                OutlinedTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    label = { Text(strings.email) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    label = { Text(strings.password) },
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
                    Text(if (state.loading) strings.working else if (state.createAccount) strings.createInvitedAccount else strings.signIn)
                }
                TextButton(onClick = onModeToggle, enabled = !state.loading) {
                    Text(if (state.createAccount) strings.alreadyHaveAccount else strings.needToClaimInvite)
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
    onSetUserGoal: (AppUser, Long?, Long?) -> Unit = { _, _, _ -> },
    onClearMessage: () -> Unit,
) {
    val strings = LocalAppStrings.current
    val snackbarHostState = remember { SnackbarHostState() }
    var choreToDelete by remember { mutableStateOf<Chore?>(null) }
    var templateToDelete by remember { mutableStateOf<ChoreTemplate?>(null) }
    var userForGoalSetting by remember { mutableStateOf<AppUser?>(null) }
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
    val completedGroups = state.completedChores
        .groupBy { completion -> completion.completedAtMillis?.let(::yearMonthFromEpochMillis) }
        .entries
        .sortedByDescending { it.key }
    val activeChores = remember(state.chores, state.completedChores) {
        state.chores.filterNot { chore ->
            state.completedChores.any { it.choreId == chore.id }
        }
    }

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

    LaunchedEffect(choreToDelete?.id, activeChores) {
        val choreId = choreToDelete?.id ?: return@LaunchedEffect
        if (activeChores.none { it.id == choreId }) {
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
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
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
                                            text = strings.totalMinutes,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = WorkHomeColors.SecondaryText,
                                        )
                                    }
                                    if (currentUser.isAdmin) {
                                        IconButton(
                                            onClick = { userForGoalSetting = currentUser },
                                            modifier = Modifier.size(24.dp),
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Edit,
                                                contentDescription = strings.setGoalTitle,
                                                tint = WorkHomeColors.SecondaryText,
                                                modifier = Modifier.size(16.dp),
                                            )
                                        }
                                    }
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
                                val accumulated = currentUser.accumulatedReward()
                                val eligible = currentUser.rewardEligible
                                if (eligible != null) {
                                    HorizontalDivider(
                                        color = WorkHomeColors.CardBorderBlue.copy(alpha = 0.3f),
                                        modifier = Modifier.padding(vertical = 2.dp),
                                    )
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = strings.accumulatedRewardLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = WorkHomeColors.SecondaryText,
                                        )
                                        Row(
                                            verticalAlignment = Alignment.Bottom,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        ) {
                                            Icon(
                                                imageVector = GoldCoinsIcon,
                                                contentDescription = null,
                                                tint = Color.Unspecified,
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .align(Alignment.CenterVertically),
                                            )
                                            Text(
                                                text = "${accumulated ?: 0}",
                                                style = MaterialTheme.typography.headlineSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = WorkHomeColors.CyanAccent,
                                            )
                                            Text(
                                                text = " / $eligible",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = WorkHomeColors.SecondaryText,
                                                modifier = Modifier.padding(bottom = 2.dp),
                                            )
                                        }
                                    }
                                }
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
                                        text = strings.daysLeft,
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
                        text = strings.activeChores,
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
                            text = strings.tasksCount(activeChores.size),
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
                                    text = if (state.editingTemplateId == null) strings.saveTemplateHeader else strings.editTemplateHeader,
                                    fontWeight = FontWeight.Bold,
                                    color = WorkHomeColors.PrimaryText,
                                )
                                OutlinedTextField(
                                    value = state.templateTitle,
                                    onValueChange = onTemplateTitleChange,
                                    label = { Text(strings.templateTitleLabel) },
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
                                    label = { Text(strings.templateRewardLabel) },
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
                                    text = strings.autoCreationSchedule,
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
                                        null to strings.recurrenceManual,
                                        1 to strings.recurrenceDaily,
                                        2 to strings.recurrenceTwoDays,
                                        3 to strings.recurrenceThreeDays,
                                        7 to strings.recurrenceWeekly,
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
                                        Text(if (state.editingTemplateId == null) strings.saveTemplateButton else strings.updateTemplateButton)
                                    }
                                    if (state.editingTemplateId != null) {
                                        OutlinedButton(
                                            onClick = onCancelTemplateEdit,
                                            enabled = templateFormEnabled,
                                            shape = WorkHomeShapes.PillShape,
                                            border = BorderStroke(1.dp, WorkHomeColors.CardBorder),
                                        ) {
                                            Text(strings.cancelEdit)
                                        }
                                    }
                                }
                                if (state.templateFormSubmitting) {
                                    Text(strings.savingTemplate, color = WorkHomeColors.SecondaryText, style = MaterialTheme.typography.bodySmall)
                                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = WorkHomeColors.CyanAccent)
                                }
                            }
                        }
                    }
                }

                if (state.choreTemplates.isNotEmpty()) {
                    item {
                        Text(
                            text = strings.choreTemplates,
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

            if (activeChores.isEmpty()) {
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
                                text = strings.noActiveChores,
                                style = MaterialTheme.typography.bodyMedium,
                                color = WorkHomeColors.SecondaryText,
                            )
                        }
                    }
                }
            }

            items(activeChores, key = { it.id }) { chore ->
                val busyAction = state.busyChoreActions[chore.id]
                val isOpen = chore.assignedToUserId.isBlank()
                val isAssignedToCurrentUser = chore.assignedToUserId == currentUser.authUid
                val assigneeName = state.usersByAuthUid[chore.assignedToUserId]
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
                                                text = strings.awaitingApproval,
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
                                            contentDescription = strings.templateRewardLabel,
                                            tint = WorkHomeColors.RewardStar,
                                            modifier = Modifier.size(14.dp),
                                        )
                                        Text(
                                            text = strings.rewardMinutesFormat(chore.reward),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = WorkHomeColors.SecondaryText,
                                        )
                                    }
                                    val assignmentText = when {
                                        isOpen -> strings.unassigned
                                        isAssignedToCurrentUser -> strings.assignedToYou
                                        currentUser.isAdmin -> strings.assignedToUser(assigneeName ?: strings.unassigned)
                                        else -> strings.assigned
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Person,
                                            contentDescription = strings.assignee,
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
                                                text = if (busyAction == ChoreRowAction.APPROVE) strings.approving else strings.approveChore,
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
                                                text = if (busyAction == ChoreRowAction.ASSIGN) strings.assigning else strings.assignToMe,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                    }
                                }

                                isAssignedToCurrentUser -> {
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
                                                    text = if (busyAction == ChoreRowAction.COMPLETE) strings.completing else strings.completeForMe,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                )
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
                                    Text(if (busyAction == ChoreRowAction.RESET) strings.resettingAssignment else strings.resetAssignment)
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
                                    Text(if (busyAction == ChoreRowAction.DELETE) strings.deleting else strings.deleteChore)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = strings.completedChores,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = WorkHomeColors.PrimaryText,
                    )
                    if (state.completedChores.isEmpty()) {
                        Text(
                            text = strings.noCompletedChores,
                            style = MaterialTheme.typography.bodyMedium,
                            color = WorkHomeColors.SecondaryText,
                        )
                    }
                }
            }

            if (state.completedChores.isNotEmpty()) {
                completedGroups.forEach { (month, chores) ->
                    item(key = "month-${month?.toString() ?: "unknown"}") {
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
                                    .padding(WorkHomeDimens.CardPadding),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Text(
                                    text = monthLabel(month),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = WorkHomeColors.PrimaryText,
                                )
                                Text(
                                    text = strings.totalRewardFormat(chores.sumOf { it.reward }),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = WorkHomeColors.SecondaryText,
                                )
                            }
                        }
                    }
                    item(key = "month-rows-${month?.toString() ?: "unknown"}") {
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
                                chores.forEachIndexed { index, completed ->
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
                    Text(strings.delete, color = Color(0xFFFF6E6E))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { choreToDelete = null },
                    enabled = !choreDeleteBusy,
                ) {
                    Text(strings.cancel, color = WorkHomeColors.SecondaryText)
                }
            },
            title = { Text(strings.deleteChoreTitle) },
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
                    Text(strings.delete, color = Color(0xFFFF6E6E))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { templateToDelete = null },
                    enabled = !templateDeleteBusy,
                ) {
                    Text(strings.cancel, color = WorkHomeColors.SecondaryText)
                }
            },
            title = { Text(strings.deleteTemplateTitle) },
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

    userForGoalSetting?.let { user ->
        SetGoalDialog(
            user = user,
            onConfirm = { goal, eligible ->
                onSetUserGoal(user, goal, eligible)
                userForGoalSetting = null
            },
            onDismiss = { userForGoalSetting = null },
        )
    }
}

internal fun daysLeftInCurrentMonth(date: LocalDate = LocalDate.now()): Int = date.lengthOfMonth() - date.dayOfMonth

private val completionMonthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
private val completionTimestampFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a", Locale.getDefault())

private fun yearMonthFromEpochMillis(epochMillis: Long): YearMonth =
    YearMonth.from(Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()))

private fun monthLabel(month: YearMonth?): String = month?.format(completionMonthFormatter) ?: "Unknown month"

private fun completionTimestampLabel(epochMillis: Long?): String =
    epochMillis?.let {
        Instant.ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .format(completionTimestampFormatter)
    } ?: "Unknown completion time"

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
                append(completionTimestampLabel(completed.completedAtMillis))
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
    val strings = LocalAppStrings.current
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
                            text = strings.rewardMinutesFormat(template.reward),
                            color = WorkHomeColors.SecondaryText,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Text(
                        text = "• ${strings.recurrence}: ${template.recurrenceLabel()}",
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
                        Text(if (busyAction == TemplateRowAction.ACTIVATE) strings.activating else strings.activate)
                    }
                    OutlinedButton(
                        onClick = { onEditTemplate(template) },
                        enabled = !isBusy,
                        shape = WorkHomeShapes.PillShape,
                        border = BorderStroke(1.dp, WorkHomeColors.CardBorder),
                    ) {
                        Text(strings.edit)
                    }
                    OutlinedButton(
                        onClick = { onDeleteTemplate(template) },
                        enabled = !isBusy,
                        shape = WorkHomeShapes.PillShape,
                        border = BorderStroke(1.dp, Color(0x33FF5252)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6E6E)),
                    ) {
                        Text(if (busyAction == TemplateRowAction.DELETE) strings.deleting else strings.delete)
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
    val strings = LocalAppStrings.current

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
                                text = strings.createChoreTitle,
                                fontWeight = FontWeight.Bold,
                                color = WorkHomeColors.PrimaryText,
                                style = MaterialTheme.typography.titleLarge,
                            )
                            OutlinedTextField(
                                value = state.choreTitleInput,
                                onValueChange = onChoreTitleChange,
                                label = { Text(strings.choreTitleLabel) },
                                placeholder = { Text(strings.choreTitlePlaceholder) },
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
                                label = { Text(strings.rewardMinutes) },
                                placeholder = { Text(strings.choreRewardPlaceholder) },
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
                                    text = strings.assignToOptional,
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
                                        label = { Text(strings.unassigned) },
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
                                            text = strings.markCompletedImmediately,
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
                                        text = strings.setToCompleteAwaitsApproval,
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
                                    text = if (state.createChoreSubmitting) strings.creating else strings.createChoreButton,
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
    onSetUserGoal: (AppUser, Long?, Long?) -> Unit,
    onRemoveUser: (AppUser) -> Unit,
    onRemovePendingUser: (PendingUser) -> Unit,
    onClearMessage: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val strings = LocalAppStrings.current
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
                        Text(strings.inviteFamilyMember, fontWeight = FontWeight.Bold)
                        OutlinedTextField(value = state.name, onValueChange = onNameChange, label = { Text(strings.name) }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = state.email, onValueChange = onEmailChange, label = { Text(strings.email) }, modifier = Modifier.fillMaxWidth())
                        Text(strings.role)
                        RowRoles(selectedRole = state.role, onRoleChange = onRoleChange)
                        Button(onClick = onAddUser, enabled = !state.submitting) {
                            Text(if (state.submitting) strings.savingInvite else strings.addInvite)
                        }
                        Text(strings.passwordWorkflowNote)
                    }
                }
            }

            item { Text(strings.activeUsers, fontWeight = FontWeight.Bold) }
            items(state.users, key = { it.id }) { user ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(user.name, fontWeight = FontWeight.Bold)
                        Text(user.email)
                        Text(strings.roleFormat(user.role.value))
                        Text(user.rewardGoal?.let { strings.goalFormat(it) } ?: strings.goalNotSet)
                        val accumulated = user.accumulatedReward()
                        val eligible = user.rewardEligible
                        if (eligible != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Text(
                                    text = "${strings.accumulatedRewardLabel}: ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = WorkHomeColors.SecondaryText,
                                )
                                Icon(
                                    imageVector = GoldCoinsIcon,
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(16.dp),
                                )
                                Text(
                                    text = "${accumulated ?: 0}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = WorkHomeColors.PrimaryText,
                                )
                                Text(
                                    text = " / $eligible",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = WorkHomeColors.SecondaryText,
                                )
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { userForGoalSetting = user }, enabled = !state.submitting) {
                                Text(strings.setGoalTitle)
                            }
                            OutlinedButton(onClick = { userToRemove = user }, enabled = !state.submitting) {
                                Text(strings.removeProfile)
                            }
                        }
                    }
                }
            }

            item { Text(strings.pendingInvites, fontWeight = FontWeight.Bold) }
            items(state.pendingUsers, key = { it.emailKey }) { pendingUser ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(pendingUser.name, fontWeight = FontWeight.Bold)
                        Text(pendingUser.email)
                        Text(strings.roleFormat(pendingUser.role.value))
                        OutlinedButton(onClick = { pendingToRemove = pendingUser }, enabled = !state.submitting) {
                            Text(strings.removeInvite)
                        }
                    }
                }
            }
        }
    }

    userToRemove?.let { user ->
        ConfirmDialog(
            title = strings.removeProfileTitle,
            body = strings.removeProfileBody(user.email),
            onConfirm = {
                onRemoveUser(user)
                userToRemove = null
            },
            onDismiss = { userToRemove = null },
        )
    }

    pendingToRemove?.let { pendingUser ->
        ConfirmDialog(
            title = strings.removeInviteTitle,
            body = strings.removeInviteBody(pendingUser.email),
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
            onConfirm = { goal, eligible ->
                onSetUserGoal(user, goal, eligible)
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
    val strings = LocalAppStrings.current
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf(UserRole.MEMBER, UserRole.ADMIN).forEach { role ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = selectedRole == role, onClick = { onRoleChange(role) })
                Text(if (role == UserRole.ADMIN) strings.roleAdmin else strings.roleMember)
            }
        }
    }
}

@Composable
private fun RewardsScreen(
    state: RewardsUiState,
    onResetRewards: () -> Unit,
    onSetUserGoal: (AppUser, Long?, Long?) -> Unit = { _, _, _ -> },
    onClearMessage: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val strings = LocalAppStrings.current
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
                            Text(strings.adminControls, fontWeight = FontWeight.Bold)
                            Text(strings.resetDescription)
                            Button(onClick = { showResetConfirmation = true }, enabled = !state.resetting) {
                                Text(if (state.resetting) strings.resettingMinutes else strings.resetMinutes)
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
                                    Text(strings.setGoalTitle)
                                }
                            }
                        }
                        Text(strings.totalMinutesFormat(user.rewardProgressText()))
                        val accumulated = user.accumulatedReward()
                        val eligible = user.rewardEligible
                        if (eligible != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Text(
                                    text = "${strings.accumulatedRewardLabel}: ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = WorkHomeColors.SecondaryText,
                                )
                                Icon(
                                    imageVector = GoldCoinsIcon,
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(16.dp),
                                )
                                Text(
                                    text = "${accumulated ?: 0}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = WorkHomeColors.PrimaryText,
                                )
                                Text(
                                    text = " / $eligible",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = WorkHomeColors.SecondaryText,
                                )
                            }
                        }
                        Text(strings.daysSinceLastChoreFormat(daysText))
                    }
                }
            }
        }
    }

    if (showResetConfirmation) {
        ConfirmDialog(
            title = strings.resetConfirmTitle,
            body = strings.resetConfirmBody,
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
            onConfirm = { goal, eligible ->
                onSetUserGoal(user, goal, eligible)
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
    confirmText: String = LocalAppStrings.current.confirm,
    cancelText: String = LocalAppStrings.current.cancel,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(cancelText)
            }
        },
        title = { Text(title) },
        text = { Text(body) },
    )
}

@Composable
private fun SetGoalDialog(
    user: AppUser,
    onConfirm: (goal: Long?, rewardEligible: Long?) -> Unit,
    onDismiss: () -> Unit,
) {
    val strings = LocalAppStrings.current
    var goalText by remember { mutableStateOf(user.rewardGoal?.toString().orEmpty()) }
    var eligibleText by remember { mutableStateOf(user.rewardEligible?.toString().orEmpty()) }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.setGoalTitle) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(strings.setGoalForUser(user.name))
                OutlinedTextField(
                    value = goalText,
                    onValueChange = { input ->
                        goalText = input.filter { it.isDigit() }
                        errorText = null
                    },
                    label = { Text(strings.targetMinutesGoal) },
                    placeholder = { Text("e.g. 500") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = errorText != null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = eligibleText,
                    onValueChange = { input ->
                        eligibleText = input.filter { it.isDigit() }
                        errorText = null
                    },
                    label = { Text(strings.totalRewardEligible) },
                    placeholder = { Text(strings.rewardEligiblePlaceholder) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = errorText != null,
                    supportingText = {
                        Text(errorText ?: strings.leaveEmptyToClear)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedGoal = if (goalText.isBlank()) null else goalText.toLongOrNull()
                    val parsedEligible = if (eligibleText.isBlank()) null else eligibleText.toLongOrNull()
                    if (goalText.isNotBlank() && (parsedGoal == null || parsedGoal <= 0)) {
                        errorText = "Enter a valid positive number for minutes goal"
                    } else if (eligibleText.isNotBlank() && (parsedEligible == null || parsedEligible <= 0)) {
                        errorText = "Enter a valid positive number for reward eligible"
                    } else {
                        onConfirm(parsedGoal, parsedEligible)
                    }
                },
            ) {
                Text(strings.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel)
            }
        },
    )
}

private fun <T : ViewModel> simpleFactory(creator: () -> T): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = creator() as VM
    }
