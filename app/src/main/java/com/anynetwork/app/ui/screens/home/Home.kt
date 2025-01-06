@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.anynetwork.app.ui.screens.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults.DragHandle
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import coil.size.Size
import com.anynetwork.app.R
import com.anynetwork.app.model.Contact
import com.anynetwork.app.ui.components.Screen
import com.anynetwork.app.ui.components.SearchTextField
import com.anynetwork.app.ui.components.SheetValue
import com.anynetwork.app.ui.components.SwipeUpToContinue
import com.anynetwork.app.ui.components.dialog.AlertDialog
import com.anynetwork.app.ui.components.dialog.AlertDialogButtonState
import com.anynetwork.app.ui.components.dialog.Message
import com.anynetwork.app.ui.components.dialog.MessageAction
import com.anynetwork.app.ui.components.hexagon.Badge
import com.anynetwork.app.ui.components.hexagon.ChangeScale
import com.anynetwork.app.ui.components.hexagon.CoverBox
import com.anynetwork.app.ui.components.hexagon.CustomHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.DeleteButton
import com.anynetwork.app.ui.components.hexagon.EmptyHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.HexGridCellPosition
import com.anynetwork.app.ui.components.hexagon.HexagonalGrid
import com.anynetwork.app.ui.components.hexagon.IconHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.ImageHexagonContentStyle
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
import com.anynetwork.app.ui.theme.DarkBlue
import com.anynetwork.app.ui.theme.EmailColor
import com.anynetwork.app.ui.theme.GreenColor
import com.anynetwork.app.ui.theme.PrimaryColor
import com.anynetwork.app.ui.theme.YellowColor
import com.anynetwork.app.ui.theme.montserratFontFamily
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.absoluteValue


@Composable
fun HomeRoot(navController: NavHostController, viewModel: HomeViewModel = hiltViewModel<HomeViewModel>()) {

    Home(
        viewModel,
        onMyProfileClick = {
            navController.navigate(Route.MyProfile)
        },
        onCreateNewContactClick = {
            navController.navigate(Route.NewContact(it))
        }
    )

    val context = LocalContext.current
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
                    navController.navigate(
                        Route.ExternalProfile(
                            id = viewEffect.contactId,
                            offsetX = viewEffect.offsetX,
                            offsetY = viewEffect.offsetY,
                        )
                    ) {
                        launchSingleTop = true
                    }
                }
                else -> {}
            }
            viewModel.onViewAction(HomeViewEvent.ClearViewEffect)
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

