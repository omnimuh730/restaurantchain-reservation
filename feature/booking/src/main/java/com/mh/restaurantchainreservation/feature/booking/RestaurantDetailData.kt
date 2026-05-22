package com.mh.restaurantchainreservation.feature.booking

import com.mh.restaurantchainreservation.core.model.Restaurant
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

data class RestaurantNextDayOff(
    val dateLabel: String,
    val dayName: String,
)

data class RestaurantPlaceOffers(
    val cuisineChips: List<PlaceOfferChip>,
    val hoursChips: List<PlaceOfferChip>,
    val paymentChips: List<PlaceOfferChip>,
    val amenityChips: List<PlaceOfferChip>,
)

data class RestaurantExtendedData(
    val description: String,
    val address: String,
    val phone: String,
    val phone2: String,
    val tags: List<String>,
    val closesAt: String,
    val deliveryTime: String,
    val openFrom: String,
    val openUntil: String,
    val availableNow: Boolean,
    val placeOffers: RestaurantPlaceOffers,
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
    val stayLabel: String = "",
    /** When set, shown in the carousel as a decimal score (e.g. 4.5); otherwise `rating.0`. */
    val ratingScore: Double? = null,
)

data class MenuItem(
    val name: String,
    val description: String,
    val price: Int,
    val category: String,
    val imageUrl: String? = null,
)

object RestaurantDetailData {
    private val weekDays = listOf(
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
        "Sunday",
    )

    private val amenityChipPool = listOf(
        "Outdoor seating" to AmenityIconType.Terrace,
        "Private dining room" to AmenityIconType.PrivateDining,
        "Street parking nearby" to AmenityIconType.Parking,
        "Wheelchair-accessible entrance" to AmenityIconType.Wheelchair,
        "Full bar" to AmenityIconType.FullBar,
        "Vegan options" to AmenityIconType.Vegan,
        "Reservations recommended" to AmenityIconType.Reservations,
        "Takeout available" to AmenityIconType.Takeout,
    )

    private val servicePeriods = listOf("10am – 12pm", "12pm – 10pm")

    private fun openFromLabel(): String =
        servicePeriods.first().substringBefore('–').trim()

    private fun openUntilLabel(): String =
        servicePeriods.last().substringAfter('–').trim()

    private val paymentChips = listOf(
        PlaceOfferChip("Foreign", AmenityIconType.Cash),
        PlaceOfferChip("National", AmenityIconType.Cash),
        PlaceOfferChip("PayPal", AmenityIconType.CardPayment),
        PlaceOfferChip("Payoneer", AmenityIconType.CardPayment),
    )

    private fun calendarDayForIndex(index: Int): Int = when (index) {
        0 -> Calendar.MONDAY
        1 -> Calendar.TUESDAY
        2 -> Calendar.WEDNESDAY
        3 -> Calendar.THURSDAY
        4 -> Calendar.FRIDAY
        5 -> Calendar.SATURDAY
        else -> Calendar.SUNDAY
    }

    private fun nextDayOff(seed: Int): RestaurantNextDayOff {
        val dayOffIndex = seed % 7
        val targetDay = calendarDayForIndex(dayOffIndex)
        val cal = Calendar.getInstance(Locale.US).apply { add(Calendar.DAY_OF_YEAR, 1) }
        while (cal.get(Calendar.DAY_OF_WEEK) != targetDay) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        val dateLabel = SimpleDateFormat("MM/dd", Locale.US).format(cal.time)
        return RestaurantNextDayOff(dateLabel = dateLabel, dayName = weekDays[dayOffIndex])
    }

    private fun cuisineChips(restaurant: Restaurant, seed: Int): List<PlaceOfferChip> {
        val fromCuisine = restaurant.cuisine
            .split("·", ",", "/")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        val labels = fromCuisine.toMutableList()
        if (labels.none { it.contains("vegan", ignoreCase = true) } && seed % 3 != 1) {
            labels.add("Vegan options")
        }
        if (labels.size < 2) {
            labels.add(restaurant.cuisine.trim())
        }
        return labels.distinct().take(4).map { PlaceOfferChip(label = it) }
    }

