package com.anynetwork.app.ui.components.hexagon

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@Composable
fun Badge(
    modifier: Modifier = Modifier,
    color: Color,
    @DrawableRes iconResourceId: Int,
    iconContentDescription: String = "",
    iconColorFilter: ColorFilter? = null,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(color)
            .clickable { onClick?.invoke() },
        contentAlignment = Alignment.Center  // Ensures everything stays centered
    ) {
        val scaleFactor = 0.5f  // Scale down the image to 50% of its original size

        Image(
            modifier = Modifier
                .scale(scaleFactor),  // Visual scaling without affecting layout position
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(iconResourceId)
                    .size(100)
                    .build()
            ),
            colorFilter = iconColorFilter,
            contentDescription = iconContentDescription,
        )
    }
}