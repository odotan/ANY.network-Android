@file:OptIn(ExperimentalFoundationApi::class, ExperimentalAnimatedInsets::class)

package com.anynetwork.app.ui.screens.externalprofile

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.anynetwork.app.R
import com.anynetwork.app.ui.base.NavigateBack
import com.anynetwork.app.ui.components.HexagonTextField
import com.anynetwork.app.ui.components.HexagonTextFieldClearTrailingIcon
import com.anynetwork.app.ui.components.NavigationIconState
import com.anynetwork.app.ui.components.Screen
import com.anynetwork.app.ui.components.SearchTextField
import com.anynetwork.app.ui.components.SheetValue
import com.anynetwork.app.ui.components.ToolbarState
import com.anynetwork.app.ui.components.ToolbarStateTitle
import com.anynetwork.app.ui.components.dialog.DropDownDialogMenuCategory
import com.anynetwork.app.ui.components.dialog.ExpandableDrillDownMenu
import com.anynetwork.app.ui.components.hexagon.CustomHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.DeleteButton
import com.anynetwork.app.ui.components.hexagon.EmptyHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.HexGridCellPosition
import com.anynetwork.app.ui.components.hexagon.HexGridCellPosition.Neighbor.BottomLeft
import com.anynetwork.app.ui.components.hexagon.HexGridCellPosition.Neighbor.BottomRight
import com.anynetwork.app.ui.components.hexagon.HexGridCellPosition.Neighbor.Left
import com.anynetwork.app.ui.components.hexagon.HexGridCellPosition.Neighbor.Right
import com.anynetwork.app.ui.components.hexagon.HexGridCellPosition.Neighbor.TopLeft
import com.anynetwork.app.ui.components.hexagon.HexGridCellPosition.Neighbor.TopRight
import com.anynetwork.app.ui.components.hexagon.HexagonalGrid
import com.anynetwork.app.ui.components.hexagon.IconHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.IconHexagonContentStyle.Image.VectorResource
import com.anynetwork.app.ui.components.hexagon.NontransparentHexagonContentStyle.Background.Gradient
import com.anynetwork.app.ui.components.hexagon.NontransparentHexagonContentStyle.Background.SingleColor
import com.anynetwork.app.ui.components.hexagon.RoundedHexagon
import com.anynetwork.app.ui.components.hexagon.RoundedPolygonShape
import com.anynetwork.app.ui.components.hexagon.TransparentHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.createPolygon
import com.anynetwork.app.ui.components.hexagon.hexCellsBackgroundColors
import com.anynetwork.app.ui.components.textfield.ProfileTextFieldLeading
import com.anynetwork.app.ui.navigation.Route
import com.anynetwork.app.ui.screens.externalprofile.ExternalProfileViewEvent.BackButtonClick
import com.anynetwork.app.ui.screens.externalprofile.ExternalProfileViewEvent.EditButtonClick
import com.anynetwork.app.ui.screens.externalprofile.ExternalProfileViewEvent.FavoriteButtonClick
import com.anynetwork.app.ui.screens.externalprofile.ExternalProfileViewEvent.RequestNetworkButtonClick
import com.anynetwork.app.ui.screens.externalprofile.ExternalProfileViewEvent.SaveButtonClick
import com.anynetwork.app.ui.screens.externalprofile.ExternalProfileViewEvent.UpdateAddress
import com.anynetwork.app.ui.screens.externalprofile.ExternalProfileViewEvent.UpdateCompany
import com.anynetwork.app.ui.screens.externalprofile.ExternalProfileViewEvent.UpdateEmail
import com.anynetwork.app.ui.screens.externalprofile.ExternalProfileViewEvent.UpdateFirstName
import com.anynetwork.app.ui.screens.externalprofile.ExternalProfileViewEvent.UpdateHomeFax
import com.anynetwork.app.ui.screens.externalprofile.ExternalProfileViewEvent.UpdateHomePhone
import com.anynetwork.app.ui.screens.externalprofile.ExternalProfileViewEvent.UpdateLastName
import com.anynetwork.app.ui.screens.externalprofile.ExternalProfileViewEvent.UpdateMainPhone
import com.anynetwork.app.ui.screens.externalprofile.ExternalProfileViewEvent.UpdateMobilePhone
import com.anynetwork.app.ui.screens.externalprofile.ExternalProfileViewEvent.UpdateOtherEmail
import com.anynetwork.app.ui.screens.externalprofile.ExternalProfileViewEvent.UpdateOtherPhone
import com.anynetwork.app.ui.screens.externalprofile.ExternalProfileViewEvent.UpdatePager
import com.anynetwork.app.ui.screens.externalprofile.ExternalProfileViewEvent.UpdatePhotoUri
import com.anynetwork.app.ui.screens.externalprofile.ExternalProfileViewEvent.UpdateWorkEmail
import com.anynetwork.app.ui.screens.externalprofile.ExternalProfileViewEvent.UpdateWorkFax
import com.anynetwork.app.ui.screens.externalprofile.ExternalProfileViewEvent.UpdateWorkPhone
import com.anynetwork.app.ui.screens.myprofile.offsetToAvoidKeyboard
import com.anynetwork.app.ui.theme.DarkBlue
import com.anynetwork.app.ui.theme.EmailColor
import com.anynetwork.app.ui.theme.FacebookColor
import com.anynetwork.app.ui.theme.GreenColor
import com.anynetwork.app.ui.theme.InstagramColor
import com.anynetwork.app.ui.theme.MessengerColor
import com.anynetwork.app.ui.theme.PhoneColor
import com.anynetwork.app.ui.theme.PrimaryColor
import com.anynetwork.app.ui.theme.TelegramColor
import com.anynetwork.app.ui.theme.TiktokColor
import com.anynetwork.app.ui.theme.TwitterColor
import com.anynetwork.app.ui.theme.WhatsappColor
import com.anynetwork.app.ui.theme.YellowColor
import com.anynetwork.app.ui.theme.montserratFontFamily
import com.anynetwork.app.ui.utils.csp
import com.anynetwork.app.ui.utils.fdph
import com.anynetwork.app.ui.utils.fdpv
import com.anynetwork.app.ui.utils.fsp
import com.anynetwork.app.ui.utils.log
import com.anynetwork.app.ui.utils.xdph
import com.anynetwork.app.ui.utils.xdpv
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.yalantis.ucrop.UCrop
import timber.log.Timber
import java.io.File
import java.util.UUID
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


@Composable
fun ExternalProfileRoot(
    navController: NavHostController,
    id: Long?,
    clickOffsetX: Float? = null,
    clickOffsetY: Float? = null,
    input: String? = null,
    isEnterAnimationFinished: Boolean = true,
) {

    val viewModel: ExternalProfileViewModel = hiltViewModel<ExternalProfileViewModel>().apply {
        val navigateEvent by navigationEvents.collectAsState()
        navigateEvent.log { "navigationEvent" }
        if (navigateEvent == NavigateBack) {
            navController.popBackStack(Route.Home, inclusive = false)
        }

        val viewEffect by viewEffectFlow.collectAsState()
        viewEffect.log { "viewEffect" }
        when (viewEffect) {
            is ExternalProfileViewEffect.WriteEmail -> {
                val context = LocalContext.current
                val email = (viewEffect as ExternalProfileViewEffect.WriteEmail).emailAddress
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.putExtra(Intent.EXTRA_EMAIL, email)
                intent.type = "text/plain"
                intent.data = Uri.parse("mailto:$email")

                context.startActivity(Intent.createChooser(intent, "Send Email"))
                onViewEvent(ExternalProfileViewEvent.ClearViewEffect)
            }
            is ExternalProfileViewEffect.CallPhoneNumber -> {
                val context = LocalContext.current
                val number = Uri.parse("tel:${(viewEffect as ExternalProfileViewEffect.CallPhoneNumber).phoneNumber}")
                val callIntent = Intent(Intent.ACTION_DIAL, number)
                context.startActivity(callIntent)
                onViewEvent(ExternalProfileViewEvent.ClearViewEffect)
            }
            else -> {}
        }
    }
    BackHandler {
        navController.popBackStack(Route.Home, inclusive = false)
    }
    LaunchedEffect(Unit) {
        viewModel.loadContact(id = id, input = input)
    }
    ExternalProfile(
        viewModel = viewModel,
        navController = navController,
        clickOffset = clickOffsetX?.let { Offset(clickOffsetX, clickOffsetY!!) },
        isEnterAnimationFinished = isEnterAnimationFinished
    )
}

