package com.anynetwork.app.ui.screens.externalprofile

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anynetwork.app.R
import com.anynetwork.app.data.contacts.ContactsRepository
import com.anynetwork.app.data.interaction.InteractionRepository
import com.anynetwork.app.model.Contact
import com.anynetwork.app.model.Interaction
import com.anynetwork.app.ui.base.NavigateBack
import com.anynetwork.app.ui.base.NavigationEvent
import com.anynetwork.app.ui.base.ViewEffect
import com.anynetwork.app.ui.components.hexagon.ChangeScale
import com.anynetwork.app.ui.components.hexagon.NontransparentHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.NontransparentHexagonContentStyle.Background.Gradient
import com.anynetwork.app.ui.components.hexagon.NontransparentHexagonContentStyle.Background.SingleColor
import com.anynetwork.app.ui.screens.externalprofile.ExternalProfileViewEvent.*
import com.anynetwork.app.ui.theme.EmailColor
import com.anynetwork.app.ui.theme.FacebookColor
import com.anynetwork.app.ui.theme.InstagramColor
import com.anynetwork.app.ui.theme.LinkedinColor
import com.anynetwork.app.ui.theme.MessengerColor
import com.anynetwork.app.ui.theme.PhoneColor
import com.anynetwork.app.ui.theme.PrimaryColor
import com.anynetwork.app.ui.theme.SkypeColor
import com.anynetwork.app.ui.theme.TelegramColor
import com.anynetwork.app.ui.theme.TiktokColor
import com.anynetwork.app.ui.theme.TwitterColor
import com.anynetwork.app.ui.theme.WhatsappColor
import com.anynetwork.app.ui.utils.log
import com.anynetwork.app.utils.isValidEmail
import com.anynetwork.app.utils.isValidPhone
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ExternalProfileViewModel @Inject constructor(
    val contactsRepository: ContactsRepository,
    val interactionRepository: InteractionRepository
): ViewModel() {
    private val _contact = MutableStateFlow<Contact?>(null)
    val contact: StateFlow<Contact?> = _contact

    private val _viewState = MutableStateFlow(ExternalProfileViewState())
    val viewState: StateFlow<ExternalProfileViewState> = _viewState

    private val _navigationEventFlow: MutableStateFlow<NavigationEvent?> = MutableStateFlow(null)
    val navigationEvents: StateFlow<NavigationEvent?> = _navigationEventFlow

    private val _viewEffectFlow: MutableStateFlow<ViewEffect?> = MutableStateFlow(null)
    val viewEffectFlow: StateFlow<ViewEffect?> = _viewEffectFlow

    fun loadContact(id: Long?, input: String? = null) = viewModelScope.launch {
        Timber.i("loadContact")
        if (id != null) {
            _contact.value = contactsRepository.getContact(id)
            _viewState.value = _viewState.value.copy(
                firstName = _contact.value?.firstName() ?: "",
                lastName = _contact.value?.lastName().log { "last name" } ?: "",
                company = _contact.value?.company ?: "",
                phone = _contact.value?.phone,
                mobilePhone = _contact.value?.mobilePhone(),
                homePhone = _contact.value?.homePhone(),
                workPhone = _contact.value?.workPhone(),
                mainPhone = _contact.value?.mainPhone(),
                workFax = _contact.value?.workFax(),
                homeFax = _contact.value?.homeFax(),
                pager = _contact.value?.pager(),
                otherPhone = _contact.value?.otherPhone(),
                email = _contact.value?.email,
                homeEmail = _contact.value?.homeEmail(),
                workEmail = _contact.value?.workPhone(),
                otherEmail = _contact.value?.otherEmail(),
                address = _viewState.value.address,
                photoUri = _contact.value?.avatarUri.log { "profile picture" },
                isFavorite = _contact.value?.isFavorite ?: false
            )
        } else {
            var email: String? = null
            var phone: String? = null
            var firstName: String? = null
            var lastName: String? = null

            if (input?.isValidEmail() == true) {
                email = input
            } else if (input?.isValidPhone() == true) {
                phone = input
            } else if (input?.isNotEmpty() == true) {
                val words = input.trim().split("\\s+".toRegex())

                // Set the first word as firstName, and join the rest as lastName
                firstName = words.firstOrNull() ?: ""
                lastName = if (words.size > 1) words.drop(1).joinToString(" ") else ""
            }
            _viewState.value = _viewState.value.copy(
                mode = ExternalProfileMode.NewContact,
                firstName = firstName ?: "",
                lastName = lastName ?: "",
                homeEmail = email,
                mobilePhone = phone
            )
        }
    }

    fun onGridZoomChange(zoom: Float) {
        if (zoom < 1f && _viewState.value.gridZoom >= 1f) {
            _viewState.value = _viewState.value.copy(gridZoom = zoom)
            _navigationEventFlow.value = NavigateBack
        } else {
            _viewState.value = _viewState.value.copy(gridZoom = zoom)
        }
    }

    fun resetNavigationState() {
        _navigationEventFlow.value = null
    }

    fun onViewEvent(event: ExternalProfileViewEvent) {
        event.log { "onViewEvent" }
        when (event) {
            BackButtonClick -> {
                when (_viewState.value.mode) {
                    is ExternalProfileMode.RequestNetwork -> {
                        _viewState.value = _viewState.value.copy(mode = ExternalProfileMode.Normal)
                    }

                    is ExternalProfileMode.Normal, is ExternalProfileMode.NewContact -> {
                        _navigationEventFlow.value = NavigateBack
                    }

                    is ExternalProfileMode.Edit -> {
                        _viewState.value = _viewState.value.copy(mode = ExternalProfileMode.Normal)
                    }

                    is ExternalProfileMode.FocusedNetwork -> {
                        _viewState.value = _viewState.value.copy(mode = ExternalProfileMode.RequestNetwork())
                    }
                }
            }
            is RequestNetworkButtonClick -> {
                _viewState.value = _viewState.value.copy(
                    mode = ExternalProfileMode.RequestNetwork(event.isSearching)
                )
            }
            EditButtonClick -> {
                _viewState.value = _viewState.value.copy(
                    mode = ExternalProfileMode.Edit
                )
            }
            SaveButtonClick -> {
                viewModelScope.launch {
                    if (_viewState.value.mode is ExternalProfileMode.NewContact) {
                        val newContactResult = contactsRepository.addContact(
                            contact = Contact(
                                id = 0,
                                name = "${_viewState.value.firstName} ${_viewState.value.lastName}",
                                company = _viewState.value.company,
                                phones = mutableListOf<Contact.Phone>().apply {
                                    _viewState.value.mobilePhone?.let {
                                        add(Contact.Phone(type = Contact.Phone.Type.Mobile, value = it))
                                    }
                                    _viewState.value.homePhone?.let {
                                        add(Contact.Phone(type = Contact.Phone.Type.Home, value = it))
                                    }
                                    _viewState.value.workPhone?.let {
                                        add(Contact.Phone(type = Contact.Phone.Type.Work, value = it))
                                    }
                                    _viewState.value.mainPhone?.let {
                                        add(Contact.Phone(type = Contact.Phone.Type.Main, value = it))
                                    }
                                    _viewState.value.workFax?.let {
                                        add(
                                            Contact.Phone(
                                                type = Contact.Phone.Type.WorkFax,
                                                value = it
                                            )
                                        )
                                    }
                                    _viewState.value.homeFax?.let {
                                        add(
                                            Contact.Phone(
                                                type = Contact.Phone.Type.HomeFax,
                                                value = it
                                            )
                                        )
                                    }
                                    _viewState.value.pager?.let {
                                        add(Contact.Phone(type = Contact.Phone.Type.Pager, value = it))
                                    }
                                    _viewState.value.otherPhone?.let {
                                        add(Contact.Phone(type = Contact.Phone.Type.Other, value = it))
                                    }
                                },
                                emails = mutableListOf<Contact.Email>().apply {
                                    _viewState.value.homeEmail?.let {
                                        add(Contact.Email(type = Contact.Email.Type.Home, value = it))
                                    }
                                    _viewState.value.workEmail?.let {
                                        add(Contact.Email(type = Contact.Email.Type.Work, value = it))
                                    }
                                    _viewState.value.otherEmail?.let {
                                        add(Contact.Email(type = Contact.Email.Type.Other, value = it))
                                    }
                                },
                                avatarUri = _viewState.value.photoUri
                            )
                        )
                        if (newContactResult) {
                            _navigationEventFlow.value = NavigateBack
                        }
                    } else {
                        val updatedContact = Contact(
                            id = _contact.value!!.id,
                            name = "${_viewState.value.firstName} ${_viewState.value.lastName}",
                            company = _viewState.value.company,
                            phones = mutableListOf<Contact.Phone>().apply {
                                _viewState.value.mobilePhone?.let {
                                    add(Contact.Phone(type = Contact.Phone.Type.Mobile, value = it))
                                }
                                _viewState.value.homePhone?.let {
                                    add(Contact.Phone(type = Contact.Phone.Type.Home, value = it))
                                }
                                _viewState.value.workPhone?.let {
                                    add(Contact.Phone(type = Contact.Phone.Type.Work, value = it))
                                }
                                _viewState.value.mainPhone?.let {
                                    add(Contact.Phone(type = Contact.Phone.Type.Main, value = it))
                                }
                                _viewState.value.workFax?.let {
                                    add(
                                        Contact.Phone(
                                            type = Contact.Phone.Type.WorkFax,
                                            value = it
                                        )
                                    )
                                }
                                _viewState.value.homeFax?.let {
                                    add(
                                        Contact.Phone(
                                            type = Contact.Phone.Type.HomeFax,
                                            value = it
                                        )
                                    )
                                }
                                _viewState.value.pager?.let {
                                    add(Contact.Phone(type = Contact.Phone.Type.Pager, value = it))
                                }
                                _viewState.value.otherPhone?.let {
                                    add(Contact.Phone(type = Contact.Phone.Type.Other, value = it))
                                }
                            },
                            emails = mutableListOf<Contact.Email>().apply {
                                _viewState.value.homeEmail?.let {
                                    add(Contact.Email(type = Contact.Email.Type.Home, value = it))
                                }
                                _viewState.value.workEmail?.let {
                                    add(Contact.Email(type = Contact.Email.Type.Work, value = it))
                                }
                                _viewState.value.otherEmail?.let {
                                    add(Contact.Email(type = Contact.Email.Type.Other, value = it))
                                }
                            },
                            avatarUri = _viewState.value.photoUri,
                        )
                        val editContactResult = contactsRepository.editContact(
                            _contact.value!!.id,
                            updatedContact
                        )
                        if (editContactResult) {
                            _viewState.value = _viewState.value.copy(
                                mode = ExternalProfileMode.Normal
                            )
                            _viewEffectFlow.value = ExternalProfileViewEffect.ContactUpdated
                        }
                    }
                }
            }
            is UpdateAddress -> _viewState.value =
                    _viewState.value.copy(address = event.address)
            is UpdateCompany -> {
                _viewState.value =
                    _viewState.value.copy(company = event.company)
            }
            is UpdateEmail -> {
                Timber.i("updateEmail: ${event.email}")
                _viewState.value =
                    _viewState.value.copy(homeEmail = event.email)
                _viewEffectFlow.value = ExternalProfileViewEffect.RequestFocusOnEmailTextField
            }
            is UpdateOtherEmail -> {
                _viewState.value =
                    _viewState.value.copy(otherEmail = event.email)
                _viewEffectFlow.value = ExternalProfileViewEffect.RequestFocusOnOtherEmailTextField
            }
            is UpdateWorkEmail -> {
                _viewState.value =
                    _viewState.value.copy(workEmail = event.email)
                _viewEffectFlow.value = ExternalProfileViewEffect.RequestFocusOnWorkEmailTextField
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
                _viewEffectFlow.value = ExternalProfileViewEffect.RequestFocusOnMobilePhoneTextField
            }
            is UpdateHomeFax -> {
                _viewState.value =
                    _viewState.value.copy(homeFax = event.phone)
                _viewEffectFlow.value = ExternalProfileViewEffect.RequestFocusOnHomeFaxTextField
            }
            is UpdateHomePhone -> {
                _viewState.value =
                    _viewState.value.copy(homePhone = event.phone)
                _viewEffectFlow.value = ExternalProfileViewEffect.RequestFocusOnHomePhoneTextField
            }
            is UpdateMainPhone -> {
                _viewState.value =
                    _viewState.value.copy(mainPhone = event.phone)
                _viewEffectFlow.value = ExternalProfileViewEffect.RequestFocusOnMainPhoneTextField
            }
            is UpdateOtherPhone -> {
                _viewState.value =
                    _viewState.value.copy(otherPhone = event.phone)
                _viewEffectFlow.value = ExternalProfileViewEffect.RequestFocusOnOtherPhoneTextField
            }
            is UpdatePager -> {
                _viewState.value =
                    _viewState.value.copy(pager = event.phone)
                _viewEffectFlow.value = ExternalProfileViewEffect.RequestFocusOnPagerTextField
            }
            is UpdateWorkFax -> {
                _viewState.value =
                    _viewState.value.copy(workFax = event.phone)
                _viewEffectFlow.value = ExternalProfileViewEffect.RequestFocusOnWorkFaxTextField
            }
            is UpdateWorkPhone -> {
                _viewState.value =
                    _viewState.value.copy(workPhone = event.phone)
                _viewEffectFlow.value = ExternalProfileViewEffect.RequestFocusOnWorkPhoneTextField
            }
            is UpdatePhotoUri -> {
                event.photoUri.log { "new profile picture" }
                _viewState.value =
                    _viewState.value.copy(photoUri = event.photoUri)
            }
            RemoveProfilePicture -> {
                _viewState.value =
                    _viewState.value.copy(photoUri = null)
            }
            is FavoriteButtonClick -> viewModelScope.launch {
                val id = _contact.value!!.id
                val newValue = !_contact.value!!.isFavorite
                _viewState.value = viewState.value.copy(isFavorite = newValue)
                _contact.value = contact.value?.copy(isFavorite = newValue)
                contactsRepository.favoriteContact(id, newValue)

                _viewEffectFlow.value = ExternalProfileViewEffect.ContactUpdated
            }

            EmailButtonClick -> viewModelScope.launch {
                val interaction = interactionRepository.getAllInteractions().firstOrNull { it.type == Interaction.Type.Email && it.contactId == contact.value!!.id }
                if (interaction == null) {
                    interactionRepository.insertInteraction(
                        Interaction(
                            contactId = contact.value!!.id,
                            type = Interaction.Type.Email
                        )
                    )
                } else {
                    interactionRepository.updateInteractionLastModified(interaction.id)
                }
                _viewEffectFlow.value = ExternalProfileViewEffect.WriteEmail(contact.value!!.email!!)
            }
            PhoneButtonClick -> viewModelScope.launch {
                val interaction = interactionRepository.getAllInteractions().firstOrNull { it.type == Interaction.Type.Phone && it.contactId == contact.value!!.id }
                if (interaction == null) {
                    interactionRepository.insertInteraction(
                        Interaction(
                            contactId = contact.value!!.id,
                            type = Interaction.Type.Phone
                        )
                    )
                } else {
                    interactionRepository.updateInteractionLastModified(interaction.id)
                }
                _viewEffectFlow.value = ExternalProfileViewEffect.CallPhoneNumber(contact.value!!.phone!!)
            }
            ClearViewEffect -> {
                _viewEffectFlow.value = null
            }
            ClearNavigationEffect -> {
                _navigationEventFlow.value = null
            }
            is RequestNetworkClick -> {
                _viewState.value = viewState.value.copy(mode = ExternalProfileMode.FocusedNetwork(network = event.network, focusedCellId = event.cellId))
                _viewEffectFlow.value = ExternalProfileViewEffect.FocusGrid(changeScale = event.changeScale)
            }
        }
    }
}

