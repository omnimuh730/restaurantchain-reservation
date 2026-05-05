package com.mh.restaurantchainreservation.core.designsystem.components.icons

import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

private const val DotPath = "M20 38a12 12 0 1 0 0 24 12 12 0 1 0 0-24Z"

private const val DonutPath =
    "M75 85C94.33 85 110 69.33 110 50C110 30.67 94.33 15 75 15C55.67 15 40 30.67 40 50C40 69.33 55.67 85 75 85Z" +
        "M75 62C81.627 62 87 56.627 87 50C87 43.373 81.627 38 75 38C68.373 38 63 43.373 63 50C63 56.627 68.373 62 75 62Z"

@Composable
fun TonightLogoMark(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    contentDescription: String? = null,
) {
    val vector = remember(color) { buildLogoVector(color) }
    Image(
        imageVector = vector,
        contentDescription = contentDescription,
        modifier = modifier,
    )
}

private fun buildLogoVector(color: Color): ImageVector =
    ImageVector.Builder(
        name = "TonightLogoMark",
        defaultWidth = 120.dp,
        defaultHeight = 100.dp,
        viewportWidth = 120f,
        viewportHeight = 100f,
    )
        .addPath(
            pathData = addPathNodes(DotPath),
            fill = SolidColor(color),
        )
        .addPath(
            pathData = addPathNodes(DonutPath),
            pathFillType = PathFillType.EvenOdd,
            fill = SolidColor(color),
        )
        .build()
