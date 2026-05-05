package com.mh.restaurantchainreservation.feature.discover

/**
 * Routes for the Discover module. Sub-routes follow `discover/{section}/{id}`
 * so the active-tab resolver can match the entire subtree with a `startsWith`.
 */
object DiscoverRoutes {
    const val Home: String = "discover"
    const val Search: String = "discover/search"
    const val Category: String = "discover/category/{categoryId}"
    const val Food: String = "discover/food/{foodId}"
    const val Location: String = "discover/location/{locationId}"
    const val Section: String = "discover/section/{sectionId}"

    fun category(id: String): String = "discover/category/$id"
    fun food(id: String): String = "discover/food/$id"
    fun location(id: String): String = "discover/location/$id"
    fun section(id: String): String = "discover/section/$id"
}
