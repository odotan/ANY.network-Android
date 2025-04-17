package com.anynetwork.app.ui.screens.connect

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anynetwork.app.R
import com.anynetwork.app.ui.components.HexagonTextField
import com.anynetwork.app.ui.components.Screen
import com.anynetwork.app.ui.components.hexagon.IconHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.IconHexagonContentStyle.Image.VectorResource
import com.anynetwork.app.ui.components.hexagon.RoundedHexagon
import com.anynetwork.app.ui.components.hexagon.RoundedPolygonShape
import com.anynetwork.app.ui.components.hexagon.createPolygon
import com.anynetwork.app.ui.navigation.MyProfileRoute
import com.anynetwork.app.ui.navigation.Route
import com.anynetwork.app.ui.theme.montserratFontFamily
import com.anynetwork.app.ui.utils.fdph
import com.anynetwork.app.ui.utils.fdpv
import com.anynetwork.app.ui.utils.fsp
import com.anynetwork.app.ui.utils.log
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

@Composable
fun ConnectRoot(navController: NavController, mode: String, emailSignInLink: String?) {
    val viewModel: ConnectViewModel = hiltViewModel<ConnectViewModel>()
        .apply {
            val viewEffect by viewEffectFlow.collectAsState()
            viewEffect.log { "viewEffect" }
            when (viewEffect) {
                is ConnectScreenViewEffect.NavigateBack -> {
                    navController.popBackStack()
                }
                is ConnectScreenViewEffect.NavigateToConnectSuccess ->
                    navController.navigate(MyProfileRoute.ConnectSuccess(mode = mode)) {
                        popUpTo(MyProfileRoute.Connect(
                            mode = mode,
                            emailSignInLink = emailSignInLink)
                        ) { inclusive = true }
                    }
                is ConnectScreenViewEffect.ShowError -> {
                    val context = LocalContext.current
                    Toast.makeText(context, (viewEffect as ConnectScreenViewEffect.ShowError).errorMessage, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
            onViewEvent(ConnectScreenViewEvent.ClearViewEffect)
        }

    val screenMode = remember(mode) {
        when (mode) {
            "facebook" -> ConnectScreenMode.Facebook
            "telegram" -> ConnectScreenMode.Telegram()
            "phone" -> ConnectScreenMode.Phone()
            else -> ConnectScreenMode.Email
        }
    }

    Connect(viewModel = viewModel)

    LaunchedEffect(screenMode) {
        viewModel.load(screenMode)
    }

    LaunchedEffect(emailSignInLink) {
        if (!emailSignInLink.isNullOrEmpty()) {
            viewModel.verifyEmail(emailSignInLink)
        }
    }
}

@Composable
fun Connect(viewModel: ConnectViewModel) {
    val viewState by viewModel.viewState.collectAsState()

    val isConnectButtonEnabled by remember { derivedStateOf { viewState.isConnectButtonEnabled } }
    val mode by remember { derivedStateOf { viewState.mode } }
    val firstTextFieldState by remember { derivedStateOf { viewState.firstTextFieldState } }
    val secondTextFieldState by remember { derivedStateOf { viewState.secondTextFieldState } }

    Screen(
        modifier = Modifier.imePadding(),
        pinButtons = {
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
                    .background(Color.White.copy(alpha = .05f))
                ) {
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.Center),
                        onClick = {
                            viewModel.onViewEvent(ConnectScreenViewEvent.BackButtonClick)
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
                    .wrapContentWidth()
                    .align(Alignment.Center),
                colors = CardColors(Color.Transparent, Color.Transparent, Color.Transparent, Color.Transparent),
                shape = RoundedCornerShape(
                    topStart = 24.fdph,
                    topEnd = 24.fdph,
                    bottomEnd = 24.fdph,
                    bottomStart = 24.fdph
                )
            ) {
                val activity = LocalActivity.current!!

                val callbackManager = remember {
                    CallbackManager.Factory.create()
                }
                val fbLauncher = rememberLauncherForActivityResult(
                    LoginManager.getInstance().createLogInActivityResultContract(callbackManager)
                ) { result ->
                    LoginManager.getInstance().onActivityResult(
                        result.resultCode,
                        result.data,
                        object: FacebookCallback<LoginResult> {
                            override fun onSuccess(result: LoginResult) {
                                viewModel.onViewEvent(
                                    ConnectScreenViewEvent.LoginWithFacebookSuccess(result)
                                )
                            }
                            override fun onCancel() {
                                viewModel.onViewEvent(ConnectScreenViewEvent.LoginWithFacebookCancel)
                            }
                            override fun onError(error: FacebookException) {
                                viewModel.onViewEvent(ConnectScreenViewEvent.LoginWithFacebookError(error))
                            }
                        }
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentWidth()
                        .background(Color.White.copy(alpha = .05f))
                        .then(
                            when {
                                isConnectButtonEnabled -> Modifier.clickable {
                                    if (mode == ConnectScreenMode.Facebook) {
                                        fbLauncher.launch(listOf("email", "public_profile"))
                                    } else {
                                        viewModel.onViewEvent(
                                            ConnectScreenViewEvent.ConnectButtonClick(
                                                activity = activity
                                            )
                                        )
                                    }
                                }
                                else -> Modifier
                            }),
                    contentAlignment = Alignment.Center
                ) {
                    val alphaValue = if (isConnectButtonEnabled) 1f else 0.4f
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.fdph),
                        text = "Connect",
                        textAlign = TextAlign.Center,
                        color = Color(0xFFCCCCCC).copy(alpha = alphaValue),
                        style = TextStyle(
                            fontFamily = montserratFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.fsp,
                        ),
                    )
                }
            }
        },
        content = {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val polygon = remember { createPolygon() }
                val roundedPolygonShape = remember { RoundedPolygonShape(polygon) }

                val cellSize = 48.fdph

                RoundedHexagon(
                    modifier = Modifier
                        .width(105.31.fdph)
                        .aspectRatio(79.93f / 89.99f)
                        .then(Modifier.graphicsLayer {
                            this.shadowElevation = shadowElevation
                            clip = true
                            shape = roundedPolygonShape
                        }),
                    contentStyle = IconHexagonContentStyle(
                        modifier = Modifier.fillMaxSize(43f / 80),
                        id = 0,
                        background = mode.cellBackground,
                        contentDescription = mode.title,
                        image = VectorResource(id = mode.imageResId),
                        onClick = {

                        },
                    )

                )

                Text(
                    modifier = Modifier.padding(top = 32.fdpv),
                    text = mode.title,
                    textAlign = TextAlign.Center,
                    color = Color(0xFFFFFFFF),
                    style = TextStyle(
                        fontFamily = montserratFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.fsp,
                    ),
                )

                Text(
                    modifier = Modifier.padding(top = 32.fdpv).padding(horizontal = 48.fdph),
                    text = mode.instructions,
                    textAlign = TextAlign.Center,
                    color = Color(0xFFCCCCCC),
                    style = TextStyle(
                        fontFamily = montserratFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.fsp,
                    ),
                )

                firstTextFieldState?.let { firstTextFieldState ->
                    HexagonTextField(
                        modifier = Modifier
                            .padding(top = 32.fdpv)
                            .padding(horizontal = 48.fdph)
                            .height(54.fdpv),
                        value = firstTextFieldState.value,
                        onValueChange = { newValue ->
                            newValue?.let {
                                viewModel.onViewEvent(ConnectScreenViewEvent.UpdateTextField(it))
                            }
                        },
                        readOnly = firstTextFieldState.readOnly,
                        placeholder = firstTextFieldState.placeholder,
                        addTrailingClearIcon = false
                    )
                }

                secondTextFieldState?.let { secondTextFieldState ->
                    HexagonTextField(
                        modifier = Modifier
                            .padding(top = 24.fdpv)
                            .padding(horizontal = 48.fdph)
                            .height(54.fdpv),
                        value = secondTextFieldState.value,
                        onValueChange = { newValue ->
                            newValue?.let {
                                viewModel.onViewEvent(ConnectScreenViewEvent.UpdateSecondTextField(it))
                            }
                        },
                        readOnly = secondTextFieldState.readOnly,
                        placeholder = secondTextFieldState.placeholder,
                        addTrailingClearIcon = false
                    )
                }
            }
        }
    )
}