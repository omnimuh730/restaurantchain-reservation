package com.mh.restaurantchainreservation.feature.wishlist.ui

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * One-shot welcome dialog shown the first time the user opens the Wishlist
 * tab in a session. Mirrors the React `GatheredModal` — Tailwind-style
 * `backdrop-blur-sm` via [android.view.Window.setBackgroundBlurRadius] on API 31+,
 * plus a light white scrim; older API uses a stronger frosted scrim only.
 */
@Composable
fun GatheredWelcomeModal(
    images: List<String>,
    onDismiss: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
    ) {
        val view = LocalView.current
        val density = LocalDensity.current
        val supportsBackdropBlur = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

        DisposableEffect(view) {
            val window = (view.parent as? DialogWindowProvider)?.window
            val previousDim = window?.attributes?.dimAmount
            if (window != null) {
                window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
                val attrs = window.attributes
                attrs.dimAmount = 0f
                window.attributes = attrs
                if (supportsBackdropBlur) {
                    // Match Tailwind default `--blur-sm` (~4px on web).
                    val blurPx = (4f * density.density).roundToInt().coerceIn(1, 150)
                    window.setBackgroundBlurRadius(blurPx)
                }
            }
            onDispose {
                if (window != null) {
                    if (supportsBackdropBlur) {
                        window.setBackgroundBlurRadius(0)
                    }
                    if (previousDim != null) {
                        val a = window.attributes
                        a.dimAmount = previousDim
                        window.attributes = a
                    }
                }
            }
        }

        val backdropAlpha = remember { Animatable(0f) }
        val cardScale = remember { Animatable(0.95f) }
        val cardY = remember { Animatable(50f) }
        val titleAlpha = remember { Animatable(0f) }
        val titleY = remember { Animatable(10f) }
        val subtitleAlpha = remember { Animatable(0f) }
        val subtitleY = remember { Animatable(10f) }
        val buttonAlpha = remember { Animatable(0f) }
        val buttonY = remember { Animatable(10f) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            scope.launch { backdropAlpha.animateTo(1f, tween(220)) }
            scope.launch {
                cardScale.animateTo(1f, spring(dampingRatio = 0.85f, stiffness = 220f))
            }
            scope.launch {
                cardY.animateTo(0f, spring(dampingRatio = 0.85f, stiffness = 220f))
            }
            scope.launch {
                kotlinx.coroutines.delay(400)
                launch { titleAlpha.animateTo(1f, tween(260)) }
                launch { titleY.animateTo(0f, tween(260)) }
            }
            scope.launch {
                kotlinx.coroutines.delay(500)
                launch { subtitleAlpha.animateTo(1f, tween(260)) }
                launch { subtitleY.animateTo(0f, tween(260)) }
            }
            scope.launch {
                kotlinx.coroutines.delay(600)
                launch { buttonAlpha.animateTo(1f, tween(260)) }
                launch { buttonY.animateTo(0f, tween(260)) }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (supportsBackdropBlur) {
                        RestaurantColors.Base.white.copy(alpha = backdropAlpha.value * 0.38f)
                    } else {
                        RestaurantColors.Base.white.copy(alpha = backdropAlpha.value * 0.82f)
                    },
                )
                .clickable(
                    indication = null,
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                ) { onDismiss() },
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .widthIn(max = 360.dp)
                    .graphicsLayer {
                        scaleX = cardScale.value
                        scaleY = cardScale.value
                        translationY = cardY.value
                    }
                    .shadow(24.dp, RoundedCornerShape(32.dp))
                    .clip(RoundedCornerShape(32.dp))
                    .background(LocalRestaurantPalette.current.cardSurface)
                    .clickable(
                        indication = null,
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        enabled = false,
                    ) {},
            ) {
                ModalContent(
                    images = images,
                    titleAlpha = titleAlpha.value,
                    titleY = titleY.value,
                    subtitleAlpha = subtitleAlpha.value,
                    subtitleY = subtitleY.value,
                    buttonAlpha = buttonAlpha.value,
                    buttonY = buttonY.value,
                    onDismiss = onDismiss,
                )
            }
        }
    }
}

@Composable
private fun ModalContent(
    images: List<String>,
    titleAlpha: Float,
    titleY: Float,
    subtitleAlpha: Float,
    subtitleY: Float,
    buttonAlpha: Float,
    buttonY: Float,
    onDismiss: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Top row with close X.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = palette.foreground,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        // Image card with 2x2 assembled tiles.
        Box(
            modifier = Modifier
                .size(192.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(palette.cardSurface),
            contentAlignment = Alignment.Center,
        ) {
            AssembledTiles(images = images.take(4))
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = "We've gathered your recently viewed restaurants",
            color = palette.foreground,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    alpha = titleAlpha
                    translationY = titleY
                },
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Restaurants you viewed from search and discovery stay here so you can compare them later.",
            color = palette.mutedForeground,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    alpha = subtitleAlpha
                    translationY = subtitleY
                },
        )
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .graphicsLayer {
                    alpha = buttonAlpha
                    translationY = buttonY
                }
                .clip(RoundedCornerShape(14.dp))
                .background(palette.foreground)
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Got it",
                color = palette.cardSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
            )
        }
    }
}

private data class TileStart(val x: Float, val y: Float, val rotation: Float)

private val TILE_STARTS = listOf(
    TileStart(-150f, -150f, -60f),
    TileStart(150f, -150f, 60f),
    TileStart(-150f, 150f, -60f),
    TileStart(150f, 150f, 60f),
)

@Composable
private fun AssembledTiles(images: List<String>) {
    val tileSize = 80.dp
    val gap = 6.dp

    val animatables = remember {
        List(4) {
            val start = TILE_STARTS[it]
            Quad(
                x = Animatable(start.x),
                y = Animatable(start.y),
                rotation = Animatable(start.rotation),
                scale = Animatable(0.2f),
            )
        }
    }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100)
        animatables.forEach { quad ->
            launch { quad.x.animateTo(0f, spring(dampingRatio = 0.55f, stiffness = 100f)) }
            launch { quad.y.animateTo(0f, spring(dampingRatio = 0.55f, stiffness = 100f)) }
            launch { quad.rotation.animateTo(0f, spring(dampingRatio = 0.55f, stiffness = 100f)) }
            launch { quad.scale.animateTo(1f, spring(dampingRatio = 0.55f, stiffness = 100f)) }
        }
    }

    Column(
        modifier = Modifier.size(tileSize * 2 + gap),
        verticalArrangement = Arrangement.spacedBy(gap),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
            TileSlot(images.getOrNull(0), tileSize, animatables[0])
            TileSlot(images.getOrNull(1), tileSize, animatables[1])
        }
        Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
            TileSlot(images.getOrNull(2), tileSize, animatables[2])
            TileSlot(images.getOrNull(3), tileSize, animatables[3])
        }
    }
}

private data class Quad(
    val x: Animatable<Float, *>,
    val y: Animatable<Float, *>,
    val rotation: Animatable<Float, *>,
    val scale: Animatable<Float, *>,
)

@Composable
private fun TileSlot(image: String?, tileSize: androidx.compose.ui.unit.Dp, q: Quad) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .size(tileSize)
            .graphicsLayer {
                translationX = q.x.value
                translationY = q.y.value
                rotationZ = q.rotation.value
                scaleX = q.scale.value
                scaleY = q.scale.value
            }
            .shadow(4.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(palette.mutedSurface),
        contentAlignment = Alignment.Center,
    ) {
        if (image != null) {
            AsyncImage(
                model = image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
