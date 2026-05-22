package com.mh.restaurantchainreservation.feature.dining.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import com.mh.restaurantchainreservation.core.designsystem.components.trackBottomNavScroll
import com.mh.restaurantchainreservation.core.designsystem.components.CollapsingTitleHeaderMetrics
import com.mh.restaurantchainreservation.core.designsystem.components.HubSurfaceCardDefaults
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.tokens.pageCanvasBackground
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.feature.dining.data.Booking
import com.mh.restaurantchainreservation.feature.dining.data.BookingStatus
import com.mh.restaurantchainreservation.feature.dining.data.DiningStore

private const val LIST_TOP_SPACER_KEY = "dining_top_spacer"
private const val LIST_HERO_KEY = "dining_hero"
private const val LIST_SECTION_GAP_KEY = "dining_section_gap"
private const val LIST_TABS_KEY = "dining_tabs"
private const val LIST_TAB_CONTENT_KEY = "dining_tab_content"

/** Lazy list index of [LIST_TABS_KEY] (spacer, hero, gap, tabs, content). */
private const val DINING_TABS_ITEM_INDEX = 3

/** Transparent strip between collapsed header border and pinned tab card. */
private val DiningTabBarPinnedGapBelowHeader = 10.dp

private val DiningTabBarListSlotHeight = DiningTabBarHeight

/** Horizontal inset for the tab card in the list (matches [LazyColumn] content padding). */
private val DiningTabBarScrollHorizontalPadding = 16.dp

/** Tighter inset when pinned so the tab card reads slightly wider. */
private val DiningTabBarPinnedHorizontalPadding = 8.dp

