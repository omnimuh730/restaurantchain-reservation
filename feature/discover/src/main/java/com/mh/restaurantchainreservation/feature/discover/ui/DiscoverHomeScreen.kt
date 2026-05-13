@file:OptIn(ExperimentalFoundationApi::class)

package com.mh.restaurantchainreservation.feature.discover.ui

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButton
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButtonSize
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantPalette
import com.mh.restaurantchainreservation.core.model.Banner
import com.mh.restaurantchainreservation.core.model.City
import com.mh.restaurantchainreservation.core.model.DiscoverData
import com.mh.restaurantchainreservation.core.model.FoodType
import com.mh.restaurantchainreservation.core.model.NewsItem
import com.mh.restaurantchainreservation.core.model.QuickCategory
import com.mh.restaurantchainreservation.core.model.Restaurant
import com.mh.restaurantchainreservation.core.model.WishlistStore
import com.mh.restaurantchainreservation.core.model.mockNews
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun DiscoverHomeScreen(
    onOpenSearch: () -> Unit,
    onOpenRestaurant: (String) -> Unit,
    onOpenCategory: (String) -> Unit,
    onOpenFood: (String) -> Unit,
    onOpenLocation: (String) -> Unit,
    onOpenSection: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val listState = rememberLazyListState()
    val news = remember { mockNews() }
    var priceTabSelected by rememberSaveable { mutableStateOf("$$$") }
    val priceTabLabels = remember { listOf("$", "$$", "$$$", "$$$$") }
    val restaurantsByPrice = remember(priceTabSelected) {
        DiscoverData.ALL.filter { it.price == priceTabSelected }.ifEmpty {
            DiscoverData.ALL.take(6).mapIndexed { index, restaurant ->
                restaurant.copy(
                    id = "price-${priceTabSelected.length}-${restaurant.id}",
                    price = priceTabSelected,
                    tag = if (index % 2 == 0) "New" else "Sale",
                )
            }
        }
    }
    val compact by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 250
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.cardSurface),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp),
        ) {
            item {
                HeroBanner(
                    banners = DiscoverData.BANNERS,
                    onOpenSearch = onOpenSearch,
                    onOpenMap = onOpenSearch,
                    onViewAll = { onOpenSection("banners") },
                    onBannerClick = { onOpenSection(it) },
                )
            }
            item {
                Column(
                    modifier = Modifier
                        .offset(y = (-40).dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .background(palette.cardSurface)
                        .padding(top = 18.dp, bottom = 10.dp),
                ) {
                    QuickCategoryGrid(DiscoverData.QUICK_CATEGORIES, onOpenCategory)
                    RailSpacer(28.dp)
                    ImageRailSection(
                        title = "Where to Eat?",
                        morePreset = MorePreset.CompactWide,
                        onMore = { onOpenSection("where-to-eat") },
                    ) {
                        CityRail(cities = DiscoverData.CITIES, onClick = onOpenLocation)
                    }
                    RailSpacer(28.dp)
                    ImageRailSection(
                        title = "Top Picks by Food Type",
                        morePreset = MorePreset.CompactNarrow,
                        onMore = { onOpenSection("top-picks-food") },
                    ) {
                        FoodRail(foodTypes = DiscoverData.FOOD_TYPES, onClick = onOpenFood)
                    }
                    RailSpacer(28.dp)
                    RestaurantRail(
                        title = "Monthly Best",
                        restaurants = DiscoverData.MONTHLY_BEST,
                        onOpenRestaurant = onOpenRestaurant,
                        onMore = { onOpenSection("monthly-best") },
                    )
                    RailSpacer(28.dp)
                    RestaurantRail(
                        title = "Loved by Locals",
                        restaurants = DiscoverData.LOVED_BY_LOCALS,
                        onOpenRestaurant = onOpenRestaurant,
                        onMore = { onOpenSection("loved-by-locals") },
                    )
                    RailSpacer(28.dp)
                    NewsRail(news = news, onMore = { onOpenSection("news") })
                    RailSpacer(28.dp)
                    RestaurantRail(
                        title = "New This Week",
                        restaurants = rememberNewThisWeek(),
                        onOpenRestaurant = onOpenRestaurant,
                        onMore = { onOpenSection("new-this-week") },
                    )
                    RailSpacer(28.dp)
                    RestaurantRail(
                        title = "Late Night Finds",
                        restaurants = rememberLateNight(),
                        onOpenRestaurant = onOpenRestaurant,
                        onMore = { onOpenSection("late-night") },
                    )
                    RailSpacer(30.dp)
                }
            }
            stickyHeader {
                RestaurantsByPriceStickyHeader(
                    selectedPrice = priceTabSelected,
                    onSelectPrice = { priceTabSelected = it },
                    placeCount = restaurantsByPrice.size,
                    tabLabels = priceTabLabels,
                )
            }
            item {
                Spacer(Modifier.height(14.dp))
            }
            items(
                items = restaurantsByPrice,
                key = { it.id },
            ) { restaurant ->
                val openId = restaurant.id.removePrefix("price-${priceTabSelected.length}-")
                RestaurantByPriceListRow(
                    restaurant = restaurant,
                    onClick = { onOpenRestaurant(openId) },
                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
                )
            }
        }

        CompactDiscoverBar(
            visible = compact,
            listState = listState,
            onOpenSearch = onOpenSearch,
            onOpenMap = onOpenSearch,
        )
    }
}

