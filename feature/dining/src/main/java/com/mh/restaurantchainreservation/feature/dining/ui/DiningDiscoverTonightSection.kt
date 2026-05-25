package com.mh.restaurantchainreservation.feature.dining.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.components.HubSurfaceCardDefaults
import com.mh.restaurantchainreservation.core.designsystem.components.PremiumPinkSheetColors
import com.mh.restaurantchainreservation.core.designsystem.components.hubSurfaceShadow
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.core.model.Banner
import com.mh.restaurantchainreservation.core.model.DiscoverData
import kotlinx.coroutines.delay

private val DiscoverTonightCardHeight = 220.dp
private val DiscoverTonightCardShape = RoundedCornerShape(24.dp)
private val DiscoverTonightShellShape = HubSurfaceCardDefaults.Shape

private fun Modifier.discoverTonightShellBackground(): Modifier =
    this
        .hubSurfaceShadow(shape = DiscoverTonightShellShape)
        .clip(DiscoverTonightShellShape)
        .drawBehind {
            val linear = Brush.linearGradient(
                0f to RestaurantColors.Base.white,
                0.45f to PremiumPinkSheetColors.LightPink,
                1f to PremiumPinkSheetColors.SoftPink,
                start = Offset.Zero,
                end = Offset(size.width, size.height),
            )
            drawRect(brush = linear)

            val radial = Brush.radialGradient(
                0f to PremiumPinkSheetColors.RadialHighlight,
                0.4f to Color.Transparent,
                center = Offset.Zero,
                radius = size.minDimension * 0.72f,
            )
            drawRect(brush = radial)
        }

@Composable
fun DiningDiscoverTonightSection(
    onViewAll: () -> Unit,
    onBannerClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    banners: List<Banner> = DiscoverData.BANNERS,
) {
    val palette = LocalRestaurantPalette.current
    val pagerState = rememberPagerState(pageCount = { banners.size.coerceAtLeast(1) })

    LaunchedEffect(pagerState, banners.size) {
        if (banners.size <= 1) return@LaunchedEffect
        while (true) {
            delay(4_000L)
            val next = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(next)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .discoverTonightShellBackground()
            .padding(HubSurfaceCardDefaults.ContentPadding),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "✨",
                        fontSize = 16.sp,
                    )
                    Text(
                        text = stringResource(I18nR.string.dining_discover_tonight),
                        color = palette.foreground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
                Text(
                    text = stringResource(I18nR.string.dining_discover_subtitle),
                    color = palette.mutedForeground,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 18.sp,
                )
            }
            Spacer(Modifier.width(12.dp))
            DiscoverTonightViewAllButton(onClick = onViewAll)
        }

        if (banners.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DiscoverTonightCardHeight)
                    .clip(DiscoverTonightCardShape),
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                ) { page ->
                    DiscoverTonightBannerCard(
                        banner = banners[page],
                        onClick = { onBannerClick(banners[page].id) },
                    )
                }
                DiscoverTonightPagerDots(
                    pageCount = banners.size,
                    currentPage = pagerState.currentPage,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 14.dp),
                )
            }
        }
    }
}

@Composable
private fun DiscoverTonightViewAllButton(onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(percent = 50)
    Row(
        modifier = Modifier
            .clip(shape)
            .border(1.dp, palette.border, shape)
            .background(palette.cardSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = stringResource(I18nR.string.dining_discover_view_all),
            color = palette.brand,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = palette.brand,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun DiscoverTonightBannerCard(
    banner: Banner,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick),
    ) {
        AsyncImage(
            model = banner.image,
            contentDescription = banner.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                RestaurantColors.Base.black.copy(alpha = 0.88f),
                                RestaurantColors.Base.black.copy(alpha = 0.55f),
                                Color.Transparent,
                            ),
                            startX = 0f,
                            endX = constraints.maxWidth * 0.72f,
                        ),
                    ),
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth(0.72f)
                .padding(start = 18.dp, end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .background(RestaurantColors.Base.black.copy(alpha = 0.42f))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocalFireDepartment,
                    contentDescription = null,
                    tint = palette.brand,
                    modifier = Modifier.size(12.dp),
                )
                Text(
                    text = stringResource(I18nR.string.dining_discover_trending_now),
                    color = RestaurantColors.Base.white,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp,
                )
            }
            Text(
                text = banner.title,
                color = RestaurantColors.Base.white,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 26.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = banner.subtitle,
                color = RestaurantColors.Overlay.imageCaption,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = RestaurantColors.Overlay.imageCaption,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = banner.cta,
                    color = RestaurantColors.Overlay.imageCaption,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFE85D4C),
                                palette.brand,
                            ),
                        ),
                    )
                    .clickable(onClick = onClick)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(I18nR.string.dining_discover_explore_places),
                    color = RestaurantColors.Base.white,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = RestaurantColors.Base.white,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun DiscoverTonightPagerDots(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    if (pageCount <= 1) return
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount.coerceAtMost(5)) { index ->
            val active = index == currentPage
            Box(
                modifier = Modifier
                    .then(
                        if (active) {
                            Modifier
                                .width(18.dp)
                                .height(6.dp)
                        } else {
                            Modifier.size(6.dp)
                        },
                    )
                    .clip(CircleShape)
                    .background(
                        if (active) {
                            palette.brand
                        } else {
                            RestaurantColors.Base.white.copy(alpha = 0.45f)
                        },
                    ),
            )
        }
    }
}
