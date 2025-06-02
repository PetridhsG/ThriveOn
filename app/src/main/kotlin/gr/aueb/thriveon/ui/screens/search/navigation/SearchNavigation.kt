package gr.aueb.thriveon.ui.screens.search.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import gr.aueb.thriveon.ui.screens.search.SearchScreen
import gr.aueb.thriveon.ui.screens.search.SearchViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object SearchRoute

fun NavController.navigateToSearchScreen(){
    navigate(SearchRoute)
}

fun NavGraphBuilder.searchScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToFeed: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToUserProfile: (String) -> Unit
) {
    composable<SearchRoute> {
        val viewModel: SearchViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        val effectFlow = remember(viewModel) { viewModel.uiEffect }

        SearchScreen(
            state = state,
            onEvent = viewModel::setEvent,
            effect = effectFlow,
            onNavigateToHome = onNavigateToHome,
            onNavigateToFeed = onNavigateToFeed,
            onNavigateToProfile = onNavigateToProfile,
            onNavigateToNotifications = onNavigateToNotifications,
            onNavigateToUserProfile = onNavigateToUserProfile
        )
    }
}
