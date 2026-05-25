package com.mh.restaurantchainreservation.feature.dining.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Single in-memory + SharedPreferences-backed source of truth for the dining feature.
 * Mirrors `DiningPage` React state in a Compose-friendly shape.
 */
object DiningStore {
    private const val PrefsName = "tonight_dining_prefs"
    private const val CheckedInKey = "checked_in_booking_ids"
    private const val InvitedMapKey = "invited_map"

    private val _bookings = MutableStateFlow(MOCK_BOOKINGS)
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    private val _checkedInIds = MutableStateFlow<Set<String>>(emptySet())
    val checkedInIds: StateFlow<Set<String>> = _checkedInIds.asStateFlow()

    private val _invitedMap = MutableStateFlow<Map<String, Set<String>>>(emptyMap())
    val invitedMap: StateFlow<Map<String, Set<String>>> = _invitedMap.asStateFlow()

    /** Bottom-sheet / dialog state — matches React `useState` flags inside DiningPage. */
    enum class AddBookingMethod { Code, Scan }

    enum class AddBookingFlowStep { ChooseMethod, EnterCode, ScanQr, Review, Success }

    data class AddBookingFlowState(
        val step: AddBookingFlowStep,
        val method: AddBookingMethod? = null,
    )

    data class ModalState(
        val showManage: Boolean = false,
        val manageBookingId: String? = null,
        val showModify: Boolean = false,
        val modifyBookingId: String? = null,
        val showCancel: Boolean = false,
        val cancelBookingId: String? = null,
        val showQrBookingId: String? = null,
        val inviteBookingId: String? = null,
        val receiptBookingId: String? = null,
        val addCodeOpen: Boolean = false,
        val addBookingFlow: AddBookingFlowState? = null,
        val scanBookingId: String? = null,
        val scanInitialStep: ScanStep = ScanStep.Scan,
    )

    private val _modal = MutableStateFlow(ModalState())
    val modal: StateFlow<ModalState> = _modal.asStateFlow()

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs != null) return
        val applicationPrefs = context.applicationContext.getSharedPreferences(PrefsName, Context.MODE_PRIVATE)
        prefs = applicationPrefs
        _checkedInIds.value = applicationPrefs.getStringSet(CheckedInKey, emptySet())?.toSet() ?: emptySet()
    }

    fun bookingById(id: String?): Booking? = id?.let { _bookings.value.firstOrNull { booking -> booking.id == it } }

    /* ---------------- Booking mutations ---------------- */
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

    /* ---------------- Check-in (persisted) ---------------- */
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

    /* ---------------- Invitations ---------------- */
    fun setInvitedFor(bookingId: String, ids: Set<String>) {
        val next = _invitedMap.value.toMutableMap().apply { put(bookingId, ids) }
        _invitedMap.value = next
    }

    /* ---------------- Modal state mutations ---------------- */
    fun openManage(bookingId: String) {
        _modal.value = _modal.value.copy(showManage = true, manageBookingId = bookingId)
    }

    fun closeManage() {
        _modal.value = _modal.value.copy(showManage = false)
    }

    fun openModify() {
        val current = _modal.value
        _modal.value = current.copy(
            showManage = false,
            showModify = true,
            modifyBookingId = current.manageBookingId,
        )
    }

    fun openModify(bookingId: String) {
        _modal.value = _modal.value.copy(
            showManage = false,
            showModify = true,
            modifyBookingId = bookingId,
        )
    }

    fun closeModify() {
        _modal.value = _modal.value.copy(showModify = false, modifyBookingId = null)
    }

    fun openCancelConfirm() {
        val current = _modal.value
        _modal.value = current.copy(
            showManage = false,
            showCancel = true,
            cancelBookingId = current.manageBookingId,
        )
    }

    fun openCancel(bookingId: String) {
        _modal.value = _modal.value.copy(
            showManage = false,
            showCancel = true,
            cancelBookingId = bookingId,
        )
    }

    fun closeCancel() {
        _modal.value = _modal.value.copy(showCancel = false, cancelBookingId = null)
    }

    fun openShowQR(bookingId: String) {
        _modal.value = _modal.value.copy(showQrBookingId = bookingId)
    }

    fun closeShowQR() {
        _modal.value = _modal.value.copy(showQrBookingId = null)
    }

    fun openInvite(bookingId: String) {
        _modal.value = _modal.value.copy(inviteBookingId = bookingId)
    }

    fun closeInvite() {
        _modal.value = _modal.value.copy(inviteBookingId = null)
    }

    fun openReceipt(bookingId: String) {
        _modal.value = _modal.value.copy(receiptBookingId = bookingId)
    }

    fun closeReceipt() {
        _modal.value = _modal.value.copy(receiptBookingId = null)
    }

    fun openAddCode() {
        openAddBookingPicker()
    }

    fun closeAddCode() {
        closeAddBookingFlow()
    }

    fun openAddBookingPicker() {
        _modal.value = _modal.value.copy(
            addCodeOpen = false,
            addBookingFlow = AddBookingFlowState(
                step = AddBookingFlowStep.ChooseMethod,
            ),
        )
    }

    fun closeAddBookingFlow() {
        _modal.value = _modal.value.copy(addBookingFlow = null, addCodeOpen = false)
    }

    fun confirmAddBooking(source: Booking): Booking {
        val existing = _bookings.value.firstOrNull {
            it.confirmationNo.equals(source.confirmationNo, ignoreCase = true)
        }
        if (existing != null) return existing
        val newBooking = source.copy(
            id = "added-${System.currentTimeMillis()}",
            status = BookingStatus.Confirmed,
        )
        upsertBookingFront(newBooking)
        return _bookings.value.firstOrNull {
            it.confirmationNo.equals(source.confirmationNo, ignoreCase = true)
        } ?: newBooking
    }

    fun openScan(bookingId: String, step: ScanStep = ScanStep.Scan) {
        _modal.value = _modal.value.copy(scanBookingId = bookingId, scanInitialStep = step)
    }

    fun closeScan() {
        _modal.value = _modal.value.copy(scanBookingId = null, scanInitialStep = ScanStep.Scan)
    }
}