val anchoredDraggableState = AnchoredDraggableState(
    initialValue = SheetValue.Collapsed,
    positionalThreshold = { 0f },
    velocityThreshold = { 0f },
    snapAnimationSpec = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium,
    ),
    decayAnimationSpec = exponentialDecay()
)
@Composable
private fun Home(
    viewModel: HomeViewModel,
    onMyProfileClick: () -> Unit,
    onCreateNewContactClick: (String?) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            Timber.i("home start animation start")
            viewModel.reloadData()
            viewModel.loadProfile()
        }
    }

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
    val contacts by viewModel.contacts.collectAsState()

    val viewState by viewModel.viewState.collectAsState()
    LaunchedEffect(Unit) {
        snapshotFlow { viewState }
            .collectLatest { newValue ->
                viewState.log { "viewState new value" }
            }
    }

    val screenMode by remember {
        derivedStateOf {
            viewState.mode
                .apply { log { "screenMode" } }
        }
    }

    var isGridCentered by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        snapshotFlow { screenMode }
            .collectLatest { newValue ->
                screenMode.log { "screenMode new value" }
            }
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
        viewModel.updateReadContactsPermissionState(
            checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
        )

        delay(900)

        showBottomSheet = true
        delay(800 + centerMessageAlphaAnimationDuration.toLong())

        if (bottomSheetCurrentState == BottomSheetOffsetMode.Automatic) {
            anchoredDraggableState.animateTo(SheetValue.PartiallyExpanded)
            bottomSheetCurrentState = BottomSheetOffsetMode.Manual
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
            anchoredDraggableState.animateTo(SheetValue.PartiallyExpanded)
            val newAnchors = DraggableAnchors {
                with(density) {
                    SheetValue.Collapsed at collapsedOffset
                    SheetValue.PartiallyExpanded at partiallyExpandedOffset
                    SheetValue.Expanded at expandedOffset
                }
            }
            anchoredDraggableState.updateAnchors(
                newAnchors,
                SheetValue.PartiallyExpanded
            )
        }
    }

    BackHandler(screenMode.isSearching) {
        onFinishSearching()
    }

    Screen(
        modifier = Modifier
            .fillMaxSize(),
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
            val gridColumns = remember { 6 * 8 }
            val gridRows = remember { 6 * 8 }
            val centralRowIndex = remember { gridRows / 2 - 2 }
            val centralColumnIndex = remember { gridColumns / 2 - 1 }
            val offsetEvenRows = false
            val initialScale = (gridColumns / 4.7f).log { "initialScale" }
            val gridScaling = remember { 4f }
            val centralPosition = remember {
                HexGridCellPosition(
                    column = centralColumnIndex,
                    row = centralRowIndex,
                    gridRows = gridRows,
                    gridColumns = gridColumns
                )
            }
            var changeScale: ChangeScale? by remember {
                mutableStateOf(null)
            }
            LaunchedEffect(isGridCentered) {
                changeScale = if (isGridCentered) ChangeScale(
                    scale = initialScale,
                    position = Offset(0f, 0f)
                ) else {
                    null
                }
            }

            centralIndex = centralPosition.getIndex()

            fun createCellPosition(row: Int, column: Int) = HexGridCellPosition(
                    column = column,
                    row = row,
                    gridRows = gridRows,
                    gridColumns = gridColumns)

            fun getElementPosition(index: Int): HexGridCellPosition {
                Timber.i("getElementPosition for index: $index")
                if (index == 0) {
                    return centralPosition
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
                    centralPosition.column + relativePosition.column + 1
                } else {
                    centralPosition.column + relativePosition.column
                }

                return createCellPosition(
                    column = adjustedColumn,
                    row = centralPosition.row + relativePosition.row
                )
            }

            val hexGridContacts by viewModel.hexGridItems.collectAsState()
            val optimizedHexGridContacts by remember {
                derivedStateOf {
                    hexGridContacts.log { "optimizedHexGridContacts" }
                }
            }

            val backgroundColorsGrid by remember {
                derivedStateOf {
                    val colors = generateHexagonColors(
                        hexagonRows = gridRows,
                        hexagonCols = gridColumns,
                        colorGrid = hexCellsBackgroundColorsGrid,
                        startRow = centralRowIndex - 4,
                        startCol = centralColumnIndex - 2,
                        offsetEvenRows = offsetEvenRows
                    )
                    colors
                }
            }
            val optimizedPhotoUri by remember {
                derivedStateOf { viewState.photoUri }
            }



//            val contactListPosition = rememberSaveable(hexGridContacts, screenMode) {
//                Timber.i("contactListPosition update")
//                hexGridContacts.mapIndexed { index, _ ->
//                    val hexPosition = getElementPosition(index + if (screenMode.isSearching) 0 else 1)
////                    Timber.i("contact $index to ${hexPosition.row}:${hexPosition.column}")
//
//                    hexPosition
//                }
//            }

            val cellsPositions = rememberSaveable {
                (0..gridRows * gridColumns).map { getElementPosition(it) }
            }

            // Find the central element
            val items = remember(cellsPositions, optimizedPhotoUri, optimizedHexGridContacts) {
                Timber.i("home start animation reload items")
                val list = List(gridRows) { row -> // Create a list with `gridRows` elements
                    List(gridColumns) { column ->  // Each element is a list with `gridColumns` elements
                        val cellIndex = createCellPosition(
                            column = column,
                            row = row,
                        ).getIndex()

                        val backgroundColor = backgroundColorsGrid[row][column]

                        if (cellIndex == centralIndex && !screenMode.isSearching) {
                            optimizedPhotoUri.log { "" }
                            if (optimizedPhotoUri != null) {
                                ImageHexagonContentStyle(
                                    id = cellIndex,
                                    isHoverable = false,
                                    image = ImageHexagonContentStyle.Image.FromUri(optimizedPhotoUri!!),
                                    onClick = {
                                        onMyProfileClick.invoke()
                                    },
                                    onLongClick = {
                                        viewModel.onViewAction(HomeViewEvent.HexagonalGridCellLongClick)
                                    },
                                )
                            } else {
                                CustomHexagonContentStyle(
                                    id = cellIndex,
                                    background = Background.SingleColor(PrimaryColor),
                                    isHoverable = false,
                                    content = {
                                        Image(
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .fillMaxSize(0.4f)
                                                .align(Alignment.Center),
                                            painter = rememberAsyncImagePainter(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(R.drawable.ic_profile)
                                                    .size(Size.ORIGINAL)
                                                    .build()
                                            ),
                                            contentScale = ContentScale.FillWidth,
                                            contentDescription = null,
                                        )
                                    },
                                    onClick = { offset ->
                                        onMyProfileClick.invoke()
                                    },
                                    onLongClick = {
                                        viewModel.onViewAction(HomeViewEvent.HexagonalGridCellLongClick)
                                    },
                                )
                            }
                        } else {
                            val contactForCell =
                                cellsPositions.take(optimizedHexGridContacts.size).find {
                                    it.row == row && it.column == column
                                }

                            if (contactForCell != null) {
                                val gridItem = optimizedHexGridContacts[cellsPositions.indexOf(
                                    contactForCell
                                )]
                                CustomHexagonContentStyle(
                                    id = cellIndex,
                                    background = Background.SingleColor(backgroundColor),
                                    isDraggable = gridItem !is GridItem.SearchGridItem,
                                    content = { scale ->
                                            if (gridItem.contact.avatarUri != null) {
                                                Image(
                                                    modifier = Modifier
                                                        .fillMaxSize(),
                                                    painter = rememberAsyncImagePainter(
                                                        model = ImageRequest.Builder(LocalContext.current)
                                                            .data(gridItem.contact.avatarUri)
                                                            .size(Size.ORIGINAL)
                                                            .scale(scale = Scale.FILL)
                                                            .build()
                                                    ),
                                                    contentScale = ContentScale.Crop,
                                                    contentDescription = null,
                                                )
                                            } else {
                                                Text(
                                                    modifier = Modifier.align(Alignment.Center),
                                                    text = gridItem.contact
                                                        .getDisplayNameFirstLetters()
                                                        .uppercase(),
                                                    textAlign = TextAlign.Center,
                                                    color = Color(0xFFAFAEB8),
                                                    style = TextStyle(
                                                        fontFamily = montserratFontFamily,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = 24.csp * (LocalConfiguration.current.screenWidthDp.dp / gridColumns / 79.93f.fdpv) * scale * gridScaling,
                                                    )
                                                )
                                            }
//                                        Text(
//                                            modifier = Modifier.align(Alignment.Center),
//                                            text = "${contactListPosition.indexOf(contactForCell)}\n" +
//                                                    "${calculateLayersForElements(contactListPosition.indexOf(contactForCell))}",
//                                            textAlign = TextAlign.Center,
//                                            color = Color(0xFFAFAEB8),
//                                            style = TextStyle(
//                                                fontFamily = montserratFontFamily,
//                                                fontWeight = FontWeight.SemiBold,
//                                                fontSize = 18.csp * (LocalConfiguration.current.screenWidthDp.dp / gridColumns / 79.93f.fdpv) * scale,
//                                            )
//                                        )
                                    },
                                    onClick = { offset ->
                                        viewModel.onViewAction(
                                            HomeViewEvent.GridItemClick(
                                                contact = gridItem.contact,
                                                offsetX = offset.x,
                                                offsetY = offset.y,
                                            )
                                        )
//                                        onContactClick.invoke(gridItem.contact, it)
                                    },
                                    onLongClick = {
                                        viewModel.onViewAction(HomeViewEvent.HexagonalGridCellLongClick)
                                    },
                                    isShakable = true,
                                    removableStrategy = RemovableStrategy {
                                        viewModel.onViewAction(
                                            HomeViewEvent.GridItemButtonRemove(
                                                gridItem = gridItem
                                            )
                                        )
                                    },
                                    overlay = gridItem.badge?.let {
                                        {
                                            if (screenMode is HomeScreenMode.Edit) DeleteButton(
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(
                                                        top = 10.fdpv * (LocalConfiguration.current.screenWidthDp.dp / gridColumns / 79.93f.fdpv * gridScaling),
                                                        end = 11.fdph * (LocalConfiguration.current.screenWidthDp.dp / gridColumns / 79.93f.fdpv * gridScaling)
                                                    )
                                                    .size(24.fdpv * (LocalConfiguration.current.screenWidthDp.dp / gridColumns / 79.93f.fdpv * gridScaling)),
                                                onClick = {
                                                    viewModel.onViewAction(
                                                        HomeViewEvent.GridItemButtonRemove(
                                                            gridItem
                                                        )
                                                    )
                                                }
                                            )

                                            Badge(
                                                Modifier
                                                    .align(Alignment.BottomEnd)
                                                    .padding(
                                                        bottom = 10.fdpv * (LocalConfiguration.current.screenWidthDp.dp / gridColumns / 79.93f.fdpv * gridScaling),
                                                        end = 11.fdph * (LocalConfiguration.current.screenWidthDp.dp / gridColumns / 79.93f.fdpv * gridScaling)
                                                    )
                                                    .size(24.fdpv * (LocalConfiguration.current.screenWidthDp.dp / gridColumns / 79.93f.fdpv * gridScaling))
                                                    .zIndex(2f),
                                                color = it.color,
                                                iconResourceId = it.iconResId,
                                                iconColorFilter = it.iconColorFilter,
                                                onClick = {
                                                    when (it) {
                                                        is GridItem.Badge.PhoneBadge -> {
                                                            val mobilePhone =
                                                                gridItem.contact.phone
                                                            val number =
                                                                Uri.parse("tel:$mobilePhone")
                                                            val callIntent =
                                                                Intent(
                                                                    Intent.ACTION_DIAL,
                                                                    number
                                                                )
                                                            context.startActivity(callIntent)
                                                        }

                                                        is GridItem.Badge.EmailBadge -> {
                                                            val email = gridItem.contact.email
                                                            val intent =
                                                                Intent(Intent.ACTION_SENDTO)
                                                            intent.putExtra(
                                                                Intent.EXTRA_EMAIL,
                                                                email
                                                            )
                                                            intent.type = "text/plain"
                                                            intent.data =
                                                                Uri.parse("mailto:$email")

                                                            context.startActivity(
                                                                Intent.createChooser(
                                                                    intent,
                                                                    "Send Email"
                                                                )
                                                            )
                                                        }

                                                        else -> {}
                                                    }
                                                }
                                            )
                                        }
                                    } ?: {}
                                )
                            } else {
                                val cellPosition = cellsPositions.find {
                                    it.row == row && it.column == column
                                }
                                val cellLayer = calculateLayersForElements(cellsPositions.indexOf(cellPosition))
                                val isOutsideHexGridHexagon = cellLayer > gridColumns / 2 - 2 || cellsPositions.indexOf(cellPosition) == -1
                                if (isOutsideHexGridHexagon) {
                                    TransparentHexagonContentStyle(id = cellIndex)
                                } else if (cellsPositions.indexOf(cellPosition) % 12 == 0) {
                                    TrashCanHexagonContentStyle(
                                        id = cellIndex,
                                        background = Background.SingleColor(backgroundColor)
                                    )
                                } else {
                                    EmptyHexagonContentStyle(
                                        id = cellIndex,
                                        background = Background.SingleColor(
                                            backgroundColor
                                        ),
                                    )
                                }
                            }

                        }
                    }.toMutableList()
                }.toMutableList()

                Timber.i("home start animation reload items completed")
                list
            }


            HexagonalGrid(
                modifier = Modifier
                    .haze(state = bottomSheetHazeState),
                items = items,
                rowSize = gridColumns,
                minScale = 2f,
//                maxScale = initialScale,
                changeScale = changeScale,
                initialScale = initialScale,
                onCellPositionCalculated = remember {{ index, offset, width, height ->
                    if (index == centralIndex) {
                        if (centralOffset == null) centralOffset = offset
                        centralHeight = height
                        Timber.i("home start animation cell position calculated for central cell")
                    }
                }},
                onZoom = remember {{ zoom, offset ->
                    Timber.i("onZoom: zoom - $zoom, offset - $offset")
                    isGridCentered = false
                }},
                isScrollEnabled = true,
                offsetY = 0,
                offsetEvenRows = offsetEvenRows,
                gridScaling = gridScaling
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
                    BoxWithConstraints(
                        modifier = Modifier
                            .alpha(showBottomSheetAnimationAlpha)
                            .drawBehind {
                                drawRect(background)
                            }
                    ) {
                        layoutHeight = constraints.maxHeight

                        Box(modifier = Modifier
                            .fillMaxSize()
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
                                if (expandedOffset == 0f) {
                                    Timber.i("onSizeChanged")
                                    val newAnchors = DraggableAnchors {
                                        with(density) {
                                            expandedOffset = 275.fdpv.toPx()
                                            partiallyExpandedOffset = 399.fdpv.toPx()
                                            collapsedOffset = (layoutHeight - 120.fdpv.toPx())
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
                                searchText = searchText,
                                hazeState = hazeState,
                                bottomSheetHazeState = bottomSheetHazeState,
                                centerGridClick = {
                                    isGridCentered = true
                                },
                                syncButtonClick = {
                                    showAllowContactsPermissionsDialog = true
                                },
                                searchButtonClick = {
                                    val newAnchors = DraggableAnchors {
                                        with(density) {
                                            SheetValue.Collapsed at collapsedOffset
                                            SheetValue.PartiallyExpanded at partiallyExpandedOffset
                                            SheetValue.Expanded at expandedOffset
                                            SheetValue.Full at fullOffset
                                        }
                                    }
//                                                    anchoredDraggableState.updateAnchors(
//                                                        newAnchors,
//                                                        anchoredDraggableState.currentValue
//                                                    )
                                    coroutineScope.launch {
                                        anchoredDraggableState.animateTo(SheetValue.Full)
                                        anchoredDraggableState.updateAnchors(
                                            newAnchors,
                                            SheetValue.Full
                                        )
                                        viewModel.updateScreenMode(HomeScreenMode.SearchingList)
                                    }
                                },
                                onCreateNewContactClick = {
                                    onCreateNewContactClick.invoke(it)
                                }
                            )
                        }
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
                    var searchTextValue by remember { mutableStateOf(searchText) }
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
                            .padding(horizontal = 16.fdpv, vertical = 16.fdph)
                            .height(56.xdpv),
                        onValueChange = {
                            searchTextValue = it
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
        }
    )

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
//                viewModel.reloadData()
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
    searchButtonClick: () -> Unit,
    centerGridClick: () -> Unit,
    syncButtonClick: () -> Unit,
    onCreateNewContactClick: (String?) -> Unit,
    searchText: String
) {
    Timber.i("BottomSheet recomposed")
    val readContactsPermissionGranted by viewModel.readContactsPermissionGranted.collectAsState()

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
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
                .padding(horizontal = 16.fdph)
                .align(Alignment.CenterHorizontally)
        ) {
            if (!screenMode.isSearching) Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    DragHandle()
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.fdpv,)
                        .align(Alignment.BottomCenter)
                ) {
                    IconButton(
                        modifier = Modifier
                            .size(48.fdpv)
                            .clip(CircleShape)
                            .background(Color(0xFFD9D9D9).copy(alpha = 0.1f)),
                        onClick = {
                            searchButtonClick.invoke()
                        }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = "Start Button"
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        modifier = Modifier
                            .size(48.fdpv)
                            .clip(CircleShape)
                            .background(Color(0xFFD9D9D9).copy(alpha = 0.1f)),
                        onClick = {
                            centerGridClick.invoke()
                        }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_locate),
                            contentDescription = "Center",
                        )
                    }
                }
            }

            val contacts by viewModel.contacts.collectAsState()
            LaunchedEffect(contacts) {
                snapshotFlow { contacts }
                    .collectLatest { newValue ->
                        newValue.size.log { "contacts new value size" }
                    }
            }
            if (readContactsPermissionGranted == false) {
                if (showSyncContactsMessage) {
                    SwipeUpToContinue(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 6.fdpv)
                    )

                    Message(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .fillMaxWidth()
                            .height(185.fdpv),
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
                            .width(263.fdph)
                            .height(67.18.fdpv)
                            .padding(start = 16.fdph),
                        painter = painterResource(id = R.drawable.shimmer_contact),
                        contentDescription = null
                    )
                }
            }

            val searchContacts by viewModel.searchContacts.collectAsState()
            val searchResultContacts by remember(/*screenMode, */searchContacts, contacts) {
                derivedStateOf {
                    if (screenMode.isSearching) searchContacts else contacts
                }
            }

            LaunchedEffect(searchResultContacts) {
                snapshotFlow { searchResultContacts }
                    .collectLatest { newValue ->
                        newValue.size.log { "searchResultContacts new value size" }
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
                    },
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
                    { index -> searchResultContacts[index].id }
                ) {
                    val contact = searchResultContacts[it]
                    key(contact) {
                        ContactsRow(
                            modifier = Modifier
                                .padding(vertical = 8.9.fdpv)
                                .animateItem(),
                            contact = contact,
                            onClick = {
                                viewModel.onViewAction(
                                    HomeViewEvent.GridItemClick(
                                        contact = contact,
                                        offsetX = 0f,
                                        offsetY = 0f,
                                    )
                                )
                            },
                            onInteractionClick = { contact, interactionType ->
                                viewModel.onViewAction(
                                    HomeViewEvent.CarouselContactInteractionClick(
                                        contact = contact,
                                        interactionType = interactionType
                                    )
                                )
                            }
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(300.fdpv))
                }
            }
        }
    }
}

