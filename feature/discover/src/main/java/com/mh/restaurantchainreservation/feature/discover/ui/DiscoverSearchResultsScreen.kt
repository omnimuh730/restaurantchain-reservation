package com.mh.restaurantchainreservation.feature.discover.ui

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.LocalParking
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButton
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButtonSize
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.DiscoverData
import com.mh.restaurantchainreservation.core.model.Restaurant
import com.mh.restaurantchainreservation.core.model.WishlistStore

private enum class ResultSheetState { Peek, Half, Full }

private data class SearchFilterState(
    val sortBy: String = "Recommended",
    val openNow: Boolean = false,
    val instantBook: Boolean = false,
    val prices: Set<String> = emptySet(),
    val cuisines: Set<String> = emptySet(),
    val amenities: Set<String> = emptySet(),
) {
    val activeCount: Int
        get() = listOf(openNow, instantBook).count { it } + prices.size + cuisines.size + amenities.size +
            if (sortBy != "Recommended") 1 else 0
}

@Composable
fun DiscoverSearchResultsScreen(
    query: String,
    onBack: () -> Unit,
    onOpenRestaurant: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var sheetState by remember { mutableStateOf(ResultSheetState.Half) }
    var activeMarker by remember { mutableIntStateOf(0) }
    var filtersOpen by remember { mutableStateOf(false) }
    var filters by remember { mutableStateOf(SearchFilterState()) }

    val matched = remember(query) {
        if (query.isBlank()) DiscoverData.ALL else matchRestaurants(query).ifEmpty { DiscoverData.ALL }
    }
    val filtered = remember(matched, filters) { applyFilters(matched, filters) }
    val preview = filtered.getOrNull(activeMarker.coerceAtLeast(0))

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F1EA)),
    ) {
        val headerHeight = 136.dp
        val peekHeight = 96.dp
        val sheetHeight = when (sheetState) {
            ResultSheetState.Peek -> peekHeight
            ResultSheetState.Half -> maxHeight * 0.48f
            ResultSheetState.Full -> maxHeight - headerHeight + 12.dp
        }

        MapSurface(
            restaurants = filtered,
            activeMarker = activeMarker,
            topInset = headerHeight,
            bottomInset = if (sheetState == ResultSheetState.Peek) peekHeight else 0.dp,
            onMarkerSelect = { index ->
                activeMarker = index
                sheetState = ResultSheetState.Peek
            },
            query = query,
            modifier = Modifier.fillMaxSize(),
        )

        SearchResultsHeader(
            query = query,
            activeFilterCount = filters.activeCount,
            onBack = onBack,
            onOpenFilters = { filtersOpen = true },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(headerHeight),
        )

        if (sheetState == ResultSheetState.Peek && preview != null) {
            MapPreviewCard(
                restaurant = preview,
                index = activeMarker,
                onOpen = { onOpenRestaurant(preview.id) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 16.dp, bottom = peekHeight + 14.dp),
            )
        }

        ResultsSheet(
            restaurants = filtered,
            query = query,
            sheetState = sheetState,
            height = sheetHeight,
            onStateChange = { sheetState = it },
            onOpenRestaurant = onOpenRestaurant,
            modifier = Modifier.align(Alignment.BottomCenter),
        )

        if (sheetState == ResultSheetState.Full) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 22.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(Color(0xFF222222))
                    .clickable { sheetState = ResultSheetState.Peek }
                    .padding(horizontal = 18.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("Map", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Icon(Icons.Outlined.Map, contentDescription = null, tint = Color.White, modifier = Modifier.size(17.dp))
            }
        }

        if (filtersOpen) {
            SearchFiltersSheet(
                filters = filters,
                resultCount = applyFilters(matched, filters).size,
                onChange = { filters = it },
                onClear = { filters = SearchFilterState() },
                onApply = { filtersOpen = false },
                onClose = { filtersOpen = false },
            )
        }
    }
}

