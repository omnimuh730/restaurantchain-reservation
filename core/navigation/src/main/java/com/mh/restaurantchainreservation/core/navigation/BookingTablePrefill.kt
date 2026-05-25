package com.mh.restaurantchainreservation.core.navigation

import com.mh.restaurantchainreservation.feature.booking.BookTableInitialState
import com.mh.restaurantchainreservation.feature.booking.BookTableResult
import com.mh.restaurantchainreservation.feature.booking.AMENITY_OPTIONS
import com.mh.restaurantchainreservation.feature.booking.CUISINE_PREFS
import com.mh.restaurantchainreservation.feature.booking.SEATING_OPTIONS
import com.mh.restaurantchainreservation.feature.booking.VIBE_OPTIONS
import com.mh.restaurantchainreservation.feature.booking.RestaurantDetailData
import com.mh.restaurantchainreservation.feature.booking.bookingDayOptions
import com.mh.restaurantchainreservation.feature.booking.formatStoredBookingDate
import com.mh.restaurantchainreservation.feature.booking.labelsForPrefIds
import com.mh.restaurantchainreservation.feature.booking.matchOccasionId
import com.mh.restaurantchainreservation.feature.booking.matchPrefOptionIds
import com.mh.restaurantchainreservation.feature.booking.occasionLabel
import com.mh.restaurantchainreservation.feature.booking.resolveInitialDateSelection
import com.mh.restaurantchainreservation.feature.dining.data.Booking
import com.mh.restaurantchainreservation.feature.dining.data.BookingStatus
import com.mh.restaurantchainreservation.feature.dining.data.displayCuisineLabels
import com.mh.restaurantchainreservation.feature.dining.data.displaySeatingLabels
import com.mh.restaurantchainreservation.core.model.Restaurant

object BookingTablePrefill {
    fun fromBooking(booking: Booking): BookTableInitialState {
        val days = bookingDayOptions()
        val (dateIndex, customDate) = resolveInitialDateSelection(booking.date, days)
        val seatingIds = matchPrefOptionIds(SEATING_OPTIONS, booking.displaySeatingLabels())
            .ifEmpty { matchPrefOptionIds(SEATING_OPTIONS, listOfNotNull(booking.seating.takeIf { it.isNotBlank() })) }
        return BookTableInitialState(
            existingBookingId = booking.id,
            guests = booking.guests,
            selectedDateIndex = dateIndex,
            customDate = customDate,
            selectedTime = booking.time,
            contactName = booking.contactName.orEmpty(),
            phone = booking.phone,
            notes = booking.specialRequest.orEmpty(),
            occasion = matchOccasionId(booking.occasion),
            seating = seatingIds,
            cuisinePrefs = matchPrefOptionIds(CUISINE_PREFS, booking.displayCuisineLabels()),
            vibes = matchPrefOptionIds(VIBE_OPTIONS, booking.vibeLabels),
            amenities = matchPrefOptionIds(AMENITY_OPTIONS, booking.amenityLabels),
        )
    }

    fun applyResult(booking: Booking, result: BookTableResult): Booking {
        val seatingLabels = labelsForPrefIds(SEATING_OPTIONS, result.seating)
        val cuisineLabels = labelsForPrefIds(CUISINE_PREFS, result.cuisinePrefs)
        val vibeLabels = labelsForPrefIds(VIBE_OPTIONS, result.vibes)
        val amenityLabels = labelsForPrefIds(AMENITY_OPTIONS, result.amenities)
        return booking.copy(
            date = formatStoredBookingDate(result.bookingDate),
            time = result.selectedTime,
            guests = result.guests,
            contactName = result.contactName.takeIf { it.isNotBlank() },
            phone = result.phone,
            specialRequest = result.notes.takeIf { it.isNotBlank() },
            occasion = result.occasion?.let { occasionLabel(it) },
            seating = seatingLabels.firstOrNull() ?: booking.seating,
            seatingLabels = seatingLabels,
            cuisineLabels = cuisineLabels,
            vibeLabels = vibeLabels,
            amenityLabels = amenityLabels,
        )
    }

    fun createPendingBooking(
        confirmationNo: String,
        restaurant: Restaurant,
        result: BookTableResult,
    ): Booking {
        val ext = RestaurantDetailData.extendedData(restaurant)
        val seatingLabels = labelsForPrefIds(SEATING_OPTIONS, result.seating)
        val cuisineLabels = labelsForPrefIds(CUISINE_PREFS, result.cuisinePrefs)
        val vibeLabels = labelsForPrefIds(VIBE_OPTIONS, result.vibes)
        val amenityLabels = labelsForPrefIds(AMENITY_OPTIONS, result.amenities)
        return Booking(
            id = "bk-$confirmationNo",
            restaurant = restaurant.name,
            cuisine = restaurant.cuisine,
            image = restaurant.image,
            date = formatStoredBookingDate(result.bookingDate),
            time = result.selectedTime,
            guests = result.guests,
            status = BookingStatus.Pending,
            address = ext.address,
            phone = ext.phone,
            diningPoints = 0,
            specialRequest = result.notes.takeIf { it.isNotBlank() },
            occasion = result.occasion?.let { occasionLabel(it) },
            seating = seatingLabels.firstOrNull() ?: "Any",
            confirmationNo = confirmationNo,
            contactName = result.contactName.takeIf { it.isNotBlank() },
            seatingLabels = seatingLabels,
            cuisineLabels = cuisineLabels,
            vibeLabels = vibeLabels,
            amenityLabels = amenityLabels,
        )
    }
}