private val gridColumns = 6
private val gridRows = 14

private val centralCellPosition = createCellPosition(
    column = (gridColumns / 2) - 1,
    row = (gridRows / 2),
)

private val trailingCellPosition = createCellPosition(
    column = gridColumns - 3,
    row = gridRows - 3,
)

private val leadingCellPosition = createCellPosition(
    column = gridColumns - 5,
    row = gridRows - 3,
)
val profilePictureCellPosition = createCellPosition(
    column = centralCellPosition.column,
    row = centralCellPosition.row - 2,
)
val linkCellPosition = createCellPosition(
    column = centralCellPosition.column,
    row = centralCellPosition.row - 2,
)
val facebookCellPosition = profilePictureCellPosition.getNeighborPosition(TopLeft)
val messengerCellPosition = profilePictureCellPosition.getNeighborPosition(TopRight)
val instagramCellPosition = profilePictureCellPosition.getNeighborPosition(Right)
val emailCellPosition = profilePictureCellPosition.getNeighborPosition(BottomRight)
val phoneCellPosition = profilePictureCellPosition.getNeighborPosition(BottomLeft)
val twitterCellPosition = profilePictureCellPosition.getNeighborPosition(Left)
val whatsappCellPosition = centralCellPosition.getNeighborPosition(BottomRight)
val telegramCellPosition = centralCellPosition.getNeighborPosition(BottomLeft)
val bitcoinCellPosition = profilePictureCellPosition.copy(row = profilePictureCellPosition.row - 2)
val skypeCellPosition = phoneCellPosition.getNeighborPosition(Left)
val tiktokCellPosition = phoneCellPosition.getNeighborPosition(BottomLeft)
val linkedinCellPosition = centralCellPosition
val ethereumCellPosition = centralCellPosition.getNeighborPosition(Right)

