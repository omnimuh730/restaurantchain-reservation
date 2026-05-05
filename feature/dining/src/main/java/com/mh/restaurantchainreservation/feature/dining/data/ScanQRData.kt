package com.mh.restaurantchainreservation.feature.dining.data

enum class ScanStep { Scan, Arrived, Dining, Bill, Pay, Review }

data class ScanStepSpec(val id: ScanStep, val labelRes: Int)

data class ScanMenuItem(
    val name: String,
    val qty: Int,
    val price: Double,
    val mark: String,
)

val InitialScanMenu = listOf(
    ScanMenuItem("Truffle Edamame", 1, 14.0, "TE"),
    ScanMenuItem("Wagyu Tartare", 1, 28.0, "WT"),
    ScanMenuItem("Omakase Selection (8pc)", 2, 170.0, "OS"),
)

val FullScanMenu = listOf(
    ScanMenuItem("Truffle Edamame", 1, 14.0, "TE"),
    ScanMenuItem("Wagyu Tartare", 1, 28.0, "WT"),
    ScanMenuItem("Omakase Selection (8pc)", 2, 170.0, "OS"),
    ScanMenuItem("A5 Wagyu Steak", 1, 120.0, "WS"),
    ScanMenuItem("Sake Flight – Premium", 1, 42.0, "SF"),
    ScanMenuItem("Sparkling Water", 2, 12.0, "SW"),
    ScanMenuItem("Matcha Crème Brûlée", 2, 32.0, "MC"),
    ScanMenuItem("Espresso", 2, 10.0, "ES"),
)

val ReviewTags = listOf(
    "Great food",
    "Amazing ambiance",
    "Excellent service",
    "Good value",
    "Will return",
)

data class MenuItemDish(
    val name: String,
    val price: Double,
    val description: String,
    val emoji: String,
    val popular: Boolean = false,
)

private val sampleMenuJp = listOf(
    MenuItemDish("Omakase Tasting (12 pc)", 88.0, "Chef's selection of premium nigiri", "🍣", popular = true),
    MenuItemDish("Junmai Daiginjo Sake", 24.0, "Premium chilled sake, 6oz", "🍶"),
    MenuItemDish("Matcha Crème Brûlée", 14.0, "House-made dessert, lightly torched", "🍵"),
    MenuItemDish("Otoro Add-on", 18.0, "Two pieces of fatty tuna belly", "🥢", popular = true),
)
private val sampleMenuFr = listOf(
    MenuItemDish("Steak Frites", 32.0, "Grass-fed sirloin with truffle fries", "🥖", popular = true),
    MenuItemDish("Salade Niçoise", 18.0, "Tuna, egg, olives, haricots verts", "🥗"),
    MenuItemDish("Côtes du Rhône", 14.0, "By the glass", "🍷"),
    MenuItemDish("Crème Caramel", 11.0, "Classic French dessert", "🍮"),
)
private val sampleMenuKr = listOf(
    MenuItemDish("Premium Wagyu Set", 96.0, "180g A5 wagyu with banchan", "🥩", popular = true),
    MenuItemDish("Bibimbap", 18.0, "Sizzling stone-pot rice bowl", "🍚"),
    MenuItemDish("Soju Bottle", 12.0, "Chilled traditional soju", "🍶"),
    MenuItemDish("Kimchi Pancake", 14.0, "Crispy pajeon with house kimchi", "🥬"),
)

fun menuFor(booking: Booking): List<MenuItemDish> = when {
    booking.cuisine.contains("Japanese", ignoreCase = true) -> sampleMenuJp
    booking.cuisine.contains("French", ignoreCase = true) -> sampleMenuFr
    booking.cuisine.contains("Korean", ignoreCase = true) -> sampleMenuKr
    else -> sampleMenuFr
}