sealed class ExternalProfileMode {
    data object Normal: ExternalProfileMode()
    data class RequestNetwork(val isSearching: Boolean = false): ExternalProfileMode()
    data class FocusedNetwork(val network: Network, val focusedCellId: Int): ExternalProfileMode()
    data object Edit: ExternalProfileMode()
    data object NewContact: ExternalProfileMode()
}

sealed class Network(val image: Int, val background: NontransparentHexagonContentStyle.Background) {
    data object Link: Network(image = R.drawable.ic_link, background = SingleColor(PrimaryColor))
    data object Facebook: Network(image = R.drawable.ic_facebook, background = SingleColor(FacebookColor))
    data object Messenger: Network(image = R.drawable.ic_messenger, background = SingleColor(MessengerColor))
    data object XTwitter: Network(image = R.drawable.ic_twitter, background = SingleColor(TwitterColor))
    data object Instagram: Network(image = R.drawable.ic_instagram, background = SingleColor(InstagramColor))
    data object Phone: Network(image = R.drawable.ic_phone, background = SingleColor(PhoneColor))
    data object Email: Network(image = R.drawable.ic_email, background = SingleColor(EmailColor))
    data object Telegram: Network(image = R.drawable.ic_telegram, background = SingleColor(TelegramColor))
    data object WhatsApp: Network(image = R.drawable.ic_whatsapp, background = SingleColor(WhatsappColor))
    data object Linkedin: Network(image = R.drawable.ic_linkedin, background = SingleColor(LinkedinColor))
    data object Bitcoin: Network(image = R.drawable.ic_bitcoin, background = Gradient(
        colors = listOf(Color(0xFFF7931A), Color(0xFFFFE81C)),
        startOffset = Offset(-75f, 100f),
        endOffset = Offset(200f, -100f)
    )
    )
    data object Ethereum: Network(image = R.drawable.ic_ethereum, background = Gradient(
        colors = listOf(Color.Black, Color.White),
        startOffset = Offset(-75f, 100f),
        endOffset = Offset(200f, -100f)
    ))
    data object Skype: Network(image = R.drawable.ic_skype, background = SingleColor(SkypeColor))
    data object Tiktok: Network(image = R.drawable.ic_tiktok, background = SingleColor(TiktokColor))
}

