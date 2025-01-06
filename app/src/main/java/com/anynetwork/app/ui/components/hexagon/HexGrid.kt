@file:OptIn(ExperimentalLayoutApi::class)

package com.anynetwork.app.ui.components.hexagon

import android.os.Parcelable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import com.anynetwork.app.ui.components.hexagon.HexGridCellPosition.Neighbor.BottomLeft
import com.anynetwork.app.ui.components.hexagon.HexGridCellPosition.Neighbor.BottomRight
import com.anynetwork.app.ui.components.hexagon.HexGridCellPosition.Neighbor.Left
import com.anynetwork.app.ui.components.hexagon.HexGridCellPosition.Neighbor.Right
import com.anynetwork.app.ui.components.hexagon.HexGridCellPosition.Neighbor.TopLeft
import com.anynetwork.app.ui.components.hexagon.HexGridCellPosition.Neighbor.TopRight
import com.anynetwork.app.ui.components.zoomable.ScrollGesturePropagation
import com.anynetwork.app.ui.components.zoomable.rememberZoomState
import com.anynetwork.app.ui.components.zoomable.zoomable
import com.anynetwork.app.ui.utils.log
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import kotlin.random.Random

sealed class ShadowStyle {
    data object None: ShadowStyle()
    data class Shown(
        val color: Color = Color.Black,
        val isAnimated: Boolean = true,
        val elevation: Float = 10f,
    ): ShadowStyle()
}

data class ChangeScale(
    val scale: Float,
    val position: Offset
)

