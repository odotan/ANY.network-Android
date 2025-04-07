package com.anynetwork.app.ui.components.hexagon

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Cubic
import androidx.graphics.shapes.RoundedPolygon
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.anynetwork.app.R
import com.anynetwork.app.model.Contact
import com.anynetwork.app.ui.components.text.AutoSizeText
import com.anynetwork.app.ui.screens.home.GridItem
import com.anynetwork.app.ui.utils.fdph
import com.anynetwork.app.ui.utils.fdpv
import com.anynetwork.app.ui.utils.fsp

class RemovableStrategy(val onRemove: () -> Unit)

sealed class HexagonContentStyle(@Stable val id: Int)

@Immutable
open class TransparentHexagonContentStyle(id: Int): HexagonContentStyle(id)

@Immutable
open class NontransparentHexagonContentStyle(
    id: Int,
    val background: Background,
    val isDraggable: Boolean,
    val isHoverable: Boolean,
    val removableStrategy: RemovableStrategy?,
    val badgeOverlay: BadgeOverlay? = null
): HexagonContentStyle(id) {
    @Immutable sealed class Background {
        @Immutable data class SingleColor(val value: Color): Background()
        @Immutable data class Gradient(
            val colors: List<Color>,
            val startOffset: Offset = Offset(0.0f, 0.0f),
            val endOffset: Offset = Offset(100.0f, 0.0f)
        ) : Background()
    }

    @Immutable
    open class BadgeOverlay(
        val badge: GridItem.Badge,
        val gridColumns: Int,
        val gridScaling: Float,
        val onClick: () -> Unit) {
        class FavoriteBadge(
            badge: GridItem.Badge,
            gridColumns: Int,
            gridScaling: Float,
            onClick: () -> Unit
        ): BadgeOverlay(badge, gridColumns, gridScaling, onClick)
        class EmailBadge(
            badge: GridItem.Badge,
            gridColumns: Int,
            gridScaling: Float,
            onClick: () -> Unit
        ): BadgeOverlay(badge, gridColumns, gridScaling, onClick)
        class PhoneBadge(
            badge: GridItem.Badge,
            gridColumns: Int,
            gridScaling: Float,
            onClick: () -> Unit
        ): BadgeOverlay(badge, gridColumns, gridScaling, onClick)
    }
}

@Immutable
class EmptyHexagonContentStyle(
    id: Int,
    background: Background = Background.SingleColor(Color(0xFF6E4CD4)),
    isDraggable: Boolean = false,
    isHoverable: Boolean = true,
    removableStrategy: RemovableStrategy? = null,
    val overlay: @Composable (BoxScope.() -> Unit) = {},
    val onClick: ((Offset) -> Unit) = {}
): NontransparentHexagonContentStyle(
    id = id,
    background = background,
    isDraggable = isDraggable,
    isHoverable = isHoverable,
    removableStrategy = removableStrategy
)

@Immutable
class TrashCanHexagonContentStyle(
    id: Int,
    background: Background = Background.SingleColor(Color(0xFF6E4CD4)),
): NontransparentHexagonContentStyle(
    id = id,
    background = background,
    isDraggable = false,
    isHoverable = true,
    removableStrategy = null
)

@Immutable
class PopupHexagonContentStyle(
    id: Int,
    background: Background = Background.SingleColor(Color(0xFF6E4CD4)),
    val message: String,
    val options: List<Option>
): NontransparentHexagonContentStyle(
    id = id,
    background = background,
    isDraggable = false,
    isHoverable = false,
    removableStrategy = null
) {
    data class Option(val title: String, val message: String, val onClick: () -> Unit)
}

@Immutable
class ContactContentStyle(
    id: Int,
    background: Background,
    isDraggable: Boolean = true,
    isHoverable: Boolean = true,
    removableStrategy: RemovableStrategy? = null,
    val onClick: ((Offset) -> Unit) = { _ -> },
    val onLongClick: (() -> Unit) = {},
    val isShakable: Boolean = false,
    val gridColumns: Int,
    val gridScaling: Float,
    val contact: Contact,
    val badge: GridItem.Badge? = null
): NontransparentHexagonContentStyle(
    id = id,
    background = background,
    isDraggable = isDraggable,
    isHoverable = isHoverable,
    removableStrategy = removableStrategy
)

@Immutable
class CustomHexagonContentStyle(
    id: Int,
    background: Background = Background.SingleColor(Color(0xFF6E4CD4)),
    isDraggable: Boolean = false,
    isHoverable: Boolean = true,
    removableStrategy: RemovableStrategy? = null,
    val content: @Composable (BoxScope.(scale: Float) -> Unit),
    val onClick: ((LayoutCoordinates) -> Unit) = { _ -> },
    val onLongClick: (() -> Unit) = {},
    val overlay: @Composable (BoxScope.() -> Unit)? = null,
    val isShakable: Boolean = false,
): NontransparentHexagonContentStyle(
    id = id,
    background = background,
    isDraggable = isDraggable,
    isHoverable = isHoverable,
    removableStrategy = removableStrategy
)

