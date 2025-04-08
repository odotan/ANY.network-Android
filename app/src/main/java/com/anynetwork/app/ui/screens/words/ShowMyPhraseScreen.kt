package com.anynetwork.app.ui.screens.words

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anynetwork.app.R
import com.anynetwork.app.ui.components.Screen
import com.anynetwork.app.ui.components.ToolbarState
import com.anynetwork.app.ui.components.ToolbarStateTitle
import com.anynetwork.app.ui.navigation.MyProfileRoute
import com.anynetwork.app.ui.navigation.Route
import com.anynetwork.app.ui.theme.montserratFontFamily
import com.anynetwork.app.ui.utils.fdph
import com.anynetwork.app.ui.utils.fdpv
import com.anynetwork.app.ui.utils.fsp
import com.anynetwork.app.ui.utils.xdph
import com.anynetwork.app.ui.utils.xdpv

@Composable
fun ShowMyPhraseScreenRoot(navController: NavController) {
    val viewModel: ShowMyPhraseScreenViewModel = hiltViewModel<ShowMyPhraseScreenViewModel>()
        .apply {
            val viewEffect by viewEffectFlow.collectAsState()
            when (viewEffect) {
                is ShowMyPhraseScreenViewEffect.NavigateBack ->
                    navController.popBackStack(MyProfileRoute.MyProfile, inclusive = false)
                else -> {}
            }
        }

    ShowMyPhraseScreen(viewModel = viewModel)
}

@Composable
private fun ShowMyPhraseScreen(viewModel: ShowMyPhraseScreenViewModel) {
    val viewState by viewModel.viewState.collectAsState()
    val words by remember { derivedStateOf { viewState.words }}

    Screen(
        topBar = ToolbarState.Shown(
            titleState = ToolbarStateTitle.Custom(
                content = {
                    Box(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "12 Magic Words",
                            textAlign = TextAlign.Center,
                            color = Color(0xFFFFFFFF),
                            style = TextStyle(
                                fontFamily = montserratFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.fsp,
                            ),
                        )
                    }
                }
            ),
        ),
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
                            viewModel.onViewEvent(ShowMyPhraseScreenViewEvent.BackButtonClick)
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
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.xdph),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 120.fdpv),
                    text = "This is your 12 word phrase needed to access your card. It is currently stored only on your device. Please copy and back up your phrase. We will verify your backup in the next screen",
                    textAlign = TextAlign.Center,
                    color = Color(0xFFCCCCCC),
                    style = TextStyle(
                        fontFamily = montserratFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.fsp,
                    ),
                )

                LazyVerticalGrid(
                    modifier = Modifier
                        .padding(30.xdpv)
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(24.xdph))
                        .background(Color.White.copy(alpha = 0.06f)),
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(vertical = 20.xdpv)
                ) {
                    items(words.size) { index ->
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(vertical = 10.xdpv),
                            text = words[index],
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            style = TextStyle(
                                fontFamily = montserratFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.fsp,
                            ),
                        )
                    }
                }
            }
        }
    )
}