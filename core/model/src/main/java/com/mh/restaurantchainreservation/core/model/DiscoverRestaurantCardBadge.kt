package com.mh.restaurantchainreservation.core.model

/** Discover listing card badges — only these four labels are shown on restaurant cards. */
enum class DiscoverRestaurantCardBadge(val label: String) {
    GuestFavorite("Guest Favorite"),
    Popular("Popular"),
    RareFind("Rare Find"),
    New("New"),
}

/**
 * Resolves the badge label for a discover restaurant card.
 * Returns null when no badge should be shown.
 */
fun Restaurant.discoverCardBadge(): DiscoverRestaurantCardBadge? {
    discoverCardBadgeFromTag(tag)?.let { return it }
    if (guestFavoriteLevel != GuestFavoriteLevel.None) {
        return DiscoverRestaurantCardBadge.GuestFavorite
    }
    return null
}

fun Restaurant.discoverCardBadgeLabel(): String? = discoverCardBadge()?.label

private fun discoverCardBadgeFromTag(tag: String?): DiscoverRestaurantCardBadge? {
    if (tag.isNullOrBlank()) return null

    DiscoverRestaurantCardBadge.entries.firstOrNull { badge ->
        badge.label.equals(tag, ignoreCase = true)
    }?.let { return it }

    return when {
        tag.contains("favorite", ignoreCase = true) ||
            tag.contains("Monthly Best", ignoreCase = true) -> DiscoverRestaurantCardBadge.GuestFavorite

        tag.contains("popular", ignoreCase = true) ||
            tag.contains("views", ignoreCase = true) ||
            tag.contains("Must Try", ignoreCase = true) -> DiscoverRestaurantCardBadge.Popular

        tag.contains("rare", ignoreCase = true) ||
            tag.contains("Local Pick", ignoreCase = true) ||
            tag.contains("Local gem", ignoreCase = true) ||
            tag.contains("Romantic", ignoreCase = true) ||
            tag.contains("Chef", ignoreCase = true) ||
            tag.contains("Late night", ignoreCase = true) -> DiscoverRestaurantCardBadge.RareFind

        tag.contains("new", ignoreCase = true) ||
            tag.contains("Sale", ignoreCase = true) ||
            tag.contains("opened", ignoreCase = true) -> DiscoverRestaurantCardBadge.New

        else -> null
    }
}
