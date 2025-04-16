package com.anynetwork.app.ui.screens.connectsuccess

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anynetwork.app.domain.FetchFacebookProfileNameUseCase
import com.anynetwork.app.ui.base.ViewEffect
import com.anynetwork.app.ui.base.ViewEvent
import com.anynetwork.app.ui.screens.connect.ConnectScreenMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectSuccessScreenViewModel @Inject constructor(): ViewModel() {
    private val _viewEffectFlow: MutableStateFlow<ConnectSuccessScreenViewEffect?> = MutableStateFlow(null)
    val viewEffectFlow: StateFlow<ConnectSuccessScreenViewEffect?> = _viewEffectFlow

    private val _viewState = MutableStateFlow(ConnectSuccessViewState())
    val viewState: StateFlow<ConnectSuccessViewState> = _viewState

    fun load(mode: ConnectScreenMode) = when (mode) {
        is ConnectScreenMode.Email -> viewModelScope.launch {
            _viewState.value = viewState.value.copy(mode = mode)
        }
        is ConnectScreenMode.Phone -> viewModelScope.launch {
            _viewState.value = viewState.value.copy(mode = mode)
        }
        is ConnectScreenMode.Telegram -> viewModelScope.launch {
            _viewState.value = viewState.value.copy(mode = mode)
        }
        is ConnectScreenMode.Facebook -> viewModelScope.launch {
            _viewState.value = viewState.value.copy(mode = mode)
        }
    }

    fun onViewEvent(viewEvent: ViewEvent) = when (viewEvent) {
        ConnectSuccessScreenViewEvent.ToggleIsInformationAvailableOnMyProfile ->
            _viewState.value = viewState.value.copy(
                isInformationAvailableOnMyProfile = !viewState.value.isInformationAvailableOnMyProfile
            )
        ConnectSuccessScreenViewEvent.ToggleIsInformationPubliclySearchable ->
            _viewState.value = viewState.value.copy(
                isInformationPubliclySearchable = !viewState.value.isInformationPubliclySearchable
            )

        ConnectSuccessScreenViewEvent.BackButtonClick ->
            _viewEffectFlow.value = ConnectSuccessScreenViewEffect.NavigateBack
        else -> {}
    }

}

data class ConnectSuccessViewState(
    val mode: ConnectScreenMode? = null,
    val isInformationAvailableOnMyProfile: Boolean = true,
    val isInformationPubliclySearchable: Boolean = false
)

sealed class ConnectSuccessScreenViewEvent: ViewEvent() {
    data object ToggleIsInformationAvailableOnMyProfile: ConnectSuccessScreenViewEvent()
    data object ToggleIsInformationPubliclySearchable: ConnectSuccessScreenViewEvent()
    data object BackButtonClick: ConnectSuccessScreenViewEvent()
}

sealed class ConnectSuccessScreenViewEffect: ViewEffect() {
    data object NavigateBack:ConnectSuccessScreenViewEffect()
}