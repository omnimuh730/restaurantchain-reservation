package com.mh.restaurantchainreservation.feature.discover.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.LocalParking
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import androidx.compose.ui.window.DialogProperties
import com.mh.restaurantchainreservation.core.designsystem.transition.LocalAnimatedContentScope
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButton
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButtonSize
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButtonStyle
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.transition.LocalRestaurantSharedTransitionScope
import com.mh.restaurantchainreservation.core.designsystem.transition.rememberRestaurantSharedHeroModifier
import com.mh.restaurantchainreservation.core.designsystem.transition.rememberRestaurantSharedTitleModifier
import com.mh.restaurantchainreservation.core.model.DiscoverData
import com.mh.restaurantchainreservation.core.model.LocationStore
import com.mh.restaurantchainreservation.core.model.Restaurant
import com.mh.restaurantchainreservation.core.model.WishlistStore

private enum class ResultSheetState { Peek, Half, Full }

/** Opaque search + plan chrome below the status bar (filter tags sit below this on the map). */
private val SearchResultsChromeHeight = 130.dp

/** Filter chip row overlaid on the map under the search/plan chrome. */
private val SearchResultsFilterTagsHeight = 52.dp

/** Extra height above peek before the results list is shown (peek = header only). */
private val ResultsSheetListRevealThreshold = 20.dp

/** Map top inset: search/plan chrome only (filter row overlays the map below). */
private val SearchResultsMapTopInset = SearchResultsChromeHeight

private fun fullSheetTopClearance(statusBarTop: Dp): Dp =
    statusBarTop +
        SearchResultsChromeHeight

private fun ResultSheetState.sheetHeightDp(
    maxHeight: Dp,
    statusBarTop: Dp,
    peekHeight: Dp,
): Dp =
    when (this) {
        ResultSheetState.Peek -> peekHeight
        ResultSheetState.Half -> maxHeight * 0.48f
        ResultSheetState.Full -> maxHeight - fullSheetTopClearance(statusBarTop)
    }

private fun sheetStateFromHeightPx(
    rawPx: Float,
    peekPx: Float,
    halfPx: Float,
    fullPx: Float,
): ResultSheetState {
    val midPeekHalf = (peekPx + halfPx) / 2f
    val midHalfFull = (halfPx + fullPx) / 2f
    return when {
        rawPx < midPeekHalf -> ResultSheetState.Peek
        rawPx < midHalfFull -> ResultSheetState.Half
        else -> ResultSheetState.Full
    }
}

