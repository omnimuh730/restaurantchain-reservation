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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.random.Random

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
    Color(0xFF020208),
    Color(0xFF0C0C14),
    Color(0xFF151520),
    Color(0xFF030306),
)

private val RoseGradient = listOf(
    Color(0xFFFF2D55),
    Color(0xFFB80C28),
    Color(0xFF5C0614),
    Color(0xFF140204),
)

private val AmethystGradient = listOf(
    Color(0xFF9D4DFF),
    Color(0xFF5B21B6),
    Color(0xFF2E0F5C),
    Color(0xFF0A0418),
)

private val OceanGradient = listOf(
    Color(0xFF2563EB),
    Color(0xFF1E3A8A),
    Color(0xFF0F1729),
    Color(0xFF020617),
)

private val SunsetGradient = listOf(
    Color(0xFFFF7B54),
    Color(0xFFDC2626),
    Color(0xFF7F1D1D),
    Color(0xFF1C0707),
)

private val ForestGradient = listOf(
    Color(0xFF10B981),
    Color(0xFF065F46),
    Color(0xFF022C1F),
    Color(0xFF020F0C),
)

internal fun hubCardThemeSpec(id: HubCardThemeId): HubCardThemeSpec = when (id) {
    HubCardThemeId.Ink -> HubCardThemeSpec(
        id = id,
        gradient = InkGradient,
        glow = Color(0xFF3B82F6).copy(alpha = 0.55f),
        highlight = Color.White.copy(alpha = 0.38f),
        shadow = Color.Black.copy(alpha = 0.72f),
        pattern = HubCardPattern.Stars,
    )
    HubCardThemeId.Rose -> HubCardThemeSpec(
        id = id,
        gradient = RoseGradient,
        glow = Color(0xFFFF4D6D).copy(alpha = 0.62f),
        highlight = Color.White.copy(alpha = 0.42f),
        shadow = Color.Black.copy(alpha = 0.58f),
        pattern = HubCardPattern.Blob,
    )
    HubCardThemeId.Amethyst -> HubCardThemeSpec(
        id = id,
        gradient = AmethystGradient,
        glow = Color(0xFFC4A5FF).copy(alpha = 0.58f),
        highlight = Color.White.copy(alpha = 0.40f),
        shadow = Color.Black.copy(alpha = 0.55f),
        pattern = HubCardPattern.Wave,
    )
    HubCardThemeId.Ocean -> HubCardThemeSpec(
        id = id,
        gradient = OceanGradient,
        glow = Color(0xFF60A5FA).copy(alpha = 0.58f),
        highlight = Color.White.copy(alpha = 0.40f),
        shadow = Color.Black.copy(alpha = 0.58f),
        pattern = HubCardPattern.Rays,
    )
    HubCardThemeId.Sunset -> HubCardThemeSpec(
        id = id,
        gradient = SunsetGradient,
        glow = Color(0xFFFFA07A).copy(alpha = 0.58f),
        highlight = Color.White.copy(alpha = 0.40f),
        shadow = Color.Black.copy(alpha = 0.58f),
        pattern = HubCardPattern.Blob,
    )
    HubCardThemeId.Forest -> HubCardThemeSpec(
        id = id,
        gradient = ForestGradient,
        glow = Color(0xFF34D399).copy(alpha = 0.52f),
        highlight = Color.White.copy(alpha = 0.36f),
        shadow = Color.Black.copy(alpha = 0.56f),
        pattern = HubCardPattern.Grid,
    )
}

/** Linear brush for compact theme swatches (picker, list rows). */
internal fun hubCardThemeSwatchBrush(themeId: HubCardThemeId): Brush {
    val colors = hubCardThemeSpec(themeId).gradient
    return Brush.linearGradient(colors)
}

internal fun hubCardLabelMuted(themeId: HubCardThemeId): Color = when (themeId) {
    HubCardThemeId.Ocean -> Color(0xFFE0EEFF).copy(alpha = 0.92f)
    HubCardThemeId.Forest -> Color(0xFFD1FAE5).copy(alpha = 0.90f)
    HubCardThemeId.Amethyst -> Color(0xFFF3E8FF).copy(alpha = 0.90f)
    HubCardThemeId.Rose, HubCardThemeId.Sunset -> Color(0xFFFFE4E6).copy(alpha = 0.90f)
    else -> Color.White.copy(alpha = 0.88f)
}

