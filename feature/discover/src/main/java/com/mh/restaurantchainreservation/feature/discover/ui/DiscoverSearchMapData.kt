package com.mh.restaurantchainreservation.feature.discover.ui

import com.mh.restaurantchainreservation.core.model.Restaurant

internal data class MapLatLng(val lat: Double, val lng: Double)

/** San Francisco center — mirrors React `USER_LOCATION` in searchMapData.ts */
internal val SEARCH_USER_LOCATION = MapLatLng(lat = 37.7849, lng = -122.4074)

/** Seed coordinates from React `RESTAURANTS` in searchMapData.ts (subset used for stable mapping). */
private val MAP_RESTAURANT_COORDS: List<MapLatLng> = listOf(
    MapLatLng(37.7920, -122.4120),
    MapLatLng(37.7870, -122.4180),
    MapLatLng(37.7810, -122.4050),
    MapLatLng(37.7890, -122.3990),
    MapLatLng(37.7760, -122.4140),
    MapLatLng(37.7940, -122.3950),
    MapLatLng(37.7960, -122.4060),
    MapLatLng(37.7800, -122.4200),
    MapLatLng(37.7830, -122.4010),
    MapLatLng(37.7950, -122.4100),
    MapLatLng(37.7780, -122.3970),
    MapLatLng(37.7910, -122.4160),
    MapLatLng(37.7870, -122.4030),
    MapLatLng(37.7840, -122.4190),
    MapLatLng(37.7920, -122.3980),
    MapLatLng(37.7770, -122.4080),
    MapLatLng(37.7980, -122.4150),
    MapLatLng(37.7750, -122.4020),
    MapLatLng(37.7860, -122.4210),
    MapLatLng(37.7850, -122.3960),
)

internal fun Restaurant.searchMapLocation(index: Int): MapLatLng {
    val numeric = id.filter { it.isDigit() }.toIntOrNull()
    val seed = if (numeric != null && numeric > 0) numeric else index + 1
    val coordIndex = (seed - 1).coerceAtLeast(0) % MAP_RESTAURANT_COORDS.size
    return MAP_RESTAURANT_COORDS[coordIndex]
}

internal fun milesToMeters(miles: Double): Double = miles * 1609.344

internal fun maxMilesForDistanceFilter(option: String): Double? = when (option) {
    "Within 0.5 mi" -> 0.5
    "Within 1 mi" -> 1.0
    "Within 2 mi" -> 2.0
    "Within 5 mi" -> 5.0
    else -> null
}