@Composable
fun DiscoverSearchResultsScreen(
    query: String,
    planSummary: String,
    locationId: String = "",
    onBack: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenRestaurant: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var sheetState by remember { mutableStateOf(ResultSheetState.Half) }
    var activeMarker by remember { mutableIntStateOf(0) }
    var filtersOpen by remember { mutableStateOf(false) }
    var filters by remember { mutableStateOf(SearchFilterState()) }
    var draftFilters by remember { mutableStateOf(SearchFilterState()) }
    var plan by remember { mutableStateOf(SearchPlanState()) }
    var planPickerOpen by remember { mutableStateOf(false) }
    var planPickerColumn by remember { mutableStateOf(PlanPickerColumn.Date) }
    var liveSummary by remember(planSummary) { mutableStateOf(planSummary) }

    val currentLocation by LocationStore.current.collectAsState()
    val locationIdTrimmed = locationId.trim()
    val matched = remember(query, locationIdTrimmed, currentLocation.name) {
        val base = when {
            locationIdTrimmed.isNotEmpty() -> {
                val fromCity = if (query.equals(DiscoverData.localFavoritesSearchQuery, ignoreCase = true)) {
                    DiscoverData.localFavoritesForCity(locationIdTrimmed)
                } else {
                    DiscoverData.byCity(locationIdTrimmed)
                }
                when {
                    query.isBlank() -> fromCity
                    query.equals(DiscoverData.localFavoritesSearchQuery, ignoreCase = true) -> fromCity
                    else -> matchRestaurantsInList(fromCity, query).ifEmpty { fromCity }
                }
            }
            query.isBlank() -> DiscoverData.ALL
            else -> matchRestaurants(query).ifEmpty { DiscoverData.ALL }
        }
        if (locationIdTrimmed.isEmpty() && query.trim().equals(currentLocation.name, ignoreCase = true)) {
            base.sortedBy { it.distance }
        } else {
            base
        }
    }
    val filtered = remember(matched, filters) { applyFilters(matched, filters) }

    LaunchedEffect(filtered.size) {
        if (filtered.isNotEmpty() && activeMarker > filtered.lastIndex) {
            activeMarker = 0
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F1EA)),
    ) {
        val appliedChips = remember(filters) { buildAppliedFilterChips(filters) }
        val headerHeight = SearchResultsMapTopInset
        val peekHeight = 64.dp
        val density = LocalDensity.current
        val statusBars = WindowInsets.statusBars
        val statusBarTopDp = remember(density, statusBars) {
            with(density) { statusBars.getTop(density).toDp() }
        }
        val fullTopClearance = remember(statusBarTopDp) { fullSheetTopClearance(statusBarTopDp) }
        val peekPx = remember(density, peekHeight) { with(density) { peekHeight.toPx() } }
        val halfPx = remember(density, maxHeight) { with(density) { (maxHeight * 0.48f).toPx() } }
        val fullPx = remember(density, maxHeight, fullTopClearance, peekPx) {
            with(density) {
                (maxHeight - fullTopClearance).toPx().coerceAtLeast(peekPx)
            }
        }
        val sheetHeightAnim = remember(peekPx, halfPx) { Animatable(halfPx) }
        var isDraggingSheet by remember { mutableStateOf(false) }
        var mapZoomUserOverride by remember { mutableStateOf(false) }
        var mapZoom by remember { mutableFloatStateOf(mapZoomForSheetProgress(0.5f)) }
        var mapRecenterSignal by remember { mutableIntStateOf(0) }
        val scope = rememberCoroutineScope()

        val listRevealThresholdPx = remember(density) {
            with(density) { ResultsSheetListRevealThreshold.toPx() }
        }
        val showSheetListContent = remember(sheetHeightAnim.value, peekPx, listRevealThresholdPx) {
            sheetHeightAnim.value > peekPx + listRevealThresholdPx
        }
        val sheetLinkedZoom = remember(sheetHeightAnim.value, peekPx, halfPx) {
            mapZoomForSheetHeight(sheetHeightAnim.value, peekPx, halfPx)
        }

        SideEffect {
            if (!mapZoomUserOverride || isDraggingSheet) {
                mapZoom = sheetLinkedZoom
            }
        }

        val sheetHeightDp = remember(sheetHeightAnim.value, density) {
            with(density) { sheetHeightAnim.value.toDp() }
        }

        fun sheetTargetPx(state: ResultSheetState): Float = when (state) {
            ResultSheetState.Peek -> peekPx
            ResultSheetState.Half -> halfPx
            ResultSheetState.Full -> fullPx
        }

        fun animateToSheetState(state: ResultSheetState) {
            sheetState = state
            mapZoomUserOverride = false
            scope.launch {
                sheetHeightAnim.animateTo(
                    targetValue = sheetTargetPx(state),
                    animationSpec = spring(dampingRatio = 0.86f, stiffness = 380f),
                )
            }
        }

        DiscoverSearchMapSurface(
            restaurants = filtered,
            activeMarker = activeMarker,
            distanceFilter = filters.distance,
            zoom = mapZoom,
            onZoomChange = { newZoom ->
                mapZoomUserOverride = true
                mapZoom = newZoom
            },
            topInset = headerHeight,
            bottomInset = sheetHeightDp,
            recenterSignal = mapRecenterSignal,
            onMarkerSelect = { index ->
                activeMarker = index
                animateToSheetState(ResultSheetState.Peek)
            },
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .zIndex(0f),
        )

        if (showSheetListContent && !filtersOpen && !planPickerOpen) {
            MapSheetDismissScrim(
                topInset = headerHeight,
                bottomInset = sheetHeightDp,
                onDismiss = { animateToSheetState(ResultSheetState.Peek) },
                modifier = Modifier.zIndex(0.5f),
            )
        }

        if (!showSheetListContent && filtered.isNotEmpty()) {
            MapPreviewCarousel(
                restaurants = filtered,
                activeIndex = activeMarker.coerceIn(0, filtered.lastIndex),
                onChangeIndex = { activeMarker = it },
                peekBottomPadding = peekHeight + 8.dp,
                onOpenRestaurant = onOpenRestaurant,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .zIndex(5f),
            )
        }

        SearchFilterTagsRow(
            filters = filters,
            appliedChips = appliedChips,
            onOpenFilters = {
                draftFilters = filters
                filtersOpen = true
            },
            onRemoveAppliedFilter = { chipId -> filters = removeAppliedFilterById(filters, chipId) },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(top = SearchResultsChromeHeight)
                .zIndex(12f),
        )

        SearchMapControlsColumn(
            mapZoom = mapZoom,
            onRecenter = {
                mapRecenterSignal++
                mapZoomUserOverride = false
                mapZoom = sheetLinkedZoom
            },
            onZoomIn = {
                mapZoomUserOverride = true
                mapZoom = (mapZoom + MAP_ZOOM_STEP).coerceAtMost(MAP_MAX_ZOOM)
            },
            onZoomOut = {
                mapZoomUserOverride = true
                mapZoom = (mapZoom - MAP_ZOOM_STEP).coerceAtLeast(MAP_MIN_ZOOM)
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(
                    top = SearchResultsChromeHeight + SearchResultsFilterTagsHeight + 12.dp,
                    end = 16.dp,
                )
                .zIndex(18f),
        )

        ResultsSheet(
            restaurants = filtered,
            query = query,
            sheetState = sheetState,
            sheetHeight = sheetHeightDp,
            showListContent = showSheetListContent,
            peekPx = peekPx,
            halfPx = halfPx,
            fullPx = fullPx,
            onSheetDragStart = {
                isDraggingSheet = true
                mapZoomUserOverride = false
            },
            onSheetDrag = { deltaPx ->
                scope.launch {
                    sheetHeightAnim.snapTo(
                        (sheetHeightAnim.value - deltaPx).coerceIn(peekPx, fullPx),
                    )
                }
            },
            onSheetDragEnd = {
                isDraggingSheet = false
                val chosen = sheetStateFromHeightPx(sheetHeightAnim.value, peekPx, halfPx, fullPx)
                animateToSheetState(chosen)
            },
            onCollapseToMap = { animateToSheetState(ResultSheetState.Peek) },
            onOpenRestaurant = onOpenRestaurant,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(25f),
        )

        SearchResultsHeader(
            query = query,
            plan = plan,
            planSummary = liveSummary,
            onBack = onBack,
            onOpenSearch = onOpenSearch,
            onOpenPlanPicker = { column ->
                planPickerColumn = column
                planPickerOpen = true
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .zIndex(30f),
        )

        PlanPickerSheet(
            visible = planPickerOpen,
            initial = plan,
            onDismiss = { planPickerOpen = false },
            onApply = { p ->
                plan = p
                liveSummary = p.displaySummary()
            },
        )

        if (filtersOpen) {
            SearchFiltersSheet(
                filters = draftFilters,
                resultCount = applyFilters(matched, draftFilters).size,
                onChange = { draftFilters = it },
                onClear = { draftFilters = SearchFilterState() },
                onApply = {
                    filters = draftFilters
                    filtersOpen = false
                },
                onClose = { filtersOpen = false },
            )
        }
    }
}

@Composable
private fun SearchFilterTagsRow(
    filters: SearchFilterState,
    appliedChips: List<AppliedFilterChip>,
    onOpenFilters: () -> Unit,
    onRemoveAppliedFilter: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var pendingRemoval by remember { mutableStateOf<AppliedFilterChip?>(null) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FilterChipButton(
            activeFilterCount = filters.activeCount,
            onClick = onOpenFilters,
        )
        appliedChips.forEach { chip ->
            AppliedFilterTag(
                label = chip.label,
                onClick = { pendingRemoval = chip },
            )
        }
    }

    pendingRemoval?.let { chip ->
        RemoveFilterDialog(
            filterLabel = chip.label,
            onDismiss = { pendingRemoval = null },
            onConfirm = {
                onRemoveAppliedFilter(chip.id)
                pendingRemoval = null
            },
        )
    }
}

@Composable
private fun SearchResultsHeader(
    query: String,
    plan: SearchPlanState,
    planSummary: String,
    onBack: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenPlanPicker: (PlanPickerColumn) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current

    Column(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(palette.cardSurface.copy(alpha = 0.97f))
                .border(1.dp, palette.borderSoft)
                .padding(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CircleIcon(icon = Icons.AutoMirrored.Filled.ArrowBack, label = "Back", onClick = onBack)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(palette.cardSurface)
                    .border(1.dp, palette.border, RoundedCornerShape(percent = 50))
                    .clickable(onClick = onOpenSearch)
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.Search, contentDescription = null, tint = palette.foreground, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(9.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = query.ifBlank { "All restaurants" },
                        color = palette.foreground,
                        fontSize = 15.sp,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                    )
                    Text(
                        planSummary,
                        color = palette.mutedForeground,
                        fontSize = 11.sp,
                        maxLines = 1,
                    )
                }
                if (query.isNotBlank()) {
                    Icon(Icons.Filled.Close, contentDescription = null, tint = palette.mutedForeground, modifier = Modifier.size(15.dp))
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(palette.cardSurface)
                .border(1.dp, palette.border, RoundedCornerShape(percent = 50)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlanSegmentButton(
                icon = Icons.Outlined.CalendarMonth,
                label = plan.dateSegmentLabel(),
                onClick = { onOpenPlanPicker(PlanPickerColumn.Date) },
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(28.dp)
                    .background(Color(0xFFDDDDDD)),
            )
            PlanSegmentButton(
                icon = Icons.Outlined.AccessTime,
                label = plan.hourSegmentLabel(),
                onClick = { onOpenPlanPicker(PlanPickerColumn.Time) },
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(28.dp)
                    .background(Color(0xFFDDDDDD)),
            )
            PlanSegmentButton(
                icon = Icons.Outlined.Groups,
                label = plan.guestSegmentLabel(),
                onClick = { onOpenPlanPicker(PlanPickerColumn.Guests) },
                modifier = Modifier.weight(1f),
            )
        }
        }
    }
}

@Composable
private fun FilterChipButton(
    activeFilterCount: Int,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(Color.White)
            .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(percent = 50))
            .clickable(role = Role.Button, onClickLabel = "Filters", onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(Icons.Outlined.Tune, contentDescription = "Filters", tint = Color(0xFF222222), modifier = Modifier.size(15.dp))
        Text("Filters", color = Color(0xFF222222), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        if (activeFilterCount > 0) {
            val badgeLabel = if (activeFilterCount > 9) "9+" else activeFilterCount.toString()
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF385C)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = badgeLabel,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 10.sp,
                )
            }
        }
    }
}

