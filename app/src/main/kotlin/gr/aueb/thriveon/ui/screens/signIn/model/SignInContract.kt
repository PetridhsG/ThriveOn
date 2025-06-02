package gr.aueb.thriveon.ui.screens.signIn.model

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import gr.aueb.thriveon.ui.common.mvi.BaseEffect
import gr.aueb.thriveon.ui.common.mvi.BaseEvent
import gr.aueb.thriveon.ui.common.mvi.BaseState

interface SignInContract {

    sealed class Event : BaseEvent {
        data object Init : Event()
        data object OnGoogleAccountRequest : Event()
        data class OnGoogleSignInSuccess(val account: GoogleSignInAccount) : Event()
        data class OnGoogleSignInFailed(val message: String) : Event()
        data object OnLoginClick : Event()
        data object OnSignOutClick : Event()
    }

    data class State(
        val isLoading: Boolean = true,
        val error: String = "",
        val isSignedIn: Boolean = false,
        val username: String = ""
    ) : BaseState

    sealed class Effect : BaseEffect {
        data object LaunchGoogleAccountPicker : Effect()
        data object NavigateToSignUpCategories : Effect()
        data object NavigateToHome : Effect()
        data class ShowSnackbar(val message: String) : Effect()
        data object DismissSnackbar : Effect()
    }
}