@Composable
private fun HeroBanner(
    banners: List<Banner>,
    onOpenSearch: () -> Unit,
    onOpenMap: () -> Unit,
    onViewAll: () -> Unit,
    onBannerClick: (String) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val pagerState = rememberPagerState(pageCount = { banners.size })

    LaunchedEffect(pagerState, banners.size) {
        while (banners.isNotEmpty()) {
            delay(3000L)
            pagerState.animateScrollToPage((pagerState.currentPage + 1) % banners.size)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            val banner = banners[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onBannerClick(banner.id) },
            ) {
                AsyncImage(
                    model = banner.image,
                    contentDescription = banner.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.05f),
                                    Color.Black.copy(alpha = 0.16f),
                                    Color.Black.copy(alpha = 0.72f),
                                ),
                            ),
                        ),
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, end = 20.dp, bottom = 88.dp),
                ) {
                    Text(
                        text = banner.title,
                        color = Color.White,
                        fontSize = 25.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 2,
                    )
                    Text(
                        text = banner.subtitle,
                        color = Color.White.copy(alpha = 0.92f),
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp),
                        maxLines = 2,
                    )
                    if (banner.cta.isNotBlank()) {
                        Text(
                            text = banner.cta.uppercase(),
                            color = Color.White.copy(alpha = 0.68f),
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
                .padding(start = 16.dp, top = 18.dp, end = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            GlassSearchButton(
                title = "Find a restaurant",
                subtitle = "Today - 2 people - Anywhere",
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
                .background(Color.White.copy(alpha = 0.92f))
                .padding(horizontal = 10.dp, vertical = 6.dp),
        ) {
            Text("View All", color = Color(0xFF222222), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(76.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, palette.cardSurface),
                    ),
                ),
        )
    }
}

@Composable
private fun CompactDiscoverBar(
    visible: Boolean,
    listState: LazyListState,
    onOpenSearch: () -> Unit,
    onOpenMap: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(360, easing = FastOutSlowInEasing),
        label = "discover-compact-alpha",
    )
    val y by animateFloatAsState(
        targetValue = if (visible) 0f else -22f,
        animationSpec = tween(420, easing = FastOutSlowInEasing),
        label = "discover-compact-y",
    )
    if (alpha <= 0.01f && !visible) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.alpha = alpha
                translationY = y
            }
            .background(discoverGlassBarBackgroundBrush(palette))
            .border(width = 1.dp, color = discoverGlassBarEdgeColor(palette))
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        GlassSearchButton(
            title = "Find a restaurant",
            subtitle = if (listState.firstVisibleItemIndex > 0) "Explore nearby tables" else "Today - 2 people",
            compact = true,
            opaqueGlass = true,
            palette = palette,
            onClick = onOpenSearch,
            modifier = Modifier.weight(1f),
        )
        GlassMapButton(compact = true, opaqueGlass = true, palette = palette, onClick = onOpenMap)
    }
}

private fun discoverGlassBarBackgroundBrush(palette: RestaurantPalette): Brush =
    if (palette.isDark) {
        Brush.verticalGradient(
            colors = listOf(
                palette.cardSurface.copy(alpha = 0.96f),
                Color(0xFF2C2C2E).copy(alpha = 0.93f),
            ),
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.94f),
                palette.cardSurface.copy(alpha = 0.90f),
            ),
        )
    }

