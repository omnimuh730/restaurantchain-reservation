package com.mh.restaurantchainreservation.core.designsystem.transition

/** Stable keys for [androidx.compose.animation.SharedTransitionScope] shared content. */
object RestaurantSharedKeys {
    fun hero(restaurantId: String) = "restaurant-$restaurantId-hero"
    fun title(restaurantId: String) = "restaurant-$restaurantId-title"
}
