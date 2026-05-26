package com.mh.restaurantchainreservation.core.designsystem.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
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
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign

/**
 * Premium iOS-style elastic overscroll (Rubber-Banding).
 */
@Stable
class BounceOverscrollEffect : OverscrollEffect {
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

        // 2. Handle overscroll if there's leftover delta and it's from user input
        if (source == NestedScrollSource.UserInput && (abs(leftOver.x) > 0.5f || abs(leftOver.y) > 0.5f)) {
            val currentOffset = overscrollOffset
            
            // Calculate "rubber band" resistance
            // NewOffset = current + (delta / (1 + (abs(current) / maxRange)))
            // We'll use a simpler factor for now
            val resistance = 0.45f
            val newOffset = currentOffset + leftOver * resistance
            
            overscrollOffset = newOffset
            
            // Re-render occurs via the DrawModifierNode
            return delta // Consume everything to prevent other overscroll effects
        }

        // 3. If we are currently in an overscroll state but moving back towards center
        if (overscrollOffset != Offset.Zero) {
             // Basic return-to-center logic during active drag could be added here
             // but usually we wait for applyToFling/Release.
        }

        return consumedByScroll
    }

    override suspend fun applyToFling(
        velocity: Velocity,
        performFling: suspend (Velocity) -> Velocity
    ) {
        // Let the container fling
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

object BounceOverscrollFactory : OverscrollFactory {
    override fun createOverscrollEffect(): OverscrollEffect = BounceOverscrollEffect()

    override fun hashCode(): Int = 31337

    override fun equals(other: Any?): Boolean = other === this
}
