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
    const val durationMillis = 400
    val easing = FastOutSlowInEasing
    val contentRevealTween = tween<Float>(durationMillis = 300, easing = easing)

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
)

/**
 * Global snapshot of the restaurant shared-element timeline so chrome outside
 * [SharedTransitionLayout] (e.g. bottom navigation) can stay in sync.
 */
object RestaurantSharedTransitionChrome {
    var snapshot by mutableStateOf(RestaurantSharedTransitionChromeSnapshot())
        private set

    internal fun update(progress: Float, active: Boolean) {
        snapshot = RestaurantSharedTransitionChromeSnapshot(
            progress = progress.coerceIn(0f, 1f),
            active = active,
        )
    }
}

/**
 * Drives [RestaurantSharedTransitionChrome] from [SharedTransitionScope.isTransitionActive]
 * with a fixed [RestaurantSharedTransitionMotion.durationMillis] curve.
 */
@Composable
fun RestaurantSharedTransitionChromeSink() {
    val scope = LocalRestaurantSharedTransitionScope.current ?: return
    val driver = remember { Animatable(0f) }
    LaunchedEffect(scope.isTransitionActive) {
        if (scope.isTransitionActive) {
            driver.snapTo(0f)
            driver.animateTo(
                targetValue = 1f,
                animationSpec = RestaurantSharedTransitionMotion.boundsProgressTween,
            )
        } else {
            driver.snapTo(0f)
        }
    }
    SideEffect {
        val active = scope.isTransitionActive || driver.value > 0f
        RestaurantSharedTransitionChrome.update(driver.value, active)
    }
}

@Composable
fun rememberRestaurantDiscoverChromeAlpha(
    sharedTransitionScope: SharedTransitionScope?,
): Float {
    val chrome = RestaurantSharedTransitionChrome.snapshot
    val scopeActive = sharedTransitionScope?.isTransitionActive == true
    val progress = if (scopeActive || chrome.active) chrome.progress else 0f
    val active = scopeActive || chrome.active
    return RestaurantSharedTransitionMotion.discoverChromeAlpha(progress, active)
}

@Composable
fun rememberRestaurantDetailChromeAlpha(
    sharedTransitionScope: SharedTransitionScope?,
): Float {
    val chrome = RestaurantSharedTransitionChrome.snapshot
    val scopeActive = sharedTransitionScope?.isTransitionActive == true
    val progress = if (scopeActive || chrome.active) chrome.progress else 0f
    val active = scopeActive || chrome.active
    return RestaurantSharedTransitionMotion.detailChromeAlpha(progress, active)
}

@Composable
fun rememberRestaurantHeroChromeAlpha(
    sharedTransitionScope: SharedTransitionScope?,
): Float {
    val transitionActive = sharedTransitionScope?.isTransitionActive == true
    return if (transitionActive) 0f else 1f
}

fun Modifier.restaurantDiscoverChromeFade(alpha: Float): Modifier =
    graphicsLayer { this.alpha = alpha }

fun Modifier.restaurantDetailChromeFade(alpha: Float): Modifier =
    graphicsLayer { this.alpha = alpha }
