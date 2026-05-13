package com.mh.restaurantchainreservation.core.model

/**
 * Display-ready restaurant card model. Mirrors React `RestaurantData` from
 * `src/app/pages/detail/restaurantDetailData.ts` with the i18n labels already
 * resolved to English literals to avoid carrying string keys around the app.
 */
data class Restaurant(
    val id: String,
    val name: String,
    val cuisine: String,
    val rating: Double,
    val reviews: Int,
    val price: String,
    val distance: String,
    val image: String,
    val area: String? = null,
    val tag: String? = null,
)

/** Mock reservation slot for discover list cards (available vs crossed-out). */
data class RestaurantTimeSlot(
    val label: String,
    val available: Boolean,
)

data class FoodType(
    val id: String,
    val label: String,
    val image: String,
)

data class City(
    val id: String,
    val label: String,
    val image: String,
)

data class Banner(
    val id: String,
    val image: String,
    val title: String,
    val subtitle: String,
    val cta: String,
)

data class QuickCategory(
    val id: String,
    val label: String,
)

data class NewsItem(
    val id: String,
    val image: String,
    val title: String,
    val summary: String,
    val category: String,
    val readMinutes: Int,
)

/**
 * Static seed data lifted from React `discoverData.ts` and the matching
 * `i18n/locales/en/discoverData.json`. Kept on a single object so feature code
 * can reference `DiscoverData.MONTHLY_BEST` directly without repository plumbing.
 */
object DiscoverData {
    val BANNERS: List<Banner> = listOf(
        Banner(
            id = "1",
            image = "https://images.unsplash.com/photo-1616671285410-2a676a9a433d?w=800&h=400&fit=crop",
            title = "MICHELIN 2026",
            subtitle = "Finally Revealed",
            cta = "Discover it now on CATCHTABLE",
        ),
        Banner(
            id = "2",
            image = "https://images.unsplash.com/photo-1708388463997-7e4b5b94f120?w=800&h=400&fit=crop",
            title = "Essential K-BBQ",
            subtitle = "Sizzle, smoke, and juicy bites!",
            cta = "Find your favorite grill",
        ),
        Banner(
            id = "3",
            image = "https://images.unsplash.com/photo-1773188243397-29591fa09047?w=800&h=400&fit=crop",
            title = "Date Night",
            subtitle = "Curated Picks",
            cta = "Find the perfect spot for two",
        ),
        Banner(
            id = "4",
            image = "https://images.unsplash.com/photo-1775481132664-53ce2276d0ee?w=800&h=400&fit=crop",
            title = "Chef's Table",
            subtitle = "Exclusive Omakase",
            cta = "Reserved for CATCHTABLE members",
        ),
        Banner(
            id = "5",
            image = "https://images.unsplash.com/photo-1775144581014-bf6cb3ab9f06?w=800&h=400&fit=crop",
            title = "NEW YORK",
            subtitle = "Top 50 Restaurants",
            cta = "Explore the city's best dining scene",
        ),
        Banner(
            id = "6",
            image = "https://images.unsplash.com/photo-1763506240757-a8a33ca2c26f?w=800&h=400&fit=crop",
            title = "Sushi Masters",
            subtitle = "Best Omakase 2026",
            cta = "Hand-picked by our editors",
        ),
    )

    val QUICK_CATEGORIES: List<QuickCategory> = listOf(
        QuickCategory("trending", "Trending Now"),
        QuickCategory("catch-only", "Catch Only"),
        QuickCategory("top-ranking", "Top Ranking"),
        QuickCategory("hot-ny", "Hot in New York"),
        QuickCategory("best-kbbq", "Best K-BBQ"),
        QuickCategory("best-american", "Best American"),
        QuickCategory("local-fav", "Local Favorite"),
        QuickCategory("nearby-me", "Nearby Me"),
    )

    val CITIES: List<City> = listOf(
        City("sf", "San Francisco", "https://images.unsplash.com/photo-1660594507889-e723045367be?w=400&h=300&fit=crop"),
        City("ny", "New York", "https://images.unsplash.com/photo-1775144581014-bf6cb3ab9f06?w=400&h=300&fit=crop"),
        City("la", "Los Angeles", "https://images.unsplash.com/photo-1680627723996-5823e984e172?w=400&h=300&fit=crop"),
        City("seattle", "Seattle", "https://images.unsplash.com/photo-1589572546737-8377644cf951?w=400&h=300&fit=crop"),
    )

