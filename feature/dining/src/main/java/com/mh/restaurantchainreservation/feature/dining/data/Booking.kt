package com.mh.restaurantchainreservation.feature.dining.data

enum class BookingStatus {
    Pending,
    Confirmed,
    Rejected,
    Completed,
    Cancelled,
    NoShow,
}

data class ReceiptItem(
    val name: String,
    val qty: Int,
    val price: Double,
    val emoji: String? = null,
)

data class Receipt(
    val items: List<ReceiptItem>,
    val subtotal: Double,
    val tax: Double,
    val tip: Double,
    val total: Double,
    val paymentMethod: String,
    val paidAt: String,
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
    val address: String,
    val phone: String,
    val diningPoints: Int,
    val specialRequest: String? = null,
    val occasion: String? = null,
    val seating: String,
    val confirmationNo: String,
    val receipt: Receipt? = null,
)

fun fmtR(n: Double): String {
    if (n == n.toLong().toDouble()) return n.toLong().toString()
    val s = "%.2f".format(n)
    return s.trimEnd('0').trimEnd('.')
}

fun compactDate(date: String): String {
    val parts = date.split(",").map { it.trim() }
    return if (parts.size > 1) parts.drop(1).joinToString(", ") else date
}
