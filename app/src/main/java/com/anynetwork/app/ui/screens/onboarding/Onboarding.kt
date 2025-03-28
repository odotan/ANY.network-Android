@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.anynetwork.app.ui.screens.onboarding

import android.content.Context
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.anynetwork.app.R
import com.anynetwork.app.ui.components.Screen
import com.anynetwork.app.ui.components.hexagon.CoverBox
import com.anynetwork.app.ui.components.hexagon.CustomHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.EmptyHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.HexGridCellPosition
import com.anynetwork.app.ui.components.hexagon.HexGridCellPosition.Neighbor.BottomLeft
import com.anynetwork.app.ui.components.hexagon.HexGridCellPosition.Neighbor.BottomRight
import com.anynetwork.app.ui.components.hexagon.HexGridCellPosition.Neighbor.Left
import com.anynetwork.app.ui.components.hexagon.HexGridCellPosition.Neighbor.Right
import com.anynetwork.app.ui.components.hexagon.HexGridCellPosition.Neighbor.TopLeft
import com.anynetwork.app.ui.components.hexagon.HexGridCellPosition.Neighbor.TopRight
import com.anynetwork.app.ui.components.hexagon.HexagonalGrid
import com.anynetwork.app.ui.components.hexagon.NontransparentHexagonContentStyle.*
import com.anynetwork.app.ui.components.hexagon.RoundedHexagon
import com.anynetwork.app.ui.components.hexagon.RoundedPolygonShape
import com.anynetwork.app.ui.components.hexagon.hexCellsBackgroundColors
import com.anynetwork.app.ui.components.hexagon.createPolygon
import com.anynetwork.app.ui.navigation.Route
import com.anynetwork.app.ui.screens.home.showHomeCover
import com.anynetwork.app.ui.theme.EmailColor
import com.anynetwork.app.ui.theme.FacebookColor
import com.anynetwork.app.ui.theme.InstagramColor
import com.anynetwork.app.ui.theme.MessengerColor
import com.anynetwork.app.ui.theme.PhoneColor
import com.anynetwork.app.ui.theme.TelegramColor
import com.anynetwork.app.ui.theme.TwitterColor
import com.anynetwork.app.ui.theme.WhatsappColor
import com.anynetwork.app.ui.theme.montserratFontFamily
import com.anynetwork.app.ui.utils.csp
import com.anynetwork.app.ui.utils.fdph
import com.anynetwork.app.ui.utils.fdpv
import com.anynetwork.app.ui.utils.log
import com.anynetwork.app.ui.utils.xdph
import com.anynetwork.app.ui.utils.xdpv
import kotlinx.coroutines.delay

@Composable
fun OnboardingRoot(navController: NavHostController) {
    Onboarding(navController)
}

data class OnboardingImageCellConfig(val resource: Int, val background: Color)

