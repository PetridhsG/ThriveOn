package gr.aueb.thriveon.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import gr.aueb.thriveon.domain.interactors.AuthInteractor
import gr.aueb.thriveon.ui.screens.categories.navigation.categoriesScreen
import gr.aueb.thriveon.ui.screens.categories.navigation.navigateToCategoriesScreen
import gr.aueb.thriveon.ui.screens.editProfile.navigation.editProfileScreen
import gr.aueb.thriveon.ui.screens.editProfile.navigation.navigateToEditProfileScreen
import gr.aueb.thriveon.ui.screens.feed.navigation.feedScreen
import gr.aueb.thriveon.ui.screens.feed.navigation.navigateToFeedScreen
import gr.aueb.thriveon.ui.screens.friends.navigation.friendsScreen
import gr.aueb.thriveon.ui.screens.friends.navigation.navigateToFriendsScreen
import gr.aueb.thriveon.ui.screens.home.navigation.HomeRoute
import gr.aueb.thriveon.ui.screens.home.navigation.homeScreen
import gr.aueb.thriveon.ui.screens.home.navigation.navigateToHomeScreen
import gr.aueb.thriveon.ui.screens.notifications.navigation.navigateToNotificationsScreen
import gr.aueb.thriveon.ui.screens.notifications.navigation.notificationsScreen
import gr.aueb.thriveon.ui.screens.profile.navigation.navigateToProfileScreen
import gr.aueb.thriveon.ui.screens.profile.navigation.profileScreen
import gr.aueb.thriveon.ui.screens.progress.navigation.navigateToProgressScreen
import gr.aueb.thriveon.ui.screens.progress.navigation.progressScreen
import gr.aueb.thriveon.ui.screens.search.navigation.navigateToSearchScreen
import gr.aueb.thriveon.ui.screens.search.navigation.searchScreen
import gr.aueb.thriveon.ui.screens.signIn.navigation.SignInRoute
import gr.aueb.thriveon.ui.screens.signIn.navigation.navigateToSignInScreen
import gr.aueb.thriveon.ui.screens.signIn.navigation.signInScreen
import gr.aueb.thriveon.ui.screens.titles.navigation.navigateToTitlesScreen
import gr.aueb.thriveon.ui.screens.titles.navigation.titlesScreen
import gr.aueb.thriveon.ui.theme.Colors
import org.koin.compose.koinInject

@Composable
fun AppNavHost(
    authInteractor: AuthInteractor = koinInject(),
    navController: NavHostController
) {
    val startDestination = if (authInteractor.getCurrentUserId() != null) {
        HomeRoute("false")
    } else {
        SignInRoute
    }

    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.Colors.textOrange,
        backgroundColor = MaterialTheme.Colors.textOrange
    )
    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.Colors.backgroundMaroonDark)
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination,
                enterTransition = { enterTransition() },
                exitTransition = { exitTransition() },
                popEnterTransition = { popEnterTransition() },
                popExitTransition = { popExitTransition() }
            ) {
                signInScreen(
                    onNavigateToHome = { navController.navigateToHomeScreen(it) },
                    onNavigateToSignCategoriesUp = navController::navigateToCategoriesScreen
                )
                categoriesScreen(
                    onNavigateToHome = { navController.navigateToHomeScreen(it) },
                    onNavigateBack = navController::popBackStack
                )
                homeScreen(
                    onNavigateToFeed = navController::navigateToFeedScreen,
                    onNavigateToSearch = navController::navigateToSearchScreen,
                    onNavigateToNotifications = navController::navigateToNotificationsScreen,
                    onNavigateToProfile = { navController.navigateToProfileScreen(authInteractor.getCurrentUserId()) }
                )
                feedScreen(
                    onNavigateToHome = navController::navigateToHomeScreen,
                    onNavigateToSearch = navController::navigateToSearchScreen,
                    onNavigateToNotifications = navController::navigateToNotificationsScreen,
                    onNavigateToProfile = { navController.navigateToProfileScreen(authInteractor.getCurrentUserId()) },
                    onNavigateToUserProfile = { userId ->
                        navController.navigateToProfileScreen(
                            userId
                        )
                    }
                )
                searchScreen(
                    onNavigateToHome = navController::navigateToHomeScreen,
                    onNavigateToFeed = navController::navigateToFeedScreen,
                    onNavigateToNotifications = navController::navigateToNotificationsScreen,
                    onNavigateToProfile = { navController.navigateToProfileScreen(authInteractor.getCurrentUserId()) },
                    onNavigateToUserProfile = { userId ->
                        navController.navigateToProfileScreen(
                            userId
                        )
                    }
                )
                notificationsScreen(
                    onNavigateToHome = navController::navigateToHomeScreen,
                    onNavigateToFeed = navController::navigateToFeedScreen,
                    onNavigateToSearch = navController::navigateToSearchScreen,
                    onNavigateToProfile = { navController.navigateToProfileScreen(authInteractor.getCurrentUserId()) },
                    onNavigateToUserProfile = { userId ->
                        navController.navigateToProfileScreen(
                            userId
                        )
                    }
                )
                profileScreen(
                    onNavigateToHome = navController::navigateToHomeScreen,
                    onNavigateToFeed = navController::navigateToFeedScreen,
                    onNavigateToSearch = navController::navigateToSearchScreen,
                    onNavigateToNotifications = navController::navigateToNotificationsScreen,
                    onNavigateToProfile = { navController.navigateToProfileScreen(authInteractor.getCurrentUserId()) },
                    onNavigateToFriends = { userId -> navController.navigateToFriendsScreen(userId) },
                    onNavigateToTitles = { userId -> navController.navigateToTitlesScreen(userId) },
                    onNavigateToEditProfile = navController::navigateToEditProfileScreen
                )
                friendsScreen(
                    onNavigateBack = navController::popBackStack,
                    onNavigateToUserProfile = { userId ->
                        navController.navigateToProfileScreen(
                            userId
                        )
                    }
                )
                titlesScreen(
                    onNavigateToProgress = { userId -> navController.navigateToProgressScreen(userId) },
                    onNavigateBack = navController::popBackStack
                )
                progressScreen(
                    onNavigateBack = navController::popBackStack
                )
                editProfileScreen(
                    onNavigateBack = navController::popBackStack,
                    onNavigateToCategories = navController::navigateToCategoriesScreen,
                    onLogout = navController::navigateToSignInScreen
                )
            }
        }
    }
}
