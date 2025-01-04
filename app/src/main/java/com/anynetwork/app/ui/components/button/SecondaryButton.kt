package com.anynetwork.app.ui.components.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.anynetwork.app.ui.theme.montserratFontFamily
import com.anynetwork.app.ui.utils.csp
import com.anynetwork.app.ui.utils.xdpv

@Composable
fun SecondaryButton(title: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .height(54.xdpv)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors().copy(containerColor = Color.Transparent),
        onClick = onClick) {
        Text(
            text = title,
            color = Color(0xFFAFAEB8),
            style = TextStyle(
                fontFamily = montserratFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.csp
            )
        )
    }
}