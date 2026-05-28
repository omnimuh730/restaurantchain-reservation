package com.mh.restaurantchainreservation.core.designsystem.transition

/** Stable keys for [androidx.compose.animation.SharedTransitionScope] shared content. */
object RestaurantSharedKeys {
    const val SearchBar = "global-search-bar"

    fun hero(restaurantId: String) = "restaurant-$restaurantId-hero"
    fun title(restaurantId: String) = "restaurant-$restaurantId-title"
    fun contentPanel(restaurantId: String) = "restaurant-$restaurantId-content-panel"
}
