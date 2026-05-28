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

private val defaultSharedCornerShape = RoundedCornerShape(20.dp)

private const val CardTitleFadeOutMillis = 70
private const val DetailTitleFadeInDelayMillis = 110
private const val DetailTitleFadeInMillis = 140

private val sharedSpring = spring<Float>(
    stiffness = Spring.StiffnessMediumLow,
    dampingRatio = Spring.DampingRatioNoBouncy,
)

private val sharedBoundsTransform: (Rect, Rect) -> androidx.compose.animation.core.FiniteAnimationSpec<Rect> =
    { _, _ ->
        tween(
            durationMillis = RestaurantSharedTransitionMotion.durationMillis,
            easing = RestaurantSharedTransitionMotion.easing,
        )
    }

private val cardTitleFadeOutSpec = tween<Float>(durationMillis = CardTitleFadeOutMillis)
private val detailTitleFadeInSpec = tween<Float>(
    durationMillis = DetailTitleFadeInMillis,
    delayMillis = DetailTitleFadeInDelayMillis,
)
private val titleFadeDefaultSpec = tween<Float>(durationMillis = 80)

/** Clip shapes for shared content-panel bounds (card → detail sheet). */
object RestaurantSharedTransitionShapes {
    val cardHero = RoundedCornerShape(20.dp)
    val detailHero = RoundedCornerShape(0.dp)
    val cardContentPanel = RoundedCornerShape(20.dp)
    val cardContentPanelCompact = RoundedCornerShape(12.dp)
    val cardContentPanelWide = RoundedCornerShape(24.dp)
    val detailContentPanel = RoundedCornerShape(32.dp)
}

/** White sheet overlap onto the hero (Airbnb-style card → detail). */
val RestaurantCardContentPanelHeroOverlap = 14.dp
val RestaurantCardContentPanelHeroOverlapCompact = 8.dp

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
    if (sharedTransitionScope == null || animatedVisibilityScope == null) {
        return Modifier.clip(shape)
    }
    val isParticipant = rememberRestaurantSharedTransitionParticipant(restaurantId, sharedTransitionScope)
    val progress = RestaurantSharedTransitionChrome.snapshot.progress

    // Smoothly interpolate all corner radii from 20dp (card) to 0dp (detail).
    // We delay the radius change until the final 15% of the transition (0.85 to 1.0)
    // so the image remains a distinct rounded "card" for most of the flight.
    val clipShape = if (isParticipant) {
        val radiusProgress = ((progress - 0.55f) / 0.45f).coerceIn(0f, 1f)
        val radius = lerp(20.dp, 0.dp, radiusProgress)
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
            enter = fadeIn(animationSpec = sharedSpring),
            exit = fadeOut(animationSpec = sharedSpring),
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
    val isTransitionCard = rememberRestaurantSharedTransitionParticipant(restaurantId, sharedTransitionScope)
    val clipShape = when {
        !clipOnlyWhileTransitioning -> shape
        isTransitionCard -> shape
        else -> RectangleShape
    }
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
    shape: Shape = RestaurantSharedTransitionShapes.cardContentPanel,
    heroOverlap: Dp = RestaurantCardContentPanelHeroOverlap,
): Modifier {
    val palette = LocalRestaurantPalette.current
    val isTransitionCard = rememberRestaurantSharedTransitionParticipant(restaurantId, sharedTransitionScope)
    val progress = RestaurantSharedTransitionChrome.snapshot.progress
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
        // Start fading in slightly after the bounds animation starts for a cleaner "reveal"
        ((progress - 0.12f) / 0.88f).coerceIn(0f, 1f)
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
                    shape = shape,
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
    val alpha by animateFloatAsState(
        targetValue = if (participant) 0f else 1f,
        animationSpec = tween(durationMillis = 90),
        label = "restaurant-card-content-meta-alpha",
    )
    return alpha
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