sealed class GridItem(val contact: Contact, val badge: Badge? = null) {
    class FavoritedContactGridItem(contact: Contact): GridItem(contact, Badge.FavoriteBadge)
    class InteractionGridItem(contact: Contact, val interactionId: Long, badge: Badge?): GridItem(contact, badge) {
        override fun equals(other: Any?): Boolean {
            if (other !is InteractionGridItem || !super.equals(other)) return false
            val identicalInteractionIds = interactionId == other.interactionId

            return identicalInteractionIds
        }
    }
    class SearchGridItem(contact: Contact): GridItem(contact, null)

    sealed class Badge(open val color: Color, open val iconResId: Int, open val iconColorFilter: ColorFilter? = null) {
        data object FavoriteBadge: Badge(
            color = YellowColor,
            iconResId = R.drawable.ic_star_filled,
            iconColorFilter = ColorFilter.tint(color = Color.White)
        )
        data object EmailBadge: Badge(
            color = EmailColor,
            iconResId = R.drawable.ic_email,
        )
        data object PhoneBadge: Badge(
            color = GreenColor,
            iconResId = R.drawable.ic_phone,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (other !is GridItem) return false

        val identicalContacts = contact == other.contact
        val identicalBadges = badge == other.badge

        return identicalContacts && identicalBadges
    }
}