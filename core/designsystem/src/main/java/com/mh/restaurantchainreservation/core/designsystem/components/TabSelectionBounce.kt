package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Scale + vertical bounce when [isActive] becomes true — same motion as [BottomNavBar] tabs.
 */
@Composable
fun TabSelectionBounceBox(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val scale = remember { Animatable(1f) }
    val translateYDp = remember { Animatable(0f) }
    var prevActive by remember { mutableStateOf(isActive) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    LaunchedEffect(isActive) {
        val newlyActive = isActive && !prevActive
        prevActive = isActive
        if (newlyActive) {
            scope.launch {
                scale.snapTo(1f)
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = keyframes {
                        durationMillis = 350
                        1f at 0
                        0.8f at 88
                        1.15f at 263
                        1f at 350
                    },
                )
            }
            scope.launch {
                translateYDp.snapTo(0f)
                translateYDp.animateTo(
                    targetValue = 0f,
                    animationSpec = keyframes {
                        durationMillis = 350
                        0f at 0
                        2f at 88
                        -2f at 263
                        0f at 350
                    },
                )
            }
        }
    }

    Box(
        modifier = modifier.graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
            translationY = with(density) { translateYDp.value.dp.toPx() }
        },
        content = content,
    )
}
