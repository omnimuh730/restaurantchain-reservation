package com.mh.restaurantchainreservation.feature.discover.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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

/**
 * Discover home — top-level "browse" surface. Mirrors React `DiscoverHome.tsx`
 * with: hero search bar, banner pager (auto-advance), food types row,
 * quick categories pills, cities row, sectioned restaurant lists, and a news
 * feed at the bottom.
 */
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
    val news = remember { mockNews() }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item { HeroHeader(onOpenSearch = onOpenSearch) }
        item { Spacer(Modifier.height(16.dp)) }
        item { BannerPager(banners = DiscoverData.BANNERS) }
        item { Spacer(Modifier.height(20.dp)) }
        item {
            FoodTypesRow(
                foodTypes = DiscoverData.FOOD_TYPES,
                onClick = onOpenFood,
            )
        }
        item { Spacer(Modifier.height(20.dp)) }
        item {
            QuickCategoryPills(
                categories = DiscoverData.QUICK_CATEGORIES,
                onClick = onOpenCategory,
            )
        }
        item { Spacer(Modifier.height(20.dp)) }
        item {
            CitiesRow(
                cities = DiscoverData.CITIES,
                onClick = onOpenLocation,
            )
        }
        item { Spacer(Modifier.height(24.dp)) }
        item {
            SectionHeader(
                title = "Loved by Locals",
                onSeeAll = { onOpenSection("loved-by-locals") },
            )
        }
        item {
            HorizontalRestaurants(
                items = DiscoverData.LOVED_BY_LOCALS,
                onClick = onOpenRestaurant,
            )
        }
        item { Spacer(Modifier.height(24.dp)) }
        item {
            SectionHeader(
                title = "Trending Now",
                onSeeAll = { onOpenSection("viral") },
            )
        }
        item {
            HorizontalRestaurants(
                items = DiscoverData.VIRAL,
                onClick = onOpenRestaurant,
            )
        }
        item { Spacer(Modifier.height(24.dp)) }
        item {
            SectionHeader(
                title = "Date Night Picks",
                onSeeAll = { onOpenSection("date-night") },
            )
        }
        item {
            HorizontalRestaurants(
                items = DiscoverData.DATE_NIGHT,
                onClick = onOpenRestaurant,
            )
        }
        item { Spacer(Modifier.height(24.dp)) }
        item {
            SectionHeader(
                title = "Monthly Best",
                onSeeAll = { onOpenSection("monthly-best") },
            )
        }
        item {
            MonthlyBestGrid(
                items = DiscoverData.MONTHLY_BEST,
                onClick = onOpenRestaurant,
            )
        }
        item { Spacer(Modifier.height(28.dp)) }
        item { SectionHeader(title = "From the Editors", onSeeAll = null) }
        items(news, key = { it.id }) { entry ->
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                NewsCard(item = entry)
            }
        }
    }
}

/* ── Header ─────────────────────────────────────── */

@Composable
private fun HeroHeader(onOpenSearch: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.cardSurface)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(
            text = "Discover",
            color = palette.foreground,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = "Find your next great meal",
            color = palette.mutedForeground,
            fontSize = 13.sp,
        )
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(999.dp))
                .background(palette.mutedSurface)
                .clickable(onClick = onOpenSearch)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = palette.mutedForeground,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.size(10.dp))
            Text(
                text = "Search restaurants, foods, locations",
                color = palette.mutedForeground,
                fontSize = 14.sp,
            )
        }
    }
}

/* ── Banner pager ────────────────────────────────── */

@Composable
private fun BannerPager(banners: List<Banner>) {
    val palette = LocalRestaurantPalette.current
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { banners.size })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(5000L)
            val next = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(next)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp),
            pageSpacing = 12.dp,
        ) { page ->
            val banner = banners[page]
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f)
                    .clip(RoundedCornerShape(20.dp)),
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
                            brush = Brush.verticalGradient(
                                listOf(Color.Black.copy(alpha = 0.0f), Color.Black.copy(alpha = 0.65f)),
                            ),
                        ),
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                ) {
                    Text(
                        text = banner.subtitle,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = banner.title,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    if (banner.cta.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = banner.cta,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            banners.indices.forEach { idx ->
                val active = idx == pagerState.currentPage
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(width = if (active) 18.dp else 6.dp, height = 6.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (active) palette.brand else palette.borderSoft),
                )
            }
        }
    }
}

/* ── Food types row ─────────────────────────────── */

