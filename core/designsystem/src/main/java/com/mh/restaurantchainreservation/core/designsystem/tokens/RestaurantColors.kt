package com.mh.restaurantchainreservation.core.designsystem.tokens

import androidx.compose.ui.graphics.Color

/**
 * Single source of truth for the app color system (light mode only).
 *
 * **To retheme the app:** edit hex values in [Primitives] only. Brand tints, modal
 * gradients, and soft surfaces are derived from [Primitives.BrandPrimary].
 */
object RestaurantColors {

    // ─── Primitives (edit these) ───────────────────────────────────────────────

    object Primitives {
        /** Primary brand / CTA pink */
        const val BrandPrimary: Long = 0xFFFF385C

        /** Titles, primary text */
        const val TextPrimary: Long = 0xFF222222
        /** Body copy, paragraphs */
        const val TextBody: Long = 0xFF484848
        /** Subtitles, captions, muted labels */
        const val TextSecondary: Long = 0xFF767676
        /** Disabled text, placeholders */
        const val TextTertiary: Long = 0xFFB0B0B0

        /** Cards, buttons, elevated UI */
        const val SurfaceCard: Long = 0xFFFFFFFF
        /** Page background, alternating blocks */
        const val SurfacePage: Long = 0xFFF7F7F7

        /** 1px solid dividers and component borders */
        const val BorderDivider: Long = 0xFFEBEBEB

        const val Destructive: Long = 0xFFC13515
        const val Success: Long = 0xFF008A05
        const val Warning: Long = 0xFFE07912
        const val Info: Long = 0xFF428BFF
        const val Gold: Long = 0xFFF59E0B

        const val DigestWarm: Long = 0xFFFAF9F5

        // Accent chips (container / on-container pairs)
        const val BlueContainer: Long = 0xFFDBEAFE
        const val BlueOnContainer: Long = 0xFF2563EB
        const val EmeraldContainer: Long = 0xFFD1FAE5
        const val EmeraldOnContainer: Long = 0xFF059669
        const val OrangeContainer: Long = 0xFFFFEDD5
        const val OrangeOnContainer: Long = 0xFFEA580C
        const val AmberContainer: Long = 0xFFFEF3C7
        const val AmberOnContainer: Long = 0xFFD97706
        const val VioletContainer: Long = 0xFFEDE9FE
        const val VioletOnContainer: Long = 0xFF7C3AED
        const val SlateContainer: Long = 0xFFE2E8F0
        const val SlateOnContainer: Long = 0xFF475569
    }

    // ─── Brand (derived from Primitives.BrandPrimary) ───────────────────────

    object Brand {
        val primary: Color = Primitives.BrandPrimary.c
        val strong: Color get() = primary.darken(0.82f)
        val softSurface: Color get() = primary.blendWithWhite(0.07f)
        val lightTint: Color get() = primary.blendWithWhite(0.04f)
        val softTint: Color get() = primary.blendWithWhite(0.08f)
        val border: Color get() = primary.copy(alpha = 0.12f)
        val shadow: Color get() = primary.copy(alpha = 0.10f)
        val radialHighlight: Color get() = Color.White.copy(alpha = 0.9f)
        val gradientStart: Color get() = primary.blendWithWhite(0.32f)
        val gradientMid: Color get() = primary
        val gradientEnd: Color get() = primary.darken(0.75f)
        val accentContainer: Color get() = primary.blendWithWhite(0.12f)
        val accentOnContainer: Color get() = strong
    }

    // ─── Text ──────────────────────────────────────────────────────────────────

    object Text {
        val primary: Color = Primitives.TextPrimary.c
        val body: Color = Primitives.TextBody.c
        val secondary: Color = Primitives.TextSecondary.c
        val tertiary: Color = Primitives.TextTertiary.c
    }

    // ─── Surfaces ──────────────────────────────────────────────────────────────

    object Surface {
        val card: Color = Primitives.SurfaceCard.c
        val page: Color = Primitives.SurfacePage.c
        val muted: Color = Primitives.SurfacePage.c
    }

    // ─── Borders ───────────────────────────────────────────────────────────────

    object Border {
        /** Standard 1px divider / border color (#EBEBEB). */
        val divider: Color = Primitives.BorderDivider.c
        val default: Color get() = divider
        /** @deprecated Same as [divider]; use [divider] for new code. */
        val soft: Color get() = divider
    }

    object Divider {
        const val ThicknessDp: Float = 1f
    }

    // ─── Semantic ─────────────────────────────────────────────────────────────

    object Semantic {
        val destructive: Color = Primitives.Destructive.c
        val success: Color = Primitives.Success.c
        val warning: Color = Primitives.Warning.c
        val info: Color = Primitives.Info.c
        val gold: Color = Primitives.Gold.c
        val goldSoft: Color = Primitives.Gold.c.copy(alpha = 0.10f)
        val rose: Color = Brand.primary
        val roseSoft: Color = Brand.softTint
        val digestWarm: Color = Primitives.DigestWarm.c
    }

    // ─── Accent pairs (chips, badges) ──────────────────────────────────────────

    object Accent {
        val blue = Primitives.BlueContainer.c to Primitives.BlueOnContainer.c
        val emerald = Primitives.EmeraldContainer.c to Primitives.EmeraldOnContainer.c
        val orange = Primitives.OrangeContainer.c to Primitives.OrangeOnContainer.c
        val amber = Primitives.AmberContainer.c to Primitives.AmberOnContainer.c
        val violet = Primitives.VioletContainer.c to Primitives.VioletOnContainer.c
        val slate = Primitives.SlateContainer.c to Primitives.SlateOnContainer.c
        val pink = Brand.accentContainer to Brand.accentOnContainer
    }

    // ─── Shadows (alpha tokens for Compose elevation) ──────────────────────────

    object Shadow {
        const val HubAmbientAlpha = 0.18f
        const val HubSpotAlpha = 0.38f
    }
}

private val Long.c: Color get() = Color(this)

internal fun blend(start: Color, end: Color, fraction: Float): Color {
    val t = fraction.coerceIn(0f, 1f)
    return Color(
        red = start.red + (end.red - start.red) * t,
        green = start.green + (end.green - start.green) * t,
        blue = start.blue + (end.blue - start.blue) * t,
        alpha = start.alpha + (end.alpha - start.alpha) * t,
    )
}

private fun Color.blendWithWhite(fraction: Float): Color = blend(Color.White, this, fraction)

private fun Color.darken(factor: Float): Color {
    val f = factor.coerceIn(0f, 1f)
    return Color(red = red * f, green = green * f, blue = blue * f, alpha = alpha)
}
