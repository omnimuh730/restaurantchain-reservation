package com.mh.restaurantchainreservation.core.model

enum class AppTab(
    val route: String,
    val labelKey: String,
) {
    Discover("discover", "tab_discover"),
    Wishlist("wishlist", "tab_wishlist"),
    Dining("dining", "tab_dining"),
    Profile("profile", "tab_profile"),
}
