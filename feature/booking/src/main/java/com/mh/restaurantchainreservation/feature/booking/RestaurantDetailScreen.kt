package com.mh.restaurantchainreservation.feature.booking

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.badge.AnimatedGuestFavoriteCenterBadge
import com.mh.restaurantchainreservation.core.designsystem.components.DiscoverMenuSeeAllCard
import com.mh.restaurantchainreservation.core.designsystem.components.DiscoverMenuTile
import com.mh.restaurantchainreservation.core.designsystem.components.HeartDrawableIcon
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantPalette
import com.mh.restaurantchainreservation.core.model.Restaurant
import com.mh.restaurantchainreservation.core.model.withDerivedGuestFavoriteLevel
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val SheetTopRadius = 34.dp
private val HeaderSheetShape = RoundedCornerShape(topStart = SheetTopRadius, topEnd = SheetTopRadius)
private val HeroHeight = 288.dp
private val DetailInfoHorizontalPadding = 24.dp
private val DetailListBottomPadding = 148.dp
/** Gap between the bottom of "Show all reviews" and the top of the sticky Reserve bar. */
private val ReviewsScrollGapAboveBookingBar = 16.dp
/** Booking bar row (padding + price + Reserve), excluding system nav inset handled on the bar. */
private val BookingBarEstimatedHeight = 84.dp
private val DetailStatsSideColumnWeight = 0.9f
private val DetailStatsCenterColumnWeight = 1.75f
private val DetailStatsDividerHeight = 36.dp
private val DetailStatsRowVerticalPadding = 20.dp
private val BookingBarTopShadowElevation = 10.dp
/** Matches CSS loader: 60×30, three dots bouncing on a 1s linear loop (l3). */
private val DetailLoaderWidth = 60.dp
private val DetailLoaderHeight = 30.dp
private val DetailLoadingDotDiameter = 10.dp
private const val DetailLoaderAnimationMillis = 1_000
/** Minimum time the loading dots stay visible while detail data is fetched. */
private const val DetailLoadingMinFetchMillis = 1_600L

/** Per-dot vertical keyframes (time → 0=top, 0.5=center, 1=bottom), CSS `l3` animation. */
private val DetailLoaderDotKeyframes: List<List<Pair<Float, Float>>> = listOf(
    listOf(0f to 0.5f, 0.2f to 0f, 0.4f to 1f, 0.6f to 0.5f, 0.8f to 0.5f, 1f to 0.5f),
    listOf(0f to 0.5f, 0.2f to 0.5f, 0.4f to 0f, 0.6f to 1f, 0.8f to 0.5f, 1f to 0.5f),
    listOf(0f to 0.5f, 0.2f to 0.5f, 0.4f to 0.5f, 0.6f to 0f, 0.8f to 1f, 1f to 0.5f),
)

/** Paints a sheet with only the top corners rounded (reliable vs clip/background on lazy lists). */
private fun Modifier.detailSheetTopRoundedBackground(color: Color): Modifier = drawBehind {
    val topRadius = SheetTopRadius.toPx()
    val path = Path().apply {
        addRoundRect(
            RoundRect(
                rect = Rect(0f, 0f, size.width, size.height),
                topLeft = CornerRadius(topRadius, topRadius),
                topRight = CornerRadius(topRadius, topRadius),
                bottomRight = CornerRadius.Zero,
                bottomLeft = CornerRadius.Zero,
            ),
        )
    }
    drawPath(path, color = color)
}

private fun interpolateLoaderKeyframes(
    keyframes: List<Pair<Float, Float>>,
    progress: Float,
): Float {
    val p = progress.coerceIn(0f, 1f)
    if (p <= keyframes.first().first) return keyframes.first().second
    for (i in 0 until keyframes.lastIndex) {
        val (t0, v0) = keyframes[i]
        val (t1, v1) = keyframes[i + 1]
        if (p <= t1) {
            val fraction = if (t1 == t0) 0f else (p - t0) / (t1 - t0)
            return v0 + (v1 - v0) * fraction
        }
    }
    return keyframes.last().second
}

