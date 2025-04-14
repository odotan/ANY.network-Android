@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.anynetwork.app.ui.screens.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.inputmethod.InputMethodManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults.DragHandle
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import coil.size.Size
import com.anynetwork.app.R
import com.anynetwork.app.model.Interaction
import com.anynetwork.app.ui.components.SHEET_VALUE_COLLAPSED
import com.anynetwork.app.ui.components.SHEET_VALUE_EXPANDED
import com.anynetwork.app.ui.components.SHEET_VALUE_PARTIALLY_EXPANDED
import com.anynetwork.app.ui.components.Screen
import com.anynetwork.app.ui.components.SearchTextField
import com.anynetwork.app.ui.components.SheetValue
import com.anynetwork.app.ui.components.SwipeUpToContinue
import com.anynetwork.app.ui.components.dialog.AlertDialog
import com.anynetwork.app.ui.components.dialog.AlertDialogButtonState
import com.anynetwork.app.ui.components.dialog.Message
import com.anynetwork.app.ui.components.dialog.MessageAction
import com.anynetwork.app.ui.components.hexagon.AutoresizeTextContentStyle
import com.anynetwork.app.ui.components.hexagon.ChangeScale
import com.anynetwork.app.ui.components.hexagon.CoverBox
import com.anynetwork.app.ui.components.hexagon.EmptyHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.HexagonalGrid
import com.anynetwork.app.ui.components.hexagon.IconHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.ImageHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.NontransparentHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.NontransparentHexagonContentStyle.Background
import com.anynetwork.app.ui.components.hexagon.RemovableStrategy
import com.anynetwork.app.ui.components.hexagon.RoundedHexagon
import com.anynetwork.app.ui.components.hexagon.RoundedPolygonShape
import com.anynetwork.app.ui.components.hexagon.TransparentHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.TrashCanHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.calculateLayersForElements
import com.anynetwork.app.ui.components.hexagon.createPolygon
import com.anynetwork.app.ui.components.hexagon.generateHexagonColors
import com.anynetwork.app.ui.components.hexagon.hexCellsBackgroundColorsGrid
import com.anynetwork.app.ui.navigation.Route
import com.anynetwork.app.ui.screens.externalprofile.VerticalLine
import com.anynetwork.app.ui.screens.home.HomeViewEvent.BadgeInteractionClick
import com.anynetwork.app.ui.screens.home.HomeViewEvent.CarouselContactInteractionClick
import com.anynetwork.app.ui.screens.home.HomeViewEvent.ClearViewEffect
import com.anynetwork.app.ui.screens.home.HomeViewEvent.GridItemButtonRemove
import com.anynetwork.app.ui.screens.home.HomeViewEvent.GridItemClick
import com.anynetwork.app.ui.screens.home.HomeViewEvent.HexagonalGridCellLongClick
import com.anynetwork.app.ui.screens.home.HomeViewEvent.MyProfileGridItemClick
import com.anynetwork.app.ui.screens.home.HomeViewEvent.SwapGridItems
import com.anynetwork.app.ui.theme.DarkBlue
import com.anynetwork.app.ui.theme.GreenColor
import com.anynetwork.app.ui.theme.PrimaryColor
import com.anynetwork.app.ui.utils.SP_HOME_ANCHORED_DRAGGABLE_INITIAL_REVEALED
import com.anynetwork.app.ui.utils.SP_HOME_ANCHORED_STATE
import com.anynetwork.app.ui.utils.SP_HOME_GRID_ZOOM
import com.anynetwork.app.ui.utils.SP_HOME_GRID_ZOOM_OFFSET_X
import com.anynetwork.app.ui.utils.SP_HOME_GRID_ZOOM_OFFSET_Y
import com.anynetwork.app.ui.utils.SP_NAME
import com.anynetwork.app.ui.utils.checkSelfPermission
import com.anynetwork.app.ui.utils.csp
import com.anynetwork.app.ui.utils.fdph
import com.anynetwork.app.ui.utils.fdpv
import com.anynetwork.app.ui.utils.log
import com.anynetwork.app.ui.utils.xdph
import com.anynetwork.app.ui.utils.xdpv
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


@Composable
fun HomeRoot(
    viewModel: HomeViewModel = hiltViewModel<HomeViewModel>(),
    homeNavController: NavHostController
) {

    Home(
        viewModel,
        onCreateNewContactClick = {
            homeNavController.navigate(Route.NewContact(it))
        }
    )

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    fun hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        focusManager.clearFocus()
        imm.hideSoftInputFromWindow(null, 0)
    }

    LaunchedEffect(viewModel.viewEffectFlow) {
        viewModel.viewEffectFlow.collect { viewEffect ->
            viewEffect.log { "viewEffect" }
            when (viewEffect) {
                is HomeViewEffect.WriteEmail -> {
                    val email = viewEffect.emailAddress
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:$email")
                    }
                    context.startActivity(
                        Intent.createChooser(intent, "Send Email")
                    )
                }
                is HomeViewEffect.CallPhoneNumber -> {
                    val phoneNumber = Uri.parse("tel:${viewEffect.phoneNumber}")
                    val callIntent = Intent(Intent.ACTION_DIAL, phoneNumber)
                    context.startActivity(callIntent)
                }
                is HomeViewEffect.NavigateToExternalProfile -> {
                    viewModel.onViewAction(ClearViewEffect)
                    hideKeyboard()
                    homeNavController.navigate(
                        if (viewEffect.offsetX != null && viewEffect.offsetY != null) Route.ExternalProfile(
                            id = viewEffect.contactId,
                            offsetX = viewEffect.offsetX,
                            offsetY = viewEffect.offsetY,
                        ) else Route.ExternalProfileNotExploding(id = viewEffect.contactId)
                    ) {
                        launchSingleTop = true
                    }
                }
                is HomeViewEffect.NavigateToMyProfile -> {
                    viewModel.onViewAction(ClearViewEffect)
                    homeNavController.navigate(Route.MyProfile(
                        offsetX = viewEffect.offsetX,
                        offsetY = viewEffect.offsetY,
                    ))
                }
                else -> {}
            }
            viewModel.onViewAction(ClearViewEffect)
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val currentLifecycleOwner = rememberUpdatedState(lifecycleOwner)

    LaunchedEffect(Unit) {
        currentLifecycleOwner.value.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
            Timber.i("home start animation created")
            viewModel.reloadData()
            viewModel.loadProfile()
        }
    }
}

