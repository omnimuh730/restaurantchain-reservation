package com.mh.restaurantchainreservation.feature.dining.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

enum class DiningTabId { Upcoming, Visited, Cancel }

private data class DiningTabSpec(
    val id: DiningTabId,
    val icon: ImageVector,
    val labelRes: Int,
)

private val DiningTabs = listOf(
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
) {
    val palette = LocalRestaurantPalette.current
    val rowShape = RoundedCornerShape(24.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(rowShape)
            .background(palette.cardSurface.copy(alpha = 0.92f))
            .padding(horizontal = 4.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DiningTabs.forEach { spec ->
                val active = spec.id == selected
                val count = counts[spec.id] ?: 0
                DiningTabButton(
                    icon = spec.icon,
                    label = stringResource(spec.labelRes),
                    count = count,
                    active = active,
                    onClick = { onSelect(spec.id) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun DiningTabButton(
    icon: ImageVector,
    label: String,
    count: Int,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val pillShape = RoundedCornerShape(percent = 50)

    val container by animateColorAsState(
        targetValue = if (active) palette.brand else palette.mutedSurface.copy(alpha = 0.75f),
        animationSpec = spring(stiffness = 520f, dampingRatio = 0.85f),
        label = "tab_container",
    )
    val content by animateColorAsState(
        targetValue = if (active) Color.White else palette.mutedForeground,
        animationSpec = spring(stiffness = 520f, dampingRatio = 0.85f),
        label = "tab_content",
    )
    val badgeContainer by animateColorAsState(
        targetValue = if (active) Color.White.copy(alpha = 0.25f) else palette.cardSurface,
        animationSpec = spring(stiffness = 520f, dampingRatio = 0.85f),
        label = "tab_badge_container",
    )
    val badgeContent by animateColorAsState(
        targetValue = if (active) Color.White else palette.mutedForeground,
        animationSpec = spring(stiffness = 520f, dampingRatio = 0.85f),
        label = "tab_badge_content",
    )

    Row(
        modifier = modifier
            .height(40.dp)
            .clip(pillShape)
            .let { if (active) it.shadow(elevation = 8.dp, shape = pillShape, ambientColor = palette.brand) else it }
            .background(container)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
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
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
        )
        Box(
            modifier = Modifier
                .defaultMinSize(minWidth = 16.dp)
                .height(16.dp)
                .clip(pillShape)
                .background(badgeContainer)
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = count.toString(),
                color = badgeContent,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}