@Composable
fun HexagonalGrid(
    modifier: Modifier = Modifier,
    items: List<List<HexagonContentStyle>>,
    rowSize: Int = 6,
    columnSize: Int = 14,
    minScale: Float = 1f,
    maxScale: Float = rowSize - 1f,
    changeScale: ChangeScale? = null,
    initialScale: Float = minScale,
    onCellPositionCalculated: ((Int, Offset, Int, Int) -> Unit)? = null,
    onZoom: ((zoom: Float, offset: Offset) -> Unit)? = null,
    isScrollEnabled: Boolean = true,
    offsetY: Int = 0,
    offsetEvenRows: Boolean = true,
    showIndexes: Boolean = false,
    isEditModeActivating: Boolean = false,
    gridScaling: Float = 1f
    ) {
    var gridCellsItems: List<HexagonContentStyle> = remember(items) {
        listOf()
    }

    LaunchedEffect(items) {
        Timber.i("home start animation hexagonalgrid for ${gridCellsItems.size} items")
    }
    gridCellsItems = items.flatten()

    val currentConfig = LocalConfiguration.current
    val gridWidth = remember { currentConfig.screenWidthDp.dp * gridScaling }
    val cellWidth = remember { (gridWidth / rowSize).log { "cellWidth" } }
    val cellHeight = remember { cellWidth * 96.99f/86.93f }
    val horizontalOffset = remember { ((cellWidth) / 2) }

    val verticalBorder = (cellWidth * 0.04403f).log { "verticalBorder" }
    val horizontalBorder = (cellHeight * 0.0395f).log { "horizontalBorder" }

    var draggedItem by remember { mutableStateOf<Int?>(null) }
    var draggedOffset by remember { mutableStateOf(DpOffset(0.dp, 0.dp)) }
    var draggedPosition by remember { mutableStateOf(Offset.Zero) }
    var hoveredItem by remember { mutableStateOf<Int?>(null) }

    val cellPositions = remember { mutableStateMapOf<Int, LayoutCoordinates>() }

    val zoomState = rememberZoomState(
        initialScale = initialScale,
        minScale = minScale,
        maxScale = maxScale,
    )

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(changeScale) {
        if (changeScale != null) zoomState.animateToPosition(
            targetScale = changeScale.scale,
            position = changeScale.position
        )
    }

    val verticalSpacing = (-(96.99f * cellWidth / 86.93f) * 0.2333333f).log { "verticalSpacing" }
//    val cellHeight = cellSize * sqrt(3f) / 2f
//    val verticalSpacing = remember { (-(2f * cellSize / sqrt(3f)) / 4).log { "verticalSpacing" } }
//    (verticalSpacing / initialScale).log { "verticalSpacing" }
//    val totalHeight = (columnSize * cellHeight + (columnSize - 1) * verticalSpacing) * initialScale
    val polygon = remember { createPolygon() }

    Box(
        modifier = if (gridScaling > 1f) Modifier.fillMaxSize()
            .requiredWidth(gridWidth * gridScaling)
            .requiredHeight(currentConfig.screenWidthDp.dp * gridScaling)
        else Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        FlowRow(
            modifier = modifier
                .offset(y = with(LocalDensity.current) { offsetY.toDp() })
                .scale(1/gridScaling)
                .zoomable(
                    zoomState,
                    zoomEnabled = isScrollEnabled,
                    enableOneFingerZoom = false,
                    scrollGesturePropagation = ScrollGesturePropagation.NotZoomed,
                    onZoom = { zoom, offsetX, offsetY ->
                        onZoom?.invoke(zoom, Offset(offsetX, offsetY))
                    }
                ),
            verticalArrangement = Arrangement.spacedBy(verticalSpacing),
            maxItemsInEachRow = rowSize
        ) {
            repeat(gridCellsItems.size) { index ->
                if (index == 0) Timber.i("home start animation start item placement")
                else if (index == gridCellsItems.lastIndex) Timber.i("home start animation last item placement")

                val contentStyle = gridCellsItems[index]
                val rowIndex = index / rowSize

                val roundedPolygonShape = remember(contentStyle.id) { RoundedPolygonShape(polygon) }

                var rotation = remember { Animatable(0f) }
                val isRotating =
                    ((contentStyle is CustomHexagonContentStyle && contentStyle.isShakable) || (contentStyle is IconHexagonContentStyle && contentStyle.isShakable)) && isEditModeActivating
                if (isRotating) {
                    rotation = remember { Animatable(-5f) }
                    // Trigger shake effect only if the cell is not empty
                    LaunchedEffect(Unit) {
                        delay(Random.nextLong(150))
                        val shakeSpec = infiniteRepeatable<Float>(
                            animation = tween(150, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        )
                        // Animate between -10 and 10 degrees
                        rotation.animateTo(5f, animationSpec = shakeSpec)
                    }
                }
                val showContent = remember(contentStyle, draggedItem) {
                    (contentStyle is TrashCanHexagonContentStyle &&
                            draggedItem != null
                            && (gridCellsItems[draggedItem!!] as? NontransparentHexagonContentStyle)?.removableStrategy != null
                            )
                }

                StatelessRoundedHexagon(
                    modifier = Modifier//(if (isDragging) Modifier.animateItem() else Modifier)
                        .offset(
                            x = when {
                                offsetEvenRows && rowIndex % 2 == 1 -> horizontalOffset
                                !offsetEvenRows && rowIndex % 2 == 0 -> horizontalOffset
                                else -> 0.dp
                            }
                        )
                        .width(cellWidth)
                        .height(cellHeight)
                        .then(if (cellPositions[index] == null)
                            Modifier.onGloballyPositioned { coordinates ->

                                val height = coordinates.size.height
                                val width = coordinates.size.width

                                onCellPositionCalculated?.invoke(
                                    index,
                                    coordinates.positionInRoot(),
                                    width,
                                    height
                                )

                                val center = coordinates.boundsInParent().center
                                if (cellPositions[index]?.boundsInParent()?.center != center) {
                                    cellPositions[index] = coordinates
                                }
                            } else if (isRotating) {
                            Modifier.rotate(rotation.value)
                        } else Modifier
                        ),
                    shape = if (contentStyle is NontransparentHexagonContentStyle) roundedPolygonShape else null,
                    contentStyle = contentStyle,
                    verticalBorder = verticalBorder,
                    horizontalBorder = horizontalBorder,
                    hovered = hoveredItem == index && (contentStyle is NontransparentHexagonContentStyle && contentStyle.isHoverable),
                    showContent = showContent,
                    isDraggable = contentStyle is NontransparentHexagonContentStyle && contentStyle.isDraggable,
                    onClick = {
                        val cellPosition = cellPositions[index]!!
                        when (contentStyle) {
                            is ImageHexagonContentStyle -> contentStyle.onClick.invoke(cellPositions[index]!!.positionOnScreen())
                            is CustomHexagonContentStyle -> contentStyle.onClick.invoke(
                                cellPosition.boundsInRoot().center
                            )

                            is IconHexagonContentStyle -> contentStyle.onClick.invoke(cellPositions[index]!!.positionOnScreen())
                            else -> {}
                        }
                    },
                    pointerInput = {
                        detectDragGesturesAfterLongPress(
                            onDragStart = {
                                Timber.i("onDragStart")
                                draggedItem = index
                            },
                            onDragEnd = {
                                Timber.i("onDragEnd")
                                draggedItem?.let { draggedIndex ->
                                    val draggedPosition = Offset(
                                        cellPositions[draggedIndex]?.boundsInParent()?.center?.x
                                            ?: 0f,
                                        cellPositions[draggedIndex]?.boundsInParent()?.center?.y
                                            ?: 0f
                                    ) + Offset(draggedOffset.x.toPx(), draggedOffset.y.toPx())
                                    //
                                    val closestCell = cellPositions.minByOrNull { (_, position) ->
                                        draggedPosition.getDistanceTo(position.boundsInParent().center)
                                    }

                                    if (closestCell != null) {
                                        val (targetIndex, _) = closestCell
                                        Timber.i("Dragged item dropped on cell $targetIndex")
                                        // Handle drop logic here
                                        if (gridCellsItems.get(targetIndex) is TrashCanHexagonContentStyle) {
                                            (gridCellsItems.get(draggedIndex) as? NontransparentHexagonContentStyle)
                                                ?.removableStrategy
                                                ?.onRemove
                                                ?.invoke()
                                        }
                                    }
                                }

                                draggedItem = null // Reset drag state
                                draggedOffset = DpOffset.Zero
                                draggedPosition = Offset.Zero
                                hoveredItem = null
                            },
                            onDragCancel = {
                                Timber.i("onDragCancel")
                                draggedItem = null // Reset drag state
                                draggedOffset = DpOffset.Zero
                                hoveredItem = null
                            },
                            onDrag = { change, dragAmount ->
                                Timber.i("onDrag isDragging")
                                draggedOffset = DpOffset(
                                    draggedOffset.x + dragAmount.x.toDp(),
                                    draggedOffset.y + dragAmount.y.toDp()
                                )
                                draggedItem?.let { draggedIndex ->
                                    cellPositions[draggedIndex]?.let { draggedItemCoordinates ->
                                        val _draggedPosition = Offset(
                                            draggedItemCoordinates.boundsInParent().center.x,
                                            draggedItemCoordinates.boundsInParent().center.y
                                        ) + Offset(draggedOffset.x.toPx(), draggedOffset.y.toPx())
                                        //
                                        val closestCell =
                                            cellPositions.minByOrNull { (_, position) ->
                                                _draggedPosition.getDistanceTo(position.boundsInParent().center)
                                            }

                                        if (closestCell != null) {
                                            val (targetIndex, _) = closestCell
                                            Timber.i("Dragged item dropped on cell $targetIndex")
                                            hoveredItem = targetIndex
                                            // Handle drop logic here
                                        }
                                        val centerX =
                                            draggedItemCoordinates.positionInRoot().x + draggedItemCoordinates.size.width / 2
                                        val centerY =
                                            draggedItemCoordinates.positionInRoot().y + draggedItemCoordinates.size.height / 2

                                        if (draggedItem == index) {
                                            draggedPosition = Offset(centerX, centerY)
                                        }
                                    }
                                }
                            }
                        )
                    }
                )
            }
        }
    }

//        Box(
//            modifier = Modifier
//                .size(5.dp) // Adjust size as needed
//                .background(Color.Red, CircleShape) // Customize appearance
//                .align(Alignment.Center)
//        )
    // Render Dragged Item Above
    draggedItem?.let { index ->
        if (draggedPosition != Offset.Zero) {
            draggedPosition.log { "draggedPosition" }
            val roundedPolygonShape = remember(polygon) { RoundedPolygonShape(polygon) }
            Box(
                Modifier
                    .offset {
                        IntOffset(
                            (draggedPosition.x).toInt(),
                            (draggedPosition.y).toInt()
                        ) + IntOffset(
                            (draggedOffset.x.toPx() * zoomState.scale).toInt(),
                            (draggedOffset.y.toPx() * zoomState.scale).toInt()
                        )
                    }
                    .zIndex(1f)
            ) {
                StatelessRoundedHexagon(
                    modifier = Modifier
                        .size((cellWidth/* + verticalBorder*/) * zoomState.scale * 1.2f)
                        .alpha(0.8f),
                    shape = roundedPolygonShape,
                    contentStyle = gridCellsItems[index],
                    verticalBorder = verticalBorder,
                    horizontalBorder = horizontalBorder,
                    drawOverlay = false,
                    scale = zoomState.scale * 1.2f
                )
            }
        }
    }
}

@Composable
private fun StatelessRoundedHexagon(
    modifier: Modifier,
    shape: Shape?,
    contentStyle: HexagonContentStyle,
    onClick: ((Offset) -> Unit)? = null,
    showIndexes: Boolean = false,
    index: Int? = null,
    verticalBorder: Dp,
    horizontalBorder: Dp,
    drawOverlay: Boolean = true,
    hovered: Boolean = false,
    showContent: Boolean = true,
    scale: Float = 1f,
    isDraggable: Boolean = false,
    pointerInput: (suspend PointerInputScope.() -> Unit)? = null
) {
    Box(modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        RoundedHexagon(
            modifier = Modifier
                .padding(
                    vertical = verticalBorder,
                    horizontal = horizontalBorder
                )
                .aspectRatio(79.93f / 89.99f)
                .graphicsLayer {
                    if (shape != null) {
                        clip = true
                        this.shape = shape
                    }
                }
                .then(
                    if (pointerInput != null && isDraggable) Modifier.pointerInput(
                        Unit,
                        pointerInput
                    ) else Modifier
                ),
            onClick = onClick,
            contentStyle = contentStyle,
            hovered = hovered,
            showContent = showContent,
            scale = scale
        )

//        Box(modifier = Modifier.size(1.dp).background(Color.Red)) { }

        if (drawOverlay) {
            if (contentStyle is IconHexagonContentStyle) {
                contentStyle.overlay?.invoke(this)
            } else if (contentStyle is EmptyHexagonContentStyle) {
                contentStyle.overlay?.invoke(this)
            } else if (contentStyle is CustomHexagonContentStyle) {
                contentStyle.overlay?.invoke(this)
            }
        }
    }
}

fun Offset.getDistanceTo(other: Offset): Float {
    val dx = this.x - other.x
    val dy = this.y - other.y
    return kotlin.math.sqrt(dx * dx + dy * dy)
}

@Composable
fun CoverBox(alpha: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { this.alpha = alpha }
            .background(Color(0xFF6E4CD4))
    )
}

