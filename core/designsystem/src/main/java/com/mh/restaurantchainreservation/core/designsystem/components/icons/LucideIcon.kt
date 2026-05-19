package com.mh.restaurantchainreservation.core.designsystem.components.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** 32×32 stroke icons for the bottom navigation bar (Airbnb-style). */
object BottomNavIconPaths {
    val DiscoverSearch = listOf(
        "m24.0002 12.6668c0 6.2593-5.0741 11.3334-11.3334 11.3334-6.2592 0-11.3333-5.0741-11.3333-11.3334 0-6.2592 5.0741-11.3333 11.3333-11.3333 6.2593 0 11.3334 5.0741 11.3334 11.3333z",
        "m20.666 20.666l10 10",
    )

    val WishlistHeart = listOf(
        "m15.9998 28.6668c7.1667-4.8847 14.3334-10.8844 14.3334-18.1088 0-1.84951-.6993-3.69794-2.0988-5.10877-1.3996-1.4098-3.2332-2.11573-5.0679-2.11573-1.8336 0-3.6683.70593-5.0668 2.11573l-2.0999 2.11677-2.0988-2.11677c-1.3995-1.4098-3.2332-2.11573-5.06783-2.11573-1.83364 0-3.66831.70593-5.06683 2.11573-1.39955 1.41083-2.09984 3.25926-2.09984 5.10877 0 7.2244 7.16667 13.2241 14.3333 18.1088z",
    )

    val ProfileInCircle = listOf(
        "M30 16a14 14 0 1 1-28 0 14 14 0 0 1 28 0",
        "m26.46 25.62c-1.58-2.81-4.26-4.9-7.46-5.73v-.72c1.79-1.04 3-2.96 3-5.17 0-3.31-2.69-6-6-6s-6 2.69-6 6c0 2.22 1.21 4.14 3 5.17v.72c-3.16.82-5.83 2.87-7.42 5.64",
    )

    const val ViewportSize = 32f
}

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

    /** Speaker + X (mute), 24×24 Lucide-style. */
    val VolumeX = listOf(
        "M11 5L6 9H4a2 2 0 0 0-2 2v2a2 2 0 0 0 2 2h2l5 4V5z",
        "M16 9l6 6",
        "M22 9l-6 6",
    )

    /** Speaker + one wave. */
    val Volume1 = listOf(
        "M11 5L6 9H4a2 2 0 0 0-2 2v2a2 2 0 0 0 2 2h2l5 4V5z",
        "M15.54 8.46a5 5 0 0 1 0 7.07",
    )

    /** Speaker + two waves. */
    val Volume2 = listOf(
        "M11 5L6 9H4a2 2 0 0 0-2 2v2a2 2 0 0 0 2 2h2l5 4V5z",
        "M15.54 8.46a5 5 0 0 1 0 7.07",
        "M19.07 4.93a10 10 0 0 1 0 14.14",
    )
}

/** Stroke-only icon for bottom navigation tabs. */
@Composable
fun BottomNavStrokeIcon(
    paths: List<String>,
    isActive: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    modifier: Modifier = Modifier,
    iconSize: Dp = 24.dp,
    activeStrokeWidth: Float = 2.5f,
    inactiveStrokeWidth: Float = 1.8f,
    viewportSize: Float = BottomNavIconPaths.ViewportSize,
) {
    LucideIcon(
        paths = paths,
        modifier = modifier.size(iconSize),
        strokeColor = if (isActive) activeColor else inactiveColor,
        fillColor = Color.Transparent,
        strokeWidth = if (isActive) activeStrokeWidth else inactiveStrokeWidth,
        viewportWidth = viewportSize,
        viewportHeight = viewportSize,
    )
}

@Composable
fun LucideIcon(
    paths: List<String>,
    modifier: Modifier = Modifier,
    strokeColor: Color,
    fillColor: Color = Color.Transparent,
    strokeWidth: Float = 2f,
    viewportWidth: Float = 24f,
    viewportHeight: Float = 24f,
    contentDescription: String? = null,
) {
    val vector = remember(paths, strokeColor, fillColor, strokeWidth, viewportWidth, viewportHeight) {
        buildLucideVector(paths, strokeColor, fillColor, strokeWidth, viewportWidth, viewportHeight)
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
    viewportWidth: Float,
    viewportHeight: Float,
): ImageVector {
    val builder = ImageVector.Builder(
        name = "Lucide",
        defaultWidth = viewportWidth.dp,
        defaultHeight = viewportHeight.dp,
        viewportWidth = viewportWidth,
        viewportHeight = viewportHeight,
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