@Composable
private fun AppliedFilterTag(
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(Color.White)
            .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(percent = 50))
            .clickable(role = Role.Button, onClickLabel = "Remove $label", onClick = onClick)
            .padding(start = 14.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(label, color = Color(0xFF222222), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(Color(0xFFF0F0F0)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Close, contentDescription = null, tint = Color(0xFF717171), modifier = Modifier.size(10.dp))
        }
    }
}

@Composable
private fun RemoveFilterDialog(
    filterLabel: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(palette.cardSurface),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(palette.brand.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Delete, contentDescription = null, tint = palette.brand, modifier = Modifier.size(26.dp))
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    "Remove this filter?",
                    color = palette.foreground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "This will remove $filterLabel",
                    color = palette.mutedForeground,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
            }
            HorizontalDivider(color = palette.borderSoft)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .border(1.dp, palette.border, RoundedCornerShape(percent = 50))
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Cancel", color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(palette.brand)
                        .clickable(onClick = onConfirm),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Remove", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PlanSegmentButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(icon, contentDescription = null, tint = palette.foreground, modifier = Modifier.size(16.dp))
        Text(
            label,
            color = palette.foreground,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier.padding(start = 4.dp),
        )
        Icon(
            imageVector = Icons.Outlined.ExpandMore,
            contentDescription = null,
            tint = palette.mutedForeground,
            modifier = Modifier
                .size(14.dp)
                .padding(start = 2.dp),
        )
    }
}

