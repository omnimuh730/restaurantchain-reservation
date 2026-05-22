package com.mh.restaurantchainreservation.feature.booking

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.lerp as lerpColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import android.app.Activity
import androidx.core.view.WindowCompat
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.Restaurant
import java.text.NumberFormat
import java.util.Locale

private val StarAccent = RestaurantColors.Semantic.starAmber
/** Matches [DetailTopBar] action row. */
private val ReviewsTopBarRowHeight = 56.dp
private val ReviewsTopBarHorizontalPadding = 16.dp
/** Scroll span after the section divider touches the header bottom (gray → white + border). */
private val Phase2HeaderWhiteTransitionRange = 48.dp
/** Compact header chrome slides up over this distance once inline toolbar is gone. */
private val CompactHeaderEnterSlideDp = 12.dp
private const val CompactHeaderEnterDurationMs = 280
private const val InitialReviewCount = 5
private const val ReviewBatchSize = 5

private enum class ReviewSortBy {
    MostRecent,
    HighestRated,
    LowestRated,
}

private data class StarDistribution(val stars: Int, val percent: Int)

private val ratingDistribution = listOf(
    StarDistribution(5, 88),
    StarDistribution(4, 64),
    StarDistribution(3, 30),
    StarDistribution(2, 14),
    StarDistribution(1, 8),
)

private data class SubRatingMetric(
    val label: String,
    val score: String,
    val emoji: String,
)

private val subRatingMetrics = listOf(
    SubRatingMetric("Taste", "5.0", "🍽️"),
    SubRatingMetric("Ambience", "4.9", "✨"),
    SubRatingMetric("Service", "4.8", "🤝"),
    SubRatingMetric("Value", "4.7", "💰"),
)

private data class ReviewSubCategory(val key: String, val emoji: String, val value: Int?)

