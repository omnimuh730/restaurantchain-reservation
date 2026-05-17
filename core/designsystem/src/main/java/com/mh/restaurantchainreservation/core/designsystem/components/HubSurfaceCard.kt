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
 * Shared surface cards for Dining (Next Up, booking rows, stats).
 * Shadow-first — no border unless [showBorder] is true.
 */
object HubSurfaceCardDefaults {
    val Shape = RoundedCornerShape(RestaurantDimensions.RadiusLg)
    val QuickActionShape = RoundedCornerShape(20.dp)

    val ShadowElevation = 6.dp
    const val ShadowAmbientAlpha = 0.18f

    val ProminentShadowElevation = 8.dp
    const val ProminentShadowAmbientAlpha = 0.18f

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
    showBorder: Boolean = false,
    onClick: (() -> Unit)? = null,
): Modifier {
    val ambient = Color.Black.copy(alpha = shadowAmbientAlpha)
    var modifier = this
        .shadow(
            elevation = shadowElevation,
            shape = shape,
            ambientColor = ambient,
            spotColor = ambient.copy(alpha = shadowAmbientAlpha * 0.9f),
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

@Composable
fun Modifier.hubSurfaceCardProminent(
    palette: RestaurantPalette,
    shape: Shape = HubSurfaceCardDefaults.Shape,
    showBorder: Boolean = false,
    onClick: (() -> Unit)? = null,
): Modifier = hubSurfaceCard(
    palette = palette,
    shape = shape,
    shadowElevation = HubSurfaceCardDefaults.ProminentShadowElevation,
    shadowAmbientAlpha = HubSurfaceCardDefaults.ProminentShadowAmbientAlpha,
    showBorder = showBorder,
    onClick = onClick,
)
