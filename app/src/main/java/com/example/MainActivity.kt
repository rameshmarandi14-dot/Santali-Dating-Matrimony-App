package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.SantaliDatabase
import com.example.data.local.UserPreferencesDataStore
import com.example.data.repository.MatrimonyRepository
import com.example.ui.components.FilterPreferencesBottomSheet
import com.example.ui.components.MatchCelebrationDialog
import com.example.ui.screens.AuthAndOnboardingScreen
import com.example.ui.screens.CallScreen
import com.example.ui.screens.ChatDetailScreen
import com.example.ui.screens.ChatListScreen
import com.example.ui.screens.HomeDiscoveryScreen
import com.example.ui.screens.IdVerificationScreen
import com.example.ui.screens.MatchesAndLikesScreen
import com.example.ui.screens.ProfileBiodataScreen
import com.example.ui.screens.SettingsPrivacyScreen
import com.example.ui.screens.UpgradePremiumScreen
import com.example.ui.theme.SantaliJuriTheme
import com.example.ui.viewmodel.MatrimonyViewModel

object Routes {
    const val HOME = "home"
    const val MATCHES = "matches"
    const val CHAT_LIST = "chat_list"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val ID_VERIFICATION = "id_verification"
    const val UPGRADE_PREMIUM = "upgrade_premium"
    const val CHAT_DETAIL = "chat_detail/{matchId}"
    const val CALL = "call/{matchId}/{isVideo}"

    fun chatDetail(matchId: String) = "chat_detail/$matchId"
    fun call(matchId: String, isVideo: Boolean) = "call/$matchId/$isVideo"
}

