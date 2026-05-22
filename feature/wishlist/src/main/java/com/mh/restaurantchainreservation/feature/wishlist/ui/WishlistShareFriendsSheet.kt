package com.mh.restaurantchainreservation.feature.wishlist.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.WishlistCollection
import kotlinx.coroutines.launch

@Composable
fun WishlistShareFriendsSheet(
    collection: WishlistCollection,
    onDismiss: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val context = LocalContext.current
    val backdropAlpha = remember { Animatable(0f) }
    val sheetOffset = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    var dismissing by remember { mutableStateOf(false) }

    val triggerDismiss: () -> Unit = remember {
        {
            if (!dismissing) {
                dismissing = true
                scope.launch {
                    launch { backdropAlpha.animateTo(0f, tween(180)) }
                    launch { sheetOffset.animateTo(1f, tween(220)) }
                    onDismiss()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        launch { backdropAlpha.animateTo(1f, tween(220, easing = LinearOutSlowInEasing)) }
        launch {
            sheetOffset.animateTo(0f, spring(dampingRatio = 0.85f, stiffness = 270f))
        }
    }

    Dialog(
        onDismissRequest = triggerDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
    ) {
        val density = LocalDensity.current
        val sheetTranslateY = with(density) { sheetOffset.value * 520.dp.toPx() }

        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = backdropAlpha.value * 0.4f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) { triggerDismiss() },
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .graphicsLayer { translationY = sheetTranslateY }
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(palette.cardSurface)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) {}
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = 12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Share wishlist",
                            color = palette.foreground,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "Send \"${collection.title}\" to a friend",
                            color = palette.mutedForeground,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { triggerDismiss() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = palette.foreground,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
                HorizontalDivider(color = palette.border)
                WishlistShareFriends.forEach { friend ->
                    FriendShareRow(
                        friend = friend,
                        onSend = {
                            shareWishlistToFriend(context, collection, friend)
                            triggerDismiss()
                        },
                    )
                    HorizontalDivider(color = palette.border)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            shareWishlistWithSystemChooser(context, collection)
                            triggerDismiss()
                        }
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.IosShare,
                        contentDescription = null,
                        tint = palette.foreground,
                        modifier = Modifier.size(22.dp),
                    )
                    Text(
                        text = "Share another way",
                        color = palette.foreground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
private fun FriendShareRow(
    friend: WishlistShareFriend,
    onSend: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val initial = friend.name.firstOrNull()?.uppercase() ?: "?"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(palette.mutedSurface)
                .border(1.dp, palette.border, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initial,
                color = palette.foreground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = friend.name,
                color = palette.foreground,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = friend.handle,
                color = palette.mutedForeground,
                fontSize = 13.sp,
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(palette.foreground)
                .clickable(onClick = onSend)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(
                text = "Send",
                color = palette.cardSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