@Parcelize data class HexGridCellPosition(val column: Int, val row: Int, val gridRows: Int = 0, val gridColumns: Int = 0, val offsetEvenRows: Boolean = true): Parcelable {
    fun getIndex() = (row) * gridColumns + column

    fun isSame(column: Int, row: Int) = row == this.row && column == this.column

    fun getNeighborPosition(neighbor: Neighbor): HexGridCellPosition {
        return when (neighbor) {
            TopLeft -> copy(column = column, row = row - 1)
            TopRight -> copy(column = column + 1, row = row - 1)
            Right -> copy(column = column + 1, row = row)
            BottomRight -> copy(column = column + 1, row = row + 1)
            BottomLeft -> copy(column = column, row = row + 1)
            Left -> copy(column = column - 1, row = row)
        }
    }

    enum class Neighbor {
        TopLeft, TopRight, Right, BottomRight, BottomLeft, Left
    }
}

val hexCellsBackgroundColorsGrid = listOf(
    // Row 1
    listOf(
        Color(0x03FFFFFF), // opacity: 0.01
        Color(0x03FFFFFF), // opacity: 0.01
        Color(0x03FFFFFF), // opacity: 0.01
        Color(0x03FFFFFF), // opacity: 0.01
        Color(0x03FFFFFF)  // opacity: 0.01
    ),

    // Row 2
    listOf(
        Color(0x05FFFFFF), // opacity: 0.02
        Color(0x03FFFFFF), // opacity: 0.01
        Color(0x07F8BF4D), // opacity: 0.03
        Color(0x03FFFFFF), // opacity: 0.01
        Color.Transparent, // Transparent
        Color(0x05FFFFFF)  // opacity: 0.02
    ),

    // Row 3
    listOf(
        Color(0x03FFFFFF), // opacity: 0.01
        Color(0x04FFFFFF), // opacity: 0.015
        Color(0xFF281D4A), // Fully opaque
        Color(0x04FFFFFF), // opacity: 0.015
        Color(0x1413D16B)   // opacity: 0.08
    ),

    // Row 4
    listOf(
        Color(0x03FFFFFF), // opacity: 0.01
        Color(0x0FE34284), // opacity: 0.06
        Color(0x03FFFFFF), // opacity: 0.01
        Color(0x03FFFFFF), // opacity: 0.01
        Color(0x07FFFFFF), // opacity: 0.03
        Color(0x07FFFFFF)  // opacity: 0.03
    ),

    // Row 5
    listOf(
        Color(0x05FFFFFF), // opacity: 0.02
        Color(0x05FFFFFF), // opacity: 0.02
        Color(0x05FFFFFF), // opacity: 0.02
        Color(0x05FFFFFF), // opacity: 0.02
        Color(0x05FFFFFF)  // opacity: 0.02
    ),

    // Row 6
    listOf(
        Color(0x05FFFFFF), // opacity: 0.02
        Color(0xFF281D4A), // Fully opaque
        Color(0x07FFFFFF), // opacity: 0.03
        Color(0x07FFFFFF), // opacity: 0.03
        Color(0xFF281D4A), // Fully opaque
        Color(0x05FFFFFF)  // opacity: 0.02
    ),

    // Row 7
    listOf(
        Color(0x05FFFFFF), // opacity: 0.02
        Color(0xFF1C1336), // Fully opaque
        Color(0x07FFFFFF), // opacity: 0.03
        Color(0xFF1C1336), // Fully opaque
        Color(0x05FFFFFF)  // opacity: 0.02
    ),

    // Row 8
    listOf(
        Color.Transparent, // Transparent
        Color(0x0AF8BF4D), // opacity: 0.04
        Color(0x07FFFFFF), // opacity: 0.03
        Color(0x07FFFFFF), // opacity: 0.03
        Color(0x07FFFFFF), // opacity: 0.03
        Color.Transparent  // Transparent
    ),

    // Row 9
    listOf(
        Color.Transparent, // Transparent
        Color(0x07FFFFFF), // opacity: 0.03
        Color(0xFF281D4A), // Fully opaque
        Color(0x07FFFFFF), // opacity: 0.03
        Color(0x17FF64FF)   // opacity: 0.09
    ),

    // Row 10
    listOf(
        Color(0x04FFFFFF), // opacity: 0.015
        Color(0x07FFFFFF), // opacity: 0.03
        Color(0x14FF6061), // opacity: 0.08
        Color(0x05FFFFFF), // opacity: 0.02
        Color.Transparent, // Transparent
        Color(0x04FFFFFF)  // opacity: 0.015
    ),

    // Row 11
    listOf(
        Color(0x03FFFFFF), // opacity: 0.012
        Color.Transparent, // Transparent
        Color(0x03FFFFFF), // opacity: 0.012
        Color(0x146E4CD4), // opacity: 0.08
        Color(0x03FFFFFF)  // opacity: 0.012
    ),

    // Row 12
    listOf(
        Color(0x05FFFFFF), // opacity: 0.02
        Color(0x03FFFFFF), // opacity: 0.01
        Color.Transparent, // Transparent
        Color(0x03FFFFFF), // opacity: 0.01
        Color.Transparent, // Transparent
        Color(0x05FFFFFF)  // opacity: 0.02
    )
)

