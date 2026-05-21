package com.mh.restaurantchainreservation.feature.wishlist.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.components.HeartDrawableIcon
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.WishlistToastKind
import com.mh.restaurantchainreservation.core.model.WishlistToastState

/**
 * Bottom-anchored "Saved to <Collection>" toast. Shown after a wishlist save.
 * Auto-dismiss is owned by the host (WishlistOverlayHost) — this composable
 * just animates the visibility of the card.
 */
@Composable
fun WishlistSavedToast(
    toast: WishlistToastState?,
    onChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Bottom),
        contentAlignment = Alignment.BottomCenter,
    ) {
        AnimatedVisibility(
            visible = toast != null,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(dampingRatio = 0.79f, stiffness = 360f),
            ) + fadeIn(tween(200)) + scaleIn(initialScale = 0.96f, animationSpec = tween(200)),
            exit = slideOutVertically(targetOffsetY = { it / 2 }, animationSpec = tween(180)) +
                fadeOut(tween(180)) + scaleOut(targetScale = 0.96f, animationSpec = tween(180)),
        ) {
            if (toast != null) {
                ToastCard(
                    toast = toast,
                    onChange = if (toast.kind == WishlistToastKind.Saved) onChange else null,
                )
            }
        }
    }
}

@Composable
private fun ToastCard(toast: WishlistToastState, onChange: (() -> Unit)?) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth()
            .widthIn(max = 464.dp)
            .shadow(12.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(palette.cardSurface)
            .border(1.dp, palette.borderSoft, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Thumbnail with red heart badge (asset matches in-card wishlist hearts — no circular chip).
        Box(modifier = Modifier.size(56.dp)) {
            AsyncImage(
                model = toast.restaurant.image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp)),
            )
            HeartDrawableIcon(
                active = toast.kind == WishlistToastKind.Saved,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp),
                iconHeight = 14.dp,
            )
        }
        Spacer(Modifier.size(12.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            val headline = buildAnnotatedString {
                when (toast.kind) {
                    WishlistToastKind.Saved -> {
                        append("Saved to ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = palette.foreground)) {
                            append(toast.collectionTitle)
                        }
                    }
                    WishlistToastKind.Removed -> {
                        append("Removed from ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = palette.foreground)) {
                            append(toast.collectionTitle)
                        }
                    }
                }
            }
            Text(
                text = headline,
                color = palette.foreground,
                fontSize = 14.sp,
                maxLines = 1,
            )
            Text(
                text = toast.restaurant.name,
                color = palette.mutedForeground,
                fontSize = 12.sp,
                maxLines = 1,
            )
        }
        if (onChange != null) {
            Spacer(Modifier.size(8.dp))
            Text(
                text = "Change",
                color = palette.foreground,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onChange() }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            )
        }
    }
}
