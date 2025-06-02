package gr.aueb.thriveon.ui.screens.progress.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import gr.aueb.thriveon.ui.screens.progress.ProgressScreen
import gr.aueb.thriveon.ui.screens.progress.ProgressViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data class ProgressRoute(val userId: String? = null)

fun NavController.navigateToProgressScreen(userId: String = "") {
    navigate(ProgressRoute(userId = userId))
}

fun NavGraphBuilder.progressScreen(
    onNavigateBack: () -> Unit,
) {
    composable<ProgressRoute> { backStackEntry ->
        val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
        val viewModel: ProgressViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        val effectFlow = remember(viewModel) { viewModel.uiEffect }

        ProgressScreen(
            state = state,
            onEvent = viewModel::setEvent,
            effect = effectFlow,
            userId = userId,
            onNavigateBack = onNavigateBack
        )
    }
}
