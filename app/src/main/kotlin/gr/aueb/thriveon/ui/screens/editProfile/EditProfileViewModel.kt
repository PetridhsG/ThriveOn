package gr.aueb.thriveon.ui.screens.editProfile

import androidx.lifecycle.viewModelScope
import gr.aueb.thriveon.domain.interactors.UserInteractor
import gr.aueb.thriveon.ui.common.mvi.BaseViewModel
import gr.aueb.thriveon.ui.screens.editProfile.model.EditProfileContract
import kotlinx.coroutines.launch
import gr.aueb.thriveon.R
import gr.aueb.thriveon.core.resources.ResourceProvider

class EditProfileViewModel(
    private val userInteractor: UserInteractor,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<EditProfileContract.Event, EditProfileContract.State, EditProfileContract.Effect>() {

    override fun setInitialState(): EditProfileContract.State = EditProfileContract.State()

    override fun handleEvents(event: EditProfileContract.Event) {
        when (event) {
            EditProfileContract.Event.Init -> {
                loadUserData()
            }

            is EditProfileContract.Event.OnUsernameChange -> {
                setState { copy(username = event.value) }
            }

            is EditProfileContract.Event.OnBioChange -> {
                setState { copy(bio = event.value) }
            }

            is EditProfileContract.Event.OnEquippedTitleChange -> {
                setState { copy(equippedTitle = event.title) }
            }

            is EditProfileContract.Event.OnProfileImageSelected -> {
                setState { copy(profileImageUri = event.uri) }
            }

            EditProfileContract.Event.OnSaveClick -> {
                saveChanges()
            }

            EditProfileContract.Event.OnEditCategoriesClick -> {
                setEffect { EditProfileContract.Effect.NavigateToCategories }
            }

            EditProfileContract.Event.OnBackClick -> {
                setEffect { EditProfileContract.Effect.NavigateBack }
            }

            EditProfileContract.Event.OnLogoutClick -> {
                setState {
                    copy(isLogoutDialogAlertVisible = true)
                }
            }

            EditProfileContract.Event.LogoutAlertDialogEvent.Cancel -> {
                dismissLogoutAlertDialog()
            }

            EditProfileContract.Event.LogoutAlertDialogEvent.Confirm -> {
                dismissLogoutAlertDialog()
                setEffect { EditProfileContract.Effect.Logout }
            }

            EditProfileContract.Event.LogoutAlertDialogEvent.Dismiss -> {
                dismissLogoutAlertDialog()
            }
        }
    }

    private fun dismissLogoutAlertDialog() {
        setState {
            copy(isLogoutDialogAlertVisible = false)
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            try {
                val profile = userInteractor.getUserEditableProfile()
                setState {
                    copy(
                        username = profile.username,
                        bio = profile.bio,
                        profilePictureUrl = profile.profilePictureUrl,
                        equippedTitle = profile.equippedTitle ?: "",
                        titles = profile.titles,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                setState { copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun saveChanges() {
        viewModelScope.launch {
            try {
                setState { copy(isLoading = true) }
                val uri = currentState.profileImageUri
                var finalProfilePicUrl = currentState.profilePictureUrl

                if (uri != null) {
                    finalProfilePicUrl = userInteractor.uploadProfilePicture(uri)
                    setState { copy(profilePictureUrl = finalProfilePicUrl) }
                }

                userInteractor.updateUserProfile(
                    username = currentState.username,
                    bio = currentState.bio,
                    equippedTitle = currentState.equippedTitle.ifBlank { null }
                )
                setState { copy(isLoading = false) }
                showSnackbar(resourceProvider.getString(R.string.toast_profile_updated))
            } catch (e: Exception) {
                setEffect {
                    EditProfileContract.Effect.SnackbarEffect.ShowSnackbar(
                        resourceProvider.getString(
                            R.string.toast_profile_update_failed,
                            e.message ?: "unknown error"
                        )
                    )
                }
            }
        }
    }

    private fun showSnackbar(message: String) {
        setEffect { EditProfileContract.Effect.SnackbarEffect.DismissSnackbar }
        setEffect { EditProfileContract.Effect.SnackbarEffect.ShowSnackbar(message) }
    }
}
