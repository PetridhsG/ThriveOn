package gr.aueb.thriveon.ui.screens.feed.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import gr.aueb.thriveon.ui.screens.feed.FeedScreen
import gr.aueb.thriveon.ui.screens.feed.FeedViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object FeedRoute

fun NavController.navigateToFeedScreen() {
    navigate(FeedRoute)
}

fun NavGraphBuilder.feedScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToUserProfile: (String) -> Unit,
) {
    composable<FeedRoute> {
        val viewModel: FeedViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        val effectFlow = remember(viewModel) { viewModel.uiEffect }

        FeedScreen(
            state = state,
            onEvent = viewModel::setEvent,
            effect = effectFlow,
            onNavigateToHome = onNavigateToHome,
            onNavigateToSearch = onNavigateToSearch,
            onNavigateToProfile = onNavigateToProfile,
            onNavigateToNotifications = onNavigateToNotifications,
            onNavigateToUserProfile = onNavigateToUserProfile
        )
    }
}
