@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.mh.restaurantchainreservation.core.designsystem.transition

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.zIndex
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette

private val sharedBoundsTransform: (Rect, Rect) -> androidx.compose.animation.core.FiniteAnimationSpec<Rect> =
    { _, _ ->
        tween(
            durationMillis = RestaurantSharedTransitionMotion.durationMillis,
            easing = RestaurantSharedTransitionMotion.easing,
        )
    }

private val cardTitleFadeOutSpec = tween<Float>(
    durationMillis = 100,
    easing = RestaurantSharedTransitionMotion.easing
)
private val detailTitleFadeInSpec = tween<Float>(
    durationMillis = 150,
    delayMillis = 150,
    easing = RestaurantSharedTransitionMotion.easing
)
private val titleFadeDefaultSpec = tween<Float>(
    durationMillis = 100,
    easing = RestaurantSharedTransitionMotion.easing
)

/** Clip shapes for shared content-panel bounds (card → detail sheet). */
object RestaurantSharedTransitionShapes {
    val cardHero = RoundedCornerShape(24.dp)
    val detailHero = RectangleShape
    val cardContentPanel = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val detailContentPanel = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
}

/** White sheet overlap onto the hero (Airbnb-style card → detail). */
val RestaurantCardContentPanelHeroOverlap = 14.dp

enum class RestaurantSharedTitleRole {
    Card,
    Detail,
}

@Composable
fun rememberRestaurantSharedHeroModifier(
    restaurantId: String,
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?,
    shape: Shape = RestaurantSharedTransitionShapes.cardHero,
): Modifier {
    if (sharedTransitionScope == null || animatedVisibilityScope == null) {
        return Modifier.clip(shape)
    }
    val isParticipant = rememberRestaurantSharedTransitionParticipant(restaurantId, sharedTransitionScope)
    val progress = RestaurantSharedTransitionChrome.snapshot.progress

    // Smoothly interpolate all corner radii from 24dp (card) to 0dp (detail) using continuous progress.
    val clipShape = if (isParticipant) {
        val radius = lerp(24.dp, 0.dp, progress)
        RoundedCornerShape(radius)
    } else {
        shape
    }

    return with(sharedTransitionScope) {
        Modifier
            .sharedElement(
                sharedContentState = rememberSharedContentState(key = RestaurantSharedKeys.hero(restaurantId)),
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = sharedBoundsTransform,
            )
            .clip(clipShape)
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
            enter = fadeIn(animationSpec = tween(RestaurantSharedTransitionMotion.durationMillis, easing = RestaurantSharedTransitionMotion.easing)),
            exit = fadeOut(animationSpec = tween(RestaurantSharedTransitionMotion.durationMillis, easing = RestaurantSharedTransitionMotion.easing)),
            resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
            boundsTransform = sharedBoundsTransform,
        )
    }
}

/**
 * Card title fades out on push; detail title fades in after a short delay. Reversed on pop.
 */
