package com.mh.restaurantchainreservation.feature.dining.data

import java.util.Calendar
import java.util.Date

private val sampleMenuJp = listOf(
    Triple("🍣", "Omakase Tasting (12 pc)" to 88.0, "Chef's selection of premium nigiri"),
    Triple("🍶", "Junmai Daiginjo Sake" to 24.0, "Premium chilled sake, 6oz"),
    Triple("🍵", "Matcha Crème Brûlée" to 14.0, "House-made dessert, lightly torched"),
    Triple("🥢", "Otoro Add-on" to 18.0, "Two pieces of fatty tuna belly"),
)

/** Long receipt for scroll testing in order receipt modal. */
private val longReceiptItems = listOf(
    ReceiptItem("Tagliatelle al Tartufo", 2, 28.0),
    ReceiptItem("Burrata Caprese", 1, 18.0),
    ReceiptItem("Chianti Classico (glass)", 2, 14.0, category = ReceiptItemCategory.Drink),
    ReceiptItem("Tiramisu", 1, 12.0),
    ReceiptItem("Margherita Pizza", 1, 16.0),
    ReceiptItem("Prosciutto e Melone", 1, 14.0),
    ReceiptItem("Risotto ai Funghi", 1, 24.0),
    ReceiptItem("Branzino al Forno", 1, 32.0),
    ReceiptItem("Panna Cotta", 2, 9.0),
    ReceiptItem("Negroni", 2, 13.0, category = ReceiptItemCategory.Drink),
    ReceiptItem("Caesar Salad", 1, 12.0),
    ReceiptItem("Calamari Fritti", 1, 15.0),
    ReceiptItem("Osso Buco", 1, 38.0),
    ReceiptItem("Gelato Trio", 1, 11.0),
    ReceiptItem("Espresso", 2, 4.0, category = ReceiptItemCategory.Drink),
    ReceiptItem("Limoncello", 1, 10.0, category = ReceiptItemCategory.Drink),
    ReceiptItem("Bread Basket", 1, 6.0),
)

private val longReceiptTotal: Double =
    longReceiptItems.sumOf { it.price * it.qty }

