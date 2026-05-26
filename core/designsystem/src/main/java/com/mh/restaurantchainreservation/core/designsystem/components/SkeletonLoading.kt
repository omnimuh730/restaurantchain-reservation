package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors

/**
 * A modifier that adds a shimmering skeleton effect to a component.
 * Ideal for image placeholders or text loading states.
 */
fun Modifier.shimmer(
    visible: Boolean = true,
    shape: Shape = RectangleShape,
): Modifier = composed {
    if (!visible) return@composed this

    val shimmerColors = listOf(
        RestaurantColors.Neutral.imagePlaceholder.copy(alpha = 0.6f),
        RestaurantColors.Neutral.imagePlaceholder.copy(alpha = 0.2f),
        RestaurantColors.Neutral.imagePlaceholder.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    clip(shape).background(brush)
}

/**
 * A standard skeleton box used as a placeholder for images or content blocks.
 */
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    content: @Composable (BoxScope.() -> Unit)? = null,
) {
    Box(
        modifier = modifier.shimmer(shape = shape),
        content = content ?: {},
    )
}
