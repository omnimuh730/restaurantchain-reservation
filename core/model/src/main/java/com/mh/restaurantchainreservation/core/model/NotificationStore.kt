package com.mh.restaurantchainreservation.core.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

enum class NotificationKind { Reservation, Promo, Reward, System, Review, Share }

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val read: Boolean,
    val kind: NotificationKind,
    val bookingId: String? = null,
    val tab: String? = null,
)

private val initial = listOf(
    AppNotification("n1", "Reservation Confirmed", "Your table at Sakura Omakase is confirmed for Apr 18 at 7:30 PM.", "2m ago", false, NotificationKind.Reservation, bookingId = "1", tab = "upcoming"),
    AppNotification("n10", "Sarah shared a restaurant", "Sarah Kim thought you would like Le Petit Bistro.", "6m ago", false, NotificationKind.Share),
    AppNotification("n11", "Reservation request pending", "Your request at Nami Counter is waiting for restaurant approval.", "9m ago", false, NotificationKind.Reservation, bookingId = "9", tab = "upcoming"),
    AppNotification("n12", "Reservation request rejected", "Riverside Tapas could not approve your request.", "12m ago", false, NotificationKind.Reservation, bookingId = "10"),
    AppNotification("n2", "Flash Deal: 30% Off", "Enjoy 30% off at Bella Napoli this weekend only!", "15m ago", false, NotificationKind.Promo),
    AppNotification("n3", "You Earned 425 pts!", "Points from your last visit have been credited.", "1h ago", false, NotificationKind.Reward),
    AppNotification("n4", "New Review Reply", "Chef Tanaka replied to your review at Sakura Omakase.", "3h ago", false, NotificationKind.Review),
    AppNotification("n5", "Booking Confirmed", "Your reservation at Le Jardin has been confirmed.", "5h ago", false, NotificationKind.Reservation, tab = "upcoming"),
    AppNotification("n6", "Weekend Picks", "Check out this week's top-rated restaurants near you.", "1d ago", true, NotificationKind.Promo),
    AppNotification("n7", "Reservation Reminder", "Don't forget your dinner at Golden Dragon tomorrow at 8 PM.", "1d ago", true, NotificationKind.Reservation),
    AppNotification("n8", "Tier Upgrade Progress", "Only 2,660 pts to reach Platinum tier!", "2d ago", true, NotificationKind.Reward),
    AppNotification("n9", "App Update Available", "Version 2.4.1 includes performance improvements.", "3d ago", true, NotificationKind.System),
)

object NotificationStore {
    private val state = MutableStateFlow(initial)
    val notifications: StateFlow<List<AppNotification>> = state.asStateFlow()

    fun observeUnreadCount() = state.map { list -> list.count { !it.read } }

    fun currentUnreadCount(): Int = state.value.count { !it.read }

    fun markAsRead(id: String) {
        state.value = state.value.map { if (it.id == id) it.copy(read = true) else it }
    }

    fun markAllAsRead() {
        state.value = state.value.map { it.copy(read = true) }
    }

    fun delete(id: String) {
        state.value = state.value.filter { it.id != id }
    }

    fun clearAll() {
        state.value = emptyList()
    }

    fun clearRead() {
        state.value = state.value.filter { !it.read }
    }

    fun clearUnread() {
        state.value = state.value.filter { it.read }
    }
}