val MOCK_BOOKINGS: List<Booking> = listOf(
    Booking(
        id = "9",
        restaurant = "Nami Counter",
        cuisine = "Japanese · Sashimi",
        image = "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=1080&h=720&fit=crop",
        date = "Sat, 2 May 2026",
        time = "20:30",
        guests = 2,
        status = BookingStatus.Pending,
        address = "88 Market Lane, San Francisco, CA 94105",
        phone = "(415) 555-0188",
        diningPoints = 0,
        specialRequest = "Counter seats if available",
        occasion = "Date",
        seating = "Counter",
        confirmationNo = "RQ-2026-0502N",
    ),
    Booking(
        id = "10",
        restaurant = "Riverside Tapas",
        cuisine = "Spanish · Tapas",
        image = "https://images.unsplash.com/photo-1544025162-d76694265947?w=1080&h=720&fit=crop",
        date = "Sun, 3 May 2026",
        time = "19:00",
        guests = 4,
        status = BookingStatus.Rejected,
        address = "12 Embarcadero Center, San Francisco, CA 94111",
        phone = "(415) 555-0199",
        diningPoints = 0,
        seating = "Outdoor",
        confirmationNo = "RQ-2026-0503R",
    ),
    Booking(
        id = "1",
        restaurant = "Sakura Omakase",
        cuisine = "Japanese · Omakase",
        image = "https://images.unsplash.com/photo-1717838207789-62684e75a770?w=1080&h=720&fit=crop",
        date = "Tue, 21 Apr 2026",
        time = "19:30",
        guests = 2,
        status = BookingStatus.Confirmed,
        address = "243 S San Pedro St, Los Angeles, CA 90012",
        phone = "(213) 265-7763",
        diningPoints = 150,
        specialRequest = "Window seat preferred",
        occasion = "Anniversary",
        seating = "Indoor",
        confirmationNo = "CT-2026-0418A",
    ),
    Booking(
        id = "2",
        restaurant = "Le Petit Bistro",
        cuisine = "French · Bistro",
        image = "https://images.unsplash.com/photo-1679586491709-478283802c40?w=1080&h=720&fit=crop",
        date = "Sat, 19 Apr 2026",
        time = "18:00",
        guests = 4,
        status = BookingStatus.Confirmed,
        address = "456 Market St, San Francisco, CA 94105",
        phone = "(415) 555-0182",
        diningPoints = 200,
        seating = "Terrace",
        confirmationNo = "CT-2026-0422B",
    ),
    Booking(
        id = "3",
        restaurant = "Gangnam BBQ House",
        cuisine = "Korean · BBQ",
        image = "https://images.unsplash.com/photo-1709433420612-8cad609df914?w=1080&h=720&fit=crop",
        date = "Sun, 20 Apr 2026",
        time = "12:30",
        guests = 1,
        status = BookingStatus.Confirmed,
        address = "789 Mission St, San Francisco, CA 94103",
        phone = "(415) 555-0299",
        diningPoints = 80,
        seating = "Any",
        confirmationNo = "CT-2026-0420W",
    ),
    Booking(
        id = "4",
        restaurant = "Bella Napoli",
        cuisine = "Italian · Trattoria",
        image = "https://images.unsplash.com/photo-1662197480393-2a82030b7b83?w=1080&h=720&fit=crop",
        date = "Fri, 28 Mar 2026",
        time = "20:00",
        guests = 2,
        status = BookingStatus.Completed,
        rating = 4.5,
        feedback = MealFeedback(
            taste = 5,
            ambience = 4,
            service = 5,
            value = 4,
            comment = "The truffle pasta was incredible and service was attentive without being intrusive. We'll definitely be back.",
            tags = listOf("Great food", "Excellent service", "Will return"),
        ),
        address = "101 Main St, San Francisco, CA 94102",
        phone = "(415) 555-0345",
        diningPoints = 120,
        seating = "Indoor",
        confirmationNo = "CT-2026-0328C",
        receipt = Receipt(
            items = longReceiptItems,
            subtotal = longReceiptTotal,
            tax = 0.0,
            tip = 0.0,
            total = longReceiptTotal,
            paymentMethod = "Tonight Wallet",
            paidAt = "Fri, 28 Mar 2026 · 21:48",
        ),
    ),
    Booking(
        id = "5",
        restaurant = "Ocean Pearl",
        cuisine = "Seafood · Raw Bar",
        image = "https://images.unsplash.com/photo-1761314037182-8ea3363cf3a3?w=1080&h=720&fit=crop",
        date = "Wed, 19 Mar 2026",
        time = "19:00",
        guests = 3,
        status = BookingStatus.Completed,
        rating = 4.8,
        feedback = MealFeedback(
            taste = 5,
            ambience = 5,
            service = 4,
            value = 4,
            comment = "Fresh oysters and the lobster roll were outstanding. A bit loud on the terrace but worth it.",
            tags = listOf("Great food", "Amazing ambiance"),
        ),
        address = "555 Pier St, San Francisco, CA 94133",
        phone = "(415) 555-0488",
        diningPoints = 100,
        seating = "Outdoor",
        confirmationNo = "CT-2026-0319D",
        receipt = Receipt(
            items = listOf(
                ReceiptItem("Oyster Sampler (12 pc)", 1, 42.0, "🦪"),
                ReceiptItem("Lobster Roll", 2, 36.0, "🦞"),
                ReceiptItem("Garlic Shrimp", 1, 24.0, "🍤"),
                ReceiptItem("Champagne (glass)", 3, 16.0, "🥂", ReceiptItemCategory.Drink),
            ),
            subtotal = 186.0,
            tax = 16.74,
            tip = 37.20,
            total = 239.94,
            paymentMethod = "Tonight Wallet",
            paidAt = "Wed, 19 Mar 2026 · 20:35",
        ),
    ),
    Booking(
        id = "6",
        restaurant = "The Morning Table",
        cuisine = "American · Brunch",
        image = "https://images.unsplash.com/photo-1664192578382-8216149bd4d5?w=1080&h=720&fit=crop",
        date = "Sat, 8 Mar 2026",
        time = "10:30",
        guests = 2,
        status = BookingStatus.Completed,
        rating = 4.2,
        feedback = MealFeedback(
            taste = 4,
            ambience = 4,
            service = 4,
            value = 5,
            comment = "Solid brunch spot — pancakes were fluffy and coffee was excellent.",
            tags = listOf("Good value", "Will return"),
        ),
        address = "222 Brunch Ave, San Francisco, CA 94110",
        phone = "(415) 555-0122",
        diningPoints = 60,
        seating = "Indoor",
        confirmationNo = "CT-2026-0308E",
        receipt = Receipt(
            items = listOf(
                ReceiptItem("Stack of Buttermilk Pancakes", 1, 16.0, "🥞"),
                ReceiptItem("Eggs Benedict", 1, 18.0, "🍳"),
                ReceiptItem("Avocado Toast", 1, 14.0, "🥑"),
                ReceiptItem("Cappuccino", 2, 6.0, "☕", ReceiptItemCategory.Drink),
            ),
            subtotal = 60.0,
            tax = 5.40,
            tip = 12.0,
            total = 77.40,
            paymentMethod = "Tonight Wallet",
            paidAt = "Sat, 8 Mar 2026 · 11:42",
        ),
    ),
    Booking(
        id = "7",
        restaurant = "Twilight Lounge",
        cuisine = "Bar · Cocktails",
        image = "https://images.unsplash.com/photo-1768508948990-f5866f800fad?w=1080&h=720&fit=crop",
        date = "Sat, 1 Mar 2026",
        time = "21:00",
        guests = 4,
        status = BookingStatus.Cancelled,
        address = "333 Nightlife Blvd, San Francisco, CA 94107",
        phone = "(415) 555-0777",
        diningPoints = 0,
        seating = "Bar",
        confirmationNo = "CT-2026-0301F",
    ),
    Booking(
        id = "8",
        restaurant = "Gangnam BBQ House",
        cuisine = "Korean · BBQ",
        image = "https://images.unsplash.com/photo-1709433420612-8cad609df914?w=1080&h=720&fit=crop",
        date = "Fri, 14 Feb 2026",
        time = "19:00",
        guests = 2,
        status = BookingStatus.NoShow,
        address = "789 Mission St, San Francisco, CA 94103",
        phone = "(415) 555-0299",
        diningPoints = 0,
        seating = "Indoor",
        confirmationNo = "CT-2026-0214G",
    ),
)

