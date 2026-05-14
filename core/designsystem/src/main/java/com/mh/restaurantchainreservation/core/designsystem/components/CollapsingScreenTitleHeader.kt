package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette

/**
 * Shared metrics for profile-style hub screens (expanded / collapsed large title header).
 */
object CollapsingTitleHeaderMetrics {
    val expandedBodyHeight = 124.dp
    val collapsedBodyHeight = 56.dp
    val trailingSlotSize = 44.dp
    val titleGapBelowTrailing = 6.dp
    val titleCollapsedLineApprox = 22.dp
    val trailingTopExpanded = 10.dp
    val titleStartPadding = 24.dp
    const val titleMaxWidthFractionWhenTrailing = 0.55f
}

/**
 * Scroll-linked collapsing app-bar style header: status-bar scrim, large title that shrinks and
 * moves up, optional top-end trailing slot (e.g. notifications). Matches profile hub behavior.
 *
 * @param collapseProgress 0f = expanded, 1f = collapsed; typically `scroll / collapseRangePx` in 0..1.
 */
@Composable
fun CollapsingScreenTitleHeader(
    title: String,
    collapseProgress: Float,
    modifier: Modifier = Modifier,
    backgroundAlpha: Float = 0.97f,
    trailing: (@Composable () -> Unit)? = null,
) {
    val palette = LocalRestaurantPalette.current
    val density = LocalDensity.current
    val strokePx = with(density) { 1.dp.toPx() }

    val m = CollapsingTitleHeaderMetrics
    val bodyHeight = with(density) {
        lerp(
            m.expandedBodyHeight.toPx(),
            m.collapsedBodyHeight.toPx(),
            collapseProgress,
        ).toDp()
    }
    val titleFontSp = lerp(34f, 20f, collapseProgress)
    val titleLineHeightSp = lerp(40f, 24f, collapseProgress)
    val borderAlpha = collapseProgress * 0.45f

    val trailingTopCollapsed = (bodyHeight - m.trailingSlotSize) / 2f
    val trailingOffsetY = m.trailingTopExpanded + (trailingTopCollapsed - m.trailingTopExpanded) * collapseProgress

    val titleTopExpanded = m.trailingTopExpanded + m.trailingSlotSize + m.titleGapBelowTrailing
    val titleTopCollapsed = (bodyHeight - m.titleCollapsedLineApprox) / 2f
    val titleOffsetY = titleTopExpanded + (titleTopCollapsed - titleTopExpanded) * collapseProgress

    val headerBackground = palette.cardSurface.copy(alpha = backgroundAlpha)

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(headerBackground),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(bodyHeight)
                .background(headerBackground)
                .drawBehind {
                    if (borderAlpha > 0.004f) {
                        drawLine(
                            color = palette.border.copy(alpha = borderAlpha),
                            start = Offset(0f, size.height - strokePx * 0.5f),
                            end = Offset(size.width, size.height - strokePx * 0.5f),
                            strokeWidth = strokePx,
                        )
                    }
                },
        ) {
            if (trailing != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 20.dp)
                        .offset(y = trailingOffsetY),
                ) {
                    trailing()
                }
            }

            Text(
                text = title,
                color = palette.foreground,
                fontSize = titleFontSp.sp,
                lineHeight = titleLineHeightSp.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = m.titleStartPadding)
                    .offset(y = titleOffsetY)
                    .fillMaxWidth(
                        if (trailing != null) {
                            CollapsingTitleHeaderMetrics.titleMaxWidthFractionWhenTrailing
                        } else {
                            1f
                        },
                    ),
            )
        }
    }
}
