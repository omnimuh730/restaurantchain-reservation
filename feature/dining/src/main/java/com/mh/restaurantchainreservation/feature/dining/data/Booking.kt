package com.mh.restaurantchainreservation.feature.dining.data

enum class BookingStatus {
    Pending,
    Confirmed,
    Rejected,
    Completed,
    Cancelled,
    NoShow,
}

enum class ReceiptItemCategory {
    Food,
    Drink,
}

data class ReceiptItem(
    val name: String,
    val qty: Int,
    val price: Double,
    val emoji: String? = null,
    val category: ReceiptItemCategory = ReceiptItemCategory.Food,
)

fun ReceiptItem.lineTotal(): Double = price * qty

data class Receipt(
    val items: List<ReceiptItem>,
    val subtotal: Double,
    val tax: Double,
    val tip: Double,
    val total: Double,
    val paymentMethod: String,
    val paidAt: String,
)

data class MealFeedback(
    val taste: Int,
    val ambience: Int,
    val service: Int,
    val value: Int,
    val comment: String? = null,
    val tags: List<String> = emptyList(),
)

data class Booking(
    val id: String,
    val restaurant: String,
    val cuisine: String,
    val image: String,
    val date: String,
    val time: String,
    val guests: Int,
    val status: BookingStatus,
    val rating: Double? = null,
    val feedback: MealFeedback? = null,
    val address: String,
    val phone: String,
    val diningPoints: Int,
    val specialRequest: String? = null,
    val occasion: String? = null,
    val seating: String,
    val confirmationNo: String,
    val receipt: Receipt? = null,
    val contactName: String? = null,
    val seatingLabels: List<String> = emptyList(),
    val cuisineLabels: List<String> = emptyList(),
    val vibeLabels: List<String> = emptyList(),
    val amenityLabels: List<String> = emptyList(),
)

fun Booking.displaySeatingLabels(): List<String> =
    seatingLabels.ifEmpty {
        seating
            .takeIf { it.isNotBlank() && !it.equals("Any", ignoreCase = true) }
            ?.let { listOf(it) }
            ?: emptyList()
    }

fun Booking.displayCuisineLabels(): List<String> =
    cuisineLabels.ifEmpty {
        cuisine.split("·", ",").map { it.trim() }.filter { it.isNotBlank() }
    }

fun fmtR(n: Double): String {
    if (n == n.toLong().toDouble()) return n.toLong().toString()
    val s = "%.2f".format(n)
    return s.trimEnd('0').trimEnd('.')
}

fun compactDate(date: String): String {
    val parts = date.split(",").map { it.trim() }
    return if (parts.size > 1) parts.drop(1).joinToString(", ") else date
}