/** Premium anodized gold for balances, PAN, emboss accents. */
internal fun hubMetalGoldBrush(): Brush = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFFFFFF),
        Color(0xFFFFF8E8),
        Color(0xFFFFE08A),
        Color(0xFFFFB020),
        Color(0xFFE8A040),
        Color(0xFFFFE9B0),
        Color(0xFFC9933E),
        Color(0xFF6B4420),
    ),
    start = Offset(0f, 0f),
    end = Offset(280f, 460f),
)

/** Diagonal brushed gold for EMV chip hardware. */
internal fun hubChipAnodizedBrush(): Brush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF4A3208),
        Color(0xFF8A5612),
        Color(0xFFFFD88A),
        Color(0xFFFFF2D8),
        Color(0xFFC7892F),
        Color(0xFF5C3D0A),
    ),
    start = Offset(0f, 36f),
    end = Offset(100f, -4f),
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

private fun DrawScope.drawVelvetDepthLayer(w: Float, h: Float) {
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                Color.Black.copy(alpha = 0.18f),
                Color.Black.copy(alpha = 0.42f),
            ),
            start = Offset(0f, h * 0.35f),
            end = Offset(0f, h * 1.02f),
        ),
        blendMode = BlendMode.Multiply,
    )
}

private fun DrawScope.drawCinematicVignette(w: Float, h: Float) {
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.52f)),
            center = Offset(w * 0.5f, h * 0.48f),
            radius = kotlin.math.max(w, h) * 0.88f,
        ),
        blendMode = BlendMode.Multiply,
    )
}

/** Large saturated shapes for clear depth planes (foreground vs deep field). */
private fun DrawScope.drawBoldGeometricMasses(themeId: HubCardThemeId, w: Float, h: Float, drift: Float) {
    val ox = (drift - 0.5f) * w * 0.1f
    val oy = (drift - 0.5f) * h * 0.06f
    val (massA, massB) = when (themeId) {
        HubCardThemeId.Ink ->
            Color(0xFFDC143C).copy(alpha = 0.48f) to Color(0xFF2563EB).copy(alpha = 0.42f)
        HubCardThemeId.Rose ->
            Color(0xFFFF0033).copy(alpha = 0.52f) to Color(0xFF1A0006).copy(alpha = 0.58f)
        HubCardThemeId.Amethyst ->
            Color(0xFFC084FC).copy(alpha = 0.52f) to Color(0xFF6D28D9).copy(alpha = 0.55f)
        HubCardThemeId.Ocean ->
            Color(0xFF3B82F6).copy(alpha = 0.55f) to Color(0xFF1E3A8A).copy(alpha = 0.52f)
        HubCardThemeId.Sunset ->
            Color(0xFFFF6B35).copy(alpha = 0.5f) to Color(0xFFB91C1C).copy(alpha = 0.54f)
        HubCardThemeId.Forest ->
            Color(0xFF059669).copy(alpha = 0.48f) to Color(0xFF022C22).copy(alpha = 0.56f)
    }
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(massA, massA.copy(alpha = 0f)),
            center = Offset(w * 0.88f + ox, -h * 0.06f + oy),
            radius = w * 0.68f,
        ),
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(massB, Color.Transparent),
            center = Offset(-w * 0.14f - ox * 0.6f, h * 1.02f - oy),
            radius = h * 0.82f,
        ),
    )
    val slab = Path().apply {
        addRoundRect(
            RoundRect(
                rect = Rect(w * 0.28f + ox * 0.4f, h * 0.48f, w * 1.12f, h * 1.06f),
                cornerRadius = CornerRadius(48f * density, 48f * density),
            ),
        )
    }
    drawPath(
        path = slab,
        brush = Brush.linearGradient(
            colors = listOf(Color.White.copy(alpha = 0.16f), Color.Transparent),
            start = Offset(w * 0.35f, h * 0.52f),
            end = Offset(w * 1.02f, h * 1.02f),
        ),
    )
}

private fun DrawScope.drawBoldMidgroundArc(w: Float, h: Float, drift: Float, strokeColor: Color) {
    val ox = (drift - 0.5f) * w * 0.14f
    val path = Path().apply {
        moveTo(-w * 0.06f + ox, h * 0.38f)
        cubicTo(
            w * 0.28f + ox, h * 0.30f,
            w * 0.62f + ox, h * 0.46f,
            w * 1.08f + ox, h * 0.34f,
        )
        lineTo(w * 1.08f, h * 0.52f)
        cubicTo(
            w * 0.55f + ox, h * 0.62f,
            w * 0.22f + ox, h * 0.48f,
            -w * 0.06f + ox, h * 0.56f,
        )
        close()
    }
    drawPath(path, color = strokeColor.copy(alpha = 0.14f))
}

