package com.mh.restaurantchainreservation.feature.booking

import com.mh.restaurantchainreservation.core.model.Restaurant

enum class AmenityIconType {
    AccessTime,
    Wifi,
    Parking,
    Phone,
    Terrace,
    PrivateDining,
    BarSeat,
    Wheelchair,
    HighChair,
    Restroom,
    AcUnit,
    Valet,
    Reservations,
    WalkIns,
    Sommelier,
    Vegan,
    GlutenFree,
    KidsMenu,
    Halal,
    LiveMusic,
    Quiet,
    Romantic,
    PetFriendly,
    Corkage,
    Birthday,
    DressCode,
    CardPayment,
    Cash,
    Cuisine,
    DataConnection,
    OutdoorHeating,
    FullBar,
    Takeout,
}

data class RestaurantAmenityItem(
    val label: String,
    val icon: AmenityIconType,
)

data class AmenityCategory(
    val title: String,
    val items: List<RestaurantAmenityItem>,
)

object RestaurantAmenitiesData {

    fun placeOfferChipCategories(ext: RestaurantExtendedData): List<PlaceOfferChipCategory> {
        val offers = ext.placeOffers
        return listOf(
            PlaceOfferChipCategory("Cuisine", offers.cuisineChips),
            PlaceOfferChipCategory("Time", offers.hoursChips),
            PlaceOfferChipCategory("Payments", offers.paymentChips),
            PlaceOfferChipCategory("Amenities", offers.amenityChips),
        ).filter { it.chips.isNotEmpty() }
    }

    fun extendedCategories(restaurant: Restaurant, ext: RestaurantExtendedData): List<AmenityCategory> {
        val seed = restaurant.id.hashCode().and(0x7FFFFFFF)
        val hasFineDining = ext.tags.any { it.contains("Fine", ignoreCase = true) || it.contains("Omakase", ignoreCase = true) }
        val hasWine = ext.tags.any { it.contains("Wine", ignoreCase = true) || it.contains("Bistro", ignoreCase = true) }

        val dining = buildList {
            add(RestaurantAmenityItem("Dining hall seating", AmenityIconType.BarSeat))
            add(RestaurantAmenityItem("Window tables available", AmenityIconType.Terrace))
            if (seed % 3 != 0) add(RestaurantAmenityItem("Private dining room", AmenityIconType.PrivateDining))
            if (seed % 2 == 0) add(RestaurantAmenityItem("Outdoor terrace", AmenityIconType.Terrace))
            add(RestaurantAmenityItem("Bar counter seating", AmenityIconType.BarSeat))
            if (hasFineDining) add(RestaurantAmenityItem("Chef's counter (omakase)", AmenityIconType.PrivateDining))
            add(RestaurantAmenityItem("Booth seating", AmenityIconType.BarSeat))
        }

        val accessibility = listOf(
            RestaurantAmenityItem("Wheelchair-accessible entrance", AmenityIconType.Wheelchair),
            RestaurantAmenityItem("Accessible restroom", AmenityIconType.Restroom),
            RestaurantAmenityItem("High chairs available", AmenityIconType.HighChair),
            RestaurantAmenityItem("Gender-neutral restroom", AmenityIconType.Restroom),
            RestaurantAmenityItem("Air-conditioned dining room", AmenityIconType.AcUnit),
        )

        val arrival = buildList {
            add(RestaurantAmenityItem("Street parking nearby", AmenityIconType.Parking))
            if (seed % 4 != 0) add(RestaurantAmenityItem("Valet parking", AmenityIconType.Valet))
            add(RestaurantAmenityItem("Reservations recommended", AmenityIconType.Reservations))
            add(RestaurantAmenityItem("Walk-ins welcome", AmenityIconType.WalkIns))
            add(RestaurantAmenityItem("Average wait: ${ext.deliveryTime}", AmenityIconType.AccessTime))
        }

        val foodAndDrink = buildList {
            add(RestaurantAmenityItem("Full bar", AmenityIconType.FullBar))
            if (hasWine || hasFineDining) add(RestaurantAmenityItem("Sommelier on staff", AmenityIconType.Sommelier))
            add(RestaurantAmenityItem("Vegan options", AmenityIconType.Vegan))
            add(RestaurantAmenityItem("Gluten-free options", AmenityIconType.GlutenFree))
            add(RestaurantAmenityItem("Kids menu", AmenityIconType.KidsMenu))
            if (seed % 5 == 0) add(RestaurantAmenityItem("Halal options", AmenityIconType.Halal))
            add(RestaurantAmenityItem("Takeout available", AmenityIconType.Takeout))
        }

        val atmosphere = buildList {
            if (seed % 3 == 0) add(RestaurantAmenityItem("Live music (select nights)", AmenityIconType.LiveMusic))
            add(RestaurantAmenityItem("Quiet tables on request", AmenityIconType.Quiet))
            add(RestaurantAmenityItem("Romantic lighting", AmenityIconType.Romantic))
            if (seed % 2 == 0)             add(RestaurantAmenityItem("Heated outdoor patio", AmenityIconType.OutdoorHeating))
        }

        val policies = buildList {
            add(RestaurantAmenityItem("Phone: ${ext.phone}", AmenityIconType.Phone))
            if (seed % 4 == 0) add(RestaurantAmenityItem("Pet-friendly patio", AmenityIconType.PetFriendly))
            add(RestaurantAmenityItem("Corkage available", AmenityIconType.Corkage))
            add(RestaurantAmenityItem("Birthday setups on request", AmenityIconType.Birthday))
            add(RestaurantAmenityItem("Smart casual dress code", AmenityIconType.DressCode))
        }

        return listOf(
            AmenityCategory("Dining & seating", dining),
            AmenityCategory("Accessibility & family", accessibility),
            AmenityCategory("Parking & arrival", arrival),
            AmenityCategory("Food & beverage", foodAndDrink),
            AmenityCategory("Atmosphere", atmosphere),
            AmenityCategory("Reservations & policies", policies),
        )
    }
}
