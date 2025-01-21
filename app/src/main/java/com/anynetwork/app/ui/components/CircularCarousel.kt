package com.anynetwork.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FloatSpringSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.anynetwork.app.ui.theme.ANYnetworkTheme
import com.anynetwork.app.ui.theme.DarkBlue
import com.anynetwork.app.ui.utils.log
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


@Stable
interface CircularCarouselState {
    val angle: Float
    val minorAxisFactor: Float

    suspend fun stop()
    suspend fun snapTo(angle: Float)
    suspend fun decayTo(angle: Float, velocity: Float)
    suspend fun snapToClosestItem(numItems: Int)
    fun getClosestItem(numItems: Int): Int
    fun setNumItems(numItems: Int)
    fun setOnSnapToItem(onSnapToItem: (Int) -> Unit)
    fun setOnSpinned(onSpinned: (Int) -> Unit)
    fun setMinorAxisFactor(factor: Float)
}

class CircularCarouselStateImpl : CircularCarouselState {
    private val _angle = Animatable(0f)
    private val _eccentricity = mutableStateOf(1f)
    private var onSnapToItem: ((Int) -> Unit)? = null
    private var numItems: Int = 0
    private var onSpinned: ((Int) -> Unit)? = null

    override val angle: Float
        get() = _angle.value

    override val minorAxisFactor: Float
        get() = _eccentricity.value

    private val decayAnimationSpec = FloatSpringSpec(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessHigh,
    )

    override suspend fun stop() {
        _angle.stop()
    }


    override fun setOnSnapToItem(onSnapToItem: (Int) -> Unit) {
        this.onSnapToItem = onSnapToItem
    }

    override fun setOnSpinned(onSpinned: (Int) -> Unit) {
        this.onSpinned = onSpinned
    }

    override suspend fun snapTo(angle: Float) {
        _angle.snapTo(angle)
        onSpinned?.invoke(getClosestItem(numItems))
    }

    override suspend fun decayTo(angle: Float, velocity: Float) {
        _angle.animateTo(
            targetValue = angle,
            initialVelocity = velocity,
            animationSpec = decayAnimationSpec,
        )
    }

    override fun setNumItems(numItems: Int) {
        this.numItems = numItems
    }

    override suspend fun snapToClosestItem(numItems: Int) {
        val angleStep = when {
            numItems <= 4 -> 60f
            else -> 360f / numItems
        }
        val currentAngle = angle
        val targetAngle = when {
            numItems <= 4 -> (currentAngle / 60f).roundToInt() * 60f
            else -> (currentAngle / angleStep).roundToInt() * angleStep
        }

        val closestItemIndex = ((targetAngle / angleStep).roundToInt().absoluteValue.log { "snapToClosestItem absoluteValue" } % numItems)
            .log { "snapToClosestItem closestItemIndex" }
        onSnapToItem?.invoke(closestItemIndex)
        _angle.animateTo(targetAngle)
    }

    override fun getClosestItem(numItems: Int): Int {
        val angleStep = when {
            numItems <= 4 -> 60f
            else -> 360f / numItems
        }
        val currentAngle = angle
        val targetAngle = when {
            numItems <= 4 -> (currentAngle / 60f).roundToInt() * 60f
            else -> (currentAngle / angleStep).roundToInt() * angleStep
        }

        val closestItemIndex = ((targetAngle / angleStep).roundToInt().absoluteValue.log { "snapToClosestItem absoluteValue" } % numItems)
            .log { "snapToClosestItem closestItemIndex" }
        return closestItemIndex
    }

    override fun setMinorAxisFactor(factor: Float) {
        _eccentricity.value = factor.coerceIn(-1f, 1f)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CircularCarouselStateImpl

        if (_angle != other._angle) return false
        if (_eccentricity != other._eccentricity) return false
        if (decayAnimationSpec != other.decayAnimationSpec) return false

        return true
    }

    override fun hashCode(): Int {
        var result = _angle.hashCode()
        result = 31 * result + _eccentricity.hashCode()
        result = 31 * result + decayAnimationSpec.hashCode()
        return result
    }
}

@Composable
fun rememberCircularCarouselState(): CircularCarouselState = remember {
    CircularCarouselStateImpl()
}

