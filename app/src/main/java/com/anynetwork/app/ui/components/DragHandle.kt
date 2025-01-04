package com.anynetwork.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.anynetwork.app.ui.utils.fdph
import com.anynetwork.app.ui.utils.fdpv

@Composable
fun DragHandle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(27.fdpv)
            .padding(vertical = 12.fdpv),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color(0xFFA4A3A7),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Box(Modifier.size(width = 74.fdph, height = 3.fdpv))
        }
    }
}