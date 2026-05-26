package com.mh.restaurantchainreservation.core.designsystem.badge

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.model.Restaurant
import com.mh.restaurantchainreservation.core.model.discoverCardBadgeLabel

@Composable
fun DiscoverRestaurantCardBadgeChip(
    restaurant: Restaurant,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 11.sp,
) {
    val label = restaurant.discoverCardBadgeLabel() ?: return
    RestaurantCardTagChip(
        text = label,
        modifier = modifier,
        fontSize = fontSize,
    )
}
