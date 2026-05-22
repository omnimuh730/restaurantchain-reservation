package com.mh.restaurantchainreservation.feature.search

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.components.DsBottomSheet
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButton
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButtonSize
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.DiscoverData
import com.mh.restaurantchainreservation.core.model.Restaurant
import com.mh.restaurantchainreservation.core.model.WishlistStore

object SearchRoutes {
    const val Results = "discover/search"
}

@Composable
fun SearchResultsScreen(
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    var query by rememberSaveable { mutableStateOf("") }
    var selectedPlan by rememberSaveable { mutableStateOf("Tonight") }
    var filtersOpen by rememberSaveable { mutableStateOf(false) }
    val results = remember(query, selectedPlan) {
        val needle = query.trim().lowercase()
        val base = if (needle.isBlank()) {
            DiscoverData.ALL
        } else {
            DiscoverData.ALL.filter {
                it.name.lowercase().contains(needle) ||
                    it.cuisine.lowercase().contains(needle) ||
                    it.area.orEmpty().lowercase().contains(needle) ||
                    it.tag.orEmpty().lowercase().contains(needle)
            }.ifEmpty { DiscoverData.ALL.take(4) }
        }
        when (selectedPlan) {
            "Nearby" -> base.sortedBy { it.distance }
            "Top rated" -> base.sortedByDescending { it.rating }
            else -> base
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.pageBackground),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, top = 18.dp, end = 20.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                SearchHeader(
                    query = query,
                    onQueryChange = { query = it },
                    onClear = { query = "" },
                    onFilter = { filtersOpen = true },
                )
            }
            item {
                SearchHeroMap(count = results.size)
            }
            item {
                PlanChips(
                    selected = selectedPlan,
                    onSelected = { selectedPlan = it },
                )
            }
            item {
                Row(verticalAlignment = Alignment.Bottom) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (query.isBlank()) "Restaurants near you" else "Results for \"$query\"",
                            color = palette.foreground,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "${results.size} available places - instant booking first",
                            color = palette.mutedForeground,
                            fontSize = 13.sp,
                        )
                    }
                    SearchChip(
                        label = "Map",
                        icon = Icons.Outlined.Map,
                        selected = false,
                        onClick = { },
                    )
                }
            }
            itemsIndexed(results, key = { _, item -> item.id }) { index, restaurant ->
                SearchRestaurantCard(
                    restaurant = restaurant,
                    index = index,
                )
            }
        }

        SearchFilterSheet(
            visible = filtersOpen,
            onDismiss = { filtersOpen = false },
        )
    }
}

@Composable
private fun SearchHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onFilter: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .clip(RoundedCornerShape(26.dp))
                .border(1.dp, palette.border, RoundedCornerShape(26.dp))
                .background(palette.cardSurface)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.Search, contentDescription = null, tint = palette.foreground, modifier = Modifier.size(20.dp))
            Spacer(Modifier.size(10.dp))
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (query.isBlank()) {
                    Text("Search restaurants", color = palette.mutedForeground, fontSize = 15.sp)
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(palette.brand),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (query.isNotBlank()) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Clear search",
                    tint = palette.mutedForeground,
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onClear)
                        .padding(2.dp),
                )
            }
        }
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .border(1.dp, palette.border, CircleShape)
                .background(palette.cardSurface)
                .clickable(onClick = onFilter),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.FilterList, contentDescription = "Filters", tint = palette.foreground, modifier = Modifier.size(21.dp))
        }
    }
}

