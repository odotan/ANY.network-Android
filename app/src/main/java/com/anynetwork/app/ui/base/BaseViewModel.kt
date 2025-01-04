package com.anynetwork.app.ui.base

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

abstract class BaseViewModel<VS: ViewState, VE: ViewEvent>: ViewModel() {
    abstract val viewState: MutableState<VS>

    protected val _navigationEventFlow: MutableSharedFlow<NavigationEvent> = MutableSharedFlow(replay = 0)
    val navigationEventFlow: SharedFlow<NavigationEvent> = _navigationEventFlow

    protected val _actionsFlow: MutableSharedFlow<Action> = MutableSharedFlow(replay = 0)
    val actionsFlow: SharedFlow<Action> = _actionsFlow

    open fun onEvent(event: VE) {}
}