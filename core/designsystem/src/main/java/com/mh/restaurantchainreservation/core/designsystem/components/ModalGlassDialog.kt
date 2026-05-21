package com.mh.restaurantchainreservation.core.designsystem.components

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import kotlin.math.roundToInt

enum class ModalGlassScrimStrength {
    /** Light frosted veil; best when the dialog card is the main focus. */
    Standard,

    /** Darker base fade so content behind (including the status bar area) is subdued. */
    Strong,

    /**
     * Frosted glass: keeps scrims translucent on API 31+ so [Window.setBackgroundBlurRadius]
     * and [WindowManager.LayoutParams.blurBehindRadius] stay visible through the backdrop.
     */
    FrostedGlass,
}

/**
 * Full-screen modal with a frosted glass backdrop: transparent dialog window,
 * optional [android.view.Window.setBackgroundBlurRadius] on API 31+, and layered
 * dark + light scrims so content behind reads as blurred and dimmed edge-to-edge.
 */
@Composable
fun ModalGlassDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    blurRadiusDp: Float = 16f,
    scrimStrength: ModalGlassScrimStrength = ModalGlassScrimStrength.Standard,
    content: @Composable BoxScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnClickOutside,
            decorFitsSystemWindows = false,
        ),
    ) {
        ConfigureModalGlassDialogWindow(blurRadiusDp = blurRadiusDp)
        ModalGlassScrim(
            modifier = modifier,
            onBackdropClick = if (dismissOnClickOutside) onDismissRequest else null,
            scrimStrength = scrimStrength,
            content = content,
        )
    }
}

@Composable
fun ConfigureModalGlassDialogWindow(blurRadiusDp: Float = 16f) {
    val view = LocalView.current
    val density = LocalDensity.current
    val supportsBackdropBlur = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    DisposableEffect(view, blurRadiusDp) {
        val window = (view.parent as? DialogWindowProvider)?.window
        val previousDim = window?.attributes?.dimAmount
        val previousBlurBehindRadius = window?.attributes?.blurBehindRadius
        val previousStatusBarColor = window?.statusBarColor
        val previousNavBarColor = window?.navigationBarColor
        if (window != null) {
            window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            val attrs = window.attributes
            attrs.dimAmount = 0f
            attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            if (supportsBackdropBlur) {
                val blurPx = (blurRadiusDp * density.density).roundToInt().coerceIn(1, 150)
                window.setBackgroundBlurRadius(blurPx)
                attrs.blurBehindRadius = blurPx
            }
            window.attributes = attrs
        }
        onDispose {
            if (window != null) {
                if (supportsBackdropBlur) {
                    window.setBackgroundBlurRadius(0)
                }
                if (previousDim != null) {
                    val a = window.attributes
                    a.dimAmount = previousDim
                    a.flags = a.flags and WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS.inv()
                    if (supportsBackdropBlur && previousBlurBehindRadius != null) {
                        a.blurBehindRadius = previousBlurBehindRadius
                    }
                    window.attributes = a
                }
                WindowCompat.setDecorFitsSystemWindows(window, true)
                if (previousStatusBarColor != null) {
                    window.statusBarColor = previousStatusBarColor
                }
                if (previousNavBarColor != null) {
                    window.navigationBarColor = previousNavBarColor
                }
            }
        }
    }
}

@Composable
fun ModalGlassScrim(
    modifier: Modifier = Modifier,
    onBackdropClick: (() -> Unit)? = null,
    scrimStrength: ModalGlassScrimStrength = ModalGlassScrimStrength.Standard,
    content: @Composable BoxScope.() -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val supportsBackdropBlur = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val (darkAlpha, lightAlpha, legacyVeilAlpha) = when (scrimStrength) {
        ModalGlassScrimStrength.Standard -> when {
            supportsBackdropBlur -> Triple(0.48f, 0.22f, 0.78f)
            palette.isDark -> Triple(0.62f, 0.18f, 0.82f)
            else -> Triple(0.52f, 0.28f, 0.88f)
        }
        ModalGlassScrimStrength.Strong -> when {
            supportsBackdropBlur -> Triple(0.72f, 0.14f, 0.90f)
            palette.isDark -> Triple(0.80f, 0.10f, 0.94f)
            else -> Triple(0.76f, 0.16f, 0.94f)
        }
        ModalGlassScrimStrength.FrostedGlass -> when {
            supportsBackdropBlur -> if (palette.isDark) {
                Triple(0.22f, 0.26f, 0.84f)
            } else {
                Triple(0.28f, 0.38f, 0.88f)
            }
            palette.isDark -> Triple(0.68f, 0.14f, 0.90f)
            else -> Triple(0.62f, 0.22f, 0.92f)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (onBackdropClick != null) {
                    Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onBackdropClick,
                    )
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = darkAlpha)),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (supportsBackdropBlur) {
                        Color.White.copy(alpha = lightAlpha)
                    } else {
                        Color.White.copy(alpha = legacyVeilAlpha)
                    },
                ),
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
            content = content,
        )
    }
}
