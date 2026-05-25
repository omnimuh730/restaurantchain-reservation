@file:OptIn(ExperimentalFoundationApi::class, ExperimentalHazeMaterialsApi::class)

package com.mh.restaurantchainreservation.feature.discover.ui

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import com.mh.restaurantchainreservation.core.designsystem.transition.LocalAnimatedContentScope
import coil.compose.AsyncImage
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButton
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButtonSize
import com.mh.restaurantchainreservation.core.designsystem.components.trackBottomNavScroll
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButtonStyle
import com.mh.restaurantchainreservation.core.designsystem.components.HubSurfaceCardDefaults
import com.mh.restaurantchainreservation.core.designsystem.components.DiscoverMenuSeeAllCard
import com.mh.restaurantchainreservation.core.designsystem.components.DiscoverMenuTile
import com.mh.restaurantchainreservation.core.designsystem.components.hubSurfaceCard
import com.mh.restaurantchainreservation.core.designsystem.components.hubSurfaceShadow
import com.mh.restaurantchainreservation.core.designsystem.components.rememberDetailHeroPullMotion
import com.mh.restaurantchainreservation.core.designsystem.components.DetailHeroPullScaleMax
import com.mh.restaurantchainreservation.core.designsystem.components.DetailHeroMaxPullFraction
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.tokens.pageCanvasBackground
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.transition.LocalRestaurantSharedTransitionScope
import com.mh.restaurantchainreservation.core.designsystem.transition.rememberRestaurantSharedHeroModifier
import com.mh.restaurantchainreservation.core.designsystem.transition.rememberRestaurantSharedTitleModifier
import com.mh.restaurantchainreservation.core.model.Banner
import com.mh.restaurantchainreservation.core.model.City
import com.mh.restaurantchainreservation.core.model.DiscoverData
import com.mh.restaurantchainreservation.core.model.FoodType
import com.mh.restaurantchainreservation.core.model.NewsItem
import com.mh.restaurantchainreservation.core.model.QuickCategory
import com.mh.restaurantchainreservation.core.model.Restaurant
import com.mh.restaurantchainreservation.core.model.WishlistStore
import com.mh.restaurantchainreservation.core.model.NewsData
import com.mh.restaurantchainreservation.feature.discover.R
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlinx.coroutines.flow.distinctUntilChanged

private val DiningNewsCardWidth = 260.dp
private val DiningNewsHeroImageHeight = 128.dp
/** Title (2 lines) + summary (2 lines) with trailing chevron + padding; fixed card height. */
private val DiningNewsTextBlockHeight = 94.dp
private val DiningNewsCardTotalHeight = DiningNewsHeroImageHeight + DiningNewsTextBlockHeight

/** Fraction of the see-all stack area (width/height of [BoxWithConstraints]). */
private data class ThumbnailLayer(
    val layerName: String,
    val topPercent: Float,
    val leftPercent: Float,
    val widthPercent: Float,
    val heightPercent: Float,
    val zIndex: Float,
)

private val SeeAllThumbnailBack = ThumbnailLayer(
    layerName = "Back (Top-Left Image)",
    topPercent = 17f,
    leftPercent = 26.5f,
    widthPercent = 33.5f,
    heightPercent = 31.0f,
    zIndex = 1f,
)

private val SeeAllThumbnailMiddle = ThumbnailLayer(
    layerName = "Middle (Right Image)",
    topPercent = 25.0f,
    leftPercent = 46.5f,
    widthPercent = 37.0f,
    heightPercent = 36.5f,
    zIndex = 2f,
)

private val SeeAllThumbnailFront = ThumbnailLayer(
    layerName = "Front (Bottom-Left Image)",
    topPercent = 31.5f,
    leftPercent = 20.0f,
    widthPercent = 36.0f,
    heightPercent = 38.5f,
    zIndex = 3f,
)

/** Overlapping cluster at animation start (ordered slide-out). */
private val SeeAllThumbnailSlideStart = ThumbnailLayer(
    layerName = "Slide start",
    topPercent = 30.5f,
    leftPercent = 36f,
    widthPercent = 28f,
    heightPercent = 28f,
    zIndex = 0f,
)

private val SeeAllThumbShape = RoundedCornerShape(12.dp)
/** Profile hub card shell for Dining News + restaurant “More” tiles. */
private val MoreCardShape = HubSurfaceCardDefaults.QuickActionShape
private val DiningNewsCardShape = MoreCardShape

/** Restaurant cards shown per home rail before the More tile. */
private const val RestaurantRailVisibleCount = 8

/** City / food image rails show this many cards, then the explore-more tile. */
private const val ImageRailVisibleCount = 7

private val RestaurantMiniCardWidth = 176.dp
private val RestaurantMiniImageHeight =
    RestaurantMiniCardWidth / DiscoverRestaurantImageAspectWidthOverHeight
private val RestaurantMiniMetaBlockHeight = 46.dp
private val RestaurantMiniCardTotalHeight = RestaurantMiniImageHeight + RestaurantMiniMetaBlockHeight

private val WhereToEatCityTileWidth = 220.dp
private val WhereToEatCityTileHeight = 150.dp
/** Portrait see-all thumbs in Where to Eat card (height > width). */
private const val WhereToEatSeeAllThumbAspectHeightOverWidth = 1.36f
private const val WhereToEatSeeAllThumbSizeScale = 1.08f
/** Wider tiles without changing height (height follows base width × aspect). */
private const val WhereToEatSeeAllThumbWidthStretch = 1.14f
private const val WhereToEatSeeAllLabelFontScale = 1.14f
private val WhereToEatSeeAllThumbClusterWidth = 172.dp
/** Fan stack: left on top, center middle, right back; wings overlap center from below. */
private val WhereToEatFanThumbLeft = ThumbnailLayer(
    layerName = "Where to eat fan left",
    topPercent = 20f,
    leftPercent = 14f,
    widthPercent = 31f,
    heightPercent = 34f,
    zIndex = 3f,
)
private val WhereToEatFanThumbCenter = ThumbnailLayer(
    layerName = "Where to eat fan center",
    topPercent = 12f,
    leftPercent = 34f,
    widthPercent = 33f,
    heightPercent = 38f,
    zIndex = 2f,
)
private val WhereToEatFanThumbRight = ThumbnailLayer(
    layerName = "Where to eat fan right",
    topPercent = 20f,
    leftPercent = 50f,
    widthPercent = 31f,
    heightPercent = 34f,
    zIndex = 1f,
)
private val WhereToEatFanThumbSlideStart = ThumbnailLayer(
    layerName = "Where to eat fan slide start",
    topPercent = 26f,
    leftPercent = 36f,
    widthPercent = 27f,
    heightPercent = 27f,
    zIndex = 0f,
)
private val WhereToEatFanThumbRotations = floatArrayOf(-11f, 0f, 11f)
private val WhereToEatFanThumbPivotLeft = TransformOrigin(0.88f, 0.94f)
private val WhereToEatFanThumbPivotCenter = TransformOrigin(0.5f, 0.94f)
private val WhereToEatFanThumbPivotRight = TransformOrigin(0.12f, 0.94f)
private val WhereToEatTileTitlePaddingStart = 10.dp
private val WhereToEatTileTitlePaddingEnd = 10.dp
private val WhereToEatTileTitlePaddingBottom = 15.dp

private enum class ImageRailExploreMoreKind {
    WhereToEatPanorama,
    FoodTypeCollage,
}

/** Status bar + compact discover bar inner padding (8+44+8); matches [CompactDiscoverBar]. */
private val CompactDiscoverBarInnerHeight = 60.dp