val hexCellsBackgroundColors = listOf(
    // Row 0 (Added Row)
    Color.Transparent,
    Color.Transparent,
    Color.Transparent,
    Color.Transparent,
    Color.Transparent,
    Color.Transparent,

    // Row 1
    Color(0x03FFFFFF), // opacity: 0.01
    Color(0x03FFFFFF), // opacity: 0.01
    Color(0x03FFFFFF), // opacity: 0.01
    Color(0x03FFFFFF), // opacity: 0.01
    Color(0x03FFFFFF), // opacity: 0.01
    Color.Transparent,

    // Row 2
    Color(0x05FFFFFF), // opacity: 0.02
    Color(0x03FFFFFF), // opacity: 0.01
    Color(0x07F8BF4D), // opacity: 0.03
    Color(0x03FFFFFF), // opacity: 0.01
    Color.Transparent, // Transparent
    Color(0x05FFFFFF), // opacity: 0.02

    // Row 3
    Color(0x03FFFFFF), // opacity: 0.01
    Color(0x04FFFFFF), // opacity: 0.015
    Color(0xFF281D4A), // Fully opaque
    Color(0x04FFFFFF), // opacity: 0.015
    Color(0x1413D16B), // opacity: 0.08
    Color.Transparent,

    // Row 4
    Color(0x03FFFFFF), // opacity: 0.01
    Color(0x0FE34284), // opacity: 0.06
    Color(0x03FFFFFF), // opacity: 0.01
    Color(0x03FFFFFF), // opacity: 0.01
    Color(0x07FFFFFF), // opacity: 0.03
    Color(0x07FFFFFF), // opacity: 0.03

    // Row 5
    Color(0x05FFFFFF), // opacity: 0.02
    Color(0x05FFFFFF), // opacity: 0.02
    Color(0x05FFFFFF), // opacity: 0.02
    Color(0x05FFFFFF), // opacity: 0.02
    Color(0x05FFFFFF), // opacity: 0.02
    Color.Transparent,

    // Row 6
    Color(0x05FFFFFF), // opacity: 0.02
    Color(0xFF281D4A), // Fully opaque
    Color(0x07FFFFFF), // opacity: 0.03
    Color(0x07FFFFFF), // opacity: 0.03
    Color(0xFF281D4A), // Fully opaque
    Color(0x05FFFFFF), // opacity: 0.02

    // Row 7
    Color(0x05FFFFFF), // opacity: 0.02
    Color(0xFF1C1336), // Fully opaque
    Color(0x07FFFFFF), // opacity: 0.03
    Color(0xFF1C1336), // Fully opaque
    Color(0x05FFFFFF), // opacity: 0.02
    Color.Transparent,

    // Row 8
    Color.Transparent, // Transparent
    Color(0x0AF8BF4D), // opacity: 0.04
    Color(0x07FFFFFF), // opacity: 0.03
    Color(0x07FFFFFF), // opacity: 0.03
    Color(0x07FFFFFF), // opacity: 0.03
    Color.Transparent, // Transparent

    // Row 9
    Color.Transparent, // Transparent
    Color(0x07FFFFFF), // opacity: 0.03
    Color(0xFF281D4A), // Fully opaque
    Color(0x07FFFFFF), // opacity: 0.03
    Color(0x17FF64FF), // opacity: 0.09
    Color.Transparent,

    // Row 10
    Color(0x04FFFFFF), // opacity: 0.015
    Color(0x07FFFFFF), // opacity: 0.03
    Color(0x14FF6061), // opacity: 0.08
    Color(0x05FFFFFF), // opacity: 0.02
    Color.Transparent, // Transparent
    Color(0x04FFFFFF), // opacity: 0.015

    // Row 11
    Color(0x03FFFFFF), // opacity: 0.012
    Color.Transparent, // Transparent
    Color(0x03FFFFFF), // opacity: 0.012
    Color(0x146E4CD4), // opacity: 0.08
    Color(0x03FFFFFF), // opacity: 0.012
    Color.Transparent,

    // Row 12
    Color(0x05FFFFFF), // opacity: 0.02
    Color(0x03FFFFFF), // opacity: 0.01
    Color.Transparent, // Transparent
    Color(0x03FFFFFF), // opacity: 0.01
    Color.Transparent, // Transparent
    Color(0x05FFFFFF), // opacity: 0.02

    // Row 13 (Added Row)
    Color.Transparent,
    Color.Transparent,
    Color.Transparent,
    Color.Transparent,
    Color.Transparent,
    Color.Transparent
)