@Composable
private fun MapSheetDismissScrim(
    topInset: Dp,
    bottomInset: Dp,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = topInset, bottom = bottomInset)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onDismiss,
            ),
    )
}

@Composable
private fun ResultsSheet(
    restaurants: List<Restaurant>,
    query: String,
    sheetState: ResultSheetState,
    sheetHeight: Dp,
    showListContent: Boolean,
    peekPx: Float,
    halfPx: Float,
    fullPx: Float,
    onSheetDragStart: () -> Unit,
    onSheetDrag: (Float) -> Unit,
    onSheetDragEnd: () -> Unit,
    onCollapseToMap: () -> Unit,
    onOpenRestaurant: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val showMapFab = showListContent && restaurants.isNotEmpty()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(sheetHeight)
            .shadow(12.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(palette.cardSurface)
            .border(1.dp, palette.borderSoft, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
    ) {
        Column(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(peekPx, halfPx, fullPx) {
                        detectVerticalDragGestures(
                            onDragStart = { onSheetDragStart() },
                            onDragEnd = { onSheetDragEnd() },
                            onDragCancel = { onSheetDragEnd() },
                            onVerticalDrag = { _, dragAmount ->
                                onSheetDrag(dragAmount)
                            },
                        )
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 40.dp, height = 4.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(Color(0xFFD1D1D1)),
                    )
                }
                Text(
                    text = if (restaurants.isEmpty()) "No restaurants found" else "Over 1,000 results",
                    color = palette.foreground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    textAlign = TextAlign.Center,
                )
            }
            if (showListContent) {
                if (restaurants.isEmpty()) {
                    EmptyResults(query = query, modifier = Modifier.weight(1f))
                } else {
                    val listBottom = when {
                        sheetState == ResultSheetState.Full && showMapFab -> 96.dp
                        showMapFab -> 80.dp
                        sheetState == ResultSheetState.Full -> 40.dp
                        else -> 28.dp
                    }
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = listBottom),
                        verticalArrangement = Arrangement.spacedBy(22.dp),
                    ) {
                        items(restaurants, key = { it.id }) { restaurant ->
                            RestaurantResultCard(
                                restaurant = restaurant,
                                onOpen = { onOpenRestaurant(restaurant.id) },
                            )
                        }
                    }
                }
            }
        }
        if (showMapFab) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 18.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(Color(0xFF222222))
                    .clickable(onClick = onCollapseToMap)
                    .padding(horizontal = 18.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("Map", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Icon(Icons.Outlined.Map, contentDescription = "Show map", tint = Color.White, modifier = Modifier.size(17.dp))
            }
        }
    }
}

