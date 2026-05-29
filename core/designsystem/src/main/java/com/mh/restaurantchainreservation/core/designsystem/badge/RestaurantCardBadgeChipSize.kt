package com.mh.restaurantchainreservation.core.designsystem.badge

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class RestaurantCardBadgeChipSize(val container: Dp, val icon: Dp) {
    Small(container = 24.dp, icon = 14.dp),
    Medium(container = 28.dp, icon = 17.dp),
    Large(container = 32.dp, icon = 20.dp),
}
