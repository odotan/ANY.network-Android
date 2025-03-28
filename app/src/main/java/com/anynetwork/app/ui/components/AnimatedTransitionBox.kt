package com.anynetwork.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedTransitionBox(
    modifier: Modifier = Modifier,
    transitionDuration: Int = 400,
    initialX: Float,
    initialY: Float,
    content: @Composable (Boolean) -> Unit
) {
    val currentConfig = LocalConfiguration.current
    val width = with(LocalDensity.current) { currentConfig.screenWidthDp.dp.toPx() }
    val height = with(LocalDensity.current) { currentConfig.screenHeightDp.dp.toPx() }

    var scaleX by remember { mutableStateOf(0.1f) }
    var scaleY by remember { mutableStateOf(0.1f) }
    var translateX by remember { mutableStateOf(initialX - (width / 2)) }
    var translateY by remember { mutableStateOf(initialY - (height / 2)) }
    var opacity by remember { mutableStateOf(0f) }
    var isEnterAnimationFinished by remember { mutableStateOf(false) }

    // Animate values
    val animatedScaleX by animateFloatAsState(
        targetValue = scaleX,
        animationSpec = tween(durationMillis = transitionDuration)
    )
    val animatedScaleY by animateFloatAsState(
        targetValue = scaleY,
        animationSpec = tween(durationMillis = transitionDuration)
    )
    val animatedTranslateX by animateFloatAsState(
        targetValue = translateX,
        animationSpec = tween(durationMillis = transitionDuration)
    )
    val animatedTranslateY by animateFloatAsState(
        targetValue = translateY,
        animationSpec = tween(durationMillis = transitionDuration)
    )
    val animatedOpacity by animateFloatAsState(
        targetValue = opacity,
        animationSpec = tween(durationMillis = transitionDuration),
        finishedListener = { isEnterAnimationFinished = true }
    )

    // Trigger enter animation
    LaunchedEffect(Unit) {
        scaleX = 1f
        scaleY = 1f
        translateX = 0f
        translateY = 0f
        opacity = 1f
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX = animatedScaleX,
                scaleY = animatedScaleY,
                translationX = animatedTranslateX,
                translationY = animatedTranslateY,
                alpha = animatedOpacity
            ),
        contentAlignment = Alignment.Center
    ) {
        content(isEnterAnimationFinished)
    }
}