private data class TopLevelDestination(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val TOP_LEVEL_DESTINATIONS = listOf(
    TopLevelDestination(
        route = Routes.HOME,
        labelRes = R.string.nav_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "nav_home"
    ),
    TopLevelDestination(
        route = Routes.MATCHES,
        labelRes = R.string.nav_matches,
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder,
        testTag = "nav_matches"
    ),
    TopLevelDestination(
        route = Routes.CHAT_LIST,
        labelRes = R.string.nav_chat,
        selectedIcon = Icons.AutoMirrored.Filled.Chat,
        unselectedIcon = Icons.AutoMirrored.Outlined.Chat,
        testTag = "nav_chat"
    ),
    TopLevelDestination(
        route = Routes.PROFILE,
        labelRes = R.string.nav_profile,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        testTag = "nav_profile"
    )
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = SantaliDatabase.getInstance(applicationContext)
        val prefs = UserPreferencesDataStore(applicationContext)
        val repository = MatrimonyRepository(db.santaliDao(), prefs)

        setContent {
            SantaliJuriTheme {
                val viewModel: MatrimonyViewModel = viewModel(
                    factory = MatrimonyViewModel.Factory(repository)
                )
                SantaliJuriApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SantaliJuriApp(viewModel: MatrimonyViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val matchCelebration by viewModel.matchCelebration.collectAsStateWithLifecycle()
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatusMessage()
        }
    }

    if (uiState.isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (!uiState.preferences.isLoggedIn) {
        AuthAndOnboardingScreen(
            authError = authError,
            onClearAuthError = { viewModel.clearAuthError() },
            onLoginWithCredentials = { identifier, password ->
                viewModel.loginWithCredentials(identifier, password)
            },
            onSignInDemo = { viewModel.signInDemo() },
            onCreateProfile = { fullName, age, gender, parsiClan, location, state, edu, eduCat, photoUri, accountIdentifier, accountPassword ->
                viewModel.createProfileAndEnter(
                    fullName = fullName,
                    age = age,
                    gender = gender,
                    parsiClan = parsiClan,
                    location = location,
                    state = state,
                    educationProfession = edu,
                    educationCategory = eduCat,
                    photoUri = photoUri,
                    accountIdentifier = accountIdentifier,
                    accountPassword = accountPassword
                )
            }
        )
        return
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showTopLevelNav = currentRoute in TOP_LEVEL_DESTINATIONS.map { it.route }

    var showFiltersSheet by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 600.dp

        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (showTopLevelNav && !useRail) {
                    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                        TOP_LEVEL_DESTINATIONS.forEach { dest ->
                            val selected = currentRoute == dest.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(dest.route) {
                                        popUpTo(Routes.HOME) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    val badgeCount = if (dest.route == Routes.MATCHES) {
                                        uiState.incomingLikes.size
                                    } else 0
                                    if (badgeCount > 0) {
                                        BadgedBox(badge = { Badge { Text(badgeCount.toString()) } }) {
                                            Icon(
                                                imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                                contentDescription = stringResource(dest.labelRes)
                                            )
                                        }
                                    } else {
                                        Icon(
                                            imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                            contentDescription = stringResource(dest.labelRes)
                                        )
                                    }
                                },
                                label = { Text(stringResource(dest.labelRes)) },
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (showTopLevelNav && useRail) {
                    NavigationRail(containerColor = MaterialTheme.colorScheme.surface) {
                        TOP_LEVEL_DESTINATIONS.forEach { dest ->
                            val selected = currentRoute == dest.route
                            NavigationRailItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(dest.route) {
                                        popUpTo(Routes.HOME) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = stringResource(dest.labelRes)
                                    )
                                },
                                label = { Text(stringResource(dest.labelRes)) },
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = Routes.HOME,
                    modifier = Modifier.weight(1f)
                ) {
                    composable(Routes.HOME) {
                        HomeDiscoveryScreen(
                            currentUser = uiState.currentUser,
                            deck = uiState.discoveryDeck,
                            preferences = uiState.preferences,
                            canRewind = uiState.canRewind,
                            onAccept = { viewModel.onAcceptProfile(it) },
                            onReject = { viewModel.onRejectProfile(it) },
                            onRewind = { viewModel.onRewindLastSwipe() },
                            onResetDeck = { viewModel.resetPassedProfiles() },
                            onOpenFilters = { showFiltersSheet = true },
                            onOpenPremium = { navController.navigate(Routes.UPGRADE_PREMIUM) },
                            onNavigateToProfile = {
                                navController.navigate(Routes.PROFILE) {
                                    popUpTo(Routes.HOME) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            onNavigateToChat = {
                                navController.navigate(Routes.CHAT_LIST) {
                                    popUpTo(Routes.HOME) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }

                    composable(Routes.MATCHES) {
                        MatchesAndLikesScreen(
                            matches = uiState.matches,
                            incomingLikes = uiState.incomingLikes,
                            allowCalls = uiState.preferences.allowCalls,
                            isPremium = uiState.preferences.isPremium,
                            onAcceptLike = { viewModel.onAcceptProfile(it) },
                            onRejectLike = { viewModel.onRejectProfile(it) },
                            onOpenChat = { profile ->
                                navController.navigate(Routes.chatDetail(profile.id))
                            },
                            onStartAudioCall = { profile ->
                                navController.navigate(Routes.call(profile.id, isVideo = false))
                            },
                            onOpenPremium = { navController.navigate(Routes.UPGRADE_PREMIUM) }
                        )
                    }

                    composable(Routes.CHAT_LIST) {
                        ChatListScreen(
                            matches = uiState.matches,
                            lastMessagesByMatch = uiState.lastMessagesByMatch,
                            onOpenChat = { profile ->
                                navController.navigate(Routes.chatDetail(profile.id))
                            }
                        )
                    }

                    composable(Routes.PROFILE) {
                        ProfileBiodataScreen(
                            currentUser = uiState.currentUser,
                            isPremium = uiState.preferences.isPremium,
                            onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                            onOpenVerification = { navController.navigate(Routes.ID_VERIFICATION) },
                            onOpenPremium = { navController.navigate(Routes.UPGRADE_PREMIUM) },
                            onSaveBiodata = { fullName, age, gender, parsi, loc, state, height, edu, eduCat, about, interests, photoUri, gallery ->
                                viewModel.saveBiodataChanges(
                                    fullName = fullName,
                                    age = age,
                                    gender = gender,
                                    parsiClan = parsi,
                                    location = loc,
                                    state = state,
                                    height = height,
                                    educationProfession = edu,
                                    educationCategory = eduCat,
                                    aboutMe = about,
                                    interests = interests,
                                    photoUri = photoUri,
                                    galleryUris = gallery
                                )
                            }
                        )
                    }

                    composable(Routes.SETTINGS) {
                        SettingsPrivacyScreen(
                            preferences = uiState.preferences,
                            currentUser = uiState.currentUser,
                            blockedProfiles = uiState.blockedProfiles,
                            onBack = { navController.popBackStack() },
                            onEditBiodata = {
                                navController.popBackStack()
                                navController.navigate(Routes.PROFILE) {
                                    launchSingleTop = true
                                }
                            },
                            onToggleHideSameClan = { viewModel.setHideFromSameClan(it) },
                            onTogglePhotoPrivacy = { viewModel.setPhotoPrivacyVerifiedOnly(it) },
                            onToggleAllowCalls = { viewModel.setAllowCalls(it) },
                            onToggleNotifications = { viewModel.setNotificationsEnabled(it) },
                            onUnblockUser = { viewModel.unblockProfile(it) },
                            onOpenVerification = { navController.navigate(Routes.ID_VERIFICATION) },
                            onOpenPremium = { navController.navigate(Routes.UPGRADE_PREMIUM) },
                            onLogout = { viewModel.logout() },
                            onDeleteAccount = { viewModel.deleteAccount() }
                        )
                    }

                    composable(Routes.ID_VERIFICATION) {
                        IdVerificationScreen(
                            currentUser = uiState.currentUser,
                            verificationRecord = uiState.verificationRecord,
                            onBack = { navController.popBackStack() },
                            onSubmitVerification = { docType, frontUri, backUri, selfieUri ->
                                viewModel.submitIdVerification(
                                    documentType = docType,
                                    frontImageUri = frontUri,
                                    backImageUri = backUri,
                                    selfieUri = selfieUri
                                )
                            }
                        )
                    }

                    composable(Routes.UPGRADE_PREMIUM) {
                        UpgradePremiumScreen(
                            preferences = uiState.preferences,
                            onBack = { navController.popBackStack() },
                            onActivatePlan = { planId, planLabel ->
                                viewModel.activatePremiumPlan(planId, planLabel)
                            }
                        )
                    }

                    composable(
                        route = Routes.CHAT_DETAIL,
                        arguments = listOf(navArgument("matchId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val matchId = backStackEntry.arguments?.getString("matchId") ?: return@composable
                        val profile by viewModel.observeProfile(matchId).collectAsStateWithLifecycle(initialValue = null)
                        val messages by viewModel.observeChatMessages(matchId).collectAsStateWithLifecycle(initialValue = emptyList())

                        profile?.let { matchProfile ->
                            ChatDetailScreen(
                                profile = matchProfile,
                                messages = messages,
                                allowCalls = uiState.preferences.allowCalls,
                                onBack = { navController.popBackStack() },
                                onSendMessage = { text -> viewModel.sendMessage(matchId, text) },
                                onStartAudioCall = {
                                    navController.navigate(Routes.call(matchId, isVideo = false))
                                },
                                onStartVideoCall = {
                                    navController.navigate(Routes.call(matchId, isVideo = true))
                                },
                                onBlockUser = {
                                    viewModel.blockProfile(matchId, matchProfile.fullName)
                                    navController.popBackStack()
                                }
                            )
                        }
                    }

                    composable(
                        route = Routes.CALL,
                        arguments = listOf(
                            navArgument("matchId") { type = NavType.StringType },
                            navArgument("isVideo") { type = NavType.BoolType }
                        )
                    ) { backStackEntry ->
                        val matchId = backStackEntry.arguments?.getString("matchId") ?: return@composable
                        val isVideo = backStackEntry.arguments?.getBoolean("isVideo") ?: false
                        val profile by viewModel.observeProfile(matchId).collectAsStateWithLifecycle(initialValue = null)

                        profile?.let { remoteProfile ->
                            CallScreen(
                                remoteProfile = remoteProfile,
                                currentUser = uiState.currentUser,
                                initialIsVideo = isVideo,
                                onHangUp = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showFiltersSheet) {
        FilterPreferencesBottomSheet(
            preferences = uiState.preferences,
            myClan = uiState.currentUser?.parsiClan.orEmpty(),
            onApply = { excludeMyClan, preferredClans, minAge, maxAge, state, edu, verifiedOnly ->
                viewModel.applyFilters(
                    excludeMyClan = excludeMyClan,
                    preferredClans = preferredClans,
                    minAge = minAge,
                    maxAge = maxAge,
                    selectedState = state,
                    selectedEducation = edu,
                    verifiedOnly = verifiedOnly
                )
            },
            onReset = { viewModel.resetFilters() },
            onDismiss = { showFiltersSheet = false }
        )
    }

    matchCelebration?.let { matched ->
        MatchCelebrationDialog(
            matchedProfile = matched,
            currentUser = uiState.currentUser,
            onSendMessage = {
                viewModel.dismissMatchCelebration()
                navController.navigate(Routes.chatDetail(matched.id))
            },
            onKeepSwiping = { viewModel.dismissMatchCelebration() }
        )
    }
}
