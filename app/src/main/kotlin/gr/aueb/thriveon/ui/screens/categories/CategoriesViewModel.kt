package gr.aueb.thriveon.ui.screens.categories

import androidx.lifecycle.viewModelScope
import gr.aueb.thriveon.core.resources.ResourceProvider
import gr.aueb.thriveon.domain.interactors.UserInteractor
import gr.aueb.thriveon.ui.common.mvi.BaseViewModel
import gr.aueb.thriveon.ui.screens.categories.model.CategoriesContract
import gr.aueb.thriveon.ui.screens.categories.model.CategoriesContract.Effect.*
import gr.aueb.thriveon.R
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val userInteractor: UserInteractor,
    private val resourceProvider: ResourceProvider
) : BaseViewModel<CategoriesContract.Event, CategoriesContract.State, CategoriesContract.Effect>() {

    override fun setInitialState(): CategoriesContract.State =
        CategoriesContract.State()

    override fun handleEvents(event: CategoriesContract.Event) {
        when (event) {
            CategoriesContract.Event.Init -> {
                viewModelScope.launch {
                    try {
                        val userPreferences = userInteractor.getUserPreferences()
                        setState {
                            copy(
                                isUserPreferencesEmpty = userPreferences.isEmpty(),
                                selected = userPreferences,
                                isLoading = false
                            )
                        }
                    } catch (e: Exception) {
                        setState { copy(isLoading = false, error = e.message) }
                        showSnackbar(resourceProvider.getString(R.string.categories_vm_load_error))
                    }
                }
            }

            is CategoriesContract.Event.OnCategoryToggle -> {
                val updated = currentState.selected.toMutableList().apply {
                    if (contains(event.category)) remove(event.category)
                    else add(event.category)
                }
                setState { copy(selected = updated) }
            }

            CategoriesContract.Event.OnSaveClick -> {
                if (currentState.selected.size < 3) {
                    showSnackbar(resourceProvider.getString(R.string.categories_vm_selection_error))
                    return
                }
                savePreferences(currentState.selected)
            }

            CategoriesContract.Event.OnBackClick -> {
                setEffect { NavigateBack }
            }
        }
    }

    private fun dismissSnackbar() {
        setEffect { DismissSnackbar }
    }

    private fun showSnackbar(message: String) {
        dismissSnackbar()
        setEffect { ShowSnackbar(message) }
    }

    private fun savePreferences(preferences: List<String>) {
        viewModelScope.launch {
            try {
                userInteractor.saveUserPreferences(preferences)
                setEffect { NavigateToNext }
            } catch (_: Exception) {
                showSnackbar(resourceProvider.getString(R.string.categories_vm_save_error))
            }
        }
    }
}