@Composable
fun CircularCarousel(
    modifier: Modifier = Modifier,
    numItems: Int,
    state: CircularCarouselState = rememberCircularCarouselState(),
    onSnapToItem: ((Int) -> Unit)? = null,
    onSpinned:((Int) -> Unit)? = null,
    onClick: (Int) -> Unit,
    contentFactory: @Composable (Int) -> Unit,
) {
    require(numItems > 0) { "The number of items must be greater than 0" }
    state.setNumItems(numItems)
    state.setOnSnapToItem { onSnapToItem?.invoke(it) }
    state.setOnSpinned { onSpinned?.invoke(it) }
    Box(modifier = modifier
        .graphicsLayer { alpha = 0.99f }
        .drawWithContent {
            // Draw the composable content first
            drawContent()

            // Apply fading effect at the edges using a gradient
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, Color.Black, Color.Black, Color.Transparent),
                    startX = 0f,
                    endX = size.width
                ),
                blendMode = BlendMode.DstIn
            )
        }
    ) {
        Layout(
            modifier = modifier.dragWithClick(state, numItems) {
                onClick.invoke(state.getClosestItem(numItems))
            },
            content = {
                // Reduce angle step for 2 and 3 items
                    val angleStep = when {
                        numItems <= 4 -> 60f
                        else -> 360f / numItems.toFloat() // Regular case for more than 3 items
                    }

                repeat(numItems) { index ->
                    val itemAngle = (state.angle + angleStep * index.toFloat()).normalizeAngle()
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(if (itemAngle <= 180f) 180f - itemAngle else itemAngle - 180f)
                            .graphicsLayer {
                                cameraDistance = 12f * density
                                rotationY = itemAngle
                                alpha = if (itemAngle < 90f || itemAngle > 270f) 1f else .0f
                                val scale = 1f - 1.2f * when {
                                    itemAngle <= 180f -> itemAngle / 180f
                                    else -> (360f - itemAngle) / 180f
                                }
                                scaleX = scale
                                scaleY = scale
                            }
                    ) {
                        contentFactory(index)
                    }
                }
            }
        ) { measurables, constraints ->
            val itemDimension = constraints.maxHeight
            val itemConstraints = Constraints.fixed(
                width = itemDimension.toInt(),
                height = constraints.maxHeight,
            )
            val placeables = measurables.map { measurable -> measurable.measure(itemConstraints) }
            layout(
                width = constraints.maxWidth,
                height = constraints.maxHeight,
            ) {
                val availableHorizontalSpace = when {
                    numItems <= 4 -> constraints.maxWidth * (0.8 / 5) * 6
                    else -> constraints.maxWidth * (0.8 / 5) * placeables.size
                }
                val horizontalOffset = constraints.maxWidth / 3.5
                val verticalOffset = 0

                // Reduce angle step for closer spacing
                val angleStep = when {
                    numItems <= 4 -> 2.0 * PI / 6
                    else -> 2.0 * PI / numItems.toDouble() // Regular case for more than 3 items
                }

                placeables.forEachIndexed { index, placeable ->
                    val itemAngle = (state.angle.toDouble()
                        .degreesToRadians() + (angleStep * index.toDouble())) % 360.0
                    val offset = getCoordinates(
                        width = availableHorizontalSpace / 2.0,
                        height = (constraints.maxHeight.toDouble() / 2.0 - itemDimension) * state.minorAxisFactor,
                        angle = itemAngle,
                    )

                    placeable.placeRelative(
                        x = (horizontalOffset + offset.x).roundToInt(),
                        y = verticalOffset,
                    )
                }
            }
        }
    }
}

private const val DRAG_THRESHOLD = 8f // Pixels
private fun Modifier.dragWithClick(
    state: CircularCarouselState,
    numItems: Int,
    onClick: (() -> Unit)? = null
) = pointerInput(Unit) {
    val decay = splineBasedDecay<Float>(this)
    coroutineScope {
        while (true) {
            val down = awaitPointerEventScope {
                awaitFirstDown().also { it.consume() } // Consume the down event
            }
            state.stop()
            val tracker = VelocityTracker()
            val degreesPerPixel = 180f / size.width.toFloat()

            var isDragging = false
            var totalDragDistance = 0f

            awaitPointerEventScope {
                drag(pointerId = down.id) { change ->
                    totalDragDistance += change.positionChange().x.absoluteValue

                    if (!isDragging && totalDragDistance > DRAG_THRESHOLD) {
                        isDragging = true // Only start dragging after crossing the threshold
                    }

                    if (isDragging) {
                        val horizontalDragOffset = state.angle + change.positionChange().x * degreesPerPixel
                        val adjustedDragOffset = when (numItems) {
                            1 -> horizontalDragOffset.coerceIn(0f, 0f)
                            2 -> horizontalDragOffset.coerceIn(-60f, 0f)
                            3 -> horizontalDragOffset.coerceIn(-120f, 0f)
                            4 -> horizontalDragOffset.coerceIn(-180f, 0f)
                            else -> horizontalDragOffset
                        }

                        launch {
                            state.snapTo(adjustedDragOffset)
                        }

                        tracker.addPosition(change.uptimeMillis, change.position)
                        change.consume()
                    }
                }
            }

            if (!isDragging) {
                // Consume the event to prevent propagation to the parent
                interceptOutOfBoundsChildEvents = true
                down.consume()
                onClick?.invoke()
            } else {
                // Handle drag fling
                val velocity = tracker.calculateVelocity().x
                val targetAngle = decay.calculateTargetValue(state.angle, velocity * degreesPerPixel)
                val adjustedTargetAngle = when (numItems) {
                    1 -> targetAngle.coerceIn(0f, 0f)
                    2 -> targetAngle.coerceIn(-60f, 0f)
                    3 -> targetAngle.coerceIn(-120f, 0f)
                    4 -> targetAngle.coerceIn(-180f, 0f)
                    else -> targetAngle
                }

                launch {
                    state.decayTo(
                        angle = adjustedTargetAngle,
                        velocity = velocity * degreesPerPixel,
                    )
                    state.snapToClosestItem(numItems)
                }
            }
        }
    }
}


private fun getCoordinates(width: Double, height: Double, angle: Double): Offset {
    val x = width * sin(angle)
    val y = height * cos(angle)
    return Offset(
        x = x.toFloat(),
        y = y.toFloat(),
    )
}

private fun Double.degreesToRadians(): Double = this / 360.0 * 2.0 * PI

private fun Float.normalizeAngle(): Float = (this % 360f).let { angle -> if (this < 0f) 360f + angle else angle }

@Preview(widthDp = 420, heightDp = 720)
@Composable
private fun PreviewCarouse() {
    ANYnetworkTheme {
        val colors = listOf(
            Color.Blue,
            Color.Red,
            Color.Green,
            Color.Magenta,
            Color.Cyan,
        )
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularCarousel(
                    numItems = 24,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    onClick = {}
                ) { index ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = colors[index % colors.size]
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = index.toString(),
                            color = Color.White,
                        )
                    }
                }
            }
        }
    }
}



data class PickerItem(
    val value: String = "",
    val color: Color = Color.Transparent,
    val iconTint: Color? = null,
    val resId: Int? = null,
    val iconContentDescription: String? = null,
    val interactionType: Int = -1
)