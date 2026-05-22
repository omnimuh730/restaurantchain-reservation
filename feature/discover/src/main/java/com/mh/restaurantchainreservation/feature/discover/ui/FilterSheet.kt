package com.mh.restaurantchainreservation.feature.discover.ui

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.components.CenteredMaterialDragHandle
import com.mh.restaurantchainreservation.core.designsystem.components.RestaurantSwitch
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.Restaurant

private enum class PriceTier(val label: String, val symbol: String) {
    Any("Any", ""), Cheap("$", "$"), Mid("$$", "$$"), Pricy("$$$", "$$$"), Lux("$$$$", "$$$$");
}

private enum class RatingFloor(val label: String, val value: Double) {
    Any("Any", 0.0), Four("4.0+", 4.0), FourFive("4.5+", 4.5), Five("5.0", 5.0);
}

/**
 * Bottom-sheet style filter for restaurant lists. Mirrors React `FilterSheet`
 * with cuisines (chips), price (single select), distance (slider 0–25 mi),
 * rating (4+/4.5+/5), and an "Open now" switch. Returns a filtered list to
 * the host via `onApply` — no global state.
 */
@Composable
fun FilterSheet(
    visible: Boolean,
    allItems: List<Restaurant>,
    onDismiss: () -> Unit,
    onApply: (List<Restaurant>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    var selectedCuisines by remember { mutableStateOf(setOf<String>()) }
    var price by remember { mutableStateOf(PriceTier.Any) }
    var distanceMi by remember { mutableFloatStateOf(25f) }
    var rating by remember { mutableStateOf(RatingFloor.Any) }
    var openNow by remember { mutableStateOf(false) }

    val cuisineOptions = remember(allItems) {
        allItems.map { it.cuisine.substringBefore("·").trim() }.distinct().sorted()
    }

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(RestaurantColors.Base.blackAlpha(0.4f))
                    .clickable(onClick = onDismiss),
            )
        }
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(palette.cardSurface),
            ) {
                CenteredMaterialDragHandle()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 4.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                Text(
                    text = "Filters",
                    color = palette.foreground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )

                FilterLabel("Cuisine")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(cuisineOptions) { option ->
                        val active = option in selectedCuisines
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(if (active) palette.brand else palette.mutedSurface)
                                .clickable {
                                    selectedCuisines = if (active) selectedCuisines - option else selectedCuisines + option
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                        ) {
                            Text(
                                text = option,
                                color = if (active) RestaurantColors.Base.white else palette.foreground,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }

                FilterLabel("Price")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PriceTier.values().forEach { tier ->
                        val active = tier == price
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (active) palette.foreground else palette.mutedSurface)
                                .clickable { price = tier }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = tier.label,
                                color = if (active) palette.cardSurface else palette.foreground,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }

                FilterLabel("Distance: ${"%.0f".format(distanceMi)} mi")
                Slider(
                    value = distanceMi,
                    onValueChange = { distanceMi = it },
                    valueRange = 0f..25f,
                )

                FilterLabel("Rating")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    RatingFloor.values().forEach { floor ->
                        val active = floor == rating
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (active) palette.foreground else palette.mutedSurface)
                                .clickable { rating = floor }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = floor.label,
                                color = if (active) palette.cardSurface else palette.foreground,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(palette.mutedSurface)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Open now",
                        color = palette.foreground,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f),
                    )
                    RestaurantSwitch(
                        checked = openNow,
                        onCheckedChange = { openNow = it },
                    )
                }

                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(999.dp))
                            .border(1.dp, palette.border, RoundedCornerShape(999.dp))
                            .clickable {
                                selectedCuisines = emptySet()
                                price = PriceTier.Any
                                distanceMi = 25f
                                rating = RatingFloor.Any
                                openNow = false
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Reset", color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(999.dp))
                            .background(palette.brand)
                            .clickable {
                                onApply(applyFilters(allItems, selectedCuisines, price, distanceMi, rating, openNow))
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Apply", color = RestaurantColors.Base.white, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
                }
            }
        }
    }
}

@Composable
private fun FilterLabel(text: String) {
    val palette = LocalRestaurantPalette.current
    Text(
        text = text,
        color = palette.mutedForeground,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
    )
}

private fun applyFilters(
    items: List<Restaurant>,
    cuisines: Set<String>,
    price: PriceTier,
    distance: Float,
    rating: RatingFloor,
    openNow: Boolean,
): List<Restaurant> {
    return items.filter { r ->
        val cuisineOk = cuisines.isEmpty() || cuisines.any { r.cuisine.contains(it, ignoreCase = true) }
        val priceOk = when (price) {
            PriceTier.Any -> true
            PriceTier.Cheap, PriceTier.Mid, PriceTier.Pricy, PriceTier.Lux -> r.price == price.symbol
        }
        val distOk = parseDistance(r.distance) <= distance
        val rateOk = r.rating >= rating.value
        // No open-hours field on the model — when toggled, treat all mock data as open.
        val openOk = !openNow || true
        cuisineOk && priceOk && distOk && rateOk && openOk
    }
}

private fun parseDistance(value: String): Float =
    value.substringBefore(" ").toFloatOrNull() ?: 0f
