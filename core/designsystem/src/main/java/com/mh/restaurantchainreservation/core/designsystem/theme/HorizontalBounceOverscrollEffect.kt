package com.mh.restaurantchainreservation.core.designsystem.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.OverscrollFactory
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
 * Premium iOS-style elastic overscroll (Rubber-Banding) restricted to Horizontal axis only.
 */
@Stable
class HorizontalBounceOverscrollEffect : OverscrollEffect {
    private val animatable = Animatable(0f)
    private var overscrollX by mutableFloatStateOf(0f)

    override fun applyToScroll(
        delta: Offset,
        source: NestedScrollSource,
        performScroll: (Offset) -> Offset
    ): Offset {
        // 1. Let the container scroll first
        val consumedByScroll = performScroll(delta)
        
        // 2. Only handle horizontal overscroll (left/right)
        val leftOverX = delta.x - consumedByScroll.x

        if (source == NestedScrollSource.UserInput && abs(leftOverX) > 0.5f) {
            // Rubber band resistance factor
            val resistance = 0.45f
            overscrollX += leftOverX * resistance
            
            // Consume X delta completely to indicate we've handled it
            return Offset(x = delta.x, y = consumedByScroll.y)
        }

        return consumedByScroll
    }

    override suspend fun applyToFling(
        velocity: Velocity,
        performFling: suspend (Velocity) -> Velocity
    ) {
        // Let the container fling first
        performFling(velocity)

        // Reset with a spring animation when fling/drag ends
        animatable.snapTo(overscrollX)
        animatable.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) {
            overscrollX = value
        }
    }

    override val isInProgress: Boolean
        get() = overscrollX != 0f

    override val node: DelegatableNode = object : Modifier.Node(), DrawModifierNode {
        override fun ContentDrawScope.draw() {
            translate(left = overscrollX) {
                this@draw.drawContent()
            }
        }
    }
}

object HorizontalBounceOverscrollFactory : OverscrollFactory {
    override fun createOverscrollEffect(): OverscrollEffect = HorizontalBounceOverscrollEffect()

    override fun hashCode(): Int = 31338

    override fun equals(other: Any?): Boolean = other === this
}