@Composable
private fun SearchResultsHeader(
    query: String,
    activeFilterCount: Int,
    onBack: () -> Unit,
    onOpenFilters: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = modifier
            .background(palette.cardSurface.copy(alpha = 0.97f))
            .windowInsetsPadding(WindowInsets.statusBars)
            .border(1.dp, palette.borderSoft)
            .padding(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CircleIcon(icon = Icons.AutoMirrored.Filled.ArrowBack, label = "Back", onClick = onBack)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(palette.cardSurface)
                    .border(1.dp, palette.border, RoundedCornerShape(percent = 50))
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.Search, contentDescription = null, tint = palette.foreground, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(9.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = query.ifBlank { "All restaurants" },
                        color = palette.foreground,
                        fontSize = 15.sp,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                    )
                    Text("Tonight - 7:00 PM - 2 people", color = palette.mutedForeground, fontSize = 11.sp, maxLines = 1)
                }
                if (query.isNotBlank()) {
                    Icon(Icons.Filled.Close, contentDescription = null, tint = palette.mutedForeground, modifier = Modifier.size(15.dp))
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(palette.foreground)
                    .clickable(role = Role.Button, onClickLabel = "Filters", onClick = onOpenFilters),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.FilterList, contentDescription = "Filters", tint = palette.cardSurface, modifier = Modifier.size(18.dp))
                if (activeFilterCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(palette.brand)
                            .border(2.dp, palette.cardSurface, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(activeFilterCount.toString(), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                item { PlanChip(icon = Icons.Outlined.Place, label = "Anywhere") }
                item { PlanChip(icon = Icons.Outlined.CalendarMonth, label = "Tonight - 7:00 PM") }
                item { PlanChip(icon = Icons.Outlined.Groups, label = "2 people") }
            }
        }
    }
}

