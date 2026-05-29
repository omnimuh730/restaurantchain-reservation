package com.mh.restaurantchainreservation.feature.booking

data class StarDistribution(val stars: Int, val percent: Int)

val ratingDistribution = listOf(
    StarDistribution(5, 88),
    StarDistribution(4, 64),
    StarDistribution(3, 30),
    StarDistribution(2, 14),
    StarDistribution(1, 8),
)

data class SubRatingMetric(
    val label: String,
    val score: String,
    val emoji: String,
)

val subRatingMetrics = listOf(
    SubRatingMetric("Taste", "5.0", "🍽️"),
    SubRatingMetric("Ambience", "4.9", "✨"),
    SubRatingMetric("Service", "4.8", "🤝"),
    SubRatingMetric("Value", "4.7", "💰"),
)
