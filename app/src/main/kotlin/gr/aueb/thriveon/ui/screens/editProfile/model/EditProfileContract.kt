package gr.aueb.thriveon.ui.screens.editProfile.model

import android.net.Uri
import gr.aueb.thriveon.ui.common.mvi.BaseEffect
import gr.aueb.thriveon.ui.common.mvi.BaseEvent
import gr.aueb.thriveon.ui.common.mvi.BaseState

interface EditProfileContract {

    sealed class Event : BaseEvent {
        data object Init : Event()
        data class OnUsernameChange(val value: String) : Event()
        data class OnBioChange(val value: String) : Event()
        data class OnEquippedTitleChange(val title: String) : Event()
        data class OnProfileImageSelected(val uri: Uri) : Event()
        data object OnSaveClick : Event()
        data object OnEditCategoriesClick : Event()
        data object OnBackClick : Event()
        data object OnLogoutClick : Event()

        sealed class LogoutAlertDialogEvent : Event() {
            data object Confirm : Event()
            data object Cancel : Event()
            data object Dismiss : Event()
        }
    }

    data class State(
        val username: String = "",
        val bio: String = "",
        val profilePictureUrl: String = "",
        val profileImageUri: Uri? = null,
        val equippedTitle: String = "",
        val titles: List<String> = emptyList(),
        val isLoading: Boolean = true,
        val error: String? = null,
        val isLogoutDialogAlertVisible: Boolean = false,
    ) : BaseState {
        val isUsernameValid: Boolean
            get() = username.length >= 4
    }

    sealed class Effect : BaseEffect {
        data object NavigateBack : Effect()
        data object NavigateToCategories : Effect()
        data object Logout : Effect()

        sealed class SnackbarEffect{
            data class ShowSnackbar(val message: String) : Effect()
            data object DismissSnackbar : Effect()
        }
    }
}
