@file:OptIn(ExperimentalFoundationApi::class, ExperimentalAnimatedInsets::class)

package com.anynetwork.app.ui.screens.myprofile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
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
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.anynetwork.app.R
import com.anynetwork.app.ui.components.HexagonTextField
import com.anynetwork.app.ui.components.HexagonTextFieldClearTrailingIcon
import com.anynetwork.app.ui.components.Screen
import com.anynetwork.app.ui.components.SheetValue
import com.anynetwork.app.ui.components.ToolbarState
import com.anynetwork.app.ui.components.ToolbarStateTitle
import com.anynetwork.app.ui.components.dialog.AlertDialog
import com.anynetwork.app.ui.components.dialog.AlertDialogButtonState
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
import com.anynetwork.app.ui.components.hexagon.NontransparentHexagonContentStyle.Background.SingleColor
import com.anynetwork.app.ui.components.hexagon.PopupHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.RoundedHexagon
import com.anynetwork.app.ui.components.hexagon.RoundedPolygonShape
import com.anynetwork.app.ui.components.hexagon.TransparentHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.createPolygon
import com.anynetwork.app.ui.components.hexagon.hexCellsBackgroundColors
import com.anynetwork.app.ui.components.textfield.ProfileTextFieldLeading
import com.anynetwork.app.ui.navigation.Route
import com.anynetwork.app.ui.screens.externalprofile.VerticalLine
import com.anynetwork.app.ui.screens.myprofile.MyProfileViewEffect.*
import com.anynetwork.app.ui.screens.myprofile.MyProfileViewEvent.SaveButtonClick
import com.anynetwork.app.ui.screens.myprofile.MyProfileViewEvent.UpdateAddress
import com.anynetwork.app.ui.screens.myprofile.MyProfileViewEvent.UpdateCompany
import com.anynetwork.app.ui.screens.myprofile.MyProfileViewEvent.UpdateEmail
import com.anynetwork.app.ui.screens.myprofile.MyProfileViewEvent.UpdateFirstName
import com.anynetwork.app.ui.screens.myprofile.MyProfileViewEvent.UpdateHomeFax
import com.anynetwork.app.ui.screens.myprofile.MyProfileViewEvent.UpdateHomePhone
import com.anynetwork.app.ui.screens.myprofile.MyProfileViewEvent.UpdateLastName
import com.anynetwork.app.ui.screens.myprofile.MyProfileViewEvent.UpdateMainPhone
import com.anynetwork.app.ui.screens.myprofile.MyProfileViewEvent.UpdateMobilePhone
import com.anynetwork.app.ui.screens.myprofile.MyProfileViewEvent.UpdateOtherEmail
import com.anynetwork.app.ui.screens.myprofile.MyProfileViewEvent.UpdateOtherPhone
import com.anynetwork.app.ui.screens.myprofile.MyProfileViewEvent.UpdatePager
import com.anynetwork.app.ui.screens.myprofile.MyProfileViewEvent.UpdatePhotoUri
import com.anynetwork.app.ui.screens.myprofile.MyProfileViewEvent.UpdateWorkEmail
import com.anynetwork.app.ui.screens.myprofile.MyProfileViewEvent.UpdateWorkFax
import com.anynetwork.app.ui.screens.myprofile.MyProfileViewEvent.UpdateWorkPhone
import com.anynetwork.app.ui.theme.DarkBlue
import com.anynetwork.app.ui.theme.EmailColor
import com.anynetwork.app.ui.theme.FacebookColor
import com.anynetwork.app.ui.theme.GreenColor
import com.anynetwork.app.ui.theme.InstagramColor
import com.anynetwork.app.ui.theme.MessengerColor
import com.anynetwork.app.ui.theme.PhoneColor
import com.anynetwork.app.ui.theme.PopupColor
import com.anynetwork.app.ui.theme.PrimaryColor
import com.anynetwork.app.ui.theme.TelegramColor
import com.anynetwork.app.ui.theme.TwitterColor
import com.anynetwork.app.ui.theme.WhatsappColor
import com.anynetwork.app.ui.theme.montserratFontFamily
import com.anynetwork.app.ui.utils.checkSelfPermission
import com.anynetwork.app.ui.utils.fdph
import com.anynetwork.app.ui.utils.fdpv
import com.anynetwork.app.ui.utils.fsp
import com.anynetwork.app.ui.utils.log
import com.anynetwork.app.ui.utils.xdph
import com.anynetwork.app.ui.utils.xdpv
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.yalantis.ucrop.UCrop
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun MyProfileRoot(
    navController: NavHostController,
    onContactUpdated: () -> Unit,
    onBackPress: @Composable () -> Unit
) {
    val viewModel: MyProfileViewModel = hiltViewModel<MyProfileViewModel>()
        .apply {
            loadProfile()
            val viewEffect by viewEffectFlow.collectAsState()
            viewEffect.log { "viewEffect" }
            when (viewEffect) {
                is NavigateBack -> {
                    onBackPress.invoke()
                }
                is ProfileUpdated -> {
                    onContactUpdated.invoke()
                }
                else -> {}
            }
            onViewEvent(MyProfileViewEvent.ClearViewEffect)
        }
    MyProfile(
        viewModel = viewModel,
        onBackButtonClick = {
            navController.popBackStack(Route.Home, inclusive = false)
        }
    )
}

