package com.mh.restaurantchainreservation.feature.dining.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Mirrors React's `staggerContainer` + `itemVariant` from `diningMotion.ts`.
 *
 *   staggerContainer: stagger child animations by 80ms each
 *   itemVariant:      from { opacity: 0, y: 15, scale: 0.96 }
 *                     to   { opacity: 1, y: 0,  scale: 1    }
 *                     transition: spring(stiffness=300, damping=28)
 */
@Composable
fun DiningStaggerItem(
    indexInGroup: Int,
    modifier: Modifier = Modifier,
    travelDp: Dp = 15.dp,
    initialScale: Float = 0.96f,
    staggerStepMs: Int = 80,
    delayStartMs: Int = 0,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val travelPx = with(density) { travelDp.toPx() }
    val alpha = remember { Animatable(0f) }
    val translateY = remember { Animatable(travelPx) }
    val scale = remember { Animatable(initialScale) }

    LaunchedEffect(indexInGroup) {
        val delayMs = (delayStartMs + indexInGroup * staggerStepMs).toLong()
        if (delayMs > 0) delay(delayMs)
        launch {
            alpha.animateTo(1f, tween(durationMillis = 320, easing = LinearOutSlowInEasing))
        }
        launch {
            translateY.animateTo(
                targetValue = 0f,
                animationSpec = spring(dampingRatio = 0.85f, stiffness = 300f),
            )
        }
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = 0.85f, stiffness = 300f),
            )
        }
    }

    Box(
        modifier = modifier.graphicsLayer {
            this.alpha = alpha.value
            this.translationY = translateY.value
            this.scaleX = scale.value
            this.scaleY = scale.value
            this.transformOrigin = androidx.compose.ui.graphics.TransformOrigin.Center
        },
    ) {
        content()
    }
}

/**
 * Mirrors React's title fade-in (initial { opacity:0, y:10 } animate { 1,0 } duration 0.5 easeOut).
 */
@Composable
fun DiningHeaderFadeIn(
    modifier: Modifier = Modifier,
    travelDp: Dp = 10.dp,
    durationMs: Int = 500,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val travelPx = with(density) { travelDp.toPx() }
    val alpha = remember { Animatable(0f) }
    val translateY = remember { Animatable(travelPx) }

    LaunchedEffect(Unit) {
        launch {
            alpha.animateTo(1f, tween(durationMs, easing = LinearOutSlowInEasing))
        }
        translateY.animateTo(0f, tween(durationMs, easing = LinearOutSlowInEasing))
    }

    Box(
        modifier = modifier.graphicsLayer {
            this.alpha = alpha.value
            this.translationY = translateY.value
        },
    ) {
        content()
    }
}