/** Airbnb-style discover home rhythm (48px sections, 24px horizontal inset, 12px card gaps). */
private object DiscoverLayout {
    val PageHorizontal = 24.dp
    val SectionGap = 40.dp
    val SheetTopPadding = 24.dp
    val RailItemGap = 12.dp
    val RestaurantRailItemGap = 12.dp
    val SectionHeaderBottom = 12.dp
    val QuickCategoryHorizontal = 16.dp
    val QuickCategoryRowGap = 16.dp
    val QuickCategoryIconLabelGap = 8.dp
    val PriceListRowVertical = 12.dp
    val HeroTextHorizontal = 24.dp
    val StickyHeaderTopMin = 24.dp
}

/** "Restaurants by Price" row thumbnail — same width:height as other Discover restaurant heroes. */
private val PriceListThumbnailWidth = 110.dp
private val PriceListThumbnailHeight =
    PriceListThumbnailWidth / DiscoverRestaurantImageAspectWidthOverHeight
private val PriceListAvatarCorner = 16.dp
private val PriceListAvatarOverlayPadding = 8.dp
private val RestaurantRailImageShape = RoundedCornerShape(18.dp)

/** Default hub card shadow + clip for Discover image tiles. */
private fun Modifier.discoverImageCardSurface(shape: Shape): Modifier =
    hubSurfaceShadow(shape = shape).clip(shape)

@Composable
fun DiscoverHomeScreen(
    onOpenSearch: () -> Unit,
    onOpenMap: () -> Unit,
    onOpenRestaurant: (String) -> Unit,
    onOpenCategory: (String) -> Unit,
    onOpenFood: (String) -> Unit,
    onOpenLocation: (String) -> Unit,
    onOpenNewsList: () -> Unit,
    onOpenNewsArticle: (String) -> Unit,
    onOpenSection: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val listState = rememberLazyListState()
    val news = remember { NewsData.all }
    val compact by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 250
        }
    }

    var selectedPriceTab by rememberSaveable { mutableStateOf("$") }
    val priceTabBasePool = remember(selectedPriceTab) {
        restaurantsForPriceTab(selectedPriceTab)
    }
    var priceSectionRestaurants by remember(selectedPriceTab) {
        mutableStateOf(restaurantsForPriceTab(selectedPriceTab).take(5))
    }
    var priceSectionLoadingMore by remember { mutableStateOf(false) }
    LaunchedEffect(selectedPriceTab, compact) {
        priceSectionLoadingMore = false
        if (compact && listState.firstVisibleItemIndex >= 2) {
            listState.scrollToItem(index = 2, scrollOffset = 0)
        }
    }
    LaunchedEffect(listState, priceSectionRestaurants.size, selectedPriceTab, priceTabBasePool) {
        snapshotFlow {
            if (priceSectionLoadingMore) return@snapshotFlow false
            val poolSize = priceTabBasePool.size
            val n = priceSectionRestaurants.size
            if (n == 0 || n >= poolSize) {
                false
            } else {
                val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
                // Items: 0 hero, 1 rails, 2 price header → first restaurant at 3; last at 2 + n.
                val lastRestaurantIndex = 2 + n
                lastVisible >= lastRestaurantIndex
            }
        }
            .distinctUntilChanged()
            .collect { showLastRow ->
                if (!showLastRow) return@collect
                val loaded = priceSectionRestaurants.size
                val more = priceTabBasePool.drop(loaded).take(5)
                if (more.isEmpty()) return@collect
                priceSectionLoadingMore = true
                try {
                    delay(520)
                    priceSectionRestaurants = priceSectionRestaurants + more
                } finally {
                    priceSectionLoadingMore = false
                }
            }
    }

    val hazeState = rememberHazeState()
    DisposableEffect(hazeState) {
        DiscoverHazeRegistry.register(hazeState)
        onDispose { DiscoverHazeRegistry.unregister(hazeState) }
    }

    CompositionLocalProvider(LocalDiscoverHazeState provides hazeState) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.pageBackground),
    ) {
        val density = LocalDensity.current
        val statusBarInsets = WindowInsets.statusBars
        val compactBarTotalHeight = remember(density, statusBarInsets) {
            with(density) { statusBarInsets.getTop(this).toDp() } + CompactDiscoverBarInnerHeight
        }
        var priceHeaderDocked by remember { mutableStateOf(false) }
        val priceHeaderUndockThresholdPx = remember(density) {
            with(density) { 12.dp.toPx().toInt() }
        }

        val headerTopPadding by remember(density, compactBarTotalHeight, compact) {
            derivedStateOf {
                val headerItem = listState.layoutInfo.visibleItemsInfo.firstOrNull {
                    it.key == "restaurants-by-price-header"
                }
                val inPriceSection = listState.firstVisibleItemIndex >= 2
                when {
                    compact && inPriceSection && (priceHeaderDocked || headerItem == null) ->
                        compactBarTotalHeight
                    headerItem != null -> {
                        val offsetDp = with(density) { headerItem.offset.toDp() }
                        if (compact) {
                            (compactBarTotalHeight - offsetDp).coerceIn(
                                DiscoverLayout.StickyHeaderTopMin,
                                compactBarTotalHeight,
                            )
                        } else {
                            DiscoverLayout.StickyHeaderTopMin
                        }
                    }
                    inPriceSection && compact -> compactBarTotalHeight
                    else -> DiscoverLayout.StickyHeaderTopMin
                }
            }
        }

        SideEffect {
            val headerItem = listState.layoutInfo.visibleItemsInfo.firstOrNull {
                it.key == "restaurants-by-price-header"
            }
            val inPriceSection = listState.firstVisibleItemIndex >= 2
            priceHeaderDocked = when {
                !compact || !inPriceSection -> false
                headerItem != null && headerItem.offset <= 0 -> true
                headerItem != null && headerItem.offset > priceHeaderUndockThresholdPx -> false
                else -> priceHeaderDocked
            }
        }

        val discoverBannerHeightPx = remember(density) { with(density) { DiscoverBannerHeight.toPx() } }
        val discoverBannerMaxPullPx = remember(discoverBannerHeightPx) {
            discoverBannerHeightPx * DetailHeroMaxPullFraction
        }
        val discoverBannerScope = rememberCoroutineScope()
        val discoverBannerMotion = rememberDetailHeroPullMotion(discoverBannerScope)
        val discoverBannerScrollOffsetPx by remember(listState, discoverBannerHeightPx) {
            derivedStateOf {
                if (listState.firstVisibleItemIndex == 0) {
                    listState.firstVisibleItemScrollOffset.toFloat()
                } else {
                    discoverBannerHeightPx
                }
            }
        }
        val discoverBannerPullPx by discoverBannerMotion.pullDistancePx
        val discoverBannerPullProgress = (discoverBannerPullPx / discoverBannerMaxPullPx).coerceIn(0f, 1f)

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .pageCanvasBackground()
                .trackBottomNavScroll()
                .nestedScroll(
                    discoverBannerMotion.nestedScrollConnection(
                        maxPullPx = discoverBannerMaxPullPx,
                        isAtTop = {
                            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
                        }
                    ),
                )
                .hazeSource(state = hazeState),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            item {
                HeroBanner(
                    banners = DiscoverData.BANNERS,
                    onOpenSearch = onOpenSearch,
                    onOpenMap = onOpenMap,
                    onViewAll = { onOpenSection("banners") },
                    onBannerClick = { bannerId -> onOpenSection(bannerId) },
                    bannerScrollOffsetPx = discoverBannerScrollOffsetPx,
                    pullProgress = discoverBannerPullProgress,
                    pullDistancePx = discoverBannerPullPx,
                )
            }
            item {
                Column(
                    modifier = Modifier
                        .offset(y = -DiscoverSheetTopOverlap)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .background(palette.pageBackground)
                        .padding(top = DiscoverLayout.SheetTopPadding, bottom = 0.dp),
                ) {
                    QuickCategoryGrid(DiscoverData.QUICK_CATEGORIES, onOpenCategory)
                    RailSpacer(DiscoverLayout.SectionGap)
                    ImageRailSection(
                        title = "Where to Eat?",
                        exploreMoreKind = ImageRailExploreMoreKind.WhereToEatPanorama,
                        seeAllPreviewImages = DiscoverData.CITIES
                            .take(ImageRailVisibleCount)
                            .take(3)
                            .map { it.image },
                        onSeeAll = { onOpenSection("where-to-eat") },
                        rowVerticalAlignment = Alignment.Top,
                    ) {
                        CityRail(
                            cities = DiscoverData.CITIES.take(ImageRailVisibleCount),
                            onClick = onOpenLocation,
                        )
                    }
                    RailSpacer(DiscoverLayout.SectionGap)
                    ImageRailSection(
                        title = "Top Picks by Food Type",
                        exploreMoreKind = ImageRailExploreMoreKind.FoodTypeCollage,
                        seeAllPreviewImages = DiscoverData.FOOD_TYPES
                            .take(ImageRailVisibleCount)
                            .take(3)
                            .map { it.image },
                        onSeeAll = { onOpenSection("top-picks-food") },
                        rowVerticalAlignment = Alignment.Top,
                    ) {
                        FoodRail(
                            foodTypes = DiscoverData.FOOD_TYPES.take(ImageRailVisibleCount),
                            onClick = onOpenFood,
                        )
                    }
                    RailSpacer(DiscoverLayout.SectionGap)
                    RestaurantRail(
                        title = "Monthly Best",
                        restaurants = DiscoverData.MONTHLY_BEST,
                        onOpenRestaurant = onOpenRestaurant,
                        onMore = { onOpenSection("monthly-best") },
                    )
                    RailSpacer(DiscoverLayout.SectionGap)
                    RestaurantRail(
                        title = "Loved by Locals",
                        restaurants = DiscoverData.LOVED_BY_LOCALS,
                        onOpenRestaurant = onOpenRestaurant,
                        onMore = { onOpenSection("loved-by-locals") },
                    )
                    RailSpacer(DiscoverLayout.SectionGap)
                    NewsRail(
                        news = news,
                        onOpenArticle = onOpenNewsArticle,
                        onMore = onOpenNewsList,
                    )
                    RailSpacer(DiscoverLayout.SectionGap)
                    RestaurantRail(
                        title = "New This Week",
                        restaurants = rememberNewThisWeek(),
                        onOpenRestaurant = onOpenRestaurant,
                        onMore = { onOpenSection("new-this-week") },
                    )
                    RailSpacer(DiscoverLayout.SectionGap)
                    RestaurantRail(
                        title = "Late Night Finds",
                        restaurants = rememberLateNight(),
                        onOpenRestaurant = onOpenRestaurant,
                        onMore = { onOpenSection("late-night") },
                    )
                }
            }
            stickyHeader(key = "restaurants-by-price-header") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(palette.pageBackground)
                        .padding(top = headerTopPadding),
                ) {
                    RestaurantsByPriceHeaderAndTabs(
                        selectedPrice = selectedPriceTab,
                        onSelectPrice = { selectedPriceTab = it },
                        placesLabel = "${priceTabBasePool.size.coerceAtLeast(6)}+ places",
                    )
                }
            }
            itemsIndexed(
                items = priceSectionRestaurants,
                key = { _, restaurant -> restaurant.id },
            ) { _, restaurant ->
                RestaurantByPriceListRowWithLazyEnter(
                    restaurant = restaurant,
                    onOpenRestaurant = {
                        onOpenRestaurant(resolveRestaurantNavigationId(selectedPriceTab, restaurant.id))
                    },
                    modifier = Modifier.padding(
                        horizontal = DiscoverLayout.PageHorizontal,
                        vertical = DiscoverLayout.PriceListRowVertical,
                    ),
                )
            }
            if (priceSectionLoadingMore) {
                item(key = "price-section-loading-more") {
                    PriceSectionLoadingMoreFooter()
                }
            }
        }

        AnimatedVisibility(
            visible = compact,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = spring(dampingRatio = 0.85f, stiffness = 400f)
            ) + fadeIn(tween(220)),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(220)
            ) + fadeOut(tween(180)),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .zIndex(4f),
        ) {
            CompactDiscoverBar(
                hazeState = hazeState,
                onOpenSearch = onOpenSearch,
                onOpenMap = onOpenMap,
            )
        }

    }
    }
}

