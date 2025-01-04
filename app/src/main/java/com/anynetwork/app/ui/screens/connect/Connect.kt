package com.anynetwork.app.ui.screens.connect

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.anynetwork.app.R
import com.anynetwork.app.ui.components.NavigationIconState
import com.anynetwork.app.ui.components.Screen
import com.anynetwork.app.ui.components.text.SubtitleText
import com.anynetwork.app.ui.components.ToolbarState
import com.anynetwork.app.ui.components.ToolbarStateTitle
import com.anynetwork.app.ui.utils.xdph

@Composable
fun ConnectRoot() {
    Connect()
}

@Composable
fun Connect() {
    Screen(
        topBar = ToolbarState.Shown(
            titleState = ToolbarStateTitle.Text(stringResource(R.string.connect_screen_title)),
            navigationIconState = NavigationIconState.BackButton {},
        ),
        content = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                SubtitleText(
                    modifier = Modifier
                        .padding(start = 16.xdph, end = 16.xdph),
                    text = stringResource(R.string.connect_screen_subtitle)
                )
            }

        }
    )
}