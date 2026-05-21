package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset

/**
 * Bottom padding the nav host applies around destinations. Edge-to-edge screens (e.g. search
 * results) can [expandIntoParentBottomPadding] so layout height stays constant when that padding
 * changes (such as when navigating to a route that hides the bottom bar).
 */
val LocalNavContentBottomPadding = staticCompositionLocalOf { 0.dp }

/**
 * Grows layout height by [extra] so this composable fills the space behind the parent's bottom
 * nav content padding.
 */
fun Modifier.expandIntoParentBottomPadding(extra: Dp): Modifier = composed {
    val extraPx = with(LocalDensity.current) { extra.roundToPx() }
    if (extraPx <= 0) return@composed this
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints.offset(vertical = extraPx))
        layout(constraints.maxWidth, constraints.maxHeight + extraPx) {
            placeable.place(0, 0)
        }
    }
}
