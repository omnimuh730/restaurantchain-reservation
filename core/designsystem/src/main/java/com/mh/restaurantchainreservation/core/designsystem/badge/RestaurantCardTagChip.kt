package com.mh.restaurantchainreservation.core.designsystem.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors

/** Radial white-to-soft-grey fill used on listing card tag pills (Airbnb-style). */
fun restaurantCardTagChipBrush(): Brush = Brush.radialGradient(
    colors = listOf(
        RestaurantColors.Base.white,
        RestaurantColors.Neutral.tagChipLight,
        RestaurantColors.Neutral.tagChipMid,
    ),
    center = Offset(0.5f, 0.45f),
    radius = 1.15f,
)

@Composable
fun RestaurantCardTagChip(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = RestaurantColors.Text.primary,
    fontSize: androidx.compose.ui.unit.TextUnit = 11.sp,
) {
    val shape = RoundedCornerShape(999.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(restaurantCardTagChipBrush(), shape)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}