@Composable
fun rememberRestaurantSharedTitleVisibilityModifier(
    restaurantId: String,
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
    val isTransitionCard = rememberRestaurantSharedTransitionParticipant(restaurantId, sharedTransitionScope)

    val targetAlpha = when (role) {
        RestaurantSharedTitleRole.Card -> when {
            leavingSource && isTransitionCard -> 0f
            returningToSource && isTransitionCard -> 1f
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
            leavingSource && isTransitionCard -> cardTitleFadeOutSpec
            returningToSource && isTransitionCard -> detailTitleFadeInSpec
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

/**
 * Morphs the card text block into the detail screen's rounded sheet under the hero.
 *
 * On discover cards, [clipOnlyWhileTransitioning] avoids clipping list text at rest; rounded
 * corners apply only once the shared-element transition is in flight.
 */
@Composable
fun rememberRestaurantSharedContentPanelModifier(
    restaurantId: String,
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?,
    shape: Shape = RestaurantSharedTransitionShapes.cardContentPanel,
    clipOnlyWhileTransitioning: Boolean = true,
): Modifier {
    if (sharedTransitionScope == null || animatedVisibilityScope == null) return Modifier
    val isParticipant = rememberRestaurantSharedTransitionParticipant(restaurantId, sharedTransitionScope)
    val progress = RestaurantSharedTransitionChrome.snapshot.progress

    // Smoothly interpolate the content panel's top rounded corners (card -> detail sheet).
    val clipShape = if (isParticipant) {
        val radius = lerp(24.dp, 32.dp, progress)
        RoundedCornerShape(topStart = radius, topEnd = radius)
    } else {
        if (clipOnlyWhileTransitioning) {
            RectangleShape
        } else {
            shape
        }
    }

    return with(sharedTransitionScope) {
        Modifier
            .sharedBounds(
                sharedContentState = rememberSharedContentState(
                    key = RestaurantSharedKeys.contentPanel(restaurantId),
                ),
                animatedVisibilityScope = animatedVisibilityScope,
                enter = fadeIn(animationSpec = tween(RestaurantSharedTransitionMotion.durationMillis, easing = RestaurantSharedTransitionMotion.easing)),
                exit = fadeOut(animationSpec = tween(RestaurantSharedTransitionMotion.durationMillis, easing = RestaurantSharedTransitionMotion.easing)),
                resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                boundsTransform = sharedBoundsTransform,
            )
            .clip(clipShape)
    }
}

/** Where [restaurantSharedContentPanelLayer] is applied. */
enum class RestaurantSharedContentPanelLayerRole {
    /** List/rail cards: transparent text block at rest; white sheet only while transitioning. */
    DiscoverCard,
    /** Detail screen: always the opaque sheet over the hero. */
    DetailSheet,
}

/**
 * Twin-layer white sheet above the hero during shared-element transitions.
 *
 * On discover cards at rest, leaves the text block fully transparent with no overlap so the
 * image (and its shadow) is not clipped by a solid panel — matching the Airbnb list layout.
 */
@Composable
fun Modifier.restaurantSharedContentPanelLayer(
    restaurantId: String,
    sharedTransitionScope: SharedTransitionScope?,
    role: RestaurantSharedContentPanelLayerRole = RestaurantSharedContentPanelLayerRole.DiscoverCard,
    heroOverlap: Dp = RestaurantCardContentPanelHeroOverlap,
): Modifier {
    val palette = LocalRestaurantPalette.current
    val isTransitionCard = rememberRestaurantSharedTransitionParticipant(restaurantId, sharedTransitionScope)
    val progress = RestaurantSharedTransitionChrome.snapshot.progress
    val currentRadius = if (isTransitionCard) {
        lerp(24.dp, 32.dp, progress)
    } else {
        if (role == RestaurantSharedContentPanelLayerRole.DetailSheet) 32.dp else 24.dp
    }
    val currentShape = RoundedCornerShape(topStart = currentRadius, topEnd = currentRadius)

    val showSurface = when (role) {
        RestaurantSharedContentPanelLayerRole.DiscoverCard -> isTransitionCard
        RestaurantSharedContentPanelLayerRole.DetailSheet -> true
    }
    val overlap = when (role) {
        RestaurantSharedContentPanelLayerRole.DiscoverCard ->
            if (isTransitionCard) heroOverlap else 0.dp
        RestaurantSharedContentPanelLayerRole.DetailSheet -> heroOverlap
    }

    // Smoothly fade in the white sheet background during the shared-element transition
    // to avoid a sudden "white flash" behind the moving hero image.
    val isTransitionActive = sharedTransitionScope?.isTransitionActive == true
    val surfaceAlpha = if (isTransitionActive) {
        progress.coerceIn(0f, 1f)
    } else {
        when (role) {
            RestaurantSharedContentPanelLayerRole.DetailSheet -> 1f
            RestaurantSharedContentPanelLayerRole.DiscoverCard -> 0f
        }
    }

    return this
        .then(if (showSurface) Modifier.zIndex(1f) else Modifier)
        .offset(y = -overlap)
        .then(
            if (showSurface) {
                Modifier.background(
                    color = palette.pageBackground.copy(alpha = surfaceAlpha),
                    shape = currentShape,
                )
            } else {
                Modifier
            },
        )
}

/** Fades secondary card lines (address, rating row) during the shared-element transition. */
@Composable
fun rememberRestaurantCardContentMetaAlpha(
    restaurantId: String,
    sharedTransitionScope: SharedTransitionScope?,
): Float {
    val participant = rememberRestaurantSharedTransitionParticipant(restaurantId, sharedTransitionScope)
    if (!participant) return 1f
    val progress = RestaurantSharedTransitionChrome.snapshot.progress
    // Fade out smoothly at the start of push
    return (1f - (progress / 0.25f)).coerceIn(0f, 1f)
}

@Composable
fun BoxScope.RestaurantCardHeroChromeLayer(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .then(modifier),
        content = content,
    )
}
