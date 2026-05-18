package com.mh.restaurantchainreservation.feature.discover.ui

internal data class SearchFilterState(
    val sortBy: Set<String> = emptySet(),
    val openNow: Boolean = false,
    val instantBook: Boolean = false,
    val prices: Set<String> = emptySet(),
    val cuisines: Set<String> = emptySet(),
    val amenities: Set<String> = emptySet(),
    val occasions: Set<String> = emptySet(),
    val seating: Set<String> = emptySet(),
    val rating: String = "Any",
    val distance: String = "Any Distance",
) {
    val activeCount: Int
        get() = listOf(openNow, instantBook).count { it } + prices.size + cuisines.size + amenities.size +
            occasions.size + seating.size + sortBy.size +
            (if (rating != "Any") 1 else 0) +
            (if (distance != "Any Distance") 1 else 0)
}

internal data class AppliedFilterChip(val id: String, val label: String)

internal fun buildAppliedFilterChips(filters: SearchFilterState): List<AppliedFilterChip> {
    val chips = mutableListOf<AppliedFilterChip>()
    filters.sortBy.forEach { sort ->
        chips += AppliedFilterChip(id = "sort:$sort", label = sort)
    }
    if (filters.openNow) chips += AppliedFilterChip("openNow", "Open now")
    if (filters.instantBook) chips += AppliedFilterChip("instantBook", "Instant book")
    filters.prices.forEach { chips += AppliedFilterChip("price:$it", it) }
    filters.cuisines.forEach { chips += AppliedFilterChip("cuisine:$it", it) }
    filters.amenities.forEach { chips += AppliedFilterChip("amenity:$it", it) }
    filters.occasions.forEach { chips += AppliedFilterChip("occasion:$it", it) }
    filters.seating.forEach { chips += AppliedFilterChip("seating:$it", it) }
    if (filters.rating != "Any") chips += AppliedFilterChip("rating:${filters.rating}", filters.rating)
    if (filters.distance != "Any Distance") {
        chips += AppliedFilterChip("distance:${filters.distance}", filters.distance)
    }
    return chips
}

internal fun removeAppliedFilterById(filters: SearchFilterState, id: String): SearchFilterState {
    val sep = id.indexOf(':')
    val type = if (sep == -1) id else id.substring(0, sep)
    val value = if (sep == -1) "" else id.substring(sep + 1)
    return when (type) {
        "sort" -> filters.copy(sortBy = filters.sortBy - value)
        "openNow" -> filters.copy(openNow = false)
        "instantBook" -> filters.copy(instantBook = false)
        "price" -> filters.copy(prices = filters.prices - value)
        "cuisine" -> filters.copy(cuisines = filters.cuisines - value)
        "amenity" -> filters.copy(amenities = filters.amenities - value)
        "occasion" -> filters.copy(occasions = filters.occasions - value)
        "seating" -> filters.copy(seating = filters.seating - value)
        "rating" -> filters.copy(rating = "Any")
        "distance" -> filters.copy(distance = "Any Distance")
        else -> filters
    }
}