private val DiscoverBannerHeight = 360.dp
private val DiscoverSheetTopOverlap = 32.dp

/** Pull-to-stretch + scroll parallax for Discover hero banner photos. */
@Composable
private fun HeroBanner(
    banners: List<Banner>,
    onOpenSearch: () -> Unit,
    onOpenMap: () -> Unit,
    onViewAll: () -> Unit,
    onBannerClick: (String) -> Unit,
    bannerScrollOffsetPx: Float,
    pullProgress: Float,
    pullDistancePx: Float,
) {
    val palette = LocalRestaurantPalette.current
    val density = LocalDensity.current
    val pagerState = rememberPagerState(pageCount = { banners.size })

    LaunchedEffect(pagerState, banners.size) {
        while (banners.isNotEmpty()) {
            delay(3000L)
            pagerState.animateScrollToPage((pagerState.currentPage + 1) % banners.size)
        }
    }

    val pullDistanceDp = with(density) { pullDistancePx.toDp() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(DiscoverBannerHeight + pullDistanceDp),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            val banner = banners[page]
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clipToBounds(),
                ) {
                    AsyncImage(
                        model = banner.image,
                        contentDescription = banner.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                translationY = bannerScrollOffsetPx * 0.40f
                                val scale = 1f + pullProgress * DetailHeroPullScaleMax
                                scaleX = scale
                                scaleY = scale
                                transformOrigin = TransformOrigin(0.5f, 0f)
                            }
                            .clickable { onBannerClick(banner.id) },
                    )
                }
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    RestaurantColors.Overlay.borderSubtle,
                                    RestaurantColors.Base.black.copy(alpha = 0.16f),
                                    RestaurantColors.Base.black.copy(alpha = 0.72f),
                                ),
                            ),
                        ),
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(
                            start = DiscoverLayout.HeroTextHorizontal,
                            end = DiscoverLayout.HeroTextHorizontal,
                            bottom = 88.dp,
                        )
                        .clickable { onBannerClick(banner.id) },
                ) {
                    Text(
                        text = banner.title,
                        color = RestaurantColors.Base.white,
                        fontSize = 25.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 2,
                    )
                    Text(
                        text = banner.subtitle,
                        color = RestaurantColors.Overlay.imageCaption,
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp),
                        maxLines = 2,
                    )
                    if (banner.cta.isNotBlank()) {
                        Text(
                            text = banner.cta.uppercase(),
                            color = RestaurantColors.Base.white.copy(alpha = 0.68f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 9.dp),
                            maxLines = 1,
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(2f)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(
                    start = DiscoverLayout.PageHorizontal,
                    top = 18.dp,
                    end = DiscoverLayout.PageHorizontal,
                )
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(DiscoverLayout.RailItemGap),
        ) {
            GlassSearchButton(
                title = "Find a restaurant",
                subtitle = "Type of food, restaurant name…",
                compact = false,
                opaqueGlass = false,
                palette = palette,
                onClick = onOpenSearch,
                modifier = Modifier.weight(1f),
            )
            GlassMapButton(compact = false, opaqueGlass = false, palette = palette, onClick = onOpenMap)
        }

        PressableScale(
            onClick = onViewAll,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .zIndex(2f)
                .padding(end = 12.dp, bottom = 48.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(RestaurantColors.Overlay.imageCaption)
                .padding(horizontal = 10.dp, vertical = 6.dp),
        ) {
            Text("View All", color = RestaurantColors.Text.primary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }

        HeroBannerPagerIndicators(
            pageCount = banners.size,
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(3f)
                .padding(bottom = 64.dp),
        )
    }
}

@Composable
private fun HeroBannerPagerIndicators(
    pageCount: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    if (pageCount <= 1) return
    val scrollPosition = pagerState.currentPage + pagerState.currentPageOffsetFraction
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val distance = abs(scrollPosition - index).coerceIn(0f, 1f)
            val width = (5f + (14f - 5f) * (1f - distance)).dp
            val dotAlpha = lerp(0.42f, 0.96f, 1f - distance)
            Box(
                modifier = Modifier
                    .height(5.dp)
                    .width(width)
                    .clip(RoundedCornerShape(50))
                    .background(RestaurantColors.Base.white.copy(alpha = dotAlpha)),
            )
        }
    }
}