@Composable
private fun RestaurantResultCard(restaurant: Restaurant, onOpen: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val collections by WishlistStore.collections.collectAsState()
    val saved = collections.any { col -> col.restaurants.any { it.id == restaurant.id } }
    val shared = LocalRestaurantSharedTransitionScope.current
    val animatedContent = LocalAnimatedContentScope.current
    val heroModifier = rememberRestaurantSharedHeroModifier(restaurant.id, shared, animatedContent)
    val titleModifier = rememberRestaurantSharedTitleModifier(restaurant.id, shared, animatedContent)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(palette.mutedSurface),
            ) {
                AsyncImage(
                    model = restaurant.image,
                    contentDescription = restaurant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .then(heroModifier),
                )
                Text(
                    text = restaurant.tag ?: "Guest favorite",
                    color = Color(0xFF222222),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(Color.White)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                )
                HeartButton(
                    active = saved,
                    onClick = { WishlistStore.openPicker(restaurant) },
                    size = HeartButtonSize.Large,
                    style = HeartButtonStyle.Overlay,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                )
            }
            Row(
                modifier = Modifier.padding(top = 12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        restaurant.name,
                        color = palette.foreground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = titleModifier,
                    )
                    Text(
                        text = "${restaurant.cuisine} - ${restaurant.area ?: restaurant.distance}",
                        color = palette.mutedForeground,
                        fontSize = 13.sp,
                        maxLines = 1,
                    )
                    Text(
                        text = "Tables tonight - ${restaurant.price} for tonight",
                        color = palette.mutedForeground,
                        fontSize = 13.sp,
                        maxLines = 1,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFB400), modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(3.dp))
                    Text("%.1f".format(restaurant.rating), color = palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MapPreviewCarousel(
    restaurants: List<Restaurant>,
    activeIndex: Int,
    onChangeIndex: (Int) -> Unit,
    peekBottomPadding: Dp,
    onOpenRestaurant: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    if (restaurants.isEmpty()) return

    val pageCount = restaurants.size
    val safeIndex = activeIndex.coerceIn(0, restaurants.lastIndex)
    val pagerState = rememberPagerState(
        initialPage = safeIndex,
        pageCount = { pageCount },
    )

    LaunchedEffect(safeIndex, pageCount) {
        if (pagerState.currentPage != safeIndex) {
            pagerState.animateScrollToPage(safeIndex)
        }
    }

    LaunchedEffect(pagerState, pageCount) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page ->
                if (page in restaurants.indices && page != activeIndex) {
                    onChangeIndex(page)
                }
            }
    }

    BoxWithConstraints(
        modifier = modifier.padding(bottom = peekBottomPadding),
    ) {
        val cardWidth = 300.dp
        val sidePadding = ((maxWidth - cardWidth) / 2).coerceAtLeast(16.dp)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            pageSpacing = 12.dp,
            contentPadding = PaddingValues(horizontal = sidePadding),
            beyondViewportPageCount = 1,
        ) { page ->
            val restaurant = restaurants[page]
            val selected = page == activeIndex
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .border(
                        width = if (selected) 2.dp else 1.dp,
                        color = if (selected) Color(0xFF222222) else palette.border,
                        shape = RoundedCornerShape(22.dp),
                    ),
            ) {
                MapPreviewCard(
                    restaurant = restaurant,
                    index = page,
                    onOpen = { onOpenRestaurant(restaurant.id) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun MapPreviewCard(
    restaurant: Restaurant,
    index: Int,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val collections by WishlistStore.collections.collectAsState()
    val saved = collections.any { col -> col.restaurants.any { it.id == restaurant.id } }
    val shared = LocalRestaurantSharedTransitionScope.current
    val animatedContent = LocalAnimatedContentScope.current
    val heroModifier = rememberRestaurantSharedHeroModifier(restaurant.id, shared, animatedContent)
    val titleModifier = rememberRestaurantSharedTitleModifier(restaurant.id, shared, animatedContent)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(136.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(palette.cardSurface)
            .clickable(onClick = onOpen),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(136.dp)
                .background(palette.mutedSurface),
        ) {
            AsyncImage(
                model = restaurant.image,
                contentDescription = restaurant.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .then(heroModifier),
            )
            Text(
                text = if (index % 2 == 0) "Trophy pick" else "Guest favorite",
                color = palette.foreground,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(Color.White)
                    .padding(horizontal = 9.dp, vertical = 4.dp),
            )
            HeartButton(
                active = saved,
                onClick = { WishlistStore.openPicker(restaurant) },
                size = HeartButtonSize.Small,
                style = HeartButtonStyle.Overlay,
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = restaurant.name,
                        color = palette.foreground,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier
                            .weight(1f)
                            .then(titleModifier),
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = palette.foreground, modifier = Modifier.size(13.dp))
                        Text("%.1f".format(restaurant.rating), color = palette.foreground, fontSize = 12.sp)
                    }
                }
                Text("${restaurant.cuisine} - ${restaurant.area ?: restaurant.distance}", color = palette.mutedForeground, fontSize = 13.sp, maxLines = 1)
                Text("Open tonight - ${restaurant.distance}", color = palette.mutedForeground, fontSize = 13.sp, maxLines = 1)
            }
            Text(
                text = "${restaurant.price} for tonight",
                color = palette.foreground,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun EmptyResults(query: String, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 38.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(Icons.Outlined.Search, contentDescription = null, tint = palette.mutedForeground.copy(alpha = 0.35f), modifier = Modifier.size(48.dp))
        Text("No results for ${query.ifBlank { "this search" }}", color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text("Try a different keyword or clear filters.", color = palette.mutedForeground, fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 5.dp))
    }
}

@Composable
private fun SearchFiltersSheet(
    filters: SearchFilterState,
    resultCount: Int,
    onChange: (SearchFilterState) -> Unit,
    onClear: () -> Unit,
    onApply: () -> Unit,
    onClose: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = true,
        ),
    ) {
        val scrimInteraction = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.30f))
                .clickable(
                    interactionSource = scrimInteraction,
                    indication = null,
                    onClick = onClose,
                ),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(720.dp)
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(palette.cardSurface)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    )
                    .windowInsetsPadding(WindowInsets.navigationBars),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(1.dp, palette.borderSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Filters", color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    CircleIcon(icon = Icons.Filled.Close, label = "Close", onClick = onClose, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp))
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                ) {
                    FilterSection(title = "Sort by") {
                        FlowPillsWrap(
                            labels = DiscoverSearchData.filterSortOptions,
                            selected = filters.sortBy,
                            onToggle = { label ->
                                onChange(filters.copy(sortBy = filters.sortBy.toggle(label)))
                            },
                        )
                    }
                    HorizontalDivider(color = palette.borderSoft)
                    FilterSection(title = "Recommended for you") {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            RecommendedFilterCard(
                                label = "Open now",
                                icon = Icons.Outlined.AccessTime,
                                active = filters.openNow,
                                onClick = { onChange(filters.copy(openNow = !filters.openNow)) },
                                modifier = Modifier.weight(1f),
                            )
                            RecommendedFilterCard(
                                label = "Instant Book",
                                icon = Icons.Outlined.Bolt,
                                active = filters.instantBook,
                                onClick = { onChange(filters.copy(instantBook = !filters.instantBook)) },
                                modifier = Modifier.weight(1f),
                            )
                            RecommendedFilterCard(
                                label = "Parking",
                                icon = Icons.Outlined.LocalParking,
                                active = filters.amenities.contains("Parking"),
                                onClick = { onChange(filters.copy(amenities = filters.amenities.toggle("Parking"))) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                    HorizontalDivider(color = palette.borderSoft)
                    FilterSection(title = "Cuisine") {
                        FlowPillsWrap(
                            labels = DiscoverSearchData.filterCuisineChips,
                            selected = filters.cuisines,
                            onToggle = { onChange(filters.copy(cuisines = filters.cuisines.toggle(it))) },
                        )
                    }
                    HorizontalDivider(color = palette.borderSoft)
                    FilterSection(
                        title = "Price range",
                        subtitle = "Average table price, includes fees",
                    ) {
                        PriceHistogramBars()
                        Spacer(Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            listOf("$", "$$", "$$$", "$$$$").forEach { price ->
                                FilterPill(
                                    label = price,
                                    active = filters.prices.contains(price),
                                    onClick = { onChange(filters.copy(prices = filters.prices.toggle(price))) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = palette.borderSoft)
                    FilterSection(title = "Rating") {
                        FlowSingleSelectPills(
                            labels = DiscoverSearchData.ratingFilterOptions,
                            selected = filters.rating,
                            onSelect = { onChange(filters.copy(rating = it)) },
                        )
                    }
                    HorizontalDivider(color = palette.borderSoft)
                    FilterSection(title = "Distance") {
                        FlowSingleSelectPills(
                            labels = DiscoverSearchData.distanceFilterOptions,
                            selected = filters.distance,
                            onSelect = { onChange(filters.copy(distance = it)) },
                        )
                    }
                    HorizontalDivider(color = palette.borderSoft)
                    FilterSection(title = "Amenities") {
                        FlowPillsWrap(
                            labels = DiscoverSearchData.filterAmenityChips,
                            selected = filters.amenities,
                            onToggle = { onChange(filters.copy(amenities = filters.amenities.toggle(it))) },
                        )
                    }
                    HorizontalDivider(color = palette.borderSoft)
                    FilterSection(title = "Seating") {
                        FlowPillsWrap(
                            labels = DiscoverSearchData.filterSeatingChips,
                            selected = filters.seating,
                            onToggle = { onChange(filters.copy(seating = filters.seating.toggle(it))) },
                        )
                    }
                    HorizontalDivider(color = palette.borderSoft)
                    FilterSection(title = "Occasion") {
                        FlowPillsWrap(
                            labels = DiscoverSearchData.filterOccasionChips,
                            selected = filters.occasions,
                            onToggle = { onChange(filters.copy(occasions = filters.occasions.toggle(it))) },
                        )
                    }
                }
                HorizontalDivider(color = palette.borderSoft)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, palette.borderSoft)
                        .padding(horizontal = 22.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Clear all",
                        color = if (filters.activeCount > 0) palette.foreground else palette.mutedForeground.copy(alpha = 0.55f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(enabled = filters.activeCount > 0, onClick = onClear)
                            .padding(vertical = 8.dp),
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(palette.foreground)
                            .clickable(onClick = onApply)
                            .padding(horizontal = 24.dp, vertical = 13.dp),
                    ) {
                        Text(
                            text = "Apply",
                            color = palette.cardSurface,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterSection(title: String, subtitle: String? = null, content: @Composable () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 16.dp),
    ) {
        Text(title, color = palette.foreground, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        if (subtitle != null) {
            Text(subtitle, color = palette.mutedForeground, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
        }
        Column(modifier = Modifier.padding(top = 12.dp)) { content() }
    }
}

@Composable
private fun RecommendedFilterCard(
    label: String,
    icon: ImageVector,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val cardInteraction = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .clickable(
                interactionSource = cardInteraction,
                indication = null,
                onClick = onClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .border(
                    width = if (active) 2.dp else 1.dp,
                    color = if (active) palette.foreground else palette.border,
                    shape = RoundedCornerShape(14.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = if (active) palette.foreground else palette.mutedForeground, modifier = Modifier.size(38.dp))
            if (active) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(palette.foreground),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = palette.cardSurface, modifier = Modifier.size(13.dp))
                }
            }
        }
        Text(label, color = palette.foreground, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp), maxLines = 2)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowPillsWrap(
    labels: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        labels.forEach { label ->
            FilterPill(
                label = label,
                active = selected.contains(label),
                onClick = { onToggle(label) },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowSingleSelectPills(
    labels: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        labels.forEach { label ->
            FilterPill(
                label = label,
                active = selected == label,
                onClick = { onSelect(label) },
            )
        }
    }
}

@Composable
private fun PriceHistogramBars() {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(palette.mutedSurface.copy(alpha = 0.5f))
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        val heights = listOf(0.15f, 0.22f, 0.35f, 0.55f, 0.72f, 0.9f, 1f, 0.88f, 0.7f, 0.5f, 0.38f, 0.28f, 0.2f, 0.18f, 0.12f, 0.1f, 0.08f, 0.12f, 0.2f, 0.35f, 0.5f, 0.42f, 0.3f, 0.2f, 0.15f, 0.12f, 0.1f, 0.14f, 0.22f, 0.3f, 0.25f, 0.18f, 0.12f, 0.1f, 0.08f, 0.06f)
        heights.forEach { h ->
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight(h)
                    .clip(RoundedCornerShape(2.dp))
                    .background(palette.brand.copy(alpha = 0.85f)),
            )
        }
    }
}

@Composable
private fun FlowPills(labels: List<String>, selected: Set<String>, onToggle: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        labels.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { label ->
                    FilterPill(label = label, active = selected.contains(label), onClick = { onToggle(label) })
                }
            }
        }
    }
}

@Composable
private fun FilterPill(label: String, active: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(if (active) palette.foreground else palette.cardSurface)
            .border(1.dp, if (active) palette.foreground else palette.border, RoundedCornerShape(percent = 50))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (active) palette.cardSurface else palette.foreground,
            fontSize = 13.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
        )
    }
}

@Composable
private fun CircleIcon(icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClickLabel = label, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = label, tint = palette.foreground, modifier = Modifier.size(20.dp))
    }
}