private fun holoAuroraPair(themeId: HubCardThemeId): Pair<Color, Color> = when (themeId) {
    HubCardThemeId.Ink ->
        Color(0xFF6366F1).copy(alpha = 0.22f) to Color(0xFFF43F5E).copy(alpha = 0.18f)
    HubCardThemeId.Rose ->
        Color(0xFFFFB4C8).copy(alpha = 0.22f) to Color(0xFFFF1744).copy(alpha = 0.2f)
    HubCardThemeId.Amethyst ->
        Color(0xFFE9D5FF).copy(alpha = 0.24f) to Color(0xFFA78BFA).copy(alpha = 0.2f)
    HubCardThemeId.Ocean ->
        Color(0xFF93C5FD).copy(alpha = 0.24f) to Color(0xFF60A5FA).copy(alpha = 0.2f)
    HubCardThemeId.Sunset ->
        Color(0xFFFFC9A8).copy(alpha = 0.22f) to Color(0xFFFF6B6B).copy(alpha = 0.2f)
    HubCardThemeId.Forest ->
        Color(0xFF6EE7B7).copy(alpha = 0.2f) to Color(0xFF34D399).copy(alpha = 0.18f)
}

private fun DrawScope.drawAuroraHolographicFilm(themeId: HubCardThemeId, w: Float, h: Float, drift: Float) {
    val (c0, c1) = holoAuroraPair(themeId)
    val sx = drift * w * 0.28f
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(c0, Color.Transparent, c1, Color.Transparent),
            start = Offset(-w * 0.08f + sx, 0f),
            end = Offset(w * 0.95f + sx, h * 1.02f),
        ),
        blendMode = BlendMode.Screen,
    )
}

private fun DrawScope.drawFilmGrainSubtle(w: Float, h: Float, seed: Int) {
    val rng = Random(seed)
    repeat(72) {
        val gx = rng.nextFloat() * w
        val gy = rng.nextFloat() * h
        val a = 0.006f + rng.nextFloat() * 0.012f
        drawCircle(
            color = Color.White.copy(alpha = a),
            radius = 0.65f * density,
            center = Offset(gx, gy),
        )
    }
}

private fun DrawScope.drawSpecularSweep(w: Float, h: Float, streak: Float) {
    val cx = w * streak
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = 0.26f),
                Color.White.copy(alpha = 0.14f),
                Color.Transparent,
            ),
            start = Offset(cx - w * 0.26f, -h * 0.02f),
            end = Offset(cx + w * 0.12f, h * 1.02f),
        ),
        blendMode = BlendMode.Overlay,
    )
}

private fun DrawScope.drawFrostedGlassSheen(w: Float, h: Float) {
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.26f),
                Color.White.copy(alpha = 0.08f),
                Color.Transparent,
            ),
            start = Offset(0f, 0f),
            end = Offset(0f, h * 0.38f),
        ),
        blendMode = BlendMode.Overlay,
    )
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(Color.White.copy(alpha = 0.18f), Color.Transparent),
            start = Offset(0f, 0f),
            end = Offset(w * 0.72f, h * 0.36f),
        ),
        blendMode = BlendMode.Plus,
    )
}

private fun DrawScope.drawVolumetricKeyLight(spec: HubCardThemeSpec, w: Float, h: Float, drift: Float) {
    val cx = w * (0.72f + (drift - 0.5f) * 0.12f)
    val cy = h * (0.12f + (drift - 0.5f) * 0.1f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(spec.highlight.copy(alpha = 0.58f), Color.Transparent),
            center = Offset(cx, cy),
            radius = w * 0.52f,
        ),
        blendMode = BlendMode.Screen,
    )
}

/** Hard-edged diagonal light for premium glossy material read. */
private fun DrawScope.drawDirectionalLightSlab(w: Float, h: Float) {
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.22f),
                Color.White.copy(alpha = 0.06f),
                Color.Transparent,
            ),
            start = Offset(-w * 0.02f, -h * 0.02f),
            end = Offset(w * 0.62f, h * 0.58f),
        ),
        blendMode = BlendMode.Plus,
    )
}

private fun DrawScope.drawEdgeRimlight(w: Float, h: Float) {
    val r = kotlin.math.min(w, h) * 0.105f
    val inset = 0.6f * density
    val roundRect = RoundRect(
        rect = Rect(inset, inset, w - inset, h - inset),
        cornerRadius = CornerRadius(r, r),
    )
    val path = Path().apply { addRoundRect(roundRect) }
    drawPath(
        path = path,
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.62f),
                Color.White.copy(alpha = 0.22f),
                Color.White.copy(alpha = 0.08f),
                Color.Transparent,
            ),
            start = Offset(0f, 0f),
            end = Offset(w * 0.55f, h * 0.22f),
        ),
        style = Stroke(width = 1.65f * density),
    )
}

