package com.anynetwork.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.anynetwork.app.R
import com.anynetwork.app.ui.theme.montserratFontFamily
import com.anynetwork.app.ui.utils.csp
import com.anynetwork.app.ui.utils.fdph
import com.anynetwork.app.ui.utils.fdpv

@Composable
fun SwipeUpToContinue(modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Image(
            modifier = Modifier
                .padding(top = 6.fdpv)
                .width(20.fdph)
                .height(7.fdpv)
                .align(Alignment.CenterHorizontally),
            painter = painterResource(id = R.drawable.chevron_top),
            contentDescription = "chevron top"
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.fdpv),
            text = "Swipe up to continue",
            textAlign = TextAlign.Center,
            color = Color(0xFFFFFFFF),
            style = TextStyle(
                fontFamily = montserratFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.csp,
            )
        )
        Image(
            modifier = Modifier
                .padding(top = 7.fdpv)
                .width(20.fdph)
                .height(7.fdpv)
                .align(Alignment.CenterHorizontally),
            painter = painterResource(id = R.drawable.chevron_bottom),
            contentDescription = "chevron bottom"
        )
    }
}