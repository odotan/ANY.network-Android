package com.anynetwork.app.ui.components.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.anynetwork.app.R
import com.anynetwork.app.ui.components.hexagon.HexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.RoundedHexagon
import com.anynetwork.app.ui.components.hexagon.RoundedPolygonShape
import com.anynetwork.app.ui.components.hexagon.createPolygon
import com.anynetwork.app.ui.theme.GreenColor
import com.anynetwork.app.ui.theme.montserratFontFamily
import com.anynetwork.app.ui.utils.csp
import com.anynetwork.app.ui.utils.fdph
import com.anynetwork.app.ui.utils.fdpv
import com.anynetwork.app.ui.utils.fsp

data class MessageAction(val text: String, val onClick: () -> Unit)

@Composable
fun Message(
    modifier: Modifier = Modifier,
    icon: @Composable (BoxScope.() -> Unit) = {},
    title: String,
    description: String,
    action: MessageAction? = null,
    onCloseClick: () -> Unit,
) {
    Box(
        modifier = modifier
//            .padding(vertical = 13.dp)
//            .fillMaxWidth()
//            .height(185.fdpv)
    ) {
        Surface(
            modifier = Modifier
                .padding(top = 3.fdpv, end = 3.fdph),
            color = Color.White.copy(alpha = 0.05f),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(modifier = Modifier) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    icon.invoke(this)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(start = 93.fdph, top = 21.fdpv, end = 23.fdph)
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = title,
                            textAlign = TextAlign.Start,
                            color = Color(0xFFFFFFFF),
                            style = TextStyle(
                                fontFamily = montserratFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.fsp,
                            )
                        )

                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.fdpv, bottom = 15.fdpv),
                            text = description,
                            textAlign = TextAlign.Start,
                            color = Color(0xFFFFFFFF),
                            style = TextStyle(
                                fontFamily = montserratFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.fsp,
                            )
                        )
                    }
                }

                if (action != null) {
                    Spacer(
                        modifier = Modifier
                            .height(0.33.fdpv)
                            .fillMaxWidth()
                            .background(Color(0xFF545458).copy(alpha = 0.65f))
                    )

                    Row(
                        modifier = Modifier
                            .height(44.fdpv)
                            .align(Alignment.CenterHorizontally)
                            .clickable { action.onClick.invoke() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .fillMaxWidth(),
                            text = action.text,
                            textAlign = TextAlign.Center,
                            color = GreenColor,
                            style = TextStyle(
                                fontFamily = montserratFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 17.csp,
                            )
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(24.fdph)
                .clip(CircleShape)
                .background(Color(0xFF39373F))
                .clickable { onCloseClick() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = null
            )
        }
    }
}