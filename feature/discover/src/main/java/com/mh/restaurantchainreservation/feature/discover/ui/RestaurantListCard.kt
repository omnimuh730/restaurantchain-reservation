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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import com.mh.restaurantchainreservation.core.designsystem.components.icons.RestaurantIcons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.badge.DiscoverRestaurantCardBadgeChip
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButton
import com.mh.restaurantchainreservation.core.designsystem.components.PressableContentScale
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButtonSize
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButtonStyle
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.transition.LocalAnimatedContentScope
import com.mh.restaurantchainreservation.core.designsystem.transition.LocalRestaurantSharedTransitionScope
import com.mh.restaurantchainreservation.core.designsystem.transition.RestaurantCardHeroChromeLayer
import com.mh.restaurantchainreservation.core.designsystem.transition.RestaurantSharedTransitionShapes
import com.mh.restaurantchainreservation.core.designsystem.transition.RestaurantSharedTitleRole
import com.mh.restaurantchainreservation.core.designsystem.transition.rememberRestaurantCardContentMetaAlpha
import com.mh.restaurantchainreservation.core.designsystem.transition.rememberRestaurantHeroChromeAlpha
import com.mh.restaurantchainreservation.core.designsystem.transition.rememberRestaurantSharedContentPanelModifier
import com.mh.restaurantchainreservation.core.designsystem.transition.rememberRestaurantSharedHeroModifier
import com.mh.restaurantchainreservation.core.designsystem.transition.rememberRestaurantSharedTitleVisibilityModifier
import com.mh.restaurantchainreservation.core.designsystem.transition.restaurantSharedContentPanelLayer
import com.mh.restaurantchainreservation.core.model.Restaurant
import com.mh.restaurantchainreservation.core.model.RestaurantTimeSlot
import com.mh.restaurantchainreservation.core.model.WishlistStore


/**
 * Discover list card — hero at [DiscoverRestaurantImageAspectWidthOverHeight], title,
 * address row (pin + address | 5 stars + score), reviews line, heart on image,
 * and optional reservation time chips.
 */
@Composable
fun RestaurantListCard(
    restaurant: Restaurant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    timeSlots: List<RestaurantTimeSlot>? = null,
    listHorizontalPadding: Dp = DiscoverListHorizontalPadding,
) {
    val palette = LocalRestaurantPalette.current
    val savedIds by WishlistStore.savedRestaurantIds.collectAsState()
    val saved = restaurant.id in savedIds
    val shared = LocalRestaurantSharedTransitionScope.current
    val animatedContent = LocalAnimatedContentScope.current
    val heroModifier = rememberRestaurantSharedHeroModifier(
        restaurant.id,
        shared,
        animatedContent,
        shape = RestaurantSharedTransitionShapes.cardHero,
    )
    val titleVisibilityModifier = rememberRestaurantSharedTitleVisibilityModifier(
        restaurantId = restaurant.id,
        sharedTransitionScope = shared,
        animatedVisibilityScope = animatedContent,
        role = RestaurantSharedTitleRole.Card,
    )
    val contentPanelModifier = rememberRestaurantSharedContentPanelModifier(
        restaurant.id,
        shared,
        animatedContent,
    )
    val contentMetaAlpha = rememberRestaurantCardContentMetaAlpha(restaurant.id, shared)
    val heroChromeAlpha = rememberRestaurantHeroChromeAlpha(restaurant.id, shared)
    val hasTimeSlots = !timeSlots.isNullOrEmpty()
    Column(modifier = modifier.fillMaxWidth()) {
        PressableContentScale(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.33f),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(heroModifier),
                    ) {
                        AsyncImage(
                            model = restaurant.image,
                            contentDescription = restaurant.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                        RestaurantCardHeroChromeLayer(
                            modifier = Modifier.graphicsLayer { alpha = heroChromeAlpha },
                        ) {
                            HeartButton(
                                active = saved,
                                onClick = { WishlistStore.onHeartTap(restaurant) },
                                size = HeartButtonSize.Medium,
                                style = HeartButtonStyle.Overlay,
                                overlayContentAlignment = Alignment.TopCenter,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(10.dp),
                            )
                            DiscoverRestaurantCardBadgeChip(
                                restaurant = restaurant,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(10.dp),
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .restaurantSharedContentPanelLayer(restaurant.id, shared)
                        .then(contentPanelModifier)
                        .padding(
                            start = 6.dp,
                            end = 6.dp,
                            top = 6.dp,
                            bottom = if (hasTimeSlots) 0.dp else 6.dp,
                        ),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = restaurant.name,
                            color = palette.foreground,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = titleVisibilityModifier.weight(1f),
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.graphicsLayer { alpha = contentMetaAlpha },
                        ) {
                            Icon(
                                imageVector = RestaurantIcons.Star,
                                contentDescription = null,
                                tint = palette.foreground,
                                modifier = Modifier.size(12.dp),
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "%.1f (%s)".format(restaurant.rating, formatReviewCount(restaurant.reviews)),
                                color = palette.foreground,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = listCardAddressLine(restaurant),
                        color = palette.mutedForeground,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.graphicsLayer { alpha = contentMetaAlpha },
                    )
                }
            }
        }
        if (hasTimeSlots) {
            TimeSlotRow(
                slots = timeSlots,
                onSlotClick = { slot ->
                    if (slot.available) onClick()
                },
                horizontalPadding = listHorizontalPadding,
                modifier = Modifier
                    .horizontalBleed(listHorizontalPadding)
                    .padding(top = 6.dp, bottom = 14.dp),
            )
        }
    }
}

/** Extends [horizontalPadding] past the list card so time chips scroll edge-to-edge on screen. */
private fun Modifier.horizontalBleed(horizontalPadding: Dp): Modifier = layout { measurable, constraints ->
    val padPx = horizontalPadding.roundToPx()
    val placeable = measurable.measure(
        constraints.copy(maxWidth = constraints.maxWidth + padPx * 2),
    )
    layout(constraints.maxWidth, placeable.height) {
        placeable.place(-padPx, 0)
    }
}

@Composable
private fun TimeSlotRow(
    slots: List<RestaurantTimeSlot>,
    onSlotClick: (RestaurantTimeSlot) -> Unit,
    horizontalPadding: Dp,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(slots, key = { it.label }) { slot ->
            val borderColor = if (slot.available) palette.foreground else palette.border
            val bg = palette.cardSurface
            val textColor = if (slot.available) palette.foreground else palette.mutedForeground.copy(alpha = 0.55f)
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
                    .padding(horizontal = 14.dp, vertical = 6.dp),
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
