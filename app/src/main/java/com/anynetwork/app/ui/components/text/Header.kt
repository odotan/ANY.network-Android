package com.anynetwork.app.ui.components.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.anynetwork.app.ui.utils.csp

@Composable
fun Header(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        textAlign = TextAlign.Center,
        overflow = TextOverflow.Visible,
        color = Color.White,
        style = MaterialTheme.typography.headlineLarge,
        lineHeight = 30.csp
    )
}