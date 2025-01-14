@file:OptIn(ExperimentalAnimationApi::class)

package com.anynetwork.app.ui.screens.home

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.anynetwork.app.R
import com.anynetwork.app.model.Contact
import com.anynetwork.app.model.Interaction
import com.anynetwork.app.ui.components.CircularCarousel
import com.anynetwork.app.ui.components.PickerItem
import com.anynetwork.app.ui.components.hexagon.CustomHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.IconHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.IconHexagonContentStyle.Image.VectorResource
import com.anynetwork.app.ui.components.hexagon.ImageHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.NontransparentHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.RoundedHexagon
import com.anynetwork.app.ui.components.hexagon.RoundedPolygonShape
import com.anynetwork.app.ui.components.hexagon.createPolygon
import com.anynetwork.app.ui.theme.EmailColor
import com.anynetwork.app.ui.theme.PhoneColor
import com.anynetwork.app.ui.theme.PopupColor
import com.anynetwork.app.ui.theme.montserratFontFamily
import com.anynetwork.app.ui.utils.csp
import com.anynetwork.app.ui.utils.fdph
import com.anynetwork.app.ui.utils.fdpv
import com.anynetwork.app.ui.utils.fsp
import com.anynetwork.app.ui.utils.xdph
import com.anynetwork.app.ui.utils.xdpv
import timber.log.Timber


@Composable
fun ContactsRow(
    modifier: Modifier = Modifier,
    contact: Contact,
    onClick: () -> Unit = {},
    onInteractionClick: (Contact, Int) -> Unit,
    backgroundColor: Color = Color(0xFF1C1A23)
) {
    var interactionType: Int? = remember { null }
    val polygon = remember { createPolygon() }
    val roundedPolygonShape = remember { RoundedPolygonShape(polygon) }
    val context = LocalContext.current
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 4.dp)
                .fillMaxHeight()
                .clickable {
                    onClick.invoke()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            key(contact) {
                RoundedHexagon(
                    modifier = Modifier
                        .width(57.fdph)
                        .aspectRatio(79.93.xdph / 89.99.xdpv)
                        .then(Modifier.graphicsLayer {
                            this.shadowElevation = shadowElevation
                            clip = true
                            shape = roundedPolygonShape
                        }),
                    contentStyle = CustomHexagonContentStyle(
                        id = 0,
                        background = NontransparentHexagonContentStyle.Background.SingleColor(
                            PopupColor
                        ),
                        onClick = { _ -> onClick.invoke() },
                        content = {
                            if (contact.avatarUri != null) {
                                Image(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    painter = rememberAsyncImagePainter(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(contact.avatarUri)
                                            .size(Size.ORIGINAL) // Load the image at its original resolution
                                            .build()
                                    ),
                                    contentScale = ContentScale.Crop,
                                    contentDescription = null,
                                )
                            } else {
                                Text(
                                    modifier = Modifier.align(Alignment.Center),
                                    text = contact
                                        .getDisplayNameFirstLetters()
                                        .uppercase(),
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFFAFAEB8),
                                    style = TextStyle(
                                        fontFamily = montserratFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 24.csp,
                                    )
                                )
                            }
                        }
                    )
                )
            }

            var subtitle by remember {
                mutableStateOf(contact.phone ?: "")
            }

            Row(
                modifier = Modifier
                    .height(64.fdpv)
                    .weight(1f)
//                    .background(Color.Red)
                    ,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 15.6.fdph)
                ) {
                    Text(
                        text = contact.name,
                        color = Color(0xFFFFFFFF),
                        style = TextStyle(
                            fontFamily = montserratFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.fsp,
                        )
                    )
                    AnimatedContent(
                        targetState = subtitle,
                        transitionSpec = {
                            addAnimation().using(
                                SizeTransform(clip = false)
                            )
                        }
                    ) { targetState ->
                        Text(
                            text = targetState,
                            color = Color(0xFFFFFFFF),
                            style = TextStyle(
                                fontFamily = montserratFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.fsp,
                            )
                        )
                    }
                }
            }

            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                fun modifyList(inputList: List<PickerItem>): List<PickerItem> {
                    return when (inputList.size) {
                        1 -> inputList
                        2 -> inputList + inputList + inputList // Repeat elements to make 6
                        3 -> inputList + inputList // Repeat elements to make 6
                        else -> inputList + inputList // Double the list if it has more than 3 elements
                    }
                }
                val interactionPickerItems = modifyList(
                    mutableListOf<PickerItem>().apply {
                        if (!contact.mobilePhone().isNullOrEmpty()) {
                            add(
                                PickerItem(
                                    resId = R.drawable.ic_phone,
                                    iconTint = PhoneColor,
                                    value = contact.mobilePhone()!!,
                                    interactionType = Interaction.Type.Phone
                                )
                            )
                        }
                        if (!contact.homeEmail().isNullOrEmpty()) {
                            add(
                                PickerItem(
                                    resId = R.drawable.ic_email,
                                    iconTint = EmailColor,
                                    value = contact.homeEmail()!!,
                                    interactionType = Interaction.Type.Email
                                )
                            )
                        }
                    }
                )

                if (interactionPickerItems.isNotEmpty()) {
                    key(contact) {
                        CircularCarousel(
                            modifier = Modifier
                                .width(67.11.fdph)
                                .height(28.23.fdpv)
                                .clipToBounds()
                                .align(Alignment.CenterVertically),
                            numItems = interactionPickerItems.size,
                            background = backgroundColor,
                            onSnapToItem = {

                            },
                            onSpinned = {
                                val newInteractionType =
                                    interactionPickerItems.getOrNull(it)?.interactionType
                                if (interactionType != newInteractionType) {
                                    Timber.i("onSpinned to ${interactionPickerItems.getOrNull(it)?.value}")
                                    interactionType = newInteractionType

                                    val vibrator =
                                        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?

                                    // Check if the device supports vibration
                                    if (vibrator != null && vibrator.hasVibrator()) {
                                        // Subtle vibration using a short duration (e.g., 50 milliseconds)
                                        vibrator.vibrate(
                                            VibrationEffect.createOneShot(
                                                15,
                                                VibrationEffect.DEFAULT_AMPLITUDE
                                            )
                                        )
                                    }
                                }
                                subtitle = interactionPickerItems.getOrNull(it)?.value ?: "phone"
                            },
                            onClick = {
                                onInteractionClick(
                                    contact,
                                    interactionPickerItems[it].interactionType
                                )
                                Timber.i("Interaction type is $it")
                            }
                        ) { index ->
                            val item = interactionPickerItems[index]
                            Card(
                                modifier = Modifier
                                    .width(28.23.fdph)
                                    .height(28.23.fdph),
                                shape = RectangleShape,
                                colors = CardDefaults.cardColors(containerColor = item.color),
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    if (item.resId != null) {
                                        AsyncImage(
                                            modifier = Modifier
                                                .align(Alignment.CenterHorizontally)
                                                .size(28.23.fdpv),
                                            model = item.resId,
                                            contentDescription = item.iconContentDescription,
                                            colorFilter = item.iconTint?.let { ColorFilter.tint(item.iconTint) }
                                        )
                                    } else {
                                        Text(text = "${index}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalAnimationApi
fun addAnimation(duration: Int = 400): ContentTransform {
    return slideInHorizontally(animationSpec = tween(durationMillis = duration)) { width -> -width } + fadeIn(
        animationSpec = tween(durationMillis = duration)
    ) with slideOutHorizontally(animationSpec = tween(durationMillis = duration)) { width -> width } + fadeOut(
        animationSpec = tween(durationMillis = duration)
    )
}