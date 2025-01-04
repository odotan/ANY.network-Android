package com.anynetwork.app.ui.components.hexagon

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

fun calculateLayersForElements(n: Int): Int {
    if (n <= 1) return 1 // If there's only one element, it fits in the central position (layer 1).

    var layer = 1
    var count = 1 // Start with the central element.

    // Increment the layer and count the number of elements until it exceeds `n`.
    while (count < n) {
        count += 6 * layer // Each layer adds 6 * layer elements.
        layer++
    }

    return layer - 1 // The layer where `n` fits is the previous layer.
}

fun generateHexagonColors(
    hexagonRows: Int,
    hexagonCols: Int,
    colorGrid: List<List<Color>>,
    startRow: Int = 0,
    startCol: Int = 0,
    offsetEvenRows: Boolean
): List<List<Color>> {
    val hexagonColors = MutableList(hexagonRows) {
        MutableList(hexagonCols) { Color.Transparent }
    }

    val colorGridRows = colorGrid.size
    val colorGridCols = colorGrid[0].size

    // Map the provided color grid to the hexagon grid starting from startRow, startCol
    for (row in 0 until colorGridRows) {
        for (col in 0 until colorGridCols) {
            val hexRow = startRow + row
            val hexCol = startCol + col

            if (hexRow < hexagonRows && hexCol < hexagonCols) {
                hexagonColors[hexRow][hexCol] = colorGrid[row][col]
            }
        }
    }

    // Randomize the rest of the hexagon grid with faded colors
    for (row in 0 until hexagonRows) {
        for (col in 0 until hexagonCols) {
            val isLastElementFromRow = col == hexagonCols - 1
            val isFromOffsettedRow = ((!offsetEvenRows && row % 2 == 0) || (offsetEvenRows && row % 2 == 1))
            if ((isLastElementFromRow && isFromOffsettedRow)) {
                hexagonColors[row][col] = Color.Transparent
            } else if (hexagonColors[row][col] == Color.Transparent) {
                val randomColor = colorGrid.random().random() // Random color from the grid
                val fadedColor = randomColor.copy(alpha = randomColor.alpha * Random.nextFloat() * 0.8f) // Random alpha between 0.2 and 0.7
                hexagonColors[row][col] = fadedColor
            }
        }
    }
    return hexagonColors
}