data class ExternalProfileViewState(
    val gridZoom: Float = 518.08f/393,
    val mode: ExternalProfileMode = ExternalProfileMode.Normal,
    val firstName: String = "",
    val lastName: String = "",
    val company: String? = "",
    val phone: String? = null,
    val mobilePhone: String? = null,
    val homePhone: String? = null,
    val workPhone: String? = null,
    val mainPhone: String? = null,
    val workFax: String? = null,
    val homeFax: String? = null,
    val pager: String? = null,
    val otherPhone: String? = null,
    val email: String? = null,
    val homeEmail: String? = null,
    val workEmail: String? = null,
    val otherEmail: String? = null,
    val address: String? = null,
    val photoUri: String? = null,
    val isFavorite: Boolean = false,
)

sealed class ExternalProfileViewEvent {
    data object BackButtonClick: ExternalProfileViewEvent()
    data object EditButtonClick: ExternalProfileViewEvent()
    data class RequestNetworkButtonClick(val isSearching: Boolean = false): ExternalProfileViewEvent()
    data object SaveButtonClick: ExternalProfileViewEvent()
    data class UpdateFirstName(val firstName: String): ExternalProfileViewEvent()
    data class UpdateLastName(val lastName: String): ExternalProfileViewEvent()
    data class UpdateCompany(val company: String?): ExternalProfileViewEvent()
    data class UpdateMobilePhone(
        val phone: String?,
        val shouldRequestFocus: Boolean = false
    ): ExternalProfileViewEvent()
    data class UpdateHomePhone(
        val phone: String?,
        val shouldRequestFocus: Boolean = false
    ): ExternalProfileViewEvent()
    data class UpdateWorkPhone(
        val phone: String?,
        val shouldRequestFocus: Boolean = false
    ): ExternalProfileViewEvent()
    data class UpdateMainPhone(
        val phone: String?,
        val shouldRequestFocus: Boolean = false
    ): ExternalProfileViewEvent()
    data class UpdateWorkFax(
        val phone: String?,
        val shouldRequestFocus: Boolean = false
    ): ExternalProfileViewEvent()
    data class UpdateHomeFax(
        val phone: String?,
        val shouldRequestFocus: Boolean = false
    ): ExternalProfileViewEvent()
    data class UpdatePager(
        val phone: String?,
        val shouldRequestFocus: Boolean = false
    ): ExternalProfileViewEvent()
    data class UpdateOtherPhone(
        val phone: String?,
        val shouldRequestFocus: Boolean = false
    ): ExternalProfileViewEvent()
    data class UpdateEmail(
        val email: String?,
        val shouldRequestFocus: Boolean = false
    ): ExternalProfileViewEvent()
    data class UpdateWorkEmail(
        val email: String?,
        val shouldRequestFocus: Boolean = false
    ): ExternalProfileViewEvent()
    data class UpdateOtherEmail(
        val email: String?,
        val shouldRequestFocus: Boolean = false
    ): ExternalProfileViewEvent()
    data class UpdateAddress(
        val address: String?,
        val shouldRequestFocus: Boolean = false
    ): ExternalProfileViewEvent()
    data class UpdatePhotoUri(val photoUri: String): ExternalProfileViewEvent()
    data object RemoveProfilePicture: ExternalProfileViewEvent()
    data object FavoriteButtonClick: ExternalProfileViewEvent()
    data object PhoneButtonClick: ExternalProfileViewEvent()
    data object EmailButtonClick: ExternalProfileViewEvent()
    data object ClearViewEffect: ExternalProfileViewEvent()
    data object ClearNavigationEffect: ExternalProfileViewEvent()
    data class RequestNetworkClick(val cellId: Int, val network: Network, val changeScale: ChangeScale): ExternalProfileViewEvent()
}

