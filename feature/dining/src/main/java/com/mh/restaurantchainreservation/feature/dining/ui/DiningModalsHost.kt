package com.mh.restaurantchainreservation.feature.dining.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mh.restaurantchainreservation.feature.dining.data.DiningStore
import com.mh.restaurantchainreservation.feature.dining.ui.modals.AddBookingCodeModal
import com.mh.restaurantchainreservation.feature.dining.ui.modals.CancelConfirmModal
import com.mh.restaurantchainreservation.feature.dining.ui.modals.InviteFriendsSheet
import com.mh.restaurantchainreservation.feature.dining.ui.modals.ManageSheet
import com.mh.restaurantchainreservation.feature.dining.ui.modals.ModifyModal
import com.mh.restaurantchainreservation.feature.dining.ui.modals.OrderReceiptModal
import com.mh.restaurantchainreservation.feature.dining.ui.modals.ShowQRModal
import com.mh.restaurantchainreservation.feature.dining.ui.scanqr.ScanQRFlowScreen

/**
 * Renders every dining bottom-sheet / dialog / full-screen flow driven by `DiningStore.modal`.
 * Place once inside the dining navigation surface — modals will open/close as state changes.
 */
@Composable
fun DiningModalsHost(
    onOpenBookingDetail: ((bookingId: String) -> Unit)? = null,
    onBookAgain: ((bookingId: String) -> Unit)? = null,
) {
    val modal by DiningStore.modal.collectAsState()
    val bookings by DiningStore.bookings.collectAsState()
    val checkedInIds by DiningStore.checkedInIds.collectAsState()
    val invitedMap by DiningStore.invitedMap.collectAsState()

    val manageBooking = DiningStore.bookingById(modal.manageBookingId)
    val modifyBooking = DiningStore.bookingById(modal.modifyBookingId)
    val cancelBooking = DiningStore.bookingById(modal.cancelBookingId)
    val showQrBooking = DiningStore.bookingById(modal.showQrBookingId)
    val inviteBooking = DiningStore.bookingById(modal.inviteBookingId)
    val receiptBooking = DiningStore.bookingById(modal.receiptBookingId)
    val scanBooking = DiningStore.bookingById(modal.scanBookingId)

    if (modal.showManage && manageBooking != null) {
        ManageSheet(
            onDismiss = { DiningStore.closeManage() },
            onModify = { DiningStore.openModify() },
            onCancel = { DiningStore.openCancelConfirm() },
        )
    }

    if (modal.showModify && modifyBooking != null) {
        ModifyModal(
            booking = modifyBooking,
            onDismiss = { DiningStore.closeModify() },
            onSave = { updated -> DiningStore.updateBooking(updated) },
        )
    }

    if (modal.showCancel && cancelBooking != null) {
        CancelConfirmModal(
            restaurantName = cancelBooking.restaurant,
            onDismiss = { DiningStore.closeCancel() },
            onConfirm = {
                DiningStore.cancelBooking(cancelBooking.id)
                DiningStore.closeCancel()
            },
        )
    }

    if (showQrBooking != null) {
        ShowQRModal(
            booking = showQrBooking,
            onDismiss = { DiningStore.closeShowQR() },
        )
    }

    if (inviteBooking != null) {
        InviteFriendsSheet(
            booking = inviteBooking,
            invited = invitedMap[inviteBooking.id] ?: emptySet(),
            onDismiss = { DiningStore.closeInvite() },
            onInvitedChanged = { ids -> DiningStore.setInvitedFor(inviteBooking.id, ids) },
        )
    }

    if (receiptBooking != null) {
        OrderReceiptModal(
            booking = receiptBooking,
            onDismiss = { DiningStore.closeReceipt() },
        )
    }

    if (modal.addCodeOpen) {
        AddBookingCodeModal(
            bookings = bookings,
            checkedInIds = checkedInIds,
            onDismiss = { DiningStore.closeAddCode() },
            onAdded = { booking -> DiningStore.upsertBookingFront(booking) },
            onView = { booking ->
                DiningStore.closeAddCode()
                onOpenBookingDetail?.invoke(booking.id)
            },
        )
    }

    if (scanBooking != null) {
        Dialog(
            onDismissRequest = { DiningStore.closeScan() },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false,
            ),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                ScanQRFlowScreen(
                    booking = scanBooking,
                    initialStep = modal.scanInitialStep,
                    onCheckedIn = { DiningStore.addCheckedIn(scanBooking.id) },
                    onClose = { DiningStore.closeScan() },
                )
            }
        }
    }
}