    private fun amenityChips(seed: Int): List<PlaceOfferChip> {
        val connectivity = PlaceOfferChip(
            label = "Data connection available",
            icon = AmenityIconType.DataConnection,
        )
        val preferred = listOf("Full bar", "Reservations recommended")
        val preferredChips = amenityChipPool
            .filter { (label, _) -> label in preferred }
            .map { (label, icon) -> PlaceOfferChip(label = label, icon = icon) }
        val extras = amenityChipPool
            .filter { (label, _) -> label !in preferred }
            .sortedBy { (label, _) -> (label.hashCode() + seed) % amenityChipPool.size }
            .take(seed % 2)
            .map { (label, icon) -> PlaceOfferChip(label = label, icon = icon) }
        return listOf(connectivity) + preferredChips + extras
    }

    private fun isAvailableNow(): Boolean {
        val hour = Calendar.getInstance(Locale.US).get(Calendar.HOUR_OF_DAY)
        return hour in 10..21
    }

    private fun hoursChips(seed: Int): List<PlaceOfferChip> {
        val dayOff = nextDayOff(seed)
        return buildList {
            if (isAvailableNow()) add(PlaceOfferChip("Available now"))
            add(PlaceOfferChip("Today: ${servicePeriods.joinToString(", ")}"))
            add(PlaceOfferChip("Next day off: ${dayOff.dateLabel} ${dayOff.dayName}"))
        }
    }

    private fun placeOffers(restaurant: Restaurant, seed: Int): RestaurantPlaceOffers =
        RestaurantPlaceOffers(
            cuisineChips = cuisineChips(restaurant, seed),
            hoursChips = hoursChips(seed),
            paymentChips = paymentChips,
            amenityChips = amenityChips(seed),
        )

    private val galleryExtras = listOf(
        "https://images.unsplash.com/photo-1552566626-52f8b828add9?w=1200&h=800&fit=crop",
        "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=1200&h=800&fit=crop",
        "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=1200&h=800&fit=crop",
    )

    fun galleryImages(restaurant: Restaurant): List<String> =
        listOf(restaurant.image) + galleryExtras

    private val mapCoordinates = listOf(
        37.7920 to -122.4120,
        37.7870 to -122.4180,
        37.7810 to -122.4050,
        37.7890 to -122.3990,
        37.7760 to -122.4140,
        37.7940 to -122.3950,
        37.7960 to -122.4060,
        37.7800 to -122.4200,
        37.7830 to -122.4010,
        37.7950 to -122.4100,
        37.7780 to -122.3970,
        37.7910 to -122.4160,
        37.7870 to -122.4030,
        37.7840 to -122.4190,
        37.7920 to -122.3980,
        37.7770 to -122.4080,
        37.7980 to -122.4150,
        37.7750 to -122.4020,
        37.7860 to -122.4210,
        37.7850 to -122.3960,
    )

