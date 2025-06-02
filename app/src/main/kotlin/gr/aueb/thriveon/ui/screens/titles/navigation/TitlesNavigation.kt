package gr.aueb.thriveon.ui.screens.titles.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import gr.aueb.thriveon.ui.screens.titles.TitlesScreen
import gr.aueb.thriveon.ui.screens.titles.TitlesViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data class TitlesRoute(val userId: String? = null)

fun NavController.navigateToTitlesScreen(userId: String) {
    navigate(TitlesRoute(userId = userId))
}

fun NavGraphBuilder.titlesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProgress: (String) -> Unit,
) {
    composable<TitlesRoute> { backStackEntry ->
        val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
        val viewModel: TitlesViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        val effectFlow = remember(viewModel) { viewModel.uiEffect }

        TitlesScreen(
            state = state,
            onEvent = viewModel::setEvent,
            effect = effectFlow,
            userId = userId,
            onNavigateBack = onNavigateBack,
            onNavigateToProgress = onNavigateToProgress
        )
    }
}
