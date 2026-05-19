package com.mh.restaurantchainreservation.feature.discover.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette

/** Small inline separator for Discover restaurant metadata (e.g. open time • rating). */
@Composable
fun DiscoverInlineDot(
    modifier: Modifier = Modifier,
    color: Color = LocalRestaurantPalette.current.mutedForeground,
    size: Dp = 3.dp,
    horizontalPadding: Dp = 6.dp,
) {
    Box(
        modifier = modifier
            .padding(horizontal = horizontalPadding)
            .size(size)
            .clip(CircleShape)
            .background(color),
    )
}
