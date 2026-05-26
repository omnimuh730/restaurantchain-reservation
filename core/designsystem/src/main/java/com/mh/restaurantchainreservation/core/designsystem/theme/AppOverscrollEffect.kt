package com.mh.restaurantchainreservation.core.designsystem.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.OverscrollFactory
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.unit.Velocity
import kotlin.math.abs

/**
 * Premium iOS-style elastic overscroll (Rubber-Banding).
 * Supports:
 * - Horizontal (Left & Right)
 * - Vertical Bottom only (Top is stable)
 */
@Stable
class AppOverscrollEffect : OverscrollEffect {
    private val animatable = Animatable(Offset.Zero, Offset.VectorConverter)
    private var overscrollOffset by mutableStateOf(Offset.Zero)

    override fun applyToScroll(
        delta: Offset,
        source: NestedScrollSource,
        performScroll: (Offset) -> Offset
    ): Offset {
        // 1. Let the container scroll first
        val consumedByScroll = performScroll(delta)
        val leftOver = delta - consumedByScroll

        // 2. Filter leftover delta based on requirements
        if (source == NestedScrollSource.UserInput) {
            val resistance = 0.45f
            
            // Horizontal is always allowed (left and right)
            val allowedX = leftOver.x
            
            // Vertical is only allowed for bottom (upward drag result, delta.y < 0)
            val allowedY = if (leftOver.y < -0.5f) leftOver.y else 0f

            if (abs(allowedX) > 0.5f || abs(allowedY) > 0.5f) {
                val current = overscrollOffset
                val newOffset = Offset(
                    x = current.x + allowedX * resistance,
                    y = current.y + allowedY * resistance
                )
                overscrollOffset = newOffset
                
                // Return consumed delta to prevent platform effects
                return Offset(
                    x = if (abs(allowedX) > 0.5f) delta.x else consumedByScroll.x,
                    y = if (abs(allowedY) > 0.5f) delta.y else consumedByScroll.y
                )
            }
        }

        return consumedByScroll
    }

    override suspend fun applyToFling(
        velocity: Velocity,
        performFling: suspend (Velocity) -> Velocity
    ) {
        performFling(velocity)

        // Reset with a spring animation when fling/drag ends
        animatable.snapTo(overscrollOffset)
        animatable.animateTo(
            targetValue = Offset.Zero,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) {
            overscrollOffset = value
        }
    }

    override val isInProgress: Boolean
        get() = overscrollOffset != Offset.Zero

    override val node: DelegatableNode = object : Modifier.Node(), DrawModifierNode {
        override fun ContentDrawScope.draw() {
            translate(left = overscrollOffset.x, top = overscrollOffset.y) {
                this@draw.drawContent()
            }
        }
    }
}

object AppOverscrollFactory : OverscrollFactory {
    override fun createOverscrollEffect(): OverscrollEffect = AppOverscrollEffect()
    override fun hashCode(): Int = 31340
    override fun equals(other: Any?): Boolean = other === this
}