private val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

fun parseBookingDateTime(b: Booking): Date? {
    val match = Regex("(\\d{1,2})\\s+([A-Za-z]+)\\s+(\\d{4})").find(b.date) ?: return null
    val (dayStr, monStr, yearStr) = match.destructured
    val monthIdx = months.indexOfFirst { monStr.startsWith(it) }
    if (monthIdx < 0) return null
    val parts = b.time.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull() ?: return null
    val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
    val cal = Calendar.getInstance()
    cal.set(yearStr.toInt(), monthIdx, dayStr.toInt(), hour, minute, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.time
}

fun isCurrentlyDining(
    booking: Booking,
    now: Date = Date(),
    checkedInIds: Set<String>? = null,
): Boolean {
    if (checkedInIds?.contains(booking.id) == true) return true
    if (booking.status != BookingStatus.Confirmed) return false
    val dt = parseBookingDateTime(booking) ?: return false
    val cDt = Calendar.getInstance().apply { time = dt }
    val cNow = Calendar.getInstance().apply { time = now }
    val sameDay = cDt.get(Calendar.YEAR) == cNow.get(Calendar.YEAR) &&
        cDt.get(Calendar.MONTH) == cNow.get(Calendar.MONTH) &&
        cDt.get(Calendar.DAY_OF_MONTH) == cNow.get(Calendar.DAY_OF_MONTH)
    if (sameDay) return true
    val start = dt.time - 15 * 60 * 1000L
    val end = dt.time + 2 * 60 * 60 * 1000L
    return now.time in start..end
}
