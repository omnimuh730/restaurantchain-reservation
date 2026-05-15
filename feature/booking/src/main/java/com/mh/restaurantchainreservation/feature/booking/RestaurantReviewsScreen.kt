package com.mh.restaurantchainreservation.feature.booking

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.Restaurant

@Composable
fun RestaurantReviewsScreen(
    restaurant: Restaurant,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    var visibleCount by remember { mutableIntStateOf(5) }
    var sortLabel by remember { mutableStateOf("Most recent") }
    val reviews = remember { RestaurantDetailData.reviews }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.cardSurface),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(palette.mutedSurface)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = palette.foreground)
                }
                Text(
                    "Reviews",
                    color = palette.foreground,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(palette.mutedSurface),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Search, "Search", tint = palette.foreground, modifier = Modifier.size(18.dp))
                }
            }
            HorizontalDivider(color = palette.borderSoft)

            ReviewStatsHeader(restaurant = restaurant)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "${reviews.size} reviews",
                    color = palette.foreground,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .border(1.dp, palette.border, RoundedCornerShape(999.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    Text(sortLabel, color = palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(reviews.take(visibleCount), key = { "${it.name}-${it.publishedAtEpochMs}" }) { review ->
                    FullReviewCard(review)
                }
                if (visibleCount < reviews.size) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .height(48.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .border(1.dp, palette.border, RoundedCornerShape(999.dp))
                                .clickable { visibleCount = (visibleCount + 5).coerceAtMost(reviews.size) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("Show more", color = palette.foreground, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewStatsHeader(restaurant: Restaurant) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF7F4EF))
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            formatRating(restaurant.rating),
            color = palette.foreground,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
        )
        Text("Guest favorite", color = palette.foreground, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
        Text(
            "This restaurant is in the top picks for quality, reviews, and reliability.",
            color = palette.mutedForeground,
            fontSize = 14.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 20.dp),
        )
        HorizontalDivider(color = palette.borderSoft)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Overall rating", color = palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                (5 downTo 1).forEach { star ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("$star", fontSize = 12.sp, modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (star == 5) palette.foreground else palette.borderSoft,
                                ),
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
            ) {
                SubRating("Taste", "5.0")
                SubRating("Ambience", "4.9")
                SubRating("Service", "4.8")
                SubRating("Value", "4.7")
            }
        }
    }
    HorizontalDivider(color = palette.borderSoft)
}

@Composable
private fun SubRating(label: String, score: String) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = palette.foreground, fontSize = 13.sp)
        Text(score, color = palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun FullReviewCard(review: ReviewEntry) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(palette.brandSoftSurface),
                contentAlignment = Alignment.Center,
            ) {
                Text(review.name.take(1), color = palette.brand, fontWeight = FontWeight.Bold)
            }
            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(review.name, fontWeight = FontWeight.SemiBold, color = palette.foreground)
                Text(formatReviewTimeAgo(review.publishedAtEpochMs), color = palette.mutedForeground, fontSize = 12.sp)
            }
            Row {
                repeat(5) { i ->
                    Icon(
                        Icons.Filled.Star,
                        null,
                        tint = if (i < review.rating) Color(0xFFFF8A00) else palette.borderSoft,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }
        review.taste?.let {
            Row(modifier = Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Taste $it", fontSize = 12.sp, color = palette.mutedForeground)
                review.ambience?.let { a -> Text("Ambience $a", fontSize = 12.sp, color = palette.mutedForeground) }
                review.service?.let { s -> Text("Service $s", fontSize = 12.sp, color = palette.mutedForeground) }
                review.value?.let { v -> Text("Value $v", fontSize = 12.sp, color = palette.mutedForeground) }
            }
        }
        Text(review.text, color = palette.foreground, fontSize = 14.sp, lineHeight = 22.sp, modifier = Modifier.padding(top = 10.dp))
        Row(
            modifier = Modifier.padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(Icons.Outlined.ThumbUp, null, tint = palette.mutedForeground, modifier = Modifier.size(14.dp))
            Text("Helpful", color = palette.mutedForeground, fontSize = 13.sp)
        }
    }
}
