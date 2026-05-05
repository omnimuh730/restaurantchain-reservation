package com.mh.restaurantchainreservation.feature.discover.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.TrendingUp
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.DiscoverData

enum class SearchScope { Restaurants, Foods, Locations, Chefs }

/**
 * Full-screen search modal opened from the Discover home search bar. Mirrors
 * React `SearchModal` — recent + trending chips, scope filter, and a `Search`
 * CTA that calls `onSubmit(query)`.
 */
@Composable
fun DiscoverSearchModal(
    onClose: () -> Unit,
    onSubmit: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    var query by remember { mutableStateOf("") }
    var scope by remember { mutableStateOf(SearchScope.Restaurants) }
    val recents = remember { listOf("Sakura Omakase", "K-BBQ", "Brunch", "Skyline Rooftop") }
    val trending = remember { DiscoverData.trendingSearches() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.cardSurface)
            .windowInsetsPadding(WindowInsets.systemBars),
    ) {
        // Header with back + search field.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Close search",
                    tint = palette.foreground,
                )
            }
            Spacer(Modifier.size(4.dp))
            SearchField(
                value = query,
                onChange = { query = it },
                onSubmit = { if (query.isNotBlank()) onSubmit(query.trim()) },
                modifier = Modifier.weight(1f),
            )
        }

        // Scope chips.
        ScopeRow(active = scope, onSelect = { scope = it })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ChipsSection(
                title = "Recent searches",
                icon = Icons.Outlined.History,
                items = recents,
                onClick = { query = it },
            )
            ChipsSection(
                title = "Trending",
                icon = Icons.Outlined.TrendingUp,
                items = trending,
                onClick = { query = it },
            )

            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (query.isBlank()) palette.mutedSurface else palette.brand)
                    .clickable(enabled = query.isNotBlank()) {
                        if (query.isNotBlank()) onSubmit(query.trim())
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Search",
                    color = if (query.isBlank()) palette.mutedForeground else Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SearchField(
    value: String,
    onChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(999.dp))
            .background(palette.mutedSurface)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            tint = palette.mutedForeground,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.size(8.dp))
        BasicTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            cursorBrush = SolidColor(palette.brand),
            textStyle = TextStyle(
                color = palette.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(
                        text = "Restaurants, foods, chefs…",
                        color = palette.mutedForeground,
                        fontSize = 14.sp,
                    )
                }
                inner()
            },
        )
        if (value.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(palette.borderSoft)
                    .clickable { onChange("") },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Clear",
                    tint = palette.foreground,
                    modifier = Modifier.size(12.dp),
                )
            }
        }
    }
}

@Composable
private fun ScopeRow(active: SearchScope, onSelect: (SearchScope) -> Unit) {
    val palette = LocalRestaurantPalette.current
    val items = listOf(
        SearchScope.Restaurants to "Restaurants",
        SearchScope.Foods to "Foods",
        SearchScope.Locations to "Locations",
        SearchScope.Chefs to "Chefs",
    )
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items) { (scope, label) ->
            val isActive = scope == active
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (isActive) palette.foreground else palette.mutedSurface)
                    .clickable { onSelect(scope) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Text(
                    text = label,
                    color = if (isActive) palette.cardSurface else palette.foreground,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun ChipsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    items: List<String>,
    onClick: (String) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = palette.mutedForeground,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.size(6.dp))
            Text(
                text = title,
                color = palette.foreground,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        WrapChips(items = items, onClick = onClick)
    }
}

@Composable
private fun WrapChips(items: List<String>, onClick: (String) -> Unit) {
    val palette = LocalRestaurantPalette.current
    // Use a LazyRow with two rows for simplicity; FlowRow would be ideal but
    // adds another opt-in. The horizontal scroll stays usable for long lists.
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items) { label ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(palette.mutedSurface)
                    .border(1.dp, palette.borderSoft, RoundedCornerShape(999.dp))
                    .clickable { onClick(label) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Text(
                    text = label,
                    color = palette.foreground,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}
