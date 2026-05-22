package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Clipped host for [BottomNavBar]: background, tabs, docked QR, shadow, and cutout slide/fade
 * with [visibilityProgress] (1 = fully visible, 0 = fully hidden).
 */
@Composable
fun BottomNavAnimatedOverlay(
    visibilityProgress: Float,
    modifier: Modifier = Modifier,
    onBarLayoutHeightChanged: (Int) -> Unit = {},
    content: @Composable () -> Unit,
) {
    val progress = visibilityProgress.coerceIn(0f, 1f)
    val density = LocalDensity.current
    var barLayoutHeightPx by remember { mutableIntStateOf(0) }
    val qrClipExtensionPx = remember(density) {
        with(density) { BottomNavClipExtensionAboveTabRow.roundToPx() }
    }
    val navigationBarInsetPx = WindowInsets.navigationBars.getBottom(density)
    val minBarLayoutPx = remember(density, navigationBarInsetPx) {
        with(density) {
            BottomNavTabRowHeight.toPx().roundToInt() + navigationBarInsetPx
        }
    }
    val layoutHeightPx = maxOf(barLayoutHeightPx, minBarLayoutPx)
    val clipHeightPx = layoutHeightPx + qrClipExtensionPx
    val clipHeightDp = with(density) { clipHeightPx.toDp() }

    Box(
        modifier
            .fillMaxWidth()
            .height(clipHeightDp)
            .clip(RectangleShape)
            .graphicsLayer {
                alpha = progress
                translationY = (1f - progress) * clipHeightPx
            },
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .onSizeChanged { size ->
                        if (size.height > 0) {
                            barLayoutHeightPx = size.height
                            onBarLayoutHeightChanged(size.height)
                        }
                    },
                contentAlignment = Alignment.BottomCenter,
            ) {
                content()
            }
        }
    }
}
