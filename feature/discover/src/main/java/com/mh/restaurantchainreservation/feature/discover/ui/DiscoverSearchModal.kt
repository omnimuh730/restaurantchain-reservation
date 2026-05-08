package com.mh.restaurantchainreservation.feature.discover.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material.icons.outlined.WineBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette

private data class SearchSuggestion(
    val label: String,
    val helper: String? = null,
    val icon: ImageVector,
)

@Composable
fun DiscoverSearchModal(
    onClose: () -> Unit,
    onSubmit: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    var keyword by rememberSaveable { mutableStateOf("") }
    val recent = remember {
        listOf(
            SearchSuggestion("Sakura Omakase", icon = Icons.Outlined.AccessTime),
            SearchSuggestion("K-BBQ", icon = Icons.Outlined.AccessTime),
            SearchSuggestion("Brunch", icon = Icons.Outlined.AccessTime),
        )
    }
    val top = remember {
        listOf(
            SearchSuggestion("Italian", "Cuisine", Icons.Outlined.Restaurant),
            SearchSuggestion("Korean", "Cuisine", Icons.Outlined.Restaurant),
            SearchSuggestion("Japanese", "Cuisine", Icons.Outlined.Restaurant),
            SearchSuggestion("Steakhouse", "Cuisine", Icons.Outlined.Restaurant),
            SearchSuggestion("Seafood", "Cuisine", Icons.Outlined.Restaurant),
            SearchSuggestion("Brunch", "Cuisine", Icons.Outlined.WineBar),
        )
    }
    val recommended = remember {
        listOf(
            SearchSuggestion("Tonight specials", "Tables with perks tonight", Icons.Outlined.AutoAwesome),
            SearchSuggestion("Best rated", "Guest favorites near you", Icons.Outlined.Star),
            SearchSuggestion("Michelin", "Awarded restaurants", Icons.Outlined.Star),
            SearchSuggestion("New tables", "Fresh openings and new slots", Icons.Outlined.LocalFireDepartment),
            SearchSuggestion("Late night", "Open after 9 PM", Icons.Outlined.WineBar),
        )
    }

    fun submit(value: String = keyword) {
        onSubmit(value.trim())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.cardSurface)
            .windowInsetsPadding(WindowInsets.systemBars),
    ) {
        SearchModalHeader(
            keyword = keyword,
            onKeywordChange = { keyword = it },
            onClose = onClose,
            onSubmit = { submit() },
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
        ) {
            item {
                SuggestionRow(
                    suggestion = SearchSuggestion("See all restaurants", icon = Icons.Outlined.Store),
                    prominent = true,
                    onClick = { submit("") },
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .height(1.dp)
                        .background(palette.borderSoft),
                )
            }
            item {
                SearchSection(title = "Recent searches", suggestions = recent, onSubmit = ::submit)
            }
            item {
                SearchSection(title = "Top searches", suggestions = top, onSubmit = ::submit)
            }
            item {
                SearchSection(title = "Recommended", suggestions = recommended, onSubmit = ::submit)
            }
        }
        AnimatedVisibility(
            visible = keyword.trim().isNotEmpty(),
            enter = fadeIn(tween(180)) + slideInVertically { it / 2 },
            exit = fadeOut(tween(120)) + slideOutVertically { it / 2 },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, palette.borderSoft)
                    .background(palette.cardSurface.copy(alpha = 0.96f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(palette.brand)
                        .clickable(role = Role.Button) { submit() },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Outlined.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(17.dp))
                    Spacer(Modifier.size(8.dp))
                    Text("Search", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SearchModalHeader(
    keyword: String,
    onKeywordChange: (String) -> Unit,
    onClose: () -> Unit,
    onSubmit: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, palette.borderSoft)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClickLabel = "Close search", onClick = onClose),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Close, contentDescription = "Close search", tint = palette.foreground, modifier = Modifier.size(20.dp))
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(palette.cardSurface)
                .border(1.dp, palette.border, RoundedCornerShape(percent = 50))
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.Search, contentDescription = null, tint = palette.mutedForeground, modifier = Modifier.size(17.dp))
            Spacer(Modifier.size(9.dp))
            BasicTextField(
                value = keyword,
                onValueChange = onKeywordChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                cursorBrush = SolidColor(palette.brand),
                textStyle = TextStyle(color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (keyword.isEmpty()) {
                            Text("Search restaurants", color = palette.mutedForeground, fontSize = 15.sp)
                        }
                        inner()
                    }
                },
            )
            if (keyword.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable { onKeywordChange("") },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Clear", tint = palette.mutedForeground, modifier = Modifier.size(15.dp))
                }
            }
        }
    }
}

@Composable
private fun SearchSection(
    title: String,
    suggestions: List<SearchSuggestion>,
    onSubmit: (String) -> Unit,
) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        val palette = LocalRestaurantPalette.current
        Text(
            text = title,
            color = palette.foreground,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 2.dp, vertical = 4.dp),
        )
        suggestions.forEach { suggestion ->
            SuggestionRow(
                suggestion = suggestion,
                onClick = { onSubmit(suggestion.label) },
            )
        }
    }
}

@Composable
private fun SuggestionRow(
    suggestion: SearchSuggestion,
    prominent: Boolean = false,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(if (prominent) RoundedCornerShape(14.dp) else CircleShape)
                .background(if (prominent) palette.mutedSurface else palette.cardSurface)
                .border(1.dp, if (prominent) Color.Transparent else palette.border, if (prominent) RoundedCornerShape(14.dp) else CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(suggestion.icon, contentDescription = null, tint = palette.foreground, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = suggestion.label,
                color = palette.foreground,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
            if (suggestion.helper != null) {
                Text(
                    text = suggestion.helper,
                    color = palette.mutedForeground,
                    fontSize = 13.sp,
                    maxLines = 1,
                )
            }
        }
        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = palette.mutedForeground, modifier = Modifier.size(17.dp))
    }
}