private fun Set<String>.toggle(value: String): Set<String> = if (contains(value)) this - value else this + value

private fun parseMiles(distanceLabel: String): Double? {
    val regex = Regex("""([\d.]+)\s*mi""", RegexOption.IGNORE_CASE)
    return regex.find(distanceLabel)?.groupValues?.getOrNull(1)?.toDoubleOrNull()
}

private fun minRatingForFilter(option: String): Double? = when (option) {
    "3+" -> 3.0
    "3.5+" -> 3.5
    "4+" -> 4.0
    "4.5+" -> 4.5
    else -> null
}

/** Deterministic mock match so multi-select amenity filters narrow results without backend fields. */
private fun mockFeatureMatch(restaurantId: String, category: String, label: String, hitRate: Int = 62): Boolean {
    val v = (restaurantId.hashCode() * 31 + category.hashCode() * 17 + label.hashCode()) and 0x7fffffff
    return v % 100 < hitRate
}

private fun applyFilters(restaurants: List<Restaurant>, filters: SearchFilterState): List<Restaurant> {
    val minRating = minRatingForFilter(filters.rating)
    val maxMiles = maxMilesForDistanceFilter(filters.distance)
    val filtered = restaurants.filter { r ->
        val priceOk = filters.prices.isEmpty() || filters.prices.contains(r.price)
        val cuisineOk = filters.cuisines.isEmpty() || filters.cuisines.any { r.cuisine.contains(it, ignoreCase = true) }
        val ratingOk = minRating == null || r.rating >= minRating
        val miles = parseMiles(r.distance)
        val distanceOk = maxMiles == null || miles == null || miles <= maxMiles + 1e-6
        val openOk = !filters.openNow || mockFeatureMatch(r.id, "open", "open-now", 68)
        val instantOk = !filters.instantBook || mockFeatureMatch(r.id, "book", "instant", 52)
        val amenitiesOk = filters.amenities.isEmpty() ||
            filters.amenities.all { mockFeatureMatch(r.id, "amenity", it, 58) }
        val seatingOk = filters.seating.isEmpty() ||
            filters.seating.all { mockFeatureMatch(r.id, "seating", it, 55) }
        val occasionsOk = filters.occasions.isEmpty() ||
            filters.occasions.all { mockFeatureMatch(r.id, "occasion", it, 55) }
        priceOk && cuisineOk && ratingOk && distanceOk && openOk && instantOk && amenitiesOk && seatingOk && occasionsOk
    }
    return applySortOrdering(filtered, filters.sortBy)
}

