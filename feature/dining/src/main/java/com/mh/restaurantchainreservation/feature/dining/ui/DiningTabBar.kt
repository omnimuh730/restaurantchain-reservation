package com.mh.restaurantchainreservation.feature.dining.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

enum class DiningTabId { Upcoming, Visited, Cancel, EmptyPreview }

/** Total height of [DiningTabBar] including divider. */
val DiningTabBarHeight = 52.dp

private data class DiningTabSpec(
    val id: DiningTabId,
    val icon: ImageVector,
    val labelRes: Int,
)

private val DiningTabs = listOf(
    DiningTabSpec(DiningTabId.Upcoming, Icons.Outlined.CalendarToday, I18nR.string.dining_tab_upcoming_short),
    DiningTabSpec(DiningTabId.Visited, Icons.Outlined.CheckCircle, I18nR.string.dining_tab_visited_short),
    DiningTabSpec(DiningTabId.Cancel, Icons.Outlined.Cancel, I18nR.string.dining_tab_cancel_short),
    // TODO: remove preview tab after no-item card QA
    DiningTabSpec(DiningTabId.EmptyPreview, Icons.Outlined.Inbox, I18nR.string.dining_tab_empty_preview_short),
)

@Composable
fun DiningTabBar(
    selected: DiningTabId,
    counts: Map<DiningTabId, Int>,
    onSelect: (DiningTabId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val scrollState = rememberScrollState()

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
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
        HorizontalDivider(
            color = palette.border,
            modifier = Modifier.padding(top = 2.dp),
        )
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
    val content by animateColorAsState(
        targetValue = if (active) palette.foreground else palette.mutedForeground,
        animationSpec = spring(stiffness = 520f, dampingRatio = 0.85f),
        label = "tab_content",
    )
    val indicator by animateColorAsState(
        targetValue = if (active) palette.brand else Color.Transparent,
        animationSpec = spring(stiffness = 520f, dampingRatio = 0.85f),
        label = "tab_indicator",
    )

    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(top = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 2.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = label,
                color = content,
                fontSize = 14.sp,
                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                maxLines = 1,
            )
            DiningTabCountBadge(
                count = count,
                active = active,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(indicator),
        )
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
