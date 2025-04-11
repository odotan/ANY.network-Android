package com.anynetwork.app.ui.screens.connectsuccess

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anynetwork.app.R
import com.anynetwork.app.ui.components.Screen
import com.anynetwork.app.ui.components.hexagon.IconHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.IconHexagonContentStyle.Image.VectorResource
import com.anynetwork.app.ui.components.hexagon.RoundedHexagon
import com.anynetwork.app.ui.components.hexagon.RoundedPolygonShape
import com.anynetwork.app.ui.components.hexagon.createPolygon
import com.anynetwork.app.ui.screens.connect.ConnectScreenMode
import com.anynetwork.app.ui.theme.montserratFontFamily
import com.anynetwork.app.ui.utils.fdph
import com.anynetwork.app.ui.utils.fdpv
import com.anynetwork.app.ui.utils.fsp
import com.anynetwork.app.ui.utils.xdph
import com.anynetwork.app.ui.utils.xdpv

@Composable
fun ConnectSuccessScreenRoot(navController: NavController, mode: String) {
    val viewModel: ConnectSuccessScreenViewModel = hiltViewModel<ConnectSuccessScreenViewModel>()
        .apply {
            val viewEffect by viewEffectFlow.collectAsState()
            when (viewEffect) {
                is ConnectSuccessScreenViewEffect.NavigateBack -> {
                    navController.popBackStack()
                }
                else -> {}
            }
        }

    val screenMode = remember(mode) {
        when (mode) {
            "telegram" -> ConnectScreenMode.Telegram()
            "phone" -> ConnectScreenMode.Phone()
            else -> ConnectScreenMode.Email
        }
    }

    LaunchedEffect(screenMode) {
        viewModel.load(screenMode)
    }
    ConnectSuccessScreen(viewModel)
}

@Composable
private fun ConnectSuccessScreen(viewModel: ConnectSuccessScreenViewModel) {
    val viewState by viewModel.viewState.collectAsState()
    val mode by remember { derivedStateOf { viewState.mode } }
    val isInformationAvailableOnMyProfile by remember { derivedStateOf { viewState.isInformationAvailableOnMyProfile } }
    val isInformationPubliclySearchable by remember { derivedStateOf { viewState.isInformationPubliclySearchable } }

    Screen(
        modifier = Modifier,
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
                            viewModel.onViewEvent(ConnectSuccessScreenViewEvent.BackButtonClick)
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
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentWidth()
                        .background(Color.White.copy(alpha = .05f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.fdph),
                        text = "Connect",
                        textAlign = TextAlign.Center,
                        color = Color(0xFFCCCCCC),
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
            mode?.let { mode ->
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val polygon = remember { createPolygon() }
                    val roundedPolygonShape = remember { RoundedPolygonShape(polygon) }

                    val cellSize = 48.fdph

                    Box() {
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

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 4.9.xdpv)
                                .size(36.xdpv)
                                .offset(x = 14.3.xdph)
                                .clip(CircleShape)
                                .background(Color.White)
                                .clickable {  },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                modifier = Modifier
                                    .fillMaxWidth(12f / 32)
                                    .wrapContentHeight(),
                                painter = painterResource(R.drawable.ic_tick),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(mode.color())
                            )
                        }
                    }

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
                        modifier = Modifier.padding(top = 32.fdpv).padding(horizontal = 16.fdph),
                        text = mode.instructions,
                        textAlign = TextAlign.Center,
                        color = Color(0xFFCCCCCC),
                        style = TextStyle(
                            fontFamily = montserratFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.fsp,
                        ),
                    )

                    Option(
                        modifier = Modifier.padding(top = 100.xdpv).padding(horizontal = 16.fdph),
                        text = "Make this information available on my public Profile",
                        checked = isInformationAvailableOnMyProfile,
                        onCheckedChange = {
                            viewModel.onViewEvent(ConnectSuccessScreenViewEvent.ToggleIsInformationAvailableOnMyProfile)
                        }
                    )

                    Option(
                        modifier = Modifier.padding(top = 35.xdpv).padding(horizontal = 16.fdph),
                        text = "Make this information publicly Searchable",
                        checked = isInformationPubliclySearchable,
                        onCheckedChange = {
                            viewModel.onViewEvent(ConnectSuccessScreenViewEvent.ToggleIsInformationPubliclySearchable)
                        }
                    )
                }
            }
        }
    )
}

@Composable
private fun Option(
    modifier: Modifier,
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF1CC580),
                checkedTrackColor = Color(0xFF1CC580).copy(alpha = 0.1f),
                checkedBorderColor = Color(0xFF1CC580).copy(alpha = 0.1f)
            )
        )

        Text(
            modifier = Modifier.padding(start = 18.xdph).fillMaxWidth(),
            text = text,
            textAlign = TextAlign.Start,
            color = Color.White,
            style = TextStyle(
                fontFamily = montserratFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.fsp,
            ),
        )
    }
}