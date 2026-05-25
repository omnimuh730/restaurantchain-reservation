package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
/** Top Picks by Food Type / Popular menu rail tiles (Discover home + restaurant detail). */
object DiscoverMenuRailDefaults {
    val TileSize = 112.dp
    val TileCornerRadius = 16.dp
    val SeeAllCardShape = RoundedCornerShape(TileCornerRadius)
    val SeeAllThumbShape = RoundedCornerShape(8.dp)
    /** Base “See all” label size on 112dp menu/food-type tiles (scaled slightly with card size). */
    const val SeeAllFooterFontSp = 11f
    const val SeeAllFooterFontScale = 1f
}

/**
 * Square image tile with gradient caption on photo + title below (Discover food-type rail style).
 */
@Composable
fun DiscoverMenuTile(
    imageUrl: String,
    title: String,
    imageCaption: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = title,
    showTitle: Boolean = true,
    showImageCaption: Boolean = true,
) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(DiscoverMenuRailDefaults.TileCornerRadius)
    PressableScale(
        onClick = onClick,
        modifier = modifier.width(DiscoverMenuRailDefaults.TileSize),
    ) {
        val imageTile = @Composable {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(DiscoverMenuRailDefaults.TileSize)
                    .hubSurfaceShadow(shape = shape)
                    .clip(shape)
                    .background(palette.cardSurface),
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                if (showImageCaption) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colorStops = arrayOf(
                                        0f to Color.Transparent,
                                        0.42f to Color.Transparent,
                                        0.68f to RestaurantColors.Overlay.imageGradientMid,
                                        1f to RestaurantColors.Overlay.imageGradientEnd,
                                    ),
                                ),
                            ),
                    )
                    Text(
                        text = imageCaption,
                        color = RestaurantColors.Overlay.imageCaption,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        if (showTitle) {
            Column {
                imageTile()
                Text(
                    text = title,
                    color = palette.foreground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        } else {
            imageTile()
        }
    }
}

/** Stacked-thumbnail “See all” card matching Discover food-type / restaurant rails. */
@Composable
fun DiscoverMenuSeeAllCard(
    previewImages: List<Any>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val images = rememberSeeAllPreviewImages(previewImages)
    val cardSize = DiscoverMenuRailDefaults.TileSize
    PressableScale(
        onClick = onClick,
        modifier = modifier
            .width(cardSize)
            .height(cardSize)
            .hubSurfaceCard(
                palette = palette,
                shape = DiscoverMenuRailDefaults.SeeAllCardShape,
            ),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SeeAllSlideThumbnailStack(
                images = images,
                thumbCornerShape = DiscoverMenuRailDefaults.SeeAllThumbShape,
                thumbBorderWidth = seeAllThumbBorderWidth(cardSize, cardSize),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 2.dp),
            )
            DiscoverMenuSeeAllFooter(
                cardWidth = cardSize,
                cardHeight = cardSize,
                fontScale = DiscoverMenuRailDefaults.SeeAllFooterFontScale,
            )
        }
    }
}

@Composable
private fun DiscoverMenuSeeAllFooter(
    cardWidth: Dp,
    cardHeight: Dp,
    fontScale: Float,
) {
    val palette = LocalRestaurantPalette.current
    val reference = DiscoverMenuRailDefaults.TileSize.value.coerceAtLeast(1f)
    val scale = (minOf(cardWidth.value, cardHeight.value) / reference).coerceIn(0.72f, 1.12f)
    Text(
        text = "See all",
        color = palette.foreground,
        fontSize = (DiscoverMenuRailDefaults.SeeAllFooterFontSp * scale * fontScale).sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .offset(y = (2f * scale).dp)
            .padding(bottom = (10f * scale).dp),
    )
}

@Composable
private fun rememberSeeAllPreviewImages(previewImages: List<Any>): List<Any> =
    remember(previewImages) {
        when {
            previewImages.size >= 3 -> previewImages.take(3)
            previewImages.size == 2 -> previewImages + previewImages.last()
            previewImages.size == 1 -> List(3) { previewImages.first() }
            else -> List(3) {
                "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=200&h=200&fit=crop"
            }
        }
    }

private fun seeAllThumbBorderWidth(cardWidth: Dp, cardHeight: Dp): Dp {
    val reference = DiscoverMenuRailDefaults.TileSize.value.coerceAtLeast(1f)
    val scale = (minOf(cardWidth.value, cardHeight.value) / reference).coerceIn(0.55f, 1f)
    return (3f * scale).dp
}

@Composable
private fun PressableScale(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.965f else 1f,
        animationSpec = spring(dampingRatio = 0.62f, stiffness = 420f),
        label = "discover-menu-press-scale",
    )
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            ),
        content = content,
    )
}

private data class ThumbnailLayer(
    val layerName: String,
    val topPercent: Float,
    val leftPercent: Float,
    val widthPercent: Float,
    val heightPercent: Float,
    val zIndex: Float,
)

