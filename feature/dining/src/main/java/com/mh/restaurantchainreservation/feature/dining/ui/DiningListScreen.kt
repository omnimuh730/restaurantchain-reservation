package com.mh.restaurantchainreservation.feature.dining.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.mh.restaurantchainreservation.core.designsystem.components.CollapsingScreenTitleHeader
import com.mh.restaurantchainreservation.core.designsystem.components.CollapsingTitleHeaderMetrics
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.feature.dining.data.Booking
import com.mh.restaurantchainreservation.feature.dining.data.BookingStatus
import com.mh.restaurantchainreservation.feature.dining.data.DiningStore

@Composable
fun DiningListScreen(
    onOpenDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { DiningStore.init(context) }

    val palette = LocalRestaurantPalette.current
    val bookings by DiningStore.bookings.collectAsState()
    val checkedInIds by DiningStore.checkedInIds.collectAsState()

    var currentTab by rememberSaveable { mutableStateOf(DiningTabId.Upcoming) }

    val upcoming = remember(bookings) {
        bookings.filter { it.status == BookingStatus.Pending || it.status == BookingStatus.Confirmed }
    }
    val approved = remember(bookings) { bookings.filter { it.status == BookingStatus.Confirmed } }
    val visited = remember(bookings) { bookings.filter { it.status == BookingStatus.Completed } }
    val cancelled = remember(bookings) {
        bookings.filter { it.status == BookingStatus.Cancelled || it.status == BookingStatus.NoShow }
    }
    val tabCounts = remember(upcoming, visited, cancelled) {
        mapOf(
            DiningTabId.Upcoming to upcoming.size,
            DiningTabId.Visited to visited.size,
            DiningTabId.Cancel to cancelled.size,
        )
    }
    val nextBooking = approved.firstOrNull()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.cardSurface),
    ) {
        val scroll = rememberScrollState()
        val density = LocalDensity.current
        val collapseRangePx = remember(density) {
            with(density) {
                (CollapsingTitleHeaderMetrics.expandedBodyHeight - CollapsingTitleHeaderMetrics.collapsedBodyHeight)
                    .toPx()
            }
                .coerceAtLeast(1f)
        }
        val collapseProgress = (scroll.value / collapseRangePx).coerceIn(0f, 1f)
        val statusBarTopDp = with(density) { WindowInsets.statusBars.getTop(this).toDp() }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .zIndex(0f)
                .padding(horizontal = 20.dp)
                .padding(bottom = 48.dp),
        ) {
            Spacer(Modifier.height(CollapsingTitleHeaderMetrics.expandedBodyHeight + statusBarTopDp))
            Spacer(Modifier.height(16.dp))

            // Hero block: NextUp + StatsGrid (staggered)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            DiningStaggerItem(indexInGroup = 0) {
                if (nextBooking != null) {
                    NextUpCard(
                        booking = nextBooking,
                        onClick = { onOpenDetail(nextBooking.id) },
                    )
                } else {
                    EmptyNextCard()
                }
            }
            DiningStaggerItem(indexInGroup = 1) {
                StatsGrid(
                    placesVisited = visited.size,
                    totalBookings = upcoming.size + visited.size + cancelled.size,
                )
            }
            }

            Spacer(Modifier.height(32.dp))

            // Tabs section
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            DiningTabBar(
                selected = currentTab,
                counts = tabCounts,
                onSelect = { currentTab = it },
            )
            DiningListHeader(
                tab = currentTab,
                onAddByCode = { DiningStore.openAddCode() },
            )

            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    (slideInVertically(animationSpec = tween(220)) { it / 6 } + fadeIn(animationSpec = tween(220)))
                        .togetherWith(
                            slideOutVertically(animationSpec = tween(220)) { -it / 6 } + fadeOut(animationSpec = tween(220)),
                        )
                },
                label = "dining_tab_content",
            ) { tab ->
                when (tab) {
                    DiningTabId.Upcoming -> TabContent(
                        items = upcoming,
                        emptyIcon = Icons.Outlined.CalendarToday,
                        emptyTitleRes = I18nR.string.dining_empty_upcoming_title,
                        emptyDescRes = I18nR.string.dining_empty_upcoming_desc,
                        renderItem = { booking ->
                            BookingCard(
                                booking = booking,
                                checkedInIds = checkedInIds,
                                onTap = { onOpenDetail(booking.id) },
                                onManage = if (booking.status == BookingStatus.Confirmed) {
                                    { DiningStore.openManage(booking.id) }
                                } else null,
                                onScanQR = if (booking.status == BookingStatus.Confirmed) {
                                    { DiningStore.openScan(booking.id) }
                                } else null,
                                onShowQR = if (booking.status == BookingStatus.Confirmed) {
                                    { DiningStore.openShowQR(booking.id) }
                                } else null,
                                onInvite = if (booking.status == BookingStatus.Confirmed) {
                                    { DiningStore.openInvite(booking.id) }
                                } else null,
                                onBookAgain = { onOpenDetail(booking.id) },
                            )
                        },
                    )
                    DiningTabId.Visited -> TabContent(
                        items = visited,
                        emptyIcon = Icons.Outlined.CheckCircle,
                        emptyTitleRes = I18nR.string.dining_empty_visited_title,
                        emptyDescRes = I18nR.string.dining_empty_visited_desc,
                        renderItem = { booking ->
                            BookingCard(
                                booking = booking,
                                onTap = { onOpenDetail(booking.id) },
                                onBookAgain = { onOpenDetail(booking.id) },
                                onViewReceipt = { DiningStore.openReceipt(booking.id) },
                            )
                        },
                    )
                    DiningTabId.Cancel -> TabContent(
                        items = cancelled,
                        emptyIcon = Icons.Outlined.Cancel,
                        emptyTitleRes = I18nR.string.dining_empty_cancel_title,
                        emptyDescRes = I18nR.string.dining_empty_cancel_desc,
                        renderItem = { booking ->
                            BookingCard(
                                booking = booking,
                                onTap = { onOpenDetail(booking.id) },
                                onBookAgain = { onOpenDetail(booking.id) },
                            )
                        },
                    )
                }
            }
            }
        }

        CollapsingScreenTitleHeader(
            title = stringResource(I18nR.string.dining_page_title),
            collapseProgress = collapseProgress,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(2f),
        )
    }
}

@Composable
private fun TabContent(
    items: List<Booking>,
    emptyIcon: androidx.compose.ui.graphics.vector.ImageVector,
    emptyTitleRes: Int,
    emptyDescRes: Int,
    renderItem: @Composable (Booking) -> Unit,
) {
    if (items.isEmpty()) {
        EmptyDiningState(
            icon = emptyIcon,
            title = stringResource(emptyTitleRes),
            description = stringResource(emptyDescRes),
        )
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            items.forEachIndexed { index, booking ->
                DiningStaggerItem(indexInGroup = index) {
                    renderItem(booking)
                }
            }
        }
    }
}
