package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors

/** Premium pink palette for glass-style center modals — all derived from [RestaurantColors.Brand]. */
object PremiumPinkSheetColors {
    val Primary: Color get() = RestaurantColors.Brand.primary
    val SoftPink: Color get() = RestaurantColors.Brand.softTint
    val LightPink: Color get() = RestaurantColors.Brand.lightTint
    val Border: Color get() = RestaurantColors.Border.divider
    val ShadowPink: Color get() = RestaurantColors.Brand.shadow
    val ShadowNeutral: Color get() = RestaurantColors.Overlay.shadowNeutral
    val RadialHighlight: Color get() = RestaurantColors.Brand.radialHighlight
}

enum class CenterModalSheetSurface {
    Default,
    PremiumPink,
}

/**
 * Layered modal surface matching web spec:
 * radial highlight (top-left) + 135° linear gradient, pink border, dual soft shadow.
 */
fun Modifier.premiumPinkModalSurface(
    shape: Shape,
    pinkShadowElevation: Dp = 20.dp,
    neutralShadowElevation: Dp = 8.dp,
): Modifier = this
    .shadow(
        elevation = pinkShadowElevation,
        shape = shape,
        ambientColor = PremiumPinkSheetColors.ShadowPink,
        spotColor = PremiumPinkSheetColors.ShadowPink,
    )
    .shadow(
        elevation = neutralShadowElevation,
        shape = shape,
        ambientColor = PremiumPinkSheetColors.ShadowNeutral,
        spotColor = PremiumPinkSheetColors.ShadowNeutral,
    )
    .clip(shape)
    .border(width = 1.dp, color = PremiumPinkSheetColors.Border, shape = shape)
    .drawBehind {
        val linear = Brush.linearGradient(
            0f to RestaurantColors.Base.white,
            0.45f to PremiumPinkSheetColors.LightPink,
            1f to PremiumPinkSheetColors.SoftPink,
            start = Offset.Zero,
            end = Offset(size.width, size.height),
        )
        drawRect(brush = linear)

        val radial = Brush.radialGradient(
            0f to PremiumPinkSheetColors.RadialHighlight,
            0.4f to Color.Transparent,
            center = Offset.Zero,
            radius = size.minDimension * 0.72f,
        )
        drawRect(brush = radial)
    }
