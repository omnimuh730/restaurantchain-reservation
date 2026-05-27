@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.mh.restaurantchainreservation.core.designsystem.transition

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private val defaultSharedCornerShape = RoundedCornerShape(20.dp)

private val sharedSpring = spring<Float>(
    stiffness = Spring.StiffnessMediumLow,
    dampingRatio = Spring.DampingRatioNoBouncy,
)

@Composable
fun rememberRestaurantSharedHeroModifier(
    restaurantId: String,
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?,
    shape: Shape = defaultSharedCornerShape,
): Modifier {
    if (sharedTransitionScope == null || animatedVisibilityScope == null) return Modifier
    return with(sharedTransitionScope) {
        Modifier
            .sharedElement(
                sharedContentState = rememberSharedContentState(key = RestaurantSharedKeys.hero(restaurantId)),
                animatedVisibilityScope = animatedVisibilityScope,
            )
            .clip(shape)
    }
}

@Composable
fun rememberRestaurantSharedTitleModifier(
    restaurantId: String,
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?,
): Modifier {
    if (sharedTransitionScope == null || animatedVisibilityScope == null) return Modifier
    return with(sharedTransitionScope) {
        Modifier.sharedBounds(
            sharedContentState = rememberSharedContentState(key = RestaurantSharedKeys.title(restaurantId)),
            animatedVisibilityScope = animatedVisibilityScope,
            enter = fadeIn(animationSpec = sharedSpring),
            exit = fadeOut(animationSpec = sharedSpring),
            resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
        )
    }
}