private fun applySortOrdering(list: List<Restaurant>, sortBy: Set<String>): List<Restaurant> {
    if (sortBy.isEmpty()) return list
    val comparators = mutableListOf<Comparator<Restaurant>>()
    if ("Highest Rated" in sortBy) comparators.add(compareByDescending { it.rating })
    if ("Most Reviewed" in sortBy) comparators.add(compareByDescending { it.reviews })
    if ("Nearest" in sortBy) comparators.add(compareBy { parseMiles(it.distance) ?: Double.MAX_VALUE })
    if ("Price: Low to High" in sortBy) comparators.add(compareBy { it.price.length })
    if ("Price: High to Low" in sortBy) comparators.add(compareByDescending { it.price.length })
    if (comparators.isEmpty()) return list
    val combined = comparators.reduce { acc, next -> acc.then(next) }
    return list.sortedWith(combined)
}

private fun matchRestaurantsInList(restaurants: List<Restaurant>, query: String): List<Restaurant> {
    if (query.isBlank()) return restaurants
    val q = query.trim().lowercase()
    return restaurants.filter {
        it.name.lowercase().contains(q) ||
            it.cuisine.lowercase().contains(q) ||
            (it.area ?: "").lowercase().contains(q) ||
            (it.tag ?: "").lowercase().contains(q)
    }
}

private fun matchRestaurants(query: String): List<Restaurant> {
    if (query.isBlank()) return DiscoverData.ALL
    val q = query.trim().lowercase()
    return DiscoverData.ALL.filter {
        it.name.lowercase().contains(q) ||
            it.cuisine.lowercase().contains(q) ||
            (it.area ?: "").lowercase().contains(q) ||
            (it.tag ?: "").lowercase().contains(q)
    }
}
