package com.mh.restaurantchainreservation.feature.booking

object BookingRoutes {
    const val RestaurantDetail: String = "discover/restaurant/{restaurantId}"
    const val RestaurantMenu: String = "discover/restaurant/{restaurantId}/menu"
    const val BookTable: String = "discover/restaurant/{restaurantId}/book"

    fun restaurantDetail(id: String): String = "discover/restaurant/$id"
    fun restaurantMenu(id: String): String = "discover/restaurant/$id/menu"
    fun bookTable(id: String): String = "discover/restaurant/$id/book"
}