@Composable
private fun Onboarding(navController: NavHostController) {
    val scale = 6f/4.7f

    val context = LocalContext.current
    var centralIndex: Int? by remember { mutableStateOf(null) }
    var centralOffset: Offset? by remember { mutableStateOf(null) }
    var centralWidth: Int? by remember { mutableStateOf(null) }
    var centralHeight: Int? by remember { mutableStateOf(null) }

    var markHamlinCellOffset: Offset? by remember { mutableStateOf(null) }

    var isCoverVisible by remember { mutableStateOf(true) }
    var mainMessage by remember { mutableStateOf("You are in the center") }
    var topMessage by remember { mutableStateOf("ANY network is a new way to interact with your contacts") }
    var showTopMessageAtTop by remember { mutableStateOf(false) }
    var showDefaultAvatarItemAtCenter by remember { mutableStateOf(false) }
    var showMainMessage by remember { mutableStateOf(false) }
    var paddingTopCenterMessage by remember { mutableStateOf(21.fdpv) }
    var showFirstGroupOfContacts by remember { mutableStateOf(false) }
    var showSecondGroupOfContacts by remember { mutableStateOf(false) }
    var showOtherContactAvatar by remember { mutableStateOf(false) }
    var showThirdGroupOfContacts by remember { mutableStateOf(false) }
    var blurContacts by remember { mutableStateOf(0.dp) }
    var slideOtherContactAvatarToCenter by remember { mutableStateOf(false) }
    var showMarkHamlinItems by remember { mutableStateOf(false) }
    var showContactFlower by remember { mutableStateOf(false) }

    val coverAnimationDuration = 700
    val topMessageAnimationShowAnyNetworkIsANewWayDuration = 800
    var topMessageAnimationHideAnyNetworkIsANewWayDuration = 800
    var showDefaultAvatarItemAtCenterAnimationDuration = 300
    var centerMessageAlphaAnimationDuration = 400
    var centerMessageAlphaAnimationDurationAnimationDuration = 2000
    var showFirstGroupOfContactsAnimationDuration = 1000
    var showSecondGroupOfContactsAnimationDuration = 1000
    var showThirdGroupOfContactsAnimationDuration = 800
    var blurContactsAnimationDuration = 1000
    var slideOtherContactAvatarToCenterAnimationDuration = 800
    var showContactFlowerAnimationDuration = 800

    val fastPace = false//BuildConfig.DEBUG
    val fastPaceDelay = 200L

    val currentConfiguration = LocalConfiguration.current
    currentConfiguration.densityDpi.log { "densityDpi" }
    val currentDensity = LocalDensity.current

    LaunchedEffect(Unit) {
        // Defer the animation start to improve performance
        delay(if (fastPace) fastPaceDelay else 300)
        isCoverVisible = false
        delay(800 + coverAnimationDuration.toLong())

        showTopMessageAtTop = true
        delay(if (fastPace) fastPaceDelay else (2000 + topMessageAnimationShowAnyNetworkIsANewWayDuration.toLong()))

        showTopMessageAtTop = false
        delay(if (fastPace) fastPaceDelay else (600 + topMessageAnimationHideAnyNetworkIsANewWayDuration.toLong()))

        showDefaultAvatarItemAtCenter = true
        delay(if (fastPace) fastPaceDelay else (1000 + showDefaultAvatarItemAtCenterAnimationDuration.toLong()))

        showMainMessage = true
        delay(if (fastPace) fastPaceDelay else (1500 + centerMessageAlphaAnimationDuration.toLong()))

        showMainMessage = false
        delay(if (fastPace) fastPaceDelay else (1000 + centerMessageAlphaAnimationDuration.toLong()))

        centerMessageAlphaAnimationDuration = 1000
        mainMessage = "And your contacts appear around you as you interact with them"
        showMainMessage = true
        delay(if (fastPace) fastPaceDelay else (1500 + centerMessageAlphaAnimationDuration.toLong()))

//        paddingTopCenterMessage = 0.fdpv
//        paddingTopCenterMessage = 400.fdpv
        paddingTopCenterMessage = currentConfiguration.screenHeightDp.dp -
                with(currentDensity) { (centralOffset!!.y).toDp() } -
                with(currentDensity) { centralHeight!!.toDp() } * scale - 70.fdpv

        showFirstGroupOfContacts = true
        delay(if (fastPace) fastPaceDelay else (600 + centerMessageAlphaAnimationDurationAnimationDuration.toLong()))

        showSecondGroupOfContacts = true
        showOtherContactAvatar = true
        delay(if (fastPace) fastPaceDelay else (600 + showSecondGroupOfContactsAnimationDuration.toLong()))

        centerMessageAlphaAnimationDuration = showThirdGroupOfContactsAnimationDuration
        showThirdGroupOfContacts = true
        showMainMessage = false
        delay(if (fastPace) fastPaceDelay else (600 + showThirdGroupOfContactsAnimationDuration.toLong()))

        blurContacts = 10.dp
        centerMessageAlphaAnimationDuration = blurContactsAnimationDuration
        mainMessage = "Tap a person to view their card"
        showMainMessage = true
        delay(if (fastPace) fastPaceDelay else (600 + blurContactsAnimationDuration.toLong()))

        centerMessageAlphaAnimationDuration = slideOtherContactAvatarToCenterAnimationDuration
        blurContactsAnimationDuration = slideOtherContactAvatarToCenterAnimationDuration
        showFirstGroupOfContactsAnimationDuration = slideOtherContactAvatarToCenterAnimationDuration
        showSecondGroupOfContactsAnimationDuration = slideOtherContactAvatarToCenterAnimationDuration
        showThirdGroupOfContactsAnimationDuration = slideOtherContactAvatarToCenterAnimationDuration
        slideOtherContactAvatarToCenter = true
        showMainMessage = false
        blurContacts = 0.dp
        showFirstGroupOfContacts = false
        showSecondGroupOfContacts = false
        showThirdGroupOfContacts = false
        delay(200 + slideOtherContactAvatarToCenterAnimationDuration.toLong())

        showMarkHamlinItems = true
        showContactFlower = true
        topMessage = "Mark Hamlin"
        centerMessageAlphaAnimationDuration = 1000
        showTopMessageAtTop = true
        delay(800 + centerMessageAlphaAnimationDuration.toLong())

        mainMessage = "Then launch an app to interact with this person in that app."
        showMainMessage = true
        showDefaultAvatarItemAtCenterAnimationDuration = 100
        showContactFlowerAnimationDuration = 1500
        centerMessageAlphaAnimationDuration = 1500
        topMessageAnimationHideAnyNetworkIsANewWayDuration = 1500
        slideOtherContactAvatarToCenterAnimationDuration = 1500
        delay(1500 + showContactFlowerAnimationDuration.toLong())
        showDefaultAvatarItemAtCenter = false
        showContactFlower = false
        showOtherContactAvatar = false
        showMainMessage = false
        showTopMessageAtTop = false

        delay(1500 + showContactFlowerAnimationDuration.toLong())

        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("onboardingCompleted", true).apply()

        showHomeCover = false

        navController.navigate(Route.Home)
    }

    Screen(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF120E1E),
        hexagonGrid = {
            val gridColumns = 6
            val gridRows = 14
            val centralRowIndex = 5
            val centralColumnIndex = (gridColumns / 2) - 1
            val centralPosition = HexGridCellPosition(
                column = centralColumnIndex,
                row = centralRowIndex,
                gridRows = gridRows,
                gridColumns = gridColumns)
            centralIndex = centralPosition.getIndex()

            fun createOnboardingCellPosition(row: Int, column: Int) = HexGridCellPosition(
                column = column,
                row = row,
                gridRows = gridRows,
                gridColumns = gridColumns)

            val firstGroup = mapOf(
                22 to OnboardingImageCellConfig(
                    resource = R.drawable.sixth_onboarding_contact_avatar,
                    background = Color(0xFFFF6B6B)),
                26 to OnboardingImageCellConfig(
                    resource = R.drawable.seventh_onboarding_contact_avatar,
                    background = Color(0xFFADF2E6)),
                39 to OnboardingImageCellConfig(
                    resource = R.drawable.twelveth_onboarding_contact_avatar,
                    background = Color(0xFF8DEF9D)),
                48 to OnboardingImageCellConfig(
                    resource = R.drawable.eighteenth_onboarding_contact_avatar,
                    background = Color(0xFF21AA47)),
                52 to OnboardingImageCellConfig(
                    resource = R.drawable.twentyfirst_onboarding_contact_avatar,
                    background = Color(0xFF8481FE)),
                55 to OnboardingImageCellConfig(
                    resource = R.drawable.twentysecond_onboarding_contact_avatar,
                    background = Color(0xFFF2EBAD)),
            )
            val secondGroup = mapOf(
                13 to OnboardingImageCellConfig(
                    resource = R.drawable.first_onboarding_contact_avatar,
                    background = Color(0xFF388AD1)),
                15 to OnboardingImageCellConfig(
                    resource = R.drawable.second_onboarding_contact_avatar,
                    background = Color(0xFFF2EBAD)),
                19 to OnboardingImageCellConfig(
                    resource = R.drawable.third_onboarding_contact_avatar,
                    background = Color(0xFFF2ADAD)),
                27 to OnboardingImageCellConfig(
                    resource = R.drawable.eight_onboarding_contact_avatar,
                    background = Color(0xFFF2EBAD)),
                28 to OnboardingImageCellConfig(
                    resource = R.drawable.ninth_onboarding_contact_avatar,
                    background = Color(0xFFD8A033)
                ),
                31 to OnboardingImageCellConfig(
                    resource = R.drawable.tenth_onboarding_contact_avatar,
                    background = Color(0xFF97B6F2)
                ),
                42 to OnboardingImageCellConfig(
                    resource = R.drawable.fourteenth_onboarding_contact_avatar,
                    background = Color(0xFF97B6F2)),
                45 to OnboardingImageCellConfig(
                    resource = R.drawable.seventeenth_onboarding_contact_avatar,
                    background = Color(0xFFD8A033)),
                57 to OnboardingImageCellConfig(
                    resource = R.drawable.twentythird_onboarding_contact_avatar,
                    background = Color(0xFF7CD0FF)),
                62 to OnboardingImageCellConfig(
                    resource = R.drawable.twentyfourth_onboarding_contact_avatar,
                    background = Color(0xFFE8ADF2)),
            )
            val thirdGroup = mapOf(
                20 to OnboardingImageCellConfig(
                    resource = R.drawable.fourth_onboarding_contact_avatar,
                    background = Color(0xFFFD95FF)),
                21 to OnboardingImageCellConfig(
                    resource = R.drawable.fifth_onboarding_contact_avatar,
                    background = Color(0xFFFFFFFF)),
                33 to OnboardingImageCellConfig(
                    resource = R.drawable.eleventh_onboarding_contact_avatar,
                    background = Color(0xFF7CD0FF)
                ),
                40 to OnboardingImageCellConfig(
                    resource = R.drawable.thirteenth_onboarding_contact_avatar,
                    background = Color(0xFFF87D90)),
                43 to OnboardingImageCellConfig(
                    resource = R.drawable.fifteenth_onboarding_contact_avatar,
                    background = Color(0xFFF2EBAD)),
                50 to OnboardingImageCellConfig(
                    resource = R.drawable.nineteenth_onboarding_contact_avatar,
                    background = Color(0xFFADF2E6)),
                51 to OnboardingImageCellConfig(
                    resource = R.drawable.twentieth_onboarding_contact_avatar,
                    background = Color(0xFFFF9DF5)),
            )

            // Find the central element
            val items = List(gridRows * gridColumns) { index ->
                val row = index / gridColumns
                val column = index % gridColumns

                val initialOverlay = @Composable {

                }

                val cellIndex = createOnboardingCellPosition(
                    column = column,
                    row = row,
                ).getIndex()

                val backgroundColor = hexCellsBackgroundColors[cellIndex]

                if (firstGroup.keys.contains(cellIndex)) {
                    val alpha by animateFloatAsState(
                        targetValue = if (showFirstGroupOfContacts) 1f else 0f,
                        animationSpec = tween(showFirstGroupOfContactsAnimationDuration),
                    )
                    remember(showFirstGroupOfContacts) {
                        CustomHexagonContentStyle(
                            id = cellIndex,
                            background = Background.SingleColor(backgroundColor),
                            content = {
                                initialOverlay.invoke()
                                firstGroup[cellIndex]?.let {
                                    Box(modifier = Modifier
                                        .fillMaxSize()
                                        .background(it.background.copy(alpha = alpha))
                                        .alpha(alpha)
                                    ) {
                                        Image(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .fillMaxSize()
                                                .padding(top = 10.fdpv),
                                            painter = painterResource(
                                                id = firstGroup[cellIndex]?.resource ?: -1
                                            ),
                                            contentDescription = null,
                                        )
                                    }
                                }
                            }
                        )
                    }
                } else if (secondGroup.keys.contains(cellIndex)) {
                    val alpha by animateFloatAsState(
                        targetValue = if (showSecondGroupOfContacts) 1f else 0f,
                        animationSpec = tween(showSecondGroupOfContactsAnimationDuration),
                    )
                    remember(showSecondGroupOfContacts) {
                        CustomHexagonContentStyle(
                            id = cellIndex,
                            background = Background.SingleColor(backgroundColor),
                            content = {
                                initialOverlay.invoke()
                                secondGroup[cellIndex]?.let {
                                    Box(modifier = Modifier
                                        .fillMaxSize()
                                        .background(it.background.copy(alpha = alpha))
                                        .alpha(alpha)
                                    ) {
                                        Image(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .fillMaxSize()
                                                .padding(top = 10.fdpv),
                                            painter = painterResource(
                                                id = secondGroup[cellIndex]?.resource ?: -1
                                            ),
                                            contentDescription = null,
                                        )
                                    }
                                }
                            }
                        )
                    }
                } else if (thirdGroup.keys.contains(cellIndex)) {
                    val alpha by animateFloatAsState(
                        targetValue = if (showThirdGroupOfContacts) 1f else 0f,
                        animationSpec = tween(showThirdGroupOfContactsAnimationDuration),
                    )
                    remember(showThirdGroupOfContacts) {
                        CustomHexagonContentStyle(
                            id = cellIndex,
                            background = Background.SingleColor(backgroundColor),
                            content = {
                                initialOverlay.invoke()
                                thirdGroup[cellIndex]?.let {
                                    Box(modifier = Modifier
                                        .fillMaxSize()
                                        .background(it.background.copy(alpha = alpha))
                                        .alpha(alpha)
                                    ) {
                                        Image(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .fillMaxSize()
                                                .padding(top = 10.fdpv),
                                            contentScale = ContentScale.FillWidth,
                                            painter = painterResource(
                                                id = thirdGroup[cellIndex]?.resource ?: -1
                                            ),
                                            contentDescription = null,
                                        )
                                    }
                                }
                            }
                        )
                    }
                } else if (cellIndex == centralIndex) {
                    val defaultAvatarItemAlpha by animateFloatAsState(
                        targetValue = if (showDefaultAvatarItemAtCenter) 1f else 0f,
                        animationSpec = tween(showDefaultAvatarItemAtCenterAnimationDuration)
                    )
                    remember (showDefaultAvatarItemAtCenter, blurContacts) {
                        CustomHexagonContentStyle(
                            id = cellIndex,
                            background = Background.SingleColor(backgroundColor),
                            content = {
                                initialOverlay.invoke()
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFF6E4CD4).copy(alpha = defaultAvatarItemAlpha))
                                ) {
                                    Image(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .fillMaxSize()
                                            .padding(top = 10.fdpv),
                                        alpha = defaultAvatarItemAlpha,
                                        imageVector = ImageVector.vectorResource(id = R.drawable.default_avatar),
                                        contentDescription = null,
                                    )
                                }
                            }
                        )
                    }
                }
                else {
                    CustomHexagonContentStyle(
                        id = cellIndex,
                        background = Background.SingleColor(backgroundColor),
                        content = {
                            initialOverlay.invoke()
                        }
                    )
                }
            }

            val centralCellPosition = HexGridCellPosition(
                column = (gridColumns / 2) - 1,
                row = (gridRows / 2),
                gridRows = gridRows,
                gridColumns = gridColumns,)
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
            val markHamlinHexGridItems = List(gridRows * gridColumns) { index ->
                val row = index / gridColumns
                val column = index % gridColumns
                val cellIndex = createOnboardingCellPosition(
                    column = column,
                    row = row,
                ).getIndex()
                val backgroundColor = hexCellsBackgroundColors[cellIndex]
                val itemAlpha by animateFloatAsState(
                    targetValue = if (showContactFlower) 1f else 0f,
                    animationSpec = tween(showContactFlowerAnimationDuration)
                )

                when {
                    cellIndex == centralCellPosition.getIndex() -> remember (showContactFlower) {
                        CustomHexagonContentStyle(
                            id = cellIndex,
                            background = Background.SingleColor(backgroundColor),
                            content = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .align(Alignment.Center)
                                        .background(Color(0xFF393939).copy(alpha = itemAlpha)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        modifier = Modifier
                                            .fillMaxWidth(1 / 2f)
                                            .fillMaxSize(43f / 80)
                                            .alpha(itemAlpha),
                                        painter = painterResource(id = R.drawable.ic_any_network),
                                        contentDescription = null,
                                    )
                                }
                            }
                        )
                    }
                    facebookCellPosition.isSame(column, row) -> remember (showContactFlower) {
                        CustomHexagonContentStyle(
                            id = cellIndex,
                            background = Background.SingleColor(backgroundColor),
                            content = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .align(Alignment.Center)
                                        .background(FacebookColor.copy(alpha = itemAlpha)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        modifier = Modifier.alpha(itemAlpha),
                                        painter = painterResource(id = R.drawable.ic_facebook),
                                        contentDescription = null,
                                    )
                                }
                            }
                        )
                    }
                    messengerCellPosition.isSame(column, row) -> remember (showContactFlower) {
                        CustomHexagonContentStyle(
                            id = cellIndex,
                            background = Background.SingleColor(backgroundColor),
                            content = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .align(Alignment.Center)
                                        .background(MessengerColor.copy(alpha = itemAlpha)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        modifier = Modifier.alpha(itemAlpha),
                                        painter = painterResource(id = R.drawable.ic_messenger),
                                        contentDescription = null,
                                    )
                                }
                            }
                        )
                    }
                    instagramCellPosition.isSame(column, row) -> remember (showContactFlower) {
                        CustomHexagonContentStyle(
                            id = cellIndex,
                            background = Background.SingleColor(backgroundColor),
                            content = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .align(Alignment.Center)
                                        .background(InstagramColor.copy(alpha = itemAlpha)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        modifier = Modifier.alpha(itemAlpha),
                                        painter = painterResource(id = R.drawable.ic_instagram),
                                        contentDescription = null,
                                    )
                                }
                            }
                        )
                    }
                    emailCellPosition.isSame(column, row) -> remember (showContactFlower) {
                        CustomHexagonContentStyle(
                            id = cellIndex,
                            background = Background.SingleColor(backgroundColor),
                            content = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .align(Alignment.Center)
                                        .background(EmailColor.copy(alpha = itemAlpha)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        modifier = Modifier.alpha(itemAlpha),
                                        painter = painterResource(id = R.drawable.ic_email),
                                        contentDescription = null,
                                    )
                                }
                            }
                        )
                    }
                    phoneCellPosition.isSame(column, row) -> remember (showContactFlower) {
                        CustomHexagonContentStyle(
                            id = cellIndex,
                            background = Background.SingleColor(backgroundColor),
                            content = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .align(Alignment.Center)
                                        .background(PhoneColor.copy(alpha = itemAlpha)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        modifier = Modifier.alpha(itemAlpha),
                                        painter = painterResource(id = R.drawable.ic_phone),
                                        contentDescription = null,
                                    )
                                }
                            }
                        )
                    }
                    twitterCellPosition.isSame(column, row) -> remember (showContactFlower) {
                        CustomHexagonContentStyle(
                            id = cellIndex,
                            background = Background.SingleColor(backgroundColor),
                            content = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .align(Alignment.Center)
                                        .background(TwitterColor.copy(alpha = itemAlpha)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        modifier = Modifier.alpha(itemAlpha),
                                        painter = painterResource(id = R.drawable.ic_twitter),
                                        contentDescription = null,
                                    )
                                }
                            }
                        )
                    }
                    whatsappCellPosition.isSame(column, row) -> remember (showContactFlower) {
                        CustomHexagonContentStyle(
                            id = cellIndex,
                            background = Background.SingleColor(backgroundColor),
                            content = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .align(Alignment.Center)
                                        .background(WhatsappColor.copy(alpha = itemAlpha)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        modifier = Modifier.alpha(itemAlpha),
                                        painter = painterResource(id = R.drawable.ic_whatsapp),
                                        contentDescription = null,
                                    )
                                }
                            }
                        )
                    }
                    telegramCellPosition.isSame(column, row) -> remember (showContactFlower) {
                        CustomHexagonContentStyle(
                            id = cellIndex,
                            background = Background.SingleColor(backgroundColor),
                            content = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .align(Alignment.Center)
                                        .background(TelegramColor.copy(alpha = itemAlpha)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        modifier = Modifier.alpha(itemAlpha),
                                        painter = painterResource(id = R.drawable.ic_telegram),
                                        contentDescription = null,
                                    )
                                }
                            }
                        )
                    }

                    else -> remember {
                        EmptyHexagonContentStyle(
                            id = cellIndex,
                            background = Background.SingleColor(backgroundColor))
                    }
                }
            }


            val animatedBlur by animateDpAsState(
                targetValue = blurContacts,
                animationSpec = tween(durationMillis = blurContactsAnimationDuration) // You can customize the duration
            )

            HexagonalGrid(
                modifier = Modifier.blur(animatedBlur),
                itemsList = if (showMarkHamlinItems) markHamlinHexGridItems else items,
                rowSize = gridColumns,
                columnSize = gridRows,
                minScale = scale,
                onCellPositionCalculated = { index, coordinates, width, height ->
                    if (index == centralIndex) {
                        centralOffset = coordinates.positionInRoot()
                        centralWidth = width
                        centralHeight = height
                    } else if (index == 44) {
                        markHamlinCellOffset = coordinates.positionInRoot()
                    }
                },
                isScrollEnabled = false,
            )

            markHamlinCellOffset?.let {
                val startOffsetInDp = markHamlinCellOffset?.let {
                    with(LocalDensity.current) {
                        DpOffset(it.x.toDp(), it.y.toDp())
                    }
                } ?: DpOffset(0.dp, 0.dp)

                val endOffsetInDp = centralOffset?.let {
                    with(LocalDensity.current) {
                        DpOffset(it.x.toDp(), it.y.toDp())
                    }
                } ?: DpOffset(0.dp, 0.dp)

                val animatedOffsetX by animateDpAsState(
                    targetValue = if (slideOtherContactAvatarToCenter) endOffsetInDp.x else startOffsetInDp.x,
                    animationSpec = tween(slideOtherContactAvatarToCenterAnimationDuration)
                )

                val animatedOffsetY by animateDpAsState(
                    targetValue = if (slideOtherContactAvatarToCenter) endOffsetInDp.y else startOffsetInDp.y,
                    animationSpec = tween(slideOtherContactAvatarToCenterAnimationDuration)
                )

                val alpha by animateFloatAsState(
                    targetValue = if (showOtherContactAvatar) 1f else 0f,
                    animationSpec = tween(slideOtherContactAvatarToCenterAnimationDuration)
                )

                val cellSize = LocalConfiguration.current.screenWidthDp.dp / gridColumns
                val verticalBorder = (cellSize * 0.04403f).log { "verticalBorder" }
                val horizontalBorder = (cellSize * 89.99f/79.93f * 0.0395f).log { "horizontalBorder" }

                val polygon = remember { createPolygon() }
                val roundedPolygonShape = remember { RoundedPolygonShape(polygon) }

                RoundedHexagon(
                    modifier = Modifier
                        .offset(animatedOffsetX, animatedOffsetY)
                        .width(with(LocalDensity.current) { centralWidth!!.toDp() } * scale)
                        .padding(
                            vertical = verticalBorder,
                            horizontal = horizontalBorder
                        )
                        .aspectRatio(79.93.xdph / 89.99.xdpv)
                        .then(Modifier.graphicsLayer {
                            this.shadowElevation = shadowElevation
                            clip = true
                            shape = roundedPolygonShape
                        }),
                    contentStyle = CustomHexagonContentStyle(
                        id = 0,
                        background = Background.SingleColor(Color.Transparent),
                        content = {
                            Box(modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF97B6F2).copy(alpha = alpha))
                                .alpha(alpha)
                            ) {
                                Image(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .fillMaxSize()
                                        .padding(top = 10.fdpv),
                                    painter = painterResource(
                                        id = R.drawable.sixteenth_onboarding_contact_avatar,
                                    ),
                                    contentDescription = null,
                                )
                            }
                        }
                    ),
                )
            }
        },
        content = {
            val topMessageAlpha by animateFloatAsState(
                targetValue = if (showTopMessageAtTop) 1f else 0f,
                animationSpec = if (showTopMessageAtTop) {
                    tween(durationMillis = topMessageAnimationShowAnyNetworkIsANewWayDuration) // Duration for showing
                } else {
                    tween(durationMillis = topMessageAnimationHideAnyNetworkIsANewWayDuration) // Duration for hiding
                }
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.fdph)
                    .padding(top = 75.fdpv),
                text = topMessage,
                textAlign = TextAlign.Center,
                color = Color(0xFFFFFFFF).copy(topMessageAlpha),
                style = TextStyle(
                    fontFamily = montserratFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.csp,
                )
            )

            centralOffset?.let {
                val centerMessageAlpha by animateFloatAsState(
                    targetValue = if (showMainMessage) 1f else 0f,
                    animationSpec = tween(centerMessageAlphaAnimationDuration)
                )
                val centralOffsetInDp = with(LocalDensity.current) {
                    DpOffset(
                        x = centralOffset!!.x.toDp(),
                        y = (centralOffset!!.y).toDp()
                                + with(LocalDensity.current) { centralHeight!!.toDp() } * scale
                    )
                }
                val animatedPadding by animateDpAsState(
                    targetValue = paddingTopCenterMessage,
                    animationSpec = tween(centerMessageAlphaAnimationDurationAnimationDuration)
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.fdph)
                        .offset(y = centralOffsetInDp.y)
                        .padding(top = animatedPadding),
                    text = mainMessage,
                    textAlign = TextAlign.Center,
                    color = Color(0xFFFFFFFF).copy(alpha = centerMessageAlpha),
                    style = TextStyle(
                        fontFamily = montserratFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.csp,
                    )
                )
            }
        }
    )

    val alpha by animateFloatAsState(
        targetValue = if (isCoverVisible) 1f else 0f,
        animationSpec = tween(coverAnimationDuration)
    )
    CoverBox(alpha)
}
