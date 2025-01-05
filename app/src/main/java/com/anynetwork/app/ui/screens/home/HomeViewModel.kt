package com.anynetwork.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anynetwork.app.data.contacts.ContactsRepository
import com.anynetwork.app.data.interaction.InteractionRepository
import com.anynetwork.app.data.profile.ProfileRepository
import com.anynetwork.app.model.Contact
import com.anynetwork.app.model.Interaction
import com.anynetwork.app.ui.utils.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val contactsRepository: ContactsRepository,
    val profileRepository: ProfileRepository,
    val interactionRepository: InteractionRepository
): ViewModel() {
    private var _contactsFetched = true

    private val _readContactsPermissionGranted: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val readContactsPermissionGranted: StateFlow<Boolean?> = _readContactsPermissionGranted

    private val _getAccountsPermissionGranted = MutableStateFlow(false)

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts
        .distinctUntilChanged { old, new -> old.size == new.size }
        .onEach { newValue ->
            newValue.size.log { "contacts emitted size" }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), _contacts.value)


    private val _searchContacts = MutableStateFlow<List<Contact>>(emptyList())
    val searchContacts: StateFlow<List<Contact>> = _searchContacts
        .distinctUntilChanged { old, new -> old.size == new.size }
        .onEach { newValue ->
            newValue.size.log { "searchContacts emitted size" }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, _searchContacts.value)

    private val _interactions = MutableStateFlow<List<Interaction>>(emptyList())
    val interactions: StateFlow<List<Interaction>> = _interactions

    private val _hexGridItems = MutableStateFlow<List<GridItem>>(emptyList())
    val hexGridItems: StateFlow<List<GridItem>> = _hexGridItems
        .distinctUntilChanged { old, new -> old.isIdenticalTo(new) }
        .onEach { newValue ->
            newValue.size.log { "hexGridItems emitted size" }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), _hexGridItems.value)

    private val _viewState = MutableStateFlow(HomeViewState())
    val viewState: StateFlow<HomeViewState> = _viewState
        .distinctUntilChanged { old, new ->
            old.mode == new.mode && old.defaultShowBottomSheet == new.defaultShowBottomSheet && old.photoUri == new.photoUri }
        .onEach { newValue ->
            newValue.log { "viewState emitted" }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), _viewState.value)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _viewEffectFlow: MutableStateFlow<HomeViewEffect?> = MutableStateFlow(null)
    val viewEffectFlow: StateFlow<HomeViewEffect?> = _viewEffectFlow

    init {
        Timber.i("init")
        viewModelScope.launch {
            _searchQuery
                .debounce(50)
                .collect { query ->
                    withContext(Dispatchers.Default) {
                        val filteredContacts = contacts.value
                            .filter {
                                it.matchesQuery(searchQuery.value)
                            }
                            .sortedBy { it.name }
                        _searchContacts.value = filteredContacts
                        val newGridItems = processContactsForGrid(filteredContacts)
                        updateHexGridItems(newGridItems)
                        if (_hexGridItems.value != newGridItems) {
                            Timber.d("Hex grid updated with new items.")
                        } else {
                            Timber.d("Hex grid update skipped; items are identical.")
                        }
                    }
                }
        }
    }

    fun reloadData() = viewModelScope.launch {
        try {
            Timber.i("reloadData")
            val latestInteractionsDeferred = async { interactionRepository.getAllInteractions() }
            val latestInteractions = latestInteractionsDeferred.await()
            updateInteractions(latestInteractions)

            // Collect emissions from contacts repository
            contactsRepository.getContacts().collect { emittedContacts ->
                _contactsFetched = true
                updateContacts(emittedContacts)

                updateDependentStates(emittedContacts, latestInteractions)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error reloading data")
        }
    }

    private fun updateContacts(emittedContacts: List<Contact>) {
        val distinctContacts = emittedContacts
            .distinctBy { it.id }
            .distinctBy { it.phone?.normalize() }
            .sortedBy { it.name }

        Timber.i("current contacts list size: ${_contacts.value.size}")
        if (_contacts.value != distinctContacts) {
            Timber.i("updateContacts")
            _contacts.value = distinctContacts
        }
    }

    private fun updateDependentStates(contacts: List<Contact>, interactions: List<Interaction>) {
        // Update search contacts
        updateSearchContacts(_searchQuery.value)

        val newGridItems = processContactsForGrid(contacts, interactions)
        updateHexGridItems(newGridItems)
        if (_hexGridItems.value != newGridItems) {
            Timber.d("Hex grid updated with new items.")
        } else {
            Timber.d("Hex grid update skipped; items are identical.")
        }
    }

    private fun updateHexGridItems(newItems: List<GridItem>) {
        val currentItems = _hexGridItems.value
        if (!currentItems.isIdenticalTo(newItems)) {
            Timber.i("updateHexGridItems")
            _hexGridItems.value = newItems
        }
    }

    // Extension function to compare lists deeply
    private fun List<GridItem>.isIdenticalTo(other: List<GridItem>): Boolean {
        if (this.size != other.size) return false
        return this.zip(other).all { (a, b) -> a == b }
    }

    private fun updateSearchContacts(query: String) {
        viewModelScope.launch {
            _searchContacts.value = _contacts.value.filter { it.matchesQuery(query) }.sortedBy { it.name }
        }
    }

    private fun updateInteractions(interactions: List<Interaction>) {
        if (_interactions.value != interactions) {
            _interactions.value = interactions
        }
    }

    private fun processContactsForGrid(contacts: List<Contact>, interactions: List<Interaction>): List<GridItem> {
        return when (viewState.value.mode) {
            is HomeScreenMode.SearchingGrid, HomeScreenMode.SearchingList -> {
                searchContacts.value.map { GridItem.SearchGridItem(it) }
            }
            else -> {
                val favoriteItems = contacts.filter { it.isFavorite }
                    .map { GridItem.FavoritedContactGridItem(it) }
                val interactionItems = interactions
                    .mapNotNull { interaction ->
                        val contact = contacts.find { it.id == interaction.contactId }
                        contact?.let {
                            GridItem.InteractionGridItem(it, interaction.id, interaction.toBadge())
                        }
                    }
                favoriteItems + interactionItems
            }
        }
    }

    fun loadProfile() = viewModelScope.launch {
        profileRepository.getOrCreateDefaultProfile().let {
            _viewState.value = _viewState.value.copy(
                photoUri = it.avatarUri
            )
        }
    }

    fun updateReadContactsPermissionState(isGranted: Boolean) {
        Timber.i("updateReadContactsPermissionState")
        if (readContactsPermissionGranted.value == false && isGranted) {
            Timber.i("updateReadContactsPermissionState reloadData")
            reloadData()
        }
        _readContactsPermissionGranted.value = isGranted
    }

    fun updateGetAccountsPermissionGranted(isGranted: Boolean) {
        _getAccountsPermissionGranted.value = isGranted
    }

    fun updateScreenMode(screenMode: HomeScreenMode) {
        Timber.i("updateScreenMode")
        if (screenMode != viewState.value.mode) _viewState.value = viewState.value.copy(
            mode = screenMode,
        )

        val newGridItems = processContactsForGrid(contacts.value, interactions.value)
        updateHexGridItems(newGridItems)
        if (_hexGridItems.value != newGridItems) {
            Timber.d("Hex grid updated with new items.")
        } else {
            Timber.d("Hex grid update skipped; items are identical.")
        }
    }

    fun updateSearchText(searchText: String) {
        Timber.i("updateSearchText: $searchText")
        _searchQuery.value = searchText
//        _hexGridItems.value = processContactsForGrid(_contacts.value)
//        _hexGridItems.value.size.log { "hexGridItems size" }
    }

    // Helper function to process contacts into the grid format
    private fun processContactsForGrid(contacts: List<Contact>): List<GridItem> {
        log { "processContactsForGrid with mode: ${viewState.value.mode}" }
        return when (viewState.value.mode) {
            is HomeScreenMode.SearchingGrid, HomeScreenMode.SearchingList -> {
                contacts
                    .apply { size.log { "searchingContacts size" } }
                    .map { GridItem.SearchGridItem(it) }
            }
            else -> {
                contacts.filter { it.isFavorite }
                    .map { GridItem.FavoritedContactGridItem(it) as GridItem }
                    .toMutableList().apply {
                        viewModelScope.launch {

                            Timber.i("processContactsForGrid interactions size: ${_interactions.value.size}")
                            addAll(
                                _interactions.value
                                    .filter { interaction ->
                                        contacts.firstOrNull { contact ->
                                            interaction.contactId == contact.id
                                        } != null
                                    }
                                    .map { interaction ->
                                        val contact = contacts.firstOrNull { contact ->
                                            interaction.contactId == contact.id
                                        }
                                        GridItem.InteractionGridItem(
                                            contact = contact!!,
                                            interactionId = interaction.id,
                                            badge = interaction.toBadge()
                                        )
                                    }
                            )
                        }
                    }
            }
        }
    }

    fun onViewAction(viewAction: HomeViewEvent) {
        when (viewAction) {
            is HomeViewEvent.CarouselContactInteractionClick -> viewModelScope.launch {
                viewAction.apply {
                    Timber.i("CarouselContactInteractionClick(contact: ${contact.name}, type: ${interactionType})")
                    if (interactions.value.firstOrNull { it.type == interactionType && it.contactId == contact.id } == null) {
                        val interactionId = interactionRepository.insertInteraction(
                            Interaction(
                                contactId = contact.id,
                                type = viewAction.interactionType
                            )
                        )
//                        val interaction = Interaction(
//                            id = interactionId,
//                            contactId = contact.id,
//                            type = interactionType
//                        )
//                        _interactions.value = _interactions.value.toMutableList().apply {
//                            add(interaction)
//                        }
//                        _hexGridItems.value = _hexGridItems.value.toMutableList().apply {
//                            add(GridItem.InteractionGridItem(
//                                contact = contact,
//                                interactionId = interactionId,
//                                badge = interaction.toBadge()
//                            ))
//                        }
                    }
                    when (viewAction.interactionType) {
                        Interaction.Type.Email -> contact.email?.let { email ->
                            _viewEffectFlow.value = HomeViewEffect.WriteEmail(email)
                        }

                        Interaction.Type.Phone -> {
                            _viewEffectFlow.value = contact.phone?.let { phoneNumber ->
                                HomeViewEffect.CallPhoneNumber(phoneNumber)
                            }
                        }
                    }
                }
            }
            is HomeViewEvent.GridItemButtonRemove -> viewModelScope.launch {
                if (viewAction.gridItem is GridItem.InteractionGridItem) {
                    _interactions.value = _interactions.value.filterNot {
                        it.id == viewAction.gridItem.interactionId
                    }
                }

                _hexGridItems.value = _hexGridItems.value.filterNot { gridItem ->
                    when (viewAction.gridItem) {
                        is GridItem.InteractionGridItem ->
                            gridItem is GridItem.InteractionGridItem && gridItem.interactionId == viewAction.gridItem.interactionId
                        is GridItem.FavoritedContactGridItem ->
                            gridItem is GridItem.FavoritedContactGridItem && gridItem.contact.id == viewAction.gridItem.contact.id
                        else -> false
                    }
                }

                // Perform additional repository actions separately
                when (viewAction.gridItem) {
                    is GridItem.InteractionGridItem ->
                        interactionRepository.deleteInteractionById(viewAction.gridItem.interactionId)
                    is GridItem.FavoritedContactGridItem ->
                        contactsRepository.favoriteContact(viewAction.gridItem.contact.id, isFavorite = false)
                    else -> {}
                }
            }
            HomeViewEvent.HexagonalGridCellLongClick -> when (viewState.value.mode) {
                HomeScreenMode.Normal -> {
                    _viewState.value = _viewState.value.copy(mode = HomeScreenMode.Edit)
                }
                HomeScreenMode.Edit -> {
                    _viewState.value = _viewState.value.copy(mode = HomeScreenMode.Normal)
                }
                else -> {}
            }
            is HomeViewEvent.GridItemClick -> {
                _viewEffectFlow.value = HomeViewEffect.NavigateToExternalProfile(
                    contactId = viewAction.contact.id,
                    offsetX = viewAction.offsetX,
                    offsetY = viewAction.offsetY,
                )
            }
            HomeViewEvent.ClearViewEffect -> _viewEffectFlow.value = null
        }
    }

    private fun <T> StateFlow<T>.toStateFlowWithLogging(tag: String): StateFlow<T> =
        this.onEach { it.log { "$tag emitted" } }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), value)
}