    val FOOD_TYPES: List<FoodType> = listOf(
        FoodType("grilled-beef", "Grilled Beef", "https://images.unsplash.com/photo-1678684279246-96e6afb970f2?w=400&h=300&fit=crop"),
        FoodType("grilled-pork", "Grilled Pork", "https://images.unsplash.com/photo-1526366411709-472085c8a586?w=400&h=300&fit=crop"),
        FoodType("bar-pub", "Bar & Pub", "https://images.unsplash.com/photo-1598990386084-8af4dd12b3b4?w=400&h=300&fit=crop"),
        FoodType("japanese", "Japanese", "https://images.unsplash.com/photo-1681270507609-e2a5f21969b0?w=400&h=300&fit=crop"),
        FoodType("italian", "Italian", "https://images.unsplash.com/photo-1762922425306-ef64664f6e4d?w=400&h=300&fit=crop"),
        FoodType("brunch", "Brunch", "https://images.unsplash.com/photo-1687276287139-88f7333c8ca4?w=400&h=300&fit=crop"),
        FoodType("thai", "Thai", "https://images.unsplash.com/photo-1675150303909-1bb94e33132f?w=400&h=300&fit=crop"),
        FoodType("french", "French", "https://images.unsplash.com/photo-1657502996869-6ccd568b9d41?w=400&h=300&fit=crop"),
        FoodType("chinese", "Chinese", "https://images.unsplash.com/photo-1694834589398-27b369c6f7a6?w=400&h=300&fit=crop"),
        FoodType("healthy", "Healthy", "https://images.unsplash.com/photo-1692780941487-505d5d908aa6?w=400&h=300&fit=crop"),
    )

    val MONTHLY_BEST: List<Restaurant> = listOf(
        Restaurant(
            id = "m1", name = "Ilpyeon Sirloin Hongik", cuisine = "Grilled Beef",
            rating = 4.7, reviews = 1850, price = "$$$", distance = "1.1 mi",
            image = "https://images.unsplash.com/photo-1678684279246-96e6afb970f2?w=400&h=300&fit=crop",
            area = "Hongik Univ", tag = "Monthly Best",
        ),
        Restaurant(
            id = "m2", name = "Gebangsikdang Sinsa", cuisine = "Korean",
            rating = 4.5, reviews = 1320, price = "$$", distance = "0.9 mi",
            image = "https://images.unsplash.com/photo-1590189599125-67138c6509ef?w=400&h=300&fit=crop",
            area = "Seongsu", tag = "Monthly Best",
        ),
        Restaurant(
            id = "m3", name = "Le Bouchon Moderne", cuisine = "French",
            rating = 4.8, reviews = 2105, price = "$$$$", distance = "0.6 mi",
            image = "https://images.unsplash.com/photo-1657502996869-6ccd568b9d41?w=400&h=300&fit=crop",
            area = "Downtown", tag = "Monthly Best",
        ),
        Restaurant(
            id = "m4", name = "Omakase Serenity", cuisine = "Japanese",
            rating = 4.9, reviews = 980, price = "$$$$", distance = "1.4 mi",
            image = "https://images.unsplash.com/photo-1681270507609-e2a5f21969b0?w=400&h=300&fit=crop",
            area = "Midtown", tag = "Monthly Best",
        ),
    )

    val LOVED_BY_LOCALS: List<Restaurant> = listOf(
        Restaurant(
            id = "l1", name = "Morning Harvest Café", cuisine = "Brunch · Café",
            rating = 4.6, reviews = 1230, price = "$$", distance = "0.3 mi",
            image = "https://images.unsplash.com/photo-1687276287139-88f7333c8ca4?w=400&h=300&fit=crop",
            tag = "Popular",
        ),
        Restaurant(
            id = "l2", name = "Golden Dragon Palace", cuisine = "Chinese · Dim Sum",
            rating = 4.5, reviews = 987, price = "$$", distance = "0.8 mi",
            image = "https://images.unsplash.com/photo-1694834589398-27b369c6f7a6?w=400&h=300&fit=crop",
            tag = "Local Pick",
        ),
        Restaurant(
            id = "l3", name = "Sakura Garden", cuisine = "Japanese · Ramen",
            rating = 4.7, reviews = 1456, price = "$$$", distance = "0.5 mi",
            image = "https://images.unsplash.com/photo-1731460202531-bf8389d565f7?w=400&h=300&fit=crop",
            tag = "Must Try",
        ),
    )

