package com.mh.restaurantchainreservation.feature.dining.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.mh.restaurantchainreservation.core.designsystem.components.TabSelectionBounceBox
import com.mh.restaurantchainreservation.core.designsystem.components.hubSurfaceBottomUnderlineShadow
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

enum class DiningTabId { Upcoming, Visited, Cancel }

/** Tab row height inside [DiningTabBar] (excluding underline / shadow). */
val DiningTabBarHeight = 50.dp

private val DiningTabBarTopPadding = 4.dp
private val DiningTabUnderlineShadowHeight = 1.dp
private val DiningTabActiveIndicatorHeight = 3.dp
private val DiningTabLabelToIndicatorGap = 10.dp

internal data class DiningTabSpec(
    val id: DiningTabId,
    val icon: ImageVector,
    val labelRes: Int,
)

internal val DiningTabs = listOf(
    DiningTabSpec(DiningTabId.Upcoming, Icons.Outlined.CalendarToday, I18nR.string.dining_tab_upcoming_short),
    DiningTabSpec(DiningTabId.Visited, Icons.Outlined.CheckCircle, I18nR.string.dining_tab_visited_short),
    DiningTabSpec(DiningTabId.Cancel, Icons.Outlined.Cancel, I18nR.string.dining_tab_cancel_short),
)

@Composable
fun DiningTabBar(
    selected: DiningTabId,
    counts: Map<DiningTabId, Int>,
    onSelect: (DiningTabId) -> Unit,
    modifier: Modifier = Modifier,
    pinnedUnderHeader: Boolean = false,
) {
    val palette = LocalRestaurantPalette.current
    val density = LocalDensity.current
    val strokePx = with(density) { 1.dp.toPx() }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = DiningTabBarTopPadding)
            .graphicsLayer { clip = false },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(DiningTabBarHeight)
                .then(
                    if (!pinnedUnderHeader) {
                        Modifier.drawBehind {
                            val y = size.height - strokePx * 0.5f
                            drawLine(
                                color = palette.border,
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = strokePx,
                            )
                        }
                    } else {
                        Modifier
                    },
                ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                DiningTabs.forEach { spec ->
                    val active = spec.id == selected
                    val count = counts[spec.id] ?: 0
                    DiningTabItem(
                        icon = spec.icon,
                        label = stringResource(spec.labelRes),
                        count = count,
                        active = active,
                        onClick = { onSelect(spec.id) },
                    )
                }
            }
        }
        if (pinnedUnderHeader) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DiningTabUnderlineShadowHeight)
                    .hubSurfaceBottomUnderlineShadow(),
            )
        }
    }
}

@Composable
private fun DiningTabItem(
    icon: ImageVector,
    label: String,
    count: Int,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val interactionSource = remember { MutableInteractionSource() }
    val contentColor by animateColorAsState(
        targetValue = if (active) palette.brand else palette.mutedForeground,
        animationSpec = spring(stiffness = 520f, dampingRatio = 0.85f),
        label = "dining_tab_content",
    )

    val indicatorScale by animateFloatAsState(
        targetValue = if (active) 1f else 0f,
        animationSpec = spring(stiffness = 500f, dampingRatio = 0.8f),
        label = "dining_tab_indicator_scale",
    )

    Box(
        modifier = modifier
            .height(DiningTabBarHeight)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onClick,
            ),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                TabSelectionBounceBox(
                    isActive = active,
                    modifier = Modifier,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = label,
                            color = contentColor,
                            fontSize = 14.sp,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1,
                        )
                        DiningTabCountBadge(
                            count = count,
                            active = active,
                        )
                    }
                }
            }
            Spacer(Modifier.height(DiningTabLabelToIndicatorGap))
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(DiningTabActiveIndicatorHeight)
                    .graphicsLayer {
                        scaleX = indicatorScale
                        alpha = indicatorScale
                    }
                    .clip(RoundedCornerShape(999.dp))
                    .background(palette.brand),
            )
        }
    }
}

@Composable
private fun DiningTabCountBadge(
    count: Int,
    active: Boolean,
) {
    val palette = LocalRestaurantPalette.current
    val background = if (active) palette.brand.copy(alpha = 0.12f) else palette.mutedSurface
    val textColor = if (active) palette.brand else palette.mutedForeground

    Box(
        modifier = Modifier
            .defaultMinSize(minWidth = 20.dp)
            .height(20.dp)
            .clip(CircleShape)
            .background(background)
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = count.toString(),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}