private fun Contact.matchesQuery(query: String) = (name.contains(query, ignoreCase = true)
        || phone?.contains(query, ignoreCase = true) == true
        || email?.contains(query, ignoreCase = true) == true)

private fun Interaction.toBadge(): GridItem.Badge? {
    return when (type) {
        Interaction.Type.Email -> {
            GridItem.Badge.EmailBadge
        }
        Interaction.Type.Phone -> {
            GridItem.Badge.PhoneBadge
        }
        else -> null
    }
}

private fun String.normalize(): String = replace(" ", "")

data class HomeViewState(
    val photoUri: String? = null,
    val defaultShowBottomSheet: Boolean = false,
    val mode: HomeScreenMode = HomeScreenMode.Normal,
)

sealed class HomeViewEvent {
    data class GridItemClick(val contact: Contact, val offsetX: Float, val offsetY: Float): HomeViewEvent()
    data object HexagonalGridCellLongClick: HomeViewEvent()
    data class GridItemButtonRemove(val gridItem: GridItem): HomeViewEvent()
    data class CarouselContactInteractionClick(val contact: Contact, val interactionType: Int): HomeViewEvent()
    data object ClearViewEffect: HomeViewEvent()
}

sealed class HomeViewEffect {
    data class CallPhoneNumber(val phoneNumber: String): HomeViewEffect()
    data class WriteEmail(val emailAddress: String): HomeViewEffect()
    data class NavigateToExternalProfile(val contactId: Long, val offsetX: Float, val offsetY: Float): HomeViewEffect()
}