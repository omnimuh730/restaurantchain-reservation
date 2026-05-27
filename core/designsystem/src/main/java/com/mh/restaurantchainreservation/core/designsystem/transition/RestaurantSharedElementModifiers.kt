@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.mh.restaurantchainreservation.core.designsystem.transition

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

private val defaultSharedCornerShape = RoundedCornerShape(20.dp)

private const val CardTitleFadeOutMillis = 70
private const val DetailTitleFadeInDelayMillis = 110
private const val DetailTitleFadeInMillis = 140

private val sharedSpring = spring<Float>(
    stiffness = Spring.StiffnessMediumLow,
    dampingRatio = Spring.DampingRatioNoBouncy,
)

private val cardTitleFadeOutSpec = tween<Float>(durationMillis = CardTitleFadeOutMillis)
private val detailTitleFadeInSpec = tween<Float>(
    durationMillis = DetailTitleFadeInMillis,
    delayMillis = DetailTitleFadeInDelayMillis,
)
private val titleFadeDefaultSpec = tween<Float>(durationMillis = 80)

/** Clip shapes for shared content-panel bounds (card → detail sheet). */
object RestaurantSharedTransitionShapes {
    val cardContentPanel = RoundedCornerShape(20.dp)
    val cardContentPanelCompact = RoundedCornerShape(12.dp)
    val cardContentPanelWide = RoundedCornerShape(24.dp)
    val detailContentPanel = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
}

enum class RestaurantSharedTitleRole {
    Card,
    Detail,
}

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

/**
 * Card title fades out on push; detail title fades in after a short delay. Reversed on pop.
 */
@Composable
fun rememberRestaurantSharedTitleVisibilityModifier(
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?,
    role: RestaurantSharedTitleRole,
): Modifier {
    if (sharedTransitionScope == null) return Modifier
    val navEntry = LocalRestaurantNavEntry.current
    val transition = animatedVisibilityScope?.transition
    val inFlight = sharedTransitionScope.isTransitionActive
    val targetState = transition?.targetState
    val currentState = transition?.currentState
    val hasNavContext = navEntry != null && transition != null
    val leavingSource = hasNavContext && inFlight && targetState != navEntry
    val returningToSource = hasNavContext && inFlight && targetState == navEntry
    val enteringDestination = hasNavContext && inFlight && targetState == navEntry
    val leavingDestination = hasNavContext && inFlight &&
        currentState == navEntry && targetState != navEntry

    val targetAlpha = when (role) {
        RestaurantSharedTitleRole.Card -> when {
            leavingSource -> 0f
            returningToSource -> 1f
            else -> 1f
        }
        RestaurantSharedTitleRole.Detail -> when {
            enteringDestination -> 1f
            leavingDestination -> 0f
            else -> 1f
        }
    }
    val animationSpec: AnimationSpec<Float> = when (role) {
        RestaurantSharedTitleRole.Card -> when {
            leavingSource -> cardTitleFadeOutSpec
            returningToSource -> detailTitleFadeInSpec
            else -> titleFadeDefaultSpec
        }
        RestaurantSharedTitleRole.Detail -> when {
            enteringDestination -> detailTitleFadeInSpec
            leavingDestination -> cardTitleFadeOutSpec
            else -> titleFadeDefaultSpec
        }
    }
    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = animationSpec,
        label = "restaurant-shared-title-alpha-$role",
    )
    return Modifier.graphicsLayer { this.alpha = alpha }
}

/** Morphs the card text block into the detail screen's rounded sheet under the hero. */
@Composable
fun rememberRestaurantSharedContentPanelModifier(
    restaurantId: String,
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?,
    shape: Shape,
): Modifier {
    if (sharedTransitionScope == null || animatedVisibilityScope == null) return Modifier
    return with(sharedTransitionScope) {
        Modifier
            .sharedBounds(
                sharedContentState = rememberSharedContentState(
                    key = RestaurantSharedKeys.contentPanel(restaurantId),
                ),
                animatedVisibilityScope = animatedVisibilityScope,
                enter = fadeIn(animationSpec = sharedSpring),
                exit = fadeOut(animationSpec = sharedSpring),
                resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
            )
            .clip(shape)
    }
}

/** Fades guest-favorite badge and heart to 0 while a shared-element transition is running. */
@Composable
fun rememberRestaurantCardHeroChromeAlpha(
    sharedTransitionScope: SharedTransitionScope?,
): Float {
    val transitionActive = sharedTransitionScope?.isTransitionActive == true
    val alpha by animateFloatAsState(
        targetValue = if (transitionActive) 0f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "restaurant-card-hero-chrome-alpha",
    )
    return alpha
}

@Composable
fun BoxScope.RestaurantCardHeroChromeLayer(
    sharedTransitionScope: SharedTransitionScope?,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val chromeAlpha = rememberRestaurantCardHeroChromeAlpha(sharedTransitionScope)
    Box(
        modifier = Modifier
            .matchParentSize()
            .graphicsLayer { alpha = chromeAlpha }
            .then(modifier),
        content = content,
    )
}
