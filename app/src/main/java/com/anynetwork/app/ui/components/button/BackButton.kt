package com.anynetwork.app.ui.components.button

import androidx.compose.foundation.Image
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.anynetwork.app.R

@Composable
fun BackButton(onClick: (() -> Unit)) {
    IconButton(onClick = onClick) {
        Image(
            painter = painterResource(R.drawable.ic_back_arrow),
            contentDescription = "back button",
        )
    }
}