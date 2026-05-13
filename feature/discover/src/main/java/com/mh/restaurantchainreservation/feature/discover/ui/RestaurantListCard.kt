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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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

/**
 * Standard restaurant card — 16:9 hero image, title row, rating chip, price ·
 * distance, heart toggle, and optional horizontal reservation time slots.
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = restaurant.name,
                        color = palette.foreground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                    )
                    RatingChip(rating = restaurant.rating)
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = restaurant.cuisine,
                    color = palette.mutedForeground,
                    fontSize = 13.sp,
                    maxLines = 1,
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = restaurant.price,
                        color = palette.foreground,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Dot()
                    Text(
                        text = "${restaurant.reviews} reviews",
                        color = palette.mutedForeground,
                        fontSize = 12.sp,
                    )
                    Dot()
                    Icon(
                        imageVector = Icons.Outlined.Place,
                        contentDescription = null,
                        tint = palette.mutedForeground,
                        modifier = Modifier.size(13.dp),
                    )
                    Spacer(Modifier.size(2.dp))
                    Text(
                        text = restaurant.distance,
                        color = palette.mutedForeground,
                        fontSize = 12.sp,
                    )
                }
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

@Composable
private fun RatingChip(rating: Double) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(palette.brandSoftSurface)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = palette.brand,
            modifier = Modifier.size(12.dp),
        )
        Spacer(Modifier.size(3.dp))
        Text(
            text = "%.1f".format(rating),
            color = palette.brand,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun Dot() {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .padding(horizontal = 6.dp)
            .size(3.dp)
            .clip(CircleShape)
            .background(palette.mutedForeground.copy(alpha = 0.6f)),
    )
}
