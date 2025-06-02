package gr.aueb.thriveon.ui.screens.signIn.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import gr.aueb.thriveon.ui.screens.signIn.SignInScreen
import gr.aueb.thriveon.ui.screens.signIn.SignInViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object SignInRoute

fun NavController.navigateToSignInScreen() {
    navigate(SignInRoute)
}

fun NavGraphBuilder.signInScreen(
    onNavigateToSignCategoriesUp: () -> Unit,
    onNavigateToHome: (String) -> Unit,
) {
    composable<SignInRoute> {
        val viewModel: SignInViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        val effectFlow = remember(viewModel) { viewModel.uiEffect }

        SignInScreen(
            state = state,
            onEvent = viewModel::setEvent,
            effect = effectFlow,
            onNavigateToSignUpCategories = onNavigateToSignCategoriesUp,
            onNavigateToHome = onNavigateToHome
        )
    }
}
