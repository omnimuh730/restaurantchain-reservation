@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.mh.restaurantchainreservation.feature.discover.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.TrendingUp
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
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.transition.LocalAnimatedContentScope
import com.mh.restaurantchainreservation.core.designsystem.transition.LocalRestaurantSharedTransitionScope
import com.mh.restaurantchainreservation.core.designsystem.transition.RestaurantSharedKeys
import com.mh.restaurantchainreservation.core.designsystem.transition.RestaurantSharedTransitionChrome
import com.mh.restaurantchainreservation.core.designsystem.transition.RestaurantSharedTransitionMotion
import com.mh.restaurantchainreservation.core.model.LocationStore

private enum class ActiveSearchInput { Location, Food }

@Composable
fun DiscoverSearchModal(
    onClose: () -> Unit,
    onSubmit: (query: String, planSummary: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val currentLocation by LocationStore.current.collectAsState()

    var keyword by rememberSaveable { mutableStateOf("") }
    var selectedWhere by rememberSaveable { mutableStateOf(WhereSelection.Anywhere.name) }
    var customWhere by rememberSaveable { mutableStateOf("") }
    var activeInput by remember { mutableStateOf(ActiveSearchInput.Location) }

    val listState = rememberLazyListState()
    val isScrolled by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }
    val dividerAlpha by animateFloatAsState(targetValue = if (isScrolled) 1f else 0f, label = "dividerAlpha")
    val shortcutGapTop by animateDpAsState(targetValue = if (isScrolled) 2.dp else 8.dp, label = "shortcutGapTop")
    val shortcutGapBottom by animateDpAsState(targetValue = if (isScrolled) 0.dp else 12.dp, label = "shortcutGapBottom")
    val dividerTranslationY by animateDpAsState(targetValue = if (isScrolled) 0.dp else 8.dp, label = "dividerTranslationY")

    fun whereSel(): WhereSelection = WhereSelection.valueOf(selectedWhere)

    val locationInputValue = remember(selectedWhere, customWhere, currentLocation.name) {
        when (whereSel()) {
            WhereSelection.Anywhere -> ""
            WhereSelection.Custom -> customWhere
            WhereSelection.NearMe -> "My current location"
            else -> whereLabel(whereSel(), "", currentLocation.name)
        }
    }

    val composedQuery = remember(keyword, selectedWhere, customWhere, currentLocation.name) {
        buildComposedSearchQuery(keyword, whereSel(), customWhere, currentLocation.name)
    }

    val planSummary = remember {
        formatPlanSummary("Tonight", "19:00", 2)
    }

    val locationFilterText = remember(selectedWhere, customWhere) {
        when (whereSel()) {
            WhereSelection.Custom -> customWhere.trim()
            else -> ""
        }
    }

    val filteredRecentLocations = remember(locationFilterText) {
        val t = locationFilterText.lowercase()
        if (t.isEmpty()) DiscoverSearchData.recentLocationLabels
        else DiscoverSearchData.recentLocationLabels.filter { it.lowercase().contains(t) }
    }

    val filteredLocations = remember(locationFilterText) {
        val t = locationFilterText.lowercase()
        if (t.isEmpty()) DiscoverSearchData.locationSuggestions
        else DiscoverSearchData.locationSuggestions.filter { row ->
            when (row) {
                is LocationSuggestionRow.Preset -> row.label.lowercase().contains(t)
                is LocationSuggestionRow.City -> row.label.lowercase().contains(t)
            }
        }
    }

    val filteredFood = remember(keyword) {
        val t = keyword.trim().lowercase()
        if (t.isEmpty()) DiscoverSearchData.foodSuggestions
        else DiscoverSearchData.foodSuggestions.filter { it.label.lowercase().contains(t) }
    }

    fun clearAll() {
        keyword = ""
        selectedWhere = WhereSelection.Anywhere.name
        customWhere = ""
        activeInput = ActiveSearchInput.Location
    }

    fun submitSearch() {
        val q = composedQuery.ifBlank { "All restaurants" }
        onSubmit(q, planSummary)
    }

    val locationFocusRequester = remember { FocusRequester() }
    val foodFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(420)
        locationFocusRequester.requestFocus()
    }

    val sharedScope = LocalRestaurantSharedTransitionScope.current
    val animatedScope = LocalAnimatedContentScope.current
    val transitionSnapshot = RestaurantSharedTransitionChrome.snapshot
    val transitionProgress = if (transitionSnapshot.active) transitionSnapshot.progress else 1f

    val sharedBoundsModifier = if (sharedScope != null && animatedScope != null) {
        with(sharedScope) {
            Modifier.sharedBounds(
                rememberSharedContentState(key = RestaurantSharedKeys.SearchBar),
                animatedVisibilityScope = animatedScope,
                boundsTransform = { _, _ ->
                    tween(
                        durationMillis = RestaurantSharedTransitionMotion.durationMillis,
                        easing = RestaurantSharedTransitionMotion.easing,
                    )
                },
                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
            )
        }
    } else Modifier

    Column(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { alpha = transitionProgress }
            .background(palette.pageBackground),
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(400, delayMillis = 200)) + slideInVertically(initialOffsetY = { -20 }),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(Modifier.size(36.dp))
                Text(
                    text = "Start your search",
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = palette.foreground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable(role = Role.Button, onClick = onClose),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = palette.foreground, modifier = Modifier.size(20.dp))
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(sharedBoundsModifier)
                .clip(RoundedCornerShape(20.dp))
                .background(palette.pageBackground)
                .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(16.dp))
            SearchInputRow(
                focused = activeInput == ActiveSearchInput.Location,
                placeholder = "Address, city, station…",
                value = locationInputValue,
                leadingIcon = Icons.Outlined.LocationOn,
                onFocus = { activeInput = ActiveSearchInput.Location },
                onValueChange = { v ->
                    if (v.isEmpty()) {
                        selectedWhere = WhereSelection.Anywhere.name
                        customWhere = ""
                    } else {
                        selectedWhere = WhereSelection.Custom.name
                        customWhere = v
                    }
                },
                onClear = {
                    selectedWhere = WhereSelection.Anywhere.name
                    customWhere = ""
                },
                focusRequester = locationFocusRequester,
                modifier = Modifier.graphicsLayer {
                    val p = (transitionProgress - 0.2f) / 0.4f
                    alpha = p.coerceIn(0f, 1f)
                    translationY = (1f - p.coerceIn(0f, 1f)) * 12.dp.toPx()
                }
            )
            Spacer(Modifier.height(10.dp))
            SearchInputRow(
                focused = activeInput == ActiveSearchInput.Food,
                placeholder = "Type of food, restaurant name…",
                value = keyword,
                leadingIcon = Icons.Outlined.Search,
                onFocus = { activeInput = ActiveSearchInput.Food },
                onValueChange = { keyword = it },
                onClear = { keyword = "" },
                focusRequester = foodFocusRequester,
                modifier = Modifier.graphicsLayer {
                    val p = (transitionProgress - 0.3f) / 0.4f
                    alpha = p.coerceIn(0f, 1f)
                    translationY = (1f - p.coerceIn(0f, 1f)) * 16.dp.toPx()
                }
            )
            Spacer(Modifier.height(shortcutGapTop))
            val shortcutProgress = ((transitionProgress - 0.45f) / 0.4f).coerceIn(0f, 1f)
            when (activeInput) {
                ActiveSearchInput.Location -> {
                    ShortcutRow(
                        icon = Icons.Outlined.Navigation,
                        label = "My current location",
                        selected = whereSel() == WhereSelection.NearMe,
                        onClick = {
                            selectedWhere = WhereSelection.NearMe.name
                            customWhere = ""
                        },
                        modifier = Modifier.graphicsLayer {
                            alpha = shortcutProgress
                            translationY = (1f - shortcutProgress) * 20.dp.toPx()
                        }
                    )
                }
                ActiveSearchInput.Food -> {
                    ShortcutRow(
                        icon = Icons.Outlined.Restaurant,
                        label = "See all restaurants",
                        selected = false,
                        onClick = {
                            keyword = ""
                            submitSearch()
                        },
                        modifier = Modifier.graphicsLayer {
                            alpha = shortcutProgress
                            translationY = (1f - shortcutProgress) * 20.dp.toPx()
                        }
                    )
                }
            }
            Spacer(Modifier.height(shortcutGapBottom))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .graphicsLayer {
                        alpha = dividerAlpha * shortcutProgress
                        translationY = dividerTranslationY.toPx()
                    }
                    .background(palette.border),
            )
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .graphicsLayer {
                    val p = ((transitionProgress - 0.55f) / 0.4f).coerceIn(0f, 1f)
                    alpha = p
                    translationY = (1f - p) * 24.dp.toPx()
                },
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
        ) {
            when (activeInput) {
                ActiveSearchInput.Location -> {
                    item {
                        Text(
                            "Recent searches",
                            color = palette.foreground,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                    }
                    items(filteredRecentLocations, key = { it }) { label ->
                        SuggestionListRow(
                            icon = Icons.Outlined.LocationOn,
                            label = label,
                            selected = whereSel() == WhereSelection.Custom && customWhere.equals(label, ignoreCase = true),
                            onClick = {
                                selectedWhere = WhereSelection.Custom.name
                                customWhere = label
                            },
                        )
                    }
                    item {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Suggested searches",
                            color = palette.foreground,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                    }
                    items(filteredLocations, key = { row ->
                        when (row) {
                            is LocationSuggestionRow.Preset -> "p:${row.where.name}"
                            is LocationSuggestionRow.City -> "c:${row.label}"
                        }
                    }) { row ->
                        val label = when (row) {
                            is LocationSuggestionRow.Preset -> row.label
                            is LocationSuggestionRow.City -> row.label
                        }
                        val selected = when (row) {
                            is LocationSuggestionRow.Preset -> whereSel() == row.where
                            is LocationSuggestionRow.City -> whereSel() == WhereSelection.Custom && customWhere == row.label
                        }
                        SuggestionListRow(
                            icon = Icons.Outlined.LocationOn,
                            label = label,
                            selected = selected,
                            onClick = {
                                when (row) {
                                    is LocationSuggestionRow.Preset -> {
                                        selectedWhere = row.where.name
                                        customWhere = ""
                                    }
                                    is LocationSuggestionRow.City -> {
                                        selectedWhere = WhereSelection.Custom.name
                                        customWhere = row.label
                                    }
                                }
                            },
                        )
                    }
                }
                ActiveSearchInput.Food -> {
                    item {
                        Text(
                            "Popular categories",
                            color = palette.foreground,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                    }
                    items(filteredFood, key = { it.label }) { food ->
                        val icon: ImageVector = if (food.trending) Icons.Outlined.TrendingUp else Icons.Outlined.Restaurant
                        SuggestionListRow(
                            icon = icon,
                            label = food.label,
                            selected = keyword.trim().equals(food.label, ignoreCase = true),
                            onClick = { keyword = food.label },
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(palette.pageBackground)
                .drawBehind {
                    drawLine(
                        color = palette.border,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 1.dp.toPx(),
                    )
                }
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                .padding(horizontal = 24.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Clear all",
                color = palette.foreground,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { clearAll() },
            )
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .background(RestaurantColors.Brand.reservePink)
                    .clickable { submitSearch() }
                    .padding(horizontal = 28.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Outlined.Search, contentDescription = null, tint = RestaurantColors.Base.white, modifier = Modifier.size(18.dp))
                Text("Search", color = RestaurantColors.Base.white, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SearchInputRow(
    focused: Boolean,
    placeholder: String,
    value: String,
    leadingIcon: ImageVector,
    onFocus: () -> Unit,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() },
) {
    val palette = LocalRestaurantPalette.current
    val border = if (focused) palette.foreground else palette.border
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, border, RoundedCornerShape(18.dp))
            .background(palette.cardSurface)
            .clickable {
                focusRequester.requestFocus()
                onFocus()
            }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(leadingIcon, contentDescription = null, tint = palette.mutedForeground, modifier = Modifier.size(18.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp)
                .focusRequester(focusRequester)
                .onFocusChanged { state ->
                    if (state.isFocused) onFocus()
                },
            singleLine = true,
            textStyle = TextStyle(color = palette.foreground, fontSize = 15.sp),
            cursorBrush = SolidColor(palette.brand),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(),
            decorationBox = { inner ->
                Box {
                    if (value.isEmpty()) {
                        Text(placeholder, color = RestaurantColors.Neutral.placeholder, fontSize = 15.sp)
                    }
                    inner()
                }
            },
        )
        if (value.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .clickable { onClear() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Close, contentDescription = "Clear", tint = palette.mutedForeground, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun ShortcutRow(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) palette.mutedSurface else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = palette.foreground, modifier = Modifier.size(22.dp))
        }
        Text(label, color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = palette.mutedForeground, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun SuggestionListRow(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) palette.mutedSurface else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = palette.foreground, modifier = Modifier.size(22.dp))
        }
        Text(
            label,
            color = palette.foreground,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (selected) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = palette.foreground, modifier = Modifier.size(18.dp))
        }
    }
}
