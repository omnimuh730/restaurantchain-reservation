package com.mh.restaurantchainreservation.core.designsystem.components.icons

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * QR scanner glyph: corner scan brackets and a 2×2 grid of square modules.
 */
@Composable
fun QrScannerIcon(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val s = size.minDimension / 24f
        val stroke = 2f * s
        val strokeStyle = Stroke(
            width = stroke,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
        val bracketLen = 6f * s
        val inset = 2.5f * s

        fun cornerBracket(topLeft: Boolean, topRight: Boolean, bottomLeft: Boolean, bottomRight: Boolean) {
            val path = Path()
            if (topLeft) {
                path.moveTo(inset, inset + bracketLen)
                path.lineTo(inset, inset)
                path.lineTo(inset + bracketLen, inset)
            }
            if (topRight) {
                path.moveTo(size.width - inset - bracketLen, inset)
                path.lineTo(size.width - inset, inset)
                path.lineTo(size.width - inset, inset + bracketLen)
            }
            if (bottomLeft) {
                path.moveTo(inset, size.height - inset - bracketLen)
                path.lineTo(inset, size.height - inset)
                path.lineTo(inset + bracketLen, size.height - inset)
            }
            if (bottomRight) {
                path.moveTo(size.width - inset - bracketLen, size.height - inset)
                path.lineTo(size.width - inset, size.height - inset)
                path.lineTo(size.width - inset, size.height - inset - bracketLen)
            }
            drawPath(path, color, style = strokeStyle)
        }
        cornerBracket(topLeft = true, topRight = true, bottomLeft = true, bottomRight = true)

        val module = 4.5f * s
        val gap = 1.5f * s
        val gridW = module * 2 + gap
        val gridLeft = (size.width - gridW) / 2f
        val gridTop = (size.height - gridW) / 2f
        val corner = CornerRadius(0.6f * s, 0.6f * s)

        listOf(
            Offset(gridLeft, gridTop),
            Offset(gridLeft + module + gap, gridTop),
            Offset(gridLeft, gridTop + module + gap),
            Offset(gridLeft + module + gap, gridTop + module + gap),
        ).forEach { topLeft ->
            drawRoundRect(
                color = color,
                topLeft = topLeft,
                size = Size(module, module),
                cornerRadius = corner,
                style = Fill,
            )
        }
    }
}
