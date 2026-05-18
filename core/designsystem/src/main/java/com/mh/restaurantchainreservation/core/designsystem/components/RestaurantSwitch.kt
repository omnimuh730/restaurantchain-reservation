package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette

private val TrackWidth = 44.dp
private val TrackHeight = 26.dp
private val ThumbSize = 19.dp
private val ThumbOffOffset = 2.dp
private val ThumbOnOffset = TrackWidth - ThumbSize - ThumbOffOffset

/**
 * Branded toggle replacing `androidx.compose.material3.Switch` across the app.
 * Track springs between palette.borderSoft (off) and palette.brand (on); the
 * thumb springs across with a small overshoot and reveals a check mark when on.
 */
@Composable
fun RestaurantSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val palette = LocalRestaurantPalette.current
    val interactionSource = remember { MutableInteractionSource() }

    val trackColor by animateColorAsState(
        targetValue = when {
            !enabled -> palette.mutedSurface
            checked -> palette.brand
            else -> palette.borderSoft
        },
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 320f),
        label = "switchTrackColor",
    )

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) ThumbOnOffset else ThumbOffOffset,
        animationSpec = if (enabled) {
            spring(dampingRatio = 0.62f, stiffness = 360f)
        } else {
            spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessHigh)
        },
        label = "switchThumbOffset",
    )

    val checkAlpha by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "switchCheckAlpha",
    )

    val toggleModifier = if (enabled) {
        Modifier.toggleable(
            value = checked,
            onValueChange = onCheckedChange,
            enabled = enabled,
            role = Role.Switch,
            interactionSource = interactionSource,
            indication = null,
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .then(toggleModifier)
            .size(width = TrackWidth, height = TrackHeight),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .size(width = TrackWidth, height = TrackHeight)
                .clip(RoundedCornerShape(percent = 50))
                .background(trackColor),
        )

        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(ThumbSize)
                .clip(CircleShape)
                .background(if (enabled) Color.White else Color.White.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = palette.brand,
                modifier = Modifier
                    .size(11.dp)
                    .alpha(checkAlpha),
            )
        }
    }
}
