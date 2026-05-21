package com.mh.restaurantchainreservation.core.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** A restaurant row in the Recently Viewed history with its view timestamp. */
data class RecentlyViewedEntry(
    val restaurant: Restaurant,
    val viewedAtEpochMillis: Long,
)

data class RecentlyViewedDayGroup(
    val dayLabel: String,
    val entries: List<RecentlyViewedEntry>,
)

private fun startOfDay(cal: Calendar): Calendar =
    (cal.clone() as Calendar).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

private fun dayKey(epochMillis: Long): Long {
    val cal = startOfDay(Calendar.getInstance().apply { timeInMillis = epochMillis })
    return cal.get(Calendar.YEAR) * 10_000L +
        cal.get(Calendar.MONTH) * 100L +
        cal.get(Calendar.DAY_OF_MONTH)
}

private fun dayOfMonthOrdinal(day: Int): String {
    if (day in 11..13) return "${day}th"
    return when (day % 10) {
        1 -> "${day}st"
        2 -> "${day}nd"
        3 -> "${day}rd"
        else -> "${day}th"
    }
}

/**
 * Labels: "Today", "Yesterday", or "May 18th" for older dates (relative to device local time).
 */
fun recentlyViewedDayLabel(epochMillis: Long, nowMillis: Long = System.currentTimeMillis()): String {
    val viewedDay = startOfDay(Calendar.getInstance().apply { timeInMillis = epochMillis })
    val today = startOfDay(Calendar.getInstance().apply { timeInMillis = nowMillis })
    val yesterday = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, -1) }

    return when {
        viewedDay.timeInMillis == today.timeInMillis -> "Today"
        viewedDay.timeInMillis == yesterday.timeInMillis -> "Yesterday"
        else -> {
            val month = SimpleDateFormat("MMMM", Locale.getDefault()).format(viewedDay.time)
            val day = viewedDay.get(Calendar.DAY_OF_MONTH)
            "$month ${dayOfMonthOrdinal(day)}"
        }
    }
}

/** Groups entries newest-first with Airbnb-style day section headers. */
fun groupRecentlyViewedByDay(entries: List<RecentlyViewedEntry>): List<RecentlyViewedDayGroup> {
    if (entries.isEmpty()) return emptyList()
    val now = System.currentTimeMillis()
    return entries
        .sortedByDescending { it.viewedAtEpochMillis }
        .groupBy { dayKey(it.viewedAtEpochMillis) }
        .entries
        .sortedByDescending { it.key }
        .map { (_, dayEntries) ->
            val sorted = dayEntries.sortedByDescending { it.viewedAtEpochMillis }
            RecentlyViewedDayGroup(
                dayLabel = recentlyViewedDayLabel(sorted.first().viewedAtEpochMillis, now),
                entries = sorted,
            )
        }
}
