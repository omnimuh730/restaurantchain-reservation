package com.mh.restaurantchainreservation.feature.booking

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.badge.GuestFavoriteCenterBadge
import com.mh.restaurantchainreservation.core.designsystem.badge.GuestFavoriteRatingLaurelRow
import com.mh.restaurantchainreservation.core.designsystem.badge.guestFavoriteDescription
import com.mh.restaurantchainreservation.core.designsystem.components.HeartDrawableIcon
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.transition.LocalAnimatedContentScope
import com.mh.restaurantchainreservation.core.designsystem.transition.LocalRestaurantSharedTransitionScope
import com.mh.restaurantchainreservation.core.designsystem.transition.rememberRestaurantSharedHeroModifier
import com.mh.restaurantchainreservation.core.designsystem.transition.rememberRestaurantSharedTitleModifier
import com.mh.restaurantchainreservation.core.model.Restaurant
import com.mh.restaurantchainreservation.core.model.withDerivedGuestFavoriteLevel
import java.text.NumberFormat
import java.util.Locale

private val SheetTopRadius = 34.dp
private val HeroHeight = 288.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RestaurantDetailScreen(
    restaurantId: String,
    onBack: () -> Unit,
    onBookNow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val restaurant = remember(restaurantId) {
        com.mh.restaurantchainreservation.core.model.DiscoverData.findById(restaurantId)
            ?: com.mh.restaurantchainreservation.core.model.DiscoverData.MONTHLY_BEST.first()
                .withDerivedGuestFavoriteLevel()
    }
    val ext = remember(restaurant) { RestaurantDetailData.extendedData(restaurant) }
    val gallery = remember(restaurant) { RestaurantDetailData.galleryImages(restaurant) }
    val topReviews = remember { RestaurantDetailData.reviews.take(10) }
    var saved by remember { mutableStateOf(false) }
    var showReviews by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showAmenities by remember { mutableStateOf(false) }
    var galleryFullscreenIndex by remember { mutableStateOf<Int?>(null) }
    var headerSolid by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val headerSolidDerived by remember {
        derivedStateOf { scrollState.value > 48 }
    }
    androidx.compose.runtime.LaunchedEffect(headerSolidDerived) {
        headerSolid = headerSolidDerived
    }

    val shared = LocalRestaurantSharedTransitionScope.current
    val animatedContent = LocalAnimatedContentScope.current
    val titleModifier = rememberRestaurantSharedTitleModifier(restaurant.id, shared, animatedContent)
    val bodyReveal = remember { Animatable(0f) }
    val density = LocalDensity.current
    val bodySlidePx = remember(density) { with(density) { 28.dp.toPx() } }
    LaunchedEffect(restaurantId) {
        bodyReveal.snapTo(0f)
        bodyReveal.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 420, delayMillis = 96, easing = FastOutSlowInEasing),
        )
    }

    Box(modifier = modifier.fillMaxSize().background(palette.cardSurface)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
        ) {
            HeroCarousel(
                restaurantId = restaurant.id,
                galleryImages = gallery,
                restaurantName = restaurant.name,
                onOpenFullscreen = { index -> galleryFullscreenIndex = index },
            )
            HeaderSummaryCard(restaurant = restaurant, ext = ext, titleModifier = titleModifier)
            Column(
                modifier = Modifier.graphicsLayer {
                    val p = bodyReveal.value
                    translationY = (1f - p) * bodySlidePx
                    alpha = 0.22f + 0.78f * p
                },
            ) {
                RatingsSummaryRow(restaurant = restaurant, onOpenReviews = { showReviews = true })
                HighlightsSection(restaurant = restaurant)
                AboutSection(ext = ext)
                AmenitiesSection(
                    restaurant = restaurant,
                    ext = ext,
                    onShowAll = { showAmenities = true },
                )
                LocationSection(restaurant = restaurant, ext = ext)
                if (restaurant.guestFavoriteLevel.isGuestFavorite()) {
                    GuestFavoriteSection(
                        restaurant = restaurant,
                        reviews = topReviews,
                        onOpenReviews = { showReviews = true },
                    )
                }
                CancellationPolicySection()
                PopularMenuSection(onShowMenu = { showMenu = true })
                Spacer(Modifier.height(120.dp))
            }
        }

        DetailTopBar(
            restaurantName = restaurant.name,
            solid = headerSolid,
            saved = saved,
            onBack = onBack,
            onToggleSave = { saved = !saved },
        )

        BookingBar(
            restaurant = restaurant,
            onBookNow = onBookNow,
            modifier = Modifier.align(Alignment.BottomCenter),
        )

        if (showReviews) {
            RestaurantReviewsScreen(
                restaurant = restaurant,
                onBack = { showReviews = false },
                modifier = Modifier.fillMaxSize(),
            )
        }
        if (showMenu) {
            RestaurantMenuScreen(
                restaurantName = restaurant.name,
                onBack = { showMenu = false },
                modifier = Modifier.fillMaxSize(),
            )
        }
        if (showAmenities) {
            RestaurantAmenitiesScreen(
                restaurant = restaurant,
                onBack = { showAmenities = false },
                modifier = Modifier.fillMaxSize(),
            )
        }

        galleryFullscreenIndex?.let { startIndex ->
            MenuImageFullscreenViewer(
                images = gallery,
                initialIndex = startIndex,
                onDismiss = { galleryFullscreenIndex = null },
            )
        }
    }
}

