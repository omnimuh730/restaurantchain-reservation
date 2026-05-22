package com.mh.restaurantchainreservation.feature.qrpay

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

/**
 * Native horizontal-drag "Slide to Pay" button.
 *
 * Duplicated from `feature/dining/.../scanqr/SlideToPayButton.kt` per the task
 * constraint: don't add a dining→qrpay dependency. Behavior matches React
 * `unified-payment/components.tsx -> SlideToPay`.
 */
@Composable
fun SlideToPayButton(
    label: String,
    enabled: Boolean,
    disabledLabel: String,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val trackHeight = 56.dp
    val thumbSize = 48.dp
    val thumbInset = 4.dp
    val commitThreshold = 0.72f

    if (!enabled) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(trackHeight)
                .clip(RoundedCornerShape(percent = 50))
                .background(palette.mutedSurface.copy(alpha = 0.7f))
                .border(1.dp, palette.border, RoundedCornerShape(percent = 50)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = disabledLabel,
                color = palette.mutedForeground,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        return
    }

    var completed by remember { mutableStateOf(false) }
    val dragX = remember { Animatable(0f) }
    var maxX by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(completed) {
        if (completed) {
            kotlinx.coroutines.delay(650L)
            onComplete()
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(trackHeight),
    ) {
        val trackWidthPx = with(density) { maxWidth.toPx() }
        val thumbSizePx = with(density) { thumbSize.toPx() }
        val insetPx = with(density) { thumbInset.toPx() }
        maxX = (trackWidthPx - thumbSizePx - insetPx * 2f).coerceAtLeast(0f)

        val progress = if (maxX <= 0f) 0f else (dragX.value / maxX).coerceIn(0f, 1f)
        val labelAlpha by animateFloatAsState(
            targetValue = (1f - progress * 1.4f).coerceIn(0f, 1f),
            label = "slide_label_alpha",
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(percent = 50))
                .background(if (completed) palette.success else palette.cardSurface)
                .border(1.dp, palette.border, RoundedCornerShape(percent = 50)),
        ) {
            if (!completed) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(palette.brand.copy(alpha = 0.12f)),
                )
            }

            AnimatedContent(
                targetState = completed,
                transitionSpec = {
                    (fadeIn(tween(180)) togetherWith fadeOut(tween(120)))
                },
                label = "slide_state",
                modifier = Modifier.fillMaxSize(),
            ) { isComplete ->
                if (isComplete) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = RestaurantColors.Base.white,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(Modifier.size(8.dp))
                        Text(
                            text = "Payment complete",
                            color = RestaurantColors.Base.white,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = thumbSize + thumbInset, end = thumbInset)
                            .alpha(labelAlpha),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = palette.foreground,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.size(8.dp))
                        Text(
                            text = label,
                            color = palette.foreground,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
            }

            if (!completed) {
                Box(
                    modifier = Modifier
                        .padding(thumbInset)
                        .size(thumbSize)
                        .offset { IntOffset(dragX.value.roundToInt(), 0) }
                        .clip(CircleShape)
                        .background(palette.brand)
                        .pointerInput(maxX) {
                            detectDragGestures(
                                onDragEnd = {
                                    if (maxX <= 0f) return@detectDragGestures
                                    val ratio = dragX.value / maxX
                                    if (ratio >= commitThreshold) {
                                        scope.launch {
                                            dragX.animateTo(maxX, spring(stiffness = 800f))
                                            completed = true
                                        }
                                    } else {
                                        scope.launch {
                                            dragX.animateTo(0f, spring(stiffness = 600f))
                                        }
                                    }
                                },
                                onDragCancel = {
                                    scope.launch { dragX.animateTo(0f, spring(stiffness = 600f)) }
                                },
                                onDrag = { _, drag ->
                                    val next = (dragX.value + drag.x).coerceIn(0f, maxX)
                                    scope.launch { dragX.snapTo(next) }
                                },
                            )
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = RestaurantColors.Base.white,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }
    }
}
