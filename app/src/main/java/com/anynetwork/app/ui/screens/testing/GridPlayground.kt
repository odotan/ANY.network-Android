package com.anynetwork.app.ui.screens.testing

import androidx.compose.runtime.Composable
import com.anynetwork.app.ui.components.Screen
import com.anynetwork.app.ui.components.ToolbarState
import com.anynetwork.app.ui.components.ToolbarStateTitle
import com.anynetwork.app.ui.theme.BlogspotColor
import com.anynetwork.app.ui.theme.FacebookColor
import com.anynetwork.app.ui.theme.FavoriteColor
import com.anynetwork.app.ui.theme.GPlusColor
import com.anynetwork.app.ui.theme.KColor
import com.anynetwork.app.ui.theme.MessengerColor
import com.anynetwork.app.ui.theme.PinColor
import com.anynetwork.app.ui.theme.RedditColor
import com.anynetwork.app.ui.theme.SkypeColor
import com.anynetwork.app.ui.theme.SnapchatColor
import com.anynetwork.app.ui.theme.SnapchatColor2
import com.anynetwork.app.ui.theme.TwitterColor
import com.anynetwork.app.ui.theme.VineColor
import com.anynetwork.app.ui.theme.WhatsappColor
import com.anynetwork.app.ui.theme.YoutubeColor
import kotlin.random.Random

@Composable
fun GridPlaygroundScreen(mode: Int) {
    Screen(topBar = ToolbarState.Shown(titleState = ToolbarStateTitle.Text("Grid Playground"))) {
        val colors = listOf(
            YoutubeColor,
            WhatsappColor,
            FacebookColor,
            YoutubeColor,
            MessengerColor,
            SnapchatColor,
            TwitterColor,
            FavoriteColor,
            BlogspotColor,
            VineColor,
            GPlusColor,
            SkypeColor,
            RedditColor,
            KColor,
            PinColor,
            SnapchatColor2
            )
    }
}