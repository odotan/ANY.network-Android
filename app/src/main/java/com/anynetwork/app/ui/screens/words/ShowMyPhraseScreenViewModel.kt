package com.anynetwork.app.ui.screens.words

import androidx.lifecycle.ViewModel
import com.anynetwork.app.ui.base.ViewEffect
import com.anynetwork.app.ui.base.ViewEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ShowMyPhraseScreenViewModel @Inject constructor(): ViewModel() {
    private val _viewState = MutableStateFlow(ShowMyPhraseScreenViewState(
        words = (0..12).map { it.toString() }
    ))
    val viewState: StateFlow<ShowMyPhraseScreenViewState> = _viewState

    private val _viewEffectFlow: MutableStateFlow<ShowMyPhraseScreenViewEffect?> = MutableStateFlow(null)
    val viewEffectFlow: StateFlow<ShowMyPhraseScreenViewEffect?> = _viewEffectFlow

    fun onViewEvent(viewEvent: ShowMyPhraseScreenViewEvent) = when (viewEvent) {
        ShowMyPhraseScreenViewEvent.BackButtonClick -> _viewEffectFlow.value = ShowMyPhraseScreenViewEffect.NavigateBack
    }
}

data class ShowMyPhraseScreenViewState(val words: List<String> = listOf())

sealed class ShowMyPhraseScreenViewEvent: ViewEvent() {
    data object BackButtonClick: ShowMyPhraseScreenViewEvent()
}
sealed class ShowMyPhraseScreenViewEffect: ViewEffect() {
    data object NavigateBack: ShowMyPhraseScreenViewEffect()
}