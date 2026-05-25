package com.mh.restaurantchainreservation.feature.dining.ui

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import com.mh.restaurantchainreservation.core.designsystem.components.CollapsingScreenTitleHeader
import com.mh.restaurantchainreservation.core.designsystem.components.trackBottomNavScroll
import com.mh.restaurantchainreservation.core.designsystem.components.CollapsingTitleHeaderMetrics
import com.mh.restaurantchainreservation.core.designsystem.components.HubSurfaceCardDefaults
import com.mh.restaurantchainreservation.core.designsystem.components.hubTitleCollapseProgress
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
private val DiningListHorizontalPadding = 16.dp
private val DiningTabsTopInset = 0.dp
private val DiningTabsSectionGap = 20.dp
private val DiningListBottomPadding = 32.dp
private val DiningListScrollEndPadding = 64.dp

@OptIn(ExperimentalFoundationApi::class)
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
    val tabIds = remember { DiningTabs.map { it.id } }
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = tabIds.indexOf(currentTab).coerceAtLeast(0),
        pageCount = { tabIds.size },
    )
    var isProgrammaticTabChange by remember { mutableStateOf(false) }

    LaunchedEffect(currentTab) {
        val page = tabIds.indexOf(currentTab).coerceAtLeast(0)
        if (pagerState.currentPage != page) {
            isProgrammaticTabChange = true
            pagerState.animateScrollToPage(page)
            isProgrammaticTabChange = false
        }
    }

    LaunchedEffect(tabIds) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            if (isProgrammaticTabChange) return@collect
            val tab = tabIds[page]
            if (currentTab != tab) {
                currentTab = tab
            }
        }
    }

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
        val collapsedHeaderTotal = statusBarTopDp + CollapsingTitleHeaderMetrics.collapsedBodyHeight

        val collapseProgress by remember {
            derivedStateOf { listState.hubTitleCollapseProgress(collapseRangePx) }
        }

        val diningTabsStickyTopPadding by remember(density, collapsedHeaderTotal) {
            derivedStateOf {
                val tabHeader = listState.layoutInfo.visibleItemsInfo
                    .firstOrNull { it.key == LIST_TABS_KEY }
                if (tabHeader != null) {
                    val offsetDp = with(density) { tabHeader.offset.toDp() }
                    (collapsedHeaderTotal - offsetDp).coerceIn(0.dp, collapsedHeaderTotal)
                } else {
                    collapsedHeaderTotal
                }
            }
        }

        val isTabsPinnedUnderHeader by remember(density) {
            derivedStateOf {
                val tabHeader = listState.layoutInfo.visibleItemsInfo
                    .firstOrNull { it.key == LIST_TABS_KEY }
                when {
                    tabHeader == null -> true
                    else -> with(density) { tabHeader.offset.toDp() } <= 1.dp
                }
            }
        }

        val diningTabsContentTopPadding = if (isTabsPinnedUnderHeader) 0.dp else DiningTabsTopInset

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .pageCanvasBackground()
                .trackBottomNavScroll()
                .zIndex(0f),
            contentPadding = PaddingValues(bottom = DiningListScrollEndPadding),
        ) {
            item(key = LIST_TOP_SPACER_KEY) {
                Spacer(
                    Modifier.height(
                        CollapsingTitleHeaderMetrics.expandedBodyHeight + statusBarTopDp + 16.dp,
                    ),
                )
            }

            item(key = LIST_HERO_KEY) {
                Column(
                    modifier = Modifier.padding(horizontal = DiningListHorizontalPadding),
                    verticalArrangement = Arrangement.spacedBy(HubSurfaceCardDefaults.SectionSpacing),
                ) {
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
                    DiningStaggerItem(indexInGroup = 2) {
                        DiningDiscoverTonightSection(
                            onViewAll = onExploreRestaurants,
                            onExplore = { onExploreRestaurants() },
                        )
                    }
                }
            }

            item(key = LIST_SECTION_GAP_KEY) {
                Spacer(Modifier.height(DiningTabsSectionGap))
            }

            stickyHeader(key = LIST_TABS_KEY) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(palette.pageBackground)
                        .padding(top = diningTabsStickyTopPadding + diningTabsContentTopPadding)
                        .zIndex(1f),
                ) {
                    DiningTabBar(
                        selected = currentTab,
                        counts = tabCounts,
                        pinnedUnderHeader = isTabsPinnedUnderHeader,
                        modifier = Modifier.padding(horizontal = DiningListHorizontalPadding),
                        onSelect = { tab ->
                            currentTab = tab
                            coroutineScope.launch {
                                val page = tabIds.indexOf(tab).coerceAtLeast(0)
                                if (pagerState.currentPage != page) {
                                    isProgrammaticTabChange = true
                                    pagerState.animateScrollToPage(page)
                                    isProgrammaticTabChange = false
                                }
                            }
                        },
                    )
                }
            }

            item(key = LIST_TAB_CONTENT_KEY) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = HubSurfaceCardDefaults.SectionSpacing)
                        .graphicsLayer { clip = false },
                    beyondViewportPageCount = 1,
                    verticalAlignment = Alignment.Top,
                ) { page ->
                    val tab = tabIds[page]
                    DiningTabPageContent(
                        tab = tab,
                        upcoming = upcoming,
                        visited = visited,
                        cancelled = cancelled,
                        checkedInIds = checkedInIds,
                        onExploreRestaurants = onExploreRestaurants,
                        onOpenDetail = onOpenDetail,
                    )
                }
            }
        }

        CollapsingScreenTitleHeader(
            title = stringResource(I18nR.string.dining_page_title),
            collapseProgress = collapseProgress,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(2f),
            trailing = {
                DiningAddDinnerHeaderButton(
                    onClick = { DiningStore.openAddBookingPicker() },
                )
            },
        )
    }
}

@Composable
private fun DiningTabPageContent(
    tab: DiningTabId,
    upcoming: List<Booking>,
    visited: List<Booking>,
    cancelled: List<Booking>,
    checkedInIds: Set<String>,
    onExploreRestaurants: () -> Unit,
    onOpenDetail: (String) -> Unit,
) {
    when (tab) {
        DiningTabId.Upcoming -> TabContent(
            items = upcoming,
            onExploreRestaurants = onExploreRestaurants,
            onAddBooking = { DiningStore.openAddBookingPicker() },
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
            onAddBooking = { DiningStore.openAddBookingPicker() },
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
            onAddBooking = { DiningStore.openAddBookingPicker() },
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
            modifier = Modifier
                .padding(horizontal = DiningListHorizontalPadding)
                .padding(bottom = DiningListBottomPadding),
        )
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(HubSurfaceCardDefaults.SectionSpacing),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DiningListHorizontalPadding)
                .padding(bottom = DiningListBottomPadding),
        ) {
            items.forEachIndexed { index, booking ->
                DiningStaggerItem(indexInGroup = index) {
                    renderItem(booking)
                }
            }
        }
    }
}