@Composable
fun RestaurantReviewsScreen(
    restaurant: Restaurant,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val allReviews = remember { RestaurantDetailData.reviews }

    var entered by remember { mutableStateOf(false) }
    var showHowReviewsWork by remember { mutableStateOf(false) }
    var sortOpen by remember { mutableStateOf(false) }
    var sortBy by remember { mutableStateOf(ReviewSortBy.MostRecent) }
    var searchOpen by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var sortMenuFromCompactHeader by remember { mutableStateOf(false) }
    var reviewLimit by remember { mutableIntStateOf(InitialReviewCount) }

    val listState = rememberLazyListState()

    val sortedAndFiltered by remember(sortBy, searchText, allReviews) {
        derivedStateOf {
            val list = allReviews.toMutableList()
            when (sortBy) {
                ReviewSortBy.HighestRated -> list.sortByDescending { it.rating }
                ReviewSortBy.LowestRated -> list.sortBy { it.rating }
                ReviewSortBy.MostRecent -> Unit
            }
            val query = searchText.trim().lowercase()
            if (query.isEmpty()) {
                list
            } else {
                list.filter { "${it.name} ${it.text}".lowercase().contains(query) }
            }
        }
    }

    val visibleReviews = remember(sortedAndFiltered, reviewLimit) {
        sortedAndFiltered.take(reviewLimit)
    }
    val hasMoreReviews = visibleReviews.size < sortedAndFiltered.size

    LaunchedEffect(Unit) {
        entered = true
    }

    LaunchedEffect(sortBy, searchText) {
        reviewLimit = InitialReviewCount
    }

    LaunchedEffect(searchOpen) {
        if (searchOpen) {
            listState.animateScrollToItem(0)
        }
    }

    val density = LocalDensity.current
    val phase2RangePx = with(density) { Phase2HeaderWhiteTransitionRange.toPx() }
    val compactHeaderSlidePx = with(density) { CompactHeaderEnterSlideDp.toPx() }
    var statsSectionHeightPx by remember { mutableIntStateOf(0) }
    var topBarHeightPx by remember { mutableIntStateOf(0) }

    val headerTransition by remember(
        searchOpen,
        listState,
        statsSectionHeightPx,
        topBarHeightPx,
        phase2RangePx,
    ) {
        derivedStateOf {
            if (searchOpen) {
                ReviewsHeaderTransition(phase2Progress = 1f, showCompactHeader = true)
            } else {
                computeReviewsHeaderTransition(
                    listState = listState,
                    statsSectionHeightPx = statsSectionHeightPx,
                    topBarHeightPx = topBarHeightPx,
                    phase2RangePx = phase2RangePx,
                )
            }
        }
    }
    val phase2Progress = headerTransition.phase2Progress
    val rawShowCompactHeader = headerTransition.showCompactHeader

    val compactHeaderProgress by animateFloatAsState(
        targetValue = if (rawShowCompactHeader) 1f else 0f,
        animationSpec = tween(
            durationMillis = CompactHeaderEnterDurationMs,
            easing = FastOutSlowInEasing,
        ),
        label = "reviewsCompactHeader",
    )

    val topBarSurfaceColor =
        if (searchOpen) {
            palette.pageBackground
        } else if (phase2Progress <= 0f) {
            palette.mutedSurface
        } else {
            lerpColor(palette.mutedSurface, palette.pageBackground, phase2Progress)
        }

    val screenWidthPx = with(density) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val slideProgress by animateFloatAsState(
        targetValue = if (entered) 0f else 1f,
        animationSpec = tween(durationMillis = 440, easing = FastOutSlowInEasing),
        label = "reviewsSlide",
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        DisposableEffect(view) {
            val window = (view.context as Activity).window
            val previousStatusBarColor = window.statusBarColor
            val insetsController = WindowCompat.getInsetsController(window, view)
            val previousLightStatusBars = insetsController.isAppearanceLightStatusBars
            onDispose {
                window.statusBarColor = previousStatusBarColor
                insetsController.isAppearanceLightStatusBars = previousLightStatusBars
            }
        }
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = topBarSurfaceColor.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { translationX = slideProgress * screenWidthPx }
            .background(palette.pageBackground),
    ) {
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 48.dp),
            ) {
                if (!searchOpen) {
                    item(key = "reviews-stats") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(palette.mutedSurface)
                                .onSizeChanged { statsSectionHeightPx = it.height },
                        ) {
                            ReviewsStatsExpandedContent(
                                restaurant = restaurant,
                                onShowHowReviewsWork = { showHowReviewsWork = true },
                                sectionDividerAlpha =
                                    if (phase2Progress <= 0f) 1f else 1f - phase2Progress,
                            )
                        }
                    }
                }

                item(key = "reviews-toolbar") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(palette.pageBackground),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .padding(top = 20.dp),
                        ) {
                            ReviewsToolbar(
                                reviewCount = sortedAndFiltered.size,
                                searchOpen = searchOpen,
                                sortBy = sortBy,
                                sortOpen = sortOpen,
                                onSortOpenChange = { open ->
                                    sortOpen = open
                                    if (open) sortMenuFromCompactHeader = false
                                },
                                onSortByChange = {
                                    sortBy = it
                                    sortOpen = false
                                },
                                onSearchOpen = {
                                    sortOpen = false
                                    searchOpen = true
                                },
                                sortMenuExpanded = sortOpen && !sortMenuFromCompactHeader,
                            )

                            Spacer(Modifier.height(4.dp))
                        }
                    }
                }

            itemsIndexed(
                items = visibleReviews,
                key = { index, review -> "${review.name}-${review.publishedAtEpochMs}-$index" },
            ) { index, review ->
                ReviewCard(
                    review = review,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 16.dp),
                )
            }

            if (hasMoreReviews) {
                item(key = "show-more-reviews") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 24.dp)
                            .height(48.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .border(1.dp, palette.foreground, RoundedCornerShape(percent = 50))
                            .background(palette.cardSurface)
                            .clickable {
                                reviewLimit = (reviewLimit + ReviewBatchSize).coerceAtMost(sortedAndFiltered.size)
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Show more",
                            color = palette.foreground,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
            }
            }

            if (searchOpen) {
                ReviewsFixedSearchHeader(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .zIndex(2f)
                        .onSizeChanged { topBarHeightPx = it.height },
                    surfaceColor = topBarSurfaceColor,
                    searchText = searchText,
                    onSearchTextChange = { searchText = it },
                    onCancelSearch = {
                        searchOpen = false
                        searchText = ""
                    },
                    onBack = onBack,
                )
            } else {
                ReviewsTopActionBar(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .zIndex(2f)
                        .onSizeChanged { topBarHeightPx = it.height },
                    surfaceColor = topBarSurfaceColor,
                    phase2Progress = phase2Progress,
                    compactHeaderProgress = compactHeaderProgress,
                    compactHeaderSlidePx = compactHeaderSlidePx,
                    sortBy = sortBy,
                    sortMenuExpanded = sortOpen && sortMenuFromCompactHeader,
                    onSortOpenChange = { open ->
                        sortOpen = open
                        if (open) sortMenuFromCompactHeader = true
                    },
                    onSortByChange = {
                        sortBy = it
                        sortOpen = false
                    },
                    onSearchOpen = {
                        sortOpen = false
                        searchOpen = true
                    },
                    onBack = onBack,
                )
            }

            if (sortOpen) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(3f)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { sortOpen = false },
                        ),
                )
            }
        }
    }

    if (showHowReviewsWork) {
        HowReviewsWorkDialog(onClose = { showHowReviewsWork = false })
    }
}