private fun discoverGlassBarEdgeColor(palette: RestaurantPalette): Color =
    if (palette.isDark) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.58f)

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
    if (palette.isDark) {
        val strong = opaqueGlass && compact
        val base = when {
            strong -> palette.cardSurface.copy(alpha = 0.90f)
            compact -> palette.cardSurface.copy(alpha = 0.62f)
            else -> palette.cardSurface.copy(alpha = 0.48f)
        }
        val border = when {
            strong -> 0.30f
            compact -> 0.24f
            else -> 0.22f
        }
        val icon = Color.White.copy(alpha = if (strong) 0.20f else 0.14f)
        val grad = when {
            strong -> 0.22f
            compact -> 0.18f
            else -> 0.15f
        }
        return GlassPillLayers(base, border, icon, grad)
    }
    val strong = opaqueGlass && compact
    val baseAlpha = when {
        strong -> 0.58f
        compact -> 0.42f
        else -> 0.32f
    }
    val borderAlpha = when {
        strong -> 0.62f
        compact -> 0.52f
        else -> 0.48f
    }
    val iconAlpha = when {
        strong -> 0.52f
        compact -> 0.44f
        else -> 0.40f
    }
    val gradTop = when {
        strong -> 0.40f
        compact -> 0.34f
        else -> 0.30f
    }
    return GlassPillLayers(
        baseFill = Color.White.copy(alpha = baseAlpha),
        borderAlpha = borderAlpha,
        iconPlateFill = Color.White.copy(alpha = iconAlpha),
        gradientTopAlpha = gradTop,
    )
}

private fun discoverGlassPillBorderColor(palette: RestaurantPalette, borderAlpha: Float): Color =
    if (palette.isDark) Color.White.copy(alpha = (borderAlpha * 0.42f + 0.10f).coerceIn(0.12f, 0.36f))
    else Color.White.copy(alpha = borderAlpha.coerceIn(0f, 1f))