@Composable
private fun CompactDiscoverBar(
    hazeState: HazeState,
    onOpenSearch: () -> Unit,
    onOpenMap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .hazeEffect(state = hazeState, style = HazeMaterials.thin())
            .border(width = 1.dp, color = discoverGlassBarEdgeColor(palette))
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(
                start = DiscoverLayout.PageHorizontal,
                end = DiscoverLayout.PageHorizontal,
                top = 8.dp,
                bottom = 8.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DiscoverLayout.RailItemGap),
    ) {
        GlassSearchButton(
            title = "Find a restaurant",
            subtitle = "Type of food, restaurant name…",
            compact = true,
            opaqueGlass = false,
            palette = palette,
            onClick = onOpenSearch,
            modifier = Modifier.weight(1f),
        )
        GlassMapButton(compact = true, opaqueGlass = false, palette = palette, onClick = onOpenMap)
    }
}

private fun discoverGlassBarEdgeColor(@Suppress("UNUSED_PARAMETER") palette: RestaurantPalette): Color =
    RestaurantColors.Base.white.copy(alpha = 0.42f)

private data class GlassPillLayers(
    val baseFill: Color,
    val borderAlpha: Float,
    val iconPlateFill: Color,
    val gradientTopAlpha: Float,
)

private fun discoverGlassPillLayers(
    palette: RestaurantPalette,
    compact: Boolean,
    opaqueGlass: Boolean,
): GlassPillLayers {
    val strong = opaqueGlass && compact
    val baseAlpha = when {
        strong -> 0.52f
        compact -> 0.38f
        else -> 0.28f
    }
    val borderAlpha = when {
        strong -> 0.70f
        compact -> 0.60f
        else -> 0.56f
    }
    val iconAlpha = when {
        strong -> 0.52f
        compact -> 0.44f
        else -> 0.40f
    }
    val gradTop = when {
        strong -> 0.50f
        compact -> 0.44f
        else -> 0.38f
    }
    return GlassPillLayers(
        baseFill = RestaurantColors.Base.white.copy(alpha = baseAlpha),
        borderAlpha = borderAlpha,
        iconPlateFill = RestaurantColors.Base.white.copy(alpha = iconAlpha),
        gradientTopAlpha = gradTop,
    )
}

private fun discoverGlassPillBorderColor(
    @Suppress("UNUSED_PARAMETER") palette: RestaurantPalette,
    borderAlpha: Float,
): Color = RestaurantColors.Base.white.copy(alpha = borderAlpha.coerceIn(0f, 1f))

@Composable
private fun GlassSearchButton(
    title: String,
    subtitle: String,
    compact: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    opaqueGlass: Boolean = false,
    palette: RestaurantPalette,
) {
    val height = if (compact) 44.dp else 56.dp
    val iconSize = if (compact) 32.dp else 38.dp
    val layers = discoverGlassPillLayers(palette, compact, opaqueGlass)
    val horizontalPadding = if (compact) 10.dp else 14.dp
    PressableScale(
        onClick = onClick,
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(999.dp))
            .border(1.dp, discoverGlassPillBorderColor(palette, layers.borderAlpha), RoundedCornerShape(999.dp))
            .background(layers.baseFill),
    ) {
        Box(modifier = Modifier.matchParentSize()) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                RestaurantColors.Base.white.copy(alpha = layers.gradientTopAlpha),
                                RestaurantColors.Base.white.copy(alpha = layers.gradientTopAlpha * 0.4f),
                                Color.Transparent,
                            ),
                        ),
                    ),
            )
            Row(
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = horizontalPadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .clip(CircleShape)
                        .background(layers.iconPlateFill)
                        .border(1.dp, discoverGlassPillBorderColor(palette, layers.borderAlpha * 0.92f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Search, contentDescription = null, tint = palette.foreground, modifier = Modifier.size(if (compact) 15.dp else 18.dp))
                }
                Spacer(Modifier.width(if (compact) 9.dp else 11.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = palette.foreground,
                        fontSize = if (compact) 13.sp else 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = subtitle,
                        color = palette.foreground.copy(alpha = if (opaqueGlass && compact) 0.72f else 0.68f),
                        fontSize = if (compact) 10.sp else 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun GlassMapButton(
    compact: Boolean,
    onClick: () -> Unit,
    opaqueGlass: Boolean = false,
    palette: RestaurantPalette,
) {
    val size = if (compact) 44.dp else 56.dp
    val layers = discoverGlassPillLayers(palette, compact, opaqueGlass)
    PressableScale(
        onClick = onClick,
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .border(1.dp, discoverGlassPillBorderColor(palette, layers.borderAlpha), CircleShape)
            .background(layers.baseFill),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            RestaurantColors.Base.white.copy(alpha = layers.gradientTopAlpha),
                            RestaurantColors.Base.white.copy(alpha = layers.gradientTopAlpha * 0.4f),
                            Color.Transparent,
                        ),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Map, contentDescription = "Open map search", tint = palette.foreground, modifier = Modifier.size(if (compact) 23.dp else 28.dp))
        }
    }
}

@Composable
private fun QuickCategoryGrid(categories: List<QuickCategory>, onClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DiscoverLayout.QuickCategoryHorizontal, vertical = DiscoverLayout.QuickCategoryIconLabelGap),
        verticalArrangement = Arrangement.spacedBy(DiscoverLayout.QuickCategoryRowGap),
    ) {
        categories.chunked(4).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
            ) {
                row.forEach { category ->
                    QuickCategoryButton(
                        category = category,
                        onClick = { onClick(category.id) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                    )
                }
                repeat(4 - row.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun QuickCategoryButton(
    category: QuickCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    PressableScale(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(categoryDrawableRes(category.id)),
                contentDescription = category.label,
                modifier = Modifier.size(48.dp),
                contentScale = ContentScale.Fit,
            )
            Text(
                text = categoryTwoLineLabel(category.id),
                color = palette.foreground,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = DiscoverLayout.QuickCategoryIconLabelGap),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ImageRailSection(
    title: String,
    exploreMoreKind: ImageRailExploreMoreKind,
    seeAllPreviewImages: List<Any>,
    onSeeAll: () -> Unit,
    rowVerticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable () -> Unit,
) {
    SectionHeader(title = title)
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = DiscoverLayout.PageHorizontal),
        horizontalArrangement = Arrangement.spacedBy(DiscoverLayout.RailItemGap),
        verticalAlignment = rowVerticalAlignment,
    ) {
        item { content() }
        item {
            when (exploreMoreKind) {
                ImageRailExploreMoreKind.WhereToEatPanorama ->
                    WhereToEatSeeAllCard(
                        previewImages = seeAllPreviewImages,
                        onClick = onSeeAll,
                    )
                ImageRailExploreMoreKind.FoodTypeCollage ->
                    FoodTypeSeeAllCard(
                        previewImages = seeAllPreviewImages,
                        onClick = onSeeAll,
                    )
            }
        }
    }
}

@Composable
private fun CityRail(cities: List<City>, onClick: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(DiscoverLayout.RailItemGap)) {
        cities.forEach { city ->
            WhereToEatCityTile(
                title = city.label,
                image = city.image,
                onClick = { onClick(city.id) },
            )
        }
    }
}

@Composable
private fun FoodRail(foodTypes: List<FoodType>, onClick: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(DiscoverLayout.RailItemGap),
        verticalAlignment = Alignment.Top,
    ) {
        foodTypes.forEach { food ->
            FoodTypeRailTile(
                food = food,
                onClick = { onClick(food.id) },
            )
        }
    }
}

