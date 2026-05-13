package com.mh.restaurantchainreservation.feature.discover.ui

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Search
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
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButtonStyle
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.Banner
import com.mh.restaurantchainreservation.core.model.City
import com.mh.restaurantchainreservation.core.model.DiscoverData
import com.mh.restaurantchainreservation.core.model.FoodType
import com.mh.restaurantchainreservation.core.model.NewsItem
import com.mh.restaurantchainreservation.core.model.QuickCategory
import com.mh.restaurantchainreservation.core.model.Restaurant
import com.mh.restaurantchainreservation.core.model.WishlistStore
import com.mh.restaurantchainreservation.core.model.mockNews
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
                    RestaurantsByPriceSection(onOpenRestaurant = onOpenRestaurant)
                }
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
                onClick = onOpenSearch,
                modifier = Modifier.weight(1f),
            )
            GlassMapButton(compact = false, onClick = onOpenMap)
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(2f)
                .padding(bottom = 58.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            banners.indices.forEach { index ->
                val active = index == pagerState.currentPage
                Box(
                    modifier = Modifier
                        .height(6.dp)
                        .width(if (active) 24.dp else 6.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (active) Color.White else Color.White.copy(alpha = 0.48f)),
                )
            }
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
            .background(palette.cardSurface.copy(alpha = 0.70f))
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        GlassSearchButton(
            title = "Find a restaurant",
            subtitle = if (listState.firstVisibleItemIndex > 0) "Explore nearby tables" else "Today - 2 people",
            compact = true,
            onClick = onOpenSearch,
            modifier = Modifier.weight(1f),
        )
        GlassMapButton(compact = true, onClick = onOpenMap)
    }
}

@Composable
private fun GlassSearchButton(
    title: String,
    subtitle: String,
    compact: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val height = if (compact) 44.dp else 56.dp
    val iconSize = if (compact) 32.dp else 38.dp
    PressableScale(
        onClick = onClick,
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(topStart = 5.dp,
                topEnd = 5.dp, bottomEnd = 5.dp,
                bottomStart = 5.dp))
            .border(1.dp, Color.White.copy(alpha = 0.45f), RoundedCornerShape(999.dp))
//            .background(Color.White.copy(alpha = if (compact) 0.34f else 0.24f))
//            .padding(horizontal = if (compact) 10.dp else 12.dp),
    ) {
        Box(modifier = Modifier.matchParentSize()) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.White.copy(alpha = 0.32f), Color.Transparent),
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
                        .background(Color.White.copy(alpha = 0.42f))
                        .border(1.dp, Color.White.copy(alpha = 0.45f), CircleShape),
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
                        color = palette.foreground.copy(alpha = 0.68f),
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
private fun GlassMapButton(compact: Boolean, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val size = if (compact) 44.dp else 56.dp
    PressableScale(
        onClick = onClick,
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .border(1.dp, Color.White.copy(alpha = 0.45f), CircleShape)
            .background(Color.White.copy(alpha = if (compact) 0.34f else 0.24f)),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.32f), Color.Transparent),
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
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
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
                    style = HeartButtonStyle.Overlay,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    repeat(5) { dot ->
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (dot == 0) Color.White else Color.White.copy(alpha = 0.52f)),
                        )
                    }
                }
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
private fun RestaurantsByPriceSection(onOpenRestaurant: (String) -> Unit) {
    val palette = LocalRestaurantPalette.current
    var selected by rememberSaveable { mutableStateOf("$$") }
    val tabs = listOf(
        PriceTab("$", "Budget-friendly", Color(0xFF16A34A)),
        PriceTab("$$", "Moderate", Color(0xFF2563EB)),
        PriceTab("$$$", "Upscale", Color(0xFF7C3AED)),
        PriceTab("$$$$", "Fine dining", Color(0xFFD97706)),
    )
    val restaurants = remember(selected) {
        DiscoverData.ALL.filter { it.price == selected }.ifEmpty {
            DiscoverData.ALL.take(6).mapIndexed { index, restaurant ->
                restaurant.copy(
                    id = "price-${selected.length}-${restaurant.id}",
                    price = selected,
                    tag = if (index % 2 == 0) "New" else "Sale",
                )
            }
        }
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader(title = "Restaurants by Price", horizontalPadding = 0.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            tabs.forEach { tab ->
                PriceTabButton(
                    tab = tab,
                    selected = selected == tab.label,
                    onClick = { selected = tab.label },
                )
            }
        }
        Column(
            modifier = Modifier.padding(top = 18.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            restaurants.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    row.forEach { restaurant ->
                        AirbnbMiniCard(
                            restaurant = restaurant,
                            width = 0.dp,
                            onClick = { onOpenRestaurant(restaurant.id.removePrefix("price-${selected.length}-")) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (row.size == 1) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
        Text(
            text = "${restaurants.size}+ places",
            color = palette.mutedForeground,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 18.dp),
        )
    }
}

@Composable
private fun PriceTabButton(tab: PriceTab, selected: Boolean, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    PressableScale(
        onClick = onClick,
        modifier = Modifier
            .height(50.dp)
            .clip(RoundedCornerShape(999.dp))
            .border(1.dp, if (selected) tab.color.copy(alpha = 0.42f) else palette.border, RoundedCornerShape(999.dp))
            .background(if (selected) tab.color.copy(alpha = 0.14f) else palette.cardSurface)
            .padding(horizontal = 14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(tab.label, color = if (selected) tab.color else palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            Column {
                Text(tab.description, color = palette.foreground, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(if (selected) "Selected" else "Tap to view", color = palette.mutedForeground, fontSize = 10.sp, maxLines = 1)
            }
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

private data class PriceTab(val label: String, val description: String, val color: Color)

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