    val VIRAL: List<Restaurant> = listOf(
        Restaurant(
            id = "v1", name = "Neon Bites", cuisine = "Fusion · Street Food",
            rating = 4.4, reviews = 2300, price = "$", distance = "1.2 mi",
            image = "https://images.unsplash.com/photo-1564759319376-a60b400ced8e?w=400&h=300&fit=crop",
            tag = "2.3M views",
        ),
        Restaurant(
            id = "v2", name = "The Sweet Spot", cuisine = "Dessert · Pastry",
            rating = 4.6, reviews = 1800, price = "$$", distance = "0.7 mi",
            image = "https://images.unsplash.com/photo-1753722157947-8a50f04a9309?w=400&h=300&fit=crop",
            tag = "1.8M views",
        ),
        Restaurant(
            id = "v3", name = "K-BBQ King", cuisine = "Korean · BBQ",
            rating = 4.8, reviews = 3100, price = "$$$", distance = "1.5 mi",
            image = "https://images.unsplash.com/photo-1590189599125-67138c6509ef?w=400&h=300&fit=crop",
            tag = "3.1M views",
        ),
    )

    val DATE_NIGHT: List<Restaurant> = listOf(
        Restaurant(
            id = "d1", name = "Candlelit Terrace", cuisine = "Mediterranean",
            rating = 4.9, reviews = 1100, price = "$$$$", distance = "1.0 mi",
            image = "https://images.unsplash.com/photo-1773188243397-29591fa09047?w=400&h=300&fit=crop",
            tag = "Romantic",
        ),
        Restaurant(
            id = "d2", name = "Wine & Whisper", cuisine = "Wine Bar · French",
            rating = 4.7, reviews = 850, price = "$$$", distance = "0.6 mi",
            image = "https://images.unsplash.com/photo-1763867641400-96b9cccdbf7d?w=400&h=300&fit=crop",
            tag = "Romantic",
        ),
        Restaurant(
            id = "d3", name = "Skyline Rooftop", cuisine = "Contemporary",
            rating = 4.8, reviews = 1250, price = "$$$$", distance = "1.8 mi",
            image = "https://images.unsplash.com/photo-1768397003905-a202ea6325f5?w=400&h=300&fit=crop",
            tag = "Romantic",
        ),
    )

    /**
     * Deterministic extended mock catalog (~187 entries) so [ALL] reaches ~200 restaurants
     * while keeping curated rails (monthly best, locals, etc.) unchanged.
     */
    private fun buildExtendedRestaurantCatalog(): List<Restaurant> {
        val first = listOf(
            "Neon", "Copper", "Silver", "Golden", "Urban", "Harbor", "Garden", "Velvet", "Maple", "Cedar",
            "Stone", "Azure", "Crimson", "Ivory", "Jade", "Lotus", "Olive", "Pepper", "Saffron", "Sesame",
            "Willow", "Birch", "Cypress", "Elm", "Hazel", "Laurel", "Magnolia", "Rowan", "Sage", "Wisteria",
        )
        val second = listOf(
            "Fork", "Spoon", "Kitchen", "House", "Table", "Corner", "Bistro", "Grill", "Noodle", "Taco",
            "Bowl", "Plate", "Pantry", "Cellar", "Roof", "Garden", "Yard", "Alley", "Lane", "Market",
            "Hall", "Room", "Den", "Parlor", "Study", "Studio", "Atelier", "Counter", "Bar", "Lounge",
        )
        val cuisines = listOf(
            "Korean", "Japanese", "Italian", "Mexican", "Vietnamese", "Thai", "French", "Chinese", "Indian",
            "Mediterranean", "Spanish", "Greek", "Turkish", "Peruvian", "Lebanese", "Ethiopian", "American",
            "BBQ", "Seafood", "Steakhouse", "Vegan", "Brunch · Café", "Fusion", "Polish", "German", "British",
            "Irish", "Moroccan", "Brazilian", "Argentine", "Filipino", "Indonesian", "Malaysian", "Singaporean",
        )
        val areas = listOf(
            "Downtown", "Midtown", "SoHo", "West Loop", "Seongsu", "Hongdae", "Gangnam", "Pike Place",
            "Mission", "Brooklyn", "West Village", "Arts District", "Nob Hill", "Capitol Hill", "Fremont",
            "Bucktown", "Lincoln Park", "Echo Park", "Silver Lake", "Hayes Valley", "Belltown", "Koreatown",
        )
        val prices = listOf("$", "$$", "$$$", "$$$$")
        val tags = listOf<String?>(null, "New", "Sale", "Popular", "Local Pick")
        return (1..187).map { n ->
            val i = n - 1
            val name = "${first[(i + n) % first.size]} ${second[(i * 3 + n * 5) % second.size]}"
            val cuisine = cuisines[i % cuisines.size]
            val rating = 3.8 + (i % 12) * 0.1
            val reviews = 180 + (i * 47) % 9800
            val price = prices[i % prices.size]
            val distanceMi = ((i % 25) + 1) * 0.1 + (i % 3) * 0.03
            val distance = "%.1f mi".format(distanceMi)
            // Deterministic unique image per row (avoids repeating the same small Unsplash set).
            val image = "https://picsum.photos/seed/restaurantchain-c%03d/400/300".format(n)
            Restaurant(
                id = "c%03d".format(n),
                name = name,
                cuisine = cuisine,
                rating = rating,
                reviews = reviews,
                price = price,
                distance = distance,
                image = image,
                area = areas[i % areas.size],
                tag = tags[i % tags.size],
            )
        }
    }

