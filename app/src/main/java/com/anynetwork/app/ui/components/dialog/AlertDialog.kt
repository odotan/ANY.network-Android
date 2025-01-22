package com.anynetwork.app.ui.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.anynetwork.app.ui.theme.montserratFontFamily
import com.anynetwork.app.ui.theme.sfProFontFamily
import com.anynetwork.app.ui.utils.csp
import com.anynetwork.app.ui.utils.fdph
import com.anynetwork.app.ui.utils.fdpv
import com.anynetwork.app.ui.utils.fsp
import com.anynetwork.app.ui.utils.xdph
import com.anynetwork.app.ui.utils.xdpv

@Composable
fun AlertDialog(
    title: String,
    message: String? = null,
    buttons: List<AlertDialogButtonState>,
    onDismiss: () -> Unit) {
    Dialog(
        properties = DialogProperties(),
        onDismissRequest = { onDismiss() },
        content = {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = Color(0xFF38383A),
            ) {
                Column {
                    Text(
                        modifier = Modifier
                            .padding(top = 24.fdpv)
                            .padding(horizontal = 46.fdph)
                            .fillMaxWidth(),
                        text = title,
                        textAlign = TextAlign.Center,
                        fontSize = 17.fsp,
                        style = TextStyle(
                            fontFamily = montserratFontFamily,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = Color.White,
                    )

                    message?.let {
                        Text(
                            modifier = Modifier
                                .padding(top = 12.fdpv)
                                .padding(horizontal = 46.fdph),
                            text = message,
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            fontSize = 13.fsp,
                            style = TextStyle(
                                fontFamily = montserratFontFamily,
                                fontWeight = FontWeight.Normal,
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(23.fdpv))

                    Spacer(
                        modifier = Modifier
                            .padding(horizontal = 24.fdph)
                            .fillMaxWidth()
                            .height(.33.fdpv)
                            .background(Color(0xFF545458).copy(alpha = 0.65f))
                    )

                    Row(
                        modifier = Modifier
                            .height(52.fdpv)
                            .fillMaxWidth()
                            .padding(horizontal = 20.fdph),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        buttons.forEachIndexed { index, button ->
                            Button(
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent
                                ),
                                onClick = {
                                    onDismiss.invoke()
                                    button.onClick.invoke()
                                }
                            ) {
                                Text(
                                    text = button.title,
                                    textAlign = TextAlign.Center,
                                    color = button.textColor,
                                    style = TextStyle(
                                        fontFamily = sfProFontFamily,
                                        fontWeight = button.fontWeight,
                                        fontSize = 16.fsp
                                    )
                                )
                            }

                            // Add a Spacer after each button except the last one
                            if (index < buttons.size - 1) {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(.33.fdph)
                                        .background(Color(0xFF545458).copy(alpha = 0.65f))
                                )
                            }
                        }
                    }
                }
            }
        },
    )
}

data class AlertDialogButtonState(
    val title: String,
    val textColor: Color = Color.White,
    val fontWeight: FontWeight = FontWeight.Normal,
    val onClick: () -> Unit = {}
)