@Immutable
class IconHexagonContentStyle(
    id: Int,
    background: Background = Background.SingleColor(Color(0xFF6E4CD4)),
    isDraggable: Boolean = false,
    isHoverable: Boolean = true,
    removableStrategy: RemovableStrategy? = null,
    val modifier: Modifier = Modifier.size(24.dp),
    val alpha: Float = 1f,
    val contentDescription: String = "",
    val tintColor: Color? = null,
    val image: Image,
    val overlay: @Composable (BoxScope.() -> Unit)? = null,
    val onClick: ((Offset) -> Unit) = {},
    val isShakable: Boolean = false,
): NontransparentHexagonContentStyle(
    id = id,
    background = background,
    isDraggable = isDraggable,
    isHoverable = isHoverable,
    removableStrategy = removableStrategy
) {
    @Immutable
    sealed class Image {
        data class VectorResource(val id: Int): Image()
    }
}

@Immutable
class ImageHexagonContentStyle(
    id: Int,
    background: Background = Background.SingleColor(Color(0xFF6E4CD4)),
    isDraggable: Boolean = true,
    isHoverable: Boolean = true,
    removableStrategy: RemovableStrategy? = null,
    val contentDescription: String = "",
    val image: Image,
    val alpha: Float = 1f,
    val onClick: ((Offset) -> Unit) = {},
    val onLongClick: (() -> Unit) = {},
    val isShakable: Boolean = false,
    badgeOverlay: BadgeOverlay? = null
): NontransparentHexagonContentStyle(
    id = id,
    background = background,
    isDraggable = isDraggable,
    isHoverable = isHoverable,
    removableStrategy = removableStrategy,
    badgeOverlay = badgeOverlay
) {
    @Immutable open class Image(val size: coil.size.Size? = null, val fractionOfParentSize: Float = 1f) {
        class VectorResource(val id: Int, size: coil.size.Size? = null, fractionOfParentSize: Float = 1f): Image(size, fractionOfParentSize)
        class FromNetwork(val url: String, size: coil.size.Size? = null, fractionOfParentSize: Float = 1f): Image(size, fractionOfParentSize)
        class Resource(val id: Int, size: coil.size.Size? = null, fractionOfParentSize: Float = 1f): Image(size, fractionOfParentSize)
        class FromUri(val uri: String, size: coil.size.Size? = null, fractionOfParentSize: Float = 1f): Image(size, fractionOfParentSize)
    }
}

@Immutable
class AutoresizeTextContentStyle(
    id: Int,
    background: Background,
    isDraggable: Boolean = true,
    isHoverable: Boolean = true,
    removableStrategy: RemovableStrategy? = null,
    val text: String,
    val maxTextSize: TextUnit,
    val onClick: (Offset) -> Unit = {},
    badgeOverlay: BadgeOverlay? = null
): NontransparentHexagonContentStyle(
    id = id,
    background = background,
    isDraggable = isDraggable,
    isHoverable = isHoverable,
    removableStrategy = removableStrategy,
    badgeOverlay = badgeOverlay
)

fun Modifier.cellBackground(background: NontransparentHexagonContentStyle.Background): Modifier {
    return when (background) {
        is NontransparentHexagonContentStyle.Background.SingleColor -> this.background(background.value)
        is NontransparentHexagonContentStyle.Background.Gradient -> this.background(
            Brush.linearGradient(background.colors, background.startOffset, background.endOffset)
        )
    }
}

