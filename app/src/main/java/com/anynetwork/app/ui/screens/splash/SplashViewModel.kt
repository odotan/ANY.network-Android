package com.anynetwork.app.ui.screens.splash

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.anynetwork.app.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(): BaseViewModel<SplashViewState, SplashViewEvent>() {
    override val viewState: MutableState<SplashViewState> = mutableStateOf(
        SplashViewState()
    )

    override fun onEvent(event: SplashViewEvent) {
        super.onEvent(event)
    }
}