@Composable
private fun DetailTopBar(
    restaurantName: String,
    solid: Boolean,
    saved: Boolean,
    onBack: () -> Unit,
    onToggleSave: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val buttonBg = Color.White
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (solid) palette.cardSurface.copy(alpha = 0.95f) else Color.Transparent,
            )
            .then(
                if (solid) Modifier.border(1.dp, palette.borderSoft) else Modifier,
            )
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GlassCircleButton(onClick = onBack, background = buttonBg) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = palette.foreground, modifier = Modifier.size(20.dp))
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (solid) {
                    Text(
                        text = restaurantName,
                        color = palette.foreground,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GlassCircleButton(onClick = { }, background = buttonBg) {
                    Icon(Icons.Default.Share, "Share", tint = palette.foreground, modifier = Modifier.size(18.dp))
                }
                GlassCircleButton(onClick = onToggleSave, background = buttonBg) {
                    HeartDrawableIcon(active = saved, contentDescription = "Save", iconHeight = 20.dp)
                }
            }
        }
    }
}

@Composable
private fun GlassCircleButton(
    onClick: () -> Unit,
    background: Color,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HeroCarousel(
    restaurantId: String,
    galleryImages: List<String>,
    restaurantName: String,
    onOpenFullscreen: (Int) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { galleryImages.size })
    val shared = LocalRestaurantSharedTransitionScope.current
    val animatedContent = LocalAnimatedContentScope.current
    val heroSharedModifier = rememberRestaurantSharedHeroModifier(restaurantId, shared, animatedContent)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeroHeight),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            AsyncImage(
                model = galleryImages[page],
                contentDescription = restaurantName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onOpenFullscreen(page) }
                    .then(if (page == 0) heroSharedModifier else Modifier),
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Text(
                text = "${pagerState.currentPage + 1} / ${galleryImages.size}",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun HeaderSummaryCard(
    restaurant: Restaurant,
    ext: RestaurantExtendedData,
    titleModifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-24).dp)
            .clip(RoundedCornerShape(topStart = SheetTopRadius, topEnd = SheetTopRadius))
            .background(palette.cardSurface)
            .padding(horizontal = 24.dp, vertical = 24.dp),
    ) {
        Text(
            text = restaurant.name,
            color = palette.foreground,
            fontSize = 32.sp,
            lineHeight = 38.sp,
            fontWeight = FontWeight.Bold,
            modifier = titleModifier,
        )
        Text(
            text = "${restaurant.cuisine} restaurant in ${restaurant.distance} area",
            color = palette.mutedForeground,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 6.dp),
        )
        Text(
            text = "${restaurant.price} · Open until ${ext.closesAt}",
            color = palette.mutedForeground,
            fontSize = 16.sp,
        )
    }
    HorizontalDivider(color = palette.borderSoft)
}

@Composable
private fun RatingsSummaryRow(restaurant: Restaurant, onOpenReviews: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val laurelTier = restaurant.guestFavoriteLevel.toLaurelTier()
    val showBadge = restaurant.guestFavoriteLevel.isGuestFavorite()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenReviews)
            .padding(vertical = 24.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = formatRating(restaurant.rating),
                color = palette.foreground,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Row(modifier = Modifier.padding(top = 8.dp)) {
                repeat(5) {
                    Icon(Icons.Filled.Star, null, tint = palette.foreground, modifier = Modifier.size(14.dp))
                }
            }
        }
        if (showBadge) {
            RatingsSummaryDivider(palette = palette)
            GuestFavoriteCenterBadge(
                tier = laurelTier,
                modifier = Modifier.weight(1.35f),
                laurelHeight = 34.dp,
                titleSize = 15.sp,
            )
        }
        RatingsSummaryDivider(palette = palette)
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = restaurant.reviews.toString(),
                color = palette.foreground,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Reviews",
                color = palette.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
    HorizontalDivider(color = palette.borderSoft)
}