private data class ReviewsHeaderTransition(
    /** Gray → white header background while section divider reaches header bottom. */
    val phase2Progress: Float,
    /** True once inline toolbar has fully scrolled under the header (then show compact chrome). */
    val showCompactHeader: Boolean,
)

/**
 * Scroll-linked header metrics: linear phase 2; compact chrome only after inline toolbar is gone.
 */
private fun computeReviewsHeaderTransition(
    listState: LazyListState,
    statsSectionHeightPx: Int,
    topBarHeightPx: Int,
    phase2RangePx: Float,
): ReviewsHeaderTransition {
    val layoutInfo = listState.layoutInfo
    val visibleItems = layoutInfo.visibleItemsInfo
    val statsItem = visibleItems.find { it.key == "reviews-stats" }
    val toolbarItem = visibleItems.find { it.key == "reviews-toolbar" }
    val headerBottomPx = topBarHeightPx.coerceAtLeast(1)

    val phase2Progress =
        when {
            statsItem == null -> 1f
            statsSectionHeightPx <= 0 -> 0f
            else -> {
                val dividerBottomInLazy = statsItem.offset + statsSectionHeightPx
                val gapToHeaderBottom = dividerBottomInLazy - headerBottomPx
                when {
                    gapToHeaderBottom > 0 -> 0f
                    else -> (-gapToHeaderBottom.toFloat() / phase2RangePx).coerceIn(0f, 1f)
                }
            }
        }

    val showCompactHeader =
        phase2Progress >= 1f &&
            when (val toolbar = toolbarItem) {
                null -> listState.firstVisibleItemIndex > 0
                else -> toolbar.offset + toolbar.size <= headerBottomPx
            }

    return ReviewsHeaderTransition(
        phase2Progress = phase2Progress,
        showCompactHeader = showCompactHeader,
    )
}

@Composable
private fun ReviewsTopActionBar(
    surfaceColor: Color,
    phase2Progress: Float,
    compactHeaderProgress: Float,
    compactHeaderSlidePx: Float,
    sortBy: ReviewSortBy,
    sortMenuExpanded: Boolean,
    onSortOpenChange: (Boolean) -> Unit,
    onSortByChange: (ReviewSortBy) -> Unit,
    onSearchOpen: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current

    Column(
        modifier = modifier
            .background(surfaceColor),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(surfaceColor),
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = surfaceColor,
            shadowElevation = if (phase2Progress >= 1f) 2.dp else 0.dp,
        ) {
            ReviewsHeaderActionRow(
                surfaceColor = surfaceColor,
                compactHeaderProgress = compactHeaderProgress,
                compactHeaderSlidePx = compactHeaderSlidePx,
                sortBy = sortBy,
                sortMenuExpanded = sortMenuExpanded,
                onSortOpenChange = onSortOpenChange,
                onSortByChange = onSortByChange,
                onSearchOpen = onSearchOpen,
                onBack = onBack,
            )
        }
        if (phase2Progress > 0f) {
            HorizontalDivider(
                modifier = Modifier.alpha(phase2Progress),
                color = palette.border,
            )
        }
    }
}

