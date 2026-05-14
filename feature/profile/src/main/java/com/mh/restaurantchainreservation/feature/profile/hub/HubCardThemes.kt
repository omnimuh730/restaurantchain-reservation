package com.mh.restaurantchainreservation.feature.profile.hub

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

internal enum class HubCardThemeId {
    Ink,
    Rose,
    Amethyst,
    Ocean,
    Sunset,
    Forest,
}

internal enum class HubCardPattern {
    Stars,
    Grid,
    Wave,
    Blob,
    Rays,
    None,
}

internal data class HubCardThemeSpec(
    val id: HubCardThemeId,
    val gradient: List<Color>,
    val glow: Color,
    val highlight: Color,
    val shadow: Color,
    val pattern: HubCardPattern,
)

private val InkGradient = listOf(
    Color(0xFF1F1F24),
    Color(0xFF111114),
    Color(0xFF050507),
)

private val RoseGradient = listOf(
    Color(0xFFE85D7A),
    Color(0xFFB3123B),
    Color(0xFF6B0F1E),
    Color(0xFF2A0508),
)

private val AmethystGradient = listOf(
    Color(0xFF6E45FF),
    Color(0xFF3A1F9C),
    Color(0xFF160E47),
)

private val OceanGradient = listOf(
    Color(0xFF1FB2FF),
    Color(0xFF1259D1),
    Color(0xFF061B5E),
)

private val SunsetGradient = listOf(
    Color(0xFFFF8650),
    Color(0xFFE03A3A),
    Color(0xFF6B0F1E),
)

private val ForestGradient = listOf(
    Color(0xFF1FB07A),
    Color(0xFF0E624C),
    Color(0xFF07241D),
)

internal fun hubCardThemeSpec(id: HubCardThemeId): HubCardThemeSpec = when (id) {
    HubCardThemeId.Ink -> HubCardThemeSpec(
        id = id,
        gradient = InkGradient,
        glow = Color.White.copy(alpha = 0.08f),
        highlight = Color.White.copy(alpha = 0.10f),
        shadow = Color.Black.copy(alpha = 0.45f),
        pattern = HubCardPattern.Stars,
    )
    HubCardThemeId.Rose -> HubCardThemeSpec(
        id = id,
        gradient = RoseGradient,
        glow = Color(0xFFE85D7A).copy(alpha = 0.38f),
        highlight = Color.White.copy(alpha = 0.18f),
        shadow = Color.Black.copy(alpha = 0.30f),
        pattern = HubCardPattern.Blob,
    )
    HubCardThemeId.Amethyst -> HubCardThemeSpec(
        id = id,
        gradient = AmethystGradient,
        glow = Color(0xFF7657FF).copy(alpha = 0.40f),
        highlight = Color.White.copy(alpha = 0.22f),
        shadow = Color.Black.copy(alpha = 0.30f),
        pattern = HubCardPattern.Wave,
    )
    HubCardThemeId.Ocean -> HubCardThemeSpec(
        id = id,
        gradient = OceanGradient,
        glow = Color(0xFF1FB2FF).copy(alpha = 0.35f),
        highlight = Color.White.copy(alpha = 0.22f),
        shadow = Color.Black.copy(alpha = 0.32f),
        pattern = HubCardPattern.Rays,
    )
    HubCardThemeId.Sunset -> HubCardThemeSpec(
        id = id,
        gradient = SunsetGradient,
        glow = Color(0xFFFF8650).copy(alpha = 0.40f),
        highlight = Color.White.copy(alpha = 0.22f),
        shadow = Color.Black.copy(alpha = 0.32f),
        pattern = HubCardPattern.Blob,
    )
    HubCardThemeId.Forest -> HubCardThemeSpec(
        id = id,
        gradient = ForestGradient,
        glow = Color(0xFF1FB07A).copy(alpha = 0.35f),
        highlight = Color.White.copy(alpha = 0.20f),
        shadow = Color.Black.copy(alpha = 0.32f),
        pattern = HubCardPattern.Grid,
    )
}

internal fun hubCardLabelMuted(themeId: HubCardThemeId): Color = when (themeId) {
    HubCardThemeId.Ocean -> Color(0xFFB8DCFF).copy(alpha = 0.82f)
    HubCardThemeId.Forest -> Color(0xFFB8F5DE).copy(alpha = 0.78f)
    else -> Color.White.copy(alpha = 0.70f)
}

/** Gold “money” metal gradient (CSS METAL_GRADIENT analogue). */
internal fun hubMetalGoldBrush(): Brush = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFFE9A8),
        Color(0xFFFFD56A),
        Color(0xFFC9933E),
        Color(0xFFFFE9A8),
        Color(0xFFC7892F),
    ),
    start = Offset(0f, 0f),
    end = Offset(220f, 420f),
)

internal fun hubMetalSilverBrush(): Brush = Brush.linearGradient(
    colors = listOf(
        Color(0xFFF4F4F8),
        Color(0xFFC9C9D5),
        Color(0xFF8B8B98),
        Color(0xFFF0F0F5),
        Color(0xFFB0B0BD),
    ),
    start = Offset(0f, 0f),
    end = Offset(200f, 400f),
)

private fun hubLinearBgBrush(stops: List<Color>, shift: Float, w: Float, h: Float): Brush {
    val colorStops: Array<Pair<Float, Color>> = when (stops.size) {
        3 -> arrayOf(0f to stops[0], 0.55f to stops[1], 1f to stops[2])
        4 -> arrayOf(0f to stops[0], 0.35f to stops[1], 0.72f to stops[2], 1f to stops[3])
        else -> arrayOf(0f to stops.first(), 1f to stops.last())
    }
    return Brush.linearGradient(
        colorStops = colorStops,
        start = Offset(-shift, h * 0.15f),
        end = Offset(w * 1.05f + shift, h * 0.95f),
    )
}

