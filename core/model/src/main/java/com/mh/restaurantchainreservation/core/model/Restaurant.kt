package com.mh.restaurantchainreservation.core.model

/** Airbnb-style guest favorite tier for laurel badge artwork. */
enum class GuestFavoriteLevel {
    /** No laurel badge. */
    None,
    /** Standard guest favorite — black laurel (`leaf`). */
    Normal,
    /** Top-tier guest favorite — golden laurel (`goldenleaf`). */
    High,
}

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
    val guestFavoriteLevel: GuestFavoriteLevel = GuestFavoriteLevel.None,
)

/** Derives a guest-favorite tier from rating, review volume, and marketing tags. */
fun deriveGuestFavoriteLevel(
    rating: Double,
    reviews: Int,
    tag: String? = null,
): GuestFavoriteLevel = when {
    rating >= 4.75 && reviews >= 800 -> GuestFavoriteLevel.High
    rating >= 4.5 && reviews >= 350 -> GuestFavoriteLevel.Normal
    tag?.contains("favorite", ignoreCase = true) == true -> GuestFavoriteLevel.Normal
    tag?.contains("Monthly Best", ignoreCase = true) == true && rating >= 4.65 ->
        GuestFavoriteLevel.High
    tag?.contains("Romantic", ignoreCase = true) == true && rating >= 4.7 ->
        GuestFavoriteLevel.High
    else -> GuestFavoriteLevel.None
}

fun Restaurant.withDerivedGuestFavoriteLevel(): Restaurant =
    if (guestFavoriteLevel != GuestFavoriteLevel.None) {
        this
    } else {
        copy(guestFavoriteLevel = deriveGuestFavoriteLevel(rating, reviews, tag))
    }

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

/**
 * Static seed data lifted from React `discoverData.ts` and the matching
 * `i18n/locales/en/discoverData.json`. Kept on a single object so feature code
 * can reference `DiscoverData.MONTHLY_BEST` directly without repository plumbing.
 */