@Composable
private fun GlassSearchButton(
    title: String,
    subtitle: String,
    compact: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    opaqueGlass: Boolean = false,
    palette: RestaurantPalette = LocalRestaurantPalette.current,
) {
    val height = if (compact) 44.dp else 56.dp
    val iconSize = if (compact) 32.dp else 38.dp
    val layers = discoverGlassPillLayers(palette, compact, opaqueGlass)
    PressableScale(
        onClick = onClick,
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(999.dp))
            .border(1.dp, discoverGlassPillBorderColor(palette, layers.borderAlpha), RoundedCornerShape(999.dp))
            .background(layers.baseFill)
            .padding(horizontal = if (compact) 10.dp else 12.dp),
    ) {
        Box(modifier = Modifier.matchParentSize()) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = layers.gradientTopAlpha),
                                Color.Transparent,
                            ),
                        ),
                    ),
            )
            Row(
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = if (compact) 0.dp else 2.dp),
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
    palette: RestaurantPalette = LocalRestaurantPalette.current,
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
                            Color.White.copy(alpha = layers.gradientTopAlpha),
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
            .padding(horizontal = 18.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        categories.chunked(4).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                row.forEach { category ->
                    QuickCategoryButton(
                        category = category,
                        onClick = { onClick(category.id) },
                        modifier = Modifier.weight(1f),
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
    PressableScale(onClick = onClick, modifier = modifier.padding(horizontal = 2.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(categoryTint(category.id).copy(alpha = 0.13f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = categoryIcon(category.id),
                    contentDescription = null,
                    tint = categoryTint(category.id),
                    modifier = Modifier.size(28.dp),
                )
            }
            Text(
                text = compactCategoryLabel(category.label),
                color = palette.foreground,
                fontSize = 13.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 7.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ImageRailSection(
    title: String,
    morePreset: MorePreset,
    onMore: () -> Unit,
    content: @Composable () -> Unit,
) {
    SectionHeader(title = title)
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { content() }
        item {
            MorePlaycardButton(
                preset = morePreset,
                label = "More",
                onClick = onMore,
            )
        }
    }
}

@Composable
private fun CityRail(cities: List<City>, onClick: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        cities.forEach { city ->
            ImageRailTile(
                title = city.label,
                image = city.image,
                width = 128.dp,
                centered = true,
                onClick = { onClick(city.id) },
            )
        }
    }
}

@Composable
private fun FoodRail(foodTypes: List<FoodType>, onClick: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        foodTypes.forEach { food ->
            ImageRailTile(
                title = food.label,
                image = food.image,
                width = 112.dp,
                centered = false,
                onClick = { onClick(food.id) },
            )
        }
    }
}

@Composable
private fun ImageRailTile(
    title: String,
    image: String,
    width: Dp,
    centered: Boolean,
    onClick: () -> Unit,
) {
    PressableScale(
        onClick = onClick,
        modifier = Modifier
            .size(width = width, height = 80.dp)
            .clip(RoundedCornerShape(13.dp)),
    ) {
        Box(modifier = Modifier.matchParentSize()) {
            AsyncImage(
                model = image,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .then(
                        if (centered) {
                            Modifier.background(Color.Black.copy(alpha = 0.36f))
                        } else {
                            Modifier.background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.64f))))
                        },
                    ),
            )
            Text(
                text = title,
                color = Color.White,
                fontSize = if (centered) 13.sp else 12.sp,
                fontWeight = if (centered) FontWeight.Bold else FontWeight.SemiBold,
                modifier = if (centered) {
                    Modifier.align(Alignment.Center)
                } else {
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                },
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
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
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        restaurants.forEach { restaurant ->
            item(key = restaurant.id) {
                AirbnbMiniCard(
                    restaurant = restaurant,
                    width = 176.dp,
                    onClick = { onOpenRestaurant(restaurant.id) },
                )
            }
        }
        item {
            MorePlaycardButton(
                preset = MorePreset.Restaurant,
                label = "More",
                onClick = onMore,
            )
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
    val collections by WishlistStore.collections.collectAsState()
    val saved = collections.any { collection -> collection.restaurants.any { it.id == restaurant.id } }
    val widthModifier = if (width > 0.dp) modifier.width(width) else modifier
    PressableScale(
        onClick = onClick,
        modifier = widthModifier,
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(palette.mutedSurface),
            ) {
                AsyncImage(
                    model = restaurant.image,
                    contentDescription = restaurant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                val tag = restaurant.tag
                if (!tag.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color.White.copy(alpha = 0.94f))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                    ) {
                        Text(tag, color = Color(0xFF222222), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                    }
                }
                HeartButton(
                    active = saved,
                    onClick = { WishlistStore.openPicker(restaurant) },
                    size = HeartButtonSize.Medium,
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
                modifier = Modifier.padding(top = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = restaurant.area ?: restaurant.cuisine,
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = palette.foreground, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(3.dp))
                    Text("%.1f".format(restaurant.rating), color = palette.foreground, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun NewsRail(news: List<NewsItem>, onMore: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    SectionHeader(title = "Dining News")
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        news.forEachIndexed { index, newsItem ->
            item(key = newsItem.id) {
                PressableScale(
                    onClick = { },
                    modifier = Modifier
                        .width(if (index == 0) 288.dp else 256.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, palette.border, RoundedCornerShape(20.dp))
                        .background(palette.cardSurface),
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(if (index == 0) 160.dp else 144.dp),
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
                                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.62f)))),
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(palette.brand)
                                    .padding(horizontal = 9.dp, vertical = 4.dp),
                            ) {
                                Text(newsItem.category, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = "${newsItem.readMinutes} min read",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp),
                            )
                        }
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(newsItem.title, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Text(newsItem.summary, color = palette.mutedForeground, fontSize = 12.sp, lineHeight = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }
        }
        item {
            MorePlaycardButton(MorePreset.News, label = "More", onClick = onMore)
        }
    }
}

@Composable
private fun RestaurantsByPriceStickyHeader(
    selectedPrice: String,
    onSelectPrice: (String) -> Unit,
    placeCount: Int,
    tabLabels: List<String>,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.cardSurface),
    ) {
        HorizontalDivider(color = palette.border.copy(alpha = 0.65f))
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Restaurants by Price",
                    color = palette.foreground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "$placeCount+ places",
                    color = palette.mutedForeground,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                tabLabels.forEach { label ->
                    PriceUnderlineTab(
                        label = label,
                        selected = selectedPrice == label,
                        onClick = { onSelectPrice(label) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        HorizontalDivider(color = palette.border.copy(alpha = 0.65f), modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun PriceUnderlineTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    PressableScale(onClick = onClick, modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = label,
                color = if (selected) palette.foreground else palette.mutedForeground,
                fontSize = 17.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (selected) palette.foreground else Color.Transparent),
            )
        }
    }
}

@Composable
private fun RestaurantByPriceListRow(
    restaurant: Restaurant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val collections by WishlistStore.collections.collectAsState()
    val saved = collections.any { collection -> collection.restaurants.any { it.id == restaurant.id } }
    val categoryLine = restaurant.area ?: restaurant.cuisine
    val reviewLabel = remember(restaurant.id, restaurant.reviews) {
        "(${NumberFormat.getNumberInstance(Locale.US).format(restaurant.reviews)} reviews)"
    }
    val summaryLine = "${restaurant.cuisine} · ${restaurant.price} · ${restaurant.distance}"
    val starGold = Color(0xFFFFC107)
    PressableScale(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(palette.mutedSurface),
            ) {
                AsyncImage(
                    model = restaurant.image,
                    contentDescription = restaurant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                val tag = restaurant.tag
                if (!tag.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color.White.copy(alpha = 0.94f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Text(tag, color = Color(0xFF222222), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                    }
                }
                HeartButton(
                    active = saved,
                    onClick = { WishlistStore.openPicker(restaurant) },
                    size = HeartButtonSize.Medium,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = restaurant.name,
                    color = palette.foreground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    modifier = Modifier.padding(top = 3.dp),
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
                        text = categoryLine,
                        color = palette.mutedForeground,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RatingStarRow(rating = restaurant.rating, starGold = starGold)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "%.1f".format(restaurant.rating),
                        color = palette.foreground,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = reviewLabel,
                        color = palette.mutedForeground,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    text = summaryLine,
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun RatingStarRow(rating: Double, starGold: Color) {
    val palette = LocalRestaurantPalette.current
    val full = kotlin.math.floor(rating).toInt().coerceIn(0, 5)
    val remainder = rating - full
    val emptyTint = palette.mutedForeground.copy(alpha = 0.38f)
    Row(
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(5) { index ->
            val tint = when {
                index < full -> starGold
                index == full && remainder >= 0.45 -> starGold.copy(alpha = 0.58f)
                else -> emptyTint
            }
            Icon(Icons.Filled.Star, contentDescription = null, tint = tint, modifier = Modifier.size(12.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String, horizontalPadding: Dp = 16.dp) {
    val palette = LocalRestaurantPalette.current
    Text(
        text = title,
        color = palette.foreground,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 0.dp)
            .padding(bottom = 12.dp),
    )
}

@Composable
private fun MorePlaycardButton(
    preset: MorePreset,
    label: String,
    onClick: () -> Unit,
) {
    val dimensions = moreDimensions(preset)
    var entered by remember { mutableStateOf(false) }
    val fanProgress by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(1550, delayMillis = 80, easing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)),
        label = "more-fan",
    )
    val labelAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(450, delayMillis = 700, easing = FastOutSlowInEasing),
        label = "more-label",
    )
    LaunchedEffect(Unit) { entered = true }

    PressableScale(
        onClick = onClick,
        modifier = Modifier
            .size(width = dimensions.outerWidth, height = dimensions.outerHeight)
            .clip(RoundedCornerShape(dimensions.radius))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFAFAFC), Color(0xFFECEEF2)),
                ),
            )
            .border(1.dp, Color.Black.copy(alpha = 0.08f), RoundedCornerShape(dimensions.radius))
            .padding(horizontal = 10.dp, vertical = 12.dp),
    ) {
        Column(
            modifier = Modifier.matchParentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier.size(width = dimensions.fanWidth, height = dimensions.fanHeight),
                contentAlignment = Alignment.BottomCenter,
            ) {
                val layers = listOf(
                    FanLayer(-15f, -dimensions.spread, 0.dp),
                    FanLayer(15f, dimensions.spread, 0.dp),
                    FanLayer(0f, 0.dp, -dimensions.lift),
                )
                layers.forEachIndexed { index, layer ->
                    FannedMiniCard(
                        size = dimensions.card,
                        glyphSize = dimensions.glyph,
                        progress = fanProgress,
                        layer = layer,
                        z = index + 1f,
                    )
                }
            }
            Spacer(Modifier.height(dimensions.labelGap))
            Text(
                text = label,
                color = Color(0xFF222222),
                fontSize = dimensions.labelSize,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.graphicsLayer { alpha = labelAlpha },
            )
        }
    }
}

@Composable
private fun FannedMiniCard(size: Dp, glyphSize: Dp, progress: Float, layer: FanLayer, z: Float) {
    Box(
        modifier = Modifier
            .size(size)
            .offset(x = layer.x * progress, y = (12.dp * (1f - progress)) + (layer.y * progress))
            .zIndex(z)
            .graphicsLayer {
                rotationZ = layer.rotation * progress
                scaleX = 0.55f + 0.45f * progress
                scaleY = 0.55f + 0.45f * progress
                alpha = progress
                shadowElevation = 7f
                this.cameraDistance = 14f
            }
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color.Black.copy(alpha = 0.06f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(glyphSize)
                .clip(RoundedCornerShape(6.dp))
                .border(2.dp, Color(0xFF9CA3AF), RoundedCornerShape(6.dp))
                .padding(4.dp),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(0.70f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0xFF9CA3AF).copy(alpha = 0.75f)),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF9CA3AF).copy(alpha = 0.75f)),
            )
        }
    }
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
    listOf(
        (DiscoverData.findById("m4") ?: DiscoverData.MONTHLY_BEST[3]).copy(area = "Just opened", tag = "New"),
        (DiscoverData.findById("l3") ?: DiscoverData.LOVED_BY_LOCALS.last()).copy(area = "Fresh tables", tag = "New"),
        (DiscoverData.findById("d1") ?: DiscoverData.DATE_NIGHT.first()).copy(area = "Soft launch", tag = "New"),
    )
}

