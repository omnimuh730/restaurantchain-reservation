package com.mh.restaurantchainreservation.core.designsystem.tokens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

object RestaurantDimensions {
    val Space2 = 2.dp
    val Space4 = 4.dp
    val Space8 = 8.dp
    val Space12 = 12.dp
    val Space16 = 16.dp
    val Space20 = 20.dp
    val Space24 = 24.dp
    val Space32 = 32.dp

    val RadiusSm = 12.dp
    val RadiusMd = 16.dp
    val RadiusLg = 24.dp
    val RadiusXl = 32.dp
}

val RestaurantShapes = Shapes(
    small = RoundedCornerShape(RestaurantDimensions.RadiusSm),
    medium = RoundedCornerShape(RestaurantDimensions.RadiusMd),
    large = RoundedCornerShape(RestaurantDimensions.RadiusLg),
)
