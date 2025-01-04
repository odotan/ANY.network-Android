package com.anynetwork.app.ui.components.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.anynetwork.app.ui.theme.sfProFontFamily
import com.anynetwork.app.ui.utils.csp
import com.anynetwork.app.ui.utils.xdpv

@Composable
fun PrimaryButton(title: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .height(54.xdpv)
            .fillMaxWidth(),
        onClick = onClick) {
        Text(
            text = title,
            color = Color.White,
            style = TextStyle(
                fontFamily = sfProFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.csp
            )
        )
    }
}