@Composable
private fun ExternalProfile(
    viewModel: ExternalProfileViewModel,
    navController: NavHostController,
    clickOffset: Offset?,
    isEnterAnimationFinished: Boolean
) {
    isEnterAnimationFinished.log { "isEnterAnimationFinished" }
    val scale = gridColumns / 4.7f
    var cellWidth: Int? by remember { mutableStateOf(null) }
    var cellHeight: Int? by remember { mutableStateOf(null) }
    var leadingCellOffset: Offset? by remember { mutableStateOf(null) }
    var trailingCellOffset: Offset? by remember { mutableStateOf(null) }
    var profilePictureCellOffset: Offset? by remember { mutableStateOf(null) }
    var fieldsDialogOptions: List<DropDownDialogMenuCategory> by remember { mutableStateOf(listOf()) }
    val listState = rememberLazyListState()

    val context = LocalContext.current
    var layoutHeight: Int = 0
    var collapsedOffset by remember { mutableStateOf(0f) }
    var expandedOffset by remember { mutableStateOf(0f) }
    var currentOffset by remember { mutableStateOf(0f) }
    var anchors by remember { mutableStateOf<DraggableAnchors<SheetValue>?>(null) }

    val scrollState = rememberScrollState()
    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.value }
            .collect { scrollState.value.log { "scroll" } }
    }

    val viewState by viewModel.viewState.collectAsState()
    val firstName by remember { derivedStateOf { viewState.firstName } }
    val lastName by remember { derivedStateOf { viewState.lastName } }
    val company by remember { derivedStateOf { viewState.company.log { "company" } } }
    val phone by remember { derivedStateOf { viewState.phone } }
    val mobilePhone by remember { derivedStateOf { viewState.mobilePhone } }
    val homePhone by remember { derivedStateOf { viewState.homePhone } }
    val workPhone by remember { derivedStateOf { viewState.workPhone } }
    val mainPhone by remember { derivedStateOf { viewState.mainPhone } }
    val workFax by remember { derivedStateOf { viewState.workFax } }
    val homeFax by remember { derivedStateOf { viewState.homeFax } }
    val pager by remember { derivedStateOf { viewState.pager } }
    val otherPhone by remember { derivedStateOf { viewState.otherPhone } }
    val email by remember { derivedStateOf { viewState.email } }
    val homeEmail by remember { derivedStateOf { viewState.homeEmail } }
    val workEmail by remember { derivedStateOf { viewState.workEmail } }
    val otherEmail by remember { derivedStateOf { viewState.otherEmail } }
    val address by remember { derivedStateOf { viewState.address } }
    val photoUri by remember { derivedStateOf { viewState.photoUri } }
    val firstNameFocusRequester = remember { FocusRequester() }
    val lastNameFocusRequester = remember { FocusRequester() }
    val companyFocusRequester = remember { FocusRequester() }
    val mobilePhoneFocusRequester = remember { FocusRequester() }
    val homePhoneFocusRequester = remember { FocusRequester() }
    val workPhoneFocusRequester = remember { FocusRequester() }
    val mainPhoneFocusRequester = remember { FocusRequester() }
    val workFaxFocusRequester = remember { FocusRequester() }
    val homeFaxFocusRequester = remember { FocusRequester() }
    val pagerFocusRequester = remember { FocusRequester() }
    val otherPhoneFocusRequester = remember { FocusRequester() }
    val emailFocusRequester = remember { FocusRequester() }
    val workEmailFocusRequester = remember { FocusRequester() }
    val otherEmailFocusRequester = remember { FocusRequester() }

    fun phoneNumberOptions(onClick: (String) -> Unit): List<DropDownDialogMenuCategory> {
        return mutableListOf<DropDownDialogMenuCategory>().apply {
            if (mobilePhone == null) add(DropDownDialogMenuCategory("Mobile") {
                onClick.invoke("Mobile")
            })
            if (homePhone == null) add(DropDownDialogMenuCategory("Home") {
                onClick.invoke("Home")
            })
            if (workPhone == null) add(DropDownDialogMenuCategory("Work") {
                onClick.invoke("Work")
            })
            if (mainPhone == null) add(DropDownDialogMenuCategory("Main") {
                onClick.invoke("Main")
            })
            if (workFax == null) add(DropDownDialogMenuCategory("Work Fax") {
                onClick.invoke("Work Fax")
            })
            if (homeFax == null) add(DropDownDialogMenuCategory("Home Fax") {
                onClick.invoke("Home Fax")
            })
            if (pager == null) add(DropDownDialogMenuCategory("Pager") {
                onClick.invoke("Pager")
            })
            if (otherPhone == null) add(DropDownDialogMenuCategory("Other") {
                onClick.invoke("Other")
            })
        }
    }

    fun emailFieldsOptions(onClick: (String) -> Unit): List<DropDownDialogMenuCategory> {
        return mutableListOf<DropDownDialogMenuCategory>().apply {
            if (homeEmail == null) add(DropDownDialogMenuCategory("Home") {
                onClick.invoke("Home")
            })
            if (workEmail == null) add(DropDownDialogMenuCategory("Work") {
                onClick.invoke("Work")
            })
            if (otherEmail == null) add(DropDownDialogMenuCategory("Other") {
                onClick.invoke("Other")
            })
        }
    }

    val anchoredDraggableState = remember {
        AnchoredDraggableState(
            initialValue = if (viewState.mode is ExternalProfileMode.NewContact) SheetValue.Expanded else SheetValue.Collapsed,
            positionalThreshold = { 0f }, // Adjust as needed
            velocityThreshold = { 0f },  // Adjust as needed
            snapAnimationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium,
            ),
            decayAnimationSpec = exponentialDecay()
        )
    }

    val dragPercentage = remember {
        derivedStateOf {
            val percentage = when {
                collapsedOffset != expandedOffset && (viewState.mode is ExternalProfileMode.Edit || viewState.mode is ExternalProfileMode.NewContact) ->
                    ((currentOffset - collapsedOffset) / (expandedOffset - collapsedOffset)) * 100f
                else -> 0f
            }.absoluteValue.log { "percentage" }
            Timber.i("currentOffset: $currentOffset / " +
                    "expandedOffset: $expandedOffset / " +
                    "anchoredDraggableState currentValue: ${anchoredDraggableState.currentValue} / " +
                    "anchoredDraggableState settledValue: ${anchoredDraggableState.settledValue} / ")
            percentage.coerceIn(0f, 100f) // Ensure percentage is between 0 and 100
        }
    }

    val targetBlur = if (viewState.mode is ExternalProfileMode.Edit || viewState.mode is ExternalProfileMode.NewContact) 20.dp else 0.dp
    val animatedBlur by animateDpAsState(
        targetValue = targetBlur,
        animationSpec = tween(durationMillis = 300) // You can customize the duration
    )
    var showAddFieldDialog by remember { mutableStateOf(false) }

    val cropperLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val croppedUri = UCrop.getOutput(result.data!!)
        croppedUri?.let {
            // Use the cropped image URI
            viewModel.onViewEvent(UpdatePhotoUri(it.toString()))
        }
    }

    // Function to launch the cropper
    fun startCrop(context: Context, sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(context.cacheDir, "${UUID.randomUUID()}.jpg"))

        // Configure UCrop options (you can customize it)
        val uCrop = UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f) // Square crop
            .withMaxResultSize(1080, 1080)

        val uCropIntent = uCrop.getIntent(context)
        cropperLauncher.launch(uCropIntent)
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // Persist the permission
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            startCrop(context, it) // Start cropping when image is picked
//            viewModel.onViewEvent(UpdatePhotoUri(it.toString()))
        }
    }

    @Composable
    fun createPhoneTextField(
        label: String,
        focusRequester: FocusRequester,
        value: String,
        onValueChange: (String?) -> Unit,
    ) {
        HexagonTextField(
            modifier = Modifier
                .padding(top = 18.fdpv)
                .height(54.fdpv),
            focusRequester = focusRequester,
            value = value,
            onValueChange = onValueChange,
            leadingIcon = {
                ProfileTextFieldLeading(label) {
                    fieldsDialogOptions = phoneNumberOptions {
                        when (it) {
                            "Mobile" -> viewModel.onViewEvent(UpdateMobilePhone(value, true))
                            "Work" -> viewModel.onViewEvent(UpdateWorkPhone(value, true))
                            "Home" -> viewModel.onViewEvent(UpdateHomePhone(value, true))
                            "Main" -> viewModel.onViewEvent(UpdateMainPhone(value, true))
                            "Work Fax" -> viewModel.onViewEvent(UpdateWorkFax(value, true))
                            "Home Fax" -> viewModel.onViewEvent(UpdateHomeFax(value, true))
                            "Pager" -> viewModel.onViewEvent(UpdatePager(value, true))
                            "Other" -> viewModel.onViewEvent(UpdateOtherPhone(value, true))
                        }
                        onValueChange.invoke(null)
                        showAddFieldDialog = false
                    }
                    showAddFieldDialog = true
                }
            }
        )
    }

    @Composable
    fun createEmailTextField(
        label: String,
        focusRequester: FocusRequester,
        value: String,
        onValueChange: (String?) -> Unit,
    ) {
        HexagonTextField(
            modifier = Modifier
                .padding(top = 16.fdpv)
                .height(54.fdpv)
                .imePadding(),
            focusRequester = focusRequester,
            value = value,
            onValueChange = onValueChange,
            leadingIcon = {
                ProfileTextFieldLeading(label) {
                    fieldsDialogOptions = emailFieldsOptions {
                        when (it) {
                            "Email" -> viewModel.onViewEvent(UpdateEmail(value, true))
                            "Work" -> viewModel.onViewEvent(UpdateWorkEmail(value, true))
                            "Other" -> viewModel.onViewEvent(UpdateOtherEmail(value, true))
                        }
                        onValueChange.invoke(null)
                        showAddFieldDialog = false
                    }
                    showAddFieldDialog = true
                }
            }
        )
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            // Handle pre-scroll (before LazyColumn starts scrolling)
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val isLazyColumnAtTop = listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
                // Only handle scroll if sheet is not fully expanded
                if (anchoredDraggableState.currentValue != SheetValue.Expanded || (anchoredDraggableState.currentValue == SheetValue.Expanded && available.y > 0 && isLazyColumnAtTop)) {
                    anchoredDraggableState.dispatchRawDelta(available.y)
                    return available
                }
                return Offset.Zero // Let LazyColumn handle the scroll
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val isLazyColumnAtTop = listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
                if (anchoredDraggableState.currentValue != SheetValue.Expanded || (anchoredDraggableState.currentValue == SheetValue.Expanded && available.y > 0 && isLazyColumnAtTop)) {
                    anchoredDraggableState.dispatchRawDelta(available.y)
                    return available
                }
                return Offset.Zero // Let LazyColumn handle the scroll
            }

            // Handle fling gestures
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                // If sheet isn't fully expanded, or LazyColumn is at top and fling is downward, let the AnchoredDraggable handle it
                val isLazyColumnAtTop = listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
                if (anchoredDraggableState.currentValue != SheetValue.Expanded || (anchoredDraggableState.currentValue == SheetValue.Expanded && available.y > 0 && isLazyColumnAtTop)) {
                    anchoredDraggableState.settle(available.y)
                }
                return Velocity.Zero
            }
        }
    }

    val alpha = 1 - dragPercentage.value/100f
    val itemAlpha by animateFloatAsState(
        targetValue = if (viewState.mode is ExternalProfileMode.Edit) .3f else 1f,
        animationSpec = tween(300)
    )

    val normalHexItems = List(gridRows) { row ->
        List(gridColumns) { column ->
            val isCenter = row == centralCellPosition.row && column == centralCellPosition.column

            val cellIndex = createCellPosition(
                column = column,
                row = row,
            ).getIndex()

            val backgroundColor = hexCellsBackgroundColors[cellIndex]

            when {
                isCenter -> /*IconHexagonContentStyle(
                    modifier = Modifier
                        .fillMaxWidth(1 / 2f)
                        .fillMaxSize(43f / 80),
                    background = SingleColor(Color(0xFF393939)),
                    contentDescription = "Any network",
                    image = VectorResource(id = R.drawable.ic_any_network),

                )*/EmptyHexagonContentStyle(
                    id = cellIndex,
                    background = SingleColor(backgroundColor)
                )
                profilePictureCellPosition.isSame(column, row) -> remember(viewState.mode) {
                    CustomHexagonContentStyle(
                        id = cellIndex,
                        content = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .align(Alignment.Center)
                                    .background(Color(0xFF6E4CD4))
                            ) {
                                if (photoUri == null) {
                                    Image(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .fillMaxSize(0.4f),
                                        painter = rememberAsyncImagePainter(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(R.drawable.ic_profile)
                                                .size(100)
                                                .build()
                                        ),
                                        contentScale = ContentScale.Crop,
                                        contentDescription = null,
                                    )
                                } else {
                                    Image(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        painter = rememberAsyncImagePainter(photoUri),
                                        contentScale = ContentScale.Crop,
                                        contentDescription = null,
                                    )
                                }
                            }
                        },
                        overlay = {
                            if (viewState.mode is ExternalProfileMode.Edit) {
                                cellWidth?.let {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 7.8.fdpv)
                                            .size(24.fdpv * ((with(LocalDensity.current) { cellWidth!!.toDp() }) / 79.93f.fdpv))
                                            .clip(CircleShape)
                                            .background(PrimaryColor)
                                            .border(
                                                width = 1.fdpv,
                                                color = Color.White,
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            modifier = Modifier
                                                .fillMaxWidth(fraction = 10.18f / 24),
                                            painter = painterResource(R.drawable.ic_edit_only_pen),
                                            contentDescription = "edit profile",
                                        )
                                    }
                                }
                            }
                        },
                        onClick = { _ ->
                            if (viewState.mode is ExternalProfileMode.Edit) {
                                imagePickerLauncher.launch("image/*")
                            }
                        },
                    )
                }
                facebookCellPosition.isSame(column, row) -> /*IconHexagonContentStyle(
                    background = SingleColor(FacebookColor),
                    contentDescription = "Facebook",
                    image = VectorResource(id = R.drawable.ic_facebook)
                )*/EmptyHexagonContentStyle(
                    id = cellIndex,
                    background = SingleColor(backgroundColor.copy(alpha = backgroundColor.alpha * alpha))
                )
                messengerCellPosition.isSame(column, row) -> /*IconHexagonContentStyle(
                    background = SingleColor(MessengerColor),
                    contentDescription = "Messenger",
                    image = VectorResource(id = R.drawable.ic_messenger)
                )*/EmptyHexagonContentStyle(
                    id = cellIndex,
                    background = SingleColor(backgroundColor.copy(alpha = backgroundColor.alpha * alpha))
                )
                instagramCellPosition.isSame(column, row) -> /*IconHexagonContentStyle(
                    background = SingleColor(InstagramColor),
                    contentDescription = "Instagram",
                    image = VectorResource(id = R.drawable.ic_instagram)
                )*/EmptyHexagonContentStyle(
                    id = cellIndex,
                    background = SingleColor(backgroundColor.copy(alpha = backgroundColor.alpha * alpha))
                )

                emailCellPosition.isSame(column, row) -> remember(viewState.mode, itemAlpha, alpha, email) {
                    if (email != null && email!!.isNotEmpty()) IconHexagonContentStyle(
                        id = cellIndex,
                        background = SingleColor(EmailColor.copy(alpha = itemAlpha * alpha)),
                        alpha = itemAlpha * alpha,
                        contentDescription = "Email",
                        image = VectorResource(id = R.drawable.ic_email),
                        onClick = {
                            viewModel.onViewEvent(ExternalProfileViewEvent.EmailButtonClick)
                        },
                        isShakable = true,
                        overlay = if (viewState.mode is ExternalProfileMode.Edit) {
                            {
                                DeleteButton(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(top = 17.31.fdpv, end = 1.fdpv)
                                        .size(24.fdpv * (LocalConfiguration.current.screenWidthDp.dp / gridColumns / 79.93f.fdpv))
                                        .alpha(alpha),
                                    onClick = {

                                    }
                                )
                            }
                        } else null
                    ) else EmptyHexagonContentStyle(
                        id = cellIndex,
                        background = SingleColor(backgroundColor.copy(alpha = backgroundColor.alpha * alpha))
                    )
                }

                phoneCellPosition.isSame(column, row) -> remember(viewState.mode, itemAlpha, alpha, phone) {
                    if (phone != null && phone!!.isNotEmpty()) IconHexagonContentStyle(
                        id = cellIndex,
                        background = SingleColor(PhoneColor.copy(alpha = itemAlpha * alpha)),
                        alpha = itemAlpha * alpha,
                        contentDescription = "Phone",
                        image = VectorResource(id = R.drawable.ic_phone),
                        onClick = {
                            viewModel.onViewEvent(ExternalProfileViewEvent.PhoneButtonClick)
                        },
                        isShakable = true,
                        overlay = if (viewState.mode is ExternalProfileMode.Edit) {
                            {
                                DeleteButton(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(top = 17.31.fdpv, end = 1.fdpv)
                                        .size(24.fdpv * (LocalConfiguration.current.screenWidthDp.dp / gridColumns / 79.93f.fdpv))
                                        .alpha(alpha),
                                    onClick = {

                                    }
                                )
                            }
                        } else null
                    ) else {
                        EmptyHexagonContentStyle(
                            id = cellIndex,
                            background = SingleColor(backgroundColor.copy(alpha = backgroundColor.alpha * alpha))
                        )
                    }
                }

                twitterCellPosition.isSame(column, row) ->
                    /*IconHexagonContentStyle(
                        background = SingleColor(TwitterColor),
                        contentDescription = "Twitter",
                        image = VectorResource(id = R.drawable.ic_twitter)
                    )*/EmptyHexagonContentStyle(
                    id = cellIndex,
                    background = SingleColor(backgroundColor.copy(alpha = backgroundColor.alpha * alpha))
                )

                whatsappCellPosition.isSame(column, row) -> /*IconHexagonContentStyle(
                    background = SingleColor(WhatsappColor),
                    contentDescription = "Whatsapp",
                    image = VectorResource(id = R.drawable.ic_whatsapp)
                )*/EmptyHexagonContentStyle(
                    id = cellIndex,
                    background = SingleColor(backgroundColor.copy(alpha = backgroundColor.alpha * alpha))
                )

                telegramCellPosition.isSame(column, row) -> //if (apps.contains("Telegram")) {
