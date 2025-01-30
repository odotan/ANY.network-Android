@file:OptIn(ExperimentalAnimationApi::class)

package com.anynetwork.app.ui.screens.home

import android.content.Context
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.anynetwork.app.R
import com.anynetwork.app.data.carouselInteraction.CarouselInteractionRepository
import com.anynetwork.app.model.Contact
import com.anynetwork.app.model.Interaction
import com.anynetwork.app.ui.components.CircularCarousel
import com.anynetwork.app.ui.components.PickerItem
import com.anynetwork.app.ui.components.hexagon.CustomHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.NontransparentHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.RoundedHexagon
import com.anynetwork.app.ui.components.hexagon.RoundedPolygonShape
import com.anynetwork.app.ui.components.hexagon.createPolygon
import com.anynetwork.app.ui.components.text.AutoSizeText
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@Composable
fun ContactsRow(
    modifier: Modifier = Modifier,
    contact: Contact,
    onClick: () -> Unit = {},
    onInteractionClick: (Contact, Int) -> Unit,
    backgroundColor: Color = Color(0xFF1C1A23)
) {
    val viewModel = hiltViewModel<ContactRowViewModel>(key = contact.id.toString())

    LaunchedEffect(contact.id) {
        viewModel.loadContact(contact)
    }

    val viewState by viewModel.viewState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewState) {
        snapshotFlow { viewState.interactionType }
            .collect { Timber.i("Observed interactionType change for ${contact.id}: $it") }
    }

    var currentInteractionType: Int? = remember { null }
    val polygon = remember { createPolygon() }
    val roundedPolygonShape = remember { RoundedPolygonShape(polygon) }
    val roundedPolygon2Shape = remember { RoundedPolygonShape(polygon) }
    val context = LocalContext.current
    Row(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
                    AutoSizeText(
                        modifier = Modifier.fillMaxSize(0.9f),
                        text = contact.name,
                        maxLines = if (contact.name.contains(" ")) 2 else 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color(0xFFAFAEB8),
                        alignment = Alignment.Center,
                        maxTextSize = 12.fsp,
                        style = TextStyle(
                            fontFamily = montserratFontFamily,
                            fontWeight = FontWeight.SemiBold,
                        )
                    )
                }
            )
        )

        var comesFromRight by remember {
            mutableStateOf(true)
        }

        var subtitle by remember {
            mutableStateOf(contact.phone ?: "")
        }

        var snappedItem by remember {
            mutableStateOf(-1)
        }

        Column(
            modifier = Modifier
                .height(64.fdpv)
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
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
                fun reorderList(list: List<PickerItem>, value: Int?): List<PickerItem> {
                    if (value == null) return list
                    val index = list.map { it.interactionType }.indexOf(value)
                    if (index == -1) return list // If the value is not in the list, return the original list
                    return list.subList(index, list.size) + list.subList(0, index)
                }

                val interactionType by remember {
                    derivedStateOf {
                        viewState.interactionType
                    }
                }

                val interactionPickerItems = remember(interactionType) {
                    modifyList(
                        reorderList(mutableListOf<PickerItem>().apply {
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
                        }, interactionType)
                    )
                }

                LaunchedEffect(interactionType) {
                    subtitle = interactionPickerItems.getOrNull(interactionPickerItems.map { it.interactionType }.indexOf(interactionType))?.value
                        ?: interactionPickerItems[0].value
                }
                LaunchedEffect(Unit) {
                    currentInteractionType = interactionType
                }

                if (interactionPickerItems.isNotEmpty()) {
                    key(contact.id) {
                        CircularCarousel(
                            modifier = Modifier
                                .width(67.11.fdph)
                                .height(28.23.fdpv)
                                .clipToBounds()
                                .align(Alignment.CenterVertically),
                            numItems = interactionPickerItems.size,
                            onSnapToItem = {
                                Timber.i("onSnapToItem: $it")
                                interactionPickerItems.getOrNull(it)
                                    ?.let {
                                        viewModel.onInteractionCarouselSpin(pickerItem = it)
                                    }

                            },
                            onSpinned = { index, fromRight ->
                                val newInteractionType =
                                    interactionPickerItems.getOrNull(index)?.interactionType
                                if (currentInteractionType != newInteractionType) {
                                    currentInteractionType = newInteractionType
                                    Timber.i("onSpinned to ${interactionPickerItems.getOrNull(index)?.value}")

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
                                subtitle = interactionPickerItems.getOrNull(index)?.value ?: "phone"

                                if (snappedItem != index && snappedItem > -1) {
                                    comesFromRight = fromRight
                                    snappedItem = index
                                } else if (snappedItem == -1) {
                                    snappedItem = index
                                }

                            },
                            onClick = {
                                contact.id
                                onInteractionClick(
                                    contact,
                                    interactionPickerItems[it].interactionType
                                )
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

            AnimatedContent(
                modifier = Modifier.padding(top = 6.dp),
                targetState = subtitle,
                transitionSpec = {
                    val animationDuration = 200
                    if (comesFromRight) {
                        slideInHorizontally(animationSpec = tween(durationMillis = animationDuration)) { -it } +
                                fadeIn(animationSpec = tween(durationMillis = animationDuration)) with
                                slideOutHorizontally(animationSpec = tween(durationMillis = animationDuration)) { it } +
                                fadeOut(animationSpec = tween(durationMillis = animationDuration))
                    } else {
                        slideInHorizontally(animationSpec = tween(durationMillis = animationDuration)) { it } +
                                fadeIn(animationSpec = tween(durationMillis = animationDuration)) with
                                slideOutHorizontally(animationSpec = tween(durationMillis = animationDuration)) { -it } +
                                fadeOut(animationSpec = tween(durationMillis = animationDuration))
                    }.using(SizeTransform(clip = false))
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

        RoundedHexagon(
            modifier = Modifier
                .width(57.fdph)
                .aspectRatio(79.93.xdph / 89.99.xdpv)
                .then(Modifier.graphicsLayer {
                    this.shadowElevation = shadowElevation
                    clip = true
                    shape = roundedPolygon2Shape
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
}

@HiltViewModel
class ContactRowViewModel @Inject constructor(
    val carouselInteractionRepository: CarouselInteractionRepository
): ViewModel() {
    private val _viewState = MutableStateFlow(ContactRowViewState())
    val viewState: StateFlow<ContactRowViewState> = _viewState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), _viewState.value)

    var contact: Contact? = null

    fun loadContact(contact: Contact) = viewModelScope.launch {
        Timber.i("loadContact for ${contact.id}")
        this@ContactRowViewModel.contact = contact
        val interaction = carouselInteractionRepository.getLatestInteraction(contact.id)
        val interactionType = interaction?.type ?: Interaction.Type.Phone
        _viewState.value = ContactRowViewState(interactionType = interactionType)
    }

    fun onInteractionCarouselSpin(pickerItem: PickerItem) = viewModelScope.launch {
        val interactionType = pickerItem.interactionType
//        _viewState.value = ContactRowViewState(interactionType = interactionType)
        carouselInteractionRepository.upsertInteraction(
            contactId = contact!!.id,
            interactionType = interactionType
        )
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

data class ContactRowViewState(
    val interactionType: Int? = null
)