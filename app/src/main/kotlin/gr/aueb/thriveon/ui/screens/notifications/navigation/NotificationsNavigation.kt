package gr.aueb.thriveon.ui.screens.notifications.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import gr.aueb.thriveon.ui.screens.notifications.NotificationsScreen
import gr.aueb.thriveon.ui.screens.notifications.NotificationsViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object NotificationsRoute

fun NavController.navigateToNotificationsScreen() {
    navigate(NotificationsRoute)
}

fun NavGraphBuilder.notificationsScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToFeed: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToUserProfile: (String) -> Unit,
) {
    composable<NotificationsRoute> {
        val viewModel: NotificationsViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        val effectFlow = remember(viewModel) { viewModel.uiEffect }

        NotificationsScreen(
            state = state,
            onEvent = viewModel::setEvent,
            effect = effectFlow,
            onNavigateToHome = onNavigateToHome,
            onNavigateToFeed = onNavigateToFeed,
            onNavigateToSearch = onNavigateToSearch,
            onNavigateToProfile = onNavigateToProfile,
            onNavigateToUserProfile = onNavigateToUserProfile
        )
    }
}
