package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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

    /** Subpage header: row sits lower; body height hugs content so the bottom border sits under the header, not far above content. */
    val subpageRowTopExpanded = 26.dp
    val subpageHeaderBottomPadding = 12.dp
    val subpageSubtitleGapBelowRow = 10.dp
    /** Two-line subtitle block (13sp / 17sp lines) + slack. */
    val subpageSubtitleBlockHeight = 40.dp

    fun subpageExpandedBodyHeight(hasSubtitle: Boolean): androidx.compose.ui.unit.Dp {
        val rowBlock = maxOf(subpageBackSizeExpanded, subpageTitleLineHeightExpanded)
        val subtitleSection =
            if (hasSubtitle) subpageSubtitleGapBelowRow + subpageSubtitleBlockHeight else 0.dp
        return subpageRowTopExpanded + rowBlock + subtitleSection + subpageHeaderBottomPadding
    }

    private val subpageBackSizeExpanded = 38.dp
    private val subpageBackSizeCollapsed = 34.dp
    private val subpageTitleLineHeightExpanded = 42.dp

    fun subpageBackSize(collapseProgress: Float): androidx.compose.ui.unit.Dp {
        val t = 1f - collapseProgress
        return subpageBackSizeCollapsed + (subpageBackSizeExpanded - subpageBackSizeCollapsed) * t
    }

    fun subpageIconSize(collapseProgress: Float): androidx.compose.ui.unit.Dp {
        val t = 1f - collapseProgress
        return 20.dp + 2.dp * t
    }
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

    val headerBackground = if (palette.isDark) {
        palette.cardSurface
    } else {
        Color.White
    }

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

/**
 * Scroll-linked subpage header: back + title on one row (large title shrinks while scrolling),
 * optional subtitle below the row, optional actions, and the same bottom border as the hub header.
 * The back control’s circular plate fades out as [collapseProgress] approaches 1f.
 */
@Composable
fun CollapsingSubpageScreenHeader(
    title: String,
    collapseProgress: Float,
    onBack: () -> Unit,
    backContentDescription: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actions: (@Composable () -> Unit)? = null,
    horizontalPaddingDp: Int = 20,
) {
    val palette = LocalRestaurantPalette.current
    val density = LocalDensity.current
    val strokePx = with(density) { 1.dp.toPx() }
    val m = CollapsingTitleHeaderMetrics
    val startPad = horizontalPaddingDp.dp
    val hasSubtitle = subtitle != null
    val expandedBodyDp = m.subpageExpandedBodyHeight(hasSubtitle)
    val bodyHeight = with(density) {
        lerp(
            expandedBodyDp.toPx(),
            m.collapsedBodyHeight.toPx(),
            collapseProgress,
        ).toDp()
    }
    val backSize = m.subpageBackSize(collapseProgress)
    val iconSize = m.subpageIconSize(collapseProgress)
    val titleFontSp = lerp(34f, 20f, collapseProgress)
    val titleLineHeightSp = lerp(40f, 24f, collapseProgress)
    val borderAlpha = collapseProgress * 0.45f

    val titleBlockHeight = with(density) { titleLineHeightSp.sp.toDp() }
    val rowVisualHeight = maxOf(backSize, titleBlockHeight)
    val rowTopExpanded = m.subpageRowTopExpanded
    val rowTopCollapsed = (bodyHeight - rowVisualHeight) / 2f
    val rowOffsetY = with(density) {
        lerp(rowTopExpanded.toPx(), rowTopCollapsed.toPx(), collapseProgress).toDp()
    }

    val backCircleAlpha = (1f - collapseProgress).coerceIn(0f, 1f)
    val backPlateColor = palette.mutedSurface.copy(alpha = palette.mutedSurface.alpha * backCircleAlpha)

    val headerBackground = if (palette.isDark) {
        palette.cardSurface
    } else {
        Color.White
    }

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
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(y = rowOffsetY)
                    .fillMaxWidth()
                    .padding(horizontal = startPad),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(backSize)
                        .clip(CircleShape)
                        .background(backPlateColor)
                        .clickable(role = Role.Button, onClickLabel = backContentDescription, onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = backContentDescription,
                        tint = palette.foreground,
                        modifier = Modifier.size(iconSize),
                    )
                }
                Text(
                    text = title,
                    color = palette.foreground,
                    fontSize = titleFontSp.sp,
                    lineHeight = titleLineHeightSp.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (actions != null) {
                    actions()
                }
            }

            if (subtitle != null) {
                val subtitleAlpha = (1f - collapseProgress).coerceIn(0f, 1f)
                Text(
                    text = subtitle,
                    color = palette.mutedForeground,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(horizontal = startPad)
                        .offset(y = rowOffsetY + rowVisualHeight + m.subpageSubtitleGapBelowRow)
                        .fillMaxWidth()
                        .graphicsLayer { alpha = subtitleAlpha },
                )
            }
        }
    }
}
