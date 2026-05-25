package com.mh.restaurantchainreservation.feature.booking

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

internal const val DEPOSIT_PER_GUEST = 10
internal const val SERVICE_FEE = 2.99
internal const val WALLET_BALANCE_USD = 5000.0
internal const val INTERNAL_BALANCE_KRW = 13_000_000.0
internal const val BONUS_BALANCE_KRW = 330_000.0
internal const val POINTS_EARN = 45

internal fun walletUsageProgress(payment: Double, balance: Double): Float =
    if (balance <= 0.0) 0f else (payment / balance).toFloat().coerceIn(0f, 1f)
internal const val BOOKING_DAY_RANGE = 14

internal val TIME_SLOTS = listOf(
    "17:00", "17:30", "18:00", "18:30", "19:00",
    "19:30", "20:00", "20:30", "21:00", "21:30",
)

internal enum class BookingFlowStep {
    Date,
    Details,
    Preferences,
    Confirm,
    Awaiting,
    Success,
}

internal val PROGRESS_STEPS = listOf(
    BookingFlowStep.Date,
    BookingFlowStep.Details,
    BookingFlowStep.Preferences,
    BookingFlowStep.Confirm,
)

data class BookingDayRow(
    val label: String,
    val day: String,
    val date: Int,
    val month: String,
    val full: LocalDate,
)

data class PrefOption(val id: String, val label: String)

internal data class OccasionOption(
    val id: String,
    val label: String,
)

internal val OCCASIONS = listOf(
    OccasionOption("anniversary", "Anniversary"),
    OccasionOption("birthday", "Birthday"),
    OccasionOption("date", "Date night"),
    OccasionOption("business", "Business"),
    OccasionOption("casual", "Casual"),
    OccasionOption("celebration", "Celebration"),
)

val SEATING_OPTIONS = listOf(
    PrefOption("dining-hall", "Dining hall"),
    PrefOption("private-room", "Private room"),
    PrefOption("terrace", "Terrace"),
    PrefOption("window", "Window seat"),
    PrefOption("bar", "Bar"),
    PrefOption("booth", "Booth"),
    PrefOption("rooftop", "Rooftop"),
    PrefOption("counter", "Counter"),
)

val CUISINE_PREFS = listOf(
    PrefOption("grilled-beef", "Grilled beef"),
    PrefOption("seafood", "Seafood"),
    PrefOption("italian", "Italian"),
    PrefOption("japanese", "Japanese"),
    PrefOption("french", "French"),
    PrefOption("korean", "Korean"),
    PrefOption("chinese", "Chinese"),
    PrefOption("thai", "Thai"),
    PrefOption("wine-pairing", "Wine pairing"),
    PrefOption("brunch", "Brunch"),
    PrefOption("steakhouse", "Steakhouse"),
    PrefOption("healthy", "Healthy"),
)

val VIBE_OPTIONS = listOf(
    PrefOption("date-night", "Date night"),
    PrefOption("business-dinner", "Business dinner"),
    PrefOption("celebration", "Celebration"),
    PrefOption("casual-dining", "Casual dining"),
    PrefOption("romantic", "Romantic"),
    PrefOption("family-friendly", "Family-friendly"),
    PrefOption("late-night", "Late night"),
    PrefOption("quiet-intimate", "Quiet / intimate"),
)

val AMENITY_OPTIONS = listOf(
    PrefOption("parking", "Parking"),
    PrefOption("valet", "Valet"),
    PrefOption("corkage-free", "Corkage-free"),
    PrefOption("lettering", "Lettering"),
    PrefOption("kids-welcome", "Kids welcome"),
    PrefOption("kids-free", "Kids free"),
    PrefOption("sommelier", "Sommelier"),
    PrefOption("accessible", "Accessible"),
    PrefOption("pet-friendly", "Pet-friendly"),
    PrefOption("high-chair", "High chair"),
    PrefOption("waiting-space", "Waiting space"),
    PrefOption("wifi", "Wi‑Fi"),
    PrefOption("live-music", "Live music"),
    PrefOption("projector", "Projector"),
    PrefOption("birthday-setup", "Birthday setup"),
    PrefOption("flower-deco", "Flower deco"),
)

internal fun createBookingDays(): List<BookingDayRow> {
    val today = LocalDate.now()
    return (0 until BOOKING_DAY_RANGE).map { offset ->
        val d = today.plusDays(offset.toLong())
        val label = when (offset) {
            0 -> "Today"
            1 -> "Tomorrow"
            else -> d.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        }
        BookingDayRow(
            label = label,
            day = d.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            date = d.dayOfMonth,
            month = d.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            full = d,
        )
    }
}