private val SeeAllThumbnailBack = ThumbnailLayer(
    layerName = "Back",
    topPercent = 15f,
    leftPercent = 22f,
    widthPercent = 42f,
    heightPercent = 40.0f,
    zIndex = 1f,
)
private val SeeAllThumbnailMiddle = ThumbnailLayer(
    layerName = "Middle",
    topPercent = 22f,
    leftPercent = 39f,
    widthPercent = 44f,
    heightPercent = 42.0f,
    zIndex = 2f,
)
private val SeeAllThumbnailFront = ThumbnailLayer(
    layerName = "Front",
    topPercent = 32f,
    leftPercent = 17f,
    widthPercent = 46f,
    heightPercent = 44.0f,
    zIndex = 3f,
)
private val SeeAllThumbnailSlideStart = ThumbnailLayer(
    layerName = "Slide start",
    topPercent = 30.0f,
    leftPercent = 33f,
    widthPercent = 34f,
    heightPercent = 34f,
    zIndex = 0f,
)
private val SeeAllStackLayerRotations = floatArrayOf(-5.5f, 7.5f, -12f)

@Composable
private fun SeeAllSlideThumbnailStack(
    images: List<Any>,
    thumbCornerShape: RoundedCornerShape,
    thumbBorderWidth: Dp,
    modifier: Modifier = Modifier,
) {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }
    val t by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(880, easing = FastOutSlowInEasing),
        label = "see-all-slide",
    )
    val pBack = staggerProgress(t, 0f, 0.36f)
    val pMid = staggerProgress(t, 0.18f, 0.62f)
    val pFront = staggerProgress(t, 0.38f, 1f)

    BoxWithConstraints(modifier = modifier) {
        val w = maxWidth
        val h = maxHeight
        val gBack = SeeAllThumbnailBack.lerpedFrom(SeeAllThumbnailSlideStart, pBack)
        val gMid = SeeAllThumbnailMiddle.lerpedFrom(SeeAllThumbnailSlideStart, pMid)
        val gFront = SeeAllThumbnailFront.lerpedFrom(SeeAllThumbnailSlideStart, pFront)

        val wBack = w * (gBack.widthPercent / 100f)
        val wMid = w * (gMid.widthPercent / 100f)
        val wFront = w * (gFront.widthPercent / 100f)

        AnimatedSeeAllThumbnail(
            imageModel = images[0],
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(w * (gBack.leftPercent / 100f), h * (gBack.topPercent / 100f))
                .zIndex(gBack.zIndex),
            width = wBack,
            height = wBack,
            cornerShape = thumbCornerShape,
            borderWidth = thumbBorderWidth,
            slideProgress = pBack,
            endRotationDegrees = SeeAllStackLayerRotations[0],
        )
        AnimatedSeeAllThumbnail(
            imageModel = images[1],
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(w * (gMid.leftPercent / 100f), h * (gMid.topPercent / 100f))
                .zIndex(gMid.zIndex),
            width = wMid,
            height = wMid,
            cornerShape = thumbCornerShape,
            borderWidth = thumbBorderWidth,
            slideProgress = pMid,
            endRotationDegrees = SeeAllStackLayerRotations[1],
        )
        AnimatedSeeAllThumbnail(
            imageModel = images[2],
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(w * (gFront.leftPercent / 100f), h * (gFront.topPercent / 100f))
                .zIndex(gFront.zIndex),
            width = wFront,
            height = wFront,
            cornerShape = thumbCornerShape,
            borderWidth = thumbBorderWidth,
            slideProgress = pFront,
            endRotationDegrees = SeeAllStackLayerRotations[2],
        )
    }
}

@Composable
private fun AnimatedSeeAllThumbnail(
    imageModel: Any,
    modifier: Modifier,
    width: Dp,
    height: Dp,
    cornerShape: RoundedCornerShape,
    borderWidth: Dp,
    slideProgress: Float,
    endRotationDegrees: Float,
    transformOrigin: TransformOrigin = TransformOrigin.Center,
) {
    val p = slideProgress.coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .graphicsLayer {
                this.transformOrigin = transformOrigin
                rotationZ = lerp(0f, endRotationDegrees, p)
                scaleX = lerp(0.82f, 1f, p)
                scaleY = lerp(0.82f, 1f, p)
                alpha = lerp(0.5f, 1f, p).coerceIn(0f, 1f)
            }
            .shadow(
                elevation = 6.dp,
                shape = cornerShape,
                clip = false,
                ambientColor = RestaurantColors.Shadow.cardAmbient,
                spotColor = RestaurantColors.Shadow.cardSpot,
            )
            .clip(cornerShape)
            .border(borderWidth, RestaurantColors.Base.white, cornerShape)
            .background(RestaurantColors.Neutral.imagePlaceholder),
    ) {
        AsyncImage(
            model = imageModel,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

private fun staggerProgress(t: Float, start: Float, end: Float): Float = when {
    t <= start -> 0f
    t >= end -> 1f
    else -> ((t - start) / (end - start)).coerceIn(0f, 1f)
}

private fun ThumbnailLayer.lerpedFrom(start: ThumbnailLayer, p: Float): ThumbnailLayer {
    val t = p.coerceIn(0f, 1f)
    return ThumbnailLayer(
        layerName = layerName,
        topPercent = lerp(start.topPercent, topPercent, t),
        leftPercent = lerp(start.leftPercent, leftPercent, t),
        widthPercent = lerp(start.widthPercent, widthPercent, t),
        heightPercent = heightPercent,
        zIndex = zIndex,
    )
}
