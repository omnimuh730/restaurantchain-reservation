package com.mh.restaurantchainreservation.feature.booking

import com.mh.restaurantchainreservation.core.model.Banner
import com.mh.restaurantchainreservation.core.model.DiscoverData
import com.mh.restaurantchainreservation.core.model.Restaurant

data class RestaurantPhotoEntry(
    val url: String,
    val title: String,
)

enum class RestaurantPhotoGallerySource(val routeValue: String) {
    Gallery("gallery"),
    PopularMenu("menu"),
    Banner("banner"),
    ;

    companion object {
        fun fromRoute(value: String?): RestaurantPhotoGallerySource =
            entries.firstOrNull { it.routeValue == value } ?: Gallery
    }
}

object RestaurantPhotoGalleryData {
    private val galleryRoomLabels = listOf(
        "Dining room",
        "Interior",
        "Kitchen",
        "Bar area",
        "Patio",
        "Exterior",
        "Lounge",
    )

    fun restaurantIdForBanner(bannerId: String): String {
        val index = (bannerId.toIntOrNull() ?: 1).coerceAtLeast(1) - 1
        return DiscoverData.MONTHLY_BEST.getOrNull(index)?.id
            ?: DiscoverData.MONTHLY_BEST.first().id
    }

    fun entries(
        restaurant: Restaurant,
        source: RestaurantPhotoGallerySource,
        banner: Banner? = null,
    ): List<RestaurantPhotoEntry> = when (source) {
        RestaurantPhotoGallerySource.Gallery -> {
            val urls = RestaurantDetailData.galleryImages(restaurant)
            galleryLabels(restaurant.name, urls.size).zip(urls) { title, url ->
                RestaurantPhotoEntry(url = url, title = title)
            }
        }
        RestaurantPhotoGallerySource.PopularMenu -> {
            RestaurantDetailData.menuItems
                .mapNotNull { item -> item.imageUrl?.let { url -> url to item.name } }
                .distinctBy { it.first }
                .map { (url, name) -> RestaurantPhotoEntry(url = url, title = name) }
        }
        RestaurantPhotoGallerySource.Banner -> {
            val bannerImage = banner?.image ?: restaurant.image
            val extras = RestaurantDetailData.galleryImages(restaurant).drop(1).take(3)
            val urls = listOf(bannerImage) + extras
            val labels = listOfNotNull(
                banner?.title?.takeIf { it.isNotBlank() },
                restaurant.name,
            ) + galleryRoomLabels
            galleryLabels(labels.firstOrNull() ?: restaurant.name, urls.size)
                .zip(urls) { title, url -> RestaurantPhotoEntry(url = url, title = title) }
        }
    }

    private fun galleryLabels(primary: String, count: Int): List<String> {
        if (count <= 0) return emptyList()
        return buildList(count) {
            add(primary)
            var roomIndex = 0
            while (size < count) {
                add(galleryRoomLabels.getOrElse(roomIndex) { "Photo ${size + 1}" })
                roomIndex++
            }
        }
    }
}