//@Composable
//fun HexagonalGrid(
//    modifier: Modifier = Modifier,
//    items: List<List<HexagonContentStyle>>,
//    rowSize: Int = 6,
//    columnSize: Int = 14,
//    minScale: Float = 1f,
//    maxScale: Float = rowSize - 1f,
//    changeScale: ChangeScale? = null,
//    initialScale: Float = minScale,
//    onCellPositionCalculated: ((Int, Offset, Int, Int) -> Unit)? = null,
//    onZoom: ((zoom: Float, offset: Offset) -> Unit)? = null,
//    isScrollEnabled: Boolean = true,
//    offsetY: Int = 0,
//    offsetEvenRows: Boolean = true,
//    showIndexes: Boolean = false,
//    isEditModeActivating: Boolean = false,
//    borderMultiplier: Float = 1f
//) {
//    val lazyGridState = rememberLazyGridState()
//
//    var gridCellsItems: List<HexagonContentStyle> = remember(items) {
//        listOf()
//    }
//
//    LaunchedEffect(gridCellsItems) {
//        Timber.i("home start animation hexagonalgrid for ${gridCellsItems.size} items")
//    }
//
//    gridCellsItems = items.flatten()
//
//    val currentConfig = LocalConfiguration.current
//    val gridWidth = remember { currentConfig.screenWidthDp.dp }
//    val cellSize = remember { (gridWidth / rowSize) }
//
//    val verticalBorder = (gridWidth / rowSize) * 0.09163265f / 3 * 3.5f / 2.5f * borderMultiplier
//    val horizontalBorder = (gridWidth / rowSize) * 0.09163265f / 3 * borderMultiplier
//
//    var draggedItem by remember { mutableStateOf<Int?>(null) }
//    var draggedOffset by remember { mutableStateOf(DpOffset(0.dp, 0.dp)) }
//    var draggedPosition by remember { mutableStateOf(Offset.Zero) }
//    var hoveredItem by remember { mutableStateOf<Int?>(null) }
//
//    val cellPositions = remember { mutableStateMapOf<Int, LayoutCoordinates>() }
//
//    val zoomState = rememberZoomState(
//        initialScale = initialScale,
//        minScale = minScale,
//        maxScale = maxScale,
//    )
//
//    val coroutineScope = rememberCoroutineScope()
//    LaunchedEffect(changeScale) {
//        if (changeScale != null) zoomState.animateToPosition(
//            targetScale = changeScale.scale,
//            position = changeScale.position
//        )
//    }
//
//    val verticalSpacing = -(89.99f * cellSize / 79.93f) / 4
////    val cellHeight = cellSize * sqrt(3f) / 2f
////    val verticalSpacing = remember { (-(2f * cellSize / sqrt(3f)) / 4).log { "verticalSpacing" } }
////    (verticalSpacing / initialScale).log { "verticalSpacing" }
////    val totalHeight = (columnSize * cellHeight + (columnSize - 1) * verticalSpacing) * initialScale
//    val polygon = remember { createPolygon() }
//
//    LazyVerticalGrid(
//        modifier = modifier
//            .offset(y = with(LocalDensity.current) { offsetY.toDp() + verticalSpacing })
//            .fillMaxSize()
//            .zoomable(
//                zoomState,
//                zoomEnabled = isScrollEnabled,
//                enableOneFingerZoom = false,
//                scrollGesturePropagation = ScrollGesturePropagation.NotZoomed,
//                onZoom = { zoom, offsetX, offsetY ->
//                    onZoom?.invoke(zoom, Offset(offsetX, offsetY))
//                }
//            ),
//        columns = GridCells.Fixed(rowSize),
//        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
//        state = lazyGridState,
//        userScrollEnabled = true
//    ) {
//        itemsIndexed(
//            items = gridCellsItems,
//            key = { index, item -> item.id }
//        ) { index, contentStyle ->
//            val rowIndex = index / rowSize
//            val horizontalOffset = when {
//                offsetEvenRows && rowIndex % 2 == 1 -> gridWidth / (rowSize * 2)
//                !offsetEvenRows && rowIndex % 2 == 0 -> gridWidth / (rowSize * 2)
//                else -> 0.dp
//            }
//
//            val roundedPolygonShape = remember(contentStyle.id) { RoundedPolygonShape(polygon) }
//
//            var rotation = remember { Animatable(0f) }
//            val isRotating = ((contentStyle is CustomHexagonContentStyle && contentStyle.isShakable) || (contentStyle is IconHexagonContentStyle && contentStyle.isShakable)) && isEditModeActivating
//            if (isRotating) {
//                rotation = remember { Animatable(-5f) }
//                // Trigger shake effect only if the cell is not empty
//                LaunchedEffect(Unit) {
//                    delay(Random.nextLong(150))
//                    val shakeSpec = infiniteRepeatable<Float>(
//                        animation = tween(150, easing = LinearEasing),
//                        repeatMode = RepeatMode.Reverse
//                    )
//                    // Animate between -10 and 10 degrees
//                    rotation.animateTo(5f, animationSpec = shakeSpec)
//                }
//            }
//            val showContent = remember(contentStyle, draggedItem) { (contentStyle is TrashCanHexagonContentStyle &&
//                    draggedItem != null
//                    && (gridCellsItems[draggedItem!!] as? NontransparentHexagonContentStyle)?.removableStrategy != null
//                    ) }
//
//            StatelessRoundedHexagon(
//                modifier = Modifier//(if (isDragging) Modifier.animateItem() else Modifier)
//                    .offset(horizontalOffset)
//                    .then(if (cellPositions[index] == null)
//                        Modifier.onGloballyPositioned { coordinates ->
//
//                            val height = coordinates.size.height
//                            val width = coordinates.size.width
//
//                            onCellPositionCalculated?.invoke(
//                                index,
//                                coordinates.positionInRoot(),
//                                width,
//                                height
//                            )
//
//                            val center = coordinates.boundsInParent().center
//                            if (cellPositions[index]?.boundsInParent()?.center != center) {
//                                cellPositions[index] = coordinates
//                            }
//                        } else if (isRotating) {
//                            Modifier.rotate(rotation.value)
//                        } else Modifier
//                    ),
//                shape = roundedPolygonShape,
//                contentStyle = contentStyle,
//                verticalBorder = verticalBorder,
//                horizontalBorder = horizontalBorder,
//                hovered = hoveredItem == index && (contentStyle is NontransparentHexagonContentStyle && contentStyle.isHoverable),
//                showContent = showContent,
//                isDraggable = contentStyle is NontransparentHexagonContentStyle && contentStyle.isDraggable,
//                onClick = {
//                    val cellPosition = cellPositions[index]!!
//                    when (contentStyle) {
//                        is ImageHexagonContentStyle -> contentStyle.onClick.invoke(cellPositions[index]!!.positionOnScreen())
//                        is CustomHexagonContentStyle -> contentStyle.onClick.invoke(
//                            cellPosition.boundsInRoot().center
//                        )
//
//                        is IconHexagonContentStyle -> contentStyle.onClick.invoke(cellPositions[index]!!.positionOnScreen())
//                        else -> {}
//                    }
//                },
//                pointerInput = {
//                    detectDragGesturesAfterLongPress(
//                        onDragStart = {
//                            Timber.i("onDragStart")
//                            draggedItem = index
//                        },
//                        onDragEnd = {
//                            Timber.i("onDragEnd")
//                            draggedItem?.let { draggedIndex ->
//                                val draggedPosition = Offset(
//                                    cellPositions[draggedIndex]?.boundsInParent()?.center?.x
//                                        ?: 0f,
//                                    cellPositions[draggedIndex]?.boundsInParent()?.center?.y
//                                        ?: 0f
//                                ) + Offset(draggedOffset.x.toPx(), draggedOffset.y.toPx())
//                                //
//                                val closestCell = cellPositions.minByOrNull { (_, position) ->
//                                    draggedPosition.getDistanceTo(position.boundsInParent().center)
//                                }
//
//                                if (closestCell != null) {
//                                    val (targetIndex, _) = closestCell
//                                    Timber.i("Dragged item dropped on cell $targetIndex")
//                                    // Handle drop logic here
//                                    if (gridCellsItems.get(targetIndex) is TrashCanHexagonContentStyle) {
//                                        (gridCellsItems.get(draggedIndex) as? NontransparentHexagonContentStyle)
//                                            ?.removableStrategy
//                                            ?.onRemove
//                                            ?.invoke()
//                                    }
//                                }
//                            }
//
//                            draggedItem = null // Reset drag state
//                            draggedOffset = DpOffset.Zero
//                            draggedPosition = Offset.Zero
//                            hoveredItem = null
//                        },
//                        onDragCancel = {
//                            Timber.i("onDragCancel")
//                            draggedItem = null // Reset drag state
//                            draggedOffset = DpOffset.Zero
//                            hoveredItem = null
//                        },
//                        onDrag = { change, dragAmount ->
//                            Timber.i("onDrag isDragging")
//                            draggedOffset = DpOffset(
//                                draggedOffset.x + dragAmount.x.toDp(),
//                                draggedOffset.y + dragAmount.y.toDp()
//                            )
//                            draggedItem?.let { draggedIndex ->
//                                cellPositions[draggedIndex]?.let { draggedItemCoordinates ->
//                                    val _draggedPosition = Offset(
//                                        draggedItemCoordinates.boundsInParent().center.x,
//                                        draggedItemCoordinates.boundsInParent().center.y
//                                    ) + Offset(draggedOffset.x.toPx(), draggedOffset.y.toPx())
//                                    //
//                                    val closestCell =
//                                        cellPositions.minByOrNull { (_, position) ->
//                                            _draggedPosition.getDistanceTo(position.boundsInParent().center)
//                                        }
//
//                                    if (closestCell != null) {
//                                        val (targetIndex, _) = closestCell
//                                        Timber.i("Dragged item dropped on cell $targetIndex")
//                                        hoveredItem = targetIndex
//                                        // Handle drop logic here
//                                    }
//                                    val centerX =
//                                        draggedItemCoordinates.positionInRoot().x + draggedItemCoordinates.size.width / 2
//                                    val centerY =
//                                        draggedItemCoordinates.positionInRoot().y + draggedItemCoordinates.size.height / 2
//
//                                    if (draggedItem == index) {
//                                        draggedPosition = Offset(centerX, centerY)
//                                    }
//                                }
//                            }
//                        }
//                    )
//                }
//            )
//        }
//    }
//
////        Box(
////            modifier = Modifier
////                .size(5.dp) // Adjust size as needed
////                .background(Color.Red, CircleShape) // Customize appearance
////                .align(Alignment.Center)
////        )
//    // Render Dragged Item Above
//    draggedItem?.let { index ->
//        if (draggedPosition != Offset.Zero) {
//            draggedPosition.log { "draggedPosition" }
//            val roundedPolygonShape = remember(polygon) { RoundedPolygonShape(polygon) }
//            Box(
//                Modifier
//                    .offset {
//                        IntOffset(
//                            (draggedPosition.x).toInt(),
//                            (draggedPosition.y).toInt()
//                        ) + IntOffset(
//                            (draggedOffset.x.toPx() * zoomState.scale).toInt(),
//                            (draggedOffset.y.toPx() * zoomState.scale).toInt()
//                        )
//                    }
//                    .zIndex(1f)
//            ) {
//                StatelessRoundedHexagon(
//                    modifier = Modifier
//                        .size((cellSize + verticalBorder) * zoomState.scale * 1.2f)
//                        .alpha(0.8f),
//                    shape = roundedPolygonShape,
//                    contentStyle = gridCellsItems[index],
//                    verticalBorder = verticalBorder,
//                    horizontalBorder = horizontalBorder,
//                    drawOverlay = false,
//                    scale = zoomState.scale * 1.2f
//                )
//            }
//        }
//    }
//}