/**
 * Scrolls so the bottom edge of "Show all reviews" sits just above the sticky Reserve bar.
 */
private suspend fun LazyListState.scrollToReviewsShowAllButton(
    showAllButtonBottomPx: Int,
    bottomClearancePx: Int,
) {
    if (showAllButtonBottomPx <= 0) return
    val layoutInfo = layoutInfo
    if (layoutInfo.totalItemsCount == 0) return
    val viewportHeight = layoutInfo.viewportSize.height
    val targetOffset =
        (showAllButtonBottomPx - (viewportHeight - bottomClearancePx)).coerceAtLeast(0)
    animateScrollToItem(index = 0, scrollOffset = targetOffset)
}

private enum class DetailLoadPhase {
    Shell,
    Loading,
    Ready,
}

private data class DetailLoadedPayload(
    val ext: RestaurantExtendedData,
    val gallery: List<String>,
    val topReviews: List<ReviewEntry>,
)

/** Keeps detail content warm when returning from photo grid or other sub-routes. */
private object RestaurantDetailPayloadCache {
    private val payloads = mutableMapOf<String, DetailLoadedPayload>()

    fun get(restaurantId: String): DetailLoadedPayload? = payloads[restaurantId]

    fun put(restaurantId: String, payload: DetailLoadedPayload) {
        payloads[restaurantId] = payload
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RestaurantDetailScreen(
    restaurantId: String,
    onBack: () -> Unit,
    onBookNow: () -> Unit,
    onShowMenu: () -> Unit,
    onOpenPhotoGrid: (RestaurantPhotoGallerySource) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val restaurant = remember(restaurantId) {
        (
            com.mh.restaurantchainreservation.core.model.DiscoverData.findById(restaurantId)
                ?: com.mh.restaurantchainreservation.core.model.DiscoverData.MONTHLY_BEST.first()
            ).withDerivedGuestFavoriteLevel()
    }
    var loadPhase by remember(restaurantId) { mutableStateOf(DetailLoadPhase.Shell) }
    var loadedPayload by remember(restaurantId) { mutableStateOf<DetailLoadedPayload?>(null) }
    var saved by remember { mutableStateOf(false) }
    var showReviews by remember { mutableStateOf(false) }
    var showHowReviewsWork by remember { mutableStateOf(false) }
    var showAmenities by remember { mutableStateOf(false) }
    var headerSolid by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var detailPageCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var reviewsShowAllButtonBottomPx by remember { mutableIntStateOf(0) }
    val headerSolidDerived by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 48
        }
    }
    androidx.compose.runtime.LaunchedEffect(headerSolidDerived) {
        headerSolid = headerSolidDerived
    }

    val bodyReveal = remember { Animatable(0f) }
    val density = LocalDensity.current
    val navigationBars = WindowInsets.navigationBars
    val bodySlidePx = remember(density) { with(density) { 28.dp.toPx() } }

    LaunchedEffect(restaurantId) {
        val cached = RestaurantDetailPayloadCache.get(restaurantId)
        if (cached != null) {
            loadedPayload = cached
            loadPhase = DetailLoadPhase.Ready
            bodyReveal.snapTo(1f)
            return@LaunchedEffect
        }
        loadPhase = DetailLoadPhase.Shell
        loadedPayload = null
        bodyReveal.snapTo(0f)
        delay(72)
        loadPhase = DetailLoadPhase.Loading
        val payload = coroutineScope {
            val fetch = async(Dispatchers.Default) {
                DetailLoadedPayload(
                    ext = RestaurantDetailData.extendedData(restaurant),
                    gallery = RestaurantDetailData.galleryImages(restaurant),
                    topReviews = RestaurantDetailData.carouselReviews,
                )
            }
            delay(DetailLoadingMinFetchMillis)
            fetch.await()
        }
        loadedPayload = payload
        RestaurantDetailPayloadCache.put(restaurantId, payload)
        loadPhase = DetailLoadPhase.Ready
    }