@Composable
fun RoundedHexagon(
    modifier: Modifier = Modifier,
    onClick: ((Offset) -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
    onDoubleTap: (() -> Unit)? = null,
    contentStyle: HexagonContentStyle,
    hovered: Boolean = false,
    showContent: Boolean = true,
    scale: Float = 1f
) {
    val montserratFontFamily = remember { FontFamily(Font(R.font.montserrat_semibold)) } // Remember at a higher level

    Box(
        modifier = if (onClick != null) modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onClick.invoke(it)
                    },
                    onLongPress = null,
                    onDoubleTap = {
                        onDoubleTap?.invoke()
                    }
                )
            } else modifier,
        content = {
            when (contentStyle) {
                is NontransparentHexagonContentStyle -> {
                    val background by rememberUpdatedState(contentStyle.background)

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .cellBackground(background),
                        contentAlignment = Alignment.Center
                    ) {
                        when (contentStyle) {

                            is AutoresizeTextContentStyle -> {
                                val textState = remember(contentStyle.text) { contentStyle.text }
                                val maxTextSize = remember(contentStyle.maxTextSize) { contentStyle.maxTextSize * scale }
                                val textColor = remember { Color(0xFFAFAEB8) } // Example

                                AutoSizeText(
                                    modifier = Modifier.fillMaxWidth(0.9f),
                                    text = textState,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = textColor,
                                    alignment = Alignment.Center,
                                    maxTextSize = maxTextSize,
                                    style = TextStyle(
                                        fontFamily = montserratFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                )
//                                Text(
//                                    modifier = Modifier.fillMaxWidth(0.9f),
//                                    text = textState,
//                                    maxLines = 2,
//                                    overflow = TextOverflow.Ellipsis,
//                                    color = textColor,
//                                    style = TextStyle(
//                                        fontFamily = montserratFontFamily,
//                                        fontWeight = FontWeight.SemiBold,
//                                    )
//                                )
                            }
                            is TrashCanHexagonContentStyle -> {
                                val alpha by animateFloatAsState(
                                    targetValue = when {
                                        showContent && hovered -> 1f
                                        showContent -> 0.4f
                                        else -> 0f
                                    },
                                    animationSpec = tween(300)
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Red.copy(alpha = alpha)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        modifier = Modifier.fillMaxSize(fraction = .7f),
                                        painter = rememberAsyncImagePainter(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(R.drawable.ic_delete)
//                                                .size(coil.size.Size.ORIGINAL)
                                                .scale(coil.size.Scale.FIT)
                                                .build()
                                        ),
                                        contentDescription = "delete",
                                        colorFilter = ColorFilter.tint(Color.White.copy(alpha = alpha)),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            }

                            is CustomHexagonContentStyle -> contentStyle.content.invoke(this, scale)

                            is ImageHexagonContentStyle -> {
                                val contentStyleImage by rememberUpdatedState(contentStyle.image)
                                if (contentStyleImage is ImageHexagonContentStyle.Image.VectorResource) {
                                    val painter = rememberAsyncImagePainter(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data((contentStyleImage as ImageHexagonContentStyle.Image.VectorResource).id)
                                            .apply {
                                                if (contentStyleImage.size != null)
                                                    size(contentStyleImage.size!!.height, contentStyleImage.size!!.width)
                                                else size(coil.size.Size.ORIGINAL)
                                            }
                                            .build()
                                    )
                                    Image(
                                        modifier = Modifier
                                            .fillMaxSize(contentStyleImage.fractionOfParentSize)
                                            .alpha(contentStyle.alpha),
                                        painter = painter,
                                        contentScale = ContentScale.Crop,
                                        contentDescription = contentStyle.contentDescription,
                                    )
                                } else if (contentStyleImage is ImageHexagonContentStyle.Image.Resource) {
                                    val painter = rememberAsyncImagePainter(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data((contentStyleImage as ImageHexagonContentStyle.Image.Resource).id)
                                            .size(coil.size.Size.ORIGINAL) // Load the image at its original resolution
                                            .build()
                                    )
                                    Image(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .alpha(contentStyle.alpha),
                                        painter = painter,
                                        contentScale = ContentScale.Crop,
                                        contentDescription = contentStyle.contentDescription,
                                    )
                                } else if (contentStyleImage is ImageHexagonContentStyle.Image.FromNetwork) {
                                    val painter = rememberAsyncImagePainter(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data((contentStyleImage as ImageHexagonContentStyle.Image.FromNetwork).url)
                                            .diskCachePolicy(CachePolicy.ENABLED)
                                            .memoryCachePolicy(CachePolicy.ENABLED)
                                            .size(coil.size.Size.ORIGINAL) // Load the image at its original resolution
                                            .build()
                                    )
                                    Image(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .alpha(contentStyle.alpha),
                                        painter = painter,
                                        contentDescription = contentStyle.contentDescription,
                                        contentScale = ContentScale.Crop
                                    )
                                } else if (contentStyleImage is ImageHexagonContentStyle.Image.FromUri) {
                                    val painter = rememberAsyncImagePainter(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data((contentStyleImage as ImageHexagonContentStyle.Image.FromUri).uri)
                                            .apply {
                                                if (contentStyleImage.size != null)
                                                    size(contentStyleImage.size!!.height, contentStyleImage.size!!.width)
                                                else size(coil.size.Size.ORIGINAL)
                                            }
                                            .build(),
                                    )
                                    Image(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .alpha(contentStyle.alpha),
                                        painter = painter,
                                        contentScale = ContentScale.Crop,
                                        contentDescription = contentStyle.contentDescription,
                                    )
                                }
                            }

                            is IconHexagonContentStyle -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .align(Alignment.Center),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (contentStyle.image is IconHexagonContentStyle.Image.VectorResource) {
                                        Image(
                                            modifier = contentStyle.modifier
                                                .alpha(contentStyle.alpha),
                                            painter = rememberAsyncImagePainter(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(contentStyle.image.id)
                                                    .size(coil.size.Size.ORIGINAL) // Load the image at its original resolution
                                                    .build(),
                                            ),
                                            contentDescription = contentStyle.contentDescription,
                                            colorFilter = when {
                                                contentStyle.tintColor != null -> ColorFilter.tint(contentStyle.tintColor)
                                                else -> null
                                            }
                                        )
                                    }
                                }
                            }

                            is PopupHexagonContentStyle -> {
                                Text(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 104.8.fdpv)
                                        .padding(horizontal = 54.fdph),
                                    text = contentStyle.message,
                                    fontSize = 24.fsp,
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(
                                        fontFamily = montserratFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    ),
                                )

                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 109.26.fdpv)
                                        .height(84.fdpv)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(top = 9.fdpv)
                                            .height(48.fdpv)
                                            .clickable { contentStyle.options[0].onClick.invoke() },
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            modifier = Modifier
                                                .weight(1f),
                                            text = contentStyle.options[0].title,
                                            fontSize = 24.fsp,
                                            style = TextStyle(
                                                fontFamily = montserratFontFamily,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                            ),
                                        )

                                        Text(
                                            modifier = Modifier
                                                .weight(1f),
                                            text = contentStyle.options[0].message,
                                            fontSize = 14.fsp,
                                            style = TextStyle(
                                                fontFamily = montserratFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            ),
                                        )
                                    }

                                    Spacer(modifier = Modifier
                                        .fillMaxHeight()
                                        .width(1.fdph)
                                        .background(Color.White.copy(alpha = 0.13f))
                                    )

                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(top = 9.fdpv)
                                            .height(48.fdpv)
                                            .clickable { contentStyle.options[1].onClick.invoke() },
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        Text(
                                            modifier = Modifier
                                                .weight(1f),
                                            text = contentStyle.options[1].title,
                                            fontSize = 24.fsp,
                                            style = TextStyle(
                                                fontFamily = montserratFontFamily,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                            ),
                                        )

                                        Text(
                                            modifier = Modifier
                                                .weight(1f),
                                            text = contentStyle.options[1].message,
                                            fontSize = 14.fsp,
                                            style = TextStyle(
                                                fontFamily = montserratFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            ),
                                        )
                                    }
                                }
                            }

                            else -> {

                            }
                        }

                    }
                }
                else -> null
            }
            if (hovered) {
                if (contentStyle !is TrashCanHexagonContentStyle) {
                    Spacer(modifier = Modifier.fillMaxSize().drawBehind {
                        drawRect(
                            color = Color.Cyan.copy(alpha = 0.25f),
                        )
                    })
                }
            }
        }
    )
}