//                    IconHexagonContentStyle(
//                            background = SingleColor(TelegramColor),
//                            contentDescription = "Telegram",
//                            image = VectorResource(id = R.drawable.ic_telegram)
//                        )
//                    } else {
                    EmptyHexagonContentStyle(
                        id = cellIndex,
                        background = SingleColor(backgroundColor.copy(alpha = backgroundColor.alpha * alpha))
                    )
//                    }

                trailingCellPosition.isSame(column, row) -> TransparentHexagonContentStyle(
                    id = cellIndex,
                )
                else -> EmptyHexagonContentStyle(
                    id = cellIndex,
                    background = SingleColor(backgroundColor.copy(alpha = backgroundColor.alpha * alpha))
                )
            }
        }
    }

    Screen(
        topBar = ToolbarState.Shown(
            navigationIconState = NavigationIconState.Custom {
                IconButton(onClick = {
                    navController.popBackStack(Route.Home, inclusive = false)
                }) {
                    Image(
                        painter = painterResource(R.drawable.ic_back_arrow),
                        contentDescription = "hamburger menu icon",
                    )
                }
            },
            titleState = ToolbarStateTitle.Custom(
                content = {
                    when (viewState.mode) {
                        is ExternalProfileMode.RequestNetwork -> Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Request network",
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            style = TextStyle(
                                fontFamily = montserratFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.fsp,
                            )
                        )
                        else -> Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "$firstName $lastName",
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            style = TextStyle(
                                fontFamily = montserratFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.fsp,
                            )
                        )
                    }
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = when (viewState.mode) {
                            is ExternalProfileMode.RequestNetwork -> "Request network"
                            else -> "Mark Hamlin"
                        },
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        style = TextStyle(
                            fontFamily = montserratFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.fsp,
                        )
                    )
                }
            ),
            actions = {
                val alphaAnimationDuration = 700
                val saveButtonAlpha by animateFloatAsState(
                    targetValue = if (viewState.mode is ExternalProfileMode.Edit || viewState.mode is ExternalProfileMode.NewContact) 1f else 0f,
                    animationSpec = tween(alphaAnimationDuration)
                )
                Button(
                    modifier = Modifier.alpha(saveButtonAlpha).zIndex(saveButtonAlpha),
                    onClick = {
                        if (viewState.mode is ExternalProfileMode.Edit || viewState.mode is ExternalProfileMode.NewContact) {
                            viewModel.onViewEvent(SaveButtonClick)
                        }
                    },
                    colors = ButtonDefaults.buttonColors().copy(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(all = 0.dp)
                ) {
                    Text(
                        modifier = Modifier,
                        text = "Save",
                        textAlign = TextAlign.Center,
                        color = GreenColor,
                        style = TextStyle(
                            fontFamily = montserratFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.csp,
                        )
                    )
                }

                val editButtonAlpha = 1 - saveButtonAlpha
                IconButton(
                    modifier = Modifier.alpha(editButtonAlpha).zIndex(editButtonAlpha),
                    onClick = {
                        if (viewState.mode !is ExternalProfileMode.Edit && viewState.mode !is ExternalProfileMode.NewContact) {
                            viewModel.onViewEvent(EditButtonClick)
                        }
                    }
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = "notifications action icon",
                    )
                }
            },
        ),
        hexagonGrid = {
            val items = when {
                viewState.mode == ExternalProfileMode.Normal || viewState.mode == ExternalProfileMode.Edit -> normalHexItems
                else -> getRequestNetworkModeGridItems()
            }

            var triggerRecalculation by remember(isEnterAnimationFinished) { mutableStateOf(isEnterAnimationFinished) }.log { "triggerRecalculation" }
            HexagonalGrid(
                modifier = Modifier.alpha(if (viewState.mode is ExternalProfileMode.NewContact) 0f else 1f),
                items = items,
                rowSize = gridColumns,
                initialScale = scale,
                onCellPositionCalculated = { index, offset, width, height ->
                    Timber.i("onCellPositionCalculated for index: $index")
                    if (isEnterAnimationFinished) {
                        if (cellWidth == null) cellWidth = width
                        if (cellHeight == null) cellHeight = height
                        if (index == leadingCellPosition.getIndex()) {
                            if (leadingCellOffset == null) leadingCellOffset = offset
                        } else if (index == trailingCellPosition.getIndex()) {
                            if (trailingCellOffset == null) trailingCellOffset = offset
                        } else if (index == profilePictureCellPosition.getIndex()) {
                            if (profilePictureCellOffset == null) profilePictureCellOffset = offset
                        }
                    }
                },
                onZoom = { zoom, offset ->
                    viewModel.onGridZoomChange(zoom = zoom)
                },
                isScrollEnabled = false,
                offsetY = when {
                    profilePictureCellOffset == null -> 0f
                    else -> -(profilePictureCellOffset!!.y - with(LocalDensity.current) {
                        113f.fdpv.toPx()
                    }) * (dragPercentage.value / 100)
                }.roundToInt(),
                isEditModeActivating = viewState.mode is ExternalProfileMode.Edit,
                gridScaling = 1f
            )

            leadingCellOffset?.let {
                val centralOffsetInDp = with(LocalDensity.current) {
                    DpOffset(it.x.toDp(), it.y.toDp())
                }

                val polygon = remember { createPolygon() }
                val roundedPolygonShape = remember { RoundedPolygonShape(polygon) }

                val alpha by animateFloatAsState(
                    targetValue = if (viewState.mode == ExternalProfileMode.Normal) 1f else 0f,
                    animationSpec = tween(500)
                )

                val searchAlpha by animateFloatAsState(
                    targetValue = if (viewState.mode is ExternalProfileMode.RequestNetwork
                        && !(viewState.mode as ExternalProfileMode.RequestNetwork).isSearching) 1f else 0f,
                    animationSpec = tween(500)
                )
                val cellSize = LocalConfiguration.current.screenWidthDp.dp / gridColumns
                val verticalBorder = (cellSize * 0.0483f).log { "verticalBorder" }
                val horizontalBorder = (cellSize * 89.99f/79.93f * 0.0429f).log { "horizontalBorder" }

                RoundedHexagon(
                    modifier = Modifier
                        .offset(centralOffsetInDp.x, centralOffsetInDp.y)
                        .width(with(LocalDensity.current) { cellWidth!!.toDp() } * scale)
                        .padding(
                            vertical = verticalBorder,
                            horizontal = horizontalBorder
                        )
                        .aspectRatio(79.93.xdph / 89.99.xdpv)
                        .alpha(alpha)
//                            .zIndex(if (alpha == 1f) 1f else 0f) // Bring to front when alpha is 1
                        .then(Modifier.graphicsLayer {
                            this.shadowElevation = shadowElevation
                            clip = true
                            shape = roundedPolygonShape
                        }),
                    onClick = {
                        if (alpha > 0.5f) {
                            viewModel.onViewEvent(RequestNetworkButtonClick())
                        }
                    },
                    contentStyle = IconHexagonContentStyle(
                        id = 0,
                        background = SingleColor(Color(0xFF302C3D)),
                        contentDescription = "Add",
                        image = VectorResource(id = R.drawable.ic_plus)
                    ),
                )

                val searchHexagonPolygon = remember { createPolygon() }
                val searchHexagonRoundedPolygonShape = remember { RoundedPolygonShape(searchHexagonPolygon) }

                RoundedHexagon(
                    modifier = Modifier
                        .offset(centralOffsetInDp.x, centralOffsetInDp.y)
                        .width(with(LocalDensity.current) { cellWidth!!.toDp() } * scale)
                        .padding(
                            vertical = verticalBorder,
                            horizontal = horizontalBorder
                        )
                        .aspectRatio(79.93.xdph / 89.99.xdpv)
                        .alpha(searchAlpha)
                        .zIndex(if (searchAlpha == 1f) 1f else 0f) // Bring to front when alpha is 1
                        .then(Modifier.graphicsLayer {
                            this.shadowElevation = shadowElevation
                            clip = true
                            shape = searchHexagonRoundedPolygonShape
                        }),
                    onClick = {
                        if (searchAlpha > 0.5f) {
                            viewModel.onViewEvent(
                                RequestNetworkButtonClick(
                                    isSearching = true
                                )
                            )
                        }
                    },
                    contentStyle = IconHexagonContentStyle(
                        id = 0,
                        background = SingleColor(Color(0xFF302C3D)),
                        contentDescription = "Search",
                        image = VectorResource(id = R.drawable.ic_search)
                    )
                )

            }

            trailingCellOffset?.let {
                trailingCellOffset?.log { "trailingCellOffset" }
                if (viewState.mode == ExternalProfileMode.Normal) {
                    val centralOffsetInDp = with(LocalDensity.current) {
                        DpOffset(it.x.toDp(), it.y.toDp())
                    }
                    val polygon = remember { createPolygon() }
                    val roundedPolygonShape = remember { RoundedPolygonShape(polygon) }
                    val cellSize = LocalConfiguration.current.screenWidthDp.dp / gridColumns
                    val verticalBorder = (cellSize * 0.0483f).log { "verticalBorder" }
                    val horizontalBorder = (cellSize * 89.99f/79.93f * 0.0429f).log { "horizontalBorder" }

                    val favoriteButtonAlpha by animateFloatAsState(
                        targetValue = if (viewState.isFavorite) 0f else 1f,
                        animationSpec = tween(300)
                    )

                    RoundedHexagon(
                        modifier = Modifier
                            .offset(centralOffsetInDp.x, centralOffsetInDp.y)
                            .width(with(LocalDensity.current) { cellWidth!!.toDp() } * scale)
                            .padding(
                                vertical = verticalBorder,
                                horizontal = horizontalBorder
                            )
                            .aspectRatio(79.93.xdph / 89.99.xdpv)
                            .alpha(favoriteButtonAlpha * itemAlpha)
                            .then(Modifier.graphicsLayer {
                                this.shadowElevation = shadowElevation
                                clip = true
                                shape = roundedPolygonShape
                            }),
                        contentStyle = IconHexagonContentStyle(
                            id = 0,
                            background = SingleColor(Color(0xFF302C3D).copy(alpha = itemAlpha)),
                            contentDescription = "",
                            image = VectorResource(id = R.drawable.ic_star),
                            tintColor = YellowColor,
                        ),
                        onClick = {
                            viewModel.onViewEvent(FavoriteButtonClick)
                        }
                    )


                    val polygon2 = remember { createPolygon() }
                    val roundedPolygonShape2 = remember { RoundedPolygonShape(polygon2) }
                    RoundedHexagon(
                        modifier = Modifier
                            .offset(centralOffsetInDp.x, centralOffsetInDp.y)
                            .width(with(LocalDensity.current) { cellWidth!!.toDp() } * scale)
                            .padding(
                                vertical = verticalBorder,
                                horizontal = horizontalBorder
                            )
                            .aspectRatio(79.93.xdph / 89.99.xdpv)
                            .alpha((1 - favoriteButtonAlpha) * itemAlpha)
                            .then(Modifier.graphicsLayer {
                                clip = true
                                shape = roundedPolygonShape2
                            }),
                        contentStyle = IconHexagonContentStyle(
                            id = 0,
                            background = SingleColor(YellowColor.copy(alpha = itemAlpha)),
                            contentDescription = "",
                            image = VectorResource(id = R.drawable.ic_star_filled),
                            tintColor = Color.White,
                        ),
                        onClick = {
                            viewModel.onViewEvent(FavoriteButtonClick)
                        }
                    )
                }
            }
        },
        content = {
            val density = LocalDensity.current
            if (isEnterAnimationFinished && cellHeight != null && viewState.mode is ExternalProfileMode.Edit || viewState.mode is ExternalProfileMode.NewContact) {
                BoxWithConstraints(
                    modifier = Modifier
                ) {
                    layoutHeight = constraints.maxHeight
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .offset {
                            val sheetOffsetY = anchoredDraggableState
                                .offset
                                .toInt()
                                .log { "sheetOffsetY" }
                            currentOffset = sheetOffsetY.toFloat()
                            IntOffset(x = 0, y = sheetOffsetY)
                        }
                        .then(if (viewState.mode is ExternalProfileMode.NewContact) Modifier else Modifier
                            .anchoredDraggable(
                                anchoredDraggableState,
                                orientation = Orientation.Vertical,
                                enabled = true
                            )
                            .onSizeChanged { sheetSize ->
                                val sheetHeight = sheetSize.height
                                if (anchors == null) {
                                    Timber.i("set anchors")
                                    anchors = DraggableAnchors {
                                        with(density) {
                                            collapsedOffset = layoutHeight - 380.fdpv.toPx()
                                            expandedOffset = maxOf(
                                                layoutHeight - sheetHeight + 92.fdpv.toPx(),
                                                0f
                                            )
                                            SheetValue.Collapsed at collapsedOffset
                                            SheetValue.Expanded at expandedOffset
                                        }
                                    }
                                    anchoredDraggableState.updateAnchors(
                                        anchors!!,
                                        anchoredDraggableState.targetValue
                                    )
                                }
                            })
                        .nestedScroll(nestedScrollConnection),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(190.fdpv)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                DarkBlue.copy(alpha = (1 - dragPercentage.value / 100)),
                                            )
                                        )
                                    )
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(DarkBlue)
                            )
                        }

                        val paddingTop = if (viewState.mode is ExternalProfileMode.NewContact) 198.fdpv else 114.fdpv
                        val paddingBottom = with(density) { expandedOffset.toDp() }//if (mode !is ExternalProfileMode.NewContact) with(density) { expandedOffset.toDp() } else 0.dp
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = paddingTop, bottom = paddingBottom)
                                .padding(horizontal = 16.fdph)
                                .graphicsLayer { this.alpha = 0.99f }
                                .drawWithContent {
                                    // Draw the composable content first
                                    drawContent()

                                    // Apply fading effect at the edges using a gradient
                                    drawRect(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black,
                                                Color.Black, Color.Black, Color.Black,
                                                Color.Black, Color.Black, Color.Black,
                                                Color.Black, Color.Black, Color.Black,
                                                Color.Black),
                                            startY = 0f,
                                            endY = size.height
                                        ),
                                        blendMode = BlendMode.DstIn
                                    )
                                }
                                .imePadding()
                                .nestedScroll(nestedScrollConnection),
                            state = listState,
                            userScrollEnabled = dragPercentage.value.log { "dragPercentage" } == 100f,
                        ) {
                            item {
                                Spacer(modifier = Modifier.height(46.fdpv))
                            }

                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {

                                    HexagonTextField(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(54.fdpv),
                                        focusRequester = firstNameFocusRequester,
                                        value = firstName,
                                        onValueChange = { newValue ->
                                            viewModel.onViewEvent(
                                                UpdateFirstName(
                                                    newValue ?: ""
                                                )
                                            )
                                        },
                                        placeholder = "First Name"
                                    )

                                    Spacer(modifier = Modifier.width(15.fdph))

                                    HexagonTextField(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(54.fdpv),
                                        focusRequester = lastNameFocusRequester,
                                        value = lastName,
                                        onValueChange = { newValue ->
                                            viewModel.onViewEvent(
                                                UpdateLastName(
                                                    newValue ?: ""
                                                )
                                            )
                                        },
                                        placeholder = "Last Name"
                                    )
                                }
                            }

                            company?.let { company ->
                                item {
                                    HexagonTextField(
                                        modifier = Modifier
                                            .padding(top = 16.fdpv)
                                            .height(54.fdpv),
                                        focusRequester = companyFocusRequester,
                                        value = company,
                                        onValueChange = { newValue ->
                                            viewModel.onViewEvent(UpdateCompany(newValue))
                                        },
                                        placeholder = "Company"
                                    )
                                }
                            }

                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 21.fdpv),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        modifier = Modifier,
                                        text = "Contact Info",
                                        textAlign = TextAlign.Center,
                                        color = Color(0xFFFFFFFF),
                                        style = TextStyle(
                                            fontFamily = montserratFontFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 18.fsp,
                                        )
                                    )

                                    Spacer(modifier = Modifier.weight(1f))

                                    Text(
                                        modifier = Modifier.clickable {
                                            fieldsDialogOptions =
                                                mutableListOf<DropDownDialogMenuCategory>().apply {
                                                    add(
                                                        DropDownDialogMenuCategory(
                                                            "Phone Number",
                                                            phoneNumberOptions {
                                                                showAddFieldDialog = false
                                                                when (it) {
                                                                    "Mobile" -> {
                                                                        viewModel.onViewEvent(
                                                                            UpdateMobilePhone(
                                                                                phone = "",
                                                                                shouldRequestFocus = true
                                                                            )
                                                                        )
                                                                    }

                                                                    "Work" -> {
                                                                        viewModel.onViewEvent(
                                                                            UpdateWorkPhone("",
                                                                                shouldRequestFocus = true)
                                                                        )
                                                                    }

                                                                    "Home" -> {
                                                                        viewModel.onViewEvent(
                                                                            UpdateHomePhone("",
                                                                                shouldRequestFocus = true)
                                                                        )
                                                                    }

                                                                    "Main" -> {
                                                                        viewModel.onViewEvent(
                                                                            UpdateMainPhone("",
                                                                                shouldRequestFocus = true)
                                                                        )
                                                                    }

                                                                    "Work Fax" -> {
                                                                        viewModel.onViewEvent(
                                                                            UpdateWorkFax("",
                                                                                shouldRequestFocus = true)
                                                                        )
                                                                    }

                                                                    "Home Fax" -> {
                                                                        viewModel.onViewEvent(
                                                                            UpdateHomeFax("",
                                                                                shouldRequestFocus = true)
                                                                        )
                                                                    }

                                                                    "Pager" -> {
                                                                        viewModel.onViewEvent(
                                                                            UpdatePager("",
                                                                                shouldRequestFocus = true)
                                                                        )
                                                                    }

                                                                    "Other" -> {
                                                                        viewModel.onViewEvent(
                                                                            UpdateOtherPhone("",
                                                                                shouldRequestFocus = true)
                                                                        )
                                                                    }
                                                                }
                                                            })
                                                    )
                                                    add(
                                                        DropDownDialogMenuCategory(
                                                            "Email Address",
                                                            emailFieldsOptions {
                                                                showAddFieldDialog = false
                                                                when (it) {
                                                                    "Home" -> viewModel.onViewEvent(
                                                                        UpdateEmail("",
                                                                            shouldRequestFocus = true)
                                                                    )

                                                                    "Work" -> viewModel.onViewEvent(
                                                                        UpdateWorkEmail("",
                                                                            shouldRequestFocus = true)
                                                                    )

                                                                    "Other" -> viewModel.onViewEvent(
                                                                        UpdateOtherEmail("",
                                                                            shouldRequestFocus = true)
                                                                    )
                                                                }
                                                                showAddFieldDialog = false
                                                            })
                                                    )
                                                    add(DropDownDialogMenuCategory("Other Info",
                                                        listOf(
                                                            DropDownDialogMenuCategory(
                                                                "Company",
                                                                onClick = {
                                                                    showAddFieldDialog = false
                                                                    viewModel.onViewEvent(
                                                                        UpdateCompany("")
                                                                    )
                                                                }),
                                                            DropDownDialogMenuCategory("Address",
                                                                listOf(
                                                                    DropDownDialogMenuCategory("Home") {

                                                                    },
                                                                    DropDownDialogMenuCategory("Work") {

                                                                    },
                                                                    DropDownDialogMenuCategory("School") {

                                                                    },
                                                                    DropDownDialogMenuCategory("Other") {

                                                                    }
                                                                ))
                                                        )))
                                                }
                                            showAddFieldDialog = true
                                        },
                                        text = "Add",
                                        textAlign = TextAlign.Center,
                                        color = GreenColor,
                                        style = TextStyle(
                                            fontFamily = montserratFontFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 14.fsp,
                                        )
                                    )

