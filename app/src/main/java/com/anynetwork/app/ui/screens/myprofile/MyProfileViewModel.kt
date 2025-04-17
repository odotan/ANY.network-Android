package com.anynetwork.app.ui.screens.myprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anynetwork.app.data.profile.ProfileRepository
import com.anynetwork.app.domain.FetchFacebookProfileNameUseCase
import com.anynetwork.app.model.Profile
import com.anynetwork.app.ui.screens.externalprofile.facebookCellPosition
import com.anynetwork.app.ui.screens.myprofile.MyProfileViewEvent.*
import com.anynetwork.app.ui.utils.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    val profileRepository: ProfileRepository,
    val fetchFacebookProfileNameUseCase: FetchFacebookProfileNameUseCase
): ViewModel() {

    private val _viewState = MutableStateFlow(MyProfileViewState())
    val viewState: StateFlow<MyProfileViewState> = _viewState

    private val _viewEffectFlow: MutableStateFlow<MyProfileViewEffect?> = MutableStateFlow(null)
    val viewEffectFlow: StateFlow<MyProfileViewEffect?> = _viewEffectFlow

    fun loadProfile() = viewModelScope.launch {
        profileRepository.getOrCreateDefaultProfile().let {
            _viewState.value = viewState.value.copy(
                firstName = it.firstName,
                lastName = it.lastName,
                company = it.company,
                address = it.address,
                mobilePhone = it.mobilePhone,
                homePhone = it.homePhone,
                workPhone = it.workPhone,
                mainPhone = it.mainPhone,
                workFax = it.workFax,
                homeFax = it.homeFax,
                pager = it.pager,
                otherPhone = it.otherPhone,
                homeEmail = it.homePhone,
                workEmail = it.workEmail,
                otherEmail = it.otherEmail,
                photoUri = it.avatarUri.log { "provide picture" },
                facebookProfileName = it.facebookProfileName.log { "fbName" },
                telegram = it.telegram
            )
        }
    }

    fun onViewEvent(event: MyProfileViewEvent) {
        when (event) {
            SaveButtonClick -> viewModelScope.launch {
                profileRepository.editProfile(
                    Profile(
                        firstName = _viewState.value.firstName,
                        lastName = _viewState.value.lastName,
                        mobilePhone = _viewState.value.mobilePhone,
                        homePhone = _viewState.value.homePhone,
                        workPhone = _viewState.value.workPhone,
                        mainPhone = _viewState.value.mainPhone,
                        workFax = _viewState.value.workFax,
                        homeFax = _viewState.value.homeFax,
                        pager = _viewState.value.pager,
                        otherPhone = _viewState.value.otherPhone,
                        homeEmail = _viewState.value.homePhone,
                        workEmail = _viewState.value.workEmail,
                        otherEmail = _viewState.value.otherEmail,
                        avatarUri = _viewState.value.photoUri,
                        facebookProfileName = _viewState.value.facebookProfileName,
                        telegram = _viewState.value.telegram
                    )
                )
                _viewState.value =
                    _viewState.value.copy(mode = MyProfileMode.Normal)
                _viewEffectFlow.value = MyProfileViewEffect.ProfileUpdated
            }
            is UpdateAddress -> _viewState.value =
                _viewState.value.copy(address = event.address)
            is UpdateCompany -> {
                _viewState.value =
                    _viewState.value.copy(company = event.company ?: "")
            }
            is UpdateEmail -> {
                _viewState.value =
                    _viewState.value.copy(homeEmail = event.email)
                _viewEffectFlow.value = MyProfileViewEffect.RequestFocusOnEmailTextField
            }
            is UpdateOtherEmail -> {
                _viewState.value =
                    _viewState.value.copy(otherEmail = event.email)
                _viewEffectFlow.value = MyProfileViewEffect.RequestFocusOnOtherEmailTextField
            }
            is UpdateWorkEmail -> {
                _viewState.value =
                    _viewState.value.copy(workEmail = event.email)
                _viewEffectFlow.value = MyProfileViewEffect.RequestFocusOnWorkEmailTextField
            }
            is UpdateFirstName -> {
                _viewState.value =
                    _viewState.value.copy(firstName = event.firstName)
            }
            is UpdateLastName -> {
                _viewState.value =
                    _viewState.value.copy(lastName = event.lastName)
            }
            is UpdateMobilePhone -> {
                _viewState.value =
                    _viewState.value.copy(mobilePhone = event.phone)
                _viewEffectFlow.value = MyProfileViewEffect.RequestFocusOnMobilePhoneTextField
            }
            is UpdateHomeFax -> {
                _viewState.value =
                    _viewState.value.copy(homeFax = event.phone)
                _viewEffectFlow.value = MyProfileViewEffect.RequestFocusOnHomeFaxTextField
            }
            is UpdateHomePhone -> {
                _viewState.value =
                    _viewState.value.copy(homePhone = event.phone)
                _viewEffectFlow.value = MyProfileViewEffect.RequestFocusOnHomePhoneTextField
            }
            is UpdateMainPhone -> {
                _viewState.value =
                    _viewState.value.copy(mainPhone = event.phone)
                _viewEffectFlow.value = MyProfileViewEffect.RequestFocusOnMainPhoneTextField
            }
            is UpdateOtherPhone -> {
                _viewState.value =
                    _viewState.value.copy(otherPhone = event.phone)
                _viewEffectFlow.value = MyProfileViewEffect.RequestFocusOnOtherPhoneTextField
            }
            is UpdatePager -> {
                _viewState.value =
                    _viewState.value.copy(pager = event.phone)
                _viewEffectFlow.value = MyProfileViewEffect.RequestFocusOnPagerTextField
            }
            is UpdateWorkFax -> {
                _viewState.value =
                    _viewState.value.copy(workFax = event.phone)
                _viewEffectFlow.value = MyProfileViewEffect.RequestFocusOnWorkFaxTextField
            }
            is UpdateWorkPhone -> {
                _viewState.value =
                    _viewState.value.copy(workPhone = event.phone)
                _viewEffectFlow.value = MyProfileViewEffect.RequestFocusOnWorkPhoneTextField
            }
            is UpdatePhotoUri -> {
                event.photoUri.log { "new profile picture" }
                _viewState.value =
                    _viewState.value.copy(photoUri = event.photoUri)
            }
            is RemoveProfilePicture -> {
                _viewState.value = _viewState.value.copy(photoUri = null)
            }
            is BackButtonClick -> {
                if (viewState.value.mode is MyProfileMode.Edit) {
                    _viewState.value = _viewState.value.copy(mode = MyProfileMode.Edit(isCanceling = true))
                } else if (viewState.value.mode is MyProfileMode.Connect) {
                    _viewState.value = _viewState.value.copy(mode = MyProfileMode.Normal)
                } else {
                    _viewEffectFlow.value = MyProfileViewEffect.NavigateBack
                }
            }
            is ClearViewEffect -> {
                _viewEffectFlow.value = null
            }

            FacebookCellClick -> viewModelScope.launch {
                if (viewState.value.mode == MyProfileMode.Connect) {
                    _viewEffectFlow.value = MyProfileViewEffect.NavigateToConnect(mode = "facebook")
                }
            }
            EmailCellClick -> viewModelScope.launch {
                if (viewState.value.mode == MyProfileMode.Connect) {
                    _viewEffectFlow.value = MyProfileViewEffect.NavigateToConnect(mode = "email")
                }
            }
            PhoneCellClick -> viewModelScope.launch {
                if (viewState.value.mode == MyProfileMode.Connect) {
                    _viewEffectFlow.value = MyProfileViewEffect.NavigateToConnect(mode = "phone")
                }
            }
            TelegramCellClick -> viewModelScope.launch {
                if (viewState.value.mode == MyProfileMode.Connect) {
                    _viewEffectFlow.value = MyProfileViewEffect.NavigateToConnect(mode = "telegram")
                }
            }
            TwelveWordsCellClick -> viewModelScope.launch {
                _viewEffectFlow.value = MyProfileViewEffect.NavigateToShowMyPhrase
            }

            ConnectButtonClick -> viewModelScope.launch {
                _viewState.value = _viewState.value.copy(mode = MyProfileMode.Connect)
            }

            EditButtonClick -> viewModelScope.launch {
                _viewState.value = _viewState.value.copy(mode = MyProfileMode.Edit())
            }

            DiscardProfileEditDialogDismiss -> viewModelScope.launch {
                _viewState.value = _viewState.value.copy(mode = MyProfileMode.Edit(isCanceling = false))
            }

            DiscardProfileEditDialogYesOptionClick -> viewModelScope.launch {
                _viewState.value = _viewState.value.copy(mode = MyProfileMode.Normal)
            }

            DiscardProfileEditDialogNoOptionClick -> viewModelScope.launch {
                _viewState.value = _viewState.value.copy(mode = MyProfileMode.Edit())
            }
        }
    }
}

