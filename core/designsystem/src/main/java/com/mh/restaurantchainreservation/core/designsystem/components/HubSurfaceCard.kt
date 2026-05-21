package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantDimensions
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
    const val ShadowAmbientAlpha = 0.18f
    const val ShadowSpotAlpha = 0.38f

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
        modifier = modifier.border(1.dp, palette.border.copy(alpha = 0.5f), shape)
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