//                                Text(
//                                    modifier = Modifier.clickable {
//                                        showAddFieldDialog = true
//                                    },
//                                    text = "Add",
//                                    textAlign = TextAlign.Center,
//                                    color = GreenColor,
//                                    style = TextStyle(
//                                        fontFamily = montserratFontFamily,
//                                        fontWeight = FontWeight.Medium,
//                                        fontSize = 14.fsp,
//                                    )
//                                )
                                }
                            }

                            mobilePhone?.let { phone ->
                                item {
                                    createPhoneTextField(
                                        label = "Mobile",
                                        focusRequester = mobilePhoneFocusRequester,
                                        value = phone,
                                    ) {
                                        viewModel.onViewEvent(UpdateMobilePhone(it))
                                    }
                                    LaunchedEffect(mobilePhone) {
                                        if (viewModel.viewEffectFlow.value is ExternalProfileViewEffect.RequestFocusOnMobilePhoneTextField) {
                                            mobilePhoneFocusRequester.requestFocus()
                                        }
                                    }
                                }
                            }

                            homePhone?.let { homePhone ->
                                item {
                                    createPhoneTextField(
                                        label = "Home Phone",
                                        focusRequester = homePhoneFocusRequester,
                                        value = homePhone
                                    ) {
                                        viewModel.onViewEvent(UpdateHomePhone(it))
                                    }
                                    LaunchedEffect(homePhone) {
                                        if (viewModel.viewEffectFlow.value is ExternalProfileViewEffect.RequestFocusOnHomePhoneTextField) {
                                            homePhoneFocusRequester.requestFocus()
                                        }
                                    }
                                }
                            }

                            workPhone?.let { workPhone ->
                                item {
                                    createPhoneTextField(
                                        label = "Work Phone",
                                        focusRequester = workPhoneFocusRequester,
                                        value = workPhone
                                    ) {
                                        viewModel.onViewEvent(UpdateWorkPhone(it))
                                    }
                                    LaunchedEffect(workPhone) {
                                        if (viewModel.viewEffectFlow.value is ExternalProfileViewEffect.RequestFocusOnWorkPhoneTextField) {
                                            workPhoneFocusRequester.requestFocus()
                                        }
                                    }
                                }
                            }

                            mainPhone?.let {
                                item {
                                    createPhoneTextField(
                                        label = "Main Phone",
                                        focusRequester = mainPhoneFocusRequester,
                                        value = it) {
                                        viewModel.onViewEvent(UpdateMainPhone(it))
                                    }
                                    LaunchedEffect(mainPhone) {
                                        if (viewModel.viewEffectFlow.value is ExternalProfileViewEffect.RequestFocusOnMainPhoneTextField) {
                                            mainPhoneFocusRequester.requestFocus()
                                        }
                                    }
                                }
                            }

                            workFax?.let {
                                item {
                                    createPhoneTextField(
                                        label = "Work Fax",
                                        focusRequester = workFaxFocusRequester,
                                        value = it
                                    ) {
                                        viewModel.onViewEvent(UpdateWorkFax(it))
                                    }
                                    LaunchedEffect(workFax) {
                                        if (viewModel.viewEffectFlow.value is ExternalProfileViewEffect.RequestFocusOnWorkFaxTextField) {
                                            workFaxFocusRequester.requestFocus()
                                        }
                                    }
                                }
                            }

                            homeFax?.let {
                                item {
                                    createPhoneTextField(
                                        label = "Home Fax",
                                        focusRequester = homeFaxFocusRequester,
                                        value = it
                                    ) {
                                        viewModel.onViewEvent(UpdateHomeFax(it))
                                    }
                                    LaunchedEffect(homeFax) {
                                        if (viewModel.viewEffectFlow.value is ExternalProfileViewEffect.RequestFocusOnHomeFaxTextField) {
                                            homeFaxFocusRequester.requestFocus()
                                        }
                                    }
                                }
                            }

                            pager?.let {
                                item {
                                    createPhoneTextField(
                                        label = "Pager",
                                        focusRequester = pagerFocusRequester,
                                        value = it
                                    ) {
                                        viewModel.onViewEvent(UpdatePager(it))
                                    }
                                    LaunchedEffect(pager) {
                                        if (viewModel.viewEffectFlow.value is ExternalProfileViewEffect.RequestFocusOnPagerTextField) {
                                            pagerFocusRequester.requestFocus()
                                        }
                                    }
                                }
                            }

                            otherPhone?.let {
                                item {
                                    createPhoneTextField(
                                        label = "Other Phone",
                                        focusRequester = otherPhoneFocusRequester,
                                        value = it
                                    ) {
                                        viewModel.onViewEvent(UpdateOtherPhone(it))
                                    }
                                    LaunchedEffect(otherPhone) {
                                        if (viewModel.viewEffectFlow.value is ExternalProfileViewEffect.RequestFocusOnOtherPhoneTextField) {
                                            otherPhoneFocusRequester.requestFocus()
                                        }
                                    }
                                }
                            }

                            homeEmail?.let { email ->
                                item {
                                    createEmailTextField(
                                        label = "Home email",
                                        focusRequester = emailFocusRequester,
                                        value = email
                                    ) {
                                        it.log { "new email input" }
                                        viewModel.onViewEvent(UpdateEmail(it))
                                    }
                                    LaunchedEffect(email) {
                                        if (viewModel.viewEffectFlow.value is ExternalProfileViewEffect.RequestFocusOnEmailTextField) {
                                            emailFocusRequester.requestFocus()
                                        }
                                    }
                                }
                            }

                            workEmail?.let { email ->
                                item {
                                    createEmailTextField(
                                        label = "Work Email",
                                        focusRequester = workEmailFocusRequester,
                                        value = email
                                    ) {
                                        viewModel.onViewEvent(UpdateWorkEmail(it))
                                    }
                                    LaunchedEffect(workEmail) {
                                        if (viewModel.viewEffectFlow.value is ExternalProfileViewEffect.RequestFocusOnWorkEmailTextField) {
                                            workEmailFocusRequester.requestFocus()
                                        }
                                    }
                                }
                            }

                            otherEmail?.let { email ->
                                item {
                                    createEmailTextField(
                                        label = "Other Email",
                                        focusRequester = otherEmailFocusRequester,
                                        value = email
                                    ) {
                                        viewModel.onViewEvent(UpdateOtherEmail(it))
                                    }
                                    LaunchedEffect(otherEmail) {
                                        if (viewModel.viewEffectFlow.value is ExternalProfileViewEffect.RequestFocusOnOtherEmailTextField) {
                                            otherEmailFocusRequester.requestFocus()
                                        }
                                    }
                                }
                            }

                            if (address != null) {

                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 21.fdpv),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            modifier = Modifier,
                                            text = "Address",
                                            textAlign = TextAlign.Center,
                                            color = Color(0xFFFFFFFF),
                                            style = TextStyle(
                                                fontFamily = montserratFontFamily,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 18.fsp,
                                            )
                                        )

                                        Spacer(modifier = Modifier.weight(1f))