@Composable
internal fun HubThemedCardBackground(
    themeId: HubCardThemeId,
    modifier: Modifier = Modifier,
    patternOverride: HubCardPattern? = null,
) {
    val spec = hubCardThemeSpec(themeId)
    val transition = rememberInfiniteTransition(label = "hubCardAmbient")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "drift",
    )
    val streak by transition.animateFloat(
        initialValue = -0.35f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 11_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "specSweep",
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val shift = drift * w * 0.07f
        val stops = spec.gradient

        drawRect(brush = hubLinearBgBrush(stops, shift, w, h))
        drawBoldGeometricMasses(themeId, w, h, drift)
        drawVelvetDepthLayer(w, h)

        drawVolumetricKeyLight(spec, w, h, drift)

        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(spec.highlight.copy(alpha = 0.92f), Color.Transparent),
                center = Offset(w * 1.0f, -h * 0.04f),
                radius = w * 1.05f,
            ),
        )
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(spec.shadow.copy(alpha = 0.92f), Color.Transparent),
                center = Offset(-w * 0.04f, h * 1.04f),
                radius = h * 0.95f,
            ),
        )

        drawBoldMidgroundArc(w, h, drift, Color.White)
        drawHubCardPattern(patternOverride ?: spec.pattern, spec.highlight, spec.shadow, w, h)

        drawAuroraHolographicFilm(themeId, w, h, drift)
        drawFilmGrainSubtle(w, h, seed = themeId.ordinal * 7919 + 13)
        drawSpecularSweep(w, h, streak)

        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.Transparent,
                    Color.White.copy(alpha = 0.24f),
                    Color.Transparent,
                    Color.Transparent,
                ),
                start = Offset(-w * 0.25f, 0f),
                end = Offset(w * 1.15f, h * 1.05f),
            ),
            blendMode = BlendMode.Overlay,
        )

        drawCinematicVignette(w, h)
        drawFrostedGlassSheen(w, h)

        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(Color.White.copy(alpha = 0.2f), Color.Transparent),
                start = Offset(0f, 0f),
                end = Offset(w * 0.4f, h * 0.08f),
            ),
        )
        drawDirectionalLightSlab(w, h)
        drawEdgeRimlight(w, h)
    }
}

/**
 * Card-face pattern layer aligned with web `PatternLayer` (stars / grid / wave / rays / blob).
 * `highlight` and `shadow` come from [HubCardThemeSpec] like the React theme tokens.
 */
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
            // SVG: radialGradient white 0.7 → transparent; circles at % positions, r in px-like units
            for (s in StarField) {
                val cx = w * s.xPct
                val cy = h * s.yPct
                val rPx = kotlin.math.min(w, h) * 0.012f * s.rDp
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.7f),
                            Color.White.copy(alpha = 0f),
                        ),
                        center = Offset(cx, cy),
                        radius = rPx * 3.2f,
                    ),
                    radius = rPx,
                    center = Offset(cx, cy),
                )
            }
        }
        HubCardPattern.Grid -> {
            // CSS: 22px grid, line rgba(255,255,255,0.85), layer opacity 0.07 → effective ~0.06
            val lineAlpha = 0.85f * 0.07f
            val line = Color.White.copy(alpha = lineAlpha)
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
            val sx = w / 320f
            val sy = h / 200f
            val path1 = Path().apply {
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
            val scale = w / 320f
            for (i in 0 until 18) {
                drawLine(
                    color = stroke,
                    start = Offset(w, 0f),
                    end = Offset(w - i * 22f * scale, h),
                    strokeWidth = 1f * density,
                    cap = StrokeCap.Round,
                )
            }
        }
        HubCardPattern.Blob -> {
            // Two offset circles: top-right (h-44 w-44 area), bottom-left (h-52 w-52) — solid theme fills
            val m = kotlin.math.min(w, h)
            val rTop = m * 0.26f
            val rBot = m * 0.31f
            drawCircle(
                color = highlight,
                radius = rTop,
                center = Offset(w + m * 0.02f, -m * 0.08f),
            )
            drawCircle(
                color = shadow,
                radius = rBot,
                center = Offset(-m * 0.06f, h + m * 0.10f),
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