@Composable
private fun PlanChip(icon: ImageVector, label: String) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .height(40.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(palette.cardSurface)
            .border(1.dp, palette.border, RoundedCornerShape(percent = 50))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(icon, contentDescription = null, tint = palette.foreground, modifier = Modifier.size(15.dp))
        Text(label, color = palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

@Composable
private fun MapSurface(
    restaurants: List<Restaurant>,
    activeMarker: Int,
    topInset: Dp,
    bottomInset: Dp,
    onMarkerSelect: (Int) -> Unit,
    query: String,
    modifier: Modifier = Modifier,
) {
    val positions = listOf(
        Alignment.Center,
        Alignment.TopStart,
        Alignment.TopEnd,
        Alignment.CenterStart,
        Alignment.CenterEnd,
        Alignment.BottomStart,
        Alignment.BottomEnd,
        Alignment.TopCenter,
    )
    Box(
        modifier = modifier
            .padding(top = topInset, bottom = bottomInset)
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFF4EFE5), Color(0xFFEFF4EC), Color(0xFFE8F2F5)),
                ),
            ),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val road = Color.White.copy(alpha = 0.72f)
            val minor = Color.White.copy(alpha = 0.42f)
            val stroke = Stroke(width = 4f, pathEffect = PathEffect.cornerPathEffect(16f))
            for (i in 0..8) {
                val y = size.height * i / 8f
                drawLine(road, Offset(0f, y), Offset(size.width, y + (i % 3 - 1) * 34f), strokeWidth = if (i % 2 == 0) 7f else 3f)
                val x = size.width * i / 8f
                drawLine(minor, Offset(x, 0f), Offset(x + (i % 3 - 1) * 28f, size.height), strokeWidth = 3f)
            }
            drawCircle(color = Color(0xFFBDE7F0).copy(alpha = 0.55f), radius = size.minDimension * 0.22f, center = Offset(size.width * 0.88f, size.height * 0.78f), style = stroke)
        }
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(Color.White.copy(alpha = 0.95f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Icon(Icons.Outlined.Place, contentDescription = null, tint = Color(0xFF222222), modifier = Modifier.size(15.dp))
            Text(query.ifBlank { "Restaurants near you" }, color = Color(0xFF222222), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        restaurants.take(8).forEachIndexed { index, restaurant ->
            val alignment = positions[index % positions.size]
            PriceMarker(
                label = restaurant.price.ifBlank { "$$" },
                active = index == activeMarker,
                onClick = { onMarkerSelect(index) },
                modifier = Modifier
                    .align(alignment)
                    .padding(38.dp),
            )
        }
    }
}

@Composable
private fun PriceMarker(label: String, active: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(if (active) Color(0xFF222222) else Color.White)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (active) Color.White else Color(0xFF222222),
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
private fun ResultsSheet(
    restaurants: List<Restaurant>,
    query: String,
    sheetState: ResultSheetState,
    height: Dp,
    onStateChange: (ResultSheetState) -> Unit,
    onOpenRestaurant: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(palette.cardSurface)
            .border(1.dp, palette.borderSoft, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onStateChange(
                        when (sheetState) {
                            ResultSheetState.Peek -> ResultSheetState.Half
                            ResultSheetState.Half -> ResultSheetState.Full
                            ResultSheetState.Full -> ResultSheetState.Half
                        },
                    )
                }
                .padding(top = 9.dp, bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(Color(0xFFD1D1D1)),
            )
            Text(
                text = if (restaurants.isEmpty()) "No restaurants found" else "Over 1,000 restaurants",
                color = palette.foreground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 10.dp),
            )
        }
        if (sheetState != ResultSheetState.Peek) {
            if (restaurants.isEmpty()) {
                EmptyResults(query = query, modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = if (sheetState == ResultSheetState.Full) 88.dp else 28.dp),
                    verticalArrangement = Arrangement.spacedBy(22.dp),
                ) {
                    items(restaurants, key = { it.id }) { restaurant ->
                        RestaurantResultCard(
                            restaurant = restaurant,
                            onOpen = { onOpenRestaurant(restaurant.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RestaurantResultCard(restaurant: Restaurant, onOpen: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val collections by WishlistStore.collections.collectAsState()
    val saved = collections.any { col -> col.restaurants.any { it.id == restaurant.id } }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(palette.mutedSurface),
            ) {
                AsyncImage(
                    model = restaurant.image,
                    contentDescription = restaurant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Text(
                    text = restaurant.tag ?: "Guest favorite",
                    color = Color(0xFF222222),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(Color.White)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                )
                HeartButton(
                    active = saved,
                    onClick = { WishlistStore.openPicker(restaurant) },
                    size = HeartButtonSize.Large,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                )
                Dots(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 10.dp))
            }
            Row(
                modifier = Modifier.padding(top = 12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(restaurant.name, color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(
                        text = "${restaurant.cuisine} - ${restaurant.area ?: restaurant.distance}",
                        color = palette.mutedForeground,
                        fontSize = 13.sp,
                        maxLines = 1,
                    )
                    Text(
                        text = "Tables tonight - ${restaurant.price} for tonight",
                        color = palette.mutedForeground,
                        fontSize = 13.sp,
                        maxLines = 1,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFB400), modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(3.dp))
                    Text("%.1f".format(restaurant.rating), color = palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun MapPreviewCard(
    restaurant: Restaurant,
    index: Int,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val collections by WishlistStore.collections.collectAsState()
    val saved = collections.any { col -> col.restaurants.any { it.id == restaurant.id } }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(136.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(palette.cardSurface)
            .clickable(onClick = onOpen),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(136.dp)
                .background(palette.mutedSurface),
        ) {
            AsyncImage(
                model = restaurant.image,
                contentDescription = restaurant.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Text(
                text = if (index % 2 == 0) "Trophy pick" else "Guest favorite",
                color = palette.foreground,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(Color.White)
                    .padding(horizontal = 9.dp, vertical = 4.dp),
            )
            HeartButton(
                active = saved,
                onClick = { WishlistStore.openPicker(restaurant) },
                size = HeartButtonSize.Small,
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = restaurant.name,
                        color = palette.foreground,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier.weight(1f),
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = palette.foreground, modifier = Modifier.size(13.dp))
                        Text("%.1f".format(restaurant.rating), color = palette.foreground, fontSize = 12.sp)
                    }
                }
                Text("${restaurant.cuisine} - ${restaurant.area ?: restaurant.distance}", color = palette.mutedForeground, fontSize = 13.sp, maxLines = 1)
                Text("Open tonight - ${restaurant.distance}", color = palette.mutedForeground, fontSize = 13.sp, maxLines = 1)
            }
            Text(
                text = "${restaurant.price} for tonight",
                color = palette.foreground,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun Dots(modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(5) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == 0) 7.dp else 6.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = if (index == 0) 1f else 0.55f)),
            )
        }
    }
}

@Composable
private fun EmptyResults(query: String, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 38.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(Icons.Outlined.Search, contentDescription = null, tint = palette.mutedForeground.copy(alpha = 0.35f), modifier = Modifier.size(48.dp))
        Text("No results for ${query.ifBlank { "this search" }}", color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text("Try a different keyword or clear filters.", color = palette.mutedForeground, fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 5.dp))
    }
}

@Composable
private fun SearchFiltersSheet(
    filters: SearchFilterState,
    resultCount: Int,
    onChange: (SearchFilterState) -> Unit,
    onClear: () -> Unit,
    onApply: () -> Unit,
    onClose: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.30f))
                .clickable(onClick = onClose),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(720.dp)
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(palette.cardSurface)
                    .clickable {}
                    .windowInsetsPadding(WindowInsets.navigationBars),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(1.dp, palette.borderSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Filters", color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    CircleIcon(icon = Icons.Filled.Close, label = "Close", onClick = onClose, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp))
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 22.dp, vertical = 20.dp),
                ) {
                    FilterSection(title = "Sort by") {
                        FlowPills(
                            labels = listOf("Recommended", "Highest Rated", "Nearest", "Price: Low to High", "Most Reviewed"),
                            selected = setOf(filters.sortBy),
                            onToggle = { onChange(filters.copy(sortBy = it)) },
                        )
                    }
                    FilterSection(title = "Recommended") {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            RecommendedFilterCard(
                                label = "Open now",
                                icon = Icons.Outlined.AccessTime,
                                active = filters.openNow,
                                onClick = { onChange(filters.copy(openNow = !filters.openNow)) },
                                modifier = Modifier.weight(1f),
                            )
                            RecommendedFilterCard(
                                label = "Instant book",
                                icon = Icons.Outlined.Bolt,
                                active = filters.instantBook,
                                onClick = { onChange(filters.copy(instantBook = !filters.instantBook)) },
                                modifier = Modifier.weight(1f),
                            )
                            RecommendedFilterCard(
                                label = "Parking",
                                icon = Icons.Outlined.LocalParking,
                                active = filters.amenities.contains("Parking"),
                                onClick = { onChange(filters.copy(amenities = filters.amenities.toggle("Parking"))) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                    FilterSection(title = "Cuisine") {
                        FlowPills(
                            labels = listOf("Korean", "Japanese", "French", "Chinese", "Brunch", "Mediterranean", "Fusion", "Dessert"),
                            selected = filters.cuisines,
                            onToggle = { onChange(filters.copy(cuisines = filters.cuisines.toggle(it))) },
                        )
                    }
                    FilterSection(title = "Price range", subtitle = "Choose the dinner budget you are comfortable with.") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            listOf("$", "$$", "$$$", "$$$$").forEach { price ->
                                FilterPill(
                                    label = price,
                                    active = filters.prices.contains(price),
                                    onClick = { onChange(filters.copy(prices = filters.prices.toggle(price))) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                    FilterSection(title = "Amenities") {
                        FlowPills(
                            labels = listOf("Outdoor seating", "Wine list", "Vegan", "Halal", "Gluten-free", "Live music", "Wi-Fi", "Private room"),
                            selected = filters.amenities,
                            onToggle = { onChange(filters.copy(amenities = filters.amenities.toggle(it))) },
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, palette.borderSoft)
                        .padding(horizontal = 22.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Clear all",
                        color = if (filters.activeCount > 0) palette.foreground else palette.mutedForeground.copy(alpha = 0.55f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(enabled = filters.activeCount > 0, onClick = onClear)
                            .padding(vertical = 8.dp),
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (resultCount > 0) palette.foreground else palette.border)
                            .clickable(enabled = resultCount > 0, onClick = onApply)
                            .padding(horizontal = 24.dp, vertical = 13.dp),
                    ) {
                        Text(
                            text = if (resultCount > 0) "Show places" else "No matches",
                            color = if (resultCount > 0) palette.cardSurface else palette.mutedForeground,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterSection(title: String, subtitle: String? = null, content: @Composable () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Transparent)
            .padding(vertical = 16.dp),
    ) {
        Text(title, color = palette.foreground, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        if (subtitle != null) {
            Text(subtitle, color = palette.mutedForeground, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
        }
        Box(modifier = Modifier.padding(top = 12.dp)) { content() }
    }
}

@Composable
private fun RecommendedFilterCard(
    label: String,
    icon: ImageVector,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(palette.cardSurface)
                .border(if (active) 2.dp else 1.dp, if (active) palette.foreground else palette.border, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = if (active) palette.foreground else palette.mutedForeground, modifier = Modifier.size(38.dp))
            if (active) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(palette.foreground),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = palette.cardSurface, modifier = Modifier.size(13.dp))
                }
            }
        }
        Text(label, color = palette.foreground, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp), maxLines = 2)
    }
}

@Composable
private fun FlowPills(labels: List<String>, selected: Set<String>, onToggle: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        labels.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { label ->
                    FilterPill(label = label, active = selected.contains(label), onClick = { onToggle(label) })
                }
            }
        }
    }
}

@Composable
private fun FilterPill(label: String, active: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(if (active) palette.foreground else palette.cardSurface)
            .border(1.dp, if (active) palette.foreground else palette.border, RoundedCornerShape(percent = 50))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (active) palette.cardSurface else palette.foreground,
            fontSize = 13.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
        )
    }
}

@Composable
private fun CircleIcon(icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClickLabel = label, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = label, tint = palette.foreground, modifier = Modifier.size(20.dp))
    }
}