@Composable
private fun FoodTypeRailTile(
    food: FoodType,
    onClick: () -> Unit,
) {
    val placeCount = remember(food.id) { DiscoverData.byFoodType(food.id).size }
    DiscoverMenuTile(
        imageUrl = food.image,
        title = food.label,
        imageCaption = "$placeCount places",
        onClick = onClick,
    )
}

@Composable
private fun WhereToEatCityTile(
    title: String,
    image: String,
    onClick: () -> Unit,
) {
    PressableScale(
        onClick = onClick,
        modifier = Modifier
            .size(WhereToEatCityTileWidth, WhereToEatCityTileHeight)
            .discoverImageCardSurface(RestaurantRailImageShape),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = image,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            WhereToEatTileTextOverlay(title = title)
        }
    }
}

@Composable
private fun WhereToEatTileTextOverlay(
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to Color.Transparent,
                            0.5f to Color.Transparent,
                            0.78f to RestaurantColors.Base.black.copy(alpha = 0.42f),
                            1f to RestaurantColors.Base.black.copy(alpha = 0.75f),
                        ),
                    ),
                ),
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(
                    start = WhereToEatTileTitlePaddingStart,
                    end = WhereToEatTileTitlePaddingEnd,
                    bottom = WhereToEatTileTitlePaddingBottom,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                color = RestaurantColors.Base.white.copy(alpha = 0.90f),
                fontSize = 19.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.2).sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 5.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = null,
                tint = RestaurantColors.Overlay.veilFrosted,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun RestaurantRail(
    title: String,
    restaurants: List<Restaurant>,
    onOpenRestaurant: (String) -> Unit,
    onMore: () -> Unit,
) {
    SectionHeader(title = title)
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = DiscoverLayout.PageHorizontal),
        horizontalArrangement = Arrangement.spacedBy(DiscoverLayout.RestaurantRailItemGap),
        verticalAlignment = Alignment.Top,
    ) {
        restaurants.take(RestaurantRailVisibleCount).forEach { restaurant ->
            item(key = restaurant.id) {
                AirbnbMiniCard(
                    restaurant = restaurant,
                    width = RestaurantMiniCardWidth,
                    onClick = { onOpenRestaurant(restaurant.id) },
                )
            }
        }
        item {
            Box(
                modifier = Modifier.height(RestaurantMiniCardTotalHeight),
                contentAlignment = Alignment.TopCenter,
            ) {
                RestaurantSeeAllCard(
                    previewImages = restaurants.take(3).map { it.image },
                    onClick = onMore,
                )
            }
        }
    }
}

