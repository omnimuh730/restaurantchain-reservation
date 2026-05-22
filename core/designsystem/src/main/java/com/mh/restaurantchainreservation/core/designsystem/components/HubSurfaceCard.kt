package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantDimensions
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantPalette

/**
 * Profile hub card shell (daily reward, refer, top up / gift tiles) shared with Dining.
 * Shadow-first — no border unless [showBorder] is true.
 */
object HubSurfaceCardDefaults {
    val Shape = RoundedCornerShape(RestaurantDimensions.RadiusLg)
    val QuickActionShape = RoundedCornerShape(20.dp)

    /** Vertical gap between stacked hub cards (profile [Stagger], dining hero / lists). */
    val SectionSpacing = 16.dp

    /** Horizontal gap between cards in a row (quick actions, stats). */
    val RowCardSpacing = 12.dp

    /** Matches Discover “Explore more” rail card (ambient + spot). */
    val ShadowElevation = 12.dp
    val ShadowAmbientAlpha: Float get() = RestaurantColors.Shadow.HubAmbientAlpha
    val ShadowSpotAlpha: Float get() = RestaurantColors.Shadow.HubSpotAlpha

    /** Primary inner padding for hero / summary cards (Next Up, stats). */
    val ContentPadding = 20.dp

    /** Denser list rows (upcoming booking cards). */
    val CompactPadding = 14.dp
}

@Composable
fun Modifier.hubSurfaceCard(
    palette: RestaurantPalette,
    shape: Shape = HubSurfaceCardDefaults.Shape,
    shadowElevation: Dp = HubSurfaceCardDefaults.ShadowElevation,
    shadowAmbientAlpha: Float = HubSurfaceCardDefaults.ShadowAmbientAlpha,
    shadowSpotAlpha: Float = HubSurfaceCardDefaults.ShadowSpotAlpha,
    showBorder: Boolean = false,
    onClick: (() -> Unit)? = null,
): Modifier {
    var modifier = this
        .hubSurfaceShadow(
            shape = shape,
            elevation = shadowElevation,
            ambientAlpha = shadowAmbientAlpha,
            spotAlpha = shadowSpotAlpha,
        )
        .clip(shape)
    if (showBorder) {
        modifier = modifier.border(1.dp, palette.border, shape)
    }
    modifier = modifier.background(palette.cardSurface)
    if (onClick != null) {
        modifier = modifier.clickable(onClick = onClick)
    }
    return modifier
}

/** Profile hub / Discover rail card drop shadow (ambient + spot, matches Explore more). */
fun Modifier.hubSurfaceShadow(
    shape: Shape,
    elevation: Dp = HubSurfaceCardDefaults.ShadowElevation,
    ambientAlpha: Float = HubSurfaceCardDefaults.ShadowAmbientAlpha,
    spotAlpha: Float = HubSurfaceCardDefaults.ShadowSpotAlpha,
): Modifier = shadow(
    elevation = elevation,
    shape = shape,
    clip = false,
    ambientColor = Color.Black.copy(alpha = ambientAlpha),
    spotColor = Color.Black.copy(alpha = spotAlpha),
)

/** 1px line along the top edge (e.g. bottom nav separator). */
fun Modifier.surfaceTopBorder(
    color: Color,
    width: Dp = RestaurantColors.Divider.ThicknessDp.dp,
): Modifier = drawWithContent {
    drawContent()
    val strokePx = width.toPx()
    if (strokePx > 0f) {
        drawLine(
            color = color,
            start = Offset(0f, strokePx * 0.5f),
            end = Offset(size.width, strokePx * 0.5f),
            strokeWidth = strokePx,
        )
    }
}

/**
 * Soft upward shadow for fixed footers (bottom nav) — same black ambient/spot feel as
 * [hubSurfaceShadow], drawn above the surface. Pairs with [surfaceTopBorder].
 */
fun Modifier.surfaceTopEdgeShadow(
    height: Dp = 8.dp,
    ambientAlpha: Float = HubSurfaceCardDefaults.ShadowAmbientAlpha * 0.5f,
    spotAlpha: Float = HubSurfaceCardDefaults.ShadowSpotAlpha * 0.22f,
): Modifier = drawBehind {
    val shadowPx = height.toPx()
    if (shadowPx <= 0f) return@drawBehind
    val blend = Brush.verticalGradient(
        0f to Color.Black.copy(alpha = spotAlpha.coerceIn(0f, 1f)),
        0.45f to Color.Black.copy(alpha = ambientAlpha.coerceIn(0f, 1f)),
        1f to Color.Transparent,
    )
    drawRect(
        brush = blend,
        topLeft = Offset.Zero,
        size = Size(size.width, shadowPx),
    )
}
