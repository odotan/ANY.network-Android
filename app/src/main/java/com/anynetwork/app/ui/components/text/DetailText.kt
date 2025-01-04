package com.anynetwork.app.ui.components.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.anynetwork.app.ui.theme.montserratFontFamily
import com.anynetwork.app.ui.utils.csp

@Composable
fun DetailText(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        textAlign = TextAlign.Center,
        color = Color(0xFFAFAEB8),
        style = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.csp,
            lineHeight = TextUnit(21f, TextUnitType.Sp)
        )
    )
}