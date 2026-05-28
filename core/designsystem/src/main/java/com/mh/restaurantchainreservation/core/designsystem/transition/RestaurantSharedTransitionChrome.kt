@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.mh.restaurantchainreservation.core.designsystem.transition

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/** Timeline for discover ↔ detail shared-element choreography. */
object RestaurantSharedTransitionMotion {
    const val durationMillis = 500
    val easing = FastOutSlowInEasing
    val contentRevealTween = tween<Float>(durationMillis = 350, easing = easing)

    val boundsProgressTween = tween<Float>(durationMillis = durationMillis, easing = easing)

    /** Detail chrome (toolbar, reserve bar) fades in over the final 30% of the push. */
    fun detailChromeAlpha(transitionProgress: Float, transitionActive: Boolean): Float {
        if (!transitionActive) return 1f
        return ((transitionProgress - 0.7f) / 0.3f).coerceIn(0f, 1f)
    }

    /** Discover chrome (search header, bottom nav) fades out over the push. */
    fun discoverChromeAlpha(transitionProgress: Float, transitionActive: Boolean): Float {
        if (!transitionActive) return 1f
        return (1f - transitionProgress).coerceIn(0f, 1f)
    }
}

data class RestaurantSharedTransitionChromeSnapshot(
    val progress: Float = 0f,
    val active: Boolean = false,
    /** Restaurant whose card is the active shared-element source/destination. */
    val restaurantId: String? = null,
    /**
     * After opening restaurant detail from Discover, keep the bottom nav hidden on return
     * until the user scrolls to reveal it ([clearBottomNavSuppressOnDiscover]).
     */
    val suppressBottomNavOnDiscover: Boolean = false,
)

/**
 * Global snapshot of the restaurant shared-element timeline so chrome outside
 * [SharedTransitionLayout] (e.g. bottom navigation) can stay in sync.
 */
object RestaurantSharedTransitionChrome {
    var snapshot by mutableStateOf(RestaurantSharedTransitionChromeSnapshot())
        private set

    internal fun update(
        progress: Float,
        active: Boolean,
        restaurantId: String? = snapshot.restaurantId,
    ) {
        snapshot = snapshot.copy(
            progress = progress.coerceIn(0f, 1f),
            active = active,
            restaurantId = if (active) restaurantId else snapshot.restaurantId,
        )
    }

    fun beginRestaurantDetailTransition(restaurantId: String) {
        snapshot = snapshot.copy(
            restaurantId = restaurantId,
            suppressBottomNavOnDiscover = true,
        )
    }

    fun clearBottomNavSuppressOnDiscover() {
        if (!snapshot.suppressBottomNavOnDiscover) return
        snapshot = snapshot.copy(suppressBottomNavOnDiscover = false)
    }

    /** Clears transition progress only; keeps [restaurantId] for the pop animation. */
    fun clearTransitionProgress() {
        snapshot = snapshot.copy(
            progress = 0f,
            active = false,
        )
    }

    fun clearRestaurantTransitionTarget() {
        snapshot = snapshot.copy(restaurantId = null)
    }
}

/**
 * Drives [RestaurantSharedTransitionChrome] from [SharedTransitionScope.isTransitionActive]
 * with a fixed [RestaurantSharedTransitionMotion.durationMillis] curve.
 */
@Composable
fun RestaurantSharedTransitionChromeSink(isPop: Boolean) {
    val scope = LocalRestaurantSharedTransitionScope.current ?: return
    val driver = remember { Animatable(0f) }

    LaunchedEffect(scope.isTransitionActive, isPop) {
        if (scope.isTransitionActive) {
            val target = if (isPop) 0f else 1f
            // Push (isPop=false): animate 0 -> 1
            // Pop (isPop=true): animate 1 -> 0
            if (isPop && driver.value < 0.01f) {
                driver.snapTo(1f)
            } else if (!isPop && driver.value > 0.99f) {
                driver.snapTo(0f)
            }

            driver.animateTo(
                targetValue = target,
                animationSpec = RestaurantSharedTransitionMotion.boundsProgressTween,
            )
        } else {
            // Reset to 0 when inactive (at rest on Discover home).
            driver.snapTo(0f)
            RestaurantSharedTransitionChrome.update(progress = 0f, active = false)
            RestaurantSharedTransitionChrome.clearTransitionProgress()
        }
    }

    SideEffect {
        if (scope.isTransitionActive) {
            RestaurantSharedTransitionChrome.update(
                progress = driver.value,
                active = true,
                restaurantId = RestaurantSharedTransitionChrome.snapshot.restaurantId,
            )
        }
    }
}

/** True when this restaurant is the card involved in the current shared-element transition. */
@Composable
fun rememberRestaurantSharedTransitionParticipant(
    restaurantId: String,
    sharedTransitionScope: SharedTransitionScope?,
): Boolean {
    if (sharedTransitionScope?.isTransitionActive != true) return false
    return RestaurantSharedTransitionChrome.snapshot.restaurantId == restaurantId
}

@Composable
fun rememberRestaurantDiscoverChromeAlpha(
    sharedTransitionScope: SharedTransitionScope?,
): Float {
    val scopeActive = sharedTransitionScope?.isTransitionActive == true
    if (!scopeActive) return 1f
    val progress = RestaurantSharedTransitionChrome.snapshot.progress
    return RestaurantSharedTransitionMotion.discoverChromeAlpha(progress, transitionActive = true)
}

@Composable
fun rememberRestaurantDetailChromeAlpha(
    sharedTransitionScope: SharedTransitionScope?,
): Float {
    val scopeActive = sharedTransitionScope?.isTransitionActive == true
    if (!scopeActive) return 1f
    val progress = RestaurantSharedTransitionChrome.snapshot.progress
    return RestaurantSharedTransitionMotion.detailChromeAlpha(progress, transitionActive = true)
}

@Composable
fun rememberRestaurantHeroChromeAlpha(
    restaurantId: String,
    sharedTransitionScope: SharedTransitionScope?,
): Float {
    val participant = rememberRestaurantSharedTransitionParticipant(restaurantId, sharedTransitionScope)
    if (!participant) return 1f
    val progress = RestaurantSharedTransitionChrome.snapshot.progress
    // Fade out smoothly at the start of push (matching meta-alpha for consistent card UI fade)
    return (1f - (progress / 0.25f)).coerceIn(0f, 1f)
}

fun Modifier.restaurantDiscoverChromeFade(alpha: Float): Modifier =
    graphicsLayer { this.alpha = alpha }

fun Modifier.restaurantDetailChromeFade(alpha: Float): Modifier =
    graphicsLayer { this.alpha = alpha }