@Composable
fun DeleteButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.White)
            .clickable { onClick.invoke() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(9f / 24)
                .fillMaxHeight(2f / 24)
                .background(
                    color = Color.Black,
                    shape = RoundedCornerShape(4.dp)
                )
        )
    }
}

fun createPolygon(): RoundedPolygon {
    val vertices = {
        val cornerX = 1f
        val cornerY = 1f + 0.025f
        val topBotX = 0f
        val topBotY = 1f + 0.025f
        floatArrayOf(
            // top left
            -cornerX,
            -cornerY/2,
            // top
            topBotX,
            -topBotY,
            // top right
            cornerX,
            -cornerY/2,
            // bottom right
            cornerX,
            cornerY/2,
            // bottom
            topBotX,
            topBotY - 0.002f,
            // bottom left
            -cornerX,
            cornerY/2,
        )
    }
    val rounding = {
        val roundingCorner = 0.15f
        val topCorner = 0.2f
        listOf(
            CornerRounding(roundingCorner),
            CornerRounding(topCorner),
            CornerRounding(roundingCorner),
            CornerRounding(roundingCorner),
            CornerRounding(topCorner),
            CornerRounding(roundingCorner),
        )
    }
    return RoundedPolygon(vertices = vertices(), perVertexRounding = rounding.invoke())
}

val polygon = createPolygon()

class RoundedPolygonShape(
    private val polygon: RoundedPolygon
) : Shape {
    private val matrix = Matrix()
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = polygon.cubics.toPath()
        matrix.scale(size.width / 2f, size.height / 2f)
        matrix.translate(1f, 1f)
        path.transform(matrix)
        return Outline.Generic(path)
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