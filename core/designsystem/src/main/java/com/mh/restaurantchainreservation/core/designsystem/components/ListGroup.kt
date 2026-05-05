package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette

@Immutable
data class ListGroupItem(
    val id: String,
    val label: String,
    val description: String? = null,
    val icon: (@Composable () -> Unit)? = null,
    val rightContent: (@Composable () -> Unit)? = null,
    val onClick: (() -> Unit)? = null,
    val disabled: Boolean = false,
)

enum class ListGroupVariant { Default, Bordered, Separated }

@Composable
fun ListGroup(
    items: List<ListGroupItem>,
    modifier: Modifier = Modifier,
    variant: ListGroupVariant = ListGroupVariant.Bordered,
    showChevron: Boolean = false,
    rowVerticalPadding: Int = 12,
    rowHorizontalPadding: Int = 16,
) {
    val palette = LocalRestaurantPalette.current
    val container: Modifier = when (variant) {
        ListGroupVariant.Bordered -> Modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
        ListGroupVariant.Default -> Modifier
        ListGroupVariant.Separated -> Modifier
    }

    Column(
        modifier = modifier.then(container),
        verticalArrangement = if (variant == ListGroupVariant.Separated) {
            Arrangement.spacedBy(8.dp)
        } else {
            Arrangement.spacedBy(0.dp)
        },
    ) {
        items.forEachIndexed { index, item ->
            ListRow(
                item = item,
                showChevron = showChevron,
                separated = variant == ListGroupVariant.Separated,
                horizontalPadding = rowHorizontalPadding,
                verticalPadding = rowVerticalPadding,
            )
            if (variant == ListGroupVariant.Bordered && index < items.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = rowHorizontalPadding.dp),
                    thickness = 1.dp,
                    color = palette.border,
                )
            }
        }
    }
}

@Composable
private fun ListRow(
    item: ListGroupItem,
    showChevron: Boolean,
    separated: Boolean,
    horizontalPadding: Int,
    verticalPadding: Int,
) {
    val palette = LocalRestaurantPalette.current
    val rowShape = RoundedCornerShape(12.dp)
    val baseModifier = Modifier
        .fillMaxWidth()
        .let { if (separated) it.clip(rowShape).border(1.dp, palette.border, rowShape) else it }
        .let { mod ->
            if (item.onClick != null && !item.disabled) {
                mod.clickable(onClick = item.onClick)
            } else {
                mod
            }
        }
        .padding(horizontal = horizontalPadding.dp, vertical = verticalPadding.dp)

    Row(
        modifier = baseModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (item.icon != null) {
            Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                item.icon.invoke()
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.label,
                color = palette.foreground,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (item.description != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = item.description,
                    color = palette.mutedForeground,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (item.rightContent != null) {
            Box(contentAlignment = Alignment.CenterEnd) {
                item.rightContent.invoke()
            }
            Spacer(Modifier.width(4.dp))
        }
        if (showChevron && item.onClick != null) {
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = palette.mutedForeground,
                modifier = Modifier.size(18.dp),
            )
        }
    }

    if (item.disabled) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)),
        )
    }
}
