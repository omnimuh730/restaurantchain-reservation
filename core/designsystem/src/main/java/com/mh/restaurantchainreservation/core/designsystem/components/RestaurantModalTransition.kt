package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

/**
 * Global state for tracking active modal sheets to drive the "Receding Stack" (Airbnb-style)
 * background animation.
 */
object RestaurantModalTransition {
    /** Number of active modals. Background recession is active if > 0. */
    private var activeModalCount by mutableIntStateOf(0)

    /**
     * Optional manual progress (0..1) provided by the active sheet (e.g., during drag).
     * If 0, the animation falls back to the discrete [isAnyModalOpen] state.
     */
    private var manualProgress by mutableFloatStateOf(0f)

    val isAnyModalOpen: Boolean
        get() = activeModalCount > 0

    fun increment() {
        activeModalCount++
    }

    fun decrement() {
        activeModalCount = (activeModalCount - 1).coerceAtLeast(0)
    }

    fun updateManualProgress(progress: Float) {
        manualProgress = progress
    }

    /**
     * Returns a 0..1 progress value for the background recession animation.
     * Uses spring physics for an organic, elastic feel, but allows manual override
     * for real-time gesture tracking.
     */
    @Composable
    fun rememberRecessionProgress(): Float {
        val target = if (isAnyModalOpen) 1f else 0f
        val animatedProgress by animateFloatAsState(
            targetValue = target,
            animationSpec = spring(
                dampingRatio = 0.85f,
                stiffness = Spring.StiffnessLow,
            ),
            label = "modal-recession-progress"
        )

        // If manualProgress is actively being updated (during drag), use it to override
        // the discrete animation for that "physical" feel.
        val progress = if (manualProgress > 0f && isAnyModalOpen) {
            manualProgress
        } else {
            animatedProgress
        }
        return progress.coerceIn(0f, 1f)
    }
}
