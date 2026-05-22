package com.mh.restaurantchainreservation.feature.dining.ui.scanqr

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class Particle(
    val xPercent: Float,
    val drift: Float,
    val color: Color,
    val shape: Int, // 0 circle, 1 rect, 2 star
    val size: Float,
    val delaySec: Float,
    val durationSec: Float,
    val rotation: Float,
)

/**
 * Full-screen confetti emitter overlay shown briefly after payment.
 * Particles fall from the top with subtle horizontal drift.
 */
@Composable
fun ConfettiEffect() {
    val palette = LocalRestaurantPalette.current
    val colors = remember(palette) {
        listOf(
            palette.brand, palette.success, palette.warning, palette.info,
            RestaurantColors.Decoration.confettiPink, RestaurantColors.Decoration.confettiViolet, RestaurantColors.Decoration.confettiYellow, RestaurantColors.Decoration.confettiGreen,
        )
    }
    val particles = remember {
        val random = Random(42)
        List(60) { i ->
            Particle(
                xPercent = 0.2f + random.nextFloat() * 0.6f,
                drift = (random.nextFloat() - 0.5f) * 0.4f,
                color = colors[i % colors.size],
                shape = i % 3,
                size = 4f + random.nextFloat() * 8f,
                delaySec = random.nextFloat() * 0.8f,
                durationSec = 2f + random.nextFloat() * 2f,
                rotation = random.nextFloat() * 720f,
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "confetti")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearOutSlowInEasing),
        ),
        label = "confetti_t",
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            particles.forEach { p ->
                val localT = ((t - p.delaySec / 2.2f) % 1f + 1f) % 1f
                val y = -0.05f + localT * 1.15f
                val x = p.xPercent + p.drift * localT
                val cx = x * size.width
                val cy = y * size.height
                val rotation = p.rotation * localT
                val alpha = when {
                    localT < 0.05f -> 0f
                    localT > 0.9f -> ((1f - localT) / 0.1f).coerceIn(0f, 1f)
                    else -> 1f
                }
                val color = p.color.copy(alpha = (0.7f + (1f - alpha) * 0.3f) * alpha)
                when (p.shape) {
                    0 -> drawCircle(color, radius = p.size, center = Offset(cx, cy))
                    1 -> drawRect(color, topLeft = Offset(cx - p.size / 2f, cy - p.size / 4f), size = androidx.compose.ui.geometry.Size(p.size, p.size * 0.6f))
                    else -> drawStar(color, Offset(cx, cy), p.size * 1.4f, rotation)
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStar(
    color: Color,
    center: Offset,
    radius: Float,
    rotationDeg: Float,
) {
    val path = Path()
    val outer = radius
    val inner = radius * 0.4f
    val rot = (rotationDeg / 180f) * Math.PI.toFloat()
    for (i in 0 until 10) {
        val angle = rot + i * (Math.PI.toFloat() / 5f)
        val r = if (i % 2 == 0) outer else inner
        val x = center.x + cos(angle - Math.PI.toFloat() / 2f) * r
        val y = center.y + sin(angle - Math.PI.toFloat() / 2f) * r
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color)
}
