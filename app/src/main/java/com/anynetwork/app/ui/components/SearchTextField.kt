package com.anynetwork.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.anynetwork.app.R
import com.anynetwork.app.ui.theme.montserratFontFamily
import com.anynetwork.app.ui.utils.fdph
import com.anynetwork.app.ui.utils.fdpv
import com.anynetwork.app.ui.utils.fsp
import com.anynetwork.app.ui.utils.xdph

@Composable
fun SearchTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Tracks if the keyboard request has already been made
    var keyboardShown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus() // Request focus when the Composable appears
    }

    LaunchedEffect(keyboardShown) {
        if (!keyboardShown) {
            // Show the keyboard after focus is gained
            keyboardController?.show()
            keyboardShown = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                shape = RoundedCornerShape(12.xdph)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically // Align children vertically in the center
        ) {
            // TextField should expand to take the remaining space
            TextField(
                modifier = Modifier
                    .weight(1f) // Take the remaining width available in the Row
                    .focusRequester(focusRequester),
                value = value,
                onValueChange = {
                    onValueChange.invoke(it)
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Black.copy(alpha = .23f),
                    focusedContainerColor = Color.Black.copy(alpha = .23f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(
                    color = Color.White,
                    fontFamily = montserratFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.fsp,
                ),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.label_search),
                        style = TextStyle(
                            color = Color.White.copy(alpha = 0.6f),
                            fontFamily = montserratFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.fsp,
                        )
                    )
                },
                leadingIcon = {
                    if (value.isEmpty()) {
                        Image(
                            modifier = Modifier
                                .width(18.5.fdph)
                                .height(18.5.fdpv),
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = "search icon",
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(24.fdpv)
                                .clip(CircleShape)
                                .clickable {
                                    onValueChange.invoke("")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                modifier = Modifier.size(19.5.fdpv),
                                painter = painterResource(R.drawable.ic_close),
                                contentDescription = "search",
                            )
                        }
                    }
                },
                trailingIcon = trailingIcon,
                singleLine = true
            )
        }
    }
}
