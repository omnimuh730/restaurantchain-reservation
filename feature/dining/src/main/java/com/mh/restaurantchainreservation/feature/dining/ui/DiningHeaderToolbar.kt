package com.mh.restaurantchainreservation.feature.dining.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

@Composable
fun DiningAddDinnerHeaderButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DiningToolbarTextButton(
        text = stringResource(I18nR.string.add_dinner_button),
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
fun DiningToolbarTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    val palette = LocalRestaurantPalette.current
    val pillShape = RoundedCornerShape(percent = 50)
    Row(
        modifier = modifier
            .heightIn(min = 32.dp)
            .clip(pillShape)
            .background(palette.cardSurface, pillShape)
            .border(1.dp, palette.border, pillShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = palette.foreground, modifier = Modifier.size(14.dp))
        }
        Text(text, color = palette.foreground, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
