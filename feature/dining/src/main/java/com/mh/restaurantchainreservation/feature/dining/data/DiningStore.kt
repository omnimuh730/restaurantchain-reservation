package com.mh.restaurantchainreservation.feature.dining.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object DiningStore {
    private const val PrefsName = "tonight_dining_prefs"
    private const val CheckedInKey = "checked_in_booking_ids"

    private val _bookings = MutableStateFlow(MOCK_BOOKINGS)
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    private val _checkedInIds = MutableStateFlow<Set<String>>(emptySet())
    val checkedInIds: StateFlow<Set<String>> = _checkedInIds.asStateFlow()

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs != null) return
        val applicationPrefs = context.applicationContext.getSharedPreferences(PrefsName, Context.MODE_PRIVATE)
        prefs = applicationPrefs
        val stored = applicationPrefs.getStringSet(CheckedInKey, emptySet()) ?: emptySet()
        _checkedInIds.value = stored.toSet()
    }

    fun setBookings(next: List<Booking>) {
        _bookings.value = next
    }

    fun updateBooking(updated: Booking) {
        _bookings.value = _bookings.value.map { if (it.id == updated.id) updated else it }
    }

    fun upsertBookingFront(booking: Booking) {
        val current = _bookings.value
        val existing = current.any { it.id == booking.id || it.confirmationNo == booking.confirmationNo }
        if (existing) return
        _bookings.value = listOf(booking) + current
    }

    fun cancelBooking(bookingId: String) {
        _bookings.value = _bookings.value.map { booking ->
            if (booking.id == bookingId) booking.copy(status = BookingStatus.Cancelled) else booking
        }
        removeCheckedIn(bookingId)
    }

    fun addCheckedIn(bookingId: String) {
        val next = _checkedInIds.value.toMutableSet().apply { add(bookingId) }
        _checkedInIds.value = next
        prefs?.edit()?.putStringSet(CheckedInKey, next)?.apply()
    }

    fun removeCheckedIn(bookingId: String) {
        if (!_checkedInIds.value.contains(bookingId)) return
        val next = _checkedInIds.value.toMutableSet().apply { remove(bookingId) }
        _checkedInIds.value = next
        prefs?.edit()?.putStringSet(CheckedInKey, next)?.apply()
    }
}