@Composable
private fun rememberLateNight(): List<Restaurant> = remember {
    listOf(
        (DiscoverData.findById("l3") ?: DiscoverData.LOVED_BY_LOCALS.last()).copy(area = "Open until 1 AM", tag = "Late night"),
        (DiscoverData.findById("v3") ?: DiscoverData.VIRAL.last()).copy(area = "Open until 2 AM", tag = "Late night"),
        (DiscoverData.findById("v1") ?: DiscoverData.VIRAL.first()).copy(area = "Last call tables", tag = "Late night"),
    )
}

private data class FanLayer(val rotation: Float, val x: Dp, val y: Dp)

private enum class MorePreset { CompactWide, CompactNarrow, Restaurant, News }

private data class MoreDimensions(
    val outerWidth: Dp,
    val outerHeight: Dp,
    val radius: Dp,
    val card: Dp,
    val glyph: Dp,
    val fanWidth: Dp,
    val fanHeight: Dp,
    val spread: Dp,
    val lift: Dp,
    val labelGap: Dp,
    val labelSize: androidx.compose.ui.unit.TextUnit,
)

private fun moreDimensions(preset: MorePreset): MoreDimensions = when (preset) {
    MorePreset.CompactWide -> MoreDimensions(128.dp, 80.dp, 13.dp, 30.dp, 14.dp, 64.dp, 44.dp, 10.dp, 3.dp, 2.dp, 13.sp)
    MorePreset.CompactNarrow -> MoreDimensions(112.dp, 80.dp, 13.dp, 30.dp, 14.dp, 64.dp, 44.dp, 10.dp, 3.dp, 2.dp, 12.sp)
    MorePreset.Restaurant -> MoreDimensions(176.dp, 224.dp, 18.dp, 60.dp, 26.dp, 130.dp, 86.dp, 22.dp, 8.dp, 12.dp, 15.sp)
    MorePreset.News -> MoreDimensions(256.dp, 264.dp, 20.dp, 76.dp, 32.dp, 162.dp, 104.dp, 28.dp, 10.dp, 16.dp, 17.sp)
}

