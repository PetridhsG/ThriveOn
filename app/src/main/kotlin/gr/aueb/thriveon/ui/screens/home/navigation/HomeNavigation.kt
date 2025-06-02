package gr.aueb.thriveon.ui.screens.home.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import gr.aueb.thriveon.ui.screens.home.HomeScreen
import gr.aueb.thriveon.ui.screens.home.HomeViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data class HomeRoute(val shouldNavigateBack: String? = null)

fun NavController.navigateToHomeScreen(shouldNavigateBack: String = "false") {
    navigate(HomeRoute(shouldNavigateBack = shouldNavigateBack))
}

fun NavGraphBuilder.homeScreen(
    onNavigateToFeed: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
) {
    composable<HomeRoute> { backStackEntry ->
        val shouldNavigateBack =
            backStackEntry.arguments?.getString("shouldNavigateBack") ?: return@composable

        val viewModel: HomeViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        val effectFlow = remember(viewModel) { viewModel.uiEffect }
        HomeScreen(
            state = state,
            onEvent = viewModel::setEvent,
            effect = effectFlow,
            onNavigateToFeed = onNavigateToFeed,
            onNavigateToSearch = onNavigateToSearch,
            onNavigateToNotifications = onNavigateToNotifications,
            onNavigateToProfile = onNavigateToProfile,
            shouldNavigateBack = shouldNavigateBack.toBoolean()
        )
    }
}
