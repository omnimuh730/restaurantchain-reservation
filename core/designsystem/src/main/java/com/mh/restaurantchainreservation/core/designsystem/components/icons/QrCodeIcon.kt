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
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun QrCodeIcon(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val s = size.minDimension / 24f
        val strokeWidth = 2f * s
        val style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
        val cornerRadius = CornerRadius(1f * s, 1f * s)

        drawRoundRect(
            color = color,
            topLeft = Offset(3f * s, 3f * s),
            size = Size(7f * s, 7f * s),
            cornerRadius = cornerRadius,
            style = style,
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(14f * s, 3f * s),
            size = Size(7f * s, 7f * s),
            cornerRadius = cornerRadius,
            style = style,
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(3f * s, 14f * s),
            size = Size(7f * s, 7f * s),
            cornerRadius = cornerRadius,
            style = style,
        )
        drawRect(
            color = color,
            topLeft = Offset(14f * s, 14f * s),
            size = Size(3f * s, 3f * s),
            style = style,
        )

        val cornerPaths = Path().apply {
            moveTo(21f * s, 14f * s)
            lineTo(18f * s, 14f * s)
            lineTo(18f * s, 17f * s)

            moveTo(18f * s, 21f * s)
            lineTo(18f * s, 18f * s)
            lineTo(21f * s, 18f * s)
        }
        drawPath(path = cornerPaths, color = color, style = style)
    }
}
