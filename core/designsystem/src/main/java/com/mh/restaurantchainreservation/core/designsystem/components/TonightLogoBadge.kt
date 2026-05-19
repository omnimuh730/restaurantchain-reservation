package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainreservation.core.designsystem.components.icons.TonightLogoMark
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette

/** Brand logo tile used on sign-in and data-sync prompts. */
@Composable
fun TonightLogoBadge(
    modifier: Modifier = Modifier,
    size: Dp = 54.dp,
    cornerRadius: Dp = 18.dp,
    logoSize: Dp = 36.dp,
) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .background(palette.brand),
        contentAlignment = Alignment.Center,
    ) {
        TonightLogoMark(modifier = Modifier.size(logoSize), color = Color.White)
    }
}
