package com.anynetwork.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anynetwork.app.data.contacts.ContactsRepository
import com.anynetwork.app.data.interaction.InteractionRepository
import com.anynetwork.app.data.order.OrderRepository
import com.anynetwork.app.data.profile.ProfileRepository
import com.anynetwork.app.model.Contact
import com.anynetwork.app.model.Interaction
import com.anynetwork.app.model.Order
import com.anynetwork.app.ui.components.hexagon.HexGridCellPosition
import com.anynetwork.app.ui.utils.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
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
    val interactionRepository: InteractionRepository,
    val orderRepository: OrderRepository
): ViewModel() {
    private var _contactsFetched = true

    private val _readContactsPermissionGranted: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val readContactsPermissionGranted: StateFlow<Boolean?> = _readContactsPermissionGranted

    private val _getAccountsPermissionGranted = MutableStateFlow(false)

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts
        .distinctUntilChanged { old, new ->
            old.isContactListIdenticalTo(new)
        }
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

    private var gridOrder: List<Order> = listOf()

    val gridColumns = 6 * 8
    val gridRows = 6 * 8
    val centralGridPosition = HexGridCellPosition(
        row = gridRows / 2 - 2,
        column = gridColumns / 2 - 1,
        gridRows = gridRows,
        gridColumns = gridColumns,
    )
    val offsetEvenRows = false
    val cellPositions = (0..gridRows * gridColumns).map { getElementPosition(it) }

    init {
        Timber.i("init")
        viewModelScope.launch {
            gridOrder = orderRepository.getOrder()

            _searchQuery
                .debounce(50)
                .collect { query ->
                    withContext(Dispatchers.Default) {
                        searchQuery.value.log { "searching contacts" }
                        if (!viewState.value.mode.isSearching) {
                            _searchContacts.value = contacts.value
                            return@withContext
                        }
                        val filteredContacts = contacts.value
                            .filter {
                                it.matchesQuery(searchQuery.value.log { "filteredContacts with search query" })
                            }
                            .sortedWith(compareBy({ it.name.firstOrNull()?.isLetter() == true }, { it.name }))

                        _searchContacts.value = filteredContacts

                        val newGridItems = processContactsForGrid(filteredContacts, interactions.value)
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

    fun reloadData() = viewModelScope.launch(Dispatchers.Default) {
        try {
            Timber.i("reloadData")
            val latestInteractionsDeferred = async { interactionRepository.getAllInteractions() }
            val latestInteractions = latestInteractionsDeferred.await()
            updateInteractions(latestInteractions)

            // Collect emissions from contacts repository
            contactsRepository.getContacts().collect { emittedContacts ->
                _contactsFetched = true
                updateContacts(emittedContacts)

                if (viewState.value.mode.isSearching) {
                    updateDependentStates(emittedContacts.filter {
                        it.matchesQuery(searchQuery.value.log { "filteredContacts with search query" })
                    }.sortedWith(compareBy({ it.name.firstOrNull()?.isLetter() == true }, { it.name })), emptyList())
                } else {
                    updateDependentStates(emittedContacts, latestInteractions)
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error reloading data")
        }
    }

    private suspend fun updateContacts(emittedContacts: List<Contact>) = withContext(Dispatchers.IO) {
        val distinctContacts = emittedContacts
            .distinctBy { it.id }
            .distinctBy { it.phone?.normalize() }
            .sortedBy { it.name }

        Timber.i("current contacts list size: ${_contacts.value.size}")
        if (_contacts.value != distinctContacts) {
            Timber.i("updateContacts")
            _contacts.value = distinctContacts

            viewModelScope.launch() {
                distinctContacts
                    .filter { it.isFavorite }
                    .forEach { contact ->
                        if (gridOrder.firstOrNull { it.itemId == contact.id && it.itemType == Order.Type.FAVORITE_CONTACT } == null) {
                            val itemOrder = addAtRandomGridPlace(
                                itemType = Order.Type.FAVORITE_CONTACT,
                                itemId = contact.id
                            )
                            gridOrder = gridOrder.toMutableList()
                                .apply {
                                    add(itemOrder)
                                }
                        }
                    }
            }
        }
    }

    private suspend fun updateDependentStates(contacts: List<Contact>, interactions: List<Interaction>) = withContext(Dispatchers.IO) {
        // Update search contacts
        updateSearchContacts(_searchQuery.value)

        viewModelScope.launch {
            val newGridItems = processContactsForGrid(contacts, interactions)
            updateHexGridItems(newGridItems)
            if (_hexGridItems.value != newGridItems) {
                Timber.d("Hex grid updated with new items.")
            } else {
                Timber.d("Hex grid update skipped; items are identical.")
            }
        }
    }

    private fun updateHexGridItems(newItems: List<GridItem>) {
        Timber.i("updateHexGridItems - size: ${newItems.size}")
        _hexGridItems.value = newItems
    }

    // Extension function to compare lists deeply
    private fun List<GridItem>.isIdenticalTo(other: List<GridItem>): Boolean {
        return (this.hashCode() == other.hashCode()).log { "gridItem list isIdenticalTo" }
    }

    private fun List<Contact>.isContactListIdenticalTo(other: List<Contact>): Boolean {
        return (this.hashCode() == other.hashCode()).log { "contact list isIdenticalTo" }
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

    private suspend fun processContactsForGrid(contacts: List<Contact>, interactions: List<Interaction>): List<GridItem> = withContext(Dispatchers.IO) {
        when (viewState.value.mode) {
            is HomeScreenMode.SearchingGrid, HomeScreenMode.SearchingList -> {
                val s = contacts.map { GridItem.SearchGridItem(it) }
                s.toMutableList().apply {
                    addAll(s)
                    addAll(s)
                    addAll(s)
                    addAll(s)
                    addAll(s)
                    addAll(s)
                    addAll(s)
                    addAll(s)
                    addAll(s)
                }
//                contacts.map { GridItem.SearchGridItem(it) }
            }
            else -> {
                contacts
                    .filter { it.isFavorite }
                    .forEach { contact ->
                        if (gridOrder.firstOrNull { it.itemId == contact.id && it.itemType == Order.Type.FAVORITE_CONTACT } == null) {
                            viewModelScope.launch {
                                addAtRandomGridPlace(
                                    itemType = Order.Type.FAVORITE_CONTACT,
                                    itemId = contact.id
                                )
                            }
                        }
                    }
                interactions
                    .forEach { interaction ->
                        if (gridOrder.firstOrNull { it.itemId == interaction.id && it.itemType == Order.Type.INTERACTION } == null) {
                            viewModelScope.launch {
                                addAtRandomGridPlace(
                                    itemType = Order.Type.INTERACTION,
                                    itemId = interaction.id
                                )
                            }
                        }
                    }
                val gridItems = cellPositions.mapIndexed { index, item ->
                    GridItem.EmptyGridItem() as GridItem
                }.toMutableList()
                gridOrder
                    .apply {
                        size.log { "gridOrder size" }
                    }.forEachIndexed { index, order ->
                    contacts.filter { it.isFavorite }
                        .find { it.id == order.itemId }
                        ?.let { GridItem.FavoritedContactGridItem(it) }
                        ?.let { favoriteGridItem ->
                            gridItems[order.order] = favoriteGridItem
                        }
                    interactions.find { it.id == order.itemId && order.itemType == Order.Type.INTERACTION}
                        ?.let { interaction ->
                            val contact = contacts.find { it.id == interaction.contactId }
                            contact?.let {
                                GridItem.InteractionGridItem(it, interaction.id, interaction.toBadge())
                            }
                        }
                        ?.let { interactionGridItem ->
                            gridItems[order.order] = interactionGridItem
                        }
                }
                gridItems
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

    fun updateReadContactsPermissionState(isGranted: Boolean) = viewModelScope.launch {
        Timber.i("updateReadContactsPermissionState")
        if (readContactsPermissionGranted.value == false && isGranted) {
            Timber.i("updateReadContactsPermissionState reloadData")
            try {
                Timber.i("reloadData")
                val latestInteractionsDeferred = async { interactionRepository.getAllInteractions() }
                val latestInteractions = latestInteractionsDeferred.await()
                updateInteractions(latestInteractions)

                // Collect emissions from contacts repository
                contactsRepository.getContacts().collect { emittedContacts ->
                    viewModelScope.launch {
                        _contactsFetched = true
                        val distinctContacts = emittedContacts
                            .distinctBy { it.id }
                            .distinctBy { it.phone?.normalize() }
                            .sortedBy { it.name }

                        Timber.i("current contacts list size: ${_contacts.value.size}")
                        if (_contacts.value != distinctContacts) {
                            Timber.i("updateContacts")
                            _contacts.value = distinctContacts
                            distinctContacts
                                .filter { it.isFavorite }
                                .forEach { contact ->
                                    if (gridOrder.firstOrNull { it.itemId == contact.id && it.itemType == Order.Type.FAVORITE_CONTACT } == null) {
                                        val itemOrder = addAtRandomGridPlace(
                                            itemType = Order.Type.FAVORITE_CONTACT,
                                            itemId = contact.id
                                        )
                                    }
                                }
                        }

                        updateContacts(distinctContacts)

                        updateDependentStates(emittedContacts, latestInteractions)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error reloading data")
            }
        }
        _readContactsPermissionGranted.value = isGranted
    }

    private suspend fun fetchRandomGridPlace(): HexGridCellPosition = withContext(Dispatchers.IO) {
        orderRepository.latestOrder().forEach {
            val randomItemAroundLatestOrder = randomItemAround(
                latestGridPosition = getElementPosition(it.order),
                occupiedPositions = gridOrder.filter { it.itemType != Order.Type.EMPTY }
                    .map {
                        getElementPosition(it.order)
                    }
                    .toMutableSet()
                    .apply {
                        add(centralGridPosition)
                    },
                createCellPosition = ::HexGridCellPosition
            )
            if (randomItemAroundLatestOrder != null) return@withContext randomItemAroundLatestOrder
        }
        return@withContext randomItemAround(
            latestGridPosition = centralGridPosition,
            occupiedPositions = gridOrder.filter { it.itemType != Order.Type.EMPTY }
                .map {
                    getElementPosition(it.order)
                }
                .toMutableSet()
                .apply {
                    add(centralGridPosition)
                },
            createCellPosition = ::HexGridCellPosition
        )!!
    }

    private suspend fun addAtRandomGridPlace(itemType: Int, itemId: Long,): Order {
        val randomNeighborPosition = fetchRandomGridPlace()
        val itemOrder = orderRepository.insertItemOrder(
            itemType = itemType,
            itemId = itemId,
            order = getElementIndex(randomNeighborPosition).log { "element index for random neighbor position" }
        )
        gridOrder = gridOrder.toMutableList()
            .apply {
                add(itemOrder)
            }
        return itemOrder
    }

    fun updateGetAccountsPermissionGranted(isGranted: Boolean) {
        _getAccountsPermissionGranted.value = isGranted
    }

    fun updateScreenMode(screenMode: HomeScreenMode) {
        Timber.i("updateScreenMode")
        if (screenMode != viewState.value.mode) _viewState.value = viewState.value.copy(
            mode = screenMode,
        )

        viewModelScope.launch {
            val newGridItems = processContactsForGrid(if (screenMode is HomeScreenMode.SearchingGrid) searchContacts.value else contacts.value, interactions.value)
            updateHexGridItems(newGridItems)
        }
    }

    fun updateSearchText(searchText: String) {
        Timber.i("updateSearchText: $searchText")
        _searchQuery.value = searchText
//        _hexGridItems.value = processContactsForGrid(_contacts.value)
//        _hexGridItems.value.size.log { "hexGridItems size" }
    }

    fun onViewAction(viewAction: HomeViewEvent) {
        when (viewAction) {
            is HomeViewEvent.CarouselContactInteractionClick -> viewModelScope.launch {
                viewAction.apply {
                    Timber.i("CarouselContactInteractionClick(contact: ${contact.name}, type: ${interactionType})")
                    val interaction = interactions.value
                        .firstOrNull {
                            it.type == interactionType && it.contactId == contact.id
                        }
                    if (interaction == null) {
                        val interaction = Interaction(
                            contactId = contact.id,
                            type = viewAction.interactionType
                        )
                        val interactionId = interactionRepository.insertInteraction(interaction)

                        _interactions.value = interactionRepository.getAllInteractions()
                        val order = addAtRandomGridPlace(
                            itemType = Order.Type.INTERACTION,
                            itemId = interactionId
                        )
                        gridOrder = gridOrder.apply {
                            toMutableList().add(order)
                        }

                    } else {
                        interactionRepository.updateInteractionLastModified(interaction.id)
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
                    if (interaction == null) {
                        val newGridItems = processContactsForGrid(contacts.value, _interactions.value)
                        updateHexGridItems(newGridItems)
                    }
                }
            }
            is HomeViewEvent.BadgeInteractionClick -> viewModelScope.launch {
                viewAction.apply {
                    Timber.i("CarouselContactInteractionClick(contact: ${contact.name}, type: ${interactionType})")
                    val interaction = interactions.value
                        .firstOrNull {
                            it.type == interactionType && it.contactId == contact.id
                        }
                    if (interaction == null) {
                        val interaction = Interaction(
                            contactId = contact.id,
                            type = viewAction.interactionType
                        )
                        val interactionId = interactionRepository.insertInteraction(interaction)

                        _interactions.value = interactionRepository.getAllInteractions()
                        val order = addAtRandomGridPlace(
                            itemType = Order.Type.INTERACTION,
                            itemId = interactionId
                        )
                        gridOrder = gridOrder.apply {
                            toMutableList().add(order)
                        }

                    } else {
                        interactionRepository.updateInteractionLastModified(interaction.id)
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
                    if (interaction == null) {
                        val newGridItems = processContactsForGrid(contacts.value, _interactions.value)
                        updateHexGridItems(newGridItems)
                    }
                }
            }
            is HomeViewEvent.GridItemButtonRemove -> viewModelScope.launch {
                if (viewAction.gridItem is GridItem.InteractionGridItem) {
                    _interactions.value = _interactions.value.filterNot {
                        it.id == viewAction.gridItem.interactionId
                    }
                }

                _hexGridItems.value = _hexGridItems.value.toMutableList().apply {
                    set(hexGridItems.value.indexOf(viewAction.gridItem), GridItem.EmptyGridItem())
                }

                // Perform additional repository actions separately
                when (viewAction.gridItem) {
                    is GridItem.InteractionGridItem ->
                        interactionRepository.deleteInteractionById(viewAction.gridItem.interactionId)
                    is GridItem.FavoritedContactGridItem ->
                        contactsRepository.favoriteContact(viewAction.gridItem.contact!!.id, isFavorite = false)
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
            is HomeViewEvent.MyProfileGridItemClick -> {
                _viewEffectFlow.value = HomeViewEffect.NavigateToMyProfile(
                    offsetX = viewAction.offsetX,
                    offsetY = viewAction.offsetY,
                )
            }
            is HomeViewEvent.GridItemClick -> {
                _viewEffectFlow.value = HomeViewEffect.NavigateToExternalProfile(
                    contactId = viewAction.contact.id,
                    offsetX = viewAction.offsetX,
                    offsetY = viewAction.offsetY,
                )
            }
            is HomeViewEvent.SwapGridItems -> {
                Timber.i("onViewAction SwapGridItems")
                val targetOrder = gridOrder.firstOrNull {
                    (it.itemType == Order.Type.FAVORITE_CONTACT
                            && viewAction.target is GridItem.FavoritedContactGridItem
                            && it.itemId == viewAction.target.contact!!.id) ||
                            (it.itemType == Order.Type.INTERACTION
                                    && viewAction.target is GridItem.InteractionGridItem
                                    && it.itemId == viewAction.target.interactionId)
                }
                val destinationOrder = gridOrder.firstOrNull {
                    (it.itemType == Order.Type.FAVORITE_CONTACT
                            && viewAction.destination is GridItem.FavoritedContactGridItem
                            && it.itemId == viewAction.destination.contact!!.id) ||
                            (it.itemType == Order.Type.INTERACTION
                                    && viewAction.destination is GridItem.InteractionGridItem
                                    && it.itemId == viewAction.destination.interactionId)
                }

                viewModelScope.launch {
                    if (targetOrder != null) {
                        orderRepository.updateItemOrders(
                            listOf(
                                targetOrder.copy(order = viewAction.destinationNewIndex),
                            )
                        )
                    }
                    if (destinationOrder != null) {
                        orderRepository.updateItemOrders(
                            listOf(
                                destinationOrder.copy(order = viewAction.targetNewIndex),
                            )
                        )
                    }

                    gridOrder = orderRepository.getOrder()
                    val newGridItems = processContactsForGrid(contacts.value, interactions.value)
                    updateHexGridItems(newGridItems)
                }
            }
            HomeViewEvent.ClearViewEffect -> _viewEffectFlow.value = null

            HomeViewEvent.ClearNavigationEffect -> _viewEffectFlow.value = null
        }
    }
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
    data class MyProfileGridItemClick(val offsetX: Float, val offsetY: Float): HomeViewEvent()
    data class GridItemClick(val contact: Contact, val offsetX: Float? = null, val offsetY: Float? = null): HomeViewEvent()
    data object HexagonalGridCellLongClick: HomeViewEvent()
    data class GridItemButtonRemove(val gridItem: GridItem): HomeViewEvent()
    data class SwapGridItems(val target: GridItem, val targetNewIndex: Int, val destination: GridItem, val destinationNewIndex: Int): HomeViewEvent()
    data class CarouselContactInteractionClick(val contact: Contact, val interactionType: Int): HomeViewEvent()
    data class BadgeInteractionClick(val contact: Contact, val interactionType: Int): HomeViewEvent()
    data object ClearViewEffect: HomeViewEvent()
    data object ClearNavigationEffect: HomeViewEvent()
}

sealed class HomeViewEffect {
    data class CallPhoneNumber(val phoneNumber: String): HomeViewEffect()
    data class WriteEmail(val emailAddress: String): HomeViewEffect()
    data class NavigateToExternalProfile(val contactId: Long, val offsetX: Float? = null, val offsetY: Float? = null): HomeViewEffect()
    data class NavigateToMyProfile(val offsetX: Float, val offsetY: Float): HomeViewEffect()
}

fun HomeViewModel.createCellPosition(row: Int, column: Int) = HexGridCellPosition(
    column = column,
    row = row,
    gridRows = gridRows,
    gridColumns = gridColumns)

fun HomeViewModel.getElementPosition(index: Int): HexGridCellPosition {
    Timber.i("getElementPosition for index: $index")
    if (index == 0) {
        return centralGridPosition
    }

    var layer = 1
    var count = 1

    // Determine the layer in which the element is located
    while (count + 6 * layer <= index) {
        count += 6 * layer
        layer += 1
    }

    val positionInLayer = if (layer == 1) index - count else (index - count + 1) % (6 * layer)
    val sideLength = layer
    val side = positionInLayer / sideLength
    val offset = positionInLayer % sideLength

    // Modify the relativePosition computation to start from the bottom-left and move counter-clockwise
    val relativePosition = when (side) {
        0 -> {
            if (layer == 1) {
                createCellPosition(0, -1)
            } else {
                createCellPosition(
                    layer - offset,
                    -((layer + 1) / 2 + (offset + if (layer % 2 == 0) 1 else 0) / 2)
                )
            }
        }
        1 -> {
            if (layer == 1) {
                createCellPosition(-1, 0)
            } else {
                createCellPosition(-offset, -(layer - offset + offset / 2))
            }
        }
        2 -> {
            if (layer == 1) {
                createCellPosition(-1, 1)
            } else {
                createCellPosition(-layer, -(layer / 2) + offset)
            }
        }
        3 -> {
            if (layer == 1) {
                createCellPosition(0, 1)
            } else {
                createCellPosition(
                    -layer + offset,
                    (layer + 1) / 2 + if (layer % 2 == 0) (offset + 1) / 2 else offset / 2
                )
            }
        }
        4 -> {
            if (layer == 1) {
                createCellPosition(1, 0)
            } else {
                createCellPosition(offset, layer - (offset + 1) / 2)
            }
        }
        5 -> {
            if (layer == 1) {
                createCellPosition(1, -1)
            } else {
                createCellPosition(layer, layer / 2 - offset)
            }
        }
        else -> createCellPosition(0, 0)
    }

    val adjustedColumn = if (relativePosition.row % 2 != 0 && relativePosition.row > 0) {
        centralGridPosition.column + relativePosition.column + 1
    } else {
        centralGridPosition.column + relativePosition.column
    }

    return createCellPosition(
        column = adjustedColumn,
        row = centralGridPosition.row + relativePosition.row
    )
}

fun HomeViewModel.randomItemAround(
    latestGridPosition: HexGridCellPosition,
    occupiedPositions: Set<HexGridCellPosition>,
    createCellPosition: (column: Int, row: Int) -> HexGridCellPosition
): HexGridCellPosition? {
    // Define the six relative neighbors in axial coordinates
    val adjustColumn = if (latestGridPosition.row % 2 != 0 && latestGridPosition.row > 0) -1 else 0
    val neighbors = listOf(
        createCellPosition(0 + adjustColumn, -1),  // Top-left
        createCellPosition(-1, 0), // Left
        createCellPosition(0 + adjustColumn, 1), // Bottom-left
        createCellPosition(1 + adjustColumn, 1),  // Bottom-right
        createCellPosition(1, 0),  // Right
        createCellPosition(1 + adjustColumn, -1) // Top-right
    )

    // Calculate absolute positions of the neighbors
    val neighborPositions = neighbors.map {
        createCellPosition(
            column = latestGridPosition.column + it.column,
            row = latestGridPosition.row + it.row
        )
    }

    // Filter out occupied positions
    val unoccupiedPositions = neighborPositions.filter { it !in occupiedPositions }

    if (unoccupiedPositions.isEmpty()) {
        // No valid positions available
        return null
    }

    // Select a random unoccupied position
    val randomPosition = unoccupiedPositions.random()

    // Add the new item to the occupied positions
    // (Assumes this function caller updates the occupied positions)
    return randomPosition
}

fun HomeViewModel.getElementIndex(position: HexGridCellPosition): Int {
    position.getIndex().log { "getElementIndex" }
    // If it's the central position, return 0
    return cellPositions.indexOf(position)
}

private fun HomeViewModel.isPositionInLayer(position: HexGridCellPosition, layer: Int): Boolean {
    val relativePosition = calculateRelativePosition(position, layer)
    return relativePosition.isWithinLayerBounds(layer)
}

private fun HexGridCellPosition.isWithinLayerBounds(layer: Int): Boolean {
    val maxDistance = layer
    val axialDistance = Math.abs(this.row) + Math.abs(this.column) + Math.abs(this.column + this.row)
    return axialDistance / 2 == maxDistance
}

private fun HomeViewModel.calculateRelativePosition(position: HexGridCellPosition, layer: Int): HexGridCellPosition {
    val rowOffset = position.row - centralGridPosition.row
    val columnOffset = position.column - centralGridPosition.column

    return createCellPosition(
        row = rowOffset,
        column = columnOffset
    )
}



