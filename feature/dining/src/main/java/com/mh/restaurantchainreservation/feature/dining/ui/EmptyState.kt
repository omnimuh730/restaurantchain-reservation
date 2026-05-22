package com.mh.restaurantchainreservation.feature.dining.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import com.mh.restaurantchainreservation.core.designsystem.components.HubSurfaceCardDefaults
import com.mh.restaurantchainreservation.core.designsystem.components.hubSurfaceShadow
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

@Composable
fun DiningNoItemsCard(
    onExploreRestaurants: () -> Unit,
    onAddBooking: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val cardShape = HubSurfaceCardDefaults.Shape

    DiningHeaderFadeIn {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .hubSurfaceShadow(shape = cardShape)
                .clip(cardShape)
                .background(palette.cardSurface)
                .border(1.dp, palette.brand.copy(alpha = 0.22f), cardShape)
                .padding(
                    horizontal = HubSurfaceCardDefaults.ContentPadding,
                    vertical = 32.dp,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DiningNoItemsScanningMagnifier()
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(I18nR.string.dining_no_items_title),
                color = palette.foreground,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(I18nR.string.dining_no_items_desc),
                color = palette.mutedForeground,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
            ChipButton(
                text = stringResource(I18nR.string.dining_no_items_explore),
                icon = Icons.Outlined.Store,
                onClick = onExploreRestaurants,
                modifier = Modifier.fillMaxWidth(),
                variant = ChipVariant.Primary,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            )
            Spacer(Modifier.height(10.dp))
            DiningNoItemsOrDivider()
            Spacer(Modifier.height(10.dp))
            ChipButton(
                text = stringResource(I18nR.string.dining_no_items_add_booking),
                icon = null,
                onClick = onAddBooking,
                modifier = Modifier.fillMaxWidth(),
                variant = ChipVariant.BrandOutline,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            )
        }
    }
}

@Composable
private fun DiningNoItemsScanningMagnifier() {
    val palette = LocalRestaurantPalette.current
    val lensSize = 56.dp
    val lensSizePx = with(LocalDensity.current) { lensSize.roundToPx().toFloat() }

    val infinite = rememberInfiniteTransition(label = "no_items_scan")
    val scanProgress by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1_800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "scan_progress",
    )
    val glassScale by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1_400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glass_scale",
    )
    val scanAlpha by infinite.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scan_alpha",
    )

    Box(
        modifier = Modifier.size(80.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(palette.brand.copy(alpha = 0.07f)),
        )
        Box(
            modifier = Modifier
                .size(lensSize)
                .clip(CircleShape)
                .background(palette.mutedSurface.copy(alpha = 0.65f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = palette.brand,
                modifier = Modifier
                    .size(30.dp)
                    .graphicsLayer {
                        scaleX = glassScale
                        scaleY = glassScale
                    },
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
            ) {
                val beamY = lensSizePx * scanProgress
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .offset { IntOffset(0, (beamY - 1.5f).roundToInt()) }
                        .graphicsLayer { alpha = scanAlpha }
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    palette.brand.copy(alpha = 0f),
                                    palette.brand.copy(alpha = 0.85f),
                                    palette.brand.copy(alpha = 0f),
                                ),
                            ),
                        ),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .offset { IntOffset(0, (beamY - 6f).roundToInt()) }
                        .graphicsLayer { alpha = scanAlpha * 0.45f }
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    palette.brand.copy(alpha = 0f),
                                    palette.brand.copy(alpha = 0.5f),
                                    palette.brand.copy(alpha = 0f),
                                ),
                            ),
                        ),
                )
            }
        }
    }
}

@Composable
private fun DiningNoItemsOrDivider() {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = palette.border,
        )
        Text(
            text = stringResource(I18nR.string.dining_no_items_or),
            color = palette.mutedForeground,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp),
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = palette.border,
        )
    }
}