private data class StarSpec(val xPct: Float, val yPct: Float, val rDp: Float)

private val StarField = listOf(
    StarSpec(0.12f, 0.18f, 1.6f),
    StarSpec(0.56f, 0.08f, 1.0f),
    StarSpec(0.80f, 0.22f, 1.4f),
    StarSpec(0.22f, 0.42f, 0.9f),
    StarSpec(0.70f, 0.48f, 1.2f),
    StarSpec(0.42f, 0.60f, 1.0f),
    StarSpec(0.18f, 0.78f, 1.5f),
    StarSpec(0.86f, 0.78f, 1.0f),
    StarSpec(0.62f, 0.86f, 0.8f),
)

@Composable
internal fun HubThemedCardBackground(
    themeId: HubCardThemeId,
    modifier: Modifier = Modifier,
) {
    val spec = hubCardThemeSpec(themeId)
    val transition = rememberInfiniteTransition(label = "hubCardAmbient")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "drift",
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val shift = drift * w * 0.08f
        val stops = spec.gradient

        drawRect(brush = hubLinearBgBrush(stops, shift, w, h))

        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(spec.highlight.copy(alpha = 0.9f), Color.Transparent),
                center = Offset(w * 1.0f, -h * 0.05f),
                radius = w * 1.1f,
            ),
        )
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(spec.shadow.copy(alpha = 0.85f), Color.Transparent),
                center = Offset(-w * 0.05f, h * 1.05f),
                radius = h * 1.0f,
            ),
        )

        drawHubCardPattern(spec.pattern, spec.highlight, spec.shadow, w, h)

        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.Transparent,
                    Color.White.copy(alpha = 0.18f),
                    Color.Transparent,
                    Color.Transparent,
                ),
                start = Offset(-w * 0.2f, 0f),
                end = Offset(w * 1.2f, h * 1.1f),
            ),
            blendMode = BlendMode.Overlay,
        )

        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(Color.White.copy(alpha = 0.16f), Color.Transparent),
                start = Offset(0f, 0f),
                end = Offset(w * 0.45f, h * 0.09f),
            ),
            topLeft = Offset(0f, 0f),
            size = Size(w, h * 0.10f),
        )
    }
}

private fun DrawScope.drawHubCardPattern(
    pattern: HubCardPattern,
    highlight: Color,
    shadow: Color,
    w: Float,
    h: Float,
) {
    when (pattern) {
        HubCardPattern.None -> Unit
        HubCardPattern.Stars -> {
            for (s in StarField) {
                val cx = w * s.xPct
                val cy = h * s.yPct
                val r = (kotlin.math.min(w, h) * 0.014f) * (s.rDp / 1.4f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.68f),
                            Color.White.copy(alpha = 0.12f),
                            Color.Transparent,
                        ),
                        center = Offset(cx, cy),
                        radius = r * 2.2f,
                    ),
                    radius = r,
                    center = Offset(cx, cy),
                )
            }
        }
        HubCardPattern.Grid -> {
            val line = Color.White.copy(alpha = 0.07f * 0.85f)
            val step = 22f * density
            var x = 0f
            while (x <= w) {
                drawLine(line, Offset(x, 0f), Offset(x, h), strokeWidth = 1f * density)
                x += step
            }
            var y = 0f
            while (y <= h) {
                drawLine(line, Offset(0f, y), Offset(w, y), strokeWidth = 1f * density)
                y += step
            }
        }
        HubCardPattern.Wave -> {
            val path1 = Path().apply {
                val sx = w / 320f
                val sy = h / 200f
                moveTo(0f, 140f * sy)
                cubicTo(
                    80f * sx, 90f * sy,
                    200f * sx, 180f * sy,
                    320f * sx, 110f * sy,
                )
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(path1, color = shadow.copy(alpha = 0.55f))
            val path2 = Path().apply {
                val sx = w / 320f
                val sy = h / 200f
                moveTo(0f, 160f * sy)
                cubicTo(
                    100f * sx, 100f * sy,
                    220f * sx, 200f * sy,
                    320f * sx, 140f * sy,
                )
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(path2, color = highlight.copy(alpha = 0.25f))
        }
        HubCardPattern.Rays -> {
            val stroke = Color.White.copy(alpha = 0.10f)
            for (i in 0 until 18) {
                drawLine(
                    color = stroke,
                    start = Offset(w, 0f),
                    end = Offset(w - i * 22f * (w / 320f), h),
                    strokeWidth = 1f * density,
                    cap = StrokeCap.Round,
                )
            }
        }
        HubCardPattern.Blob -> {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(highlight.copy(alpha = 0.55f), Color.Transparent),
                    center = Offset(w * 1.08f, -h * 0.08f),
                    radius = w * 0.38f,
                ),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(shadow.copy(alpha = 0.45f), Color.Transparent),
                    center = Offset(-w * 0.06f, h * 1.12f),
                    radius = h * 0.42f,
                ),
            )
        }
    }
}

@Composable
internal fun HubContactlessIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White.copy(alpha = 0.88f),
) {
    Canvas(modifier.size(24.dp)) {
        val stroke = Stroke(width = 1.65f * density, cap = StrokeCap.Round)
        val w = size.width
        val h = size.height
        val m = kotlin.math.min(w, h)
        val cx = w * 0.42f
        val cy = h * 0.5f
        listOf(0.34f, 0.48f, 0.62f).forEach { f ->
            val d = m * f
            drawArc(
                color = tint,
                startAngle = -58f,
                sweepAngle = 116f,
                useCenter = false,
                topLeft = Offset(cx - d * 0.5f, cy - d * 0.5f),
                size = Size(d, d),
                style = stroke,
            )
        }
    }
}