@Composable
private fun FoodTypesRow(
    foodTypes: List<FoodType>,
    onClick: (String) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column {
        SectionHeader(title = "Browse by Food", onSeeAll = null)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(foodTypes, key = { it.id }) { food ->
                Column(
                    modifier = Modifier
                        .clickable { onClick(food.id) }
                        .size(width = 76.dp, height = 110.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(palette.mutedSurface),
                    ) {
                        AsyncImage(
                            model = food.image,
                            contentDescription = food.label,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = food.label,
                        color = palette.foreground,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

/* ── Quick categories ───────────────────────────── */

@Composable
private fun QuickCategoryPills(
    categories: List<QuickCategory>,
    onClick: (String) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column {
        SectionHeader(title = "Quick Categories", onSeeAll = null)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(categories, key = { it.id }) { category ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(palette.brandSoftSurface)
                        .clickable { onClick(category.id) }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = category.label.replace("\n", " "),
                        color = palette.brand,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

/* ── Cities row ─────────────────────────────────── */

@Composable
private fun CitiesRow(
    cities: List<City>,
    onClick: (String) -> Unit,
) {
    Column {
        SectionHeader(title = "Where to Eat?", onSeeAll = null)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(cities, key = { it.id }) { city ->
                Box(
                    modifier = Modifier
                        .size(width = 168.dp, height = 96.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onClick(city.id) },
                ) {
                    AsyncImage(
                        model = city.image,
                        contentDescription = city.label,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(Color.Black.copy(alpha = 0.0f), Color.Black.copy(alpha = 0.55f)),
                                ),
                            ),
                    )
                    Text(
                        text = city.label,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

/* ── Section header ─────────────────────────────── */

@Composable
private fun SectionHeader(
    title: String,
    onSeeAll: (() -> Unit)?,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = palette.foreground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        if (onSeeAll != null) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .clickable(onClick = onSeeAll)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "View All",
                    color = palette.brand,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.size(2.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = palette.brand,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}

/* ── Horizontal restaurants ─────────────────────── */

@Composable
private fun HorizontalRestaurants(
    items: List<Restaurant>,
    onClick: (String) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items, key = { it.id }) { item ->
            Column(
                modifier = Modifier
                    .size(width = 220.dp, height = 220.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(palette.cardSurface)
                    .border(1.dp, palette.borderSoft, RoundedCornerShape(18.dp))
                    .clickable { onClick(item.id) },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                ) {
                    AsyncImage(
                        model = item.image,
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                    val tag = item.tag
                    if (!tag.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(palette.cardSurface.copy(alpha = 0.92f))
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                        ) {
                            Text(
                                text = tag,
                                color = palette.foreground,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                    SaveHeartOverlay(
                        restaurant = item,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                    )
                }
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = item.name,
                        color = palette.foreground,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = item.cuisine,
                        color = palette.mutedForeground,
                        fontSize = 12.sp,
                        maxLines = 1,
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = palette.brand,
                            modifier = Modifier.size(13.dp),
                        )
                        Spacer(Modifier.size(3.dp))
                        Text(
                            text = "%.1f · ${item.distance}".format(item.rating),
                            color = palette.foreground,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}

/* ── Monthly Best — 2x2 grid ─────────────────────── */

@Composable
private fun MonthlyBestGrid(
    items: List<Restaurant>,
    onClick: (String) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                row.forEach { item ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(palette.cardSurface)
                            .border(1.dp, palette.borderSoft, RoundedCornerShape(16.dp))
                            .clickable { onClick(item.id) },
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(4f / 3f),
                        ) {
                            AsyncImage(
                                model = item.image,
                                contentDescription = item.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                            SaveHeartOverlay(
                                restaurant = item,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp),
                            )
                        }
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = item.name,
                                color = palette.foreground,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                            )
                            Text(
                                text = item.area ?: item.cuisine,
                                color = palette.mutedForeground,
                                fontSize = 11.sp,
                                maxLines = 1,
                            )
                        }
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

/* ── News card ───────────────────────────────────── */

@Composable
private fun NewsCard(item: NewsItem) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.cardSurface)
            .border(1.dp, palette.borderSoft, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(86.dp)
                .clip(RoundedCornerShape(12.dp)),
        ) {
            AsyncImage(
                model = item.image,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Text(
                text = item.category,
                color = palette.brand,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = item.title,
                color = palette.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = item.summary,
                color = palette.mutedForeground,
                fontSize = 11.sp,
                maxLines = 2,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${item.readMinutes} min read",
                color = palette.mutedForeground.copy(alpha = 0.8f),
                fontSize = 10.sp,
            )
        }
    }
}

@Composable
private fun SaveHeartOverlay(
    restaurant: Restaurant,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val collections by WishlistStore.collections.collectAsState()
    val saved = collections.any { col -> col.restaurants.any { it.id == restaurant.id } }
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable { WishlistStore.openPicker(restaurant) },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = if (saved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (saved) "Remove from saved" else "Save",
            tint = if (saved) palette.brand else Color.White,
            modifier = Modifier.size(15.dp),
        )
    }
}
