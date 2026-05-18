package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainreservation.core.designsystem.R
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/** Premium warm red (Airbnb-adjacent); active asset uses the same hex. */
private val PremiumHeartRed = Color(0xFFFF5A5F)

/** Original assets are 31×28; keep proportion when scaling. */
private const val HEART_ASPECT_RATIO = 31f / 28f

enum class HeartButtonSize(val container: Dp, val icon: Dp) {
    ExtraSmall(container = 24.dp, icon = 18.dp),
    Small(container = 28.dp, icon = 21.dp),
    Medium(container = 36.dp, icon = 27.dp),
    Large(container = 42.dp, icon = 32.dp),
}

enum class HeartButtonStyle {
    /** Circular scrim behind the heart (e.g. search / non-discover contexts). */
    Floating,

    /** Heart only — no circular background (discover, wishlist, overlays on photos). */
    Overlay,
}

/**
 * Wishlist heart artwork from `ic_heart_active` / `ic_heart_inactive` (same as [HeartButton]).
 * Use where a tappable [HeartButton] is not needed (e.g. inline inside another control).
 */
@Composable
fun HeartDrawableIcon(
    active: Boolean,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    iconHeight: Dp = 24.dp,
) {
    Icon(
        painter = painterResource(id = if (active) R.drawable.ic_heart_active else R.drawable.ic_heart_inactive),
        contentDescription = contentDescription,
        modifier = modifier
            .height(iconHeight)
            .aspectRatio(HEART_ASPECT_RATIO),
        tint = Color.Unspecified,
    )
}

@Suppress("UNUSED_PARAMETER")
@Composable
fun HeartButton(
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: HeartButtonSize = HeartButtonSize.Medium,
    style: HeartButtonStyle = HeartButtonStyle.Floating,
    /** When [style] is [HeartButtonStyle.Overlay], aligns the icon inside the tap target (e.g. top-align with a badge). */
    overlayContentAlignment: Alignment = Alignment.Center,
    contentDescription: String = if (active) "Remove from saved" else "Save",
    containerColor: Color = Color.Black.copy(alpha = 0.38f),
    activeContainerColor: Color = Color.Black.copy(alpha = 0.38f),
    inactiveTint: Color = Color.White,
    activeTint: Color? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val fillProgress by animateFloatAsState(
        targetValue = if (active) 1f else 0f,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "heart_fill_crossfade",
    )

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = tween(durationMillis = 90, easing = FastOutSlowInEasing),
        label = "heart_press",
    )

    val bumpScale = remember { Animatable(1f) }
    val glowPulse = remember { Animatable(0f) }
    val sparklePhase = remember { Animatable(0f) }

    var previousActive by remember { mutableStateOf(active) }

    LaunchedEffect(active) {
        val wasActive = previousActive
        previousActive = active

        if (active && !wasActive) {
            bumpScale.snapTo(1f)
            glowPulse.snapTo(0f)
            sparklePhase.snapTo(0f)
            coroutineScope {
                launch {
                    bumpScale.animateTo(
                        targetValue = 1.18f,
                        animationSpec = spring(
                            dampingRatio = 0.72f,
                            stiffness = 560f,
                        ),
                    )
                    bumpScale.animateTo(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = 0.88f,
                            stiffness = 380f,
                        ),
                    )
                }
                launch {
                    glowPulse.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 110, easing = FastOutSlowInEasing),
                    )
                    glowPulse.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 140, easing = LinearOutSlowInEasing),
                    )
                }
                launch {
                    sparklePhase.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 235, easing = FastOutSlowInEasing),
                    )
                    sparklePhase.snapTo(0f)
                }
            }
        } else if (!active && wasActive) {
            bumpScale.snapTo(1f)
            glowPulse.snapTo(0f)
            sparklePhase.snapTo(0f)
            bumpScale.animateTo(
                targetValue = 0.97f,
                animationSpec = tween(durationMillis = 85, easing = FastOutSlowInEasing),
            )
            bumpScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 110, easing = FastOutSlowInEasing),
            )
        }
    }

    val scrimModifier = if (style == HeartButtonStyle.Floating) {
        Modifier
            .clip(CircleShape)
            .background(if (active) activeContainerColor else containerColor)
    } else {
        Modifier
    }

    val inactivePainter = painterResource(R.drawable.ic_heart_inactive)
    val activePainter = painterResource(R.drawable.ic_heart_active)

    Box(
        modifier = modifier
            .size(size.container)
            .then(scrimModifier)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClickLabel = contentDescription,
                onClick = onClick,
            ),
        contentAlignment = if (style == HeartButtonStyle.Overlay) overlayContentAlignment else Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .scale(pressScale * bumpScale.value)
                .height(size.icon)
                .aspectRatio(HEART_ASPECT_RATIO)
                .drawBehind {
                    val pulse = glowPulse.value
                    if (pulse <= 0.001f) return@drawBehind
                    val c = Offset(this.size.width / 2f, this.size.height / 2f)
                    val r = this.size.maxDimension * 0.72f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colorStops = arrayOf(
                                0f to PremiumHeartRed.copy(alpha = 0.32f * pulse),
                                0.45f to PremiumHeartRed.copy(alpha = 0.10f * pulse),
                                1f to Color.Transparent,
                            ),
                            center = c,
                            radius = r,
                        ),
                        radius = r,
                        center = c,
                    )
                },
        ) {
            Icon(
                painter = inactivePainter,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = 1f - fillProgress
                    },
                tint = Color.Unspecified,
            )
            Icon(
                painter = activePainter,
                contentDescription = contentDescription,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = fillProgress
                    },
                tint = Color.Unspecified,
            )
            HeartSparkles(
                progress = sparklePhase.value,
                color = PremiumHeartRed,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

/** Minimal sparkle burst: 3 soft dots drifting outward and fading (luxury, not confetti). */
@Composable
private fun HeartSparkles(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
) {
    if (progress <= 0.001f) return
    val anglesDeg = remember { floatArrayOf(-30f, 22f, 108f, 168f) }
    Canvas(
        modifier.graphicsLayer {
            alpha = (1f - progress * 0.2f).coerceIn(0f, 1f)
        },
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val travel = size.minDimension * 0.42f
        val baseR = size.minDimension * 0.055f
        for (i in anglesDeg.indices) {
            val stagger = i * 0.06f
            val t = ((progress * 1.08f) - stagger).coerceIn(0f, 1f)
            val eased = t * t * (3f - 2f * t)
            val rad = Math.toRadians(anglesDeg[i].toDouble())
            val dist = travel * eased
            val ox = (cos(rad) * dist).toFloat()
            val oy = (sin(rad) * dist).toFloat()
            val alpha = (1f - eased) * 0.36f * (1f - progress * 0.4f)
            drawCircle(
                color = color.copy(alpha = alpha.coerceIn(0f, 0.38f)),
                radius = baseR * (0.7f + 0.2f * eased),
                center = Offset(cx + ox, cy - oy),
            )
        }
    }
}
