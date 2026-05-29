package com.mh.restaurantchainreservation.core.designsystem.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
    size: RestaurantCardBadgeChipSize = RestaurantCardBadgeChipSize.Medium,
    icon: ImageVector? = null,
) {
    val shape = RoundedCornerShape(999.dp)
    val fontSize = when (size) {
        RestaurantCardBadgeChipSize.Small -> 10.sp
        RestaurantCardBadgeChipSize.Medium -> 11.sp
        RestaurantCardBadgeChipSize.Large -> 13.sp
    }
    Row(
        modifier = modifier
            .height(size.container)
            .clip(shape)
            .background(restaurantCardTagChipBrush(), shape)
            .padding(horizontal = (size.container.value * 0.4f).dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(size.icon),
            )
            Spacer(Modifier.width(4.dp))
        }
        Text(
            text = text,
            color = textColor,
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}
