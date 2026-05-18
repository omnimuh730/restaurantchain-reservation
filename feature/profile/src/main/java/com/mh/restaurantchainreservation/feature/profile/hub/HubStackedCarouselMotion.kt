package com.mh.restaurantchainreservation.feature.profile.hub

import androidx.compose.ui.util.lerp
import kotlin.math.abs

/**
 * Shared motion for stacked credit-card [HorizontalPager]s (hub + full cards page).
 *
 * Goals:
 * - At rest, inactive pages sit **partly under** the active card (deck overlap) with **scale close** to the front.
 * - Inactive header starts a **few px higher** than the active card so the back **top rounded corner** peeks above the front edge.
 * - **RotationZ** tilts inactive bodies **high → low** (outer edge lower); sign matches page side.
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
        val inactiveT = (1f - focusT).coerceIn(0f, 1f)
        val trailingBias = when {
            d > 0.015f -> 1.06f
            d < -0.015f -> 0.97f
            else -> 1f
        }
        // Negative Y moves content up — tiny lift so the back card’s top corner clears the active top edge.
        val headerPeekPx = 9f * density * inactiveT * trailingBias
        val u = absD.coerceIn(0f, 1f)
        val midPeekK = (1f - abs(u * 2f - 1f)).coerceIn(0f, 1f)
        val midPeekPx = midPeekK * 3f * density
        return -(headerPeekPx + midPeekPx)
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
        val base = lerp(0.94f, 1f, focusT)
        return (base - midK * 0.015f).coerceIn(0.90f, 1f)
    }

    fun rotationZForPage(pageOffsetPages: Float): Float =
        (pageOffsetPages * 6.6f).coerceIn(-9.5f, 9.5f)

    fun alphaForPage(pageOffsetPages: Float): Float {
        val absD = abs(pageOffsetPages).coerceAtMost(DClamp)
        val focusT = 1f - absD.coerceIn(0f, 1f)
        return lerp(0.76f, 1f, focusT).coerceIn(0.62f, 1f)
    }
}
