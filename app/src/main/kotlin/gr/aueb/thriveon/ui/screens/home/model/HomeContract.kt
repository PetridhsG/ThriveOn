package gr.aueb.thriveon.ui.screens.home.model

import android.net.Uri
import gr.aueb.thriveon.domain.model.PrivateTask
import gr.aueb.thriveon.domain.model.DailyTask
import gr.aueb.thriveon.domain.model.FirebaseTask
import gr.aueb.thriveon.ui.common.mvi.BaseEffect
import gr.aueb.thriveon.ui.common.mvi.BaseEvent
import gr.aueb.thriveon.ui.common.mvi.BaseState
import java.time.LocalDate

interface HomeContract {
    sealed class Event : BaseEvent {

        data object Init : Event()
        data object Refresh : Event()

        sealed class NavigationEvent() : Event() {
            data object OnFeedClick : Event()
            data object OnSearchClick : Event()
            data object OnProfileClick : Event()
            data object OnNotificationsClick : Event()
        }

        sealed class PrivateTaskEvent : Event() {
            data object OnAddPrivateTaskClick : Event()
            data class OnCompleteTaskClick(val taskId: Long) : Event()
        }

        sealed class AddPrivateTaskBottomSheetEvent : Event() {
            data object Dismiss : AddPrivateTaskBottomSheetEvent()
            data class TitleChange(val title: String) : AddPrivateTaskBottomSheetEvent()
            data class DateChange(val date: LocalDate?) : AddPrivateTaskBottomSheetEvent()
            data object Confirm : AddPrivateTaskBottomSheetEvent()
        }

        data class OnDailyTaskClick(val index: Int) : Event()

        sealed class DailyTaskBottomSheetEvent : Event() {
            data object Reroll : Event()
            data class Select(val index: Int) : Event()
            data object Confirm : Event()
            data object Cancel : Event()
        }

        sealed class CompleteTaskEvent : Event() {
            data class Start(val task: FirebaseTask, val slotIndex: Int) : Event()
            data class SetRating(val rating: Int) : CompleteTaskEvent()
            data object ChooseWithoutPhoto : Event()
            data object ChooseWithPhoto : Event()
            data object ConfirmCompletion : Event()
            data object Cancel : Event()
            data class SetCapturedPhoto(val uri: Uri) : Event()
            data class PhotoCaptured(val uri: Uri) : Event()
        }
    }

    data class State(
        val isRefreshing: Boolean = false,
        val isLoading: Boolean = false,
        val areSuggestionsLoading: Boolean = false,
        val isInitialized: Boolean = false,
        val username: String = "",
        val privateTasks: List<PrivateTask> = emptyList(),
        val isAddPrivateTaskBottomSheetVisible: Boolean = false,
        val newTaskTitle: String = "",
        val newTaskDueDate: LocalDate? = null,
        val suggestedTasks: List<FirebaseTask> = emptyList(),
        val selectedSuggestionIndex: Int? = null,
        val isDailyTaskBottomSheetVisible: Boolean = false,
        val rerollCount: Int = 0,
        val dailyTasks: List<DailyTask?> = List(3) { null },
        val selectedDailySlotIndex: Int? = null,
        val loadedTaskMap: Map<String, FirebaseTask> = emptyMap(),
        val isCompleteTaskBottomSheetVisible: Boolean = false,
        val taskToComplete: FirebaseTask? = null,
        val taskSlotIndex: Int? = null,
        val isPreviewVisible: Boolean = false,
        val isUsingCustomPhoto: Boolean = false,
        val photoUri: Uri? = null,
        val profilePictureUrl: String? = null,
        val selectedRating: Int = 3
    ) : BaseState {
        val isCreatePrivateTaskButtonEnabled: Boolean
            get() = newTaskTitle.isNotBlank() && newTaskDueDate != null
    }

    sealed class Effect : BaseEffect {
        sealed class Navigation : Effect() {
            data object NavigateToFeed : Effect()
            data object NavigateToSearch : Effect()
            data object NavigateToProfile : Effect()
            data object NavigateToNotifications: Effect()
        }

        sealed class AddPrivateTaskBottomSheetEffect() : Effect() {
            data object Dismiss : Effect()
        }

        sealed class DailyTaskBottomSheetEffect() : Effect() {
            data object Dismiss : Effect()
        }

        sealed class CompleteTaskBottomSheetEffect() : Effect() {
            data object Dismiss : Effect()
        }

        sealed class SnackBarEffect : Effect() {
            data class ShowSnackbar(val message: String) : Effect()
            data object DismissSnackbar : Effect()
        }

        data object LaunchCamera : Effect()
    }
}