sealed class MyProfileMode {
    data object Normal: MyProfileMode()
    data class Edit(val isCanceling: Boolean = false): MyProfileMode()
}

@Composable
private fun MyProfile(onBackButtonClick: () -> Unit, viewModel: MyProfileViewModel) {
    val scale = 6 / 4.7f

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var cellWidth: Int? by remember { mutableStateOf(null) }
    var cellHeight: Int? by remember { mutableStateOf(null) }
    var trailingCellOffset: Offset? by remember { mutableStateOf(null) }
    var profilePictureCellOffset: Offset? by remember { mutableStateOf(null) }
    var mode: MyProfileMode by remember { mutableStateOf(MyProfileMode.Normal) }
    var layoutHeight: Int = 0
    var collapsedOffset by remember { mutableStateOf(0f) }
    var expandedOffset by remember { mutableStateOf(0f) }
    var currentOffset by remember { mutableStateOf(0f) }
    var showAddFieldDialog by remember { mutableStateOf(false) }
    var fieldsDialogOptions: List<DropDownDialogMenuCategory> by remember { mutableStateOf(listOf()) }
    var anchors: DraggableAnchors<SheetValue>? = null
    val listState = rememberLazyListState()

    val viewState by viewModel.viewState.collectAsState()
    val firstName by remember { derivedStateOf { viewState.firstName } }
    val lastName by remember { derivedStateOf { viewState.lastName } }
    val company by remember { derivedStateOf { viewState.company } }
    val phone by remember { derivedStateOf { viewState.mobilePhone } }
    val mobilePhone by remember { derivedStateOf { viewState.mobilePhone } }
    val homePhone by remember { derivedStateOf { viewState.homePhone } }
    val workPhone by remember { derivedStateOf { viewState.workPhone } }
    val mainPhone by remember { derivedStateOf { viewState.mainPhone } }
    val workFax by remember { derivedStateOf { viewState.workFax } }
    val homeFax by remember { derivedStateOf { viewState.homeFax } }
    val pager by remember { derivedStateOf { viewState.pager } }
    val otherPhone by remember { derivedStateOf { viewState.otherPhone } }
    val homeEmail by remember { derivedStateOf { viewState.homeEmail.log { "email" } } }
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

    BackHandler {
        if (mode is MyProfileMode.Edit) {
            mode = MyProfileMode.Edit(isCanceling = true)
        } else {
            viewModel.onViewEvent(MyProfileViewEvent.BackButtonClick)
        }
    }

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

    var hasReadPhoneStatePermission by remember { mutableStateOf(false) }
    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasReadPhoneStatePermission = permissions[Manifest.permission.READ_PHONE_STATE] ?: false
    }

    val dragPercentage = remember {
        derivedStateOf {
            currentOffset.log { "currentOffset" }
            val percentage = when {
                collapsedOffset != expandedOffset && mode is MyProfileMode.Edit ->
                    ((currentOffset - collapsedOffset) / (expandedOffset - collapsedOffset)) * 100f
                else -> 0f
            }.absoluteValue.log { "percentage" }
            percentage.coerceIn(0f, 100f) // Ensure percentage is between 0 and 100
        }
    }

    // Use this percentage in your UI or log it
    LaunchedEffect(dragPercentage.value) {
        dragPercentage.value.log { "dragPercentage" }
    }

    LaunchedEffect(Unit) {
        hasReadPhoneStatePermission = checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
    }

    val anchoredDraggableState = remember {
        AnchoredDraggableState(
            initialValue = SheetValue.Collapsed,
            positionalThreshold = { 0f },
            velocityThreshold = { 0f },
            snapAnimationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium,
            ),
            decayAnimationSpec = exponentialDecay()
        )
    }

    val cropperLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.let { resultData ->
            val croppedUri = UCrop.getOutput(resultData)
            croppedUri?.let {
                // Use the cropped image URI
                viewModel.onViewEvent(UpdatePhotoUri(it.toString()))
            }
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
            startCrop(context, it)
        }
    }

    // Create a file for the captured image
    val photoFile = remember {
        File(
            context.getExternalFilesDir("Pictures"),
            "IMG_${System.currentTimeMillis()}.jpg"
        )
    }
    val cameraPhotoUri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        photoFile
    )
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            startCrop(context, cameraPhotoUri)
        }
    }

    var isChooseMethodEditProfilePictureDialog by remember { mutableStateOf(false) }
    if (isChooseMethodEditProfilePictureDialog) {
        AlertDialog(
            title = "Edit Profile Picture",
            buttons = listOf(
                AlertDialogButtonState(
                    title = "Take Picture",
                    onClick = {
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                            putExtra(MediaStore.EXTRA_OUTPUT, cameraPhotoUri)
                            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        cameraLauncher.launch(cameraIntent)
                    }
                ),
                AlertDialogButtonState(
                    title = "Select From Gallery",
                    onClick = {
                        imagePickerLauncher.launch("image/*")
                    }
                )
            ),
            onDismiss = {
                isChooseMethodEditProfilePictureDialog = false
            }
        )
    }

    val hazeState = remember { HazeState() }
    val hazeStyle = HazeStyle(
        backgroundColor = DarkBlue,
        tints = listOf(HazeTint(Color.White.copy(alpha = .10f))),
        blurRadius = 8.dp,
    )

    val targetBlur = if (mode is MyProfileMode.Edit && (mode as MyProfileMode.Edit).isCanceling) 20.dp else 0.dp

    // Animate the blur value
    val animatedBlur by animateDpAsState(
        targetValue = targetBlur,
        animationSpec = tween(durationMillis = 300) // You can customize the duration
    )

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
                .height(54.fdpv)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        coroutineScope.launch {
                            anchoredDraggableState.animateTo(SheetValue.Expanded)
                        }
                    }
                },
            focusRequester = focusRequester,
            value = value,
            onValueChange = onValueChange,
            leadingIcon = {
                ProfileTextFieldLeading(label) {
                    fieldsDialogOptions = phoneNumberOptions {
                        when (it) {
                            "Mobile" -> viewModel.onViewEvent(
                                UpdateMobilePhone(
                                    value,
                                    true
                                )
                            )
                            "Work" -> viewModel.onViewEvent(
                                UpdateWorkPhone(
                                    value,
                                    true
                                )
                            )
                            "Home" -> viewModel.onViewEvent(
                                UpdateHomePhone(
                                    value,
                                    true
                                )
                            )
                            "Main" -> viewModel.onViewEvent(
                                UpdateMainPhone(
                                    value,
                                    true
                                )
                            )
                            "Work Fax" -> viewModel.onViewEvent(
                                UpdateWorkFax(
                                    value,
                                    true
                                )
                            )
                            "Home Fax" -> viewModel.onViewEvent(
                                UpdateHomeFax(
                                    value,
                                    true
                                )
                            )
                            "Pager" -> viewModel.onViewEvent(
                                UpdatePager(
                                    value,
                                    true
                                )
                            )
                            "Other" -> viewModel.onViewEvent(
                                UpdateOtherPhone(
                                    value,
                                    true
                                )
                            )
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
                .imePadding()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        coroutineScope.launch {
                            anchoredDraggableState.animateTo(SheetValue.Expanded)
                        }
                    }
                },
            focusRequester = focusRequester,
            value = value,
            onValueChange = onValueChange,
            leadingIcon = {
                ProfileTextFieldLeading(label) {
                    fieldsDialogOptions = emailFieldsOptions {
                        when (it) {
                            "Email" -> viewModel.onViewEvent(
                                UpdateEmail(
                                    value,
                                    true
                                )
                            )
                            "Work" -> viewModel.onViewEvent(
                                UpdateWorkEmail(
                                    value,
                                    true
                                )
                            )
                            "Other" -> viewModel.onViewEvent(
                                UpdateOtherEmail(
                                    value,
                                    true
                                )
                            )
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
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y

                // If the draggable sheet can still move
                return if (delta < 0 || anchoredDraggableState.offset > expandedOffset) {
                    // Consume the gesture for the draggable sheet first
                    val consumed = anchoredDraggableState.dispatchRawDelta(delta)
                    Offset(x = 0f, y = consumed) // Return the consumed delta
                } else {
                    Offset.Zero // Let LazyColumn handle it
                }
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val delta = available.y

                // If there's remaining drag, let the draggable sheet handle it
                val consumed = anchoredDraggableState.dispatchRawDelta(delta)
                return Offset(x = 0f, y = consumed) // Return how much the draggable consumed
            }

            //            override fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
//                // Optionally handle fling here if needed
//                return Velocity.Zero
//            }
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                return Velocity.Zero
            }
        }
    }

    Screen(
        modifier = Modifier.blur(animatedBlur)
            .haze(state = hazeState),
        topBar = ToolbarState.Shown(
            titleState = ToolbarStateTitle.Custom(
                content = {
                    Box(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        contentAlignment = Alignment.Center
                    ) {
                        val alphaAnimationDuration = 700
                        val editHeaderAlpha by animateFloatAsState(
                            targetValue = if (mode is MyProfileMode.Edit) 1f else 0f,
                            animationSpec = tween(alphaAnimationDuration)
                        )
                        Text(
                            modifier = Modifier.alpha(editHeaderAlpha),
                            text = "Edit",
                            textAlign = TextAlign.Center,
                            color = Color(0xFFFFFFFF),
                            style = TextStyle(
                                fontFamily = montserratFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.fsp,
                            ),
                        )

                        Image(
                            modifier = Modifier.alpha(1 - editHeaderAlpha),
                            painter = painterResource(R.drawable.ic_any_network),
                            contentDescription = "notifications action icon",
                        )
                    }
                }
            ),
        ),
        hexagonGrid = {
            val gridColumns = 6
            val gridRows = 14
            fun createOnboardingCellPosition(row: Int, column: Int) = HexGridCellPosition(
                column = column,
                row = row,
                gridRows = gridRows,
                gridColumns = gridColumns
            )

            val centralCellPosition = HexGridCellPosition(column = (gridColumns / 2) - 1, row = (gridRows / 2),
                gridRows = gridRows,
                gridColumns = gridColumns,
            )
            val trailingCellPosition = HexGridCellPosition(column = gridColumns - 3, row = gridRows - 3,
                gridRows = gridRows,
                gridColumns = gridColumns,
            )
            val profilePictureCellPosition = HexGridCellPosition(
                column = centralCellPosition.column,
                row = centralCellPosition.row - 2,
                gridRows = gridRows,
                gridColumns = gridColumns,
            )
            val facebookCellPosition = profilePictureCellPosition.getNeighborPosition(TopLeft)
            val messengerCellPosition = profilePictureCellPosition.getNeighborPosition(TopRight)
            val instagramCellPosition = profilePictureCellPosition.getNeighborPosition(Right)
            val emailCellPosition = profilePictureCellPosition.getNeighborPosition(BottomRight)
            val phoneCellPosition = profilePictureCellPosition.getNeighborPosition(BottomLeft)
            val twitterCellPosition = profilePictureCellPosition.getNeighborPosition(Left)
            val whatsappCellPosition = centralCellPosition.getNeighborPosition(BottomRight)
            val telegramCellPosition = centralCellPosition.getNeighborPosition(BottomLeft)

            val itemAlpha by animateFloatAsState(
                targetValue = if (mode is MyProfileMode.Edit) .3f else 1f,
                animationSpec = tween(300)
            )

            // Find the central element
            val items = List(gridRows * gridColumns) { index ->
                val row = index / gridColumns
                val column = index % gridColumns
                val isCenter = row == centralCellPosition.row && column == centralCellPosition.column
                val cellIndex = createOnboardingCellPosition(
                    column = column,
                    row = row,
                ).getIndex()

                val alpha = 1 - dragPercentage.value/100f
                val backgroundColor = hexCellsBackgroundColors[cellIndex]

                when {
                    isCenter -> remember(mode, alpha) {
                        IconHexagonContentStyle(
                            id = cellIndex,
                            modifier = Modifier
                                .fillMaxWidth(1 / 2f)
                                .fillMaxSize(43f / 80),
                            background = SingleColor(Color(0xFF393939).copy(alpha)),
                            contentDescription = "Any network",
                            alpha = alpha,
                            image = VectorResource(id = R.drawable.ic_any_network),
                            isShakable = true,
                        )
                    }
                    profilePictureCellPosition.isSame(column, row) -> remember(mode) {
//                            TransparentHexagonContentStyle(id = cellIndex,)
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
                                                .fillMaxSize(0.335f),
                                            painter = rememberAsyncImagePainter(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(R.drawable.ic_profile)
                                                    .size(Size(580, 660))
                                                    .build()
                                            ),
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
                                if (mode is MyProfileMode.Edit) {
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
                                    if (photoUri != null) DeleteButton(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(top = 17.31.fdpv, end = 1.fdpv)
                                            .size(24.fdpv * (LocalConfiguration.current.screenWidthDp.dp / gridColumns / 79.93f.fdpv))
                                            .alpha(alpha),
                                        onClick = {
                                            viewModel.onViewEvent(MyProfileViewEvent.RemoveProfilePicture)
                                        }
                                    )
                                }
                            },
                            isShakable = true,
                            onClick = { _ ->
                                if (mode is MyProfileMode.Edit) {
                                    isChooseMethodEditProfilePictureDialog = true
                                }
                            },
                        )
                    }
                    facebookCellPosition.isSame(column, row) -> remember(mode, itemAlpha, alpha) {
                        IconHexagonContentStyle(
                            id = cellIndex,
                            background = SingleColor(FacebookColor.copy(alpha = itemAlpha * alpha)),
                            alpha = itemAlpha * alpha,
                            contentDescription = "Facebook",
                            isShakable = true,
                            image = VectorResource(id = R.drawable.ic_facebook),
                            overlay =
                            if (mode is MyProfileMode.Edit) {
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
                        )
                    }
                    messengerCellPosition.isSame(column, row) -> remember(mode, itemAlpha, alpha) {
                        IconHexagonContentStyle(
                            id = cellIndex,
                            background = SingleColor(MessengerColor.copy(alpha = itemAlpha * alpha)),
                            alpha = itemAlpha * alpha,
                            contentDescription = "Messenger",
                            image = VectorResource(id = R.drawable.ic_messenger),
                            isShakable = true,
                            overlay = if (mode is MyProfileMode.Edit) {
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
                        )
                    }
                    instagramCellPosition.isSame(column, row) -> remember(mode, itemAlpha, alpha) {
                        IconHexagonContentStyle(
                            id = cellIndex,
                            background = SingleColor(InstagramColor.copy(alpha = itemAlpha * alpha)),
                            alpha = itemAlpha * alpha,
                            contentDescription = "Instagram",
                            image = VectorResource(id = R.drawable.ic_instagram),
                            isShakable = true,
                            overlay = if (mode is MyProfileMode.Edit) {
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
                        )
                    }
                    emailCellPosition.isSame(column, row) -> remember(mode, itemAlpha, alpha) {
                        IconHexagonContentStyle(
                            id = cellIndex,
                            background = SingleColor(EmailColor.copy(alpha = itemAlpha * alpha)),
                            alpha = itemAlpha * alpha,
                            contentDescription = "Email",
                            image = VectorResource(id = R.drawable.ic_email),
                            isShakable = true,
                            overlay = if (mode is MyProfileMode.Edit) {
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
                        )
                    }
                    phoneCellPosition.isSame(column, row) -> remember(mode, itemAlpha, alpha) {
                        IconHexagonContentStyle(
                            id = cellIndex,
                            background = SingleColor(PhoneColor.copy(alpha = itemAlpha * alpha)),
                            alpha = itemAlpha * alpha,
                            contentDescription = "Phone",
                            image = VectorResource(id = R.drawable.ic_phone),
                            isShakable = true,
                            overlay = if (mode is MyProfileMode.Edit) {
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
                        )
                    }
                    twitterCellPosition.isSame(column, row) -> remember(mode, itemAlpha, alpha) {
                        IconHexagonContentStyle(
                            id = cellIndex,
                            background = SingleColor(TwitterColor.copy(alpha = itemAlpha * alpha)),
                            alpha = itemAlpha * alpha,
                            contentDescription = "Twitter",
                            image = VectorResource(id = R.drawable.ic_twitter),
                            isShakable = true,
                            overlay = if (mode is MyProfileMode.Edit) {
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
                        )
                    }
                    whatsappCellPosition.isSame(column, row) -> remember(mode, itemAlpha, alpha) {
                        IconHexagonContentStyle(
                            id = cellIndex,
                            background = SingleColor(WhatsappColor.copy(alpha = itemAlpha * alpha)),
                            alpha = itemAlpha * alpha,
                            contentDescription = "Whatsapp",
                            image = VectorResource(id = R.drawable.ic_whatsapp),
                            isShakable = true,
                            overlay = if (mode is MyProfileMode.Edit) {
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
                        )
                    }
                    telegramCellPosition.isSame(column, row) -> remember(mode, itemAlpha, alpha) {
                        IconHexagonContentStyle(
                            id = cellIndex,
                            background = SingleColor(TelegramColor.copy(alpha = itemAlpha * alpha)),
                            alpha = itemAlpha * alpha,
                            contentDescription = "Telegram",
                            image = VectorResource(id = R.drawable.ic_telegram),
                            isShakable = true,
                            overlay = if (mode is MyProfileMode.Edit) {
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
                        )
                    }

                    trailingCellPosition.isSame(column, row) -> remember {
                        TransparentHexagonContentStyle(id = cellIndex,)
                    }
                    else -> remember(alpha) {
                        EmptyHexagonContentStyle(
                            id = cellIndex,
                            background = SingleColor(backgroundColor.copy(alpha = backgroundColor.alpha * alpha))
                        )
                    }
                }
            }

            HexagonalGrid(
                modifier = Modifier,
                itemsList = items,
                rowSize = gridColumns,
                columnSize = gridRows,
                minScale = scale,
                onCellPositionCalculated = { index, offset, width, height ->
                    if (cellWidth == null) cellWidth = width
                    if (cellHeight == null) cellHeight = height
                    if (index == trailingCellPosition.getIndex()) {
                        if (trailingCellOffset == null) trailingCellOffset = offset
                    } else if (index == profilePictureCellPosition.getIndex()) {
                        if (profilePictureCellOffset == null) profilePictureCellOffset = offset
                    }
                },
                isScrollEnabled = false,
                offsetY = when {
                    profilePictureCellOffset == null -> 0f
                    else -> - (profilePictureCellOffset!!.y - with(LocalDensity.current) {
                        113f.fdpv.toPx()
                    }) * (dragPercentage.value/100).log { "hex grid offset" }
                }.roundToInt(),
                isEditModeActivating = mode is MyProfileMode.Edit,
            )
        },
        applyInnerPaddingToContent = false,
        content = {
            val density = LocalDensity.current
            if (cellHeight != null && mode is MyProfileMode.Edit) {
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
                        .anchoredDraggable(
                            anchoredDraggableState,
                            orientation = Orientation.Vertical,
                        )
                        .nestedScroll(nestedScrollConnection)
                        .onSizeChanged { sheetSize ->
                            val sheetHeight = sheetSize.height
                            anchors = DraggableAnchors {
                                with(density) {
                                    collapsedOffset =
                                        layoutHeight - 380.fdpv.toPx() // Capture collapsed offset
                                    expandedOffset = maxOf(
                                        layoutHeight - sheetHeight + 92.fdpv.toPx(),
                                        0f
                                    )  // Capture expanded offset
                                    SheetValue.Collapsed at collapsedOffset.log { "collapsed" }
                                    SheetValue.Expanded at expandedOffset.log { "expanded" }
                                }
                            }
                            anchoredDraggableState.updateAnchors(
                                anchors!!,
                                anchoredDraggableState.targetValue
                            )
                        }
//                        .verticalScroll(rememberScrollState())
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

                        val paddingBottom = with(density) { expandedOffset.toDp() }//if (mode !is ExternalProfileMode.NewContact) with(density) { expandedOffset.toDp() } else 0.dp
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 160.fdpv, bottom = paddingBottom)
                                .padding(horizontal = 16.fdph)
                                .graphicsLayer { alpha = 0.99f }
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
                            userScrollEnabled = true,
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
                                            .height(54.fdpv)
                                            .onFocusChanged { focusState ->
                                                if (focusState.isFocused) {
                                                    coroutineScope.launch {
                                                        anchoredDraggableState.animateTo(SheetValue.Expanded)
                                                    }
                                                }
                                            },
                                        value = firstName,
                                        onValueChange = { newValue ->
                                            viewModel.onViewEvent(
                                                UpdateFirstName(newValue ?: "")
                                            )
                                        },
                                        placeholder = "First Name"
                                    )

                                    Spacer(modifier = Modifier.width(15.fdph))

                                    HexagonTextField(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(54.fdpv)
                                            .onFocusChanged { focusState ->
                                                if (focusState.isFocused) {
                                                    coroutineScope.launch {
                                                        anchoredDraggableState.animateTo(SheetValue.Expanded)
                                                    }
                                                }
                                            },
                                        value = lastName,
                                        onValueChange = { newValue ->
                                            viewModel.onViewEvent(
                                                UpdateLastName(newValue ?: "")
                                            )
                                        },
                                        placeholder = "Last Name"
                                    )
                                }
                            }

                            item {HexagonTextField(
                                modifier = Modifier
                                    .padding(top = 16.fdpv)
                                    .height(54.fdpv)
                                    .onFocusChanged { focusState ->
                                        if (focusState.isFocused) {
                                            coroutineScope.launch {
                                                anchoredDraggableState.animateTo(SheetValue.Expanded)
                                            }
                                        }
                                    },
                                value = company,
                                onValueChange = { newValue ->
                                    viewModel.onViewEvent(UpdateCompany(newValue ?: ""))
                                },
                                placeholder = "Company"
                            )
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
                                        if (viewModel.viewEffectFlow.value is RequestFocusOnMobilePhoneTextField) {
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
                                        if (viewModel.viewEffectFlow.value is RequestFocusOnHomePhoneTextField) {
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
                                        if (viewModel.viewEffectFlow.value is RequestFocusOnWorkPhoneTextField) {
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
                                        if (viewModel.viewEffectFlow.value is RequestFocusOnMainPhoneTextField) {
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
                                        if (viewModel.viewEffectFlow.value is RequestFocusOnWorkFaxTextField) {
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
                                        if (viewModel.viewEffectFlow.value is RequestFocusOnWorkFaxTextField) {
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
                                        viewModel.onViewEvent(
                                            UpdatePager(it)
                                        )
                                    }
                                    LaunchedEffect(pager) {
                                        if (viewModel.viewEffectFlow.value is RequestFocusOnPagerTextField) {
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
                                        if (viewModel.viewEffectFlow.value is RequestFocusOnOtherPhoneTextField) {
                                            otherPhoneFocusRequester.requestFocus()
                                        }
                                    }
                                }
                            }

                            homeEmail?.let { email ->
                                item {
                                    createEmailTextField(
                                        label = "Email",
                                        focusRequester = emailFocusRequester,
                                        value = email
                                    ) {
                                        viewModel.onViewEvent(UpdateEmail(it))
                                    }
                                    LaunchedEffect(email) {
                                        if (viewModel.viewEffectFlow.value is RequestFocusOnEmailTextField) {
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
                                        if (viewModel.viewEffectFlow.value is RequestFocusOnWorkEmailTextField) {
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
                                        if (viewModel.viewEffectFlow.value is RequestFocusOnOtherEmailTextField) {
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
                                                                UpdateAddress(
                                                                    ""
                                                                )
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
        }
    )


    val polygon = remember { createPolygon() }
    val roundedPolygonShape = remember { RoundedPolygonShape(polygon) }

    val targetАlpha = if (mode is MyProfileMode.Edit && (mode as MyProfileMode.Edit).isCanceling) 1f else 0f
    val animatedAlpha by animateFloatAsState(
        targetValue = targetАlpha,
        animationSpec = tween(durationMillis = 300)
    )

    // Animate scale (zoom effect)
    val targetScale = if (mode is MyProfileMode.Edit && (mode as MyProfileMode.Edit).isCanceling) 1f else 0f
    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = 300)
    )

    Box(modifier = Modifier
        .fillMaxSize()
        .let { baseModifier ->
            if (mode is MyProfileMode.Edit && (mode as MyProfileMode.Edit).isCanceling) {
                baseModifier.clickable {
                    mode = MyProfileMode.Edit(isCanceling = false)
                }
            } else {
                baseModifier
            }
        }) {
        RoundedHexagon(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 16.fdph)
                .fillMaxWidth()
                .aspectRatio(79.93.xdph / 89.99.xdpv)
                .alpha(animatedAlpha)
                .scale(animatedScale)
                .then(Modifier.graphicsLayer {
                    this.shadowElevation = shadowElevation
                    clip = true
                    shape = roundedPolygonShape
                }),
            contentStyle = PopupHexagonContentStyle(
                id = 0,
                background = SingleColor(PopupColor),
                message = "Do you want to discard changes?",
                options = listOf(
                    PopupHexagonContentStyle.Option(
                        title = "Yes",
                        message = "Discard Changes",
                        onClick = {
                            coroutineScope.launch {
//                                anchoredDraggableState.updateAnchors(
//                                    anchors!!,
//                                    SheetValue.PartiallyExpanded
//                                )
                                mode = MyProfileMode.Normal
                                currentOffset = 0f
                                anchoredDraggableState.animateTo(SheetValue.Collapsed)
                            }
                        }
                    ),
                    PopupHexagonContentStyle.Option(
                        title = "No",
                        message = "Keep Editing",
                        onClick = {
                            mode = MyProfileMode.Edit()
                        }
                    )
                )
                ),
        )
    }

    // Show Drill Down Menu Dialog when showDialog is true
    if (showAddFieldDialog) {
        ExpandableDrillDownMenu(
            menuData = fieldsDialogOptions,
            onDismiss = { showAddFieldDialog = false }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                    .width(46.fdph),
                colors = CardColors(Color.Transparent, Color.Transparent, Color.Transparent, Color.Transparent),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 24.fdph,
                    bottomEnd = 24.fdph,
                    bottomStart = 0.dp
                )
            ) {
                Box(modifier = Modifier.fillMaxSize()
                    .hazeChild(
                        state = hazeState,
                        style = hazeStyle
                    )
                ) {
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.Center),
                        onClick = {
                            if (mode is MyProfileMode.Edit) {
                                mode = MyProfileMode.Edit(isCanceling = true)
                            } else {
                                viewModel.onViewEvent(MyProfileViewEvent.BackButtonClick)
                            }
                        }
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize().padding(vertical = 10.fdpv),
                            painter = painterResource(R.drawable.ic_arrow_left),
                            contentDescription = "back button",
                        )
                    }
                }
            }

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
                            state = hazeState,
                            style = hazeStyle
                        )
                        .padding(horizontal = 9.fdph),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        modifier = Modifier,
                        onClick = {}
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize().padding(vertical = 10.fdpv),
                            painter = painterResource(R.drawable.ic_rounded_plus_2),
                            contentDescription = "back button",
                        )
                    }

                    VerticalLine()

                    IconButton(
                        modifier = Modifier,
                        onClick = {
                            if (mode !is MyProfileMode.Edit) {
                                mode = MyProfileMode.Edit()
                            } else if (mode is MyProfileMode.Edit) {
                                if (!hasReadPhoneStatePermission) {
                                    viewModel.onViewEvent(SaveButtonClick)
                                    mode = MyProfileMode.Normal
                                }
                            }
                        }
                    ) {
                        val alphaAnimationDuration = 300
                        val editButtonAlpha by animateFloatAsState(
                            targetValue = if (mode is MyProfileMode.Edit) 0f else 1f,
                            animationSpec = tween(alphaAnimationDuration)
                        )
                        Image(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 10.fdpv)
                                .alpha(editButtonAlpha),
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = "back button",
                        )

                        Image(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 10.fdpv)
                                .alpha(1- editButtonAlpha),
                            painter = rememberVectorPainter(Icons.Outlined.Check),
                            colorFilter = ColorFilter.tint(Color.White),
                            contentDescription = "back button",
                        )
                    }
                }
            }
        }
    }
}

fun Modifier.offsetToAvoidKeyboard(): Modifier = composed {
    var yOffset by remember { mutableStateOf(0f) }
    val density = LocalDensity.current

    onGloballyPositioned { coordinates ->
        val windowSize = coordinates.findRootCoordinates().size.height
        val bottomY = coordinates.positionInWindow().y + coordinates.size.height

        // Calculate the distance the field is obscured by the keyboard
        val overlap = (bottomY - windowSize).toInt()

        // Ensure we only offset when the field is actually obscured by the keyboard
        yOffset = if (overlap > 0) {
            overlap.toFloat() / density.density  // Convert overlap to dp
        } else {
            0f
        }
    }

    this.offset { IntOffset(0, -yOffset.roundToInt()) }  // Move field up if obscured
}
