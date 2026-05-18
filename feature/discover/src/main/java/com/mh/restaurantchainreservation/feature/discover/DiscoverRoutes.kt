package com.mh.restaurantchainreservation.feature.discover

/**
 * Routes for the Discover module. Sub-routes follow `discover/{section}/{id}`
 * so the active-tab resolver can match the entire subtree with a `startsWith`.
 */
object DiscoverRoutes {
    const val Home: String = "discover"
    /** Full-screen gallery of hero banners (View All on Discover). */
    const val AllPromotions: String = "discover/promotions"
    /** “Where to Eat?” → list of cities/areas before opening map search for one area. */
    const val WhereToEatAreas: String = "discover/where-to-eat/areas"
    /** “Top Picks by Food Type” → list of cuisines before opening that cuisine’s restaurant list. */
    const val FoodTypes: String = "discover/food-types"
    const val Search: String = "discover/search"
    const val Category: String = "discover/category/{categoryId}"
    const val Food: String = "discover/food/{foodId}"
    const val Location: String = "discover/location/{locationId}"
    const val Section: String = "discover/section/{sectionId}"
    /** Map search results; optional [locationId] scopes the restaurant pool to [DiscoverData.byCity]. */
    const val SearchResults: String = "discover/search/results?q={q}&summary={summary}&locationId={locationId}"
    const val NewsList: String = "discover/news"
    const val NewsDetail: String = "discover/news/{articleId}"

    fun category(id: String): String = "discover/category/$id"
    fun food(id: String): String = "discover/food/$id"
    fun location(id: String): String = "discover/location/$id"
    fun section(id: String): String = "discover/section/$id"
    fun newsDetail(articleId: String): String = "discover/news/$articleId"
}
