package gr.aueb.thriveon.ui.common.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

abstract class BaseViewModel<Event : BaseEvent, State : BaseState, Effect : BaseEffect> :
    ViewModel(), KoinComponent {

    private val initialState: State by lazy { setInitialState() }

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()

    private val _state: MutableStateFlow<State> = MutableStateFlow(initialState)

    private val _effect: Channel<Effect> = Channel()

    /**
     * Used by the views to acquire the current state as read-only.
     */
    val uiState: StateFlow<State> =
        _state.asStateFlow()

    /**
     * Used by the ViewModels to acquire the current state.
     */
    protected val currentState: State
        get() = _state.value

    /**
     * Used by the views to acquire the current effect.
     */
    val uiEffect: Flow<Effect> = _effect.receiveAsFlow()

    init {
        subscribeToEvents()
    }

    /**
     * Sets an effect.
     */
    fun setEffect(builder: () -> Effect) {
        val effectValue = builder()
        viewModelScope.launch {
            _effect.send(effectValue)
        }
    }

    /**
     * Sets the initial state value.
     */
    protected abstract fun setInitialState(): State

    protected fun setState(reducer: State.() -> State) {
        val newState = uiState.value.reducer()
        _state.value = newState
    }

    fun setEvent(event: Event) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    private fun subscribeToEvents() {
        viewModelScope.launch {
            _event.collect {
                handleEvents(it)
            }
        }
    }

    protected abstract fun handleEvents(event: Event)
}