@Composable
private fun AirbnbMiniCard(
    restaurant: Restaurant,
    width: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val savedIds by WishlistStore.savedRestaurantIds.collectAsState()
    val saved = restaurant.id in savedIds
    val shared = LocalRestaurantSharedTransitionScope.current
    val animatedContent = LocalAnimatedContentScope.current
    val heroModifier = rememberRestaurantSharedHeroModifier(restaurant.id, shared, animatedContent)
    val titleModifier = rememberRestaurantSharedTitleModifier(restaurant.id, shared, animatedContent)
    val widthModifier = if (width > 0.dp) modifier.width(width) else modifier
    PressableScale(
        onClick = onClick,
        modifier = widthModifier,
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(DiscoverRestaurantImageAspectWidthOverHeight)
                    .discoverImageCardSurface(RestaurantRailImageShape)
                    .background(palette.cardSurface),
            ) {
                AsyncImage(
                    model = restaurant.image,
                    contentDescription = restaurant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .then(heroModifier),
                )
                val tag = restaurant.tag
                if (!tag.isNullOrBlank()) {
                    com.mh.restaurantchainreservation.core.designsystem.badge.RestaurantCardTagChip(
                        text = tag,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                    )
                }
                HeartButton(
                    active = saved,
                    onClick = { WishlistStore.onHeartTap(restaurant) },
                    size = HeartButtonSize.Medium,
                    style = HeartButtonStyle.Overlay,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                )
            }
            Text(
                text = restaurant.name,
                color = palette.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = titleModifier.padding(top = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val metaColor = palette.mutedForeground
                Text(
                    text = restaurant.area ?: restaurant.cuisine,
                    color = metaColor,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                DiscoverInlineDot(color = metaColor)
                Text(
                    text = "★ %.1f".format(restaurant.rating),
                    color = metaColor,
                    fontSize = 12.sp,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun NewsRail(
    news: List<NewsItem>,
    onOpenArticle: (String) -> Unit,
    onMore: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    SectionHeader(title = "Dining News")
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = DiscoverLayout.PageHorizontal),
        horizontalArrangement = Arrangement.spacedBy(DiscoverLayout.RailItemGap),
        verticalAlignment = Alignment.Top,
    ) {
        news.forEachIndexed { index, newsItem ->
            item(key = newsItem.id) {
                val cardWidth = if (index == 0) 288.dp else DiningNewsCardWidth
                PressableScale(
                    onClick = { onOpenArticle(newsItem.id) },
                    modifier = Modifier
                        .width(cardWidth)
                        .height(DiningNewsCardTotalHeight)
                        .hubSurfaceCard(palette = palette, shape = DiningNewsCardShape),
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(DiningNewsHeroImageHeight),
                        ) {
                            AsyncImage(
                                model = newsItem.image,
                                contentDescription = newsItem.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(Brush.verticalGradient(listOf(Color.Transparent, RestaurantColors.Base.black.copy(alpha = 0.62f)))),
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(newsItem.category.badgeColor(palette))
                                    .padding(horizontal = 9.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    newsItem.category.displayLabel(),
                                    color = RestaurantColors.Base.white,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AccessTime,
                                    contentDescription = null,
                                    tint = RestaurantColors.Base.white,
                                    modifier = Modifier.size(12.dp),
                                )
                                Text(
                                    text = formatNewsTimeAgo(newsItem.publishedAtEpochMs),
                                    color = RestaurantColors.Base.white,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text("·", color = RestaurantColors.Base.white.copy(alpha = 0.6f), fontSize = 11.sp)
                                Text(
                                    text = "${newsItem.readMinutes} mins read",
                                    color = RestaurantColors.Base.white,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                        ) {
                            Text(
                                newsItem.title,
                                color = palette.foreground,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Bottom,
                            ) {
                                Text(
                                    text = newsItem.summary,
                                    color = palette.mutedForeground,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 4.dp),
                                )
                                Icon(
                                    imageVector = Icons.Outlined.ChevronRight,
                                    contentDescription = null,
                                    tint = palette.mutedForeground,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
        item {
            NewsSeeAllCard(
                previewImages = news.take(3).map { it.image },
                onClick = onMore,
            )
        }
    }
}

/** 0..1 progress within [start,end] of global timeline `t` in 0..1. */
private fun staggerProgress(t: Float, start: Float, end: Float): Float = when {
    t <= start -> 0f
    t >= end -> 1f
    else -> ((t - start) / (end - start)).coerceIn(0f, 1f)
}

private fun ThumbnailLayer.lerpedFrom(start: ThumbnailLayer, p: Float): ThumbnailLayer {
    val t = p.coerceIn(0f, 1f)
    return ThumbnailLayer(
        layerName = layerName,
        topPercent = lerp(start.topPercent, topPercent, t),
        leftPercent = lerp(start.leftPercent, leftPercent, t),
        widthPercent = lerp(start.widthPercent, widthPercent, t),
        heightPercent = heightPercent,
        zIndex = zIndex,
    )
}

/** End rotations: back CCW, middle CW, front CCW (front tilted further left vs back). */
private val SeeAllStackLayerRotations = floatArrayOf(-5.5f, 7.5f, -12f)

@Composable
private fun SeeAllSlideThumbnailStack(
    images: List<Any>,
    thumbCornerShape: RoundedCornerShape,
    thumbBorderWidth: Dp,
    modifier: Modifier = Modifier,
) {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }
    val t by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(880, easing = FastOutSlowInEasing),
        label = "see-all-slide",
    )
    val pBack = staggerProgress(t, 0f, 0.36f)
    val pMid = staggerProgress(t, 0.18f, 0.62f)
    val pFront = staggerProgress(t, 0.38f, 1f)

    BoxWithConstraints(modifier = modifier) {
        val w = maxWidth
        val h = maxHeight
        val gBack = SeeAllThumbnailBack.lerpedFrom(SeeAllThumbnailSlideStart, pBack)
        val gMid = SeeAllThumbnailMiddle.lerpedFrom(SeeAllThumbnailSlideStart, pMid)
        val gFront = SeeAllThumbnailFront.lerpedFrom(SeeAllThumbnailSlideStart, pFront)

        val wBack = w * (gBack.widthPercent / 100f)
        val wMid = w * (gMid.widthPercent / 100f)
        val wFront = w * (gFront.widthPercent / 100f)

        AnimatedSeeAllThumbnail(
            imageModel = images[0],
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(w * (gBack.leftPercent / 100f), h * (gBack.topPercent / 100f))
                .zIndex(gBack.zIndex),
            width = wBack,
            height = wBack,
            cornerShape = thumbCornerShape,
            borderWidth = thumbBorderWidth,
            slideProgress = pBack,
            endRotationDegrees = SeeAllStackLayerRotations[0],
        )
        AnimatedSeeAllThumbnail(
            imageModel = images[1],
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(w * (gMid.leftPercent / 100f), h * (gMid.topPercent / 100f))
                .zIndex(gMid.zIndex),
            width = wMid,
            height = wMid,
            cornerShape = thumbCornerShape,
            borderWidth = thumbBorderWidth,
            slideProgress = pMid,
            endRotationDegrees = SeeAllStackLayerRotations[1],
        )
        AnimatedSeeAllThumbnail(
            imageModel = images[2],
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(w * (gFront.leftPercent / 100f), h * (gFront.topPercent / 100f))
                .zIndex(gFront.zIndex),
            width = wFront,
            height = wFront,
            cornerShape = thumbCornerShape,
            borderWidth = thumbBorderWidth,
            slideProgress = pFront,
            endRotationDegrees = SeeAllStackLayerRotations[2],
        )
    }
}

/** Three-thumbnail fan for Where to Eat see-all (left top, center middle, right back). */
@Composable
private fun WhereToEatFanThumbnailStack(
    images: List<Any>,
    thumbCornerShape: RoundedCornerShape,
    thumbBorderWidth: Dp,
    modifier: Modifier = Modifier,
    thumbAspectHeightOverWidth: Float = WhereToEatSeeAllThumbAspectHeightOverWidth,
    thumbSizeScale: Float = WhereToEatSeeAllThumbSizeScale,
    thumbWidthStretch: Float = WhereToEatSeeAllThumbWidthStretch,
) {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }
    val t by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(880, easing = FastOutSlowInEasing),
        label = "where-to-eat-fan-slide",
    )
    val pLeft = staggerProgress(t, 0f, 0.36f)
    val pCenter = staggerProgress(t, 0.18f, 0.62f)
    val pRight = staggerProgress(t, 0.38f, 1f)

    BoxWithConstraints(modifier = modifier) {
        val w = maxWidth
        val h = maxHeight
        val gLeft = WhereToEatFanThumbLeft.lerpedFrom(WhereToEatFanThumbSlideStart, pLeft)
        val gCenter = WhereToEatFanThumbCenter.lerpedFrom(WhereToEatFanThumbSlideStart, pCenter)
        val gRight = WhereToEatFanThumbRight.lerpedFrom(WhereToEatFanThumbSlideStart, pRight)
        val baseLeft = w * (gLeft.widthPercent / 100f) * thumbSizeScale
        val baseCenter = w * (gCenter.widthPercent / 100f) * thumbSizeScale
        val baseRight = w * (gRight.widthPercent / 100f) * thumbSizeScale
        val hLeft = baseLeft * thumbAspectHeightOverWidth
        val hCenter = baseCenter * thumbAspectHeightOverWidth
        val hRight = baseRight * thumbAspectHeightOverWidth
        val wLeft = baseLeft * thumbWidthStretch
        val wCenter = baseCenter * thumbWidthStretch
        val wRight = baseRight * thumbWidthStretch

        AnimatedSeeAllThumbnail(
            imageModel = images[2],
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(w * (gRight.leftPercent / 100f), h * (gRight.topPercent / 100f))
                .zIndex(gRight.zIndex),
            width = wRight,
            height = hRight,
            cornerShape = thumbCornerShape,
            borderWidth = thumbBorderWidth,
            slideProgress = pRight,
            endRotationDegrees = WhereToEatFanThumbRotations[2],
            transformOrigin = WhereToEatFanThumbPivotRight,
        )
        AnimatedSeeAllThumbnail(
            imageModel = images[1],
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(w * (gCenter.leftPercent / 100f), h * (gCenter.topPercent / 100f))
                .zIndex(gCenter.zIndex),
            width = wCenter,
            height = hCenter,
            cornerShape = thumbCornerShape,
            borderWidth = thumbBorderWidth,
            slideProgress = pCenter,
            endRotationDegrees = WhereToEatFanThumbRotations[1],
            transformOrigin = WhereToEatFanThumbPivotCenter,
        )
        AnimatedSeeAllThumbnail(
            imageModel = images[0],
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(w * (gLeft.leftPercent / 100f), h * (gLeft.topPercent / 100f))
                .zIndex(gLeft.zIndex),
            width = wLeft,
            height = hLeft,
            cornerShape = thumbCornerShape,
            borderWidth = thumbBorderWidth,
            slideProgress = pLeft,
            endRotationDegrees = WhereToEatFanThumbRotations[0],
            transformOrigin = WhereToEatFanThumbPivotLeft,
        )
    }
}

@Composable
private fun WhereToEatSeeAllCard(
    previewImages: List<Any>,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val images = remember(previewImages) {
        when {
            previewImages.size >= 3 -> previewImages.take(3)
            previewImages.size == 2 -> previewImages + previewImages.last()
            previewImages.size == 1 -> List(3) { previewImages.first() }
            else -> List(3) {
                "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=200&h=200&fit=crop"
            }
        }
    }
    val thumbBorder = seeAllThumbBorderWidth(WhereToEatCityTileWidth, WhereToEatCityTileHeight)

    PressableScale(
        onClick = onClick,
        modifier = Modifier
            .size(WhereToEatCityTileWidth, WhereToEatCityTileHeight)
            .hubSurfaceCard(palette = palette, shape = MoreCardShape),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .width(WhereToEatSeeAllThumbClusterWidth)
                        .fillMaxHeight(),
                ) {
                    WhereToEatFanThumbnailStack(
                        images = images,
                        thumbCornerShape = SeeAllThumbShape,
                        thumbBorderWidth = thumbBorder,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            MoreCardFooterLabel(
                cardWidth = WhereToEatCityTileWidth,
                cardHeight = WhereToEatCityTileHeight,
                fontScale = WhereToEatSeeAllLabelFontScale,
            )
        }
    }
}

@Composable
private fun AnimatedSeeAllThumbnail(
    imageModel: Any,
    modifier: Modifier,
    width: Dp,
    height: Dp,
    cornerShape: RoundedCornerShape,
    borderWidth: Dp,
    slideProgress: Float,
    endRotationDegrees: Float,
    transformOrigin: TransformOrigin = TransformOrigin.Center,
) {
    val p = slideProgress.coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .graphicsLayer {
                this.transformOrigin = transformOrigin
                rotationZ = lerp(0f, endRotationDegrees, p)
                scaleX = lerp(0.82f, 1f, p)
                scaleY = lerp(0.82f, 1f, p)
                alpha = lerp(0.5f, 1f, p).coerceIn(0f, 1f)
            }
            .shadow(
                elevation = 6.dp,
                shape = cornerShape,
                clip = false,
                ambientColor = RestaurantColors.Shadow.cardAmbient,
                spotColor = RestaurantColors.Shadow.cardSpot,
            )
            .clip(cornerShape)
            .border(borderWidth, RestaurantColors.Base.white, cornerShape)
            .background(RestaurantColors.Neutral.imagePlaceholder),
    ) {
        AsyncImage(
            model = imageModel,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun NewsSeeAllCard(
    previewImages: List<Any>,
    onClick: () -> Unit,
) {
    val images = remember(previewImages) {
        when {
            previewImages.size >= 3 -> previewImages.take(3)
            previewImages.size == 2 -> previewImages + previewImages.last()
            previewImages.size == 1 -> List(3) { previewImages.first() }
            else -> List(3) {
                "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=200&h=200&fit=crop"
            }
        }
    }
    val palette = LocalRestaurantPalette.current
    PressableScale(
        onClick = onClick,
        modifier = Modifier
            .width(DiningNewsCardWidth)
            .height(DiningNewsCardTotalHeight)
            .hubSurfaceCard(palette = palette, shape = MoreCardShape),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SeeAllSlideThumbnailStack(
                images = images,
                thumbCornerShape = SeeAllThumbShape,
                thumbBorderWidth = seeAllThumbBorderWidth(
                    DiningNewsCardWidth,
                    DiningNewsCardTotalHeight,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 2.dp),
            )
            MoreCardFooterLabel(
                cardWidth = DiningNewsCardWidth,
                cardHeight = DiningNewsCardTotalHeight,
            )
        }
    }
}

private data class MoreCardFooterMetrics(
    val fontSize: TextUnit,
    val offsetY: Dp,
    val bottomPadding: Dp,
)

/** Scales see-all thumbnail white border with card size (restaurant rail ≈ 3dp). */
private fun seeAllThumbBorderWidth(cardWidth: Dp, cardHeight: Dp): Dp {
    val reference = RestaurantMiniImageHeight.value.coerceAtLeast(1f)
    val scale = (minOf(cardWidth.value, cardHeight.value) / reference).coerceIn(0.55f, 1f)
    return (3f * scale).dp
}

/** Scales “See all” label with [StackedSeeAllCard] size (restaurant rail as reference). */
private fun moreCardFooterMetrics(
    cardWidth: Dp,
    cardHeight: Dp,
    fontScale: Float = 1f,
): MoreCardFooterMetrics {
    val reference = RestaurantMiniImageHeight.value.coerceAtLeast(1f)
    val scale = (minOf(cardWidth.value, cardHeight.value) / reference).coerceIn(0.72f, 1.12f)
    return MoreCardFooterMetrics(
        fontSize = (13f * scale * fontScale).sp,
        offsetY = (-10f * scale).dp,
        bottomPadding = (6f * scale).dp,
    )
}

@Composable
private fun MoreCardFooterLabel(
    cardWidth: Dp,
    cardHeight: Dp,
    fontScale: Float = 1f,
) {
    val palette = LocalRestaurantPalette.current
    val metrics = moreCardFooterMetrics(cardWidth, cardHeight, fontScale = fontScale)
    Text(
        text = "See all",
        color = palette.foreground,
        fontSize = metrics.fontSize,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .offset(y = metrics.offsetY)
            .padding(bottom = metrics.bottomPadding),
    )
}

@Composable
private fun StackedSeeAllCard(
    width: Dp,
    height: Dp,
    previewImages: List<Any>,
    onClick: () -> Unit,
    cardShape: Shape = MoreCardShape,
    thumbCornerShape: RoundedCornerShape = SeeAllThumbShape,
    footerFontScale: Float = 1f,
) {
    val palette = LocalRestaurantPalette.current
    val images = remember(previewImages) {
        when {
            previewImages.size >= 3 -> previewImages.take(3)
            previewImages.size == 2 -> previewImages + previewImages.last()
            previewImages.size == 1 -> List(3) { previewImages.first() }
            else -> List(3) {
                "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=200&h=200&fit=crop"
            }
        }
    }
    PressableScale(
        onClick = onClick,
        modifier = Modifier
            .width(width)
            .height(height)
            .hubSurfaceCard(palette = palette, shape = cardShape),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SeeAllSlideThumbnailStack(
                images = images,
                thumbCornerShape = thumbCornerShape,
                thumbBorderWidth = seeAllThumbBorderWidth(width, height),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 2.dp),
            )
            MoreCardFooterLabel(
                cardWidth = width,
                cardHeight = height,
                fontScale = footerFontScale,
            )
        }
    }
}

@Composable
private fun RestaurantSeeAllCard(
    previewImages: List<Any>,
    onClick: () -> Unit,
) {
    StackedSeeAllCard(
        width = RestaurantMiniCardWidth,
        height = RestaurantMiniImageHeight,
        previewImages = previewImages,
        onClick = onClick,
    )
}

@Composable
private fun FoodTypeSeeAllCard(
    previewImages: List<Any>,
    onClick: () -> Unit,
) {
    DiscoverMenuSeeAllCard(
        previewImages = previewImages,
        onClick = onClick,
    )
}

private fun restaurantsForPriceTab(selected: String): List<Restaurant> =
    DiscoverData.ALL.filter { it.price == selected }.ifEmpty {
        DiscoverData.ALL.take(6).mapIndexed { index, restaurant ->
            restaurant.copy(
                id = "price-${selected.length}-${restaurant.id}",
                price = selected,
                tag = if (index % 2 == 0) "New" else "Sale",
            )
        }
    }

private fun resolveRestaurantNavigationId(typedPrice: String, id: String): String {
    val prefix = "price-${typedPrice.length}-"
    if (!id.startsWith(prefix)) return id
    val tail = id.removePrefix(prefix)
    val candidate = tail.substringAfterLast('-')
    return if (candidate.matches(Regex("[a-z]\\d+"))) candidate else tail
}

private fun discoverListCategoryLabel(restaurant: Restaurant): String {
    val area = restaurant.area?.trim()?.takeIf { it.isNotBlank() }
    if (!area.isNullOrBlank()) return area
    return restaurant.cuisine.split("·").first().trim()
}

private fun cuisineDetailLabel(restaurant: Restaurant): String =
    restaurant.cuisine.split("·").first().trim()

@Composable
private fun PriceSectionLoadingMoreFooter() {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DiscoverLayout.PageHorizontal, vertical = 18.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(22.dp),
            strokeWidth = 2.dp,
            color = palette.foreground.copy(alpha = 0.55f),
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text = "Loading more places…",
            color = palette.mutedForeground,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun RestaurantByPriceListRowWithLazyEnter(
    restaurant: Restaurant,
    onOpenRestaurant: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isPaginated = restaurant.id.contains("-more-")
    val slot = if (isPaginated) {
        restaurant.id.substringAfter("-more-").substringBefore("-").toIntOrNull()?.rem(5) ?: 0
    } else {
        0
    }
    var revealed by remember(restaurant.id) { mutableStateOf(!isPaginated) }
    LaunchedEffect(restaurant.id, isPaginated) {
        if (isPaginated) {
            delay(50L + slot * 55L)
            revealed = true
        }
    }
    val alpha by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(360, easing = FastOutSlowInEasing),
        label = "price-lazy-a",
    )
    val offsetY by animateFloatAsState(
        targetValue = if (revealed) 0f else 22f,
        animationSpec = spring(dampingRatio = 0.84f, stiffness = 360f),
        label = "price-lazy-y",
    )
    val rowModifier = if (isPaginated) {
        modifier.graphicsLayer {
            this.alpha = alpha
            translationY = offsetY
        }
    } else {
        modifier
    }
    RestaurantByPriceListRow(
        restaurant = restaurant,
        onOpenRestaurant = onOpenRestaurant,
        modifier = rowModifier,
    )
}

@Composable
private fun RestaurantsByPriceHeaderAndTabs(
    selectedPrice: String,
    onSelectPrice: (String) -> Unit,
    placesLabel: String,
) {
    val palette = LocalRestaurantPalette.current
    val tabs = listOf("$", "$$", "$$$", "$$$$")
    Column(modifier = Modifier.padding(horizontal = DiscoverLayout.PageHorizontal)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Restaurants by Price",
                color = palette.foreground,
                fontSize = 22.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = placesLabel,
                color = palette.mutedForeground,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
        ) {
            tabs.forEach { tab ->
                val selected = selectedPrice == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            role = Role.Tab,
                            onClick = { onSelectPrice(tab) },
                        )
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = tab,
                            color = if (selected) palette.foreground else palette.mutedForeground,
                            fontSize = 16.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        )
                        Spacer(Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .background(if (selected) palette.foreground else Color.Transparent),
                        )
                    }
                }
            }
        }
        HorizontalDivider(
            thickness = 1.dp,
            color = palette.border,
        )
    }
}