@Composable
private fun ReviewsFixedSearchHeader(
    surfaceColor: Color,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onCancelSearch: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(surfaceColor),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(surfaceColor),
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = surfaceColor,
            shadowElevation = 2.dp,
        ) {
            ReviewsSearchModeHeader(
                searchText = searchText,
                onSearchTextChange = onSearchTextChange,
                onCancel = onCancelSearch,
                onBack = onBack,
            )
        }
        val palette = LocalRestaurantPalette.current
        HorizontalDivider(color = palette.border)
    }
}

@Composable
private fun ReviewsHeaderActionRow(
    surfaceColor: Color,
    compactHeaderProgress: Float,
    compactHeaderSlidePx: Float,
    sortBy: ReviewSortBy,
    sortMenuExpanded: Boolean,
    onSortOpenChange: (Boolean) -> Unit,
    onSortByChange: (ReviewSortBy) -> Unit,
    onSearchOpen: () -> Unit,
    onBack: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val compactSlideY = compactHeaderSlidePx * (1f - compactHeaderProgress)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ReviewsTopBarRowHeight)
            .padding(horizontal = ReviewsTopBarHorizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ReviewsGlassCircleButton(
            onClick = onBack,
            background = surfaceColor,
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = palette.foreground,
                modifier = Modifier.size(20.dp),
            )
        }

        if (compactHeaderProgress > 0.01f) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
                    .graphicsLayer {
                        alpha = compactHeaderProgress
                        translationY = compactSlideY
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Reviews",
                    color = palette.foreground,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ReviewsCompactSortSearchActions(
                        sortBy = sortBy,
                        sortMenuExpanded = sortMenuExpanded,
                        onSortOpenChange = onSortOpenChange,
                        onSortByChange = onSortByChange,
                        onSearchOpen = onSearchOpen,
                        sortButtonHeight = 36.dp,
                        searchButtonSize = 36.dp,
                        sortIconSize = 16.dp,
                        searchIconSize = 18.dp,
                        sortTextSize = 14.sp,
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ReviewsCompactSortSearchActions(
    sortBy: ReviewSortBy,
    sortMenuExpanded: Boolean,
    onSortOpenChange: (Boolean) -> Unit,
    onSortByChange: (ReviewSortBy) -> Unit,
    onSearchOpen: () -> Unit,
    sortButtonHeight: Dp,
    searchButtonSize: Dp,
    sortIconSize: Dp,
    searchIconSize: Dp,
    sortTextSize: TextUnit,
) {
    val palette = LocalRestaurantPalette.current

    Box {
        Row(
            modifier = Modifier
                .height(sortButtonHeight)
                .clip(RoundedCornerShape(percent = 50))
                .border(1.dp, palette.border, RoundedCornerShape(percent = 50))
                .background(palette.cardSurface)
                .clickable { onSortOpenChange(!sortMenuExpanded) },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                Icons.Outlined.Tune,
                contentDescription = null,
                tint = palette.foreground,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(sortIconSize),
            )
            Text(
                text = sortBy.label(),
                color = palette.foreground,
                fontSize = sortTextSize,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(end = 12.dp),
            )
        }
        SortDropdownMenu(
            expanded = sortMenuExpanded,
            sortBy = sortBy,
            onDismiss = { onSortOpenChange(false) },
            onSortByChange = onSortByChange,
        )
    }

    Box(
        modifier = Modifier
            .size(searchButtonSize)
            .clip(CircleShape)
            .border(1.dp, palette.border, CircleShape)
            .background(palette.cardSurface)
            .clickable(onClick = onSearchOpen),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Outlined.Search,
            contentDescription = "Search reviews",
            tint = palette.foreground,
            modifier = Modifier.size(searchIconSize),
        )
    }
}

