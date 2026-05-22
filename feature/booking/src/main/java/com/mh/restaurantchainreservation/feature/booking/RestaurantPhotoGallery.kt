package com.mh.restaurantchainreservation.feature.booking

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.components.CollapsingTitleHeaderMetrics
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButton
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButtonSize
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButtonStyle
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.Banner
import com.mh.restaurantchainreservation.core.model.Restaurant
import com.mh.restaurantchainreservation.core.model.WishlistStore
private val GridImageCorner = 16.dp
private val FullscreenImageCorner = 24.dp
private val FullscreenImageSideInset = 20.dp
private val FullscreenCaptionBottomPadding = 32.dp
/** Frame moves slightly; image inside moves more for a layered (Airbnb-style) parallax. */
private const val FullscreenFrameParallaxFactor = 0.28f
private const val FullscreenImageParallaxFactor = 1.0f
private const val FullscreenImageOverscale = 1.12f
private const val DoubleTapZoomScale = 2.5f
private const val ZoomTransitionMillis = 280
private val PhotoGridBottomNavClearance = 72.dp

@Composable
fun RestaurantPhotoGridScreen(
    restaurant: Restaurant,
    source: RestaurantPhotoGallerySource,
    banner: Banner? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val entries = remember(restaurant.id, source, banner?.id) {
        RestaurantPhotoGalleryData.entries(restaurant, source, banner)
    }
    var fullscreenStartIndex by remember { mutableStateOf<Int?>(null) }
    var showShareSheet by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()
    val showTopBarBorder by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex > 0 || gridState.firstVisibleItemScrollOffset > 0
        }
    }
    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues()
    val gridBottomPadding = remember(navigationBarPadding) {
        PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 12.dp,
            bottom = 12.dp + navigationBarPadding.calculateBottomPadding() + PhotoGridBottomNavClearance,
        )
    }

    if (entries.isEmpty()) {
        Box(modifier.fillMaxSize().background(palette.cardSurface), contentAlignment = Alignment.Center) {
            Text("No photos", color = palette.mutedForeground)
        }
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.pageBackground),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            PhotoGridTopBar(
                onBack = onBack,
                onShare = { showShareSheet = true },
                restaurant = restaurant,
                showBottomBorder = showTopBarBorder,
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = gridState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = gridBottomPadding,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                itemsIndexed(entries, key = { _, entry -> entry.url }) { index, entry ->
                    AsyncImage(
                        model = entry.url,
                        contentDescription = entry.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(GridImageCorner))
                            .clickable { fullscreenStartIndex = index },
                    )
                }
            }
        }

        fullscreenStartIndex?.let { start ->
            RestaurantPhotoFullscreenViewer(
                entries = entries,
                initialIndex = start,
                onDismiss = { fullscreenStartIndex = null },
                onOpenGrid = { fullscreenStartIndex = null },
            )
        }

        if (showShareSheet) {
            RestaurantPhotoShareSheet(
                restaurant = restaurant,
                onDismiss = { showShareSheet = false },
            )
        }
    }
}

