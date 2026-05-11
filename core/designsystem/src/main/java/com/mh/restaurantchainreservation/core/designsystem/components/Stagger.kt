package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StaggerScope internal constructor(
    private val staggerMs: Int,
    private val delayStartMs: Int,
) {
    private var counter = 0

    internal fun nextDelayMs(): Int {
        val d = delayStartMs + counter * staggerMs
        counter++
        return d
    }

    internal fun reset() {
        counter = 0
    }
}

@Composable
fun Stagger(
    modifier: Modifier = Modifier,
    staggerMs: Int = 80,
    delayStartMs: Int = 0,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(0.dp),
    content: @Composable StaggerScope.() -> Unit,
) {
    val scope = remember(staggerMs, delayStartMs) { StaggerScope(staggerMs, delayStartMs) }
    SideEffect { scope.reset() }
    Column(modifier = modifier, verticalArrangement = verticalArrangement) {
        scope.content()
    }
}

enum class StaggerPreset {
    FadeInUp,
    FadeIn,
}

@Composable
fun StaggerScope.StaggerItem(
    modifier: Modifier = Modifier,
    preset: StaggerPreset = StaggerPreset.FadeInUp,
    durationMs: Int = 400,
    travelDp: Dp = 24.dp,
    content: @Composable () -> Unit,
) {
    val delayMs = remember { nextDelayMs() }
    val density = LocalDensity.current
    val travelPx = with(density) { travelDp.toPx() }

    val alpha = remember { Animatable(0f) }
    val translateYPx = remember { Animatable(if (preset == StaggerPreset.FadeInUp) travelPx else 0f) }

    LaunchedEffect(Unit) {
        if (delayMs > 0) delay(delayMs.toLong())
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = durationMs, easing = LinearOutSlowInEasing),
            )
        }
        if (preset == StaggerPreset.FadeInUp) {
            translateYPx.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = durationMs, easing = LinearOutSlowInEasing),
            )
        }
    }

    Box(
        modifier = modifier.graphicsLayer {
            this.alpha = alpha.value
            this.translationY = translateYPx.value
        },
    ) {
        content()
    }
}

@Composable
fun ColumnScope.ColumnStaggerItem(
    modifier: Modifier = Modifier,
    delayMs: Int,
    durationMs: Int = 400,
    travelDp: Dp = 24.dp,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val travelPx = with(density) { travelDp.toPx() }
    val alpha = remember { Animatable(0f) }
    val translateYPx = remember { Animatable(travelPx) }

    LaunchedEffect(Unit) {
        if (delayMs > 0) delay(delayMs.toLong())
        launch {
            alpha.animateTo(1f, tween(durationMs, easing = LinearOutSlowInEasing))
        }
        translateYPx.animateTo(0f, tween(durationMs, easing = LinearOutSlowInEasing))
    }

    Box(
        modifier = modifier.graphicsLayer {
            this.alpha = alpha.value
            this.translationY = translateYPx.value
        },
    ) {
        content()
    }
}
