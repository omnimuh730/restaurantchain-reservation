package com.mh.restaurantchainreservation.feature.dining.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

@Composable
fun DiningListHeader(
    tab: DiningTabId,
    onAddByCode: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val titleRes = when (tab) {
        DiningTabId.Upcoming -> I18nR.string.dining_tab_upcoming_title
        DiningTabId.Visited -> I18nR.string.dining_tab_visited_title
        DiningTabId.Cancel -> I18nR.string.dining_tab_cancel_title
    }
    val descRes = when (tab) {
        DiningTabId.Upcoming -> I18nR.string.dining_tab_upcoming_desc
        DiningTabId.Visited -> I18nR.string.dining_tab_visited_desc
        DiningTabId.Cancel -> I18nR.string.dining_tab_cancel_desc
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
    ) {
        Text(
            text = stringResource(titleRes),
            color = palette.foreground,
            fontSize = 18.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(descRes),
            color = palette.mutedForeground,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        )
        if (tab == DiningTabId.Upcoming) {
            Spacer(Modifier.height(12.dp))
            AddByCodeChip(onClick = onAddByCode)
        }
    }
}

@Composable
private fun AddByCodeChip(onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(percent = 50)
    Row(
        modifier = Modifier
            .height(36.dp)
            .clip(shape)
            .border(1.dp, palette.brand.copy(alpha = 0.20f), shape)
            .background(palette.brand.copy(alpha = 0.08f))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.Add,
            contentDescription = null,
            tint = palette.brand,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = stringResource(I18nR.string.dining_add_by_code),
            color = palette.brand,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}