@Composable
private fun PhotoGridTopBar(
    onBack: () -> Unit,
    onShare: () -> Unit,
    restaurant: Restaurant,
    showBottomBorder: Boolean,
) {
    val palette = LocalRestaurantPalette.current
    val density = LocalDensity.current
    val strokePx = with(density) { 1.dp.toPx() }
    val savedIds by WishlistStore.savedRestaurantIds.collectAsState()
    val unsavedInDetail by WishlistStore.unsavedInDetailCollection.collectAsState()
    val saved = remember(savedIds, unsavedInDetail, restaurant.id) {
        WishlistStore.isSaved(restaurant.id)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .drawBehind {
                if (showBottomBorder) {
                    drawLine(
                        color = palette.border,
                        start = Offset(0f, size.height - strokePx * 0.5f),
                        end = Offset(size.width, size.height - strokePx * 0.5f),
                        strokeWidth = strokePx,
                    )
                }
            }
            .padding(start = 4.dp, end = 8.dp, top = 4.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(40.dp),
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = palette.foreground,
                modifier = Modifier.size(22.dp),
            )
        }
        Box(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onShare,
            modifier = Modifier.size(40.dp),
        ) {
            Icon(
                Icons.Outlined.Share,
                contentDescription = "Share",
                tint = palette.foreground,
                modifier = Modifier.size(20.dp),
            )
        }
        HeartButton(
            active = saved,
            onClick = { WishlistStore.onHeartTap(restaurant) },
            size = HeartButtonSize.Small,
            style = HeartButtonStyle.Overlay,
            modifier = Modifier.padding(end = 4.dp),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RestaurantPhotoFullscreenViewer(
    entries: List<RestaurantPhotoEntry>,
    initialIndex: Int,
    onDismiss: () -> Unit,
    onOpenGrid: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (entries.isEmpty()) return

    val palette = LocalRestaurantPalette.current
    val safeStart = initialIndex.coerceIn(0, entries.lastIndex)
    val pagerState = rememberPagerState(
        initialPage = safeStart,
        pageCount = { entries.size },
    )
    var pagerScrollEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(pagerState.currentPage) {
        pagerScrollEnabled = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.pageBackground),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 88.dp)
                .clip(RectangleShape),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageSize = PageSize.Fill,
                pageSpacing = 0.dp,
                beyondViewportPageCount = 0,
                userScrollEnabled = pagerScrollEnabled,
            ) { page ->
                val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(palette.pageBackground)
                        .graphicsLayer { clip = true },
                ) {
                    FullscreenPhotoPage(
                        entry = entries[page],
                        pageOffset = pageOffset,
                        onZoomed = { zoomed ->
                            if (page == pagerState.currentPage) {
                                pagerScrollEnabled = !zoomed
                            }
                        },
                    )
                }
            }
        }

        FullscreenPhotoTopBar(
            title = entries[pagerState.currentPage].title,
            pageLabel = "${pagerState.currentPage + 1} of ${entries.size}",
            onOpenGrid = onOpenGrid,
            onDismiss = onDismiss,
        )
    }
}

@Composable
private fun FullscreenPhotoTopBar(
    title: String,
    pageLabel: String,
    onOpenGrid: () -> Unit,
    onDismiss: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onOpenGrid) {
            AllPhotosStackIcon(tint = palette.foreground)
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                color = palette.foreground,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
            Text(
                text = pageLabel,
                color = palette.foreground,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
            )
        }
        IconButton(onClick = onDismiss) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = palette.foreground)
        }
    }
}

/** Airbnb-style overlapping rounded squares for “all photos”. */
@Composable
private fun AllPhotosStackIcon(
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.size(22.dp)) {
        val strokeWidth = 2.dp.toPx()
        val corner = 4.dp.toPx()
        val squareW = size.width * 0.62f
        val squareH = size.height * 0.62f
        val backOffsetX = size.width * 0.06f
        val backOffsetY = size.height * 0.22f
        val frontOffsetX = size.width - squareW - size.width * 0.06f
        val frontOffsetY = size.height * 0.06f

        drawRoundRect(
            color = tint,
            topLeft = Offset(backOffsetX, backOffsetY),
            size = Size(squareW, squareH),
            cornerRadius = CornerRadius(corner, corner),
            style = Stroke(width = strokeWidth),
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(frontOffsetX, frontOffsetY),
            size = Size(squareW, squareH),
            cornerRadius = CornerRadius(corner, corner),
            style = Stroke(width = strokeWidth),
        )
    }
}

@Composable
private fun FullscreenPhotoPage(
    entry: RestaurantPhotoEntry,
    pageOffset: Float,
    onZoomed: (Boolean) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val density = LocalDensity.current
    val maxParallaxPx = with(density) { 80.dp.toPx() }
    val parallaxPx = pageOffset * maxParallaxPx

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = FullscreenImageSideInset),
            contentAlignment = Alignment.Center,
        ) {
            ZoomableRoundedPhoto(
                imageUrl = entry.url,
                parallaxOffsetPx = parallaxPx,
                cornerRadius = FullscreenImageCorner,
                onZoomed = onZoomed,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Text(
            text = entry.title,
            color = palette.foreground,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(
                    start = FullscreenImageSideInset,
                    end = FullscreenImageSideInset,
                    top = 16.dp,
                    bottom = FullscreenCaptionBottomPadding,
                ),
        )
    }
}

