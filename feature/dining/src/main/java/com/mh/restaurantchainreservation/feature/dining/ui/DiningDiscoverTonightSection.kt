package com.mh.restaurantchainreservation.feature.dining.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.feature.dining.data.DINING_DISCOVER_SPOTLIGHTS
import com.mh.restaurantchainreservation.feature.dining.data.DiningDiscoverSpotlight

@Composable
fun DiningDiscoverTonightSection(
    onViewAll: () -> Unit,
    onExplore: (DiningDiscoverSpotlight) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val pagerState = rememberPagerState(pageCount = { DINING_DISCOVER_SPOTLIGHTS.size })
    val outerShape = RoundedCornerShape(24.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(outerShape)
            .background(palette.brand.copy(alpha = 0.08f))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "✨ ${stringResource(I18nR.string.dining_discover_tonight)}",
                color = palette.brand,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(I18nR.string.dining_discover_view_all),
                color = palette.mutedForeground,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onViewAll)
                    .padding(horizontal = 4.dp, vertical = 2.dp),
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            DiscoverSpotlightCard(
                spotlight = DINING_DISCOVER_SPOTLIGHTS[page],
                onExplore = { onExplore(DINING_DISCOVER_SPOTLIGHTS[page]) },
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DINING_DISCOVER_SPOTLIGHTS.indices.forEach { index ->
                val active = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(if (active) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (active) palette.brand else palette.brand.copy(alpha = 0.22f),
                        ),
                )
            }
        }
    }
}

@Composable
private fun DiscoverSpotlightCard(
    spotlight: DiningDiscoverSpotlight,
    onExplore: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val cardShape = RoundedCornerShape(20.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(palette.cardSurface)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = spotlight.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(16.dp)),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .background(palette.brand.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text(
                    text = stringResource(I18nR.string.dining_discover_trending),
                    color = palette.brand,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.6.sp,
                )
            }
            Text(
                text = spotlight.title,
                color = palette.foreground,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
            )
            Text(
                text = spotlight.placesLabel,
                color = palette.mutedForeground,
                fontSize = 12.sp,
            )
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .border(1.dp, palette.brand.copy(alpha = 0.45f), RoundedCornerShape(percent = 50))
                    .clickable(onClick = onExplore)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(I18nR.string.dining_discover_explore),
                    color = palette.brand,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = palette.brand,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}
