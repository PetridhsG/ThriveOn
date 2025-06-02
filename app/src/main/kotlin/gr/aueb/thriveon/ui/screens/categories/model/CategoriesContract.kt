package gr.aueb.thriveon.ui.screens.categories.model

import gr.aueb.thriveon.ui.common.mvi.BaseEffect
import gr.aueb.thriveon.ui.common.mvi.BaseEvent
import gr.aueb.thriveon.ui.common.mvi.BaseState

interface CategoriesContract {

    sealed class Event: BaseEvent {
        data object Init : Event()
        data class OnCategoryToggle(val category: String) : Event()
        data object OnSaveClick : Event()
        data object OnBackClick : Event()
    }

    data class State(
        val isUserPreferencesEmpty: Boolean = false,
        val selected: List<String> = emptyList(),
        val isLoading: Boolean = true,
        val error: String? = null,
        val categories: List<String> = listOf(
            "Learning & Growth",
            "Physical Exercise & Health",
            "Adventures & Discoveries",
            "Cooking & Nutrition",
            "Volunteering & Good Deeds",
            "Expression & Creativity",
            "Cleaning & Space Organization",
            "Sustainability & Environment",
            "Focus & Work Efficiency",
        )
    ) : BaseState {
        val isBackButtonEnabled: Boolean
            get() = selected.size >= 3
    }

    sealed class Effect : BaseEffect {
        data object NavigateToNext : Effect()
        data object NavigateBack : Effect()
        data class ShowSnackbar(val message: String) : Effect()
        data object DismissSnackbar : Effect()
    }
}
