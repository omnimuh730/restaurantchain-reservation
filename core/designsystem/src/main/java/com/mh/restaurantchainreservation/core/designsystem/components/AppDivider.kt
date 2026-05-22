package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors

/** Standard 1px horizontal rule using [RestaurantColors.Border.divider] (#EBEBEB). */
@Composable
fun AppHorizontalDivider(
    modifier: Modifier = Modifier,
    paddingHorizontal: Dp = 0.dp,
) {
    val palette = LocalRestaurantPalette.current
    HorizontalDivider(
        modifier = modifier.padding(horizontal = paddingHorizontal),
        thickness = RestaurantColors.Divider.ThicknessDp.dp,
        color = palette.border,
    )
}

/** Box-based 1px divider for layouts that do not use [HorizontalDivider]. */
@Composable
fun AppDividerLine(
    modifier: Modifier = Modifier,
    paddingHorizontal: Dp = 0.dp,
) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = modifier
            .padding(horizontal = paddingHorizontal)
            .fillMaxWidth()
            .height(RestaurantColors.Divider.ThicknessDp.dp)
            .background(palette.border),
    )
}