    /** Combined catalog used for search / detail lookups (~200 restaurants). */
    val ALL: List<Restaurant> by lazy {
        (MONTHLY_BEST + LOVED_BY_LOCALS + VIRAL + DATE_NIGHT + buildExtendedRestaurantCatalog()).distinctBy { it.id }
    }

    fun findById(id: String): Restaurant? = ALL.firstOrNull { it.id == id }

    /** Restaurants associated with a given quick category id. Returns a fallback subset if no specific match. */
    fun byCategory(id: String): List<Restaurant> = when (id) {
        "trending" -> MONTHLY_BEST + VIRAL.take(2)
        "catch-only" -> MONTHLY_BEST.take(2) + DATE_NIGHT.take(1)
        "top-ranking" -> MONTHLY_BEST
        "hot-ny" -> LOVED_BY_LOCALS
        "best-kbbq" -> listOf(VIRAL.last(), MONTHLY_BEST.first())
        "best-american" -> DATE_NIGHT.take(2) + LOVED_BY_LOCALS.first()
        "local-fav" -> LOVED_BY_LOCALS
        "nearby-me" -> ALL.sortedBy { it.distance }.take(5)
        else -> ALL
    }

    fun byFoodType(id: String): List<Restaurant> {
        val cuisineNeedle = FOOD_TYPES.firstOrNull { it.id == id }?.label?.lowercase()?.split(" ")?.firstOrNull()
        if (cuisineNeedle.isNullOrBlank()) return ALL
        return ALL.filter { it.cuisine.lowercase().contains(cuisineNeedle) }.ifEmpty { ALL.take(4) }
    }

    fun byCity(id: String): List<Restaurant> {
        // No city tagging on individual restaurants — just slice the catalog deterministically.
        val seed = id.hashCode()
        return ALL.shuffled(kotlin.random.Random(seed)).take(5)
    }

    fun bySection(id: String): List<Restaurant> = when (id) {
        "monthly-best" -> MONTHLY_BEST
        "loved-by-locals" -> LOVED_BY_LOCALS
        "viral" -> VIRAL
        "date-night" -> DATE_NIGHT
        else -> ALL
    }

    fun trendingSearches(): List<String> = listOf(
        "K-BBQ", "Omakase", "Brunch", "Date night", "Rooftop", "Dim sum",
    )
}

fun mockNews(): List<NewsItem> = listOf(
    NewsItem(
        id = "n1",
        image = "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=600&h=400&fit=crop",
        title = "5 Cozy Spots for a Rainy Date Night",
        summary = "Editor-picked restaurants with candlelight, banquettes, and patient service.",
        category = "Date Night",
        readMinutes = 4,
    ),
    NewsItem(
        id = "n2",
        image = "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=600&h=400&fit=crop",
        title = "Inside the Michelin 2026 Ceremony",
        summary = "Three new two-stars and a wave of fresh one-stars across the city.",
        category = "News",
        readMinutes = 6,
    ),
    NewsItem(
        id = "n3",
        image = "https://images.unsplash.com/photo-1552566626-52f8b828add9?w=600&h=400&fit=crop",
        title = "How K-BBQ Took Over Friday Night",
        summary = "Late nights, sticky tongs, and the obsession that keeps booking apps busy.",
        category = "Trends",
        readMinutes = 5,
    ),
    NewsItem(
        id = "n4",
        image = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=600&h=400&fit=crop",
        title = "Counter Seats: Why Omakase Owns the Wishlist",
        summary = "Twenty courses, one chef, and the most-saved listings on Tonight.",
        category = "Editor",
        readMinutes = 7,
    ),
    NewsItem(
        id = "n5",
        image = "https://images.unsplash.com/photo-1481833761820-0509d3217039?w=600&h=400&fit=crop",
        title = "Brunch Crawl: 6 Stops Under $40",
        summary = "From flaky croissants to a final espresso — a Saturday morning, mapped.",
        category = "Guides",
        readMinutes = 3,
    ),
)