@Composable
fun DiningListScreen(
    onOpenDetail: (String) -> Unit,
    onExploreRestaurants: () -> Unit,
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
            DiningTabId.EmptyPreview to 0,
        )
    }
    val nextBooking = approved.firstOrNull()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.pageBackground),
    ) {
        val listState = rememberLazyListState()
        val density = LocalDensity.current
        val collapseRangePx = remember(density) {
            with(density) {
                (CollapsingTitleHeaderMetrics.expandedBodyHeight - CollapsingTitleHeaderMetrics.collapsedBodyHeight)
                    .toPx()
            }
                .coerceAtLeast(1f)
        }
        val statusBarTopDp = with(density) { WindowInsets.statusBars.getTop(this).toDp() }
        val collapsedHeaderBottomDp =
            statusBarTopDp + CollapsingTitleHeaderMetrics.collapsedBodyHeight
        val pinnedTabBarTopDp = collapsedHeaderBottomDp + DiningTabBarPinnedGapBelowHeader
        val pinnedTabBarTopPx = remember(density, pinnedTabBarTopDp) {
            with(density) { pinnedTabBarTopDp.roundToPx() }
        }

        val collapseProgress by remember {
            derivedStateOf { listState.collapseProgress(collapseRangePx) }
        }
        val tabBarOverlayTopPx by remember {
            derivedStateOf { listState.tabBarOverlayTopPx(pinnedTabBarTopPx) }
        }
        val overlayTopPx = tabBarOverlayTopPx
        val tabBarOverlayTopDp = overlayTopPx?.let { with(density) { it.toDp() } }
        val isTabBarPinned = overlayTopPx != null && overlayTopPx <= pinnedTabBarTopPx

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .pageCanvasBackground()
                .trackBottomNavScroll()
                .zIndex(0f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 48.dp),
        ) {
            item(key = LIST_TOP_SPACER_KEY) {
                Spacer(
                    Modifier.height(
                        CollapsingTitleHeaderMetrics.expandedBodyHeight + statusBarTopDp + 16.dp,
                    ),
                )
            }

            item(key = LIST_HERO_KEY) {
                Column(verticalArrangement = Arrangement.spacedBy(HubSurfaceCardDefaults.SectionSpacing)) {
                    DiningStaggerItem(indexInGroup = 0) {
                        if (nextBooking != null) {
                            NextUpCard(
                                booking = nextBooking,
                                onClick = { onOpenDetail(nextBooking.id) },
                                onQrClick = { DiningStore.openShowQR(nextBooking.id) },
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
            }

            item(key = LIST_SECTION_GAP_KEY) {
                Spacer(Modifier.height(32.dp))
            }

            item(key = LIST_TABS_KEY) {
                Column {
                    if (tabBarOverlayTopPx == null) {
                        DiningTabBar(
                            selected = currentTab,
                            counts = tabCounts,
                            onSelect = { currentTab = it },
                        )
                    } else {
                        Spacer(Modifier.height(DiningTabBarListSlotHeight))
                    }
                }
            }

            item(key = LIST_TAB_CONTENT_KEY) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(HubSurfaceCardDefaults.SectionSpacing),
                    modifier = Modifier.padding(top = HubSurfaceCardDefaults.SectionSpacing),
                ) {
                    AnimatedContent(
                        targetState = currentTab,
                        transitionSpec = {
                            (slideInVertically(animationSpec = tween(220)) { it / 6 } + fadeIn(animationSpec = tween(220)))
                                .togetherWith(
                                    slideOutVertically(animationSpec = tween(220)) { -it / 6 } +
                                        fadeOut(animationSpec = tween(220)),
                                )
                        },
                        label = "dining_tab_content",
                    ) { tab ->
                        when (tab) {
                            DiningTabId.Upcoming -> TabContent(
                                items = upcoming,
                                onExploreRestaurants = onExploreRestaurants,
                                onAddBooking = { DiningStore.openAddCode() },
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
                                onExploreRestaurants = onExploreRestaurants,
                                onAddBooking = { DiningStore.openAddCode() },
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
                                onExploreRestaurants = onExploreRestaurants,
                                onAddBooking = { DiningStore.openAddCode() },
                                renderItem = { booking ->
                                    BookingCard(
                                        booking = booking,
                                        onTap = { onOpenDetail(booking.id) },
                                        onBookAgain = { onOpenDetail(booking.id) },
                                    )
                                },
                            )
                            DiningTabId.EmptyPreview -> TabContent(
                                items = emptyList(),
                                onExploreRestaurants = onExploreRestaurants,
                                onAddBooking = { DiningStore.openAddCode() },
                                renderItem = {},
                            )
                        }
                    }
                }
            }
        }

        tabBarOverlayTopDp?.let { topDp ->
            val tabBarHorizontalPadding by animateDpAsState(
                targetValue = if (isTabBarPinned) {
                    DiningTabBarPinnedHorizontalPadding
                } else {
                    DiningTabBarScrollHorizontalPadding
                },
                animationSpec = spring(stiffness = 380f, dampingRatio = 0.82f),
                label = "tab_bar_horizontal_padding",
            )
            DiningTabBar(
                selected = currentTab,
                counts = tabCounts,
                onSelect = { currentTab = it },
                pinned = isTabBarPinned,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .zIndex(1f)
                    .fillMaxWidth()
                    .padding(horizontal = tabBarHorizontalPadding)
                    .offset(y = topDp),
            )
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

private fun LazyListState.collapseProgress(collapseRangePx: Float): Float =
    if (firstVisibleItemIndex == 0) {
        (firstVisibleItemScrollOffset / collapseRangePx).coerceIn(0f, 1f)
    } else {
        1f
    }

/**
 * Screen Y offset for the floating tab card while it tracks scroll, clamped at [pinnedTopPx].
 * Returns null before the tab row has entered the overlay range (in-list placement only).
 */
private fun LazyListState.tabBarOverlayTopPx(pinnedTopPx: Int): Int? {
    val tabsItem = layoutInfo.visibleItemsInfo.find { it.key == LIST_TABS_KEY }
    return when {
        tabsItem != null -> maxOf(tabsItem.offset, pinnedTopPx)
        firstVisibleItemIndex > DINING_TABS_ITEM_INDEX -> pinnedTopPx
        else -> null
    }
}

@Composable
private fun TabContent(
    items: List<Booking>,
    onExploreRestaurants: () -> Unit,
    onAddBooking: () -> Unit,
    renderItem: @Composable (Booking) -> Unit,
) {
    if (items.isEmpty()) {
        DiningNoItemsCard(
            onExploreRestaurants = onExploreRestaurants,
            onAddBooking = onAddBooking,
        )
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(HubSurfaceCardDefaults.SectionSpacing),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items.forEachIndexed { index, booking ->
                DiningStaggerItem(indexInGroup = index) {
                    renderItem(booking)
                }
            }
        }
    }
}
