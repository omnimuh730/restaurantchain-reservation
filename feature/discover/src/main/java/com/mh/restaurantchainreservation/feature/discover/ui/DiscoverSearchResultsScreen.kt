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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

private enum class ResultTab { All, Restaurants, Foods, Locations, Chefs }

/**
 * Results screen for `discover/search?q=…`. Mirrors React `SearchResultsView`
 * with a query header, segmented tabs (with counts), and lists per tab.
 * Filtering is client-side `String.contains` against the mock catalog.
 */
@Composable
fun DiscoverSearchResultsScreen(
    query: String,
    onBack: () -> Unit,
    onOpenRestaurant: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    var tab by remember { mutableStateOf(ResultTab.All) }

    val restaurants = remember(query) { matchRestaurants(query) }
    val foods = remember(query) { matchFoodLabels(query) }
    val locations = remember(query) { matchLocationLabels(query) }
    val chefs = remember(query) { matchChefs(query) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.cardSurface)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        // Header.
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
                    text = "Results for",
                    color = palette.mutedForeground,
                    fontSize = 11.sp,
                )
                Text(
                    text = "\"" + query + "\"",
                    color = palette.foreground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
            }
        }

        // Tabs row with counts.
        TabsRow(
            tab = tab,
            counts = mapOf(
                ResultTab.All to (restaurants.size + foods.size + locations.size + chefs.size),
                ResultTab.Restaurants to restaurants.size,
                ResultTab.Foods to foods.size,
                ResultTab.Locations to locations.size,
                ResultTab.Chefs to chefs.size,
            ),
            onSelect = { tab = it },
        )

        when (tab) {
            ResultTab.All -> AllResultsList(
                restaurants = restaurants,
                foods = foods,
                locations = locations,
                chefs = chefs,
                onOpenRestaurant = onOpenRestaurant,
            )
            ResultTab.Restaurants -> RestaurantList(items = restaurants, onClick = onOpenRestaurant)
            ResultTab.Foods -> SimpleStringList(items = foods, kind = "Food")
            ResultTab.Locations -> SimpleStringList(items = locations, kind = "Location")
            ResultTab.Chefs -> SimpleStringList(items = chefs, kind = "Chef")
        }
    }
}

@Composable
private fun TabsRow(
    tab: ResultTab,
    counts: Map<ResultTab, Int>,
    onSelect: (ResultTab) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val items = listOf(
        ResultTab.All to "All",
        ResultTab.Restaurants to "Restaurants",
        ResultTab.Foods to "Foods",
        ResultTab.Locations to "Locations",
        ResultTab.Chefs to "Chefs",
    )
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items) { (id, label) ->
            val isActive = id == tab
            val count = counts[id] ?: 0
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (isActive) palette.foreground else palette.mutedSurface)
                    .clickable { onSelect(id) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "$label ($count)",
                    color = if (isActive) palette.cardSurface else palette.foreground,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun RestaurantList(items: List<Restaurant>, onClick: (String) -> Unit) {
    if (items.isEmpty()) {
        EmptyHint(text = "No restaurants matched.")
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items, key = { it.id }) { item ->
            RestaurantListCard(restaurant = item, onClick = { onClick(item.id) })
        }
    }
}

@Composable
private fun AllResultsList(
    restaurants: List<Restaurant>,
    foods: List<String>,
    locations: List<String>,
    chefs: List<String>,
    onOpenRestaurant: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (foods.isNotEmpty()) {
            item { GroupTitle("Foods") }
            items(foods) { label -> StringRow(label = label, kind = "Food") }
        }
        if (locations.isNotEmpty()) {
            item { GroupTitle("Locations") }
            items(locations) { label -> StringRow(label = label, kind = "Location") }
        }
        if (chefs.isNotEmpty()) {
            item { GroupTitle("Chefs") }
            items(chefs) { label -> StringRow(label = label, kind = "Chef") }
        }
        if (restaurants.isNotEmpty()) {
            item { GroupTitle("Restaurants") }
            items(restaurants, key = { it.id }) { item ->
                RestaurantListCard(restaurant = item, onClick = { onOpenRestaurant(item.id) })
            }
        }
        if (restaurants.isEmpty() && foods.isEmpty() && locations.isEmpty() && chefs.isEmpty()) {
            item { EmptyHint(text = "No results. Try a different keyword.") }
        }
    }
}

@Composable
private fun GroupTitle(text: String) {
    val palette = LocalRestaurantPalette.current
    Text(
        text = text,
        color = palette.mutedForeground,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 4.dp),
    )
}

@Composable
private fun SimpleStringList(items: List<String>, kind: String) {
    if (items.isEmpty()) {
        EmptyHint(text = "No matches.")
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items) { label -> StringRow(label = label, kind = kind) }
    }
}

@Composable
private fun StringRow(label: String, kind: String) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(palette.mutedSurface)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = palette.foreground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = kind,
            color = palette.mutedForeground,
            fontSize = 11.sp,
        )
    }
}

@Composable
private fun EmptyHint(text: String) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = palette.mutedForeground,
            fontSize = 14.sp,
        )
    }
}

private fun matchRestaurants(query: String): List<Restaurant> {
    if (query.isBlank()) return emptyList()
    val q = query.trim().lowercase()
    return DiscoverData.ALL.filter {
        it.name.lowercase().contains(q) ||
            it.cuisine.lowercase().contains(q) ||
            (it.area ?: "").lowercase().contains(q) ||
            (it.tag ?: "").lowercase().contains(q)
    }
}

private fun matchFoodLabels(query: String): List<String> {
    if (query.isBlank()) return emptyList()
    val q = query.trim().lowercase()
    return DiscoverData.FOOD_TYPES
        .filter { it.label.lowercase().contains(q) }
        .map { it.label }
}

private fun matchLocationLabels(query: String): List<String> {
    if (query.isBlank()) return emptyList()
    val q = query.trim().lowercase()
    return DiscoverData.CITIES
        .filter { it.label.lowercase().contains(q) }
        .map { it.label }
}

private val CHEF_POOL = listOf(
    "Chef Hong Seok-cheon · Hong's Kitchen",
    "Chef Jun Hong · Jun's Omakase",
    "Chef David Chang · Momofuku",
    "Chef Yuki Tanaka · Sakura Omakase",
)

private fun matchChefs(query: String): List<String> {
    if (query.isBlank()) return emptyList()
    val q = query.trim().lowercase()
    return CHEF_POOL.filter { it.lowercase().contains(q) }
}
