package com.mh.restaurantchainreservation.core.designsystem.badge

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.mh.restaurantchainreservation.core.designsystem.R
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette

enum class GuestFavoriteLaurelTier {
    None,
    Normal,
    High,
}

fun guestFavoriteDescription(tier: GuestFavoriteLaurelTier): String? = when (tier) {
    GuestFavoriteLaurelTier.High ->
        "This restaurant is in the top 5% of eligible listings based on ratings, reviews, and reliability."
    GuestFavoriteLaurelTier.Normal ->
        "Guests love this spot for consistent quality, great reviews, and dependable reservations."
    GuestFavoriteLaurelTier.None -> null
}

@Composable
fun GuestFavoriteLaurel(
    tier: GuestFavoriteLaurelTier,
    modifier: Modifier = Modifier,
    height: Dp = 48.dp,
    mirror: Boolean = false,
) {
    if (tier == GuestFavoriteLaurelTier.None) return

    val context = LocalContext.current
    val drawableId = when (tier) {
        GuestFavoriteLaurelTier.High -> R.drawable.goldenleaf
        GuestFavoriteLaurelTier.Normal -> R.drawable.leaf
        GuestFavoriteLaurelTier.None -> return
    }

    val laurelModifier = modifier
        .height(height)
        .width(height * (18f / 31f))
        .graphicsLayer {
            scaleX = if (mirror) -1f else 1f
        }

    if (tier == GuestFavoriteLaurelTier.High) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context).data(drawableId).build(),
            ),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = laurelModifier,
        )
    } else {
        Image(
            painter = painterResource(drawableId),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = laurelModifier,
        )
    }
}

@Composable
fun GuestFavoriteCenterBadge(
    tier: GuestFavoriteLaurelTier,
    modifier: Modifier = Modifier,
    laurelHeight: Dp = 36.dp,
    titleSize: TextUnit = 16.sp,
) {
    if (tier == GuestFavoriteLaurelTier.None) return
    val palette = LocalRestaurantPalette.current

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        GuestFavoriteLaurel(tier = tier, height = laurelHeight)
        Column(
            modifier = Modifier.padding(horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Guest",
                color = palette.foreground,
                fontSize = titleSize,
                fontWeight = FontWeight.Bold,
                lineHeight = titleSize * 1.1f,
            )
            Text(
                text = "favorite",
                color = palette.foreground,
                fontSize = titleSize,
                fontWeight = FontWeight.Bold,
                lineHeight = titleSize * 1.1f,
            )
        }
        GuestFavoriteLaurel(tier = tier, height = laurelHeight, mirror = true)
    }
}

@Composable
fun GuestFavoriteRatingLaurelRow(
    tier: GuestFavoriteLaurelTier,
    ratingText: String,
    modifier: Modifier = Modifier,
    ratingFontSize: TextUnit = 56.sp,
    laurelHeight: Dp = 72.dp,
) {
    val palette = LocalRestaurantPalette.current
    val showLaurels = tier != GuestFavoriteLaurelTier.None

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (showLaurels) {
            GuestFavoriteLaurel(tier = tier, height = laurelHeight)
        }
        Text(
            text = ratingText,
            color = palette.foreground,
            fontSize = ratingFontSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = if (showLaurels) 8.dp else 0.dp),
        )
        if (showLaurels) {
            GuestFavoriteLaurel(tier = tier, height = laurelHeight, mirror = true)
        }
    }
}
