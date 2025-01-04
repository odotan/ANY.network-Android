package com.anynetwork.app.ui.components.textfield

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.anynetwork.app.R
import com.anynetwork.app.ui.components.button.HexButtonShape
import com.anynetwork.app.ui.components.button.polygon
import com.anynetwork.app.ui.theme.GreenColor
import com.anynetwork.app.ui.theme.montserratFontFamily
import com.anynetwork.app.ui.utils.fdph
import com.anynetwork.app.ui.utils.fsp

@Composable
fun ProfileTextFieldLeading(
    text: String,
    onClick: (() -> Unit)? = null
) {
    val clipShape = remember {
        HexButtonShape(polygon)
    }
    Row(
        modifier = Modifier
            .width(110.23.fdph)
            .fillMaxHeight()
            .graphicsLayer {
                shape = clipShape
                clip = true
            }
//            .background(Color(0xFF272331))
            .border(
                width = 1.dp,
                color = Color(0xFF4E4955),
                clipShape
            )
            .clickable { onClick?.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(start = 11.5.fdph)
                .weight(1f),
            text = text,
            textAlign = TextAlign.Center,
            color = GreenColor,
            style = TextStyle(
                fontFamily = montserratFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.fsp,
            )
        )

        Image(
            modifier = Modifier.padding(end = 17.91.fdph),
            painter = painterResource(id = R.drawable.ic_detail_arrow),
            contentDescription = "Mobile detail arrow",
        )
    }
}