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

enum class TierType { Silver, Gold, Platinum, Diamond }

private data class TierColors(
    val bg: Color,
    val ring: Color,
    val symbolMain: Color,
    val symbolAccent: Color,
)

private fun tierColors(tier: TierType): TierColors = when (tier) {
    TierType.Silver -> TierColors(
        bg = Color(0xFF94A3B8),
        ring = Color(0xFF64748B),
        symbolMain = Color(0xFFE2E8F0),
        symbolAccent = Color(0xFFE2E8F0),
    )
    TierType.Gold -> TierColors(
        bg = Color(0xFFF59E0B),
        ring = Color(0xFFD97706),
        symbolMain = Color(0xFFFEF3C7),
        symbolAccent = Color(0xFFFEF3C7),
    )
    TierType.Platinum -> TierColors(
        bg = Color(0xFF8B5CF6),
        ring = Color(0xFF7C3AED),
        symbolMain = Color(0xFFE0E7FF),
        symbolAccent = Color(0xFFC4B5FD),
    )
    TierType.Diamond -> TierColors(
        bg = Color(0xFFEC4899),
        ring = Color(0xFFDB2777),
        symbolMain = Color(0xFFFCE7F3),
        symbolAccent = Color(0xFFFBCFE8),
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
            colors = listOf(Color.White.copy(alpha = 0.55f), Color.White.copy(alpha = 0f)),
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
    val sparkle = Color(0xFFFDE68A)
    drawCircle(color = sparkle, radius = 1f * unit, center = Offset(12f * unit, 5f * unit))
    drawCircle(color = sparkle, radius = 0.7f * unit, center = Offset(9f * unit, 5.5f * unit))
    drawCircle(color = sparkle, radius = 0.7f * unit, center = Offset(15f * unit, 5.5f * unit))
}