var showHomeCover = true
enum class BottomSheetOffsetMode {
    Automatic,
    Manual
}

sealed class HomeScreenMode(val isSearching: Boolean) {
    data object Normal: HomeScreenMode(isSearching = false)
    data object Edit: HomeScreenMode(isSearching = false)
    data object SearchingList: HomeScreenMode(isSearching = true)
    data object SearchingGrid: HomeScreenMode(isSearching = true)
}

@Composable
private fun Home(
    viewModel: HomeViewModel,
    onCreateNewContactClick: (String?) -> Unit,
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
    val toolbarHeight = TopAppBarDefaults.LargeAppBarCollapsedHeight

    var centralOffset: Offset? by remember { mutableStateOf(null) }
    var centralIndex: Int? by remember { mutableStateOf(null) }
    var centralHeight: Int? by rememberSaveable { mutableStateOf(null) }

    var collapsedOffset by rememberSaveable { mutableStateOf(0f) }
    var partiallyExpandedOffset by rememberSaveable { mutableStateOf(0f) }
    var expandedOffset by rememberSaveable { mutableStateOf(0f) }
    var currentOffset by rememberSaveable { mutableStateOf(0f) }
    var fullOffset by rememberSaveable { mutableStateOf(0f) }

    var bottomSheetCurrentState by rememberSaveable { mutableStateOf(BottomSheetOffsetMode.Automatic) }
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showAllowContactsPermissionsDialog by remember { mutableStateOf(false) }

    val pillHazeState = remember { HazeState() }
    val pillHazeStyle = HazeStyle(
        backgroundColor = DarkBlue,
        tints = listOf(HazeTint(Color.White.copy(alpha = .10f))),
        blurRadius = 8.dp,
    )

    val sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    val anchoredDraggableState = remember {
        AnchoredDraggableState(
            initialValue = SheetValue.Collapsed,
            positionalThreshold = { 0f },
            velocityThreshold = { 0f },
            snapAnimationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium,
            ),
            decayAnimationSpec = exponentialDecay(),
            confirmValueChange = { sheetValue ->
                Timber.i("confirmValueChange to ${sheetValue}")
                when (sheetValue) {
                    SheetValue.Collapsed -> sharedPreferences.edit().putInt(SP_HOME_ANCHORED_STATE, SHEET_VALUE_COLLAPSED).apply()
                    SheetValue.PartiallyExpanded -> sharedPreferences.edit().putInt(SP_HOME_ANCHORED_STATE, SHEET_VALUE_PARTIALLY_EXPANDED).apply()
                    SheetValue.Expanded -> sharedPreferences.edit().putInt(SP_HOME_ANCHORED_STATE, SHEET_VALUE_EXPANDED).apply()
                    else -> {}
                }

                true
            }
        )}

    val viewState by viewModel.viewState.collectAsState()

    val screenMode by remember {
        derivedStateOf {
            viewState.mode
                .apply { log { "screenMode" } }
        }
    }

    var isGridCentered by remember {
        mutableStateOf(false)
    }
    var isGridZoomDefault by remember {
        mutableStateOf(false)
    }

    val centerMessageAlphaAnimationDuration = 1500
    val showBottomSheetAnimationDuration = 1500

    val dragPercentage = remember {
        derivedStateOf {
            val percentage = when {
                collapsedOffset != expandedOffset ->
                    ((currentOffset - collapsedOffset) / (expandedOffset - collapsedOffset)) * 100f
                else -> 0f
            }.absoluteValue.log { "percentage" }
            percentage.coerceIn(0f, 100f) // Ensure percentage is between 0 and 100
        }
    }

    LaunchedEffect(dragPercentage.value) {
        dragPercentage.value.log { "dragPercentage" }
    }

    LaunchedEffect(Unit) {
        val sheetValue = when {
            screenMode.isSearching -> SheetValue.Full
            sharedPreferences.contains(SP_HOME_ANCHORED_STATE) -> {
                when (sharedPreferences.getInt(SP_HOME_ANCHORED_STATE, SHEET_VALUE_PARTIALLY_EXPANDED)) {
                    SHEET_VALUE_COLLAPSED -> SheetValue.Collapsed
                    SHEET_VALUE_EXPANDED -> SheetValue.Expanded
                    else -> SheetValue.PartiallyExpanded
                }
            }
            else -> SheetValue.PartiallyExpanded
        }
        if (expandedOffset != 0f) {
            Timber.i("onSizeChanged")
            val newAnchors = DraggableAnchors {
                if (!screenMode.isSearching) {
                    SheetValue.Collapsed at collapsedOffset
                    SheetValue.PartiallyExpanded at partiallyExpandedOffset
                    SheetValue.Expanded at expandedOffset
                } else {
                    SheetValue.Full at fullOffset
                }
            }
            anchoredDraggableState.updateAnchors(
                newAnchors,
                sheetValue
            )
        }
        viewModel.updateReadContactsPermissionState(
            checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
        )

        delay(900)

        showBottomSheet = true

        if (!sharedPreferences.getBoolean(SP_HOME_ANCHORED_DRAGGABLE_INITIAL_REVEALED, false)) {
            sharedPreferences.edit().putBoolean(SP_HOME_ANCHORED_DRAGGABLE_INITIAL_REVEALED, true).apply()
            delay(800 + centerMessageAlphaAnimationDuration.toLong())
            anchoredDraggableState.animateTo(sheetValue)
            if (bottomSheetCurrentState == BottomSheetOffsetMode.Automatic) {
                bottomSheetCurrentState = BottomSheetOffsetMode.Manual
            }
        }
    }

    var layoutHeight = 0

    val bottomSheetHazeState = remember { HazeState() }

    fun onFinishSearching() {
        val newAnchors = DraggableAnchors {
            with(density) {
                SheetValue.Collapsed at collapsedOffset
                SheetValue.PartiallyExpanded at partiallyExpandedOffset
                SheetValue.Expanded at expandedOffset
                SheetValue.Full at fullOffset
            }
        }
        anchoredDraggableState.updateAnchors(
            newAnchors,
            anchoredDraggableState.currentValue
        )
        coroutineScope.launch {
            viewModel.updateScreenMode(screenMode = HomeScreenMode.Normal)
            viewModel.updateSearchText("")
            val sheetValue = if (sharedPreferences.contains(SP_HOME_ANCHORED_STATE)) {
                when (sharedPreferences.getInt(SP_HOME_ANCHORED_STATE, SHEET_VALUE_PARTIALLY_EXPANDED)) {
                    SHEET_VALUE_COLLAPSED -> SheetValue.Collapsed
                    SHEET_VALUE_EXPANDED -> SheetValue.Expanded
                    else -> SheetValue.PartiallyExpanded
                }
            } else SheetValue.PartiallyExpanded
            anchoredDraggableState.animateTo(sheetValue)
            val newAnchors = DraggableAnchors {
                with(density) {
                    SheetValue.Collapsed at collapsedOffset
                    SheetValue.PartiallyExpanded at partiallyExpandedOffset
                    SheetValue.Expanded at expandedOffset
                }
            }
            anchoredDraggableState.updateAnchors(
                newAnchors,
                sheetValue
            )
        }
    }

    BackHandler(screenMode.isSearching) {
        onFinishSearching()
    }

    Screen(
        modifier = Modifier
            .fillMaxSize()
            .haze(state = pillHazeState),
        topBar = HomeToolbar(
            screenMode = screenMode,
            onBackPress = {
                onFinishSearching()
            },
            onActionClick = {
                viewModel.updateScreenMode(
                    if (screenMode is HomeScreenMode.SearchingGrid)
                        HomeScreenMode.SearchingList
                    else
                        HomeScreenMode.SearchingGrid
                )
            }
        ),
        hexagonGrid = {
            val gridColumns = viewModel.gridColumns
            val gridRows = viewModel.gridRows
            val offsetEvenRows = viewModel.offsetEvenRows
            val defaultZoomScale = gridColumns / 4.7f
            val initialOffset = remember {
                when {
                    sharedPreferences.contains(SP_HOME_GRID_ZOOM_OFFSET_X) && sharedPreferences.contains(
                        SP_HOME_GRID_ZOOM_OFFSET_Y
                    ) && !viewState.mode.isSearching -> Offset(
                        x = sharedPreferences.getFloat(SP_HOME_GRID_ZOOM_OFFSET_X, 0f),
                        y = sharedPreferences.getFloat(SP_HOME_GRID_ZOOM_OFFSET_Y, 0f)
                    )

                    else -> Offset.Zero
                }
            }
            val initialScale = remember {
                when {
                    viewState.mode.isSearching -> 1f
                    sharedPreferences.contains(SP_HOME_GRID_ZOOM) ->
                        sharedPreferences.getFloat(SP_HOME_GRID_ZOOM, defaultZoomScale)
                    else -> defaultZoomScale
                }
            }

            val gridScaling = remember { 4f }
            val centralPosition = viewModel.centralGridPosition
            var changeScale: ChangeScale? by remember {
                mutableStateOf(null)
            }
            LaunchedEffect(screenMode) {
                changeScale = if (screenMode is HomeScreenMode.SearchingGrid) {
                    ChangeScale(
                        scale = 1f,
                        position = Offset.Zero
                    )
                } else {
                    ChangeScale(
                        scale = sharedPreferences.getFloat(SP_HOME_GRID_ZOOM, defaultZoomScale),
                        position = Offset(
                            sharedPreferences.getFloat(SP_HOME_GRID_ZOOM_OFFSET_X, 0f),
                            sharedPreferences.getFloat(SP_HOME_GRID_ZOOM_OFFSET_Y, 0f)
                        )
                    )
                }
            }
            LaunchedEffect(isGridCentered) {
                changeScale = if (isGridCentered) {
                    sharedPreferences.edit().putFloat(SP_HOME_GRID_ZOOM_OFFSET_X, 0f).apply()
                    sharedPreferences.edit().putFloat(SP_HOME_GRID_ZOOM_OFFSET_Y, 0f).apply()
                    val scale = if (sharedPreferences.contains(SP_HOME_GRID_ZOOM)) {
                        sharedPreferences.getFloat(SP_HOME_GRID_ZOOM, defaultZoomScale)
                    } else defaultZoomScale
                    ChangeScale(
                        scale = scale,
                        position = Offset.Zero
                    )
                } else {
                    null
                }
            }
            LaunchedEffect(isGridZoomDefault) {
                changeScale = if (isGridCentered) {
                    sharedPreferences.edit().putFloat(SP_HOME_GRID_ZOOM, defaultZoomScale).apply()
                    ChangeScale(
                        scale = defaultZoomScale,
                        position = Offset.Zero
                    )
                } else {
                    null
                }
            }

            centralIndex = centralPosition.getIndex()

            val hexGridContacts by viewModel.hexGridItems.collectAsState()
            val optimizedHexGridContacts by remember {
                derivedStateOf {
                    hexGridContacts.size.log { "optimizedHexGridContacts size" }
                    hexGridContacts
                }
            }

            val backgroundColorsGrid by remember {
                derivedStateOf {
                    val colors = generateHexagonColors(
                        hexagonRows = gridRows,
                        hexagonCols = gridColumns,
                        colorGrid = hexCellsBackgroundColorsGrid,
                        startRow = viewModel.centralGridPosition.row - 4,
                        startCol = viewModel.centralGridPosition.column - 2,
                        offsetEvenRows = offsetEvenRows
                    )
                    colors
                }
            }
            val optimizedPhotoUri by remember {
                derivedStateOf {
                    viewState.photoUri.log { "optimizedPhotoUri" }
                }
            }

            val currentConfig = LocalConfiguration.current
            val screenWidthDp by remember { derivedStateOf { currentConfig.screenWidthDp } }
            val (cellWidth, cellHeight) = remember(screenWidthDp, gridScaling, gridColumns) {
                val calculatedWidth = screenWidthDp.toFloat() / 2
                val calculatedHeight = calculatedWidth * 96.99f/86.93f

                Timber.i("cell width/height: $calculatedWidth/ ${calculatedHeight.roundToInt()}")
                calculatedWidth.roundToInt() to calculatedHeight.roundToInt()
            }
            // Find the central element
            val items = remember(optimizedPhotoUri, optimizedHexGridContacts) {
                Timber.i("home start animation reload items")
                val list = (0 until gridRows * gridColumns).map { index ->
                    val row = index / gridColumns
                    val column = index % gridColumns
                    val cellIndex = viewModel.createCellPosition(column = column, row = row).getIndex()
                    val backgroundColor = backgroundColorsGrid[row][column]

                    if (cellIndex == centralIndex && !screenMode.isSearching) {
                        optimizedPhotoUri.log { "" }
                        if (optimizedPhotoUri != null) {
                            ImageHexagonContentStyle(
                                id = cellIndex,
                                isHoverable = false,
                                image = ImageHexagonContentStyle.Image.FromUri(optimizedPhotoUri!!),
                                onClick = {
                                    viewModel.onViewAction(
                                        MyProfileGridItemClick(
                                            offsetX = it.x,
                                            offsetY = it.y
                                        )
                                    )
                                },
                                onLongClick = { viewModel.onViewAction(HexagonalGridCellLongClick) },
                            )
                        } else {
                            ImageHexagonContentStyle(
                                id = cellIndex,
                                background = Background.SingleColor(PrimaryColor),
                                isHoverable = false,
                                image = ImageHexagonContentStyle.Image.VectorResource(
                                    id = R.drawable.ic_profile,
                                    size = Size(580, 660),
                                    fractionOfParentSize = .33f
                                ),
                                onClick = {
                                    viewModel.onViewAction(
                                        MyProfileGridItemClick(
                                            offsetX = it.x,
                                            offsetY = it.y
                                        )
                                    )
                                },
                                onLongClick = { viewModel.onViewAction(HexagonalGridCellLongClick) },
                            )
                        }
                    } else {
                        val contactForCell = viewModel.cellPositions.take(optimizedHexGridContacts.size)
                            .find { it.getIndex() == cellIndex }


                        val cellPositionIndex = viewModel.cellPositions.indexOfFirst { it.row == row && it.column == column }
                        val cellLayer = calculateLayersForElements(cellPositionIndex)
                        val isOutsideHexGridHexagon = cellLayer > gridColumns / 2 - 2 || cellPositionIndex == -1

                        if (!isOutsideHexGridHexagon) {
                            if (contactForCell != null) {
                                val gridItem =
                                    optimizedHexGridContacts[viewModel.cellPositions.indexOf(
                                        contactForCell
                                    )]

                                if (gridItem !is GridItem.EmptyGridItem) {
//                                val gridItem = optimizedHexGridContacts[viewModel.cellPositions.indexOf(contactForCell)]
                                    val onClick: (Offset) -> Unit = { offset ->
                                        gridItem.contact?.let {
                                            when (gridItem.badge) {
                                                is GridItem.Badge.PhoneBadge ->
                                                    viewModel.onViewAction(
                                                        BadgeInteractionClick(
                                                            contact = gridItem.contact,
                                                            interactionType = Interaction.Type.Phone
                                                        )
                                                    )

                                                is GridItem.Badge.EmailBadge ->
                                                    viewModel.onViewAction(
                                                        BadgeInteractionClick(
                                                            contact = gridItem.contact,
                                                            interactionType = Interaction.Type.Email
                                                        )
                                                    )

                                                else -> viewModel.onViewAction(
                                                    GridItemClick(
                                                        contact = gridItem.contact,
                                                        offsetX = offset.x,
                                                        offsetY = offset.y,
                                                    )
                                                )
                                            }
                                        }
                                    }
                                    if (gridItem.contact?.avatarUri != null)
                                        ImageHexagonContentStyle(
                                            id = cellIndex,
                                            background = Background.SingleColor(backgroundColor),
                                            image = ImageHexagonContentStyle.Image.FromUri(
                                                uri = gridItem.contact.avatarUri,
                                                size = Size(width = cellWidth, height = cellHeight)
                                            ),
                                            onLongClick = {
                                                viewModel.onViewAction(
                                                    HexagonalGridCellLongClick
                                                )
                                            },
                                            isShakable = true,
                                            isDraggable = gridItem !is GridItem.SearchGridItem,
                                            removableStrategy = RemovableStrategy {
                                                viewModel.onViewAction(
                                                    GridItemButtonRemove(gridItem = gridItem)
                                                )
                                            },
                                            onClick = onClick,
                                            badgeOverlay = when (gridItem.badge) {
                                                is GridItem.Badge.PhoneBadge ->
                                                    NontransparentHexagonContentStyle.BadgeOverlay.PhoneBadge(
                                                        badge = gridItem.badge,
                                                        gridColumns = gridColumns,
                                                        gridScaling = gridScaling,
                                                        onClick = {
                                                            viewModel.onViewAction(
                                                                BadgeInteractionClick(
                                                                    contact = gridItem.contact,
                                                                    interactionType = Interaction.Type.Phone
                                                                )
                                                            )
                                                        }
                                                    )

                                                is GridItem.Badge.EmailBadge ->
                                                    NontransparentHexagonContentStyle.BadgeOverlay.EmailBadge(
                                                        badge = gridItem.badge,
                                                        gridColumns = gridColumns,
                                                        gridScaling = gridScaling,
                                                        onClick = {
                                                            viewModel.onViewAction(
                                                                BadgeInteractionClick(
                                                                    contact = gridItem.contact,
                                                                    interactionType = Interaction.Type.Email
                                                                )
                                                            )
                                                        }
                                                    )

                                                is GridItem.Badge.FavoriteBadge ->
                                                    NontransparentHexagonContentStyle.BadgeOverlay.FavoriteBadge(
                                                        badge = gridItem.badge,
                                                        gridColumns = gridColumns,
                                                        gridScaling = gridScaling,
                                                        onClick = {
                                                            viewModel.onViewAction(
                                                                GridItemClick(
                                                                    contact = gridItem.contact,
                                                                )
                                                            )
                                                        }
                                                    )

                                                else -> null
                                            }
                                        )
                                    else AutoresizeTextContentStyle(
                                        id = cellIndex,
                                        background = Background.SingleColor(backgroundColor),
                                        isDraggable = gridItem !is GridItem.SearchGridItem,
                                        removableStrategy = RemovableStrategy {
                                            viewModel.onViewAction(
                                                GridItemButtonRemove(gridItem = gridItem)
                                            )
                                        },
                                        text = gridItem.contact!!.name.uppercase(),
                                        maxTextSize = 11.csp * (configuration.screenWidthDp.dp / gridColumns / 79.93f.fdpv) * gridScaling,
                                        onClick = onClick,
                                        badgeOverlay = when (gridItem.badge) {
                                            is GridItem.Badge.PhoneBadge ->
                                                NontransparentHexagonContentStyle.BadgeOverlay.PhoneBadge(
                                                    badge = gridItem.badge,
                                                    gridColumns = gridColumns,
                                                    gridScaling = gridScaling,
                                                    onClick = {
                                                        viewModel.onViewAction(
                                                            BadgeInteractionClick(
                                                                contact = gridItem.contact,
                                                                interactionType = Interaction.Type.Phone
                                                            )
                                                        )
                                                    }
                                                )

                                            is GridItem.Badge.EmailBadge ->
                                                NontransparentHexagonContentStyle.BadgeOverlay.EmailBadge(
                                                    badge = gridItem.badge,
                                                    gridColumns = gridColumns,
                                                    gridScaling = gridScaling,
                                                    onClick = {
                                                        viewModel.onViewAction(
                                                            BadgeInteractionClick(
                                                                contact = gridItem.contact,
                                                                interactionType = Interaction.Type.Email
                                                            )
                                                        )
                                                    }
                                                )

                                            is GridItem.Badge.FavoriteBadge ->
                                                NontransparentHexagonContentStyle.BadgeOverlay.FavoriteBadge(
                                                    badge = gridItem.badge,
                                                    gridColumns = gridColumns,
                                                    gridScaling = gridScaling,
                                                    onClick = {
                                                        viewModel.onViewAction(
                                                            GridItemClick(
                                                                contact = gridItem.contact,
                                                            )
                                                        )
                                                    }
                                                )

                                            else -> null
                                        }
                                    )
                                } else {
                                    if (cellPositionIndex % 11 == 0) {
                                        TrashCanHexagonContentStyle(
                                            id = cellIndex,
                                            background = Background.SingleColor(backgroundColor)
                                        )
                                    } else {
                                        EmptyHexagonContentStyle(
                                            id = cellIndex,
                                            background = Background.SingleColor(backgroundColor),
                                        )
                                    }
                                }
                            } else {
                                EmptyHexagonContentStyle(
                                    id = cellIndex,
                                    background = Background.SingleColor(backgroundColor),
                                )
                            }
                        } else {
                            TransparentHexagonContentStyle(id = cellIndex)
                        }
                    }
                }
                Timber.i("home start animation reload items completed")
                list
            }


            LaunchedEffect(items) {
                items.filterIsInstance<NontransparentHexagonContentStyle>().size.log { "items size" }
            }

            HexagonalGrid(
                modifier = Modifier
                    .haze(state = bottomSheetHazeState),
                itemsList = items,
                rowSize = gridColumns,
                minScale = 2f,
//                maxScale = initialScale,
                changeScale = changeScale,
                initialScale = initialScale,
                initialOffset = initialOffset,
                onCellPositionCalculated = remember {{ index, offset, width, height ->
                    if (index == centralIndex) {
                        if (centralOffset == null) centralOffset = offset.positionInRoot()
                        centralHeight = height
                        Timber.i("home start animation cell position calculated for central cell")
                    }
                }},
                onZoom = remember {{ zoom, offset ->
                    Timber.i("onZoom: zoom - $zoom, offset - $offset")
                    if (!screenMode.isSearching) {
                        isGridCentered = false
                        isGridZoomDefault = false
                        sharedPreferences.edit().putFloat(SP_HOME_GRID_ZOOM, zoom).apply()
                        sharedPreferences.edit().putFloat(SP_HOME_GRID_ZOOM_OFFSET_X, offset.x)
                            .apply()
                        sharedPreferences.edit().putFloat(SP_HOME_GRID_ZOOM_OFFSET_Y, offset.y)
                            .apply()
                    }
                }},
                isScrollEnabled = true,
                offsetY = 0,
                offsetEvenRows = offsetEvenRows,
                gridScaling = gridScaling,
                onPlacesSwap = { target, destination ->
                    viewModel.cellPositions.take(optimizedHexGridContacts.size).let {
                        val targetCellPosition = it.indexOf(
                            it.find {
                                it.getIndex() == target
                            }
                        )
                        val targetGridItem = optimizedHexGridContacts[targetCellPosition]
                        val destinationCellPosition = it.indexOf(
                            it.find {
                                it.getIndex() == destination
                            }
                        )
                        val destinationGridItem = optimizedHexGridContacts[destinationCellPosition]

                        viewModel.onViewAction(
                            SwapGridItems(
                                target = targetGridItem,
                                targetNewIndex = targetCellPosition,
                                destination = destinationGridItem,
                                destinationNewIndex = destinationCellPosition
                            )
                        )
                    }
                }
            )
        },
        content = {
            if (centralHeight != null) Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
            ) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(systemBarsPadding)
                        .background(color = DarkBlue)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = toolbarHeight)
                        .height(54.fdpv)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    DarkBlue.copy(alpha = 0.99f),
                                    DarkBlue.copy(alpha = 0f)
                                )
                            )
                        )
                )
            }

            val searchText by viewModel.searchQuery.collectAsState()

            val backgroundAlpha by animateFloatAsState(
                targetValue = if (screenMode.isSearching) 1f else 0f,
                animationSpec = tween(800)
            )
            val background = DarkBlue.copy(alpha = backgroundAlpha)
            val hazeState = remember { HazeState() }

            val showBottomSheetAnimationAlpha by animateFloatAsState(
                targetValue = if (showBottomSheet) 1f else 0f,
                animationSpec = tween(showBottomSheetAnimationDuration)
            )
            LaunchedEffect(showBottomSheetAnimationAlpha) {
                showBottomSheetAnimationAlpha.log { "showBottomSheetAnimationAlpha" }
            }
            AnimatedVisibility(
                visible = screenMode !is HomeScreenMode.SearchingGrid,
                enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                exit = fadeOut(animationSpec = tween(durationMillis = 300))
            ) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .alpha(showBottomSheetAnimationAlpha)
                    .background(background)
                    .navigationBarsPadding()
                    .offset {
                        val sheetOffsetY = anchoredDraggableState
                            .offset
                            .toInt()
                        //                            currentOffset = sheetOffsetY.toFloat()
                        IntOffset(x = 0, y = sheetOffsetY)
                    }
                    .anchoredDraggable(
                        anchoredDraggableState,
                        orientation = Orientation.Vertical
                    )
                    .onSizeChanged { sheetSize ->
                        layoutHeight = sheetSize.height
                        if (expandedOffset == 0f) {
                            Timber.i("onSizeChanged")
                            val newAnchors = DraggableAnchors {
                                with(density) {
                                    expandedOffset = 275.fdpv.toPx()
                                    partiallyExpandedOffset = 399.fdpv.toPx()
                                    collapsedOffset = (layoutHeight - 135.fdpv.toPx())
                                    fullOffset =
                                        systemBarsPadding.toPx() + TopAppBarDefaults.LargeAppBarCollapsedHeight.toPx()

                                    SheetValue.Collapsed at collapsedOffset
                                    SheetValue.PartiallyExpanded at partiallyExpandedOffset
                                    SheetValue.Expanded at expandedOffset
                                }
                            }
                            anchoredDraggableState.updateAnchors(
                                newAnchors,
                                anchoredDraggableState.targetValue
                            )
                        }
                    }
                ) {
                    BottomSheet(
                        viewModel = viewModel,
                        screenMode = screenMode,
                        hazeState = hazeState,
                        bottomSheetHazeState = bottomSheetHazeState,
                        syncButtonClick = {
                            showAllowContactsPermissionsDialog = true
                        },
                        onCreateNewContactClick = {
                            onCreateNewContactClick.invoke(it)
                        },
                        searchText = searchText,
                        anchoredDraggableState = anchoredDraggableState
                    )
                }
            }

            if (screenMode.isSearching) Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(),
                verticalArrangement = Arrangement.Bottom
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp)
                ) {
                    SearchTextField(
                        modifier = Modifier
                            .hazeChild(
                                hazeState,
                                style = HazeStyle(
                                    backgroundColor = Color.White.copy(alpha = .5f),
                                    blurRadius = 10.dp,
                                    tint = HazeTint(Color.White.copy(alpha = .5f))

                                )
                            )
                            .navigationBarsPadding()
                            .padding(horizontal = 16.fdpv, vertical = 16.fdph)
                            .height(56.xdpv),
                        value = searchText,
                        onValueChange = {
                            viewModel.updateSearchText(it)
                        },
                        trailingIcon = {
                            if (searchText.isNotEmpty()) {
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        modifier = Modifier.clickable {
                                            onCreateNewContactClick.invoke(searchText)
                                        },
                                        text = "Add",
                                        color = GreenColor,
                                        fontSize = TextUnit(value = 15f, type = TextUnitType.Sp)
                                    )

                                    Spacer(modifier = Modifier.width(16.xdph))
                                }
                            }
                        },
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .align(Alignment.BottomCenter)
                    .clickable {  }
            )
        }
    )

    val showPill = remember(screenMode) { screenMode is HomeScreenMode.Normal }
    if (showPill) Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 31.fdpv)
                .navigationBarsPadding()
                .fillMaxWidth()
                .height(48.fdpv)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.Center),
                colors = CardColors(Color.Transparent, Color.Transparent, Color.Transparent, Color.Transparent),
                shape = RoundedCornerShape(
                    topStart = 24.fdph,
                    topEnd = 24.fdph,
                    bottomEnd = 24.fdph,
                    bottomStart = 24.fdph
                )
            ) {
                Row(
                    modifier = Modifier
                        .hazeChild(
                            state = pillHazeState,
                            style = pillHazeStyle
                        )
                        .padding(horizontal = 9.fdph),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        modifier = Modifier,
                        onClick = {
                            val newAnchors = DraggableAnchors {
                                with(density) {
                                    SheetValue.Collapsed at collapsedOffset
                                    SheetValue.PartiallyExpanded at partiallyExpandedOffset
                                    SheetValue.Expanded at expandedOffset
                                    SheetValue.Full at fullOffset
                                }
                            }
                            anchoredDraggableState.updateAnchors(
                                newAnchors,
                                anchoredDraggableState.currentValue
                            )
                            coroutineScope.launch {
                                anchoredDraggableState.animateTo(SheetValue.Full)
                                anchoredDraggableState.updateAnchors(
                                    newAnchors,
                                    SheetValue.Full
                                )
                                viewModel.updateScreenMode(HomeScreenMode.SearchingList)
                            }
                        }
                    ) {
                        Image(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 10.fdpv),
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = "back button",
                        )
                    }

                    VerticalLine()

                    IconButton(
                        modifier = Modifier,
                        onClick = {
                            when {
                                !isGridCentered -> isGridCentered = true
                                else -> isGridZoomDefault = true
                            }
                        }
                    ) {
                        Image(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 10.fdpv),
                            painter = painterResource(R.drawable.ic_locate),
                            contentDescription = "back button",
                        )
                    }
                }
            }
        }
    }

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        viewModel.updateReadContactsPermissionState(
            isGranted = permissions[Manifest.permission.READ_CONTACTS] ?: false
        )
    }

    rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        viewModel.updateGetAccountsPermissionGranted(
            isGranted = permissions[Manifest.permission.GET_ACCOUNTS] ?: false
        )
    }
    if (showAllowContactsPermissionsDialog) {
        AlertDialog(
            title = stringResource(R.string.contact_permissions_dialog_title),
            message = stringResource(R.string.contact_permissions_dialog_subtitle),
            buttons = listOf(
                AlertDialogButtonState(stringResource(R.string.contact_permissions_dialog_button_dont_allow)),
                AlertDialogButtonState(
                    title = stringResource(R.string.contact_permissions_dialog_button_allow),
                    textColor = GreenColor,
                    fontWeight = FontWeight.SemiBold
                ) {
                    permissionsLauncher.launch(
                        arrayOf(
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.WRITE_CONTACTS,
                        )
                    )
                }
            ),
            onDismiss = {
                showAllowContactsPermissionsDialog = false
            })
    }

    if (showHomeCover && centralOffset != null) {
        AnimatedCover()
    }
}

