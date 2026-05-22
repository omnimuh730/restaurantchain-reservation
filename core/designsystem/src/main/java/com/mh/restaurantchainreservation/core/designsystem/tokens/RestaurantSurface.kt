package com.mh.restaurantchainreservation.core.designsystem.tokens

import androidx.compose.ui.graphics.Color

/** @see RestaurantColors.Surface */
@Deprecated("Use RestaurantColors.Surface", ReplaceWith("RestaurantColors.Surface"))
object RestaurantSurface {
    val PureWhite: Color get() = RestaurantColors.Surface.canvas
    @Deprecated("Foggy #F7F7F7 — chips/pills only, not page canvas", ReplaceWith("RestaurantColors.Surface.foggy"))
    val OffWhite: Color get() = RestaurantColors.Surface.foggy
}