@Composable
private fun RatingsSummaryDivider(palette: RestaurantPalette) {
    Box(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .width(1.dp)
            .height(44.dp)
            .background(palette.borderSoft),
    )
}

@Composable
private fun HighlightsSection(restaurant: Restaurant) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        FeatureRow(
            icon = Icons.Outlined.EmojiEvents,
            title = "Exceptional dining experience",
            subtitle = "Guests consistently praise this place for quality and atmosphere.",
        )
        FeatureRow(
            icon = Icons.Outlined.Restaurant,
            title = "${restaurant.cuisine} cuisine",
            subtitle = "Chef-driven menu with seasonal ingredients and house specialties.",
        )
    }
    HorizontalDivider(color = palette.borderSoft)
}

@Composable
private fun FeatureRow(icon: ImageVector, title: String, subtitle: String) {
    val palette = LocalRestaurantPalette.current
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Icon(icon, null, tint = palette.foreground, modifier = Modifier.size(22.dp))
        Column {
            Text(title, color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = palette.mutedForeground, fontSize = 15.sp, lineHeight = 22.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
private fun AboutSection(ext: RestaurantExtendedData) {
    val palette = LocalRestaurantPalette.current
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
        Text("About this place", color = palette.foreground, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "${ext.description} Experience the best of our cuisine at this location.",
            color = palette.foreground.copy(alpha = 0.9f),
            fontSize = 16.sp,
            lineHeight = 28.sp,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
    HorizontalDivider(color = palette.borderSoft)
}

@Composable
private fun AmenitiesSection(
    restaurant: Restaurant,
    ext: RestaurantExtendedData,
    onShowAll: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val previewItems = remember(restaurant, ext) {
        RestaurantAmenitiesData.previewItems(restaurant, ext)
    }
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
        Text("What this place offers", color = palette.foreground, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))
        previewItems.forEach { item ->
            AmenityRow(
                icon = item.icon.toImageVector(),
                text = item.label,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(palette.mutedSurface)
                .clickable(onClick = onShowAll),
            contentAlignment = Alignment.Center,
        ) {
            Text("Show all amenities", color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
    HorizontalDivider(color = palette.borderSoft)
}

@Composable
private fun AmenityRow(icon: ImageVector, text: String) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(icon, null, tint = palette.foreground, modifier = Modifier.size(22.dp))
        Text(text, color = palette.foreground, fontSize = 16.sp)
    }
}

@Composable
private fun LocationSection(restaurant: Restaurant, ext: RestaurantExtendedData) {
    val palette = LocalRestaurantPalette.current
    val (lat, lng) = remember(restaurant.id) { RestaurantDetailData.mapCoordinate(restaurant) }
    val locationLine = remember(restaurant.area, ext.address) {
        listOfNotNull(restaurant.area, ext.address).joinToString(", ")
    }
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
        Text("Where you'll be", color = palette.foreground, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(
            text = locationLine,
            color = palette.mutedForeground,
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
        )
        RestaurantDetailLocationMap(
            latitude = lat,
            longitude = lng,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
        )
        Text(
            text = "Exact location will be provided after booking.",
            color = palette.mutedForeground,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
    HorizontalDivider(color = palette.borderSoft)
}

@Composable
private fun GuestFavoriteSection(
    restaurant: Restaurant,
    reviews: List<ReviewEntry>,
    onOpenReviews: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(modifier = Modifier.padding(vertical = 24.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GuestFavoriteRatingLaurelRow(
                tier = restaurant.guestFavoriteLevel.toLaurelTier(),
                ratingText = formatRating(restaurant.rating),
                ratingFontSize = 52.sp,
                laurelHeight = 56.dp,
            )
            Text(
                text = "Guest favorite",
                color = palette.foreground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp),
            )
            guestFavoriteDescription(restaurant.guestFavoriteLevel.toLaurelTier())?.let { description ->
                Text(
                    text = description,
                    color = palette.mutedForeground,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp),
                )
            }
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(top = 16.dp),
        ) {
            items(reviews, key = { "${it.name}-${it.publishedAtEpochMs}" }) { review ->
                GuestReviewCard(review = review, onShowMore = onOpenReviews)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(palette.mutedSurface)
                .clickable(onClick = onOpenReviews),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "Show all ${restaurant.reviews} reviews",
                color = palette.foreground,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Text(
            "Drag to browse guest reviews",
            color = palette.mutedForeground,
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
    HorizontalDivider(color = palette.borderSoft, modifier = Modifier.padding(top = 8.dp))
}

private val GuestReviewCardHeight = 248.dp

@Composable
private fun GuestReviewCard(review: ReviewEntry, onShowMore: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .width(300.dp)
            .height(GuestReviewCardHeight)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
            .background(palette.cardSurface.copy(alpha = 0.5f))
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.Filled.Star, null, tint = palette.foreground, modifier = Modifier.size(16.dp))
            Text(formatReviewTimeAgo(review.publishedAtEpochMs), color = palette.foreground, fontSize = 14.sp)
        }
        Text(
            review.text,
            color = palette.foreground,
            fontSize = 15.sp,
            lineHeight = 24.sp,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            "Show more",
            color = palette.foreground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .padding(top = 8.dp)
                .clickable(onClick = onShowMore),
        )
        Spacer(Modifier.weight(1f))
        Row(
            modifier = Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(palette.mutedSurface),
                contentAlignment = Alignment.Center,
            ) {
                Text(review.name.take(1), fontWeight = FontWeight.Bold, color = palette.foreground)
            }
            Column {
                Text(review.name, fontWeight = FontWeight.SemiBold, color = palette.foreground, fontSize = 15.sp)
                Text("Verified diner", color = palette.mutedForeground, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun CancellationPolicySection() {
    val palette = LocalRestaurantPalette.current
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
        Text("Cancellation policy", color = palette.foreground, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(
            "Free cancellation up to 24 hours before your reservation. Late cancellations may include a partial fee depending on booking size and timing.",
            color = palette.foreground,
            fontSize = 16.sp,
            lineHeight = 28.sp,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
    HorizontalDivider(color = palette.borderSoft)
}

private const val PopularMenuPreviewCount = 6

@Composable
private fun PopularMenuSection(onShowMenu: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val allImages = remember { RestaurantDetailData.popularMenuImages() }
    var fullscreenIndex by remember { mutableStateOf<Int?>(null) }
    val previewImages = remember(allImages) { allImages.take(PopularMenuPreviewCount) }

    Column(modifier = Modifier.padding(vertical = 24.dp)) {
        Text(
            "Popular menu",
            color = palette.foreground,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                count = previewImages.size,
                key = { previewImages[it] },
            ) { index ->
                val imageUrl = previewImages[index]
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(112.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, palette.border, RoundedCornerShape(16.dp))
                        .clickable { fullscreenIndex = index },
                )
            }
            if (allImages.isNotEmpty()) {
                item(key = "popular-menu-see-all") {
                    Box(
                        modifier = Modifier
                            .size(112.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
                            .background(palette.mutedSurface)
                            .clickable { fullscreenIndex = 0 },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "See all",
                            color = palette.foreground,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
        fullscreenIndex?.let { startIndex ->
            MenuImageFullscreenViewer(
                images = allImages,
                initialIndex = startIndex,
                onDismiss = { fullscreenIndex = null },
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(palette.mutedSurface)
                .clickable(onClick = onShowMenu),
            contentAlignment = Alignment.Center,
        ) {
            Text("Show more", color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun BookingBar(
    restaurant: Restaurant,
    onBookNow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val amount = RestaurantDetailData.bookingPrice(restaurant)
    val formatted = NumberFormat.getCurrencyInstance(Locale.US).format(amount).replace(".00", "")
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(palette.cardSurface)
            .border(1.dp, palette.borderSoft)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = formatted,
                color = palette.foreground,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
            )
            Text("for 2 guests · Tonight", color = palette.mutedForeground, fontSize = 14.sp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp),
            ) {
                Icon(Icons.Outlined.Shield, null, tint = palette.foreground, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Free cancellation", color = palette.foreground, fontSize = 14.sp)
            }
        }
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(percent = 50))
                .background(Color(0xFFE31C5F))
                .clickable(onClick = onBookNow)
                .padding(horizontal = 28.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Outlined.CalendarMonth, null, tint = Color.White, modifier = Modifier.size(18.dp))
            Text("Reserve", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
