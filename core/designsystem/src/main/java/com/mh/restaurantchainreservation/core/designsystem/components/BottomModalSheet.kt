package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette

/**
 * Bottom-anchored modal sheet: scrim fades in, sheet slides up + scales from 0.97 with a spring.
 * Tap on the scrim closes.
 */
@Composable
fun BottomModalSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadiusDp: Int = 32,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    content: @Composable () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val scrimAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(stiffness = 320f, dampingRatio = 0.85f),
        label = "scrim_alpha",
    )
    val sheetTranslate by animateFloatAsState(
        targetValue = if (visible) 0f else 80f,
        animationSpec = spring(stiffness = 420f, dampingRatio = 0.78f),
        label = "sheet_translate",
    )
    val sheetScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.97f,
        animationSpec = spring(stiffness = 420f, dampingRatio = 0.78f),
        label = "sheet_scale",
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.40f * scrimAlpha))
                .let {
                    if (dismissOnClickOutside) {
                        it.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = onDismiss,
                        )
                    } else {
                        it
                    }
                },
            contentAlignment = Alignment.BottomCenter,
        ) {
            val sheetShape = RoundedCornerShape(topStart = cornerRadiusDp.dp, topEnd = cornerRadiusDp.dp)
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        translationY = sheetTranslate
                        scaleX = sheetScale
                        scaleY = sheetScale
                        transformOrigin = TransformOrigin(0.5f, 1f)
                        alpha = scrimAlpha
                    }
                    .shadow(elevation = 18.dp, shape = sheetShape, ambientColor = Color.Black.copy(alpha = 0.18f))
                    .clip(sheetShape)
                    .background(palette.cardSurface)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {},
                    ),
            ) {
                ModalSheetDragHandle()
                content()
            }
        }
    }
}

/** Center-anchored modal for confirms (cancel/destructive style). */
@Composable
fun CenterModalSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadiusDp: Int = 32,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    content: @Composable () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(stiffness = 320f, dampingRatio = 0.85f),
        label = "center_alpha",
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.94f,
        animationSpec = spring(stiffness = 420f, dampingRatio = 0.78f),
        label = "center_scale",
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f * alpha))
                .let {
                    if (dismissOnClickOutside) {
                        it.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = onDismiss,
                        )
                    } else {
                        it
                    }
                }
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center,
        ) {
            val shape = RoundedCornerShape(cornerRadiusDp.dp)
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
                    .shadow(elevation = 18.dp, shape = shape, ambientColor = Color.Black.copy(alpha = 0.18f))
                    .clip(shape)
                    .background(palette.cardSurface)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {},
                    ),
                content = { content() },
            )
        }
    }
}
