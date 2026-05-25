package com.mh.restaurantchainreservation.core.model

private val mapCoordinates = listOf(
    37.7920 to -122.4120,
    37.7870 to -122.4180,
    37.7810 to -122.4050,
    37.7890 to -122.3990,
    37.7760 to -122.4140,
    37.7940 to -122.3950,
    37.7960 to -122.4060,
    37.7800 to -122.4200,
    37.7830 to -122.4010,
    37.7950 to -122.4100,
    37.7780 to -122.3970,
    37.7910 to -122.4160,
    37.7870 to -122.4030,
    37.7840 to -122.4190,
    37.7920 to -122.3980,
    37.7770 to -122.4080,
    37.7980 to -122.4150,
    37.7750 to -122.4020,
    37.7860 to -122.4210,
    37.7850 to -122.3960,
)

fun Restaurant.mapCoordinate(): Pair<Double, Double> {
    val numeric = id.filter { it.isDigit() }.toIntOrNull()
    val seed = if (numeric != null && numeric > 0) {
        numeric
    } else {
        id.hashCode().and(0x7FFFFFFF)
    }
    val index = (seed - 1).coerceAtLeast(0) % mapCoordinates.size
    return mapCoordinates[index]
}
