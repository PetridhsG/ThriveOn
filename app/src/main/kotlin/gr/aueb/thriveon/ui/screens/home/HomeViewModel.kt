package gr.aueb.thriveon.ui.screens.home

import androidx.lifecycle.viewModelScope
import gr.aueb.thriveon.R
import gr.aueb.thriveon.core.resources.ResourceProvider
import gr.aueb.thriveon.domain.interactors.DailyTaskInteractor
import gr.aueb.thriveon.domain.interactors.PrivateTaskInteractor
import gr.aueb.thriveon.domain.interactors.UserInteractor
import gr.aueb.thriveon.domain.model.DailyTask
import gr.aueb.thriveon.domain.model.PrivateTask
import gr.aueb.thriveon.ui.common.mvi.BaseViewModel
import gr.aueb.thriveon.ui.screens.home.model.HomeContract
import kotlinx.coroutines.launch
import java.time.LocalTime

class HomeViewModel(
    private val privateTaskInteractor: PrivateTaskInteractor,
    private val dailyTaskInteractor: DailyTaskInteractor,
    private val userInteractor: UserInteractor,
    private val resourceProvider: ResourceProvider,
) :
    BaseViewModel<HomeContract.Event, HomeContract.State, HomeContract.Effect>() {

    override fun setInitialState(): HomeContract.State = HomeContract.State()

    override fun handleEvents(event: HomeContract.Event) {
        when (event) {
            HomeContract.Event.Init -> { viewModelScope.launch {
                    val rerollCount = dailyTaskInteractor.getRerollCount()
                    val username = userInteractor.getUsername()
                    privateTaskInteractor.deleteExpiredTasks()
                    setState {
                        copy(
                            username = username,
                            rerollCount = rerollCount,
                            isInitialized = true
                        )
                    }
                    loadTasks()
                }
            }

            HomeContract.Event.Refresh -> {
                setState { copy(isRefreshing = true) }

                viewModelScope.launch {
                    loadTasks()
                    setState { copy(isRefreshing = false) }
                }
            }

            HomeContract.Event.NavigationEvent.OnFeedClick -> {
                setEffect {
                    HomeContract.Effect.Navigation.NavigateToFeed
                }
            }

            HomeContract.Event.NavigationEvent.OnSearchClick -> {
                setEffect {
                    HomeContract.Effect.Navigation.NavigateToSearch
                }
            }

            HomeContract.Event.NavigationEvent.OnNotificationsClick -> {
                setEffect {
                    HomeContract.Effect.Navigation.NavigateToNotifications
                }
            }

            HomeContract.Event.NavigationEvent.OnProfileClick -> {
                setEffect {
                    HomeContract.Effect.Navigation.NavigateToProfile
                }
            }

            HomeContract.Event.PrivateTaskEvent.OnAddPrivateTaskClick -> {
                showAddPrivateTaskBottomSheet()
            }

            is HomeContract.Event.PrivateTaskEvent.OnCompleteTaskClick -> {
                viewModelScope.launch {
                    privateTaskInteractor.deletePrivateTask(event.taskId)
                }
                val message = resourceProvider.getString(R.string.home_vm_private_task_completed)
                showSnackbar(message)
            }

            HomeContract.Event.AddPrivateTaskBottomSheetEvent.Confirm -> {
                val title = currentState.newTaskTitle.trim()
                val date = currentState.newTaskDueDate

                viewModelScope.launch {
                    privateTaskInteractor.addPrivateTask(
                        PrivateTask().apply {
                            taskTitle = title
                            dueOn = date?.toString()
                        }
                    )
                }

                dismissAddPrivateTaskBottomSheet()
                val message = resourceProvider.getString(R.string.home_vm_private_task_added)
                showSnackbar(message)
            }

            HomeContract.Event.AddPrivateTaskBottomSheetEvent.Dismiss -> {
                dismissAddPrivateTaskBottomSheet()
            }

            is HomeContract.Event.AddPrivateTaskBottomSheetEvent.DateChange -> {
                setState { copy(newTaskDueDate = event.date) }
            }

            is HomeContract.Event.AddPrivateTaskBottomSheetEvent.TitleChange -> {
                setState { copy(newTaskTitle = event.title) }
            }

            is HomeContract.Event.OnDailyTaskClick -> {
                setState{ copy(areSuggestionsLoading = true) }
                val currentSuggestions = currentState.suggestedTasks

                setState {
                    copy(selectedDailySlotIndex = event.index)
                }

                if (currentSuggestions.isEmpty()) {
                    viewModelScope.launch {
                        val rerollCount = dailyTaskInteractor.getRerollCount()
                        setState { copy(rerollCount = rerollCount) }

                        loadSuggestions()
                    }
                } else {
                    setState {
                        copy(isDailyTaskBottomSheetVisible = true)
                    }
                    setState{ copy(areSuggestionsLoading = false) }
                }
            }

            is HomeContract.Event.DailyTaskBottomSheetEvent.Select -> {
                setState { copy(selectedSuggestionIndex = event.index) }
            }

            is HomeContract.Event.DailyTaskBottomSheetEvent.Reroll -> {
                val currentRerolls = currentState.rerollCount
                val slotIndex = currentState.selectedDailySlotIndex

                if (currentRerolls > 0 && slotIndex != null) {
                    viewModelScope.launch {
                        setState { copy(areSuggestionsLoading = true) }

                        setState{
                            copy(rerollCount = currentRerolls - 1)
                        }
                        dailyTaskInteractor.decrementRerollCount()

                        val preferences = userInteractor.getUserPreferences()
                        val newSuggestions = dailyTaskInteractor.fetchThreeTasks(preferences)

                        val suggestionsMap = newSuggestions.mapIndexed { index, task ->
                            index to task.id
                        }.toMap()
                        dailyTaskInteractor.saveTodaySuggestions(suggestionsMap)

                        setState {
                            copy(
                                suggestedTasks = newSuggestions,
                                selectedSuggestionIndex = null,
                                areSuggestionsLoading = false
                            )
                        }
                    }
                }
            }

            is HomeContract.Event.DailyTaskBottomSheetEvent.Confirm -> {
                val selectedIndex = currentState.selectedSuggestionIndex
                if (selectedIndex == null) return

                val task = currentState.suggestedTasks.getOrNull(selectedIndex) ?: return

                val targetSlot = currentState.dailyTasks.indexOfFirst { it == null }

                if (targetSlot != -1) {
                    viewModelScope.launch {
                        dailyTaskInteractor.writeTaskToFirebase(task.id)

                        dailyTaskInteractor.saveTodaySuggestions(emptyMap())

                        setState {
                            copy(
                                isDailyTaskBottomSheetVisible = false,
                                selectedSuggestionIndex = null,
                                suggestedTasks = emptyList()
                            )
                        }

                        val message = resourceProvider.getString(R.string.home_vm_task_added)
                        showSnackbar(message)
                    }
                }
            }

            is HomeContract.Event.DailyTaskBottomSheetEvent.Cancel -> {
                dismissDailyTaskBottomSheet()
            }

            is HomeContract.Event.CompleteTaskEvent.Start -> {
                showCompleteTaskSheet()
                setState {
                    copy(
                        taskToComplete = event.task,
                        taskSlotIndex = event.slotIndex,
                        isPreviewVisible = false,
                        isUsingCustomPhoto = false,
                        photoUri = null
                    )
                }
            }

            HomeContract.Event.CompleteTaskEvent.ChooseWithoutPhoto -> {
                setState { copy(isPreviewVisible = true, isUsingCustomPhoto = false) }
            }

            HomeContract.Event.CompleteTaskEvent.ChooseWithPhoto -> {
                setState { copy(isUsingCustomPhoto = true) }
                setEffect {
                    HomeContract.Effect.LaunchCamera
                }
            }

            is HomeContract.Event.CompleteTaskEvent.SetCapturedPhoto -> {
                setState { copy(photoUri = event.uri, isPreviewVisible = true) }
            }

            is HomeContract.Event.CompleteTaskEvent.ConfirmCompletion -> {
                viewModelScope.launch {
                    val isUsingCustomPhoto = currentState.isUsingCustomPhoto
                    val photoUri = currentState.photoUri
                    val task = currentState.taskToComplete
                    val slotIndex = currentState.taskSlotIndex

                    dismissCompleteTaskSheet()
                    setState { copy(isLoading = false) }

                    if (task == null || slotIndex == null) {
                        showSnackbar(resourceProvider.getString(R.string.home_vm_missing_task_info))
                        return@launch
                    }

                    try {
                        val imageUrl = if (isUsingCustomPhoto) {
                            if (photoUri == null) {
                                showSnackbar(resourceProvider.getString(R.string.home_vm_no_photo_found))
                                return@launch
                            }
                            dailyTaskInteractor.uploadPhotoToFirebase(photoUri)
                        } else {
                            task.defaultPicture
                        }

                        if (imageUrl.isBlank()) {
                            showSnackbar(resourceProvider.getString(R.string.home_vm_no_image_available))
                            return@launch
                        }

                        val username = userInteractor.getUsername()
                        val profileUrl = userInteractor.getUserProfilePictureUrl() ?: ""

                        dailyTaskInteractor.createPost(
                            taskId = task.id,
                            taskTitle = task.title,
                            taskCategory = task.categoryTitle,
                            imageUrl = imageUrl,
                            userProfilePictureUrl = profileUrl,
                            username = username
                        )

                        dailyTaskInteractor.markTaskAsCompleted(slotIndex, rating = currentState.selectedRating)
                        dailyTaskInteractor.updateStreakAfterCompletion()

                        if (isUsingCustomPhoto) {
                            dailyTaskInteractor.incrementRerollCount()
                        }

                        val successMsg = resourceProvider.getString(R.string.home_vm_task_completed)
                        showSnackbar(successMsg)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        val errorMsg =
                            resourceProvider.getString(R.string.home_vm_failed_task_completion)
                        showSnackbar(errorMsg)
                    }
                }
            }

            is HomeContract.Event.CompleteTaskEvent.Cancel -> {
                dismissCompleteTaskSheet()
            }

            is HomeContract.Event.CompleteTaskEvent.PhotoCaptured -> {
                setState {
                    copy(
                        isPreviewVisible = true,
                        isUsingCustomPhoto = true,
                        photoUri = event.uri
                    )
                }
            }

            is HomeContract.Event.CompleteTaskEvent.SetRating -> {
                setState { copy(selectedRating = event.rating) }
            }

        }
    }

    private fun dismissAddPrivateTaskBottomSheet() {
        setState {
            copy(
                isAddPrivateTaskBottomSheetVisible = false,
                newTaskTitle = "",
                newTaskDueDate = null
            )
        }
        setEffect {
            HomeContract.Effect.AddPrivateTaskBottomSheetEffect.Dismiss
        }
    }

    private fun showAddPrivateTaskBottomSheet() {
        dismissSnackbar()
        setState {
            copy(isAddPrivateTaskBottomSheetVisible = true)
        }
    }

    private fun dismissDailyTaskBottomSheet() {
        setState {
            copy(
                isDailyTaskBottomSheetVisible = false,
                selectedSuggestionIndex = null
            )
        }
        setEffect {
            HomeContract.Effect.DailyTaskBottomSheetEffect.Dismiss
        }
    }

    private fun dismissSnackbar() {
        setEffect {
            HomeContract.Effect.SnackBarEffect.DismissSnackbar
        }
    }

    private fun showSnackbar(message: String) {
        dismissSnackbar()
        setEffect {
            HomeContract.Effect.SnackBarEffect.ShowSnackbar(message)
        }
    }

    private fun dismissCompleteTaskSheet() {
        setState {
            copy(
                isCompleteTaskBottomSheetVisible = false,
                selectedRating = 3,
                taskToComplete = null,
                taskSlotIndex = null,
                isPreviewVisible = false,
                isUsingCustomPhoto = false,
                photoUri = null
            )
        }
        setEffect {
            HomeContract.Effect.CompleteTaskBottomSheetEffect.Dismiss
        }
    }

    private fun showCompleteTaskSheet() {
        setState {
            copy(
                isCompleteTaskBottomSheetVisible = true,
            )
        }
    }

    private fun loadSuggestions() {
        viewModelScope.launch {
            setState {
                copy(
                    isDailyTaskBottomSheetVisible = true,
                    areSuggestionsLoading = true
                )
            }

            val savedSuggestions = dailyTaskInteractor.getTodaySuggestions()

            val suggestionIds = savedSuggestions.values.toList()

            val tasks = if (suggestionIds.isNotEmpty()) {
                dailyTaskInteractor.getTasksByIds(suggestionIds)
                    .filterKeys { it in suggestionIds }
                    .values
                    .toList()
            } else {
                val preferences = userInteractor.getUserPreferences()
                val newSuggestions = dailyTaskInteractor.fetchThreeTasks(preferences)

                val mapToSave = newSuggestions.mapIndexed { index, task ->
                    index to task.id
                }.toMap()

                dailyTaskInteractor.saveTodaySuggestions(mapToSave)

                newSuggestions
            }

            setState {
                copy(
                    suggestedTasks = tasks,
                    areSuggestionsLoading = false
                )
            }
        }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            setState { copy(isLoading = false) }

            val now = LocalTime.now()
            if (now.hour == 0 && now.minute == 0) {
                privateTaskInteractor.deleteExpiredTasks()
            }

            privateTaskInteractor.deleteExpiredTasks()
            val rerollCount = dailyTaskInteractor.getRerollCount()
            val profileUrl = userInteractor.getUserProfilePictureUrl()

            setState {
                copy(
                    rerollCount = rerollCount,
                    profilePictureUrl = profileUrl
                )
            }

            observePrivateTasks()
            observeTodayTasks()
        }
    }

    private fun observePrivateTasks() {
        viewModelScope.launch {
            privateTaskInteractor.getPrivateTasks().collect { tasks ->
                setState { copy(privateTasks = tasks) }
            }
        }
    }

    private fun observeTodayTasks() {
        viewModelScope.launch {
            dailyTaskInteractor.getTodayDailyTasks().collect { dailyTasks ->
                val paddedTasks: MutableList<DailyTask?> = dailyTasks.toMutableList()
                while (paddedTasks.size < 3) paddedTasks.add(null)

                val taskIds = paddedTasks.filterNotNull().map { it.taskId }
                val taskMap = dailyTaskInteractor.getTasksByIds(taskIds)

                setState {
                    copy(
                        dailyTasks = paddedTasks,
                        loadedTaskMap = taskMap,
                        isLoading = true
                    )
                }
            }
        }
    }
}
