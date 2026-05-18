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
import androidx.compose.ui.graphics.vector.ImageVector
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

    /** Subpage header: controls row sits lower; large title sits under it (wishlist / LargeTopAppBar style). */
    val subpageRowTopExpanded = 10.dp
    val subpageHeaderBottomPadding = 12.dp
    val subpageTitleGapBelowControls = 6.dp
    val subpageSubtitleGapBelowTitle = 8.dp
    /** Two-line subtitle block (13sp / 17sp lines) + slack. */
    val subpageSubtitleBlockHeight = 40.dp
    /** Reserved width for one trailing header action when collapsed. */
    val subpageCollapsedTrailingReserve = 52.dp

    fun subpageExpandedBodyHeight(hasSubtitle: Boolean): androidx.compose.ui.unit.Dp {
        val subtitleSection =
            if (hasSubtitle) subpageSubtitleGapBelowTitle + subpageSubtitleBlockHeight else 0.dp
        return subpageRowTopExpanded +
            subpageBackSizeExpanded +
            subpageTitleGapBelowControls +
            subpageTitleLineHeightExpanded +
            subtitleSection +
            subpageHeaderBottomPadding
    }

    private val subpageBackSizeExpanded = 48.dp
    private val subpageBackSizeCollapsed = 36.dp
    private val subpageTitleLineHeightExpanded = 42.dp

    fun subpageBackSize(collapseProgress: Float): androidx.compose.ui.unit.Dp {
        val t = 1f - collapseProgress
        return subpageBackSizeCollapsed + (subpageBackSizeExpanded - subpageBackSizeCollapsed) * t
    }

    fun subpageIconSize(collapseProgress: Float): androidx.compose.ui.unit.Dp {
        val t = 1f - collapseProgress
        return 22.dp + 7.dp * t
    }
}

/**
 * Circular header control for collapsing subpages: lerps size with [collapseProgress] and fades
 * the muted plate to transparent when collapsed (same behavior as the back control).
 */
@Composable
fun CollapsingSubpageHeaderIconButton(
    collapseProgress: Float,
    onClick: () -> Unit,
    contentDescription: String,
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val m = CollapsingTitleHeaderMetrics
    val size = m.subpageBackSize(collapseProgress)
    val iconSize = m.subpageIconSize(collapseProgress)
    val plateAlpha = (1f - collapseProgress).coerceIn(0f, 1f)
    val plateColor = palette.mutedSurface.copy(alpha = palette.mutedSurface.alpha * plateAlpha)
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(plateColor)
            .clickable(role = Role.Button, onClickLabel = contentDescription, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = palette.foreground,
            modifier = Modifier.size(iconSize),
        )
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
 * Scroll-linked subpage header (wishlist collection / LargeTopAppBar style): back and trailing
 * actions on one row ([Arrangement.SpaceBetween]), large title below, then optional subtitle.
 * On scroll the title shrinks and moves up into the toolbar row beside the back control.
 *
 * @param actions Optional trailing controls; receive [collapseProgress] so they can match the back
 * button (see [CollapsingSubpageHeaderIconButton]).
 */
@Composable
fun CollapsingSubpageScreenHeader(
    title: String,
    collapseProgress: Float,
    onBack: () -> Unit,
    backContentDescription: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actions: (@Composable (collapseProgress: Float) -> Unit)? = null,
    horizontalPaddingDp: Int = 20,
    titleFontExpandedSp: Float = 34f,
    titleFontCollapsedSp: Float = 20f,
    titleLineHeightExpandedSp: Float = 40f,
    titleLineHeightCollapsedSp: Float = 24f,
) {
    val palette = LocalRestaurantPalette.current
    val density = LocalDensity.current
    val strokePx = with(density) { 1.dp.toPx() }
    val m = CollapsingTitleHeaderMetrics
    val startPad = horizontalPaddingDp.dp
    val hasSubtitle = subtitle != null
    val hasActions = actions != null
    val expandedBodyDp = m.subpageExpandedBodyHeight(hasSubtitle)
    val bodyHeight = with(density) {
        lerp(
            expandedBodyDp.toPx(),
            m.collapsedBodyHeight.toPx(),
            collapseProgress,
        ).toDp()
    }
    val backSize = m.subpageBackSize(collapseProgress)
    val titleFontSp = lerp(titleFontExpandedSp, titleFontCollapsedSp, collapseProgress)
    val titleLineHeightSp = lerp(titleLineHeightExpandedSp, titleLineHeightCollapsedSp, collapseProgress)
    val borderAlpha = collapseProgress * 0.45f

    val titleBlockHeight = with(density) { titleLineHeightSp.sp.toDp() }
    val controlRowHeight = backSize
    val controlsTopExpanded = m.subpageRowTopExpanded
    val controlsTopCollapsed = (bodyHeight - controlRowHeight) / 2f
    val controlsRowY = with(density) {
        lerp(controlsTopExpanded.toPx(), controlsTopCollapsed.toPx(), collapseProgress).toDp()
    }
    val titleYExpanded = controlsRowY + controlRowHeight + m.subpageTitleGapBelowControls
    val titleYCollapsed = controlsRowY + (controlRowHeight - titleBlockHeight) / 2f
    val titleOffsetY = with(density) {
        lerp(titleYExpanded.toPx(), titleYCollapsed.toPx(), collapseProgress).toDp()
    }
    val titleStartInset = with(density) {
        lerp(0f, (backSize + 8.dp).toPx(), collapseProgress).toDp()
    }
    val titleEndPad = if (hasActions) {
        with(density) {
            lerp(startPad.toPx(), m.subpageCollapsedTrailingReserve.toPx(), collapseProgress).toDp()
        }
    } else {
        startPad
    }

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
                    .offset(y = controlsRowY)
                    .fillMaxWidth()
                    .padding(horizontal = startPad),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                CollapsingSubpageHeaderIconButton(
                    collapseProgress = collapseProgress,
                    onClick = onBack,
                    contentDescription = backContentDescription,
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    actions?.invoke(collapseProgress)
                }
            }

            Text(
                text = title,
                color = palette.foreground,
                fontSize = titleFontSp.sp,
                lineHeight = titleLineHeightSp.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(y = titleOffsetY)
                    .padding(start = startPad + titleStartInset, end = titleEndPad)
                    .fillMaxWidth(),
            )

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
                        .offset(y = titleOffsetY + titleBlockHeight + m.subpageSubtitleGapBelowTitle)
                        .fillMaxWidth()
                        .graphicsLayer { alpha = subtitleAlpha },
                )
            }
        }
    }
}