sealed class ExternalProfileViewEffect: ViewEffect() {
    data object RequestFocusOnMobilePhoneTextField: ExternalProfileViewEffect()
    data object RequestFocusOnHomePhoneTextField: ExternalProfileViewEffect()
    data object RequestFocusOnWorkPhoneTextField: ExternalProfileViewEffect()
    data object RequestFocusOnMainPhoneTextField: ExternalProfileViewEffect()
    data object RequestFocusOnWorkFaxTextField: ExternalProfileViewEffect()
    data object RequestFocusOnHomeFaxTextField: ExternalProfileViewEffect()
    data object RequestFocusOnPagerTextField: ExternalProfileViewEffect()
    data object RequestFocusOnOtherPhoneTextField: ExternalProfileViewEffect()
    data object RequestFocusOnEmailTextField: ExternalProfileViewEffect()
    data object RequestFocusOnWorkEmailTextField: ExternalProfileViewEffect()
    data object RequestFocusOnOtherEmailTextField: ExternalProfileViewEffect()
    data class CallPhoneNumber(val phoneNumber: String): ExternalProfileViewEffect()
    data class WriteEmail(val emailAddress: String): ExternalProfileViewEffect()
    data object ContactUpdated: ExternalProfileViewEffect()
    data class FocusGrid(val changeScale: ChangeScale): ExternalProfileViewEffect()
}