private fun compactCategoryLabel(label: String): String = when (label) {
    "Trending Now" -> "Trending\nNow"
    "Top Ranking" -> "Top\nRanking"
    "Hot in New York" -> "Hot in\nNew York"
    "Best K-BBQ" -> "Best\nK-BBQ"
    "Best American" -> "Best\nAmerican"
    "Local Favorite" -> "Local\nFavorite"
    "Nearby Me" -> "Nearby\nMe"
    else -> label
}

private fun categoryIcon(id: String): ImageVector = when (id) {
    "nearby-me", "hot-ny" -> Icons.Outlined.Place
    "local-fav", "catch-only" -> Icons.Outlined.FavoriteBorder
    "best-kbbq", "best-american" -> Icons.Outlined.Restaurant
    else -> Icons.Filled.Star
}

private fun categoryTint(id: String): Color = when (id) {
    "trending" -> Color(0xFFFF385C)
    "catch-only" -> Color(0xFFDB2777)
    "top-ranking" -> Color(0xFFEAB308)
    "hot-ny" -> Color(0xFF2563EB)
    "best-kbbq" -> Color(0xFFEA580C)
    "best-american" -> Color(0xFF059669)
    "local-fav" -> Color(0xFFE11D48)
    "nearby-me" -> Color(0xFF0891B2)
    else -> Color(0xFFFF385C)
}