private fun Set<String>.toggle(value: String): Set<String> = if (contains(value)) this - value else this + value

private fun applyFilters(restaurants: List<Restaurant>, filters: SearchFilterState): List<Restaurant> {
    val filtered = restaurants.filter { r ->
        val priceOk = filters.prices.isEmpty() || filters.prices.contains(r.price)
        val cuisineOk = filters.cuisines.isEmpty() || filters.cuisines.any { r.cuisine.contains(it, ignoreCase = true) }
        priceOk && cuisineOk
    }
    val sorted = when (filters.sortBy) {
        "Highest Rated" -> filtered.sortedByDescending { it.rating }
        "Nearest" -> filtered.sortedBy { it.distance }
        "Most Reviewed" -> filtered.sortedByDescending { it.reviews }
        "Price: Low to High" -> filtered.sortedBy { it.price.length }
        else -> filtered
    }
    return sorted
}

private fun matchRestaurants(query: String): List<Restaurant> {
    if (query.isBlank()) return DiscoverData.ALL
    val q = query.trim().lowercase()
    return DiscoverData.ALL.filter {
        it.name.lowercase().contains(q) ||
            it.cuisine.lowercase().contains(q) ||
            (it.area ?: "").lowercase().contains(q) ||
            (it.tag ?: "").lowercase().contains(q)
    }
}
