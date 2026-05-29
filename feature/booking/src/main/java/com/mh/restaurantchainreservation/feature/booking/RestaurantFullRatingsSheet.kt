package com.mh.restaurantchainreservation.feature.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.mh.restaurantchainreservation.core.designsystem.badge.GuestFavoriteLaurelTier
import com.mh.restaurantchainreservation.core.designsystem.badge.GuestFavoriteRatingLaurelRow
import com.mh.restaurantchainreservation.core.model.Restaurant
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.components.RestaurantModalBottomSheet
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette

@Composable
fun RestaurantFullRatingsSheet(
    restaurant: Restaurant,
    onDismiss: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val laurelTier = restaurant.guestFavoriteLevel.toDetailLaurelTier()

    RestaurantModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (laurelTier == GuestFavoriteLaurelTier.None) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = palette.foreground,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = formatRating(restaurant.rating),
                                color = palette.foreground,
                                fontSize = 56.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        GuestFavoriteRatingLaurelRow(
                            tier = laurelTier,
                            ratingText = formatRating(restaurant.rating),
                            ratingFontSize = 56.sp,
                            laurelHeight = 64.dp
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Overall rating",
                    color = palette.foreground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Based on ${NumberFormat.getIntegerInstance(Locale.US).format(restaurant.reviews)} reviews",
                    color = palette.mutedForeground,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                ) {
                    ratingDistribution.forEach { row ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(percent = 50))
                                    .background(palette.border),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(row.percent / 100f)
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(percent = 50))
                                        .background(
                                            if (row.stars == 5) palette.foreground else palette.mutedForeground.copy(alpha = 0.35f),
                                        ),
                                )
                            }
                            Text(
                                text = "${row.stars}",
                                color = palette.mutedForeground,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(start = 12.dp),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Detailed ratings",
                    color = palette.foreground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Guests leave a rating for each category",
                    color = palette.mutedForeground,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )
                HorizontalDivider(color = palette.border)
            }

            items(subRatingMetrics) { metric ->
                DetailedRatingRow(metric = metric)
                HorizontalDivider(color = palette.border)
            }
        }
    }
}

@Composable
private fun DetailedRatingRow(
    metric: SubRatingMetric,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = metric.emoji,
            fontSize = 20.sp,
            modifier = Modifier.width(32.dp)
        )
        Text(
            text = metric.label,
            color = palette.foreground,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = metric.score,
            color = palette.foreground,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