@Composable
private fun SearchHeroMap(count: Int) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(palette.mutedSurface),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val road = palette.border
            repeat(6) { idx ->
                val y = size.height * (0.14f + idx * 0.16f)
                drawLine(road, Offset(0f, y), Offset(size.width, y + if (idx % 2 == 0) 42f else -28f), strokeWidth = 16f)
            }
            repeat(5) { idx ->
                val x = size.width * (0.10f + idx * 0.22f)
                drawLine(road.copy(alpha = 0.58f), Offset(x, 0f), Offset(x - 46f, size.height), strokeWidth = 12f)
            }
        }
        listOf(
            Triple("$$", 0.18f, 0.42f),
            Triple("4.9", 0.44f, 0.28f),
            Triple("$$$", 0.67f, 0.58f),
            Triple("Open", 0.78f, 0.32f),
        ).forEach { marker ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(
                        start = (280 * marker.second).dp,
                        top = (126 * marker.third).dp,
                    )
                    .clip(RoundedCornerShape(999.dp))
                    .background(palette.brand)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Text(marker.first, color = RestaurantColors.Base.white, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(14.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(palette.cardSurface.copy(alpha = 0.94f))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(palette.brandSoftSurface),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Restaurant, contentDescription = null, tint = palette.brand, modifier = Modifier.size(16.dp))
            }
            Column {
                Text("$count places found", color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Map preview", color = palette.mutedForeground, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun PlanChips(selected: String, onSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SearchChip("Tonight", Icons.Outlined.AccessTime, selected == "Tonight") { onSelected("Tonight") }
        SearchChip("Nearby", Icons.Outlined.Place, selected == "Nearby") { onSelected("Nearby") }
        SearchChip("Top rated", Icons.Filled.Star, selected == "Top rated") { onSelected("Top rated") }
        SearchChip("2 people", Icons.Outlined.Restaurant, selected == "2 people") { onSelected("2 people") }
    }
}

@Composable
private fun SearchRestaurantCard(
    restaurant: Restaurant,
    index: Int,
) {
    val palette = LocalRestaurantPalette.current
    val savedIds by WishlistStore.savedRestaurantIds.collectAsState()
    val saved = restaurant.id in savedIds
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, palette.border, RoundedCornerShape(24.dp))
            .background(palette.cardSurface)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(108.dp)
                .clip(RoundedCornerShape(18.dp)),
        ) {
            AsyncImage(
                model = restaurant.image,
                contentDescription = restaurant.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(RestaurantColors.Overlay.scrimStrong)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text("#${index + 1}", color = RestaurantColors.Base.white, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 3.dp),
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = restaurant.name,
                        color = palette.foreground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = listOfNotNull(restaurant.cuisine, restaurant.area).joinToString(" - "),
                        color = palette.mutedForeground,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                HeartButton(
                    active = saved,
                    onClick = { WishlistStore.onHeartTap(restaurant) },
                    size = HeartButtonSize.Small,
                    containerColor = palette.mutedSurface,
                    activeContainerColor = palette.brandSoftSurface,
                    inactiveTint = palette.foreground,
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Filled.Star, contentDescription = null, tint = palette.brand, modifier = Modifier.size(14.dp))
                Text("%.1f".format(restaurant.rating), color = palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("(${restaurant.reviews})", color = palette.mutedForeground, fontSize = 12.sp)
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                Text(restaurant.price, color = palette.foreground, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Dot()
                Text(restaurant.distance, color = palette.mutedForeground, fontSize = 12.sp)
                Dot()
                Text(restaurant.tag ?: "Instant book", color = palette.brand, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SearchFilterSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
) {
    DsBottomSheet(visible = visible, onDismiss = onDismiss) {
        val palette = LocalRestaurantPalette.current
        var sort by rememberSaveable { mutableStateOf("Recommended") }
        var price by rememberSaveable { mutableStateOf("Any") }
        Column(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Text("Filters", color = palette.foreground, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            FilterGroup("Sort by") {
                OptionRow("Recommended", "Balanced by popularity, distance, and availability", sort == "Recommended") { sort = "Recommended" }
                OptionRow("Rating", "Highest rated restaurants first", sort == "Rating") { sort = "Rating" }
                OptionRow("Distance", "Closest available tables", sort == "Distance") { sort = "Distance" }
            }
            FilterGroup("Price") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    listOf("Any", "$", "$$", "$$$", "$$$$").forEach { label ->
                        SearchChip(label = label, icon = null, selected = price == label) { price = label }
                    }
                }
            }
            FilterGroup("Dining preferences") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    SearchChip("Open now", Icons.Outlined.AccessTime, selected = true) { }
                    SearchChip("Instant book", Icons.Outlined.Restaurant, selected = false) { }
                    SearchChip("Near me", Icons.Outlined.Place, selected = false) { }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(palette.brand)
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center,
            ) {
                Text("Show restaurants", color = RestaurantColors.Base.white, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FilterGroup(title: String, content: @Composable () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            content()
        }
    }
}

@Composable
private fun OptionRow(title: String, subtitle: String, selected: Boolean, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, if (selected) palette.brand else palette.border, RoundedCornerShape(18.dp))
            .background(if (selected) palette.brandSoftSurface else palette.cardSurface)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .border(2.dp, if (selected) palette.brand else palette.border, CircleShape)
                .background(if (selected) palette.brand else Color.Transparent),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = palette.mutedForeground, fontSize = 12.sp)
        }
    }
}

@Composable
private fun SearchChip(
    label: String,
    icon: ImageVector?,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, if (selected) palette.brand else palette.border, RoundedCornerShape(20.dp))
            .background(if (selected) palette.brandSoftSurface else palette.cardSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = if (selected) palette.brand else palette.foreground, modifier = Modifier.size(16.dp))
        }
        Text(label, color = if (selected) palette.brand else palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Dot() {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .size(3.dp)
            .clip(CircleShape)
            .background(palette.border),
    )
}