object DiscoverData {
    val BANNERS: List<Banner> = listOf(
        Banner(
            id = "michelin",
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
        QuickCategory("catch-only", "Best Noodles"),
        QuickCategory("top-ranking", "Top Ranking"),
        QuickCategory("hot-ny", "Foreign Foods"),
        QuickCategory("best-kbbq", "Best BBQ"),
        QuickCategory("best-american", "Best Fast Food"),
        QuickCategory("local-fav", "Local Favorite"),
        QuickCategory("nearby-me", "Nearby Me"),
    )

    val CITIES: List<City> = listOf(
        City("sf", "San Francisco", "https://images.unsplash.com/photo-1660594507889-e723045367be?w=400&h=300&fit=crop"),
        City("ny", "New York", "https://images.unsplash.com/photo-1775144581014-bf6cb3ab9f06?w=400&h=300&fit=crop"),
        City("la", "Los Angeles", "https://images.unsplash.com/photo-1680627723996-5823e984e172?w=400&h=300&fit=crop"),
        City("seattle", "Seattle", "https://images.unsplash.com/photo-1589572546737-8377644cf951?w=400&h=300&fit=crop"),
        City("chicago", "Chicago", "https://images.unsplash.com/photo-1494522358652-f30e61a603d5?w=400&h=300&fit=crop"),
        City("boston", "Boston", "https://images.unsplash.com/photo-1506197067357-23c07593a9aa?w=400&h=300&fit=crop"),
        City("miami", "Miami", "https://images.unsplash.com/photo-1505118380757-91f5f5632de0?w=400&h=300&fit=crop"),
        City("austin", "Austin", "https://images.unsplash.com/photo-1531218150217-54595bc2b79d?w=400&h=300&fit=crop"),
        City("denver", "Denver", "https://images.unsplash.com/photo-1546156929-a1951014851e?w=400&h=300&fit=crop"),
        City("portland", "Portland", "https://images.unsplash.com/photo-1541452880569-b95c1a5d8542?w=400&h=300&fit=crop"),
        City("vancouver", "Vancouver", "https://images.unsplash.com/photo-1559511260-66a654ae982a?w=400&h=300&fit=crop"),
        City("toronto", "Toronto", "https://images.unsplash.com/photo-1517935706615-2717063c2225?w=400&h=300&fit=crop"),
        City("montreal", "Montreal", "https://images.unsplash.com/photo-1519178616552-771609cc6686?w=400&h=300&fit=crop"),
        City("london", "London", "https://images.unsplash.com/photo-1513635269973-596154e42ae7?w=400&h=300&fit=crop"),
        City("paris", "Paris", "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=400&h=300&fit=crop"),
        City("barcelona", "Barcelona", "https://images.unsplash.com/photo-1583422409516-2895a77efded?w=400&h=300&fit=crop"),
        City("berlin", "Berlin", "https://images.unsplash.com/photo-1560969184-10fe8719e047?w=400&h=300&fit=crop"),
        City("amsterdam", "Amsterdam", "https://images.unsplash.com/photo-1534353436294-0dbd4bdac845?w=400&h=300&fit=crop"),
        City("tokyo", "Tokyo", "https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?w=400&h=300&fit=crop"),
        City("seoul", "Seoul", "https://images.unsplash.com/photo-1517154421773-0529f29ea451?w=400&h=300&fit=crop"),
        City("singapore", "Singapore", "https://images.unsplash.com/photo-1525625293386-3f8f99389edd?w=400&h=300&fit=crop"),
        City("hong-kong", "Hong Kong", "https://images.unsplash.com/photo-1536599018102-9f803c140fc1?w=400&h=300&fit=crop"),
        City("sydney", "Sydney", "https://images.unsplash.com/photo-1506973035872-a4ec16b8e8d9?w=400&h=300&fit=crop"),
        City("melbourne", "Melbourne", "https://images.unsplash.com/photo-1514395462725-fb4566210144?w=400&h=300&fit=crop"),
        City("dublin", "Dublin", "https://images.unsplash.com/photo-1590089419295-328f9a0f1a8a?w=400&h=300&fit=crop"),
        City("honolulu", "Honolulu", "https://images.unsplash.com/photo-1542259675220-9ba045d2a3d5?w=400&h=300&fit=crop"),
        City("las-vegas", "Las Vegas", "https://images.unsplash.com/photo-1605833556227-1da40a469196?w=400&h=300&fit=crop"),
        City("nashville", "Nashville", "https://images.unsplash.com/photo-1546412414-e1885e7e5c36?w=400&h=300&fit=crop"),
        City("philadelphia", "Philadelphia", "https://images.unsplash.com/photo-1569767106989-64d9526a9e9a?w=400&h=300&fit=crop"),
        City("washington-dc", "Washington, D.C.", "https://images.unsplash.com/photo-1617581629397-a72507c3de9e?w=400&h=300&fit=crop"),
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
        FoodType("mexican", "Mexican", "https://images.unsplash.com/photo-1565299585323-38174cb3b886?w=400&h=300&fit=crop"),
        FoodType("indian", "Indian", "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=400&h=300&fit=crop"),
        FoodType("vietnamese", "Vietnamese", "https://images.unsplash.com/photo-1582878826628-29b7a1c89db4?w=400&h=300&fit=crop"),
        FoodType("spanish", "Spanish", "https://images.unsplash.com/photo-1624300629298-e9de39c13be5?w=400&h=300&fit=crop"),
        FoodType("greek", "Greek", "https://images.unsplash.com/photo-1608219992759-8d74ed8d76eb?w=400&h=300&fit=crop"),
        FoodType("middle-eastern", "Middle Eastern", "https://images.unsplash.com/photo-1541519227354-08fa5d50c44d?w=400&h=300&fit=crop"),
        FoodType("steakhouse", "Steakhouse", "https://images.unsplash.com/photo-1600891964092-4316c288032e?w=400&h=300&fit=crop"),
        FoodType("seafood", "Seafood", "https://images.unsplash.com/photo-1559339352-11d035aa65de?w=400&h=300&fit=crop"),
        FoodType("pizza", "Pizza", "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400&h=300&fit=crop"),
        FoodType("burgers", "Burgers", "https://images.unsplash.com/photo-1550547660-d9450f859349?w=400&h=300&fit=crop"),
        FoodType("vegan", "Vegan", "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400&h=300&fit=crop"),
        FoodType("bakery", "Bakery", "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=400&h=300&fit=crop"),
        FoodType("coffee", "Coffee", "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=400&h=300&fit=crop"),
        FoodType("wine-bar", "Wine Bar", "https://images.unsplash.com/photo-1510812431401-41d2bd2722f3?w=400&h=300&fit=crop"),
        FoodType("korean-fried-chicken", "Korean Fried Chicken", "https://images.unsplash.com/photo-1590301157890-4810ed352e52?w=400&h=300&fit=crop"),
        FoodType("ramen", "Ramen", "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=400&h=300&fit=crop"),
        FoodType("sushi", "Sushi", "https://images.unsplash.com/photo-1579584425555-cb18d1714a92?w=400&h=300&fit=crop"),
        FoodType("dim-sum", "Dim Sum", "https://images.unsplash.com/photo-1563245372-f21724e3856d?w=400&h=300&fit=crop"),
        FoodType("tapas", "Tapas", "https://images.unsplash.com/photo-1544025162-d76694265947?w=400&h=300&fit=crop"),
        FoodType("soul-food", "Soul Food", "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=400&h=300&fit=crop"),
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
            guestFavoriteLevel = GuestFavoriteLevel.High,
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
     * Extra mock rows so each price tab has a long list (used by discover "Restaurants by Price").
     * Distinct ids: `catalog-t{1-4}-{001-024}`.
     */
    private val priceCatalogPerTier = 24

    private val priceCatalogImagePool: List<String> = listOf(
        "https://images.unsplash.com/photo-1678684279246-96e6afb970f2?w=400&h=300&fit=crop",
        "https://images.unsplash.com/photo-1590189599125-67138c6509ef?w=400&h=300&fit=crop",
        "https://images.unsplash.com/photo-1657502996869-6ccd568b9d41?w=400&h=300&fit=crop",
        "https://images.unsplash.com/photo-1681270507609-e2a5f21969b0?w=400&h=300&fit=crop",
        "https://images.unsplash.com/photo-1687276287139-88f7333c8ca4?w=400&h=300&fit=crop",
        "https://images.unsplash.com/photo-1694834589398-27b369c6f7a6?w=400&h=300&fit=crop",
        "https://images.unsplash.com/photo-1731460202531-bf8389d565f7?w=400&h=300&fit=crop",
        "https://images.unsplash.com/photo-1564759319376-a60b400ced8e?w=400&h=300&fit=crop",
        "https://images.unsplash.com/photo-1753722157947-8a50f04a9309?w=400&h=300&fit=crop",
        "https://images.unsplash.com/photo-1773188243397-29591fa09047?w=400&h=300&fit=crop",
        "https://images.unsplash.com/photo-1763867641400-96b9cccdbf7d?w=400&h=300&fit=crop",
        "https://images.unsplash.com/photo-1768397003905-a202ea6325f5?w=400&h=300&fit=crop",
        "https://images.unsplash.com/photo-1526366411709-472085c8a586?w=400&h=300&fit=crop",
        "https://images.unsplash.com/photo-1598990386084-8af4dd12b3b4?w=400&h=300&fit=crop",
        "https://images.unsplash.com/photo-1762922425306-ef64664f6e4d?w=400&h=300&fit=crop",
        "https://images.unsplash.com/photo-1675150303909-1bb94e33132f?w=400&h=300&fit=crop",
        "https://images.unsplash.com/photo-1692780941487-505d5d908aa6?w=400&h=300&fit=crop",
        "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=400&h=300&fit=crop",
        "https://images.unsplash.com/photo-1552566626-52f8b828add9?w=400&h=300&fit=crop",
        "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=400&h=300&fit=crop",
    )

    private val priceCatalogNameA = listOf(
        "Harbor", "Neon", "Maple", "Copper", "Velvet", "Stone", "Silver", "Golden",
        "Urban", "Garden", "Riverside", "Summit", "Lotus", "Ember", "Crimson", "Azure",
    )
    private val priceCatalogNameB = listOf(
        "Kitchen", "Table", "Yard", "House", "Bistro", "Grill", "Noodle", "Tavern",
        "Cellar", "Roof", "Counter", "Hall", "Corner", "Market", "Social", "Club",
    )
    private val priceCatalogCuisines = listOf(
        "Korean", "Japanese", "Italian", "Thai", "French", "Chinese", "Mexican", "Indian",
        "Vietnamese", "Spanish", "Greek", "Turkish", "BBQ", "Seafood", "Steakhouse", "Brunch",
        "Mediterranean", "Fusion", "American", "Peruvian", "Ethiopian", "Lebanese", "Polish", "Tapas",
    )
    private val priceCatalogAreas = listOf(
        "Downtown", "Midtown", "Arts District", "Waterfront", "Old Town", "North End",
        "SoHo", "West Loop", "Koreatown", "Japantown", "Uptown", "Financial District",
    )
    private val priceCatalogTags = listOf("New", "Popular", "Chef's pick", "Local gem", null, null, null)

    private fun buildPriceTierCatalog(): List<Restaurant> {
        fun tier(price: String, tierKey: String): List<Restaurant> {
            return (1..priceCatalogPerTier).map { idx ->
                val i = idx - 1
                val baseName = "${priceCatalogNameA[i % priceCatalogNameA.size]} ${priceCatalogNameB[(i / priceCatalogNameA.size) % priceCatalogNameB.size]}"
                val name = "$baseName · $idx"
                val cuisine = priceCatalogCuisines[i % priceCatalogCuisines.size]
                val rating = (3.85 + (i % 14) * 0.08).coerceAtMost(5.0)
                val reviews = 180 + i * 97 + (tierKey.last().digitToIntOrNull()?.times(11) ?: 0)
                val distance = "${(i % 8) + 1}.${(i * 7) % 10} mi"
                Restaurant(
                    id = "catalog-$tierKey-${idx.toString().padStart(3, '0')}",
                    name = name,
                    cuisine = cuisine,
                    rating = rating,
                    reviews = reviews,
                    price = price,
                    distance = distance,
                    image = priceCatalogImagePool[i % priceCatalogImagePool.size],
                    area = priceCatalogAreas[i % priceCatalogAreas.size],
                    tag = priceCatalogTags[i % priceCatalogTags.size],
                )
            }
        }
        return tier("$", "t1") + tier("$$", "t2") + tier("$$$", "t3") + tier("$$$$", "t4")
    }

    /** Combined catalog used for search / detail lookups. */
    val ALL: List<Restaurant> by lazy {
        (MONTHLY_BEST + LOVED_BY_LOCALS + VIRAL + DATE_NIGHT + buildPriceTierCatalog())
            .distinctBy { it.id }
            .map { it.withDerivedGuestFavoriteLevel() }
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
        val ft = FOOD_TYPES.firstOrNull { it.id == id } ?: return ALL
        val label = ft.label.lowercase()
        val tokens = label.split(Regex("[^a-zA-Z0-9]+")).filter { it.length >= 3 }
        val needles = buildList {
            add(label)
            addAll(tokens)
        }.distinct()
        return ALL.filter { r ->
            val hay = "${r.name} ${r.cuisine} ${r.area.orEmpty()} ${r.tag.orEmpty()}".lowercase()
            needles.any { needle -> hay.contains(needle) }
        }.ifEmpty { ALL.take(6) }
    }

    /** Search-results query used when opening local favorites scoped to the user's city. */
    val localFavoritesSearchQuery: String = "Local favorites"

    /** Maps the user's selected location (name/address) to a catalog [City] id. */
    fun cityForLocation(location: UserLocation): City {
        val haystack = "${location.name} ${location.address}".lowercase()
        CITIES.forEach { city ->
            if (haystack.contains(city.label.lowercase())) return city
            val idWords = city.id.replace('-', ' ')
            if (haystack.contains(idWords)) return city
        }
        return CITIES.firstOrNull { it.id == "seoul" } ?: CITIES.first()
    }

    fun byCity(id: String): List<Restaurant> {
        // No city tagging on individual restaurants — just slice the catalog deterministically.
        val seed = id.hashCode()
        return ALL.shuffled(kotlin.random.Random(seed)).take(5)
    }

    /** Loved-by-locals list, deterministically varied per city. */
    fun localFavoritesForCity(cityId: String): List<Restaurant> {
        val seed = cityId.hashCode()
        val pool = LOVED_BY_LOCALS.ifEmpty { ALL }
        return pool.shuffled(kotlin.random.Random(seed))
    }

    fun bySection(id: String): List<Restaurant> = when (id) {
        "monthly-best" -> MONTHLY_BEST
        "loved-by-locals" -> LOVED_BY_LOCALS
        "viral" -> VIRAL
        "date-night" -> DATE_NIGHT
        "where-to-eat" -> ALL.shuffled(kotlin.random.Random(42))
        "top-picks-food" -> ALL.shuffled(kotlin.random.Random(43))
        else -> ALL
    }

    fun trendingSearches(): List<String> = listOf(
        "K-BBQ", "Omakase", "Brunch", "Date night", "Rooftop", "Dim sum",
    )
}

