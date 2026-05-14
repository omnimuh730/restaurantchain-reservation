package com.mh.restaurantchainreservation.feature.profile.hub

import androidx.compose.ui.util.lerp
import kotlin.math.abs

/**
 * Shared motion for stacked credit-card [HorizontalPager]s (hub + full cards page).
 *
 * Goals:
 * - At rest, inactive pages sit **partly under** the active card (deck overlap) with **scale close** to the front.
 * - **Trailing** (next, d > 0) stack biased a touch higher than **leading** (prev).
 * - Mid-swipe: extra **horizontal** spread + slightly smaller scale so layers do not awkwardly cover each other in the center.
 * - **Z-order** steepest near |d| == 0 so the interactive card stays clearly on top while dragging.
 */
internal object HubStackedCarouselMotion {
    private const val DClamp = 2.5f

    fun zIndexForPage(pageOffsetPages: Float): Float {
        val absD = abs(pageOffsetPages).coerceAtMost(DClamp)
        return 50_000f - absD * 18_000f
    }

    fun translationY(pageOffsetPages: Float, density: Float): Float {
        val d = pageOffsetPages.coerceIn(-DClamp, DClamp)
        val absD = abs(d)
        val focusT = 1f - absD.coerceIn(0f, 1f)
        val raiseT = (1f - focusT).coerceIn(0f, 1f)
        val trailingBias = when {
            d > 0.015f -> 1.12f
            d < -0.015f -> 0.96f
            else -> 1f
        }
        val baseLiftPx = 26f * density * raiseT * trailingBias
        val u = absD.coerceIn(0f, 1f)
        val midLiftK = (1f - abs(u * 2f - 1f)).coerceIn(0f, 1f)
        val midLiftPx = midLiftK * 10f * density
        return -baseLiftPx - midLiftPx
    }

    /**
     * Parallax + mid-swipe **outward** push so neighbours never sit on the same center line while dragging.
     */
    fun translationX(pageOffsetPages: Float, density: Float): Float {
        val d = pageOffsetPages.coerceIn(-DClamp, DClamp)
        val absD = abs(d)
        val u = absD.coerceIn(0f, 1f)
        val midK = (1f - abs(u * 2f - 1f)).coerceIn(0f, 1f)
        val parallaxPx = d * 32f * density
        val signD = when {
            d > 0.0001f -> 1f
            d < -0.0001f -> -1f
            else -> 0f
        }
        val midSpreadPx = signD * midK * 36f * density
        return parallaxPx + midSpreadPx
    }

    fun scaleForPage(pageOffsetPages: Float): Float {
        val absD = abs(pageOffsetPages).coerceAtMost(DClamp)
        val focusT = 1f - absD.coerceIn(0f, 1f)
        val u = absD.coerceIn(0f, 1f)
        val midK = (1f - abs(u * 2f - 1f)).coerceIn(0f, 1f)
        val base = lerp(0.91f, 1f, focusT)
        return (base - midK * 0.022f).coerceIn(0.86f, 1f)
    }

    fun rotationZForPage(pageOffsetPages: Float): Float =
        (-pageOffsetPages * 6.6f).coerceIn(-9.5f, 9.5f)

    fun alphaForPage(pageOffsetPages: Float): Float {
        val absD = abs(pageOffsetPages).coerceAtMost(DClamp)
        val focusT = 1f - absD.coerceIn(0f, 1f)
        return lerp(0.76f, 1f, focusT).coerceIn(0.62f, 1f)
    }
}