    fun mapCoordinate(restaurant: Restaurant): Pair<Double, Double> {
        val numeric = restaurant.id.filter { it.isDigit() }.toIntOrNull()
        val seed = if (numeric != null && numeric > 0) {
            numeric
        } else {
            restaurant.id.hashCode().and(0x7FFFFFFF)
        }
        val index = (seed - 1).coerceAtLeast(0) % mapCoordinates.size
        return mapCoordinates[index]
    }

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
        val closesAt = openUntilLabel()
        val availableNow = isAvailableNow()
        return RestaurantExtendedData(
            description = descriptions[restaurant.id]
                ?: "Experience the best of ${restaurant.cuisine} at ${restaurant.name}. A perfect spot for any occasion.",
            address = "${streetNum} W ${stNum}th St",
            phone = "(212) 555-$phoneNum",
            phone2 = "(415) 555-${((phoneNum + 137) % 9000) + 1000}",
            tags = tags[restaurant.id] ?: listOf(restaurant.cuisine.split("·").first().trim(), "Dining"),
            closesAt = closesAt,
            deliveryTime = "$delivMin–$delivMax min",
            openFrom = openFromLabel(),
            openUntil = openUntilLabel(),
            availableNow = availableNow,
            placeOffers = placeOffers(restaurant, idSeed),
        )
    }

    fun bookingPrice(restaurant: Restaurant): Int =
        (restaurant.rating * 50 + 80).roundToInt().coerceAtLeast(60)

    val carouselReviews: List<ReviewEntry> = listOf(
        ReviewEntry(
            name = "Margarida",
            publishedAtEpochMs = daysAgo(22),
            rating = 5,
            text = "Comfortable. Quiet. Just like home. The staff were kind. Good communication. Fresh linens. Would return.",
        ),
        ReviewEntry(
            name = "Patrick",
            publishedAtEpochMs = daysAgo(18),
            rating = 5,
            stayLabel = "Deluxe suite",
            text = "Great place to stay. However, the hosts forgot some of the furniture when we entered. Nice neighborhood. The bedroom is spacious. The sofa bed is comfortable. We didn't have any issues during the stay. Great value!",
        ),
        ReviewEntry(
            name = "Rachel",
            publishedAtEpochMs = daysAgo(24),
            rating = 5,
            ratingScore = 4.5,
            stayLabel = "Premier King suite with balcony",
            text = "Wow. Great. Worth it. Central, clean and stylish. Also liked the slides. The host was very communicative. Would stay here again. Easy check in.",
        ),
        ReviewEntry(
            name = "Chadae",
            publishedAtEpochMs = daysAgo(31),
            rating = 5,
            stayLabel = "Premier king suite with balcony",
            text = "Wow everything was exactly perfect the building the unit the valet and the host amazing Five star service. Short walk to everything.",
        ),
        ReviewEntry(
            name = "Daniel",
            publishedAtEpochMs = daysAgo(38),
            rating = 4,
            ratingScore = 4.5,
            stayLabel = "Studio with city view",
            text = "Check-in was smooth and the room matched the photos. A few small maintenance items but nothing that ruined the trip. Location made up for it — we walked everywhere.",
        ),
        ReviewEntry(
            name = "Aisha",
            publishedAtEpochMs = daysAgo(45),
            rating = 5,
            stayLabel = "Penthouse dining room",
            text = "Stunning. Memorable. Every course surprised us. Service was warm without being intrusive. Book ahead.",
        ),
    )

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
        ReviewEntry("Ethan B.", daysAgo(42), 4, null, 5, null, null, "Beautiful dining room and attentive staff throughout the evening."),
        ReviewEntry("Ava W.", daysAgo(42), 3, 4, null, null, 2, "Good flavors but portions felt small for the price point."),
        ReviewEntry("Lucas M.", daysAgo(49), 5, 5, 5, 5, 4, "Every course was thoughtfully prepared. A memorable omakase experience."),
        ReviewEntry("Mia F.", daysAgo(49), 4, 5, null, 3, null, "Fish quality was excellent. Service pacing could be smoother."),
        ReviewEntry("Leo G.", daysAgo(56), 5, 5, 5, 5, 5, "Flawless from start to finish. Will return for special occasions."),
        ReviewEntry("Zoe N.", daysAgo(56), 2, null, null, 2, null, "Our table waited a long time and the room was louder than expected."),
        ReviewEntry("Henry C.", daysAgo(63), 4, null, null, null, 4, "Fair prices for the quality. The miso cod was outstanding."),
        ReviewEntry("Isla J.", daysAgo(63), 5, null, 5, 5, null, "Impeccable service and a calm, elegant atmosphere."),
        ReviewEntry("Mason V.", daysAgo(70), 4, 4, 4, 4, 3, "Consistent quality on repeat visits. Great for business dinners."),
        ReviewEntry("Aria K.", daysAgo(70), 5, 5, null, null, null, "The chef's selection never disappoints."),
        ReviewEntry("Caleb Z.", daysAgo(77), 3, 3, 4, 3, 3, "Decent meal overall, though a few dishes were under-seasoned."),
        ReviewEntry("Ruby E.", daysAgo(77), 5, 5, 5, 5, null, "Top-tier sushi and warm hospitality. Highly recommend."),
        ReviewEntry("Finn O.", daysAgo(84), 4, null, null, 5, null, "Service team was fantastic and very knowledgeable about the menu."),
        ReviewEntry("Sophia Q.", daysAgo(84), 4, 5, null, null, 3, "Loved the nigiri selection. Dessert was just okay."),
        ReviewEntry("Jack H.", daysAgo(91), 5, 5, 5, 4, 4, "One of our favorite spots in the neighborhood."),
        ReviewEntry("Layla R.", daysAgo(91), 3, null, 3, null, null, "Food was fine but the ambience felt rushed on a busy night."),
        ReviewEntry("Tobias W.", daysAgo(98), 4, 4, null, 4, null, "Reliable quality and friendly staff every time."),
        ReviewEntry("Nora P.", daysAgo(98), 5, 5, 5, 5, 5, "Exceptional in every category. Worth planning ahead to book."),
        ReviewEntry("Owen L.", daysAgo(105), 4, 4, 5, null, null, "Great atmosphere for date night. Would come back."),
        ReviewEntry("Eva S.", daysAgo(105), 5, 5, null, 5, null, "Fresh fish and thoughtful presentation on every plate."),
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
        MenuItem("Salmon Sashimi", "Fresh Atlantic salmon, 5 pieces", 26, "Sashimi", "https://images.unsplash.com/photo-1553621042-f6e147245757?w=400&h=300&fit=crop"),
        MenuItem("Dragon Roll", "Eel, avocado, and sweet sauce", 24, "Rolls", "https://images.unsplash.com/photo-1579584425555-c3ce17fd1871?w=400&h=300&fit=crop"),
        MenuItem("Spicy Tuna Roll", "Spicy tuna with cucumber and sesame", 18, "Rolls", "https://images.unsplash.com/photo-1617196034796-aa9c788a6a88?w=400&h=300&fit=crop"),
        MenuItem("Edamame", "Steamed soybeans with sea salt", 8, "Appetizers", "https://images.unsplash.com/photo-1609501678979-5e6b7c5c8f8a?w=400&h=300&fit=crop"),
        MenuItem("Ramen Bowl", "Rich tonkotsu broth with chashu", 22, "Noodles", "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=400&h=300&fit=crop"),
        MenuItem("Green Tea Ice Cream", "House-made matcha ice cream", 10, "Desserts", "https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=400&h=300&fit=crop"),
    )

    /** Distinct image URLs for the popular-menu carousel and fullscreen gallery. */
    fun popularMenuImages(): List<String> = menuItems.mapNotNull { it.imageUrl }.distinct()

    /** Menu items with images for the popular-menu rail (name + category + price). */
    fun popularMenuPreviewItems(limit: Int = 6): List<MenuItem> =
        menuItems
            .filter { it.imageUrl != null }
            .distinctBy { it.imageUrl }
            .take(limit)

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

fun detailHeaderLocationLine(restaurant: Restaurant, ext: RestaurantExtendedData): String {
    val location = restaurant.area?.takeIf { it.isNotBlank() } ?: ext.address
    return "${restaurant.cuisine} restaurant in $location"
}

fun detailHeaderHoursLine(restaurant: Restaurant, ext: RestaurantExtendedData): String {
    val availability = if (ext.availableNow) "Available now" else "Closed now"
    return "${restaurant.price} · From ${ext.openFrom} · Until ${ext.openUntil} · $availability"
}

fun formatRating(n: Double): String =
    if (n == n.toInt().toDouble()) n.toInt().toString() else n.toString()

fun formatReviewCarouselScore(review: ReviewEntry): String {
    val score = review.ratingScore ?: review.rating.toDouble()
    return String.format(Locale.US, "%.1f", score)
}

fun formatReviewMonthYear(publishedAtEpochMs: Long): String =
    SimpleDateFormat("MMMM yyyy", Locale.US).format(Date(publishedAtEpochMs))

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