@Composable
fun AnimatedCover() {
    var isCoverVisible by remember { mutableStateOf(true) }
    val coverAnimationDuration = 500
    val alpha by animateFloatAsState(
        targetValue = if (isCoverVisible.log { "isCoverVisible" }) 1f else 0f,
        animationSpec = tween(coverAnimationDuration),
        finishedListener = {
            showHomeCover = false
            Timber.i("home start animation cover revealed")
        }
    )
    CoverBox(alpha)
    LaunchedEffect(Unit) {
        Timber.i("home start animation reveal cover")
        isCoverVisible = false
    }
}

@Composable
fun BottomSheet(
    viewModel: HomeViewModel,
    screenMode: HomeScreenMode,
    hazeState: HazeState,
    bottomSheetHazeState: HazeState,
    syncButtonClick: () -> Unit,
    onCreateNewContactClick: (String?) -> Unit,
    searchText: String,
    anchoredDraggableState: AnchoredDraggableState<SheetValue>
) {
    Timber.i("BottomSheet recomposed")
    val readContactsPermissionGranted by viewModel.readContactsPermissionGranted.collectAsState()

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    var showCreateYourContactCardMessage by remember { mutableStateOf(!sharedPreferences.getBoolean("createYourContactCardMessageShown", false)) }
    var showSyncContactsMessage by remember { mutableStateOf(!sharedPreferences.getBoolean("syncContactsMessageShown", false)) }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(36.dp, 36.dp, 0.dp, 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .hazeChild(
                    state = bottomSheetHazeState,
                    style = HazeStyle(
                        backgroundColor = Color(0xFF1C1A23).copy(alpha = 1f),
                        blurRadius = 10.dp,
                        tint = HazeTint(Color(0xFF1C1A23).copy(alpha = 0.8f))
                    )
                )
                .align(Alignment.CenterHorizontally)
        ) {
            AnimatedVisibility(visible = !screenMode.isSearching) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        DragHandle()
                    }
                }
            }

            val contacts by viewModel.contacts.collectAsState()

            if (readContactsPermissionGranted == false) {
                if (showSyncContactsMessage) {
                    SwipeUpToContinue(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(horizontal = 16.fdph)
                            .padding(top = 6.fdpv)
                    )

                    Message(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .padding(horizontal = 16.fdph)
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        icon = {
                            val polygon = remember { createPolygon() }
                            val roundedPolygonShape =
                                remember { RoundedPolygonShape(polygon) }
                            RoundedHexagon(
                                modifier = Modifier
                                    .padding(start = 19.fdph, top = 46.fdpv)
                                    .width(56.71.fdph)
                                    .height(64.2.fdpv)
                                    .graphicsLayer {
                                        clip = true
                                        shape = roundedPolygonShape
                                    },
                                contentStyle = IconHexagonContentStyle(
                                    id = 0,
                                    background = Background.SingleColor(
                                        GreenColor
                                    ),
                                    image = IconHexagonContentStyle.Image.VectorResource(
                                        R.drawable.ic_contact
                                    )
                                )
                            )
                        },
                        title = "Sync contacts?",
                        description = "Enjoy a new contacts experience.\n\nYour contacts will not be shared with ANY network without your consent.",
                        action = MessageAction("Sync") {
                            syncButtonClick.invoke()
                        },
                        onCloseClick = {
                            sharedPreferences.edit()
                                .putBoolean("syncContactsMessageShown", true)
                                .apply()
                            showSyncContactsMessage = false
                        }
                    )
                }

                if (contacts.isEmpty()) {
                    Image(
                        modifier = Modifier
                            .padding(vertical = 8.9.fdpv)
                            .padding(horizontal = 16.fdph)
                            .width(263.fdph)
                            .height(67.18.fdpv)
                            .padding(start = 16.fdph),
                        painter = painterResource(id = R.drawable.shimmer_contact),
                        contentDescription = null
                    )
                }
            }

            val searchContacts by viewModel.searchContacts.collectAsState()
            val searchResultContacts by remember(searchContacts) {
                derivedStateOf {
                    searchContacts
                }
            }

            val listState = rememberLazyListState()
            LazyColumn(
                modifier = Modifier
                    .haze(hazeState)
                    .graphicsLayer { alpha = 0.99f }
                    .drawWithContent {
                        // Draw the composable content first
                        drawContent()

                        // Apply fading effect at the edges using a gradient
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent, Color.Black,
                                    Color.Black, Color.Black, Color.Black,
                                    Color.Black, Color.Black, Color.Black,
                                    Color.Black, Color.Black, Color.Black,
                                    Color.Black
                                ),
                                startY = 0f,
                                endY = size.height
                            ),
                            blendMode = BlendMode.DstIn
                        )
                    }
                    .imePadding(),
                state = listState,
                userScrollEnabled = anchoredDraggableState.currentValue == SheetValue.Expanded || anchoredDraggableState.currentValue == SheetValue.Full
            ) {
                item {
                    Spacer(modifier = Modifier.height(36.fdpv))
                }
                if (searchResultContacts.isEmpty() && contacts.isNotEmpty()) item {
                    EmptyScreen(
                        searchText = searchText,
                        onCreateNewContactClick = {
                            onCreateNewContactClick.invoke(searchText)
                        }
                    )
                }
                if (showCreateYourContactCardMessage && readContactsPermissionGranted == true) item {
                    Message(
                        modifier = Modifier
                            .padding(horizontal = 16.fdph)
                            .padding(vertical = 16.dp)
                            .fillMaxWidth()
                            .height(132.fdpv),
                        icon = {
                            Image(
                                modifier = Modifier
                                    .padding(start = 31.fdph, top = 35.fdpv)
                                    .width(33.fdph)
                                    .height(42.67.fdpv),
                                painter = painterResource(id = R.drawable.ic_bell),
                                contentDescription = null
                            )
                        },
                        title = "Create your contact card?",
                        description = "Tap your hexagon in the grid to create or edit your card. Tap other hexagons to add or edit contacts.",
                        onCloseClick = {
                            sharedPreferences.edit().putBoolean(
                                "createYourContactCardMessageShown",
                                true
                            ).apply()
                            showCreateYourContactCardMessage = false
                        }
                    )
                }
                items(
                    searchResultContacts.size,
                    key = { index -> searchResultContacts[index].id }
                ) {
                    val contact = searchResultContacts[it]
                    ContactsRow(
                        modifier = Modifier
                            .clickable {
                                viewModel.onViewAction(
                                    GridItemClick(contact = contact,)
                                )
                            }
                            .padding(horizontal = 16.fdph)
                            .padding(vertical = 8.9.fdpv)
                            .animateItem(),
                        contact = contact,
                        onClick = {
                            viewModel.onViewAction(
                                GridItemClick(contact = contact,)
                            )
                        },
                        onInteractionClick = { contact, interactionType ->
                            viewModel.onViewAction(
                                CarouselContactInteractionClick(
                                    contact = contact,
                                    interactionType = interactionType
                                )
                            )
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(if (!screenMode.isSearching) 300.fdpv else 160.xdpv))
                }
            }
        }
    }
}