package com.mh.restaurantchainreservation.core.designsystem.components.icons

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

object LucidePaths {
    val Home = listOf(
        "M3 10a2 2 0 0 1 .709-1.528l7-5.999a2 2 0 0 1 2.582 0l7 5.999A2 2 0 0 1 21 10v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z",
        "M15 21v-8a1 1 0 0 0-1-1h-4a1 1 0 0 0-1 1v8",
    )

    val Heart = listOf(
        "M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z",
    )

    val UtensilsCrossed = listOf(
        "m16 2-2.3 2.3a3 3 0 0 0 0 4.2l1.8 1.8a3 3 0 0 0 4.2 0L22 8",
        "M15 15 3.3 3.3a1 1 0 0 0 1.4 0l2.6-2.6a1 1 0 0 0 0-1.4L19 11",
        "m2.1 21.8 6.4-6.3",
        "m19 5-7 7",
    )

    val User = listOf(
        "M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2",
        "M16 7a4 4 0 1 1-8 0 4 4 0 0 1 8 0z",
    )
}

@Composable
fun LucideIcon(
    paths: List<String>,
    modifier: Modifier = Modifier,
    strokeColor: Color,
    fillColor: Color = Color.Transparent,
    strokeWidth: Float = 2f,
    contentDescription: String? = null,
) {
    val vector = remember(paths, strokeColor, fillColor, strokeWidth) {
        buildLucideVector(paths, strokeColor, fillColor, strokeWidth)
    }
    Image(
        imageVector = vector,
        contentDescription = contentDescription,
        modifier = modifier,
    )
}

private fun buildLucideVector(
    paths: List<String>,
    strokeColor: Color,
    fillColor: Color,
    strokeWidth: Float,
): ImageVector {
    val builder = ImageVector.Builder(
        name = "Lucide",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    )
    paths.forEach { path ->
        builder.addPath(
            pathData = addPathNodes(path),
            fill = if (fillColor.alpha > 0f) SolidColor(fillColor) else null,
            stroke = SolidColor(strokeColor),
            strokeLineWidth = strokeWidth,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
    }
    return builder.build()
}
