package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors

enum class TierType { Silver, Gold, Platinum, Diamond }

private data class TierColors(
    val bg: Color,
    val ring: Color,
    val symbolMain: Color,
    val symbolAccent: Color,
)

private fun tierColors(tier: TierType): TierColors = when (tier) {
    TierType.Silver -> TierColors(
        bg = RestaurantColors.Tier.Silver.bg,
        ring = RestaurantColors.Tier.Silver.ring,
        symbolMain = RestaurantColors.Tier.Silver.symbol,
        symbolAccent = RestaurantColors.Tier.Silver.symbol,
    )
    TierType.Gold -> TierColors(
        bg = RestaurantColors.Tier.Gold.bg,
        ring = RestaurantColors.Tier.Gold.ring,
        symbolMain = RestaurantColors.Tier.Gold.symbol,
        symbolAccent = RestaurantColors.Tier.Gold.symbol,
    )
    TierType.Platinum -> TierColors(
        bg = RestaurantColors.Tier.Platinum.bg,
        ring = RestaurantColors.Tier.Platinum.ring,
        symbolMain = RestaurantColors.Tier.Platinum.symbolMain,
        symbolAccent = RestaurantColors.Tier.Platinum.symbolAccent,
    )
    TierType.Diamond -> TierColors(
        bg = RestaurantColors.Tier.Diamond.bg,
        ring = RestaurantColors.Tier.Diamond.ring,
        symbolMain = RestaurantColors.Tier.Diamond.symbolMain,
        symbolAccent = RestaurantColors.Tier.Diamond.symbolAccent,
    )
}

@Composable
fun TierMedalIcon(
    tier: TierType,
    modifier: Modifier = Modifier,
) {
    val colors = tierColors(tier)
    Canvas(modifier = modifier) {
        val s = size.minDimension
        val unit = s / 24f
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = 10f * unit

        drawCircle(color = colors.bg, radius = radius, center = center)

        val shineBrush = Brush.radialGradient(
            colors = listOf(RestaurantColors.Base.whiteAlpha(0.55f), RestaurantColors.Base.whiteAlpha(0f)),
            center = center.copy(
                x = center.x - radius * 0.25f,
                y = center.y - radius * 0.25f,
            ),
            radius = radius * 1.05f,
        )
        drawCircle(brush = shineBrush, radius = radius, center = center)

        drawCircle(
            color = colors.ring.copy(alpha = 0.5f),
            radius = 9f * unit,
            center = center,
            style = Stroke(width = 1.5f * unit),
        )

        when (tier) {
            TierType.Silver, TierType.Gold -> drawStarSymbol(unit, colors.symbolMain)
            TierType.Platinum -> drawPlatinumSymbol(unit, colors.symbolMain, colors.symbolAccent)
            TierType.Diamond -> drawDiamondSymbol(unit, colors.symbolMain, colors.symbolAccent)
        }
    }
}

private fun DrawScope.drawStarSymbol(unit: Float, color: Color) {
    val path = Path().apply {
        moveTo(12f * unit, 6f * unit)
        lineTo(13.5f * unit, 9.5f * unit)
        lineTo(17f * unit, 11f * unit)
        lineTo(13.5f * unit, 12.5f * unit)
        lineTo(12f * unit, 16f * unit)
        lineTo(10.5f * unit, 12.5f * unit)
        lineTo(7f * unit, 11f * unit)
        lineTo(10.5f * unit, 9.5f * unit)
        close()
    }
    drawPath(path = path, color = color)
}

private fun DrawScope.drawPlatinumSymbol(unit: Float, top: Color, base: Color) {
    val triTop = Path().apply {
        moveTo(12f * unit, 7f * unit)
        lineTo(14f * unit, 11f * unit)
        lineTo(10f * unit, 11f * unit)
        close()
    }
    drawPath(path = triTop, color = top)
    val triBottom = Path().apply {
        moveTo(8f * unit, 11f * unit)
        lineTo(16f * unit, 11f * unit)
        lineTo(12f * unit, 17f * unit)
        close()
    }
    drawPath(path = triBottom, color = base)
}

private fun DrawScope.drawDiamondSymbol(unit: Float, top: Color, base: Color) {
    val triTop = Path().apply {
        moveTo(12f * unit, 6f * unit)
        lineTo(14.5f * unit, 10f * unit)
        lineTo(7.5f * unit, 10f * unit)
        close()
    }
    drawPath(path = triTop, color = top)
    val gem = Path().apply {
        moveTo(7.5f * unit, 10f * unit)
        lineTo(16.5f * unit, 10f * unit)
        lineTo(12f * unit, 17f * unit)
        close()
    }
    drawPath(path = gem, color = base)
    val sparkle = RestaurantColors.Tier.Diamond.sparkle
    drawCircle(color = sparkle, radius = 1f * unit, center = Offset(12f * unit, 5f * unit))
    drawCircle(color = sparkle, radius = 0.7f * unit, center = Offset(9f * unit, 5.5f * unit))
    drawCircle(color = sparkle, radius = 0.7f * unit, center = Offset(15f * unit, 5.5f * unit))
}
