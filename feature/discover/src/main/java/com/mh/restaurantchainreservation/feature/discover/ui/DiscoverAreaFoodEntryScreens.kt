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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButton
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButtonSize
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButtonStyle
import com.mh.restaurantchainreservation.core.designsystem.components.trackBottomNavScroll
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.City
import com.mh.restaurantchainreservation.core.model.DiscoverData
import com.mh.restaurantchainreservation.core.model.FoodType
import com.mh.restaurantchainreservation.core.model.WishlistStore
import kotlin.math.roundToInt

/**
 * Middle step for “Where to Eat?”: lists cities/areas; each opens [DiscoverSearchResultsScreen] scoped to that area.
 */
@Composable
fun WhereToEatAreaListScreen(
    onBack: () -> Unit,
    onSelectArea: (locationId: String, areaLabel: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val cities = remember { DiscoverData.CITIES }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.cardSurface)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = palette.foreground,
                )
            }
            Spacer(Modifier.width(4.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Where to Eat?",
                    color = palette.foreground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                )
                Text(
                    text = "${cities.size} areas",
                    color = palette.mutedForeground,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        if (cities.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No areas available yet.",
                    color = palette.mutedForeground,
                    fontSize = 15.sp,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().trackBottomNavScroll(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(cities, key = { it.id }) { city ->
                    WhereToEatAreaCard(
                        city = city,
                        onClick = { onSelectArea(city.id, city.label) },
                    )
                }
            }
        }
    }
}

@Composable
private fun WhereToEatAreaCard(
    city: City,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val restaurants = remember(city.id) { DiscoverData.byCity(city.id) }
    val avgRating = remember(restaurants) {
        if (restaurants.isEmpty()) 0.0 else restaurants.map { it.rating }.average()
    }
    val totalReviews = remember(restaurants) { restaurants.sumOf { it.reviews } }
    val collections by WishlistStore.collections.collectAsState()
    val areaWishlisted = remember(collections, city.id) {
        restaurants.any { r -> collections.any { col -> col.restaurants.any { it.id == r.id } } }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(palette.cardSurface)
            .border(1.dp, palette.borderSoft, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
        ) {
            AsyncImage(
                model = city.image,
                contentDescription = city.label,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.22f)),
            )
            Text(
                text = city.label.uppercase(),
                color = palette.foreground,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.White.copy(alpha = 0.94f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            )
            if (restaurants.isNotEmpty()) {
                HeartButton(
                    active = areaWishlisted,
                    onClick = { restaurants.firstOrNull()?.let { WishlistStore.openPicker(it) } },
                    size = HeartButtonSize.Medium,
                    style = HeartButtonStyle.Overlay,
                    overlayContentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                )
            }
        }
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)) {
            Text(
                text = "Best of ${city.label}",
                color = palette.foreground,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Place,
                    contentDescription = null,
                    tint = palette.mutedForeground,
                    modifier = Modifier.size(15.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = areaSubtitleLine(city, restaurants.size),
                    color = palette.mutedForeground,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (restaurants.isNotEmpty()) {
                    AreaCardStarRating(avgRating = avgRating)
                }
            }
            if (restaurants.isNotEmpty()) {
                Text(
                    text = "${formatThousands(totalReviews)} reviews",
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 6.dp),
                )
            } else {
                Text(
                    text = "Explore restaurants in this area",
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
    }
}

private fun areaSubtitleLine(city: City, restaurantCount: Int): String =
    if (restaurantCount > 0) {
        "Curated picks · ${city.label}"
    } else {
        "Discover dining · ${city.label}"
    }

@Composable
private fun AreaCardStarRating(avgRating: Double) {
    val palette = LocalRestaurantPalette.current
    val goldStar = Color(0xFFEAB308)
    val emptyStar = palette.mutedForeground.copy(alpha = 0.35f)
    val filledStars = (avgRating + 0.25).roundToInt().coerceIn(0, 5)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = if (index < filledStars) goldStar else emptyStar,
                modifier = Modifier.size(13.dp),
            )
        }
        Spacer(Modifier.width(4.dp))
        Text(
            text = "%.1f".format(avgRating),
            color = palette.foreground,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun formatThousands(count: Int): String =
    count.toString().reversed().chunked(3).joinToString(",").reversed()

/**
 * Middle step for “Top Picks by Food Type”: lists cuisines; each opens [FoodResultsScreen] for that type.
 */
@Composable
fun FoodTypeCuisineListScreen(
    onBack: () -> Unit,
    onSelectCuisine: (foodId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val foodTypes = remember { DiscoverData.FOOD_TYPES }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.cardSurface)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = palette.foreground,
                )
            }
            Spacer(Modifier.width(4.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Top Picks by Food Type",
                    color = palette.foreground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                )
                Text(
                    text = "${foodTypes.size} cuisines",
                    color = palette.mutedForeground,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        if (foodTypes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No cuisines available yet.",
                    color = palette.mutedForeground,
                    fontSize = 15.sp,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().trackBottomNavScroll(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(foodTypes, key = { it.id }) { food ->
                    FoodTypeCuisineRow(
                        food = food,
                        onClick = { onSelectCuisine(food.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun FoodTypeCuisineRow(
    food: FoodType,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val count = remember(food.id) { DiscoverData.byFoodType(food.id).size }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.mutedSurface.copy(alpha = 0.45f))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = food.image,
            contentDescription = food.label,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp)),
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = food.label,
                color = palette.foreground,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = if (count > 0) "$count restaurants" else "Browse picks",
                color = palette.mutedForeground,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = palette.mutedForeground,
            modifier = Modifier.size(22.dp),
        )
    }
}