//                                    Text(
//                                        modifier = Modifier.clickable {
//                                            showAddFieldDialog = true
//                                        },
//                                        text = "Add",
//                                        textAlign = TextAlign.Center,
//                                        color = GreenColor,
//                                        style = TextStyle(
//                                            fontFamily = montserratFontFamily,
//                                            fontWeight = FontWeight.Medium,
//                                            fontSize = 14.fsp,
//                                        )
//                                    )
                                    }
                                }

                                item {
                                    address?.let { address ->
                                        HexagonTextField(
                                            modifier = Modifier
                                                .padding(top = 16.fdpv)
                                                .height(54.fdpv)
                                                .offsetToAvoidKeyboard(),
                                            value = address,
                                            onValueChange = { newValue ->
                                                viewModel.onViewEvent(
                                                    UpdateAddress(
                                                        newValue
                                                    )
                                                )
                                            },
                                            placeholder = "Address",
                                            trailingIcon = when {
                                                address.isNotEmpty() -> {
                                                    {
                                                        HexagonTextFieldClearTrailingIcon(onClick = {
                                                            viewModel.onViewEvent(
                                                                UpdateAddress("")
                                                            )
                                                        })
                                                    }
                                                }

                                                else -> null
                                            }
                                        )
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(46.fdpv))
                            }
                        }
                    }
                }
            }

            if (viewState.mode is ExternalProfileMode.NewContact) {
                cellWidth?.let {

                    val polygon = remember { createPolygon() }
                    val roundedPolygonShape = remember { RoundedPolygonShape(polygon) }


                    val cropperLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult()
                    ) { result ->
                        val croppedUri = UCrop.getOutput(result.data!!)
                        croppedUri?.let {
                            // Use the cropped image URI
                            viewModel.onViewEvent(UpdatePhotoUri(it.toString()))
                        }
                    }

                    // Function to launch the cropper
                    fun startCrop(context: Context, sourceUri: Uri) {
                        val destinationUri =
                            Uri.fromFile(File(context.cacheDir, "${UUID.randomUUID()}.jpg"))

                        // Configure UCrop options (you can customize it)
                        val uCrop = UCrop.of(sourceUri, destinationUri)
                            .withAspectRatio(1f, 1f) // Square crop
                            .withMaxResultSize(1080, 1080)

                        val uCropIntent = uCrop.getIntent(context)
                        cropperLauncher.launch(uCropIntent)
                    }

                    val imagePickerLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { uri ->
                        uri?.let {
                            // Persist the permission
                            context.contentResolver.takePersistableUriPermission(
                                it,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )
                            startCrop(context, it) // Start cropping when image is picked
                        }
                    }

                    val cellSize = LocalConfiguration.current.screenWidthDp.dp / gridColumns
                    val verticalBorder = (cellSize * 0.0483f).log { "verticalBorder" }
                    val horizontalBorder = (cellSize * 89.99f/79.93f * 0.0429f).log { "horizontalBorder" }
                    Box(
                        modifier = Modifier
                            .padding(top = 113.fdpv)
                            .fillMaxWidth()
                            .height(100.dp)
                            .alpha(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        RoundedHexagon(
                            modifier = Modifier
                                .width(105.31.fdph)
                                .padding(
                                    vertical = verticalBorder,
                                    horizontal = horizontalBorder
                                )
                                .aspectRatio(79.93f / 89.99f)
                                .then(Modifier.graphicsLayer {
                                    this.shadowElevation = shadowElevation
                                    clip = true
                                    shape = roundedPolygonShape
                                }),
                            onClick = {
                                imagePickerLauncher.launch("image/*")
                            },
                            contentStyle = remember(viewState.mode) {
                                CustomHexagonContentStyle(
                                    id = 0,
                                    content = {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .align(Alignment.Center)
                                                .background(PrimaryColor)
                                        ) {
                                            if (photoUri == null) {
                                                Image(
                                                    modifier = Modifier.align(Alignment.Center),
                                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_profile),
                                                    contentDescription = null,
                                                )
                                            } else {
                                                Image(
                                                    modifier = Modifier
                                                        .fillMaxSize(),
                                                    painter = rememberAsyncImagePainter(photoUri),
                                                    contentScale = ContentScale.Crop,
                                                    contentDescription = null,
                                                )
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .padding(bottom = 7.8.fdpv)
                                                .size(24.fdpv * ((with(LocalDensity.current) { cellWidth!!.toDp() } * scale) / 79.93f.fdpv))
                                                .clip(CircleShape)
                                                .background(PrimaryColor)
                                                .border(
                                                    width = 1.fdpv,
                                                    color = Color.White,
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Image(
                                                modifier = Modifier
                                                    .fillMaxWidth(fraction = 10.18f / 24),
                                                painter = painterResource(R.drawable.ic_edit_only_pen),
                                                contentDescription = "edit profile",
                                            )
                                        }
                                    },
                                    onClick = { _ ->
                                        imagePickerLauncher.launch("image/*")
                                    }
                                )
                            },
                        )
                    }
                }
            }
            if (viewState.mode is ExternalProfileMode.RequestNetwork && (viewState.mode as ExternalProfileMode.RequestNetwork).isSearching) {
                Column(
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
                        var value by remember {
                            mutableStateOf("")
                        }
                        SearchTextField(
                            modifier = Modifier
                                .padding(horizontal = 16.fdpv, vertical = 16.fdph)
                                .padding(bottom = 16.fdpv)
                                .height(56.xdpv),
                            onValueChange = {
                                value = it
                            },
                        )
                    }
                }
            }
        }
    )

    // Show Drill Down Menu Dialog when showDialog is true
    if (showAddFieldDialog) {
        ExpandableDrillDownMenu(
            menuData = fieldsDialogOptions,
            onDismiss = { showAddFieldDialog = false }
        )
    }
}


