package com.mh.restaurantchainreservation.feature.wishlist.ui

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.components.shimmer
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButton
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButtonSize
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButtonStyle
import com.mh.restaurantchainreservation.core.designsystem.components.PressableContentScale
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.Restaurant
import com.mh.restaurantchainreservation.core.model.WishlistStore

/** Matches discover search results hero aspect ([DiscoverRestaurantImageAspectWidthOverHeight]). */
private const val WishlistResultImageAspect = 1.0527f

/**
 * Grid tile for Recently Viewed — heart when browsing; close control in edit mode.
 * Pass [Modifier.animateItem] from the lazy grid for smooth reflow when an item is removed.
 */
@Composable
fun RecentlyViewedGridItem(
    restaurant: Restaurant,
    editing: Boolean,
    onOpen: () -> Unit,
    onRemoveFromRecentlyViewed: () -> Unit,
    onHeartTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val savedIds by WishlistStore.savedRestaurantIds.collectAsState()
    val saved = restaurant.id in savedIds

    PressableContentScale(
        onClick = onOpen,
        enabled = !editing,
        modifier = modifier.fillMaxWidth(),
    ) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .shimmer(shape = RoundedCornerShape(16.dp))
                .background(palette.mutedSurface),
        ) {
            AsyncImage(
                model = restaurant.image,
                contentDescription = restaurant.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            if (editing) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(RestaurantColors.Overlay.veilFrosted)
                        .clickable(onClick = onRemoveFromRecentlyViewed),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Remove from Recently Viewed",
                        tint = palette.foreground,
                        modifier = Modifier.size(16.dp),
                    )
                }
            } else {
                HeartButton(
                    active = saved,
                    onClick = onHeartTap,
                    size = HeartButtonSize.Medium,
                    style = HeartButtonStyle.Overlay,
                    overlayContentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = restaurant.name,
            color = palette.foreground,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = restaurant.cuisine,
                color = palette.mutedForeground,
                fontSize = 12.sp,
                maxLines = 1,
                modifier = Modifier.weight(1f, fill = false),
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.size(6.dp))
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = palette.brand,
                modifier = Modifier.size(11.dp),
            )
            Spacer(Modifier.size(2.dp))
            Text(
                text = "%.1f".format(restaurant.rating),
                color = palette.mutedForeground,
                fontSize = 12.sp,
            )
        }
    }
    }
}

/**
 * Vertical card matching [RestaurantResultCard] on the search results sheet.
 */
@Composable
fun WishlistRestaurantResultCard(
    restaurant: Restaurant,
    onClick: () -> Unit,
    onHeartTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val savedIds by WishlistStore.savedRestaurantIds.collectAsState()
    val unsavedInDetail by WishlistStore.unsavedInDetailCollection.collectAsState()
    val saved = remember(savedIds, unsavedInDetail, restaurant.id) {
        WishlistStore.isSaved(restaurant.id)
    }

    PressableContentScale(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(WishlistResultImageAspect)
                    .clip(RoundedCornerShape(16.dp))
                    .shimmer(shape = RoundedCornerShape(16.dp))
                    .background(palette.mutedSurface),
            ) {
                AsyncImage(
                    model = restaurant.image,
                    contentDescription = restaurant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                restaurant.tag?.takeIf { it.isNotBlank() }?.let { tag ->
                    com.mh.restaurantchainreservation.core.designsystem.badge.RestaurantCardTagChip(
                        text = tag,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp),
                    )
                }
                HeartButton(
                    active = saved,
                    onClick = onHeartTap,
                    size = HeartButtonSize.Large,
                    style = HeartButtonStyle.Overlay,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                )
            }
            Row(
                modifier = Modifier.padding(top = 12.dp),
                verticalAlignment = Alignment.Top,
            ) {
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
                        text = "${restaurant.cuisine} - ${restaurant.area ?: restaurant.distance}",
                        color = palette.mutedForeground,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "Tables tonight - ${restaurant.price} for tonight",
                        color = palette.mutedForeground,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = RestaurantColors.Semantic.starGold,
                        modifier = Modifier.size(15.dp),
                    )
                    Spacer(Modifier.size(3.dp))
                    Text(
                        text = "%.1f".format(restaurant.rating),
                        color = palette.foreground,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}
