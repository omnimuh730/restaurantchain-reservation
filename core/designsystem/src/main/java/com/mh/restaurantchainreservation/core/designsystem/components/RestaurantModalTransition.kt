package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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

    /**
     * Tracks if the currently active modal is in the process of closing (targetValue == Hidden).
     * When true, we flip the animation target early to synchronize background restoration.
     */
    private var _isHiding by mutableStateOf(false)

    val isAnyModalOpen: Boolean
        get() = activeModalCount > 0

    fun increment() {
        activeModalCount++
        _isHiding = false
    }

    fun decrement() {
        activeModalCount = (activeModalCount - 1).coerceAtLeast(0)
        if (activeModalCount == 0) _isHiding = false
    }

    fun updateManualProgress(progress: Float) {
        manualProgress = progress
    }

    fun setHiding(hiding: Boolean) {
        _isHiding = hiding
    }

    /**
     * Returns a 0..1 progress value for the background recession animation.
     * Uses spring physics for an organic, elastic feel, but allows manual override
     * for real-time gesture tracking.
     */
    @Composable
    fun rememberRecessionProgress(): Float {
        // We use isHiding to anticipate the close and start the restore animation early.
        val target = if (isAnyModalOpen && !_isHiding) 1f else 0f
        
        val animatedProgress by animateFloatAsState(
            targetValue = target,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow,
            ),
            label = "modal-recession-progress"
        )

        // Smoothly blend between manual progress (real-time drag) and the spring-driven 
        // target progress. If the user is dragging (manualProgress > 0), we use it. 
        // But if the modal is closing (isHiding), we prioritize the spring-driven 
        // restoration to ensure it finishes exactly at 0.0 even if the gesture is released.
        return when {
            _isHiding -> animatedProgress
            manualProgress > 0f && isAnyModalOpen -> manualProgress
            else -> animatedProgress
        }.coerceIn(0f, 1f)
    }
}
