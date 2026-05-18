package com.mh.restaurantchainreservation.feature.discover.ui

import androidx.compose.ui.graphics.Color
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantPalette
import com.mh.restaurantchainreservation.core.model.NewsCategory
import java.util.concurrent.TimeUnit

fun NewsCategory.displayLabel(): String = when (this) {
    NewsCategory.Trending -> "Trending"
    NewsCategory.NewOpening -> "New Opening"
    NewsCategory.Award -> "Award"
    NewsCategory.Event -> "Event"
    NewsCategory.Chef -> "Chef"
    NewsCategory.Guide -> "Guide"
}

fun NewsCategory.badgeColor(palette: RestaurantPalette): Color = when (this) {
    NewsCategory.Trending -> palette.destructive
    NewsCategory.NewOpening -> palette.success
    NewsCategory.Award -> palette.warning
    NewsCategory.Event -> palette.info
    NewsCategory.Chef -> palette.brand
    NewsCategory.Guide -> palette.mutedForeground
}

fun formatNewsTimeAgo(publishedAtEpochMs: Long, nowEpochMs: Long = System.currentTimeMillis()): String {
    val diffMs = (nowEpochMs - publishedAtEpochMs).coerceAtLeast(0L)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMs)
    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        minutes < 60 * 24 -> "${minutes / 60}h ago"
        minutes < 60 * 24 * 7 -> "${minutes / (60 * 24)}d ago"
        minutes < 60 * 24 * 30 -> "${minutes / (60 * 24 * 7)}w ago"
        else -> "${minutes / (60 * 24 * 30)}mo ago"
    }
}