    LaunchedEffect(loadPhase) {
        if (loadPhase == DetailLoadPhase.Ready) {
            if (bodyReveal.value >= 0.99f) return@LaunchedEffect
            bodyReveal.snapTo(0f)
            bodyReveal.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 420, delayMillis = 48, easing = FastOutSlowInEasing),
            )
        }
    }

    val contentReady = loadPhase == DetailLoadPhase.Ready && loadedPayload != null
    val payload = loadedPayload
    val heroImages = payload?.gallery ?: listOf(restaurant.image)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.pageBackground),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = DetailListBottomPadding),
        ) {
            // Hero + sheet must live in one item: LazyColumn clips each item, so a separate
            // "detail-sheet" item cannot draw its rounded top over the hero via negative offset.
            item(key = "detail-page") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { detailPageCoordinates = it },
                ) {
                    HeroCarousel(
                        restaurantId = restaurant.id,
                        galleryImages = heroImages,
                        restaurantName = restaurant.name,
                        showPageIndicator = contentReady && heroImages.size > 1,
                        onOpenFullscreen = {
                            if (contentReady) {
                                onOpenPhotoGrid(RestaurantPhotoGallerySource.Gallery)
                            }
                        },
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = -SheetTopRadius)
                            .detailSheetTopRoundedBackground(palette.pageBackground)
                            .clip(HeaderSheetShape),
                    ) {
                        HeaderSummaryCard(
                            restaurant = restaurant,
                            ext = payload?.ext,
                            loadPhase = loadPhase,
                        )
                        if (contentReady && payload != null) {
                            RatingsSummaryRow(
                                restaurant = restaurant,
                                onScrollToReviews = {
                                    coroutineScope.launch {
                                        listState.scrollToReviewsShowAllButton(
                                            showAllButtonBottomPx = reviewsShowAllButtonBottomPx,
                                            bottomClearancePx = with(density) {
                                                navigationBars.getBottom(this) +
                                                    (
                                                        BookingBarEstimatedHeight +
                                                            ReviewsScrollGapAboveBookingBar
                                                        ).roundToPx()
                                            },
                                        )
                                    }
                                },
                            )
                            Column(
                                modifier = Modifier.graphicsLayer {
                                    val p = bodyReveal.value
                                    translationY = (1f - p) * bodySlidePx
                                    alpha = 0.22f + 0.78f * p
                                },
                            ) {
                                AboutSection(ext = payload.ext)
                                AmenitiesSection(
                                    restaurant = restaurant,
                                    ext = payload.ext,
                                    onShowAll = { showAmenities = true },
                                )
                                LocationSection(restaurant = restaurant, ext = payload.ext)
                                ReviewsPreviewSection(
                                    restaurant = restaurant,
                                    reviews = payload.topReviews,
                                    onOpenReviews = { showReviews = true },
                                    onShowHowReviewsWork = { showHowReviewsWork = true },
                                    detailPageCoordinates = detailPageCoordinates,
                                    onShowAllButtonBottomMeasured = { bottomPx ->
                                        reviewsShowAllButtonBottomPx = bottomPx
                                    },
                                )
                                CancellationPolicySection()
                                PopularMenuSection(
                                    onShowMenu = onShowMenu,
                                    onOpenPhotoGrid = onOpenPhotoGrid,
                                )
                            }
                        } else {
                            Spacer(Modifier.height(DetailListBottomPadding))
                        }
                    }
                }
            }
        }

        DetailTopBar(
            restaurantName = restaurant.name,
            solid = headerSolid,
            saved = saved,
            onBack = onBack,
            onToggleSave = { saved = !saved },
        )

        if (contentReady) {
            BookingBar(
                restaurant = restaurant,
                onBookNow = onBookNow,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        if (showReviews) {
            RestaurantReviewsScreen(
                restaurant = restaurant,
                onBack = { showReviews = false },
                modifier = Modifier.fillMaxSize(),
            )
        }
        if (showHowReviewsWork) {
            HowReviewsWorkDialog(onClose = { showHowReviewsWork = false })
        }
        if (showAmenities) {
            RestaurantAmenitiesScreen(
                restaurant = restaurant,
                onBack = { showAmenities = false },
                modifier = Modifier.fillMaxSize(),
            )
        }

    }
}

