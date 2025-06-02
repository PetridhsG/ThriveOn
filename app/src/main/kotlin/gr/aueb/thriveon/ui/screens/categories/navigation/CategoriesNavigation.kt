package gr.aueb.thriveon.ui.screens.categories.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import gr.aueb.thriveon.ui.screens.categories.CategoriesScreen
import gr.aueb.thriveon.ui.screens.categories.CategoriesViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object CategoriesRoute

fun NavController.navigateToCategoriesScreen() {
    navigate(CategoriesRoute)
}

fun NavGraphBuilder.categoriesScreen(
    onNavigateToHome: (String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    composable<CategoriesRoute> {
        val viewModel: CategoriesViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        val effectFlow = remember(viewModel) { viewModel.uiEffect }

        CategoriesScreen(
            state = state,
            onEvent = viewModel::setEvent,
            effect = effectFlow,
            onNavigateToHome = onNavigateToHome,
            onNavigateBack = onNavigateBack
        )
    }
}
