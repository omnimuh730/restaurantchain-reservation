package com.mh.restaurantchainreservation.core.designsystem.components

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette

/**
 * Matches the web `Toggle` (md, success) used on the unified payment sheet.
 */
@Composable
fun PaymentToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val palette = LocalRestaurantPalette.current
    val trackWidth = 46.dp
    val trackHeight = 26.dp
    val thumbSize = 20.dp
    val thumbPadding = 3.dp

    val trackColor by animateColorAsState(
        targetValue = when {
            !enabled -> palette.mutedSurface
            checked -> palette.success
            else -> palette.mutedForeground.copy(alpha = 0.3f)
        },
        animationSpec = tween(200),
        label = "paymentToggleTrack",
    )

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) trackWidth - thumbSize - thumbPadding else thumbPadding,
        animationSpec = tween(200),
        label = "paymentToggleThumb",
    )

    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .size(width = trackWidth, height = trackHeight)
            .then(
                if (enabled) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        role = Role.Switch,
                        onClick = { onCheckedChange(!checked) },
                    )
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .size(trackWidth, trackHeight)
                .clip(RoundedCornerShape(percent = 50))
                .border(2.dp, Color.Transparent, RoundedCornerShape(percent = 50))
                .background(trackColor),
        )
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(thumbSize)
                .shadow(2.dp, CircleShape, ambientColor = RestaurantColors.Base.black.copy(0.12f))
                .clip(CircleShape)
                .background(RestaurantColors.Base.white),
        )
    }
}
