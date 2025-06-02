package gr.aueb.thriveon.ui.screens.profile.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import gr.aueb.thriveon.ui.screens.profile.ProfileScreen
import gr.aueb.thriveon.ui.screens.profile.ProfileViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data class ProfileRoute(val userId: String? = null)

fun NavController.navigateToProfileScreen(userId: String? = null) {
    val route = ProfileRoute(userId)
    this.navigate(route)
}

fun NavGraphBuilder.profileScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToFeed: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToFriends: (String) -> Unit,
    onNavigateToTitles: (String) -> Unit,
    onNavigateToEditProfile: () -> Unit,
) {
    composable<ProfileRoute> { backStackEntry ->
        val userId = backStackEntry.arguments?.getString("userId") ?: return@composable

        val viewModel: ProfileViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        val effectFlow = remember(viewModel) { viewModel.uiEffect }

        ProfileScreen(
            userId = userId,
            state = state,
            onEvent = viewModel::setEvent,
            effect = effectFlow,
            onNavigateToHome = onNavigateToHome,
            onNavigateToFeed = onNavigateToFeed,
            onNavigateToSearch = onNavigateToSearch,
            onNavigateToNotifications = onNavigateToNotifications,
            onNavigateToProfile = onNavigateToProfile,
            onNavigateToFriends = onNavigateToFriends,
            onNavigateToTitles = onNavigateToTitles,
            onNavigateToEditProfile = onNavigateToEditProfile
        )
    }
}
