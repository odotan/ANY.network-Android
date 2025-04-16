package com.anynetwork.app.ui.screens.connect

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anynetwork.app.R
import com.anynetwork.app.data.networkauth.EmailNetworkAuthentication.EmailError
import com.anynetwork.app.data.networkauth.PhoneNumberNetworkAuthenticationEvent
import com.anynetwork.app.data.profile.ProfileRepository
import com.anynetwork.app.domain.SendSignInLinkUseCase
import com.anynetwork.app.domain.SendTelegramCodeToPhone
import com.anynetwork.app.domain.SendVerificationSmsCodeUseCase
import com.anynetwork.app.domain.VerifyEmailSignInUseCase
import com.anynetwork.app.domain.VerifyFacebookUseCase
import com.anynetwork.app.domain.VerifyPhoneUseCase
import com.anynetwork.app.domain.VerifyTelegramCodeUseCase
import com.anynetwork.app.ui.base.ViewEffect
import com.anynetwork.app.ui.base.ViewEvent
import com.anynetwork.app.ui.components.hexagon.NontransparentHexagonContentStyle
import com.anynetwork.app.ui.theme.EmailColor
import com.anynetwork.app.ui.theme.FacebookColor
import com.anynetwork.app.ui.theme.PhoneColor
import com.anynetwork.app.ui.theme.TelegramColor
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ConnectViewModel @Inject constructor(
    val profileRepository: ProfileRepository,
    val sendSignInLinkUseCase: SendSignInLinkUseCase,
    val verifyEmailSignInUseCase: VerifyEmailSignInUseCase,
    val sendVerificationSmsCodeUseCase: SendVerificationSmsCodeUseCase,
    val verifyPhoneUseCase: VerifyPhoneUseCase,
    val sendTelegramNetworkAuthentication: SendTelegramCodeToPhone,
    val verifyTelegramCodeUseCase: VerifyTelegramCodeUseCase,
    val verifyFacebookUseCase: VerifyFacebookUseCase
): ViewModel() {

    private val _viewState = MutableStateFlow(ConnectScreenViewState())
    val viewState: StateFlow<ConnectScreenViewState> = _viewState

    private val _viewEffectFlow: MutableStateFlow<ConnectScreenViewEffect?> = MutableStateFlow(null)
    val viewEffectFlow: StateFlow<ConnectScreenViewEffect?> = _viewEffectFlow

    fun onViewEvent(viewEvent: ConnectScreenViewEvent) {
        when (viewEvent) {
            is ConnectScreenViewEvent.UpdateTextField -> {
                _viewState.value = viewState.value.copy(
                    firstTextFieldState = ConnectScreenViewState.TextFieldState(value = viewEvent.value),
                    isConnectButtonEnabled = viewEvent.value.isNotEmpty()
                )
            }
            is ConnectScreenViewEvent.ClearViewEffect -> {
                _viewEffectFlow.value = null
            }
            is ConnectScreenViewEvent.BackButtonClick -> {
                _viewEffectFlow.value = ConnectScreenViewEffect.NavigateBack
            }
            is ConnectScreenViewEvent.ConnectButtonClick -> viewModelScope.launch {
                when (val mode = viewState.value.mode) {
                    is ConnectScreenMode.Email -> {
                        try {
                            val email = viewState.value.firstTextFieldState!!.value
                            sendSignInLinkUseCase.execute(email)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    is ConnectScreenMode.Phone -> when {
                        mode.verificationId == null && mode.token == null -> sendVerificationSmsCodeUseCase.execute(
                            phoneNumber = viewState.value.firstTextFieldState!!.value,
                            activity = viewEvent.activity
                        ).collectLatest { event: PhoneNumberNetworkAuthenticationEvent ->
                            when (event) {
                                is PhoneNumberNetworkAuthenticationEvent.CodeSend ->
                                    _viewState.value = viewState.value
                                        .copy(
                                            secondTextFieldState = ConnectScreenViewState.TextFieldState(
                                                placeholder = "Enter code",
                                                value = ""
                                            ),
                                            mode = (viewState.value.mode as ConnectScreenMode.Phone)
                                                .copy(
                                                    verificationId = event.verificationId,
                                                    token = event.token
                                                )
                                        )
                                else -> {  }
                            }
                        }
                        else -> {
                            val result = verifyPhoneUseCase.execute(
                                verificationId = mode.verificationId!!,
                                code = viewState.value.secondTextFieldState!!.value,
                            )

                            result.onSuccess { authResult ->
                                val user = authResult.user
                                _viewEffectFlow.value = ConnectScreenViewEffect.NavigateToConnectSuccess
                                Timber.i("sign in with Firebase through phone success")
                            }.onFailure { exception ->
                                Timber.i("sign in with Firebase through phone error: ${exception.message}")
                            }

                        }
                    }
                    is ConnectScreenMode.Telegram -> when {
                        mode.requestId == null -> try {
                            val requestId: String? = sendTelegramNetworkAuthentication.execute(phone = viewState.value.firstTextFieldState!!.value)
                            _viewState.value = viewState.value.copy(
                                mode = (viewState.value.mode as ConnectScreenMode.Telegram).copy(requestId = requestId as String?),
                                secondTextFieldState = ConnectScreenViewState.TextFieldState()
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        else -> viewState.value.secondTextFieldState?.value?.let {
                            try {
                                verifyTelegramCodeUseCase.execute(code = it, requestId = mode.requestId)
                                _viewEffectFlow.value = ConnectScreenViewEffect.NavigateToConnectSuccess
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    is ConnectScreenMode.Facebook -> try {
                        val accessToken = verifyFacebookUseCase.execute(viewEvent.activity)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Timber.i("facebook connect error: $e")
                    }

                    else -> {

                    }
                }
            }
            is ConnectScreenViewEvent.UpdateSecondTextField -> viewModelScope.launch {
                _viewState.value = viewState.value
                    .copy(
                        secondTextFieldState = viewState.value
                            .secondTextFieldState
                            ?.copy(value = viewEvent.value)
                    )
            }
            is ConnectScreenViewEvent.LoginWithFacebookSuccess -> {
                _viewEffectFlow.value = ConnectScreenViewEffect.NavigateToConnectSuccess
            }
            is ConnectScreenViewEvent.LoginWithFacebookError -> {

            }
            is ConnectScreenViewEvent.LoginWithFacebookCancel -> {

            }
        }
    }

    fun load(mode: ConnectScreenMode) = when (mode) {
        is ConnectScreenMode.Email -> viewModelScope.launch {
            val profile = profileRepository.getOrCreateDefaultProfile()
            _viewState.value = viewState.value.copy(
                firstTextFieldState = ConnectScreenViewState.TextFieldState(
                    value = profile.workEmail ?: "",
                    placeholder = mode.title
                ),
                mode = mode
            )
        }
        is ConnectScreenMode.Phone -> viewModelScope.launch {
            val profile = profileRepository.getOrCreateDefaultProfile()
            _viewState.value = viewState.value.copy(
                firstTextFieldState = ConnectScreenViewState.TextFieldState(
                    value = profile.mobilePhone ?: "",
                    placeholder = mode.title
                ),
                mode = mode
            )
        }
        is ConnectScreenMode.Telegram -> viewModelScope.launch {
            _viewState.value = viewState.value.copy(
                mode = mode,
                firstTextFieldState = ConnectScreenViewState.TextFieldState(
                    value = "",
                    placeholder = mode.title
                )
            )
        }
        is ConnectScreenMode.Facebook -> viewModelScope.launch {
            _viewState.value = viewState.value.copy(
                mode = mode,
                isConnectButtonEnabled = true
            )
        }
    }

    fun verifyEmail(emailSignInLink: String) {
        viewModelScope.launch {
            Timber.i("verifyEmail - emailSignInLink: $emailSignInLink")
            try {
                Timber.i("verifyEmail - success")
                val email = verifyEmailSignInUseCase("talkappdanny@gmail.com", emailSignInLink)
                val profile = profileRepository.getOrCreateDefaultProfile()
                profileRepository.editProfile(profile.copy(workEmail = email))
                _viewEffectFlow.value = ConnectScreenViewEffect.NavigateToConnectSuccess
            } catch (e: EmailError.Unknown) {
                e.printStackTrace()
                Timber.i("verifyEmail - error")
            } catch (e: Exception) {

            }
        }
    }
}

sealed class ConnectScreenViewEvent: ViewEvent() {
    data object BackButtonClick: ConnectScreenViewEvent()
    data object ClearViewEffect: ConnectScreenViewEvent()
    data class UpdateTextField(val value: String): ConnectScreenViewEvent()
    data class UpdateSecondTextField(val value: String): ConnectScreenViewEvent()
    data class ConnectButtonClick(val activity: Activity): ConnectScreenViewEvent()
    data class LoginWithFacebookSuccess(val loginResult: LoginResult): ConnectScreenViewEvent()
    data class LoginWithFacebookError(val error: FacebookException): ConnectScreenViewEvent()
    data object LoginWithFacebookCancel: ConnectScreenViewEvent()
}

sealed class ConnectScreenViewEffect: ViewEffect() {
    data object NavigateBack: ConnectScreenViewEffect()
    data object NavigateToConnectSuccess: ConnectScreenViewEffect()
    data object LoginWithFacebook: ConnectScreenViewEffect()
}

data class ConnectScreenViewState(
    val mode: ConnectScreenMode = ConnectScreenMode.Email,
    val isConnectButtonEnabled: Boolean = false,
    val firstTextFieldState: TextFieldState? = null,
    val secondTextFieldState: TextFieldState? = null
) {
    data class TextFieldState(
        val value: String = "",
        val placeholder: String = "",
    )
}

sealed class ConnectScreenMode(
    val title: String,
    val instructions: String = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",
    val imageResId: Int,
    val cellBackground: NontransparentHexagonContentStyle.Background
) {
    fun color() = when (cellBackground) {
        is NontransparentHexagonContentStyle.Background.Gradient ->
            cellBackground.colors.first()
        is NontransparentHexagonContentStyle.Background.SingleColor ->
            cellBackground.value
    }

    data object Email: ConnectScreenMode(
        title = "Email",
        imageResId = R.drawable.ic_email,
        cellBackground = NontransparentHexagonContentStyle.Background.SingleColor(EmailColor)
    )

    data class Phone(
        val verificationId: String? = null,
        val token: PhoneAuthProvider.ForceResendingToken? = null
    ): ConnectScreenMode(
        title = "Phone",
        imageResId = R.drawable.ic_phone,
        cellBackground = NontransparentHexagonContentStyle.Background.SingleColor(PhoneColor),
    )

    data class Telegram(
        val requestId: String? = null
    ): ConnectScreenMode(
        title = "Telegram",
        imageResId = R.drawable.ic_telegram,
        cellBackground = NontransparentHexagonContentStyle.Background.SingleColor(TelegramColor),
    )

    data object Facebook: ConnectScreenMode(
        title = "Facebook",
        imageResId = R.drawable.ic_facebook,
        cellBackground = NontransparentHexagonContentStyle.Background.SingleColor(FacebookColor)
    )
}