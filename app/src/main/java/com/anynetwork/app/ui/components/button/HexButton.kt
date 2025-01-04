package com.anynetwork.app.ui.components.button

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Cubic
import androidx.graphics.shapes.RoundedPolygon
import com.anynetwork.app.R
import com.anynetwork.app.ui.theme.DarkBlue
import com.anynetwork.app.ui.theme.montserratFontFamily
import com.anynetwork.app.ui.theme.sfProFontFamily
import com.anynetwork.app.ui.utils.csp
import com.anynetwork.app.ui.utils.xdph
import com.anynetwork.app.ui.utils.xdpv
import com.anynetwork.app.ui.utils.log
import com.gigamole.composeshadowsplus.common.ShadowsPlusDefaults
import com.gigamole.composeshadowsplus.softlayer.softLayerShadow

@Composable
fun HexButton(
    modifier: Modifier = Modifier,
    contentColor: Color = Color(0xFF6E4CD4),
    shadowAmbientColor: Color = Color(0xFF6E4CD4),
    shadowColor: Color = Color(0xFF6E4CD4),
    text: String = "",
    textColor: Color = Color.White,
    onClick: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition()

    // Create an animated float value for shadow elevation
    val animatedElevation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    animatedElevation.dp.log { "elevation" }

    Surface(
        modifier = modifier
            .softLayerShadow(
                radius = animatedElevation.dp,
                color = shadowColor,
                offset = DpOffset.Zero,
                shape = HexButtonShape(polygon),
                spread = ShadowsPlusDefaults.ShadowSpread,
                isAlphaContentClip = true
            )
            .clip(HexButtonShape(polygon))
            .clickable {
                onClick.invoke()
            },
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(contentColor),
            contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = textColor,
                style = TextStyle(
                    fontFamily = sfProFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.csp,
                    lineHeight = TextUnit(22f, TextUnitType.Sp)
                )
            )
        }
    }
}

@Composable
fun OutlineHexButton(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF6E4CD4),
    textColor: Color = Color.White,
    onClick: () -> Unit,
) {
    val clipShape = remember {
        HexButtonShape(polygon)
    }
    Box(modifier = Modifier.background(DarkBlue)) {
        Surface(
            modifier = modifier
                .graphicsLayer {
                    shape = clipShape
                    clip = true
                }
                .border(
                    width = 1.dp,
                    color = color,
                    clipShape
                ),
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Content",
                    color = textColor,
                    style = TextStyle(
                        fontFamily = montserratFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.csp,
                        lineHeight = TextUnit(22f, TextUnitType.Sp)
                    )
                )
            }
        }
    }
}

private fun createPolygon(): RoundedPolygon {
    val vertices = {
        val horizontal = 1 + 0.02f
        val vertical = 1f
        floatArrayOf(
            // left
            -horizontal,
            0f,
            // top left
            -horizontal/2,
            -vertical,
            // top right
            horizontal/2,
            -vertical,
            // right
            horizontal,
            0f,
            // bottom right
            horizontal/2,
            vertical,
            // bottom left
            -horizontal/2,
            vertical,
        )
    }
    val rounding = {
        val roundingCorner = 0.15f
        val sideCorner = 0.15f
        listOf(
            CornerRounding(sideCorner),
            CornerRounding(roundingCorner),
            CornerRounding(roundingCorner),
            CornerRounding(sideCorner),
            CornerRounding(roundingCorner),
            CornerRounding(roundingCorner),
        )
    }
    return RoundedPolygon(vertices = vertices(), perVertexRounding = rounding.invoke())
}

val polygon = createPolygon()

class RoundedPolygonShape(
    private val polygon: RoundedPolygon
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        var matrix = Matrix()
        val path = polygon.cubics.toPath()
        matrix.scale(size.width / 2f, size.height / 2f)
        matrix.translate(1f, 1f)
        path.transform(matrix)

        return Outline.Generic(path)
    }
}

class HexButtonShape(
    private val polygon: RoundedPolygon
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        var matrix = Matrix()
        val path = polygon.cubics.toPath()
        matrix.scale(size.height / 2f * 61.71f / 54f, size.height / 2f)
        matrix.translate(1f, 1f)
        path.transform(matrix)

        matrix = Matrix()
        val secondPath = polygon.cubics.toPath()
        matrix.scale(size.height / 2f * 61.71f / 54f, size.height / 2f)
        matrix.translate(1f, 1f)
        secondPath.transform(matrix)

        // Calculate the offset for the second hexagon
        val hexagonWidth = size.height * 61.71f / 54f
        val offsetX = size.width - hexagonWidth

        // Create a rectangular path between the two hexagons
        val rectPath = Path().apply {
            // Calculate the dimensions and position of the rectangle
            val rectLeft = hexagonWidth / 2
            val rectRight = offsetX + hexagonWidth / 2
            val rectTop = 0f
            val rectBottom = size.height

            // Add the rectangle to the path
            addRect(Rect(rectLeft, rectTop, rectRight, rectBottom), Path.Direction.Clockwise)
        }

        return Outline.Generic(Path().apply {
            op(
                path1 = path,
                path2 = rectPath,
                operation = PathOperation.Union
            )
            op(
                path1 = this,
                path2 = Path().apply {
                    addPath(path, Offset(offsetX, 0f))
                },
                operation = PathOperation.Union
            )
//            addPath(path)
//            addPath(path, Offset(offsetX, 0f))
//            addPath(rectPath)
        })
    }
}

private fun List<Cubic>.toPath(path: Path = Path(), scale: Float = 1f): Path {
    path.rewind()
    firstOrNull()?.let { first ->
        path.moveTo(first.anchor0X * scale, first.anchor0Y * scale)
    }
    for (bezier in this) {
        path.cubicTo(
            bezier.control0X * scale, bezier.control0Y * scale,
            bezier.control1X * scale, bezier.control1Y * scale,
            bezier.anchor1X * scale, bezier.anchor1Y * scale
        )
    }
    path.close()
    return path
}

@Preview
@Composable
private fun HexagonRotated_CompareWithXdDesign_Preview() {
    var x by remember { mutableStateOf(true) }

    val width: Dp = 164.xdph
    val height: Dp = 54.xdpv

    Box(Modifier.clickable { x = !x }) {

        if (x) HexButton(
            modifier = Modifier.width(width).height(height),
            onClick = {},
        )
        if (!x) Image(
            modifier = Modifier
                .width(width)
                .height(height)
                .clipToBounds(),
    contentScale = ContentScale.FillBounds,
            painter = painterResource(id = R.drawable.hex_button),
            contentDescription = ""
        )
    }
}

@Preview
@Composable
private fun OutlineHexagonButton_CompareWithXdDesign_Preview() {
    var x by remember { mutableStateOf(true) }

    val width: Dp = 343.xdph
    val height: Dp = 54.xdpv

    Box(Modifier.clickable { x = !x }) {

        if (x) OutlineHexButton(
            modifier = Modifier.width(width).height(height),
            onClick = {},
        )
        if (!x) Image(
            modifier = Modifier
                .width(width)
                .height(height)
                .clipToBounds(),
            contentScale = ContentScale.FillBounds,
            painter = painterResource(id = R.drawable.outline_wide_hex_button),
            contentDescription = ""
        )
    }
}