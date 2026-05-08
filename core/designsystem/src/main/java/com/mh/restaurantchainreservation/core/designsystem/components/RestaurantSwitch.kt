package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import kotlinx.coroutines.delay

private val TrackWidth = 52.dp
private val TrackHeight = 30.dp
private val ThumbSize = 24.dp
private val ThumbOffOffset = 2.dp
private val ThumbOnOffset = 24.dp

/**
 * Branded toggle replacing `androidx.compose.material3.Switch` across the app.
 * Track springs between palette.borderSoft (off) and palette.brand (on); the
 * thumb springs across with a small overshoot and reveals a check mark when on.
 * A subtle glow ring pulses once on every flip to acknowledge the gesture.
 */
@Composable
fun RestaurantSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val palette = LocalRestaurantPalette.current

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

    var glowAlpha by remember { mutableFloatStateOf(0f) }
    var pulseTrigger by remember { mutableStateOf(0) }
    LaunchedEffect(checked) {
        if (pulseTrigger == 0) {
            pulseTrigger = 1
            return@LaunchedEffect
        }
        if (!enabled || !checked) {
            glowAlpha = 0f
            return@LaunchedEffect
        }
        glowAlpha = 0.45f
        val steps = 12
        val totalMs = 360L
        repeat(steps) { i ->
            delay(totalMs / steps)
            glowAlpha = 0.45f * (1f - (i + 1).toFloat() / steps)
        }
        glowAlpha = 0f
    }

    val toggleModifier = if (enabled) {
        Modifier.toggleable(
            value = checked,
            onValueChange = onCheckedChange,
            role = Role.Switch,
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
        if (glowAlpha > 0f) {
            Box(
                modifier = Modifier
                    .size(width = TrackWidth, height = TrackHeight)
                    .border(
                        width = 2.dp,
                        color = palette.brand.copy(alpha = 0.18f * (glowAlpha / 0.45f).coerceIn(0f, 1f)),
                        shape = RoundedCornerShape(percent = 50),
                    ),
            )
        }

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
                .shadow(elevation = 2.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(if (enabled) Color.White else Color.White.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = palette.brand,
                modifier = Modifier
                    .size(14.dp)
                    .alpha(checkAlpha),
            )
        }
    }
}