internal fun formatBookingDate(
    selectedDateIndex: Int,
    customDate: LocalDate?,
    days: List<BookingDayRow>,
): String {
    if (selectedDateIndex == -1 && customDate != null) {
        val weekday = customDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        val month = customDate.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        return "$weekday, $month ${customDate.dayOfMonth}"
    }
    val day = days.getOrNull(selectedDateIndex) ?: return ""
    return "${day.day}, ${day.month} ${day.date}"
}

internal fun formatShortDate(localDate: LocalDate): String {
    val weekday = localDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    val month = localDate.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    return "$weekday, $month ${localDate.dayOfMonth}"
}

internal fun genBookingId(restaurantId: String): String {
    val n = restaurantId.toIntOrNull() ?: restaurantId.firstOrNull()?.code ?: 0
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val c1 = chars[(n * 7 + 3) % 26]
    val c2 = chars[(n * 13 + 11) % 26]
    val c3 = chars[(n * 19 + 5) % 26]
    val c4 = chars[(n * 23 + 17) % 26]
    val num = ((n * 9973 + 7919) % 90 + 10).toString()
    return "RSV-$num$c1$c2$c3$c4"
}

internal fun collectPrefLabels(
    seating: List<String>,
    cuisine: List<String>,
    vibes: List<String>,
    amenities: List<String>,
): List<String> {
    fun mapIds(options: List<PrefOption>, ids: List<String>) =
        ids.mapNotNull { id -> options.find { it.id == id }?.label }
    return buildList {
        addAll(mapIds(SEATING_OPTIONS, seating))
        addAll(mapIds(CUISINE_PREFS, cuisine))
        addAll(mapIds(VIBE_OPTIONS, vibes))
        addAll(mapIds(AMENITY_OPTIONS, amenities))
    }
}

fun occasionLabel(occasionId: String?): String =
    OCCASIONS.find { it.id == occasionId }?.label ?: "None"

internal fun fmtMoney(amount: Double): String = String.format(Locale.US, "%.2f", amount)

data class BookTableInitialState(
    val existingBookingId: String? = null,
    val guests: Int = 2,
    val selectedDateIndex: Int = 0,
    val customDate: LocalDate? = null,
    val selectedTime: String? = null,
    val contactName: String = "",
    val phone: String = "",
    val notes: String = "",
    val occasion: String? = null,
    val seating: List<String> = emptyList(),
    val cuisinePrefs: List<String> = emptyList(),
    val vibes: List<String> = emptyList(),
    val amenities: List<String> = emptyList(),
)

data class BookTableResult(
    val bookingDate: LocalDate,
    val selectedTime: String,
    val guests: Int,
    val contactName: String,
    val phone: String,
    val notes: String,
    val occasion: String?,
    val seating: List<String>,
    val cuisinePrefs: List<String>,
    val vibes: List<String>,
    val amenities: List<String>,
)

fun bookingDayOptions(): List<BookingDayRow> = createBookingDays()

fun matchPrefOptionIds(options: List<PrefOption>, labels: List<String>): List<String> =
    labels.mapNotNull { label ->
        options.find { it.label.equals(label, ignoreCase = true) }?.id
            ?: options.find { label.contains(it.label, ignoreCase = true) }?.id
    }.distinct()

fun matchOccasionId(value: String?): String? {
    if (value.isNullOrBlank()) return null
    return OCCASIONS.find {
        it.id.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true)
    }?.id
}

fun resolveInitialDateSelection(dateStr: String, days: List<BookingDayRow>): Pair<Int, LocalDate?> {
    val parsed = parseStoredBookingDate(dateStr) ?: return 0 to null
    val index = days.indexOfFirst { it.full == parsed }
    return if (index >= 0) index to null else -1 to parsed
}

fun formatStoredBookingDate(date: LocalDate): String {
    val weekday = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    val month = date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    return "$weekday, ${date.dayOfMonth} $month ${date.year}"
}

fun labelsForPrefIds(options: List<PrefOption>, ids: List<String>): List<String> =
    ids.mapNotNull { id -> options.find { it.id == id }?.label }

private fun parseStoredBookingDate(dateStr: String): LocalDate? {
    val datePart = dateStr.trim().substringAfter(",", dateStr.trim()).trim()
    if (datePart.isBlank()) return null
    val formatterPatterns = listOf("d MMM yyyy", "dd MMM yyyy")
    for (pattern in formatterPatterns) {
        try {
            return LocalDate.parse(
                datePart,
                java.time.format.DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH),
            )
        } catch (_: Exception) {
            // try next pattern
        }
    }
    return null
}