@Composable
private fun ReviewsGlassCircleButton(
    onClick: () -> Unit,
    background: Color,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
private fun ReviewsStatsExpandedContent(
    restaurant: Restaurant,
    onShowHowReviewsWork: () -> Unit,
    sectionDividerAlpha: Float = 1f,
) {
    val palette = LocalRestaurantPalette.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = formatRating(restaurant.rating),
                color = palette.foreground,
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Based on ${NumberFormat.getIntegerInstance(Locale.US).format(restaurant.reviews)} guest reviews",
                color = palette.mutedForeground,
                fontSize = 15.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = "How reviews work",
                color = palette.mutedForeground,
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable(onClick = onShowHowReviewsWork),
            )
        }

        HorizontalDivider(
            color = palette.border,
            modifier = Modifier.padding(top = 20.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Overall rating",
                    color = palette.foreground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                ratingDistribution.forEach { row ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 6.dp),
                    ) {
                        Text(
                            text = "${row.stars}",
                            color = palette.mutedForeground,
                            fontSize = 15.sp,
                            modifier = Modifier.width(12.dp),
                        )
                        Box(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(percent = 50))
                                .background(palette.border),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(row.percent / 100f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(percent = 50))
                                    .background(
                                        if (row.stars == 5) palette.foreground else palette.mutedForeground.copy(alpha = 0.35f),
                                    ),
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .width(1.dp)
                    .height(160.dp),
                color = palette.border,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    SubRatingGridCell(
                        metric = subRatingMetrics[0],
                        modifier = Modifier.weight(1f),
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(72.dp)
                            .background(palette.border),
                    )
                    SubRatingGridCell(
                        metric = subRatingMetrics[1],
                        modifier = Modifier.weight(1f),
                    )
                }
                HorizontalDivider(color = palette.border)
                Row(modifier = Modifier.fillMaxWidth()) {
                    SubRatingGridCell(
                        metric = subRatingMetrics[2],
                        modifier = Modifier.weight(1f),
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(72.dp)
                            .background(palette.border),
                    )
                    SubRatingGridCell(
                        metric = subRatingMetrics[3],
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        }

        Spacer(modifier = Modifier.height(20.dp))

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(sectionDividerAlpha),
            color = palette.border,
        )
    }
}

@Composable
private fun SubRatingGridCell(
    metric: SubRatingMetric,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(metric.label, color = palette.foreground, fontSize = 14.sp)
        Text(
            metric.score,
            color = palette.foreground,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 2.dp),
        )
        Text(metric.emoji, fontSize = 15.sp, modifier = Modifier.padding(top = 6.dp))
    }
}

@Composable
private fun ReviewsSearchModeHeader(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(ReviewsTopBarRowHeight),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onBack,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = palette.foreground,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                text = "Reviews",
                color = palette.foreground,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(start = 12.dp),
            )
        }

        ReviewsSearchFieldRow(
            searchText = searchText,
            onSearchTextChange = onSearchTextChange,
            onCancel = onCancel,
            modifier = Modifier.padding(bottom = 12.dp),
        )
    }
}

@Composable
private fun ReviewsSearchFieldRow(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(percent = 50))
                .border(2.dp, palette.foreground, RoundedCornerShape(percent = 50))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Outlined.Search, null, tint = palette.foreground, modifier = Modifier.size(20.dp))
            BasicTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = palette.foreground,
                    fontSize = 17.sp,
                ),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { inner ->
                    if (searchText.isEmpty()) {
                        Text(
                            "Search reviews",
                            color = palette.mutedForeground,
                            fontSize = 17.sp,
                        )
                    }
                    inner()
                },
            )
        }
        Text(
            text = "Cancel",
            color = palette.foreground,
            fontSize = 15.sp,
            modifier = Modifier.clickable(onClick = onCancel),
        )
    }
}

