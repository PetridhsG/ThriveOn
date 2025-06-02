package gr.aueb.thriveon.ui.screens.editProfile.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import gr.aueb.thriveon.ui.screens.editProfile.EditProfileScreen
import gr.aueb.thriveon.ui.screens.editProfile.EditProfileViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object EditProfileRoute

fun NavController.navigateToEditProfileScreen() {
    navigate(EditProfileRoute)
}

fun NavGraphBuilder.editProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onLogout: () -> Unit
) {
    composable<EditProfileRoute> {
        val viewModel: EditProfileViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        val effectFlow = remember(viewModel) { viewModel.uiEffect }

        EditProfileScreen(
            state = state,
            onEvent = viewModel::setEvent,
            effect = effectFlow,
            onNavigateBack = onNavigateBack,
            onNavigateToCategories = onNavigateToCategories,
            onLogout = onLogout
        )
    }
}
