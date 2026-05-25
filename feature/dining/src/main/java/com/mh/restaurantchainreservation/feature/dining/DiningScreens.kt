package com.mh.restaurantchainreservation.feature.dining

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.mh.restaurantchainreservation.feature.dining.data.BookingStatus
import com.mh.restaurantchainreservation.feature.dining.data.DiningStore
import com.mh.restaurantchainreservation.feature.dining.data.isCurrentlyDining
import com.mh.restaurantchainreservation.feature.dining.ui.BookingDetailScreen
import com.mh.restaurantchainreservation.feature.dining.ui.DiningListScreen
import com.mh.restaurantchainreservation.feature.dining.ui.DiningModalsHost
import com.mh.restaurantchainreservation.feature.dining.ui.EnjoyMealScreen
import com.mh.restaurantchainreservation.feature.dining.ui.EnjoyMode

object DiningRoutes {
    const val Home = "dining"
    const val DetailBase = "dining/detail"
    const val Detail = "dining/detail/{bookingId}"
    const val EnjoyBase = "dining/enjoy"
    const val Enjoy = "dining/enjoy/{bookingId}"

    fun detail(bookingId: String) = "$DetailBase/$bookingId"
    fun enjoy(bookingId: String) = "$EnjoyBase/$bookingId"
}

@Composable
fun DiningHomeScreen(
    onOpenDetail: (String) -> Unit,
    onExploreRestaurants: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { DiningStore.init(context) }

    Box(modifier = modifier.fillMaxSize()) {
        DiningListScreen(
            onOpenDetail = onOpenDetail,
            onExploreRestaurants = onExploreRestaurants,
        )
        DiningModalsHost(
            onOpenBookingDetail = onOpenDetail,
        )
    }
}

@Composable
fun DiningDetailScreen(
    bookingId: String,
    onBack: () -> Unit,
    onOpenEnjoy: (String) -> Unit,
    onModifyBooking: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { DiningStore.init(context) }
    val bookings by DiningStore.bookings.collectAsState()
    val booking = remember(bookings, bookingId) { bookings.firstOrNull { it.id == bookingId } } ?: return

    Box(modifier = modifier.fillMaxSize()) {
        BookingDetailScreen(
            booking = booking,
            onBack = onBack,
            onManage = { onModifyBooking(booking.id) },
            onCancel = { DiningStore.openCancel(booking.id) },
            onScanQR = { DiningStore.openScan(booking.id) },
            onShowQR = { DiningStore.openShowQR(booking.id) },
            onInvite = { DiningStore.openInvite(booking.id) },
            onBookAgain = onBack,
            onViewReceipt = { DiningStore.openReceipt(booking.id) },
            onDeleteRequest = {
                DiningStore.cancelBooking(booking.id)
                onBack()
            },
        )
        DiningModalsHost(
            onOpenBookingDetail = { /* already on detail */ },
        )
    }
}

@Composable
fun DiningEnjoyScreen(
    bookingId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { DiningStore.init(context) }
    val bookings by DiningStore.bookings.collectAsState()
    val checkedInIds by DiningStore.checkedInIds.collectAsState()
    val booking = remember(bookings, bookingId) { bookings.firstOrNull { it.id == bookingId } } ?: return

    val live = booking.status == BookingStatus.Confirmed && isCurrentlyDining(booking, checkedInIds = checkedInIds)
    val mode = if (live) EnjoyMode.Live else EnjoyMode.Upcoming

    Box(modifier = modifier.fillMaxSize()) {
        EnjoyMealScreen(
            booking = booking,
            mode = mode,
            onBack = onBack,
            onShowQR = { DiningStore.openShowQR(booking.id) },
            onScanQR = { DiningStore.openScan(booking.id) },
            onScanPay = { DiningStore.openScan(booking.id, com.mh.restaurantchainreservation.feature.dining.data.ScanStep.Bill) },
            onInvite = { DiningStore.openInvite(booking.id) },
            onOpenDirections = {},
            onCallServer = {},
        )
        DiningModalsHost()
    }
}
