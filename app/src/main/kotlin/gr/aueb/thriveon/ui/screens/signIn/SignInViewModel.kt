package gr.aueb.thriveon.ui.screens.signIn

import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import gr.aueb.thriveon.R
import gr.aueb.thriveon.core.resources.ResourceProvider
import gr.aueb.thriveon.domain.interactors.AuthInteractor
import gr.aueb.thriveon.domain.interactors.UserInteractor
import gr.aueb.thriveon.ui.common.mvi.BaseViewModel
import gr.aueb.thriveon.ui.screens.signIn.model.SignInContract
import gr.aueb.thriveon.ui.screens.signIn.model.SignInContract.Effect.*
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class SignInViewModel(
    private val authInteractor: AuthInteractor,
    private val userInteractor: UserInteractor,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<SignInContract.Event, SignInContract.State, SignInContract.Effect>() {

    override fun setInitialState(): SignInContract.State = SignInContract.State()

    override fun handleEvents(event: SignInContract.Event) {
        when (event) {
            SignInContract.Event.Init -> {
                val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
                if (isLoggedIn) {
                    viewModelScope.launch {
                        val username = userInteractor.getUsername()
                        setState {
                            copy(
                                isSignedIn = true,
                                username = username,
                                isLoading = false
                            )
                        }
                    }
                } else {
                    setState {
                        copy(
                            isSignedIn = false,
                            username = "",
                            isLoading = false
                        )
                    }
                }
            }

            SignInContract.Event.OnGoogleAccountRequest -> {
                setEffect { LaunchGoogleAccountPicker }
            }

            is SignInContract.Event.OnGoogleSignInSuccess -> {
                signInWithoutNavigation(event.account)
            }

            is SignInContract.Event.OnGoogleSignInFailed -> {
                setState { copy(error = event.message) }
            }

            SignInContract.Event.OnLoginClick -> {
                val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
                if (!isLoggedIn) {
                    showSnackbar(resourceProvider.getString(R.string.sign_in_vm_sign_in_first))
                } else {
                    viewModelScope.launch {
                        val prefs = userInteractor.getUserPreferences()
                        if (prefs.isEmpty()) {
                            setEffect { NavigateToSignUpCategories }
                        } else {
                            setEffect { NavigateToHome }
                        }
                    }
                }
            }

            SignInContract.Event.OnSignOutClick -> {
                viewModelScope.launch {
                    authInteractor.signOut()
                }
                setState { copy(isSignedIn = false, username = "") }
                showSnackbar(resourceProvider.getString(R.string.sign_in_vm_signed_out))
            }
        }
    }

    private fun dismissSnackbar() {
        setEffect {
            DismissSnackbar
        }
    }

    private fun showSnackbar(message: String) {
        dismissSnackbar()
        setEffect {
            ShowSnackbar(message)
        }
    }

    private fun signInWithoutNavigation(account: GoogleSignInAccount) {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            authInteractor.signInWithGoogle(account.idToken ?: "")
            val username = userInteractor.getUsername()
            setState {
                copy(isLoading = false, isSignedIn = true, username = username)
            }
            showSnackbar(resourceProvider.getString(R.string.sign_in_vm_signed_in_success))
        }
    }
}