@Composable
private fun ZoomableRoundedPhoto(
    imageUrl: String,
    parallaxOffsetPx: Float,
    cornerRadius: androidx.compose.ui.unit.Dp,
    onZoomed: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var targetScale by remember(imageUrl) { mutableFloatStateOf(1f) }
    var targetOffset by remember(imageUrl) { mutableStateOf(Offset.Zero) }
    var animateZoomTransition by remember(imageUrl) { mutableStateOf(false) }
    var containerSize by remember(imageUrl) { mutableStateOf(IntSize.Zero) }
    val shape = RoundedCornerShape(cornerRadius)
    val zoomTransition = tween<Float>(
        durationMillis = ZoomTransitionMillis,
        easing = FastOutSlowInEasing,
    )
    val zoomAnimationSpec = if (animateZoomTransition) zoomTransition else snap()

    val displayScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = zoomAnimationSpec,
        label = "photo-zoom-scale",
    )
    val displayOffsetX by animateFloatAsState(
        targetValue = targetOffset.x,
        animationSpec = zoomAnimationSpec,
        label = "photo-zoom-offset-x",
    )
    val displayOffsetY by animateFloatAsState(
        targetValue = targetOffset.y,
        animationSpec = zoomAnimationSpec,
        label = "photo-zoom-offset-y",
    )

    fun applyZoomPan(pan: Offset, zoom: Float) {
        animateZoomTransition = false
        val newScale = (targetScale * zoom).coerceIn(1f, 4f)
        targetScale = newScale
        val zoomed = newScale > 1.01f
        onZoomed(zoomed)
        targetOffset = if (zoomed) {
            clampZoomPanOffset(targetOffset + pan, newScale, containerSize)
        } else {
            Offset.Zero
        }
    }

    fun animateZoomIn() {
        animateZoomTransition = true
        targetScale = DoubleTapZoomScale
        targetOffset = Offset.Zero
        onZoomed(true)
    }

    fun animateZoomOut() {
        animateZoomTransition = true
        targetScale = 1f
        targetOffset = Offset.Zero
        onZoomed(false)
    }

    val frameParallaxX = parallaxOffsetPx * FullscreenFrameParallaxFactor
    val imageParallaxX = parallaxOffsetPx * FullscreenImageParallaxFactor
    val zoomedIn = targetScale > 1.01f

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { containerSize = it }
                .pointerInput(imageUrl, targetScale) {
                    awaitEachGesture {
                        var pastTouchSlop = false
                        val touchSlop = viewConfiguration.touchSlop
                        awaitFirstDown(requireUnconsumed = false)
                        do {
                            val event = awaitPointerEvent()
                            if (event.changes.any { it.isConsumed }) continue

                            val zoomChange = event.calculateZoom()
                            val panChange = event.calculatePan()
                            val zoomedInGesture = targetScale > 1.01f
                            val isPinch = zoomChange != 1f

                            if (!pastTouchSlop) {
                                val zoomMotion = abs(1f - zoomChange)
                                if (zoomMotion > 0f || panChange.getDistance() > touchSlop || zoomedInGesture) {
                                    pastTouchSlop = true
                                }
                            }

                            if (pastTouchSlop && (isPinch || zoomedInGesture)) {
                                applyZoomPan(
                                    pan = if (zoomedInGesture) panChange else Offset.Zero,
                                    zoom = zoomChange,
                                )
                                event.changes.forEach { if (it.pressed) it.consume() }
                            }
                        } while (event.changes.any { it.pressed })
                    }
                }
                .graphicsLayer {
                    scaleX = displayScale
                    scaleY = displayScale
                    translationX = frameParallaxX + displayOffsetX
                    translationY = displayOffsetY
                    // Clip drawing only on the child; keep hit bounds in sync with zoom/pan.
                    clip = false
                }
                .pointerInput(imageUrl, zoomedIn) {
                    detectTapGestures(
                        onDoubleTap = {
                            if (zoomedIn) {
                                animateZoomOut()
                            } else {
                                animateZoomIn()
                            }
                        },
                    )
                },
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .graphicsLayer {
                        scaleX = FullscreenImageOverscale
                        scaleY = FullscreenImageOverscale
                        translationX = imageParallaxX
                    },
            )
        }
    }
}

/**
 * Keeps pan inside the zoomed image so empty space does not scroll into the rounded frame.
 * Assumes [scale] is applied about the center of [containerSize].
 */
private fun clampZoomPanOffset(
    offset: Offset,
    scale: Float,
    containerSize: IntSize,
): Offset {
    if (scale <= 1.01f || containerSize.width == 0 || containerSize.height == 0) {
        return Offset.Zero
    }
    val maxX = containerSize.width * 0.5f * (scale - 1f)
    val maxY = containerSize.height * 0.5f * (scale - 1f)
    return Offset(
        x = offset.x.coerceIn(-maxX, maxX),
        y = offset.y.coerceIn(-maxY, maxY),
    )
}
