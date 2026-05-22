package com.mh.restaurantchainreservation.feature.discover.ui

import java.util.Locale

internal sealed class LocationSuggestionRow {
    data class Preset(val where: WhereSelection, val label: String) : LocationSuggestionRow()
    data class City(val label: String) : LocationSuggestionRow()
}

internal enum class WhereSelection {
    Anywhere,
    NearMe,
    NewYork,
    SanFrancisco,
    Soho,
    Custom,
}

internal object DiscoverSearchData {
    val locationSuggestions: List<LocationSuggestionRow> = listOf(
        LocationSuggestionRow.Preset(WhereSelection.NewYork, "New York"),
        LocationSuggestionRow.Preset(WhereSelection.SanFrancisco, "San Francisco"),
        LocationSuggestionRow.Preset(WhereSelection.Soho, "SoHo / Downtown"),
        LocationSuggestionRow.City("Los Angeles, CA"),
        LocationSuggestionRow.City("Chicago, IL"),
        LocationSuggestionRow.City("Miami, FL"),
        LocationSuggestionRow.City("Austin, TX"),
        LocationSuggestionRow.City("Seattle, WA"),
        LocationSuggestionRow.City("Boston, MA"),
        LocationSuggestionRow.City("Denver, CO"),
        LocationSuggestionRow.City("Nashville, TN"),
        LocationSuggestionRow.City("Washington, D.C."),
        LocationSuggestionRow.City("Las Vegas, NV"),
    )

    val foodSuggestions: List<FoodSuggestion> = listOf(
        FoodSuggestion("Trending Now", trending = true),
        FoodSuggestion("Best BBQ", trending = true),
        FoodSuggestion("Hot in New York", trending = true),
        FoodSuggestion("Japanese", trending = false),
        FoodSuggestion("Italian", trending = false),
        FoodSuggestion("French", trending = false),
        FoodSuggestion("Thai", trending = false),
        FoodSuggestion("Chinese", trending = false),
        FoodSuggestion("Grilled Beef", trending = false),
        FoodSuggestion("Grilled Pork", trending = false),
        FoodSuggestion("Brunch", trending = false),
        FoodSuggestion("Bar & Pub", trending = false),
        FoodSuggestion("Healthy", trending = false),
    )

    val recentSearchLabels: List<String> = listOf(
        "Sakura Omakase",
        "BBQ",
        "Brunch near SoHo",
    )

    /** Shown when the address field is focused (recent place-style picks). */
    val recentLocationLabels: List<String> = listOf(
        "Los Angeles",
        "Beverly Hills",
        "Santa Monica",
    )

    val planTimeSlots: List<String> = listOf(
        "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
        "17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00",
        "20:30", "21:00", "21:30", "22:00",
    )

    val guestOptions: List<Int> = (1..10).toList()

    val filterSortOptions: List<String> = listOf(
        "Recommended", "Highest Rated", "Nearest", "Price: Low to High", "Price: High to Low", "Most Reviewed",
    )

    val filterCuisineChips: List<String> = listOf(
        "Grilled Beef", "Grilled Pork", "Bar & Pub", "Meat", "Fine Dining", "Seafood",
        "Korean", "Italian", "Japanese", "French", "Chinese",
    )

    val filterAmenityChips: List<String> = listOf(
        "Parking", "Valet", "Corkage-free", "Kids Welcome", "Pet Friendly", "Wi-Fi",
        "Wheelchair Accessible", "Outdoor Seating", "Private Dining", "Chef Counter", "Bar Seating",
        "Vegan Options", "Vegetarian Options", "Gluten-Free Options", "Wine Pairing", "Tasting Menu",
        "Takeout", "Delivery", "Open Late", "Live Music", "Rooftop", "High Chairs", "No Booking Fee",
    )

    val filterSeatingChips: List<String> = listOf(
        "Dining Hall", "Private Room", "Terrace", "Window Seat", "Bar",
    )

    val filterOccasionChips: List<String> = listOf(
        "Date Night", "Business Dinner", "Celebration", "Casual Dining", "Romantic", "Family-friendly", "Late Night", "Quick Bite",
    )

    val ratingFilterOptions: List<String> = listOf("Any", "3+", "3.5+", "4+", "4.5+")

    val distanceFilterOptions: List<String> = listOf(
        "Any Distance", "Within 0.5 mi", "Within 1 mi", "Within 2 mi", "Within 5 mi",
    )
}

internal data class FoodSuggestion(
    val label: String,
    val trending: Boolean,
)

internal fun whereLabel(where: WhereSelection, customText: String, nearMeName: String): String = when (where) {
    WhereSelection.Anywhere -> ""
    WhereSelection.NearMe -> nearMeName
    WhereSelection.NewYork -> "New York"
    WhereSelection.SanFrancisco -> "San Francisco"
    WhereSelection.Soho -> "SoHo / Downtown"
    WhereSelection.Custom -> customText.trim()
}

internal fun buildComposedSearchQuery(
    keyword: String,
    where: WhereSelection,
    customWhere: String,
    nearMeName: String,
): String {
    val kw = keyword.trim()
    val wl = whereLabel(where, customWhere, nearMeName)
    return when {
        kw.isEmpty() && wl.isEmpty() -> ""
        kw.isEmpty() -> wl
        wl.isEmpty() -> kw
        where == WhereSelection.NearMe -> "$kw near $wl"
        else -> "$kw in $wl"
    }
}

internal fun formatPlanSummary(dateLabel: String, time24: String, partySize: Int): String {
    return "$dateLabel, ${formatReservationTime(time24)}, ${peopleLabel(partySize)}"
}

internal fun peopleLabel(count: Int): String =
    if (count == 1) "1 person" else "$count people"

internal fun formatReservationTime(time24: String): String {
    val parts = time24.split(":")
    if (parts.size < 2) return time24
    var hour = parts[0].toIntOrNull() ?: return time24
    val minute = parts[1].toIntOrNull() ?: 0
    val am = hour < 12
    val h12 = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    val suffix = if (am) "AM" else "PM"
    return String.format(Locale.US, "%d:%02d %s", h12, minute, suffix)
}

internal fun mapMarkerScore(rating: Double): String =
    String.format(Locale.US, "%.1f", (rating * 2).coerceIn(7.0, 10.0))