data class MyProfileViewState(
    val mode: MyProfileMode = MyProfileMode.Normal,
    val firstName: String = "",
    val lastName: String = "",
    val company: String = "",
    val mobilePhone: String? = null,
    val homePhone: String? = null,
    val workPhone: String? = null,
    val mainPhone: String? = null,
    val workFax: String? = null,
    val homeFax: String? = null,
    val pager: String? = null,
    val otherPhone: String? = null,
    val homeEmail: String? = null,
    val workEmail: String? = null,
    val otherEmail: String? = null,
    val address: String? = null,
    val photoUri: String? = null,
    val facebookProfileName: String? = null,
    val telegram: String? = null
)

sealed class MyProfileViewEvent {
    data object SaveButtonClick: MyProfileViewEvent()
    data class UpdateFirstName(val firstName: String): MyProfileViewEvent()
    data class UpdateLastName(val lastName: String): MyProfileViewEvent()
    data class UpdateCompany(val company: String?): MyProfileViewEvent()
    data class UpdateMobilePhone(
        val phone: String?,
        val shouldRequestFocus: Boolean = false
    ): MyProfileViewEvent()
    data class UpdateHomePhone(
        val phone: String?,
        val shouldRequestFocus: Boolean = false
    ): MyProfileViewEvent()
    data class UpdateWorkPhone(
        val phone: String?,
        val shouldRequestFocus: Boolean = false
    ): MyProfileViewEvent()
    data class UpdateMainPhone(
        val phone: String?,
        val shouldRequestFocus: Boolean = false
    ): MyProfileViewEvent()
    data class UpdateWorkFax(
        val phone: String?,
        val shouldRequestFocus: Boolean = false
    ): MyProfileViewEvent()
    data class UpdateHomeFax(
        val phone: String?,
        val shouldRequestFocus: Boolean = false
    ): MyProfileViewEvent()
    data class UpdatePager(
        val phone: String?,
        val shouldRequestFocus: Boolean = false
    ): MyProfileViewEvent()
    data class UpdateOtherPhone(
        val phone: String?,
        val shouldRequestFocus: Boolean = false
    ): MyProfileViewEvent()
    data class UpdateEmail(
        val email: String?,
        val shouldRequestFocus: Boolean = false
    ): MyProfileViewEvent()
    data class UpdateWorkEmail(
        val email: String?,
        val shouldRequestFocus: Boolean = false
    ): MyProfileViewEvent()
    data class UpdateOtherEmail(
        val email: String?,
        val shouldRequestFocus: Boolean = false
    ): MyProfileViewEvent()
    data class UpdateAddress(
        val address: String?,
        val shouldRequestFocus: Boolean = false
    ): MyProfileViewEvent()
    data class UpdatePhotoUri(val photoUri: String): MyProfileViewEvent()
    data object FacebookCellClick: MyProfileViewEvent()
    data object EmailCellClick: MyProfileViewEvent()
    data object PhoneCellClick: MyProfileViewEvent()
    data object TelegramCellClick: MyProfileViewEvent()
    data object RemoveProfilePicture: MyProfileViewEvent()
    data object BackButtonClick: MyProfileViewEvent()
    data object ClearViewEffect: MyProfileViewEvent()
    data object ConnectButtonClick: MyProfileViewEvent()
    data object EditButtonClick: MyProfileViewEvent()
    data object DiscardProfileEditDialogDismiss: MyProfileViewEvent()
    data object DiscardProfileEditDialogYesOptionClick: MyProfileViewEvent()
    data object DiscardProfileEditDialogNoOptionClick: MyProfileViewEvent()
    data object TwelveWordsCellClick: MyProfileViewEvent()
}

sealed class MyProfileViewEffect {
    data object RequestFocusOnMobilePhoneTextField: MyProfileViewEffect()
    data object RequestFocusOnHomePhoneTextField: MyProfileViewEffect()
    data object RequestFocusOnWorkPhoneTextField: MyProfileViewEffect()
    data object RequestFocusOnMainPhoneTextField: MyProfileViewEffect()
    data object RequestFocusOnWorkFaxTextField: MyProfileViewEffect()
    data object RequestFocusOnHomeFaxTextField: MyProfileViewEffect()
    data object RequestFocusOnPagerTextField: MyProfileViewEffect()
    data object RequestFocusOnOtherPhoneTextField: MyProfileViewEffect()
    data object RequestFocusOnEmailTextField: MyProfileViewEffect()
    data object RequestFocusOnWorkEmailTextField: MyProfileViewEffect()
    data object RequestFocusOnOtherEmailTextField: MyProfileViewEffect()
    data object NavigateBack: MyProfileViewEffect()
    data object ProfileUpdated: MyProfileViewEffect()
    data class NavigateToConnect(val mode: String): MyProfileViewEffect()
    data object NavigateToShowMyPhrase: MyProfileViewEffect()
}
