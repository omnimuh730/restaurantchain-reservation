package com.mh.restaurantchainreservation.feature.booking

import com.mh.restaurantchainreservation.core.model.Restaurant
import kotlin.math.roundToInt

data class RestaurantExtendedData(
    val description: String,
    val address: String,
    val phone: String,
    val tags: List<String>,
    val closesAt: String,
    val deliveryTime: String,
)

data class ReviewEntry(
    val name: String,
    val publishedAtEpochMs: Long,
    val rating: Int,
    val taste: Int? = null,
    val ambience: Int? = null,
    val service: Int? = null,
    val value: Int? = null,
    val text: String,
)

data class MenuItem(
    val name: String,
    val description: String,
    val price: Int,
    val category: String,
    val imageUrl: String? = null,
)

object RestaurantDetailData {
    private val galleryExtras = listOf(
        "https://images.unsplash.com/photo-1552566626-52f8b828add9?w=1200&h=800&fit=crop",
        "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=1200&h=800&fit=crop",
        "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=1200&h=800&fit=crop",
    )

    fun galleryImages(restaurant: Restaurant): List<String> =
        listOf(restaurant.image) + galleryExtras

    fun extendedData(restaurant: Restaurant): RestaurantExtendedData {
        val idSeed = restaurant.id.hashCode().and(0x7FFFFFFF)
        val streetNum = (idSeed % 500) + 100
        val stNum = (idSeed % 80) + 10
        val phoneNum = (idSeed % 9000) + 1000
        val delivMin = (idSeed % 20) + 20
        val delivMax = delivMin + (idSeed % 15) + 10
        val descriptions = mapOf(
            "m1" to "Award-winning omakase experience with the freshest seasonal fish flown in daily from Tsukiji Market.",
            "m3" to "Classic French bistro cuisine with a modern twist, featuring seasonal ingredients.",
        )
        val tags = mapOf(
            "m1" to listOf("Sushi", "Omakase", "Fine Dining"),
            "m3" to listOf("Bistro", "Wine", "Brunch"),
        )
        return RestaurantExtendedData(
            description = descriptions[restaurant.id]
                ?: "Experience the best of ${restaurant.cuisine} at ${restaurant.name}. A perfect spot for any occasion.",
            address = "${streetNum} W ${stNum}th St",
            phone = "(212) 555-$phoneNum",
            tags = tags[restaurant.id] ?: listOf(restaurant.cuisine.split("·").first().trim(), "Dining"),
            closesAt = "23:00",
            deliveryTime = "$delivMin–$delivMax min",
        )
    }

    fun bookingPrice(restaurant: Restaurant): Int =
        (restaurant.rating * 50 + 80).roundToInt().coerceAtLeast(60)

    val reviews: List<ReviewEntry> = listOf(
        ReviewEntry("Matthew Y.", daysAgo(7), 5, 5, 5, 5, 4, "Absolutely incredible experience. The omakase was a journey through flavors I never knew existed."),
        ReviewEntry("Sarah L.", daysAgo(9), 5, 5, 4, 5, 4, "Best sushi in the city, hands down. The wagyu tataki melts in your mouth."),
        ReviewEntry("James K.", daysAgo(14), 4, 4, 5, 3, null, "Great food and ambiance. Service was a bit slow but the quality makes up for it."),
        ReviewEntry("Emily R.", daysAgo(21), 5, 5, 5, 4, 5, "We celebrated our anniversary here and it was perfect. Highly recommend the chef's omakase."),
        ReviewEntry("David M.", daysAgo(28), 4, 5, null, null, null, "Fantastic fresh fish. The uni bruschetta is a must-try appetizer."),
        ReviewEntry("Priya S.", daysAgo(28), 4, 4, null, null, 5, "Solid value for the quality. Would book again for a special night out."),
        ReviewEntry("Noah P.", daysAgo(35), 3, null, null, 3, null, "Food was good but we waited a long time between courses."),
        ReviewEntry("Grace H.", daysAgo(35), 5, 5, 5, null, null, "Stunning room and impeccable plating. Every bite felt intentional."),
        ReviewEntry("Oliver T.", daysAgo(35), 4, 4, 4, 4, 3, "Reliable favorite for client dinners. Consistent quality every visit."),
        ReviewEntry("Chloe D.", daysAgo(35), 5, 5, null, null, null, "The truffle toro nigiri alone is worth the trip."),
    )

