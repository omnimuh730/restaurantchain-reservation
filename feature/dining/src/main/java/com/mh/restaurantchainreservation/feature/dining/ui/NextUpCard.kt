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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.feature.dining.data.Booking

@Composable
fun NextUpCard(
    booking: Booking,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val cardShape = RoundedCornerShape(28.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = cardShape, ambientColor = Color.Black.copy(alpha = 0.05f))
            .clip(cardShape)
            .border(1.dp, palette.brand.copy(alpha = 0.20f), cardShape)
            .background(palette.brand.copy(alpha = 0.05f))
            .clickable(onClick = onClick)
            .padding(20.dp),
    ) {
        // Decorative MapPin icon (very faint, behind content)
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = palette.brand.copy(alpha = 0.04f),
            modifier = Modifier
                .size(192.dp)
                .align(Alignment.BottomEnd)
                .padding(end = 0.dp, bottom = 0.dp),
        )

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                NextUpChip()
                ChevronAffordance()
            }
            Spacer(Modifier.height(32.dp))
            Text(
                text = booking.restaurant,
                color = palette.foreground,
                fontSize = 22.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
            Spacer(Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = booking.date,
                    color = palette.foreground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(palette.brand),
                )
                Text(
                    text = booking.time,
                    color = palette.foreground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun NextUpChip() {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .height(28.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(palette.brand.copy(alpha = 0.10f))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.CalendarToday,
            contentDescription = null,
            tint = palette.brand,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = stringResource(I18nR.string.dining_next_up).uppercase(),
            color = palette.brand,
            fontSize = 11.sp,
            letterSpacing = 1.6.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
private fun ChevronAffordance() {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(palette.cardSurface.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = palette.brand,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
fun EmptyNextCard(
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val cardShape = RoundedCornerShape(28.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = cardShape, ambientColor = Color.Black.copy(alpha = 0.05f))
            .clip(cardShape)
            .border(1.dp, palette.border.copy(alpha = 0.5f), cardShape)
            .background(palette.mutedSurface.copy(alpha = 0.40f))
            .padding(20.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.CalendarToday,
                contentDescription = null,
                tint = palette.mutedForeground,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = stringResource(I18nR.string.dining_no_upcoming_plans).uppercase(),
                color = palette.mutedForeground,
                fontSize = 12.sp,
                letterSpacing = 1.6.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(I18nR.string.dining_zero_upcoming),
            color = palette.foreground,
            fontSize = 22.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(I18nR.string.dining_discover_prompt),
            color = palette.mutedForeground,
            fontSize = 14.sp,
        )
    }
}

@Composable
fun StatsGrid(
    placesVisited: Int,
    totalBookings: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatTile(
            value = placesVisited,
            label = stringResource(I18nR.string.dining_places_visited),
            modifier = Modifier.weight(1f),
        )
        StatTile(
            value = totalBookings,
            label = stringResource(I18nR.string.dining_total_bookings),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatTile(
    value: Int,
    label: String,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val cardShape = RoundedCornerShape(24.dp)
    Column(
        modifier = modifier
            .shadow(elevation = 4.dp, shape = cardShape, ambientColor = Color.Black.copy(alpha = 0.05f))
            .clip(cardShape)
            .border(1.dp, palette.border.copy(alpha = 0.6f), cardShape)
            .background(palette.cardSurface)
            .padding(20.dp),
    ) {
        Text(
            text = value.toString(),
            color = palette.foreground,
            fontSize = 28.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            color = palette.mutedForeground,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
