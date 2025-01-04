package com.anynetwork.app.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.anynetwork.app.R
import com.anynetwork.app.ui.theme.GreenColor
import com.anynetwork.app.ui.theme.montserratFontFamily
import com.anynetwork.app.ui.utils.fdph
import com.anynetwork.app.ui.utils.fdpv
import com.anynetwork.app.ui.utils.fsp

@Composable
fun EmptyScreen(
    searchText: String,
    onCreateNewContactClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 98.fdpv),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier,
            text = "\"$searchText\"",
            textAlign = TextAlign.Center,
            color = Color.White,
            style = TextStyle(
                fontFamily = montserratFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.fsp,
            )
        )

        Text(
            modifier = Modifier.padding(top = 16.fdpv),
            text = "There is nothing to show on your contact list",
            textAlign = TextAlign.Center,
            color = Color.White.copy(alpha = 0.7f),
            style = TextStyle(
                fontFamily = montserratFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.fsp,
            )
        )

        Button(
            modifier = Modifier
                .padding(top = 17.fdpv),
            onClick = { onCreateNewContactClick.invoke() },
            colors = ButtonDefaults.buttonColors().copy(containerColor = Color.Transparent)
        ) {
            Row(
                modifier = Modifier
                    .height(24.fdpv),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier.size(15.61.fdpv),
                    painter = painterResource(R.drawable.ic_rounded_plus),
                    contentDescription = "add new contact",
                    colorFilter = ColorFilter.tint(GreenColor)
                )

                Text(
                    modifier = Modifier.padding(start = 8.fdph),
                    text = "Create New Contact",
                    textAlign = TextAlign.Center,
                    color = GreenColor,
                    style = TextStyle(
                        fontFamily = montserratFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.fsp,
                    )
                )
            }
        }
    }
}