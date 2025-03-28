package com.anynetwork.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anynetwork.app.ui.theme.ANYnetworkTheme
import com.anynetwork.app.ui.theme.montserratFontFamily
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max

@Composable
fun TiltedWheelPicker(
    options: List<String>,
    selectedIndex: Int,
    onSelectedChange: (Int) -> Unit
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)
    val coroutineScope = rememberCoroutineScope()

    val itemHeight = 60.dp  // Adjust based on needs
    val visibleItems = 3

    LaunchedEffect(selectedIndex) {
        listState.animateScrollToItem(selectedIndex)
    }

    Box(
        modifier = Modifier
            .height(itemHeight * visibleItems)  // Ensure only 3 items are visible
            .width(100.dp)
            .background(Color.Blue, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
        ) {
            itemsIndexed(options) { index, item ->
                val centerOffset = listState.layoutInfo.viewportSize.height / 2f
                val itemOffset = listState.layoutInfo.visibleItemsInfo
                    .find { it.index == index }?.offset ?: 0
                val distanceFromCenter = (itemOffset - centerOffset).toFloat()

                // 🔹 **Improved Scaling Formula**
                val scaleFactor = max(0.7f, 1.2f - abs(distanceFromCenter) / centerOffset * 0.5f)

                // 🔹 **Improved Tilt**
                val rotationX = (distanceFromCenter / centerOffset) * 25f

                Text(
                    text = item,
                    fontSize = 18.sp,  // Adjust for visibility
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .height(itemHeight)
                        .padding(vertical = 2.dp)
                        .graphicsLayer(
                            rotationX = rotationX,
                            scaleX = scaleFactor,  // 🔹 Ensures central item is largest
                            scaleY = scaleFactor,
                            alpha = scaleFactor
                        )
                        .clickable {
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                                onSelectedChange(index)
                            }
                        }
                )
            }
        }
    }
}

@Preview
@Composable
private fun TiltedWheelPickerPreview() {
    ANYnetworkTheme {
        Box(modifier = Modifier.size(40.dp)) {
            TiltedWheelPicker(
                options = (1..10).map { it.toString() },
                selectedIndex = 0,
                onSelectedChange = { newSelected -> }
            )
        }
    }
}
