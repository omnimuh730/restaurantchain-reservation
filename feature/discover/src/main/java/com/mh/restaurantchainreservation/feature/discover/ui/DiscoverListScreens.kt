package com.mh.restaurantchainreservation.feature.discover.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.DiscoverData
import com.mh.restaurantchainreservation.core.model.Restaurant

/** Restaurants for a quick category id. */
@Composable
fun CategoryResultsScreen(
    categoryId: String,
    onBack: () -> Unit,
    onOpenRestaurant: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = DiscoverData.QUICK_CATEGORIES.firstOrNull { it.id == categoryId }
        ?.label?.replace("\n", " ")
        ?: "Restaurants"
    ListScaffold(
        title = title,
        items = DiscoverData.byCategory(categoryId),
        onBack = onBack,
        onOpenRestaurant = onOpenRestaurant,
        modifier = modifier,
    )
}

/** Restaurants for a food-type id. */
@Composable
fun FoodResultsScreen(
    foodId: String,
    onBack: () -> Unit,
    onOpenRestaurant: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = DiscoverData.FOOD_TYPES.firstOrNull { it.id == foodId }?.label ?: "Food"
    ListScaffold(
        title = title,
        items = DiscoverData.byFoodType(foodId),
        onBack = onBack,
        onOpenRestaurant = onOpenRestaurant,
        modifier = modifier,
    )
}

/** Restaurants for a city/location id. */
@Composable
fun LocationResultsScreen(
    locationId: String,
    onBack: () -> Unit,
    onOpenRestaurant: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val city = DiscoverData.CITIES.firstOrNull { it.id == locationId }
    val title = city?.let { "Best of ${it.label}" } ?: "Restaurants"
    ListScaffold(
        title = title,
        items = DiscoverData.byCity(locationId),
        onBack = onBack,
        onOpenRestaurant = onOpenRestaurant,
        modifier = modifier,
    )
}

/** Restaurants for a section id (monthly-best / loved-by-locals / viral / date-night). */
@Composable
fun SectionListScreen(
    sectionId: String,
    onBack: () -> Unit,
    onOpenRestaurant: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = when (sectionId) {
        "monthly-best" -> "Monthly Best"
        "loved-by-locals" -> "Loved by Locals"
        "viral" -> "Trending Now"
        "date-night" -> "Date Night Picks"
        else -> "Featured"
    }
    ListScaffold(
        title = title,
        items = DiscoverData.bySection(sectionId),
        onBack = onBack,
        onOpenRestaurant = onOpenRestaurant,
        modifier = modifier,
    )
}

@Composable
private fun ListScaffold(
    title: String,
    items: List<Restaurant>,
    onBack: () -> Unit,
    onOpenRestaurant: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    var filterOpen by remember { mutableStateOf(false) }
    var visibleItems by remember(items) { mutableStateOf(items) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
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
                Spacer(Modifier.size(4.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = palette.foreground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                    )
                    Text(
                        text = "${visibleItems.size} restaurants",
                        color = palette.mutedForeground,
                        fontSize = 12.sp,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { filterOpen = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FilterList,
                        contentDescription = "Filters",
                        tint = palette.foreground,
                    )
                }
            }
            if (visibleItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Nothing matches your filters yet.",
                        color = palette.mutedForeground,
                        fontSize = 14.sp,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(visibleItems, key = { it.id }) { item ->
                        RestaurantListCard(restaurant = item, onClick = { onOpenRestaurant(item.id) })
                    }
                }
            }
        }

        FilterSheet(
            visible = filterOpen,
            allItems = items,
            onDismiss = { filterOpen = false },
            onApply = { filtered ->
                visibleItems = filtered
                filterOpen = false
            },
        )
    }
}
