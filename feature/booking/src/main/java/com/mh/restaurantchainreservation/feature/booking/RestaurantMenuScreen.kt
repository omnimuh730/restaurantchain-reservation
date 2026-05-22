package com.mh.restaurantchainreservation.feature.booking

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlin.coroutines.coroutineContext
import kotlin.math.abs
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.isActive
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import kotlinx.coroutines.launch

private val MenuTabScrollEdgeInset = 16.dp
private const val MenuTabScrollFarJumpThreshold = 2
private const val MenuTabScrollDurationMillis = 320
private const val MenuTabScrollFastDurationMillis = 140

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantMenuScreen(
    restaurantName: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val categories = remember { RestaurantDetailData.menuCategories }
    if (categories.isEmpty()) return

    val lastCategoryIndex = categories.lastIndex
    val pagerState = rememberPagerState(pageCount = { categories.size })
    val tabRowState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val tabScrollEdgeInsetPx = remember(density) { with(density) { MenuTabScrollEdgeInset.roundToPx() } }
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)
    var displayedSelectedIndex by remember { mutableIntStateOf(0) }
    var pagerNavigationJob by remember { mutableStateOf<Job?>(null) }
    val currentPagerPage by remember { derivedStateOf { pagerState.currentPage } }

    LaunchedEffect(tabScrollEdgeInsetPx, lastCategoryIndex) {
        snapshotFlow { pagerState.isScrollInProgress to pagerState.currentPage }
            .distinctUntilChanged()
            .filter { (inProgress, _) -> !inProgress }
            .collect { (_, page) ->
                val safePage = page.coerceIn(0, lastCategoryIndex)
                if (displayedSelectedIndex != safePage) {
                    displayedSelectedIndex = safePage
                }
                tabRowState.scrollMenuTabIntoView(
                    index = safePage,
                    edgeInsetPx = tabScrollEdgeInsetPx,
                    fast = false,
                )
            }
    }

    val appBarBrush = remember(palette) {
        Brush.verticalGradient(
            colors = listOf(
                palette.gradientStart,
                palette.brand,
                palette.gradientEnd,
            ),
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.pageBackground),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(appBarBrush),
        ) {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Menu",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                        )
                        Text(
                            text = restaurantName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.White.copy(alpha = 0.88f),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                ),
                scrollBehavior = scrollBehavior,
            )
        }

        MenuCategoryTabRow(
            categories = categories,
            selectedIndex = displayedSelectedIndex,
            listState = tabRowState,
            onCategorySelected = { index ->
                val safeIndex = index.coerceIn(0, lastCategoryIndex)
                val distance = abs(safeIndex - displayedSelectedIndex)
                displayedSelectedIndex = safeIndex
                pagerNavigationJob?.cancel()
                pagerNavigationJob = scope.launch {
                    if (!isActive) return@launch
                    val fast = distance >= MenuTabScrollFarJumpThreshold
                    tabRowState.scrollMenuTabIntoView(
                        index = safeIndex,
                        edgeInsetPx = tabScrollEdgeInsetPx,
                        fast = fast,
                    )
                    if (!isActive) return@launch
                    if (fast) {
                        pagerState.scrollToPage(safeIndex)
                    } else {
                        pagerState.animateScrollToPage(safeIndex)
                    }
                }
            },
        )

        HorizontalDivider(color = palette.border)

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 1,
            userScrollEnabled = true,
        ) { page ->
            val category = categories[page]
            val items = remember(category) { RestaurantDetailData.menuForCategory(category) }
            val attachAppBarScroll = page == currentPagerPage
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (attachAppBarScroll) {
                            Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                        } else {
                            Modifier
                        },
                    ),
                contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp),
            ) {
                items(
                    items = items,
                    key = { "${category}_${it.name}" },
                ) { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                    ) {
                        RowMenuItem(item = item)
                    }
                }
            }
        }
    }
}

/**
 * Ensures the tab at [index] is fully visible inside the row, with a smooth scroll when needed.
 */
private suspend fun LazyListState.scrollMenuTabIntoView(
    index: Int,
    edgeInsetPx: Int,
    fast: Boolean,
) {
    if (!coroutineContext.isActive) return
    val itemCount = layoutInfo.totalItemsCount
    if (itemCount == 0) return
    val safeIndex = index.coerceIn(0, itemCount - 1)

    val animationSpec = tween<Float>(
        durationMillis = if (fast) MenuTabScrollFastDurationMillis else MenuTabScrollDurationMillis,
        easing = FastOutSlowInEasing,
    )

    var item = layoutInfo.visibleItemsInfo.find { it.index == safeIndex }
    if (item == null) {
        val visibleItems = layoutInfo.visibleItemsInfo
        val viewportSize = (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset).coerceAtLeast(1)
        val estimatedItemSize = visibleItems
            .map { it.size }
            .average()
            .toInt()
            .coerceAtLeast(1)
        val scrollOffset = when {
            visibleItems.isEmpty() || safeIndex <= visibleItems.first().index -> 0
            else -> (viewportSize - estimatedItemSize).coerceAtLeast(0)
        }
        if (!coroutineContext.isActive) return
        if (fast) {
            scrollToItem(safeIndex, scrollOffset = scrollOffset)
        } else {
            animateScrollToItem(safeIndex, scrollOffset = scrollOffset)
        }
        if (!coroutineContext.isActive) return
        item = layoutInfo.visibleItemsInfo.find { it.index == safeIndex }
    }

    item ?: return

    val viewportStart = layoutInfo.viewportStartOffset + edgeInsetPx
    val viewportEnd = layoutInfo.viewportEndOffset - edgeInsetPx
    val itemStart = item.offset
    val itemEnd = item.offset + item.size
    if (itemStart >= viewportStart && itemEnd <= viewportEnd) return

    val delta = when {
        itemStart < viewportStart -> (itemStart - viewportStart).toFloat()
        itemEnd > viewportEnd -> (itemEnd - viewportEnd).toFloat()
        else -> 0f
    }
    if (delta != 0f && coroutineContext.isActive) {
        animateScrollBy(delta, animationSpec)
    }
}

private fun Int.coerceTabIndex(lastIndex: Int): Int = coerceIn(0, lastIndex)

@Composable
private fun MenuCategoryTabRow(
    categories: List<String>,
    selectedIndex: Int,
    listState: LazyListState,
    onCategorySelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    LazyRow(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .background(palette.cardSurface)
            .padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            count = categories.size,
            key = { categories[it] },
        ) { index ->
            val category = categories[index]
            val selected = index == selectedIndex.coerceTabIndex(categories.lastIndex)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (selected) palette.brand else palette.mutedSurface)
                    .clickable { onCategorySelected(index) }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = category,
                    color = if (selected) Color.White else palette.foreground,
                    fontSize = 14.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun RowMenuItem(item: MenuItem) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
            .background(palette.cardSurface)
            .padding(16.dp),
    ) {
        Text(
            text = item.name,
            color = palette.foreground,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
        )
        Text(
            text = item.description,
            color = palette.mutedForeground,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            text = "$${item.price}",
            color = palette.foreground,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        )
    }
}
