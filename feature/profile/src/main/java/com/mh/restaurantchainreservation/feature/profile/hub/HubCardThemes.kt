package com.mh.restaurantchainreservation.feature.profile.hub

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
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

/** sRGB mix: `ratioFirst` is fraction of `c1` (matches CSS `color-mix(in srgb, c1 p%, c2)` with p = ratioFirst*100). */
private fun mixSrgb(c1: Color, c2: Color, ratioFirst: Float): Color {
    val t = ratioFirst.coerceIn(0f, 1f)
    return Color(
        red = c1.red * t + c2.red * (1f - t),
        green = c1.green * t + c2.green * (1f - t),
        blue = c1.blue * t + c2.blue * (1f - t),
        alpha = c1.alpha * t + c2.alpha * (1f - t),
    )
}

private val InkGradient = listOf(
    Color(0xFF1F1F24),
    Color(0xFF111114),
    Color(0xFF050507),
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

/**
 * Credit card themes aligned with web `CARD_THEMES` (135° gradients, glow / highlight / shadow rgba, default pattern per id).
 * Rose gradient and glow use [brandColor] when provided (CSS `color-mix` with `var(--primary)`); otherwise primary defaults to `#FF385C`.
 */
internal fun hubCardThemeSpec(
    id: HubCardThemeId,
    /** When set, Rose theme uses `color-mix`-style stops and glow from this primary (web `var(--primary)`). */
    brandColor: Color? = null,
): HubCardThemeSpec = when (id) {
    HubCardThemeId.Ink -> HubCardThemeSpec(
        id = id,
        gradient = InkGradient,
        glow = Color.White.copy(alpha = 0.08f),
        highlight = Color.White.copy(alpha = 0.10f),
        shadow = Color.Black.copy(alpha = 0.45f),
        pattern = HubCardPattern.Stars,
    )
    HubCardThemeId.Rose -> {
        val primary = brandColor ?: Color(0xFFFF385C)
        val black = Color.Black
        HubCardThemeSpec(
            id = id,
            gradient = listOf(
                mixSrgb(primary, black, 0.70f),
                mixSrgb(primary, black, 0.45f),
                mixSrgb(primary, black, 0.22f),
            ),
            glow = primary.copy(alpha = 0.38f),
            highlight = Color.White.copy(alpha = 0.18f),
            shadow = Color.Black.copy(alpha = 0.30f),
            pattern = HubCardPattern.Blob,
        )
    }
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

/** Same 135° multi-stop gradient as the card canvas (use in `drawBehind { drawRect(...) }`). */
internal fun hubCardThemeBackgroundBrush(
    themeId: HubCardThemeId,
    widthPx: Float,
    heightPx: Float,
    shiftPx: Float = 0f,
    brandColor: Color? = null,
): Brush = hubLinearBgBrush(
    stops = hubCardThemeSpec(themeId, brandColor).gradient,
    shift = shiftPx,
    w = widthPx,
    h = heightPx,
)

internal fun hubCardLabelMuted(themeId: HubCardThemeId): Color = when (themeId) {
    HubCardThemeId.Ocean -> Color(0xFFE0EEFF).copy(alpha = 0.92f)
    HubCardThemeId.Forest -> Color(0xFFD1FAE5).copy(alpha = 0.90f)
    HubCardThemeId.Amethyst -> Color(0xFFF3E8FF).copy(alpha = 0.90f)
    HubCardThemeId.Rose, HubCardThemeId.Sunset -> Color(0xFFFFE4E6).copy(alpha = 0.90f)
    else -> Color.White.copy(alpha = 0.88f)
}

/** Web-fidelity 160° gold foil gradient brush. */
internal fun hubWebCardGoldBrush(): Brush {
    val rad = (160.0 * PI / 180.0).toFloat()
    val ux = sin(rad)
    val uy = -cos(rad)
    val len = 720f
    return Brush.linearGradient(
        0.00f to Color(0xFFFFE9A8),
        0.22f to Color(0xFFFFD56A),
        0.50f to Color(0xFFC9933E),
        0.72f to Color(0xFFFFE9A8),
        1.00f to Color(0xFFC7892F),
        start = Offset.Zero,
        end = Offset(ux * len, uy * len)
    )
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
        3 -> when (stops) {
            OceanGradient -> arrayOf(
                0f to stops[0],
                0.5f to stops[1],
                1f to stops[2],
            )
            else -> arrayOf(
                0f to stops[0],
                0.55f to stops[1],
                1f to stops[2],
            )
        }
        4 -> arrayOf(0f to stops[0], 0.35f to stops[1], 0.72f to stops[2], 1f to stops[3])
        else -> arrayOf(0f to stops.first(), 1f to stops.last())
    }
    // CSS linear-gradient(135deg, …): diagonal top-left → bottom-right
    return Brush.linearGradient(
        colorStops = colorStops,
        start = Offset(-shift * 0.5f, -shift * 0.5f),
        end = Offset(w * 1.02f + shift * 0.5f, h * 1.02f + shift * 0.5f),
    )
}

private data class StarSpec(val xPct: Float, val yPct: Float, val rDp: Float)

/** Dense small-dot field (web-like stipple); `rDp` scales a shared min-side factor. */
private val StarField = listOf(
    StarSpec(0.08f, 0.10f, 0.55f),
    StarSpec(0.22f, 0.08f, 0.45f),
    StarSpec(0.38f, 0.12f, 0.60f),
    StarSpec(0.52f, 0.06f, 0.40f),
    StarSpec(0.68f, 0.11f, 0.50f),
    StarSpec(0.88f, 0.09f, 0.48f),
    StarSpec(0.92f, 0.22f, 0.55f),
    StarSpec(0.74f, 0.20f, 0.42f),
    StarSpec(0.58f, 0.24f, 0.58f),
    StarSpec(0.44f, 0.20f, 0.45f),
    StarSpec(0.28f, 0.22f, 0.52f),
    StarSpec(0.10f, 0.26f, 0.48f),
    StarSpec(0.14f, 0.38f, 0.50f),
    StarSpec(0.32f, 0.36f, 0.44f),
    StarSpec(0.50f, 0.40f, 0.56f),
    StarSpec(0.66f, 0.38f, 0.46f),
    StarSpec(0.84f, 0.36f, 0.52f),
    StarSpec(0.78f, 0.50f, 0.48f),
    StarSpec(0.60f, 0.52f, 0.54f),
    StarSpec(0.40f, 0.54f, 0.42f),
    StarSpec(0.22f, 0.50f, 0.50f),
    StarSpec(0.08f, 0.58f, 0.46f),
    StarSpec(0.18f, 0.68f, 0.52f),
    StarSpec(0.36f, 0.70f, 0.44f),
    StarSpec(0.54f, 0.72f, 0.58f),
    StarSpec(0.72f, 0.70f, 0.46f),
    StarSpec(0.90f, 0.66f, 0.50f),
    StarSpec(0.82f, 0.82f, 0.48f),
    StarSpec(0.62f, 0.86f, 0.54f),
    StarSpec(0.42f, 0.88f, 0.45f),
    StarSpec(0.20f, 0.86f, 0.52f),
    StarSpec(0.48f, 0.62f, 0.47f),
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

/**
 * Web: `linear-gradient(115deg, transparent 0%, transparent 38%, rgba(255,255,255,0.18) 50%,
 * transparent 62%, transparent 100%)` + `opacity-30` + `mix-blend-overlay` over full card.
 */
private fun DrawScope.drawHubCardOverlaySheen(w: Float, h: Float) {
    val rad = (115.0 * PI / 180.0).toFloat()
    val ux = sin(rad)
    val uy = -cos(rad)
    val halfExtent = hypot(w.toDouble(), h.toDouble()).toFloat() * 0.65f + 1f
    val cx = w * 0.5f
    val cy = h * 0.5f
    val start = Offset(cx - ux * halfExtent, cy - uy * halfExtent)
    val end = Offset(cx + ux * halfExtent, cy + uy * halfExtent)
    val brush = Brush.linearGradient(
        0f to Color.Transparent,
        0.38f to Color.Transparent,
        0.5f to Color.White.copy(alpha = 0.18f),
        0.62f to Color.Transparent,
        1f to Color.Transparent,
        start = start,
        end = end,
    )
    drawRect(
        brush = brush,
        alpha = 0.3f,
        blendMode = BlendMode.Overlay,
    )
}

/**
 * Web: `radial-gradient(120% 80% at 100% 0%, rgba(255,255,255,0.22), transparent 55%)` and
 * `radial-gradient(80% 60% at 0% 100%, rgba(0,0,0,0.3), transparent 65%)` on `inset-0`.
 * Compose has no elliptical radial brush here — use corner-centered circles whose radii match
 * the CSS ellipse axes (`max(120%×w, 80%×h)` / `max(80%×w, 60%×h)` as extent).
 */
private fun DrawScope.drawHubCardCornerRadialWash(w: Float, h: Float) {
    val rHighlight = kotlin.math.max(1.2f * w, 0.8f * h)
    val rShadow = kotlin.math.max(0.8f * w, 0.6f * h)
    val hi = Offset(w, 0f)
    val lo = Offset(0f, h)
    drawCircle(
        brush = Brush.radialGradient(
            0f to Color.White.copy(alpha = 0.22f),
            0.55f to Color.Transparent,
            center = hi,
            radius = rHighlight,
        ),
        radius = rHighlight,
        center = hi,
    )
    drawCircle(
        brush = Brush.radialGradient(
            0f to Color.Black.copy(alpha = 0.3f),
            0.65f to Color.Transparent,
            center = lo,
            radius = rShadow,
        ),
        radius = rShadow,
        center = lo,
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
    /** Rose gradient/glow use design-system primary when set (web `var(--primary)`). */
    brandColor: Color? = null,
) {
    val spec = hubCardThemeSpec(themeId, brandColor)
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        drawRect(brush = hubLinearBgBrush(spec.gradient, shift = 0f, w, h))
        drawHubCardPattern(
            pattern = patternOverride ?: spec.pattern,
            highlight = spec.highlight,
            shadow = spec.shadow,
            w = w,
            h = h,
        )
        drawHubCardCornerRadialWash(w = w, h = h)
        drawHubCardOverlaySheen(w = w, h = h)
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
            // Small soft dots (web stipple): tight radial falloff, many placements via [StarField].
            val m = kotlin.math.min(w, h)
            val base = m * 0.0048f
            for (s in StarField) {
                val cx = w * s.xPct
                val cy = h * s.yPct
                val rPx = (base * s.rDp).coerceAtLeast(0.35f * density)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.55f),
                            Color.White.copy(alpha = 0f),
                        ),
                        center = Offset(cx, cy),
                        radius = rPx * 2.05f,
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
            // Web: top-right `h-44 w-44` (176px) + `-top-16 -right-12`; bottom-left `h-52 w-52` (208px) + `-bottom-20 -left-16`.
            // Centers on a 360×224 reference: top (w-40, 24), bottom (40, h-24); radii 88 / 104. Scale by min side vs ref min.
            val refW = 360f
            val refH = 224f
            val refMin = kotlin.math.min(refW, refH)
            val scale = kotlin.math.min(w, h) / refMin
            val rTop = 88f * scale
            val rBot = 104f * scale
            drawCircle(
                color = highlight,
                radius = rTop,
                center = Offset(w * (refW - 40f) / refW, 24f * h / refH),
            )
            drawCircle(
                color = shadow,
                radius = rBot,
                center = Offset(40f * w / refW, h * (refH - 24f) / refH),
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
