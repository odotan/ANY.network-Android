package com.anynetwork.app.ui.utils

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity

@Composable
fun KeyboardDismissListener(onDismiss: () -> Unit) {
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    var wasImeVisible by remember { mutableStateOf(imeVisible) }

    LaunchedEffect(imeVisible) {
        if (wasImeVisible && !imeVisible) {
            onDismiss()
        }
        wasImeVisible = imeVisible
    }
}