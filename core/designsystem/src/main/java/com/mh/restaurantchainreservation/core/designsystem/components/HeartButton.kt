package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette

enum class HeartButtonSize(val container: Dp, val icon: Dp) {
    Small(container = 28.dp, icon = 21.dp),
    Medium(container = 36.dp, icon = 27.dp),
    Large(container = 42.dp, icon = 32.dp),
}

enum class HeartButtonStyle {
    /** Circular scrim behind the heart (default). */
    Floating,

    /** Heart only on top of imagery; no circular background (e.g. discover photo cards). */
    Overlay,
}

@Composable
fun HeartButton(
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: HeartButtonSize = HeartButtonSize.Medium,
    style: HeartButtonStyle = HeartButtonStyle.Floating,
    /** When [style] is [HeartButtonStyle.Overlay], aligns the icon inside the tap target (e.g. top-align with a badge). */
    overlayContentAlignment: Alignment = Alignment.Center,
    contentDescription: String = if (active) "Remove from saved" else "Save",
    containerColor: Color = Color.Black.copy(alpha = 0.38f),
    activeContainerColor: Color = Color.Black.copy(alpha = 0.38f),
    inactiveTint: Color = Color.White,
    activeTint: Color? = null,
) {
    val palette = LocalRestaurantPalette.current
    val resolvedActiveTint = activeTint ?: palette.brand
    val scale = remember { Animatable(1f) }
    var previous by remember { mutableStateOf(active) }

    LaunchedEffect(active) {
        if (active && !previous) {
            scale.snapTo(1f)
            scale.animateTo(
                targetValue = 1f,
                animationSpec = keyframes {
                    durationMillis = 420
                    1f at 0
                    1.28f at 160
                    0.94f at 285
                    1f at 420
                },
            )
        }
        previous = active
    }

    val scrimModifier = if (style == HeartButtonStyle.Floating) {
        Modifier
            .clip(CircleShape)
            .background(if (active) activeContainerColor else containerColor)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .size(size.container)
            .scale(scale.value)
            .then(scrimModifier)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClickLabel = contentDescription,
                onClick = onClick,
            ),
        contentAlignment = if (style == HeartButtonStyle.Overlay) overlayContentAlignment else Alignment.Center,
    ) {
        Icon(
            imageVector = if (active) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = contentDescription,
            tint = if (active) resolvedActiveTint else inactiveTint,
            modifier = Modifier.size(size.icon),
        )
    }
}
