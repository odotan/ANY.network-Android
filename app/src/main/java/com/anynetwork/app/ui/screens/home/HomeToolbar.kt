package com.anynetwork.app.ui.screens.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.anynetwork.app.R
import com.anynetwork.app.ui.components.NavigationIconState
import com.anynetwork.app.ui.components.ToolbarState
import com.anynetwork.app.ui.components.ToolbarStateTitle
import com.anynetwork.app.ui.theme.DarkBlue
import com.anynetwork.app.ui.theme.montserratFontFamily
import com.anynetwork.app.ui.utils.csp
import com.anynetwork.app.ui.utils.xdph

fun HomeToolbar(screenMode: HomeScreenMode, onBackPress: () -> Unit, onActionClick: () -> Unit): ToolbarState.Shown {
    val alphaAnimationDuration = 500
    return ToolbarState.Shown(
        modifier = Modifier
            .drawBehind {
                this.drawRect(DarkBlue)
            }
            .shadow(elevation = 25.dp, ambientColor = DarkBlue, spotColor = DarkBlue),
        navigationIconState = NavigationIconState.Custom {
            val alpha by animateFloatAsState(
                targetValue = if (screenMode.isSearching) 1f else 0f,
                animationSpec = tween(alphaAnimationDuration)
            )
            IconButton(
                modifier = Modifier.alpha(alpha = alpha),
                onClick = {
                    onBackPress()
                }
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_back_arrow),
                    contentDescription = "hamburger menu icon",
                )
            }
        },
        titleState = ToolbarStateTitle.Custom(
            content = {
                val alpha by animateFloatAsState(
                    targetValue = if (!screenMode.isSearching) 1f else 0f,
                    animationSpec = tween(alphaAnimationDuration)
                )

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.alpha(alpha),
                        painter = painterResource(R.drawable.ic_any_network),
                        contentDescription = "notifications action icon",
                    )

                    Text(
                        modifier = Modifier.alpha(1 - alpha),
                        text = "Search",
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        style = TextStyle(
                            fontFamily = montserratFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.csp,
                        )
                    )
                }
            }
        ),
        actions = {
            val searchingGridAlpha by animateFloatAsState(
                targetValue = if (screenMode is HomeScreenMode.SearchingList) 1f else 0f,
                animationSpec = tween(alphaAnimationDuration)
            )
            val searchingListAlpha by animateFloatAsState(
                targetValue = if (screenMode is HomeScreenMode.SearchingGrid) 1f else 0f,
                animationSpec = tween(alphaAnimationDuration)
            )
            Box {
                IconButton(
                    modifier = Modifier.alpha(searchingGridAlpha),
                    onClick = onActionClick,
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_hex_grid),
                        contentDescription = "open hex grid",
                    )
                }
                IconButton(
                    modifier = Modifier.alpha(searchingListAlpha),
                    onClick = onActionClick,
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_list),
                        contentDescription = "open list",
                    )
                }
            }
        }
    )
}