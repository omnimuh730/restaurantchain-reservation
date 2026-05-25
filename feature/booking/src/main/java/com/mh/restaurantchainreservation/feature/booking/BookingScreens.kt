package com.mh.restaurantchainreservation.feature.booking

object BookingRoutes {
    const val RestaurantDetail: String = "discover/restaurant/{restaurantId}"
    const val RestaurantMenu: String = "discover/restaurant/{restaurantId}/menu"
    const val BookTable: String = "discover/restaurant/{restaurantId}/book?modifyBookingId={modifyBookingId}"
    const val PhotoGrid: String =
        "discover/restaurant/{restaurantId}/photos?source={source}&bannerId={bannerId}"

    fun restaurantDetail(id: String): String = "discover/restaurant/$id"
    fun restaurantMenu(id: String): String = "discover/restaurant/$id/menu"
    fun bookTable(id: String, modifyBookingId: String? = null): String =
        if (modifyBookingId.isNullOrBlank()) {
            "discover/restaurant/$id/book?modifyBookingId="
        } else {
            "discover/restaurant/$id/book?modifyBookingId=$modifyBookingId"
        }
    fun photoGrid(
        restaurantId: String,
        source: RestaurantPhotoGallerySource,
        bannerId: String = "",
    ): String = "discover/restaurant/$restaurantId/photos?source=${source.routeValue}&bannerId=$bannerId"
}
