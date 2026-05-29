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
    size: RestaurantCardBadgeChipSize = RestaurantCardBadgeChipSize.Medium,
) {
    val label = restaurant.discoverCardBadgeLabel() ?: return
    RestaurantCardTagChip(
        text = label,
        modifier = modifier,
        size = size,
    )
}
