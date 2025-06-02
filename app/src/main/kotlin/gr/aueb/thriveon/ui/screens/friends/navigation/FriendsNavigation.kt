package gr.aueb.thriveon.ui.screens.friends.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import gr.aueb.thriveon.ui.screens.friends.FriendsScreen
import gr.aueb.thriveon.ui.screens.friends.FriendsViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data class FriendsRoute(val userId: String? = null)

fun NavController.navigateToFriendsScreen(userId: String = "") {
    navigate(FriendsRoute(userId = userId))
}

fun NavGraphBuilder.friendsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToUserProfile: (String) -> Unit,
) {
    composable<FriendsRoute> { backStackEntry ->
        val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
        val viewModel: FriendsViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        val effectFlow = remember(viewModel) { viewModel.uiEffect }

        FriendsScreen(
            state = state,
            onEvent = viewModel::setEvent,
            effect = effectFlow,
            userId = userId,
            onNavigateBack = onNavigateBack,
            onNavigateToUserProfile = onNavigateToUserProfile,
        )
    }
}
