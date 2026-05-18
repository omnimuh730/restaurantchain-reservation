package com.mh.restaurantchainreservation.core.model

/**
 * Dining news article. Mirrors React `news-section/types.ts` and `newsData.ts`.
 */
enum class NewsCategory {
    Trending,
    NewOpening,
    Award,
    Event,
    Chef,
    Guide,
}

data class NewsItem(
    val id: String,
    val category: NewsCategory,
    val title: String,
    val summary: String,
    val body: String,
    val image: String,
    val author: String,
    val authorAvatar: String,
    /** Epoch millis (UTC). */
    val publishedAtEpochMs: Long,
    val readMinutes: Int,
    val tags: List<String>,
)

object NewsData {
    val all: List<NewsItem> = mockNewsArticles()

    fun findById(id: String): NewsItem? = all.firstOrNull { it.id == id }
}

/** @deprecated Use [NewsData.all] — kept for discover rail until callers migrate. */
fun mockNews(): List<NewsItem> = NewsData.all

private fun mockNewsArticles(): List<NewsItem> {
    val now = System.currentTimeMillis()
    val hour = 3_600_000L
    return listOf(
        NewsItem(
            id = "n-101",
            category = NewsCategory.Award,
            title = "Three Bay Area Restaurants Earn Their First Michelin Star",
            summary = "The 2026 Michelin Guide spotlights bold new voices in California fine dining.",
            body = "The 2026 California Michelin Guide unveiled today recognizes three first-time stars across the Bay Area. Inspectors highlighted impeccable seasonal sourcing, technically refined plating and a renewed focus on hospitality. Reservations have already surged 480% in the first 12 hours after the announcement, with most restaurants booked solid through the next eight weeks.",
            image = "https://images.unsplash.com/photo-1514933651103-005eec06c04b?w=800&h=600&fit=crop",
            author = "Maya Tanaka",
            authorAvatar = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop",
            publishedAtEpochMs = now - hour / 2,
            readMinutes = 4,
            tags = listOf("Michelin", "Fine Dining", "Bay Area"),
        ),
        NewsItem(
            id = "n-102",
            category = NewsCategory.NewOpening,
            title = "Chef Daniel Park Opens Hansik Tasting Counter in Hayes Valley",
            summary = "A 12-seat omakase-style Korean counter brings rare regional dishes to the city.",
            body = "After three years cooking in Seoul and Busan, chef Daniel Park returns with Hansik Counter — a 12-seat tasting room dedicated to lesser-known regional Korean cooking. The 14-course menu draws on hand-foraged seaweeds, slow-aged kimchi, and a brassware-only service tradition. Booking opens Friday at 10 AM and is expected to fill quickly.",
            image = "https://images.unsplash.com/photo-1590189599125-67138c6509ef?w=800&h=600&fit=crop",
            author = "Jin Lee",
            authorAvatar = "https://images.unsplash.com/photo-1502685104226-ee32379fefbe?w=100&h=100&fit=crop",
            publishedAtEpochMs = now - 5 * hour,
            readMinutes = 3,
            tags = listOf("Opening", "Korean", "Tasting Menu"),
        ),
        NewsItem(
            id = "n-103",
            category = NewsCategory.Trending,
            title = "Why Everyone Is Suddenly Obsessed with Hand-Pulled Knife Noodles",
            summary = "The texture-forward dish is breaking out of regional menus and into the mainstream.",
            body = "From Flushing to Oakland, knife-cut and hand-pulled noodles are dominating social feeds — and reservation queues. Chefs cite a renewed appetite for craft and visible technique post-pandemic. We round up six counters where the dough hits the table within minutes of being shaped.",
            image = "https://images.unsplash.com/photo-1731460202531-bf8389d565f7?w=800&h=600&fit=crop",
            author = "Renee Cho",
            authorAvatar = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=100&h=100&fit=crop",
            publishedAtEpochMs = now - 22 * hour,
            readMinutes = 5,
            tags = listOf("Trending", "Noodles", "Asian"),
        ),
        NewsItem(
            id = "n-104",
            category = NewsCategory.Event,
            title = "Spring Truffle Week Returns with 38 Participating Restaurants",
            summary = "April 28 – May 5: prix-fixe truffle menus from \$65 across the city.",
            body = "Spring Truffle Week kicks off next Monday with a record 38 participating restaurants. Diners can book curated three-course menus starting at \$65, and the festival pass unlocks priority seating at flagship Italian, French, and modern American kitchens.",
            image = "https://images.unsplash.com/photo-1681270507609-e2a5f21969b0?w=800&h=600&fit=crop",
            author = "Marco Bellini",
            authorAvatar = "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=100&h=100&fit=crop",
            publishedAtEpochMs = now - 30 * hour,
            readMinutes = 2,
            tags = listOf("Event", "Truffle", "Festival"),
        ),
        NewsItem(
            id = "n-105",
            category = NewsCategory.Chef,
            title = "Pastry Chef Aiko Sato Wins National Dessert of the Year",
            summary = "Her yuzu-pine ice cream sweeps the judges at the 2026 Pastry Awards.",
            body = "Aiko Sato of Patisserie Ume took home the National Dessert of the Year for her yuzu-pine ice cream — a dish nine months in development. Judges praised the layered aromatics and balance of acidity. Tasting menu seats featuring the dish are released every Sunday at midnight.",
            image = "https://images.unsplash.com/photo-1657502996869-6ccd568b9d41?w=800&h=600&fit=crop",
            author = "Hannah Wright",
            authorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&h=100&fit=crop",
            publishedAtEpochMs = now - 48 * hour,
            readMinutes = 3,
            tags = listOf("Pastry", "Award", "Dessert"),
        ),
        NewsItem(
            id = "n-106",
            category = NewsCategory.Guide,
            title = "12 Patios Worth Booking Before Summer Hits",
            summary = "Our updated 2026 patio guide ranks the best al fresco dining in the city.",
            body = "We walked, dined and lingered through every notable patio in town. The 2026 list balances skyline views, tree-lined courtyards and sea-breeze terraces. Each pick links to live availability so you can book your table before the crowds arrive.",
            image = "https://images.unsplash.com/photo-1598990386084-8af4dd12b3b4?w=800&h=600&fit=crop",
            author = "Chris Donovan",
            authorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop",
            publishedAtEpochMs = now - 72 * hour,
            readMinutes = 6,
            tags = listOf("Guide", "Outdoor", "Summer"),
        ),
    )
}