@Composable
private fun DetailTopBar(
    restaurantName: String,
    solid: Boolean,
    saved: Boolean,
    onBack: () -> Unit,
    onToggleSave: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val buttonBg = RestaurantColors.Base.white
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (solid) palette.pageBackground.copy(alpha = 0.95f) else Color.Transparent,
            )
            .then(
                if (solid) Modifier.border(1.dp, palette.border) else Modifier,
            )
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GlassCircleButton(onClick = onBack, background = buttonBg) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = palette.foreground, modifier = Modifier.size(20.dp))
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (solid) {
                    Text(
                        text = restaurantName,
                        color = palette.foreground,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GlassCircleButton(onClick = { }, background = buttonBg) {
                    Icon(Icons.Default.Share, "Share", tint = palette.foreground, modifier = Modifier.size(18.dp))
                }
                GlassCircleButton(onClick = onToggleSave, background = buttonBg) {
                    HeartDrawableIcon(active = saved, contentDescription = "Save", iconHeight = 20.dp)
                }
            }
        }
    }
}

@Composable
private fun GlassCircleButton(
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HeroCarousel(
    restaurantId: String,
    galleryImages: List<String>,
    restaurantName: String,
    showPageIndicator: Boolean,
    onOpenFullscreen: (Int) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { galleryImages.size.coerceAtLeast(1) })
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeroHeight),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = showPageIndicator,
        ) { page ->
            AsyncImage(
                model = galleryImages[page],
                contentDescription = restaurantName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (showPageIndicator) {
                            Modifier.clickable { onOpenFullscreen(page) }
                        } else {
                            Modifier
                        },
                    ),
            )
        }
        if (showPageIndicator) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(RestaurantColors.Base.black.copy(alpha = 0.6f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    text = "${pagerState.currentPage + 1} / ${galleryImages.size}",
                    color = RestaurantColors.Base.white,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun HeaderSummaryCard(
    restaurant: Restaurant,
    ext: RestaurantExtendedData?,
    loadPhase: DetailLoadPhase,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DetailInfoHorizontalPadding)
            .padding(
                top = 28.dp,
                bottom = when (loadPhase) {
                    DetailLoadPhase.Ready -> 0.dp
                    DetailLoadPhase.Loading -> 16.dp
                    DetailLoadPhase.Shell -> 20.dp
                },
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = restaurant.name,
            color = palette.foreground,
            fontSize = 32.sp,
            lineHeight = 38.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        if (loadPhase == DetailLoadPhase.Loading) {
            RestaurantDetailLoadingDots(
                modifier = Modifier.padding(top = 24.dp),
            )
        }
        if (loadPhase == DetailLoadPhase.Ready && ext != null) {
            Text(
                text = detailHeaderLocationLine(restaurant, ext),
                color = palette.mutedForeground,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
            )
            Text(
                text = detailHeaderHoursLine(restaurant, ext),
                color = palette.mutedForeground,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun RestaurantDetailLoadingDots(modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    val density = LocalDensity.current
    val transition = rememberInfiniteTransition(label = "detail-loading-dots")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = DetailLoaderAnimationMillis,
                easing = LinearEasing,
            ),
        ),
        label = "detail-loader-l3-progress",
    )
    val bounceTravelPx = remember(density) {
        with(density) { (DetailLoaderHeight - DetailLoadingDotDiameter).toPx() / 2f }
    }

    Box(
        modifier = modifier
            .width(DetailLoaderWidth)
            .height(DetailLoaderHeight),
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center,
                ) {
                    val yFraction = interpolateLoaderKeyframes(
                        keyframes = DetailLoaderDotKeyframes[index],
                        progress = progress,
                    )
                    val offsetY = (yFraction - 0.5f) * 2f * bounceTravelPx
                    Box(
                        modifier = Modifier
                            .offset(y = with(density) { offsetY.toDp() })
                            .size(DetailLoadingDotDiameter)
                            .clip(CircleShape)
                            .background(palette.foreground),
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingsSummaryRow(restaurant: Restaurant, onScrollToReviews: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val laurelTier = restaurant.guestFavoriteLevel.toDetailLaurelTier()
    val showGuestFavorite = restaurant.guestFavoriteLevel.isGuestFavorite()
    val sideWeight = if (showGuestFavorite) DetailStatsSideColumnWeight else 1f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onScrollToReviews)
            .padding(horizontal = DetailInfoHorizontalPadding)
            .padding(
                top = DetailStatsRowVerticalPadding,
                bottom = DetailStatsRowVerticalPadding,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(sideWeight),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = formatRating(restaurant.rating),
                color = palette.foreground,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Row(
                modifier = Modifier.padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                repeat(5) {
                    Icon(Icons.Filled.Star, null, tint = palette.foreground, modifier = Modifier.size(13.dp))
                }
            }
        }
        if (showGuestFavorite) {
            RatingsSummaryDivider(
                palette = palette,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
            AnimatedGuestFavoriteCenterBadge(
                tier = laurelTier,
                animationKey = restaurant.id,
                modifier = Modifier.weight(DetailStatsCenterColumnWeight),
                laurelHeight = 38.dp,
                titleSize = 20.sp,
            )
        }
        RatingsSummaryDivider(
            palette = palette,
            modifier = Modifier.align(Alignment.CenterVertically),
        )
        Column(
            modifier = Modifier.weight(sideWeight),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = NumberFormat.getIntegerInstance(Locale.US).format(restaurant.reviews),
                color = palette.foreground,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Reviews",
                color = palette.foreground,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
    DetailInsetDivider()
}

@Composable
private fun DetailInsetDivider(modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    HorizontalDivider(
        color = palette.border,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = DetailInfoHorizontalPadding),
    )
}

@Composable
private fun RatingsSummaryDivider(
    palette: RestaurantPalette,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(horizontal = 10.dp)
            .width(1.dp)
            .height(DetailStatsDividerHeight)
            .background(palette.border),
    )
}

@Composable
private fun AboutSection(ext: RestaurantExtendedData) {
    val palette = LocalRestaurantPalette.current
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
        Text("About this place", color = palette.foreground, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "${ext.description} Experience the best of our cuisine at this location.",
            color = palette.foreground.copy(alpha = 0.9f),
            fontSize = 16.sp,
            lineHeight = 28.sp,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
    DetailInsetDivider()
}

@Composable
private fun DetailInfoRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = palette.foreground,
            modifier = Modifier.size(22.dp),
        )
        Column {
            Text(
                text = title,
                color = palette.foreground,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                color = palette.mutedForeground,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun AmenitiesSection(
    restaurant: Restaurant,
    ext: RestaurantExtendedData,
    onShowAll: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val chipCategories = remember(ext) {
        RestaurantAmenitiesData.placeOfferChipCategories(ext)
    }
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
        Text("What this place offers", color = palette.foreground, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))
        PlaceOfferChipsContent(categories = chipCategories)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(palette.mutedSurface)
                .clickable(onClick = onShowAll),
            contentAlignment = Alignment.Center,
        ) {
            Text("Show all amenities", color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
    DetailInsetDivider()
}

@Composable
private fun LocationSection(restaurant: Restaurant, ext: RestaurantExtendedData) {
    val palette = LocalRestaurantPalette.current
    val (lat, lng) = remember(restaurant.id) { RestaurantDetailData.mapCoordinate(restaurant) }
    val locationLine = remember(restaurant.area, ext.address) {
        listOfNotNull(restaurant.area, ext.address).joinToString(", ")
    }
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
        Text("Where you'll be", color = palette.foreground, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        RestaurantDetailLocationMap(
            latitude = lat,
            longitude = lng,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .padding(top = 16.dp),
        )
        DetailInfoRow(
            icon = Icons.Outlined.Place,
            title = "Location",
            subtitle = locationLine,
            modifier = Modifier.padding(top = 20.dp),
        )
        DetailInfoRow(
            icon = Icons.Outlined.Phone,
            title = "Contact",
            subtitle = ext.phone2,
            modifier = Modifier.padding(top = 20.dp),
        )
    }
    DetailInsetDivider()
}

@Composable
private fun ReviewsPreviewSection(
    restaurant: Restaurant,
    reviews: List<ReviewEntry>,
    onOpenReviews: () -> Unit,
    onShowHowReviewsWork: () -> Unit,
    detailPageCoordinates: LayoutCoordinates?,
    onShowAllButtonBottomMeasured: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (reviews.isEmpty()) return
    val palette = LocalRestaurantPalette.current
    val reviewCountLabel = NumberFormat.getIntegerInstance(Locale.US).format(restaurant.reviews)
    Column(modifier = modifier.padding(top = 8.dp, bottom = 24.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DetailInfoHorizontalPadding)
                .padding(vertical = 20.dp)
                .padding(bottom = 14.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = palette.foreground,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = String.format(Locale.US, "%.1f · %s reviews", restaurant.rating, reviewCountLabel),
                    color = palette.foreground,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = "How reviews work",
                color = palette.foreground,
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable(onClick = onShowHowReviewsWork),
            )
        }
        DetailInsetDivider()
        ReviewsCarousel(
            reviews = reviews,
            onShowMore = onOpenReviews,
            modifier = Modifier.padding(top = 20.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DetailInfoHorizontalPadding)
                .padding(top = 20.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(palette.mutedSurface)
                .clickable(onClick = onOpenReviews)
                .onGloballyPositioned { coordinates ->
                    val page = detailPageCoordinates ?: return@onGloballyPositioned
                    val topLeft = page.localPositionOf(coordinates, Offset.Zero)
                    onShowAllButtonBottomMeasured((topLeft.y + coordinates.size.height).toInt())
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Show all ${NumberFormat.getIntegerInstance(Locale.US).format(restaurant.reviews)} reviews",
                color = palette.foreground,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
    DetailInsetDivider()
}

private val ReviewPreviewHeight = 200.dp
private val ReviewPreviewBodyLineHeight = 22.sp
private val ReviewPreviewShowMoreRowHeight = 22.dp
private val ReviewPreviewMaxBodyLines = 3
private val ReviewCarouselDividerWidth = 1.dp
private val ReviewCarouselContentPadding = 24.dp
/** How much of the next card peeks on the right while the active card is snapped. */
private const val ReviewCarouselNextPeekFraction = 0.24f

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ReviewsCarousel(
    reviews: List<ReviewEntry>,
    onShowMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (reviews.isEmpty()) return

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val viewportWidth = maxWidth - DetailInfoHorizontalPadding
        val nextPeekWidth = viewportWidth * ReviewCarouselNextPeekFraction
        val pageWidth = viewportWidth - nextPeekWidth
        val pagerState = rememberPagerState(pageCount = { reviews.size })

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = DetailInfoHorizontalPadding)
                .clipToBounds(),
            pageSize = PageSize.Fixed(pageWidth),
            pageSpacing = 0.dp,
            contentPadding = PaddingValues(end = DetailInfoHorizontalPadding),
            beyondViewportPageCount = 1,
        ) { page ->
            ReviewCarouselPage(
                review = reviews[page],
                index = page,
                pageWidth = pageWidth,
                onShowMore = onShowMore,
            )
        }
    }
}

@Composable
private fun ReviewCarouselPage(
    review: ReviewEntry,
    index: Int,
    pageWidth: Dp,
    onShowMore: () -> Unit,
) {
    val contentWidth =
        if (index == 0) {
            pageWidth
        } else {
            pageWidth - ReviewCarouselDividerWidth
        }
    Row(
        modifier = Modifier
            .width(pageWidth)
            .clipToBounds(),
        verticalAlignment = Alignment.Top,
    ) {
        if (index > 0) {
            ReviewPreviewColumnDivider()
        }
        ReviewPreviewCard(
            review = review,
            cardWidth = contentWidth,
            onShowMore = onShowMore,
        )
    }
}

@Composable
private fun ReviewPreviewColumnDivider(modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = modifier
            .width(ReviewCarouselDividerWidth)
            .height(ReviewPreviewHeight)
            .background(palette.border),
    )
}

@Composable
private fun ReviewPreviewCard(
    review: ReviewEntry,
    cardWidth: Dp,
    onShowMore: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val filledStars = review.rating.coerceIn(0, 5)

    Column(
        modifier = Modifier
            .width(cardWidth)
            .height(ReviewPreviewHeight)
            .padding(horizontal = ReviewCarouselContentPadding),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(palette.mutedSurface),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = review.name.take(1).uppercase(),
                    color = palette.foreground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = review.name,
                    color = palette.foreground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = formatReviewMonthYear(review.publishedAtEpochMs),
                    color = palette.mutedForeground,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        Row(
            modifier = Modifier.padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(5) { starIndex ->
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (starIndex < filledStars) palette.foreground else palette.border,
                    modifier = Modifier.size(12.dp),
                )
            }
            Text(
                text = "· ${formatReviewCarouselScore(review)}",
                color = palette.foreground,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 6.dp),
            )
        }
        Column(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = review.text,
                color = palette.foreground,
                fontSize = 16.sp,
                lineHeight = ReviewPreviewBodyLineHeight,
                maxLines = ReviewPreviewMaxBodyLines,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = ReviewPreviewShowMoreRowHeight),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Show more",
                    color = palette.foreground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable(onClick = onShowMore),
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun CancellationPolicySection() {
    val palette = LocalRestaurantPalette.current
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
        Text("Cancellation policy", color = palette.foreground, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(
            "Free cancellation up to 24 hours before your reservation. Late cancellations may include a partial fee depending on booking size and timing.",
            color = palette.foreground,
            fontSize = 16.sp,
            lineHeight = 28.sp,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
    DetailInsetDivider()
}

private const val PopularMenuPreviewCount = 6

@Composable
private fun PopularMenuSection(
    onShowMenu: () -> Unit,
    onOpenPhotoGrid: (RestaurantPhotoGallerySource) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val previewItems = remember {
        RestaurantDetailData.popularMenuPreviewItems(PopularMenuPreviewCount)
            .filter { it.imageUrl != null }
    }
    val allImages = remember { RestaurantDetailData.popularMenuImages() }

    Column(modifier = Modifier.padding(vertical = 24.dp)) {
        Text(
            "Popular menu",
            color = palette.foreground,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            items(
                items = previewItems,
                key = { it.imageUrl ?: it.name },
            ) { item ->
                DiscoverMenuTile(
                    imageUrl = item.imageUrl!!,
                    title = item.name,
                    imageCaption = "",
                    onClick = { onOpenPhotoGrid(RestaurantPhotoGallerySource.PopularMenu) },
                    contentDescription = item.name,
                    showTitle = false,
                    showImageCaption = false,
                )
            }
            if (allImages.isNotEmpty()) {
                item(key = "popular-menu-see-all") {
                    DiscoverMenuSeeAllCard(
                        previewImages = allImages.take(3),
                        onClick = { onOpenPhotoGrid(RestaurantPhotoGallerySource.PopularMenu) },
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp, bottom = 16.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(palette.mutedSurface)
                .clickable(onClick = onShowMenu),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Show Full Menu",
                color = palette.foreground,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun BookingBar(
    restaurant: Restaurant,
    onBookNow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val amount = RestaurantDetailData.bookingPrice(restaurant)
    val formatted = NumberFormat.getCurrencyInstance(Locale.US).format(amount).replace(".00", "")
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(palette.pageBackground)
            .border(1.dp, palette.border)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = formatted,
                color = palette.foreground,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp),
            ) {
                Icon(Icons.Outlined.Shield, null, tint = palette.foreground, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Free cancellation", color = palette.foreground, fontSize = 14.sp)
            }
        }
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(percent = 50))
                .background(RestaurantColors.Brand.reservePink)
                .clickable(onClick = onBookNow)
                .padding(horizontal = 28.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Outlined.CalendarMonth, null, tint = RestaurantColors.Base.white, modifier = Modifier.size(18.dp))
            Text("Reserve", color = RestaurantColors.Base.white, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
