package com.mh.restaurantchainreservation.feature.dining.data

data class DiningDiscoverSpotlight(
    val id: String,
    val title: String,
    val placesLabel: String,
    val imageUrl: String,
)

val DINING_DISCOVER_SPOTLIGHTS = listOf(
    DiningDiscoverSpotlight(
        id = "omakase",
        title = "Best Omakase Experiences",
        placesLabel = "12 places nearby",
        imageUrl = "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=400&h=400&fit=crop",
    ),
    DiningDiscoverSpotlight(
        id = "bbq",
        title = "Top Smokehouse Picks",
        placesLabel = "8 places nearby",
        imageUrl = "https://images.unsplash.com/photo-1709433420612-8cad609df914?w=400&h=400&fit=crop",
    ),
    DiningDiscoverSpotlight(
        id = "bistro",
        title = "Cozy French Bistros",
        placesLabel = "15 places nearby",
        imageUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=400&h=400&fit=crop",
    ),
    DiningDiscoverSpotlight(
        id = "seafood",
        title = "Fresh Seafood Tonight",
        placesLabel = "10 places nearby",
        imageUrl = "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=400&h=400&fit=crop",
    ),
    DiningDiscoverSpotlight(
        id = "pasta",
        title = "Handmade Pasta Spots",
        placesLabel = "9 places nearby",
        imageUrl = "https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?w=400&h=400&fit=crop",
    ),
)