@Composable
private fun ReviewsToolbar(
    reviewCount: Int,
    searchOpen: Boolean,
    sortBy: ReviewSortBy,
    sortOpen: Boolean,
    sortMenuExpanded: Boolean,
    onSortOpenChange: (Boolean) -> Unit,
    onSortByChange: (ReviewSortBy) -> Unit,
    onSearchOpen: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "$reviewCount reviews",
            color = palette.foreground,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box {
                Box(
                    modifier = Modifier
                        .height(44.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .border(1.dp, palette.border, RoundedCornerShape(percent = 50))
                        .clickable { onSortOpenChange(!sortOpen) }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = sortBy.label(),
                        color = palette.foreground,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
                SortDropdownMenu(
                    expanded = sortMenuExpanded,
                    sortBy = sortBy,
                    onDismiss = { onSortOpenChange(false) },
                    onSortByChange = onSortByChange,
                )
            }

            if (!searchOpen) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(1.dp, palette.border, CircleShape)
                        .clickable(onClick = onSearchOpen),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Outlined.Search,
                        contentDescription = "Search reviews",
                        tint = palette.foreground,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SortDropdownMenu(
    expanded: Boolean,
    sortBy: ReviewSortBy,
    onDismiss: () -> Unit,
    onSortByChange: (ReviewSortBy) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier
            .width(176.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(palette.cardSurface)
            .border(1.dp, palette.border, RoundedCornerShape(12.dp)),
    ) {
        ReviewSortBy.entries.forEach { option ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = option.label(),
                        fontWeight = if (sortBy == option) FontWeight.Bold else FontWeight.Medium,
                        color = palette.foreground,
                        fontSize = 15.sp,
                    )
                },
                onClick = { onSortByChange(option) },
                modifier = Modifier.background(
                    if (sortBy == option) palette.mutedSurface else Color.Transparent,
                ),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReviewCard(
    review: ReviewEntry,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    var helpful by remember { mutableStateOf(false) }

    val subCategories = listOf(
        ReviewSubCategory("Taste", "🍴", review.taste),
        ReviewSubCategory("Ambience", "✨", review.ambience),
        ReviewSubCategory("Service", "🤝", review.service),
        ReviewSubCategory("Value", "💰", review.value),
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
            .background(palette.cardSurface.copy(alpha = 0.5f))
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(palette.brandSoftSurface),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = review.name.take(1),
                        color = palette.brand,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(
                        text = review.name,
                        color = palette.foreground,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = formatReviewTimeAgo(review.publishedAtEpochMs),
                        color = palette.mutedForeground,
                        fontSize = 11.sp,
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(5) { index ->
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (index < review.rating) StarAccent else palette.border,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }

        if (subCategories.any { it.value != null }) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                subCategories.forEach { category ->
                    val value = category.value ?: return@forEach
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${category.emoji} ${category.key} ",
                            color = palette.mutedForeground,
                            fontSize = 11.sp,
                        )
                        Text(
                            text = "$value",
                            color = palette.foreground,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }

        Text(
            text = review.text,
            color = palette.mutedForeground,
            fontSize = 13.sp,
            lineHeight = 20.sp,
            modifier = Modifier.padding(top = 8.dp),
        )

        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(if (helpful) palette.brandSoftSurface else Color.Transparent)
                .clickable { helpful = !helpful }
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                Icons.Outlined.ThumbUp,
                contentDescription = null,
                tint = if (helpful) palette.brand else palette.mutedForeground,
                modifier = Modifier.size(12.dp),
            )
            Text(
                text = if (helpful) "Marked helpful" else "Helpful",
                color = if (helpful) palette.brand else palette.mutedForeground,
                fontSize = 11.sp,
            )
        }
    }
}

private fun ReviewSortBy.label(): String = when (this) {
    ReviewSortBy.MostRecent -> "Most recent"
    ReviewSortBy.HighestRated -> "Highest rated"
    ReviewSortBy.LowestRated -> "Lowest rated"
}