@Composable
private fun RestaurantByPriceListRow(
    restaurant: Restaurant,
    onOpenRestaurant: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val savedIds by WishlistStore.savedRestaurantIds.collectAsState()
    val saved = restaurant.id in savedIds
    val goldStar = RestaurantColors.Semantic.starYellow
    val emptyStar = palette.mutedForeground.copy(alpha = 0.35f)
    val filledStars = (restaurant.rating + 0.25).roundToInt().coerceIn(0, 5)

    PressableScale(
        onClick = onOpenRestaurant,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(PriceListThumbnailWidth, PriceListThumbnailHeight)
                    .clip(RoundedCornerShape(PriceListAvatarCorner))
                    .background(palette.cardSurface),
            ) {
                AsyncImage(
                    model = restaurant.image,
                    contentDescription = restaurant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                val tag = restaurant.tag
                if (!tag.isNullOrBlank()) {
                    com.mh.restaurantchainreservation.core.designsystem.badge.RestaurantCardTagChip(
                        text = tag,
                        fontSize = 10.sp,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(
                                start = PriceListAvatarOverlayPadding,
                                top = PriceListAvatarOverlayPadding,
                            ),
                    )
                }
                HeartButton(
                    active = saved,
                    onClick = { WishlistStore.onHeartTap(restaurant) },
                    size = HeartButtonSize.ExtraSmall,
                    style = HeartButtonStyle.Overlay,
                    overlayContentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(
                            top = PriceListAvatarOverlayPadding,
                            end = PriceListAvatarOverlayPadding,
                        ),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = restaurant.name,
                    color = palette.foreground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        Icons.Outlined.Place,
                        contentDescription = null,
                        tint = palette.mutedForeground,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = discoverListCategoryLabel(restaurant),
                        color = palette.mutedForeground,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    repeat(5) { index ->
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (index < filledStars) goldStar else emptyStar,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = "%.1f".format(restaurant.rating),
                        color = palette.mutedForeground,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "(${formatReviewCount(restaurant.reviews)} reviews)",
                        color = palette.mutedForeground,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 2.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val metaColor = palette.mutedForeground
                    Text(
                        text = cuisineDetailLabel(restaurant),
                        color = metaColor,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    DiscoverInlineDot(color = metaColor)
                    Text(
                        text = restaurant.price,
                        color = metaColor,
                        fontSize = 12.sp,
                        maxLines = 1,
                    )
                    DiscoverInlineDot(color = metaColor)
                    Text(
                        text = restaurant.distance,
                        color = metaColor,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

private fun formatReviewCount(count: Int): String =
    count.toString().reversed().chunked(3).joinToString(",").reversed()

@Composable
private fun SectionHeader(title: String, horizontalPadding: Dp = DiscoverLayout.PageHorizontal) {
    val palette = LocalRestaurantPalette.current
    Text(
        text = title,
        color = palette.foreground,
        fontSize = 22.sp,
        lineHeight = 26.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 0.dp)
            .padding(bottom = DiscoverLayout.SectionHeaderBottom),
    )
}

@Composable
private fun PressableScale(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.965f else 1f,
        animationSpec = spring(dampingRatio = 0.62f, stiffness = 420f),
        label = "press-scale",
    )
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            ),
        content = content,
    )
}

@Composable
private fun RailSpacer(height: Dp) {
    Spacer(Modifier.height(height))
}

@Composable
private fun rememberNewThisWeek(): List<Restaurant> = remember {
    homeRailRestaurants(
        seed = 101,
        tag = "New",
        areaFor = { index ->
            when (index % 4) {
                0 -> "Just opened"
                1 -> "Fresh tables"
                2 -> "Soft launch"
                else -> "New this week"
            }
        },
    )
}

@Composable
private fun rememberLateNight(): List<Restaurant> = remember {
    homeRailRestaurants(
        seed = 102,
        tag = "Late night",
        areaFor = { index ->
            when (index % 4) {
                0 -> "Open until 1 AM"
                1 -> "Open until 2 AM"
                2 -> "Last call tables"
                else -> "Open late"
            }
        },
    )
}

private fun homeRailRestaurants(
    seed: Int,
    tag: String,
    areaFor: (index: Int) -> String,
): List<Restaurant> {
    val areas = listOf("Just opened", "Fresh tables", "Soft launch", "New this week")
    return DiscoverData.ALL
        .shuffled(kotlin.random.Random(seed))
        .take(RestaurantRailVisibleCount)
        .mapIndexed { index, restaurant ->
            restaurant.copy(
                area = areaFor(index).ifBlank { areas[index % areas.size] },
                tag = tag,
            )
        }
}

/** Same WebP assets as `restaurantchain-reservation-ui-demo` / `public/icons/discover`. */
private fun categoryDrawableRes(id: String): Int = when (id) {
    "trending" -> R.drawable.discover_cat_trending
    "catch-only" -> R.drawable.discover_cat_catch_only
    "top-ranking" -> R.drawable.discover_cat_top_ranking
    "hot-ny" -> R.drawable.discover_cat_hot_ny
    "best-kbbq" -> R.drawable.discover_cat_best_kbbq
    "best-american" -> R.drawable.discover_cat_best_american
    "local-fav" -> R.drawable.discover_cat_local_fav
    "nearby-me" -> R.drawable.discover_cat_nearby_me
    else -> R.drawable.discover_cat_trending
}

/** Matches web demo line breaks (`whitespace-pre-line` labels). */
private fun categoryTwoLineLabel(id: String): String = when (id) {
    "trending" -> "Trending\nNow"
    "catch-only" -> "Best\nNoodles"
    "top-ranking" -> "Top\nRanking"
    "hot-ny" -> "Foreign\nFoods"
    "best-kbbq" -> "Best\nBBQ"
    "best-american" -> "Best\nFast Food"
    "local-fav" -> "Local\nFavorite"
    "nearby-me" -> "Nearby\nMe"
    else -> "Explore\nMore"
}
