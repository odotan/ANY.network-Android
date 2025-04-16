package com.anynetwork.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.anynetwork.app.R
import com.anynetwork.app.ui.components.button.HexButtonShape
import com.anynetwork.app.ui.components.button.polygon
import com.anynetwork.app.ui.theme.montserratFontFamily
import com.anynetwork.app.ui.theme.sfProFontFamily
import com.anynetwork.app.ui.utils.csp
import com.anynetwork.app.ui.utils.fdph
import com.anynetwork.app.ui.utils.fdpv
import com.anynetwork.app.ui.utils.fsp

@Composable
fun HexagonTextField(
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    value: String,
    onValueChange: (String?) -> Unit,
    placeholder: String? = null,
    readOnly: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    addTrailingClearIcon: Boolean = false
) {
    val clipShape = remember {
        HexButtonShape(polygon)
    }
    Box(
        modifier = modifier
            .graphicsLayer {
                shape = clipShape
                clip = true
            }
            .border(
                width = 1.dp,
                color = Color(0xFF4E4955),
                clipShape
            ),
    ) {
        Row(modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF221C2A)),) {
            leadingIcon?.invoke()

            TextField(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        when {
                            focusRequester != null -> Modifier.focusRequester(focusRequester)
                            else -> Modifier
                        }
                    ),
                value = value,
                onValueChange = onValueChange,
                readOnly = readOnly,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                maxLines = 1,
                singleLine = true,
                textStyle = TextStyle(
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    color = if (readOnly) Color.LightGray else Color.White,
                    fontFamily = montserratFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.fsp,
                ),
                placeholder = {
                    if (placeholder != null) Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = placeholder,
                        style = TextStyle(
                            fontFamily = montserratFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.fsp,
                        )
                    )
                },
                trailingIcon = {
                    if (value.isNotEmpty() && addTrailingClearIcon) HexagonTextFieldClearTrailingIcon {
                        onValueChange.invoke(null)
                    }
                }
            )

            trailingIcon?.let {
                Box(
                    modifier = Modifier
                        .padding(end = 13.fdph)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    it.invoke()
                }
            }
        }
    }
}

@Composable
fun HexagonTextFieldClearTrailingIcon(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(20.fdph)
            .clip(CircleShape)
            .background(Color(0xFFFF6061))
            .clickable { onClick.invoke() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(8.fdph)
                .height(2.fdpv)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(4.dp)
                )
        )
    }
}