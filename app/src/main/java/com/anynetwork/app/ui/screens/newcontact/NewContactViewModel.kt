package com.anynetwork.app.ui.screens.newcontact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anynetwork.app.data.contacts.ContactsRepository
import com.anynetwork.app.model.Contact
import com.anynetwork.app.ui.base.NavigateBack
import com.anynetwork.app.ui.base.NavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewContactViewModel @Inject constructor(
    val contactsRepository: ContactsRepository
): ViewModel() {

    private val _navigationEventFlow: MutableStateFlow<NavigationEvent?> = MutableStateFlow(null)
    val navigationEvents: StateFlow<NavigationEvent?> = _navigationEventFlow

    private val _viewState = MutableStateFlow(NewContactViewState())
    val viewState: StateFlow<NewContactViewState> = _viewState

    fun onViewAction(viewAction: NewContactViewAction) {
        when (viewAction) {
            is NewContactViewAction.UpdateEmail -> _viewState.value =
                _viewState.value.copy(email = viewAction.email)

            is NewContactViewAction.UpdateFirstName -> _viewState.value =
                _viewState.value.copy(firstName = viewAction.firstName)

            is NewContactViewAction.UpdateLastName -> _viewState.value =
                _viewState.value.copy(lastName = viewAction.lastName)

            is NewContactViewAction.UpdatePhone -> _viewState.value =
                _viewState.value.copy(phone = viewAction.phone)

            is NewContactViewAction.UpdatePhotoUri -> _viewState.value =
                _viewState.value.copy(photoUri = viewAction.photoUri)

            NewContactViewAction.SaveButtonClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val newContactAdded = contactsRepository.addContact(
                        Contact(
                            id = 0,
                            name = "${viewState.value.firstName} ${viewState.value.lastName}",
                            avatarUri = viewState.value.photoUri
                        )
                    )
                    if (newContactAdded) {
                        _navigationEventFlow.value = NavigateBack
                    }
                }
            }
        }
    }

    fun resetNavigationState() {
        _navigationEventFlow.value = null
    }
}

data class NewContactViewState(
    val firstName: String = "",
    val lastName: String = "",
    val phone: String? = null,
    val email: String? = null,
    val photoUri: String? = null
)

sealed class NewContactViewAction {
    data object SaveButtonClick: NewContactViewAction()
    data class UpdateFirstName(val firstName: String): NewContactViewAction()
    data class UpdateLastName(val lastName: String): NewContactViewAction()
    data class UpdatePhone(val phone: String?): NewContactViewAction()
    data class UpdateEmail(val email: String?): NewContactViewAction()
    data class UpdatePhotoUri(val photoUri: String?): NewContactViewAction()
}