package com.mh.restaurantchainreservation.feature.profile.hub

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

@Composable
fun ProfileTopCard(
    selectedAvatarUrl: String?,
    onOpenAvatarPicker: () -> Unit,
    onOpenTierDetails: () -> Unit,
    isPro: Boolean,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val displayName = stringResource(I18nR.string.profile_display_name)
    val handle = stringResource(I18nR.string.profile_handle)
    val tierProLabel = stringResource(I18nR.string.profile_tier_pro)
    val tierFreeLabel = stringResource(I18nR.string.profile_tier_free)
    val avatarAria = stringResource(I18nR.string.profile_avatar_aria, displayName)

    val cardShape = RoundedCornerShape(24.dp)

    Box(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = cardShape, ambientColor = Color.Black.copy(alpha = 0.18f))
            .clip(cardShape)
            .background(palette.cardSurface)
            .border(1.dp, palette.border.copy(alpha = 0.5f), cardShape)
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AvatarBlock(
                avatarUrl = selectedAvatarUrl,
                displayName = displayName,
                isPro = isPro,
                tierProLabel = tierProLabel,
                tierFreeLabel = tierFreeLabel,
                avatarAria = avatarAria,
                onClick = onOpenAvatarPicker,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = displayName,
                color = palette.foreground,
                fontSize = 22.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = handle,
                color = palette.mutedForeground,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.height(12.dp))
            TierProgressCard(onClick = onOpenTierDetails)
        }
    }
}

@Composable
private fun AvatarBlock(
    avatarUrl: String?,
    displayName: String,
    isPro: Boolean,
    tierProLabel: String,
    tierFreeLabel: String,
    avatarAria: String,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .size(112.dp)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(112.dp)
                .clip(CircleShape)
                .background(palette.mutedSurface)
                .border(2.dp, palette.cardSurface, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (avatarUrl != null) {
                val context = LocalContext.current
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(avatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = avatarAria,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(CircleShape),
                )
            } else {
                Text(
                    text = displayName.firstOrNull()?.uppercase() ?: "A",
                    color = palette.foreground,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 4.dp, bottom = 0.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(if (isPro) palette.brand else Color(0xFF374151))
                .border(2.dp, palette.cardSurface, RoundedCornerShape(999.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp),
        ) {
            Text(
                text = (if (isPro) tierProLabel else tierFreeLabel).uppercase(),
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
            )
        }
    }
}

@Composable
private fun TierProgressCard(onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val tierShape = RoundedCornerShape(16.dp)

    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 0.67f,
            animationSpec = tween(durationMillis = 1000, delayMillis = 200, easing = FastOutSlowInEasing),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(tierShape)
            .border(1.dp, palette.border.copy(alpha = 0.6f), tierShape)
            .background(palette.mutedSurface.copy(alpha = 0.6f))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(palette.gold.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WorkspacePremium,
                        contentDescription = null,
                        tint = palette.gold,
                        modifier = Modifier.size(14.dp),
                    )
                }
                Text(
                    text = stringResource(I18nR.string.profile_gold_tier),
                    color = palette.foreground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = stringResource(I18nR.string.profile_points_to_platinum),
                color = palette.mutedForeground,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(palette.border),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.value)
                    .height(6.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(palette.gold),
            )
        }
    }
}

@Suppress("unused")
@Composable
private fun WidthSpacer() {
    Spacer(Modifier.width(0.dp))
}