    val menuItems: List<MenuItem> = listOf(
        MenuItem("Truffle Toro Nigiri", "Bluefin tuna belly with black truffle and gold leaf", 28, "Appetizers", "https://images.unsplash.com/photo-1700324828870-43027cba6d18?w=400&h=300&fit=crop"),
        MenuItem("Wagyu Tataki", "Seared A5 wagyu with ponzu and microgreens", 32, "Appetizers", "https://images.unsplash.com/photo-1697659206568-7a0148bc5482?w=400&h=300&fit=crop"),
        MenuItem("Uni Bruschetta", "Fresh sea urchin on crispy toast with yuzu", 24, "Appetizers", "https://images.unsplash.com/photo-1761095596656-7142a0600ecc?w=400&h=300&fit=crop"),
        MenuItem("Crispy Rice Spicy Tuna", "Crispy sushi rice with spicy tuna tartare", 19, "Appetizers"),
        MenuItem("Hamachi Jalapeno", "Yellowtail sashimi with jalapeno ponzu", 21, "Appetizers"),
        MenuItem("Shiso Shrimp Tempura", "Light battered shrimp wrapped in shiso", 18, "Appetizers"),
        MenuItem("King Crab Gyoza", "Pan-seared dumplings filled with king crab", 22, "Appetizers"),
        MenuItem("Agedashi Tofu", "Silken tofu with savory dashi broth", 12, "Appetizers"),
        MenuItem("Otoro Nigiri", "Premium fatty tuna nigiri", 14, "Nigiri"),
        MenuItem("Chef's Omakase", "12-piece seasonal selection by the chef", 85, "Main Course", "https://images.unsplash.com/photo-1607886098701-91274ad78cf9?w=400&h=300&fit=crop"),
        MenuItem("Lobster Risotto", "Creamy arborio rice with butter-poached lobster", 42, "Main Course", "https://images.unsplash.com/photo-1461009683693-342af2f2d6ce?w=400&h=300&fit=crop"),
        MenuItem("Miso Black Cod", "48-hour marinated cod with sweet miso glaze", 38, "Main Course", "https://images.unsplash.com/photo-1632420758649-a9bde11730ef?w=400&h=300&fit=crop"),
        MenuItem("Matcha Tiramisu", "Japanese-Italian fusion with mascarpone cream", 16, "Desserts", "https://images.unsplash.com/photo-1768165335825-c2552c6b2299?w=400&h=300&fit=crop"),
        MenuItem("Yuzu Sorbet", "Refreshing citrus sorbet with candied zest", 12, "Desserts", "https://images.unsplash.com/photo-1629245425377-b999b2a7d1ae?w=400&h=300&fit=crop"),
    )

    val menuCategories: List<String> = listOf(
        "Appetizers", "Nigiri", "Sashimi", "Rolls", "Main Course", "Noodles", "Desserts", "Beverages",
    )

    fun menuForCategory(category: String): List<MenuItem> =
        if (category == "Appetizers") {
            menuItems.filter { it.category == "Appetizers" }
        } else {
            menuItems.filter { it.category == category }.ifEmpty {
                menuItems.take(8)
            }
        }

    private fun daysAgo(days: Int): Long =
        System.currentTimeMillis() - days * 86_400_000L
}

fun formatRating(n: Double): String =
    if (n == n.toInt().toDouble()) n.toInt().toString() else n.toString()

fun formatReviewTimeAgo(publishedAtEpochMs: Long, nowEpochMs: Long = System.currentTimeMillis()): String {
    val minutes = ((nowEpochMs - publishedAtEpochMs).coerceAtLeast(0L)) / 60_000L
    return when {
        minutes < 60 -> "${minutes.coerceAtLeast(1)}m ago"
        minutes < 60 * 24 -> "${minutes / 60}h ago"
        minutes < 60 * 24 * 7 -> "${minutes / (60 * 24)}d ago"
        minutes < 60 * 24 * 30 -> "${minutes / (60 * 24 * 7)}w ago"
        else -> "${minutes / (60 * 24 * 30)}mo ago"
    }
}