private fun createCellPosition(row: Int, column: Int) = HexGridCellPosition(
    column = column,
    row = row,
    gridRows = gridRows,
    gridColumns = gridColumns)

private fun getRequestNetworkModeGridItems() = List(gridRows) { row ->
    List(gridColumns) { column ->
        val isCenter = row == centralCellPosition.row && column == centralCellPosition.column

        val cellIndex = createCellPosition(
            column = column,
            row = row,
        ).getIndex()

        val backgroundColor = hexCellsBackgroundColors[cellIndex]

        when {
            isCenter -> IconHexagonContentStyle(
                id = cellIndex,
                background = SingleColor(FacebookColor),
                contentDescription = "LinkedIn",
                image = VectorResource(id = R.drawable.ic_linkedin)
            )
            linkCellPosition.isSame(column, row) -> IconHexagonContentStyle(
                id = cellIndex,
                background = SingleColor(PrimaryColor),
                contentDescription = "Link",
                image = VectorResource(id = R.drawable.ic_link)
            )
            bitcoinCellPosition.isSame(column, row) -> IconHexagonContentStyle(
                id = cellIndex,
                background = Gradient(
                    colors = listOf(Color(0xFFF7931A), Color(0xFFFFE81C)),
                    startOffset = Offset(-75f, 100f),
                    endOffset = Offset(200f, -100f)
                ),
                contentDescription = "Bitcoin",
                image = VectorResource(id = R.drawable.ic_bitcoin)
            )
            facebookCellPosition.isSame(column, row) -> IconHexagonContentStyle(
                id = cellIndex,
                background = SingleColor(FacebookColor),
                contentDescription = "Facebook",
                image = VectorResource(id = R.drawable.ic_facebook)
            )
            messengerCellPosition.isSame(column, row) -> IconHexagonContentStyle(
                id = cellIndex,
                background = SingleColor(MessengerColor),
                contentDescription = "Messenger",
                image = VectorResource(id = R.drawable.ic_messenger)
            )
            instagramCellPosition.isSame(column, row) -> IconHexagonContentStyle(
                id = cellIndex,
                background = SingleColor(InstagramColor),
                contentDescription = "Instagram",
                image = VectorResource(id = R.drawable.ic_instagram)
            )

            emailCellPosition.isSame(column, row) ->IconHexagonContentStyle(
                id = cellIndex,
                background = SingleColor(EmailColor),
                contentDescription = "Email",
                image = VectorResource(id = R.drawable.ic_email)
            )

            phoneCellPosition.isSame(column, row) -> IconHexagonContentStyle(
                id = cellIndex,
                background = SingleColor(PhoneColor),
                contentDescription = "Phone",
                image = VectorResource(id = R.drawable.ic_phone),
                onClick = {
//                    val number = Uri.parse("tel:123456789")
//                    val callIntent = Intent(Intent.ACTION_DIAL, number)
//                    context.startActivity(callIntent)
                }
            )

            twitterCellPosition.isSame(column, row) ->
                IconHexagonContentStyle(
                    id = cellIndex,
                    background = SingleColor(TwitterColor),
                    contentDescription = "Twitter",
                    image = VectorResource(id = R.drawable.ic_twitter)
                )

            whatsappCellPosition.isSame(column, row) -> IconHexagonContentStyle(
                id = cellIndex,
                background = SingleColor(WhatsappColor),
                contentDescription = "Whatsapp",
                image = VectorResource(id = R.drawable.ic_whatsapp)
            )

            telegramCellPosition.isSame(column, row) -> IconHexagonContentStyle(
                id = cellIndex,
                background = SingleColor(TelegramColor),
                contentDescription = "Telegram",
                image = VectorResource(id = R.drawable.ic_telegram)
            )

            skypeCellPosition.isSame(column, row) -> IconHexagonContentStyle(
                id = cellIndex,
                background = SingleColor(TelegramColor),
                contentDescription = "Skype",
                image = VectorResource(id = R.drawable.ic_skype)
            )

            tiktokCellPosition.isSame(column, row) -> IconHexagonContentStyle(
                id = cellIndex,
                background = SingleColor(TiktokColor),
                contentDescription = "TikTok",
                image = VectorResource(id = R.drawable.ic_tiktok)
            )

            ethereumCellPosition.isSame(column, row) -> IconHexagonContentStyle(
                id = cellIndex,
                background = Gradient(
                    colors = listOf(Color.Black, Color.White),
                    startOffset = Offset(-75f, 100f),
                    endOffset = Offset(200f, -100f)
                ),
                contentDescription = "Ethereum",
                image = VectorResource(id = R.drawable.ic_ethereum)
            )

            trailingCellPosition.isSame(column, row) -> TransparentHexagonContentStyle(id = cellIndex,)

            else -> EmptyHexagonContentStyle(id = cellIndex, background = SingleColor(backgroundColor))
        }
    }
}