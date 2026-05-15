package com.mh.restaurantchainreservation.feature.discover.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButton
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButtonSize
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButtonStyle
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.Restaurant
import com.mh.restaurantchainreservation.core.model.RestaurantTimeSlot
import com.mh.restaurantchainreservation.core.model.WishlistStore
import kotlin.math.roundToInt

/**
 * Discover list card — 16:9 hero, title, address row (pin + address | 5 stars + score),
 * reviews line, heart on image, and optional reservation time chips.
 */
@Composable
fun RestaurantListCard(
    restaurant: Restaurant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    timeSlots: List<RestaurantTimeSlot>? = null,
) {
    val palette = LocalRestaurantPalette.current
    val collections by WishlistStore.collections.collectAsState()
    val saved = collections.any { col -> col.restaurants.any { it.id == restaurant.id } }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(palette.cardSurface)
            .border(1.dp, palette.borderSoft, RoundedCornerShape(20.dp)),
    ) {
        Column(
            modifier = Modifier.clickable(onClick = onClick),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
            ) {
                AsyncImage(
                    model = restaurant.image,
                    contentDescription = restaurant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                HeartButton(
                    active = saved,
                    onClick = { WishlistStore.openPicker(restaurant) },
                    size = HeartButtonSize.Medium,
                    style = HeartButtonStyle.Overlay,
                    overlayContentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                )
                // Optional tag chip top-left.
                val tag = restaurant.tag
                if (!tag.isNullOrBlank()) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(palette.cardSurface.copy(alpha = 0.92f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = tag,
                            color = palette.foreground,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = restaurant.name,
                    color = palette.foreground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Place,
                            contentDescription = null,
                            tint = palette.mutedForeground,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = listCardAddressLine(restaurant),
                            color = palette.mutedForeground,
                            fontSize = 13.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    ListCardStarRating(rating = restaurant.rating)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${formatReviewCount(restaurant.reviews)} reviews",
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                    maxLines = 1,
                )
            }
        }
        val slots = timeSlots
        if (!slots.isNullOrEmpty()) {
            TimeSlotRow(
                slots = slots,
                onSlotClick = { slot ->
                    if (slot.available) onClick()
                },
                modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 14.dp, top = 2.dp),
            )
        }
    }
}

@Composable
private fun TimeSlotRow(
    slots: List<RestaurantTimeSlot>,
    onSlotClick: (RestaurantTimeSlot) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(slots, key = { it.label }) { slot ->
            val borderColor = if (slot.available) palette.brand.copy(alpha = 0.45f) else palette.border
            val bg = if (slot.available) palette.brandSoftSurface else palette.cardSurface
            val textColor = if (slot.available) palette.brandStrong else palette.mutedForeground.copy(alpha = 0.55f)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(bg)
                    .border(1.dp, borderColor, RoundedCornerShape(999.dp))
                    .then(
                        if (slot.available) {
                            Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = { onSlotClick(slot) },
                            )
                        } else {
                            Modifier
                        },
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(
                    text = slot.label,
                    color = textColor,
                    fontSize = 13.sp,
                    fontWeight = if (slot.available) FontWeight.SemiBold else FontWeight.Medium,
                    textDecoration = if (slot.available) TextDecoration.None else TextDecoration.LineThrough,
                )
            }
        }
    }
}

/** Pin line: neighborhood/area when present, else cuisine, with distance like a short address. */
private fun listCardAddressLine(r: Restaurant): String {
    val area = r.area?.trim()?.takeIf { it.isNotBlank() }
    val dist = r.distance.trim()
    return when {
        !area.isNullOrBlank() -> "$area · $dist"
        else -> "${r.cuisine} · $dist"
    }
}

private fun formatReviewCount(count: Int): String =
    count.toString().reversed().chunked(3).joinToString(",").reversed()

@Composable
private fun ListCardStarRating(rating: Double) {
    val palette = LocalRestaurantPalette.current
    val goldStar = Color(0xFFEAB308)
    val emptyStar = palette.mutedForeground.copy(alpha = 0.35f)
    val filledStars = (rating + 0.25).roundToInt().coerceIn(0, 5)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = if (index < filledStars) goldStar else emptyStar,
                modifier = Modifier.size(12.dp),
            )
        }
        Spacer(Modifier.width(4.dp))
        Text(
            text = "%.1f".format(rating),
            color = palette.foreground,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
