package com.mh.restaurantchainreservation.core.designsystem.tokens

import androidx.compose.ui.graphics.Color

/**
 * Single source of truth for the app color system (light mode only).
 *
 * **Airbnb core palette** (edit in [Primitives] first):
 * | Token | Hex | Role |
 * |-------|-----|------|
 * | White | `#FFFFFF` | Canvas background and main card surface |
 * | Foggy | `#F7F7F7` | Secondary fills — unselected pills, container blocks |
 * | Border | `#EBEBEB` | 1px dividers and card outlines |
 * | Muted | `#717171` | Placeholder copy, unselected tabs, secondary text/icons |
 * | Charcoal | `#222222` | Primary titles and bold body copy |
 *
 * Brand pink and feature-specific colors extend [Primitives] below.
 */
object RestaurantColors {

    // ─── Primitives (edit these) ───────────────────────────────────────────────

    object Primitives {
        const val White: Long = 0xFFFFFFFF
        const val Black: Long = 0xFF000000

        /** Airbnb White — canvas and card surfaces */
        const val AirbnbWhite: Long = White
        /** Airbnb Foggy — muted surfaces, unselected chips */
        const val AirbnbFoggy: Long = 0xFFF7F7F7
        /** Airbnb Borders — dividers and outlines */
        const val AirbnbBorder: Long = 0xFFEBEBEB
        /** Airbnb Muted — secondary text, placeholders, inactive tabs */
        const val AirbnbMuted: Long = 0xFF717171
        /** Airbnb Charcoal (Babu) — primary titles and bold body */
        const val AirbnbCharcoal: Long = 0xFF222222

        /** Primary brand / CTA pink */
        const val BrandPrimary: Long = 0xFFFF385C

        const val TextPrimary: Long = AirbnbCharcoal
        const val TextBody: Long = AirbnbCharcoal
        const val TextSecondary: Long = AirbnbMuted
        const val TextTertiary: Long = AirbnbMuted

        const val SurfaceCard: Long = AirbnbWhite
        const val SurfacePage: Long = AirbnbWhite
        const val SurfaceMuted: Long = AirbnbFoggy

        const val BorderDivider: Long = AirbnbBorder

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

        // Extended primitives
        const val HeartRed: Long = 0xFFFF5A5F
        const val DeepPink: Long = 0xFFE91E63
        const val ReservePink: Long = 0xFFE31C5F
        const val StarGold: Long = 0xFFFFB400
        const val StarAmber: Long = 0xFFF5A623
        const val StarYellow: Long = 0xFFEAB308
        const val ErrorBright: Long = 0xFFEF4444
        const val ErrorSurface: Long = 0xFFFEE2E2
        const val SuccessSurface: Long = 0xFFE8F5E9
        const val SuccessDark: Long = 0xFF008A44
        const val SuccessOpenBg: Long = 0xFFE5F6ED

        const val Placeholder: Long = AirbnbMuted
        const val IconMuted: Long = AirbnbMuted
        const val Skeleton: Long = 0xFFD1D1D1
        const val ChipLight: Long = AirbnbFoggy
        const val Chip: Long = AirbnbFoggy
        const val DividerAlt: Long = AirbnbBorder
        const val InputSurface: Long = AirbnbFoggy
        const val ImagePlaceholder: Long = 0xFFE8EAED
        const val QrForeground: Long = 0xFF1A1A1A

        const val MapCanvas: Long = 0xFFF4F0E8
        const val MapGradientTop: Long = 0xFFF6F7F8
        const val MapGradientBottom: Long = 0xFFEFF3F7
        const val MapMarkerBlue: Long = 0xFF3B82F6

        const val ImmersiveBlack: Long = 0xFF000000
        const val ImmersivePanel: Long = 0xFF1C1C1C
        const val ImmersiveClose: Long = 0xFF3A3A3C

        // Payment providers
        const val PaymentApple: Long = 0xFF111111
        const val PaymentAppleSurface: Long = 0xFFEFEFEF
        const val PaymentGoogle: Long = 0xFF1976D2
        const val PaymentGoogleSurface: Long = 0xFFE3F2FD
        const val PaymentPaypal: Long = 0xFFE39A1A
        const val PaymentPaypalSurface: Long = 0xFFFFF4E0
        const val PaymentBank: Long = 0xFF0D9D63
        const val PaymentBankSurface: Long = 0xFFE6F5EE

        // Currency
        const val CurrencyKrwContainer: Long = 0xFFFFE4E6
        const val CurrencyKrwContent: Long = 0xFFE91E63
        const val CurrencyUsdContainer: Long = 0xFFE0F2FE
        const val CurrencyUsdContent: Long = 0xFF1976D2
        const val CurrencyUsdContentDark: Long = 0xFF1565C0

        // Dining NextUp
        const val DiningPink: Long = 0xFFEF3F67
        const val DiningPinkSoft: Long = 0xFFFDEAF0
        const val DiningPinkLine: Long = 0xFFF6A0B5
        const val DiningTextDark: Long = 0xFF242424
        const val DiningTextGray: Long = 0xFF686868
        const val DiningDash: Long = 0xFFD8D8D8

        // Tag chip gradient (Foggy family)
        const val TagChipLight: Long = AirbnbFoggy
        const val TagChipMid: Long = AirbnbFoggy

        // Hub card theme primitives
        const val HubMidnight1: Long = 0xFF1F1F24
        const val HubMidnight2: Long = 0xFF111114
        const val HubMidnight3: Long = 0xFF050507
        const val HubAmethyst1: Long = 0xFF6E45FF
        const val HubAmethyst2: Long = 0xFF3A1F9C
        const val HubAmethyst3: Long = 0xFF160E47
        const val HubOcean1: Long = 0xFF1FB2FF
        const val HubOcean2: Long = 0xFF1259D1
        const val HubOcean3: Long = 0xFF061B5E
        const val HubSunset1: Long = 0xFFFF8650
        const val HubSunset2: Long = 0xFFE03A3A
        const val HubSunset3: Long = 0xFF6B0F1E
        const val HubForest1: Long = 0xFF1FB07A
        const val HubForest2: Long = 0xFF0E624C
        const val HubForest3: Long = 0xFF07241D
        const val HubGlowAmethyst: Long = 0xFF7657FF
        const val HubGlowOcean: Long = 0xFF1FB2FF
        const val HubGlowSunset: Long = 0xFFFF8650
        const val HubGlowForest: Long = 0xFF1FB07A
        const val HubChipOcean: Long = 0xFFE0EEFF
        const val HubChipForest: Long = 0xFFD1FAE5
        const val HubChipAmethyst: Long = 0xFFF3E8FF
        const val HubChipRose: Long = 0xFFFFE4E6
        const val HubGold1: Long = 0xFFFFE9A8
        const val HubGold2: Long = 0xFFFFD56A
        const val HubGold3: Long = 0xFFC9933E
        const val HubGold4: Long = 0xFFC7892F
        const val HubGold5: Long = 0xFFFFF8E8
        const val HubGold6: Long = 0xFFFFE08A
        const val HubGold7: Long = 0xFFFFB020
        const val HubGold8: Long = 0xFFE8A040
        const val HubGold9: Long = 0xFFFFE9B0
        const val HubGold10: Long = 0xFF6B4420
        const val HubGold11: Long = 0xFF4A3208
        const val HubGold12: Long = 0xFF8A5612
        const val HubGold13: Long = 0xFFFFD88A
        const val HubGold14: Long = 0xFFFFF2D8
        const val HubGold15: Long = 0xFF5C3D0A
        const val HubSilver1: Long = 0xFFF4F4F8
        const val HubSilver2: Long = 0xFFC9C9D5
        const val HubSilver3: Long = 0xFF8B8B98
        const val HubSilver4: Long = 0xFFF0F0F5
        const val HubSilver5: Long = 0xFFB0B0BD

        // Tier medals
        const val TierSilverBg: Long = 0xFF94A3B8
        const val TierSilverRing: Long = 0xFF64748B
        const val TierSilverSymbol: Long = 0xFFE2E8F0
        const val TierGoldBg: Long = 0xFFF59E0B
        const val TierGoldRing: Long = 0xFFD97706
        const val TierGoldSymbol: Long = 0xFFFEF3C7
        const val TierPlatinumBg: Long = 0xFF8B5CF6
        const val TierPlatinumRing: Long = 0xFF7C3AED
        const val TierPlatinumSymbolMain: Long = 0xFFE0E7FF
        const val TierPlatinumSymbolAccent: Long = 0xFFC4B5FD
        const val TierDiamondBg: Long = 0xFFEC4899
        const val TierDiamondRing: Long = 0xFFDB2777
        const val TierDiamondSymbolMain: Long = 0xFFFCE7F3
        const val TierDiamondSymbolAccent: Long = 0xFFFBCFE8
        const val TierSparkle: Long = 0xFFFDE68A

        // Avatar pool
        const val AvatarRose: Long = 0xFFE11D48
        const val AvatarBlue: Long = 0xFF2563EB
        const val AvatarEmerald: Long = 0xFF059669
        const val AvatarAmber: Long = 0xFFD97706
        const val AvatarViolet: Long = 0xFF7C3AED
        const val AvatarCyan: Long = 0xFF0891B2
        const val AvatarRed: Long = 0xFFDC2626
        const val AvatarTeal: Long = 0xFF0D9488
        const val AvatarPink: Long = 0xFFDB2777

        // Confetti
        const val ConfettiPink: Long = 0xFFF472B6
        const val ConfettiViolet: Long = 0xFFA78BFA
        const val ConfettiYellow: Long = 0xFFFBBF24
        const val ConfettiGreen: Long = 0xFF34D399

        // Gift gradients
        const val GiftBlueStart: Long = 0xFF2563EB
        const val GiftBlueEnd: Long = 0xFF0EA5E9

        const val AuthScrim: Long = 0xFF3D4F63
    }

    // ─── Base ────────────────────────────────────────────────────────────────────

    object Base {
        val white: Color = Primitives.White.c
        val black: Color = Primitives.Black.c
        fun whiteAlpha(alpha: Float): Color = white.copy(alpha = alpha)
        fun blackAlpha(alpha: Float): Color = black.copy(alpha = alpha)
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
        val radialHighlight: Color get() = Base.whiteAlpha(0.9f)
        val gradientStart: Color get() = primary.blendWithWhite(0.32f)
        val gradientMid: Color get() = primary
        val gradientEnd: Color get() = primary.darken(0.75f)
        val accentContainer: Color get() = primary.blendWithWhite(0.12f)
        val accentOnContainer: Color get() = strong
        val heartRed: Color = Primitives.HeartRed.c
        val deepPink: Color = Primitives.DeepPink.c
        val reservePink: Color = Primitives.ReservePink.c
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
        /** Airbnb White — page canvas and cards */
        val card: Color = Primitives.SurfaceCard.c
        val page: Color = Primitives.SurfacePage.c
        /** Alias for [page]; use for every full-screen background */
        val canvas: Color = page
        /** Airbnb Foggy — unselected pills, chips, input wells only (not page canvas) */
        val muted: Color = Primitives.SurfaceMuted.c
        val foggy: Color = muted
    }

    // ─── Borders ───────────────────────────────────────────────────────────────

    object Border {
        /** Airbnb Borders `#EBEBEB` */
        val divider: Color = Primitives.BorderDivider.c
        val default: Color get() = divider
        /** @deprecated Same as [divider]; use [divider] for new code. */
        val soft: Color get() = divider
    }

    /** Airbnb five-color palette — semantic accessors for the core system. */
    object Airbnb {
        val white: Color = Primitives.AirbnbWhite.c
        val foggy: Color = Primitives.AirbnbFoggy.c
        val border: Color = Primitives.AirbnbBorder.c
        val muted: Color = Primitives.AirbnbMuted.c
        val charcoal: Color = Primitives.AirbnbCharcoal.c
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
        val heart: Color = Brand.heartRed
        val starGold: Color = Primitives.StarGold.c
        val starAmber: Color = Primitives.StarAmber.c
        val starYellow: Color = Primitives.StarYellow.c
        val errorBright: Color = Primitives.ErrorBright.c
        val errorSurface: Color = Primitives.ErrorSurface.c
        val successSurface: Color = Primitives.SuccessSurface.c
        val successDark: Color = Primitives.SuccessDark.c
        val successOpenBg: Color = Primitives.SuccessOpenBg.c
    }

    // ─── Overlay scrims & gradients ────────────────────────────────────────────

    object Overlay {
        val scrimLight: Color = Base.blackAlpha(0.30f)
        val scrimMedium: Color = Base.blackAlpha(0.35f)
        val scrimHeavy: Color = Base.blackAlpha(0.50f)
        val scrimModal: Color = Base.blackAlpha(0.40f)
        val scrimStrong: Color = Base.blackAlpha(0.55f)
        val scrimPhoto: Color = Base.blackAlpha(0.38f)
        val scrimHeart: Color = Base.blackAlpha(0.38f)
        val imageGradientMid: Color = Base.blackAlpha(0.38f)
        val imageGradientEnd: Color = Base.blackAlpha(0.82f)
        val imageCaption: Color = Base.whiteAlpha(0.92f)
        val veilLight: Color = Base.whiteAlpha(0.38f)
        val veilHeavy: Color = Base.whiteAlpha(0.82f)
        val veilFrosted: Color = Base.whiteAlpha(0.95f)
        val shadowNeutral: Color = Base.blackAlpha(0.05f)
        val borderSubtle: Color = Base.blackAlpha(0.08f)
        val textOnImageMuted: Color = Base.whiteAlpha(0.85f)
        val textOnImageDim: Color = Base.whiteAlpha(0.70f)
        val authScrim: Color = Primitives.AuthScrim.c.copy(alpha = 0.32f)
    }

    // ─── Map ───────────────────────────────────────────────────────────────────

    object Map {
        val canvas: Color = Primitives.MapCanvas.c
        val gradientTop: Color = Primitives.MapGradientTop.c
        val gradientBottom: Color = Primitives.MapGradientBottom.c
        val markerBlue: Color = Primitives.MapMarkerBlue.c
        val markerRing: Color = markerBlue.copy(alpha = 0.35f)
        val markerHalo: Color = markerBlue.copy(alpha = 0.14f)
        val markerFill: Color = markerBlue.copy(alpha = 0.12f)
        val road: Color = Base.whiteAlpha(0.95f)
        val pinHighlight: Color = markerBlue.copy(alpha = 0.20f)
    }

    // ─── Neutral UI ────────────────────────────────────────────────────────────

    object Neutral {
        val placeholder: Color = Primitives.Placeholder.c
        val iconMuted: Color = Primitives.IconMuted.c
        val skeleton: Color = Primitives.Skeleton.c
        val chipLight: Color = Primitives.ChipLight.c
        val chip: Color = Primitives.Chip.c
        val dividerAlt: Color = Primitives.DividerAlt.c
        val inputSurface: Color = Primitives.InputSurface.c
        val imagePlaceholder: Color = Primitives.ImagePlaceholder.c
        val qrForeground: Color = Primitives.QrForeground.c
        val tagChipLight: Color = Primitives.TagChipLight.c
        val tagChipMid: Color = Primitives.TagChipMid.c
        val disabledIcon: Color = Text.primary.copy(alpha = 0.35f)
        val disabledText: Color = Text.primary.copy(alpha = 0.8f)
    }

    // ─── Payment ───────────────────────────────────────────────────────────────

    object Payment {
        val apple: Color = Primitives.PaymentApple.c
        val appleSurface: Color = Primitives.PaymentAppleSurface.c
        val google: Color = Primitives.PaymentGoogle.c
        val googleSurface: Color = Primitives.PaymentGoogleSurface.c
        val paypal: Color = Primitives.PaymentPaypal.c
        val paypalSurface: Color = Primitives.PaymentPaypalSurface.c
        val bank: Color = Primitives.PaymentBank.c
        val bankSurface: Color = Primitives.PaymentBankSurface.c
    }

    // ─── Currency ────────────────────────────────────────────────────────────────

    object Currency {
        val krwContainer: Color = Primitives.CurrencyKrwContainer.c
        val krwContent: Color = Primitives.CurrencyKrwContent.c
        val usdContainer: Color = Primitives.CurrencyUsdContainer.c
        val usdContent: Color = Primitives.CurrencyUsdContent.c
        val usdContentDark: Color = Primitives.CurrencyUsdContentDark.c
    }

    // ─── Immersive (promotions, full-bleed) ────────────────────────────────────

    object Immersive {
        val black: Color = Primitives.ImmersiveBlack.c
        val panel: Color = Primitives.ImmersivePanel.c
        val closeScrim: Color = Primitives.ImmersiveClose.c
    }

    // ─── Dining ────────────────────────────────────────────────────────────────

    object Dining {
        val pink: Color = Primitives.DiningPink.c
        val pinkSoft: Color = Primitives.DiningPinkSoft.c
        val pinkLine: Color = Primitives.DiningPinkLine.c
        val textDark: Color = Primitives.DiningTextDark.c
        val textGray: Color = Primitives.DiningTextGray.c
        val dash: Color = Primitives.DiningDash.c
    }

    // ─── Decoration ────────────────────────────────────────────────────────────

    object Decoration {
        val confettiPink: Color = Primitives.ConfettiPink.c
        val confettiViolet: Color = Primitives.ConfettiViolet.c
        val confettiYellow: Color = Primitives.ConfettiYellow.c
        val confettiGreen: Color = Primitives.ConfettiGreen.c
        val confetti: List<Color> = listOf(confettiPink, confettiViolet, confettiYellow, confettiGreen)
        val giftBlueStart: Color = Primitives.GiftBlueStart.c
        val giftBlueEnd: Color = Primitives.GiftBlueEnd.c
    }

    // ─── Avatar contact pool ───────────────────────────────────────────────────

    object Avatar {
        val pool: List<Color> = listOf(
            Primitives.AvatarRose.c,
            Primitives.AvatarBlue.c,
            Primitives.AvatarEmerald.c,
            Primitives.AvatarAmber.c,
            Primitives.AvatarViolet.c,
            Primitives.AvatarCyan.c,
            Primitives.AvatarRed.c,
            Primitives.AvatarTeal.c,
        )
        val rose: Color = Primitives.AvatarRose.c
        val blue: Color = Primitives.AvatarBlue.c
        val emerald: Color = Primitives.AvatarEmerald.c
        val amber: Color = Primitives.AvatarAmber.c
        val violet: Color = Primitives.AvatarViolet.c
        val cyan: Color = Primitives.AvatarCyan.c
        val red: Color = Primitives.AvatarRed.c
        val teal: Color = Primitives.AvatarTeal.c
        val pink: Color = Primitives.AvatarPink.c
    }

    // ─── Tier medals ───────────────────────────────────────────────────────────

    object Tier {
        object Silver {
            val bg: Color = Primitives.TierSilverBg.c
            val ring: Color = Primitives.TierSilverRing.c
            val symbol: Color = Primitives.TierSilverSymbol.c
        }
        object Gold {
            val bg: Color = Primitives.TierGoldBg.c
            val ring: Color = Primitives.TierGoldRing.c
            val symbol: Color = Primitives.TierGoldSymbol.c
        }
        object Platinum {
            val bg: Color = Primitives.TierPlatinumBg.c
            val ring: Color = Primitives.TierPlatinumRing.c
            val symbolMain: Color = Primitives.TierPlatinumSymbolMain.c
            val symbolAccent: Color = Primitives.TierPlatinumSymbolAccent.c
        }
        object Diamond {
            val bg: Color = Primitives.TierDiamondBg.c
            val ring: Color = Primitives.TierDiamondRing.c
            val symbolMain: Color = Primitives.TierDiamondSymbolMain.c
            val symbolAccent: Color = Primitives.TierDiamondSymbolAccent.c
            val sparkle: Color = Primitives.TierSparkle.c
        }
    }

    // ─── Hub card themes ───────────────────────────────────────────────────────

    object HubCard {
        val midnight = listOf(
            Primitives.HubMidnight1.c,
            Primitives.HubMidnight2.c,
            Primitives.HubMidnight3.c,
        )
        val amethyst = listOf(
            Primitives.HubAmethyst1.c,
            Primitives.HubAmethyst2.c,
            Primitives.HubAmethyst3.c,
        )
        val ocean = listOf(
            Primitives.HubOcean1.c,
            Primitives.HubOcean2.c,
            Primitives.HubOcean3.c,
        )
        val sunset = listOf(
            Primitives.HubSunset1.c,
            Primitives.HubSunset2.c,
            Primitives.HubSunset3.c,
        )
        val forest = listOf(
            Primitives.HubForest1.c,
            Primitives.HubForest2.c,
            Primitives.HubForest3.c,
        )
        val glowAmethyst: Color = Primitives.HubGlowAmethyst.c
        val glowOcean: Color = Primitives.HubGlowOcean.c
        val glowSunset: Color = Primitives.HubGlowSunset.c
        val glowForest: Color = Primitives.HubGlowForest.c
        val chipOcean: Color = Primitives.HubChipOcean.c
        val chipForest: Color = Primitives.HubChipForest.c
        val chipAmethyst: Color = Primitives.HubChipAmethyst.c
        val chipRose: Color = Primitives.HubChipRose.c
        val goldGradient = listOf(
            Primitives.HubGold1.c,
            Primitives.HubGold2.c,
            Primitives.HubGold3.c,
            Primitives.HubGold1.c,
            Primitives.HubGold4.c,
        )
        val goldMetallic = listOf(
            Base.white,
            Primitives.HubGold5.c,
            Primitives.HubGold6.c,
            Primitives.HubGold7.c,
            Primitives.HubGold8.c,
            Primitives.HubGold9.c,
            Primitives.HubGold3.c,
            Primitives.HubGold10.c,
        )
        val goldDark = listOf(
            Primitives.HubGold11.c,
            Primitives.HubGold12.c,
            Primitives.HubGold13.c,
            Primitives.HubGold14.c,
            Primitives.HubGold4.c,
            Primitives.HubGold15.c,
        )
        val silverMetallic = listOf(
            Primitives.HubSilver1.c,
            Primitives.HubSilver2.c,
            Primitives.HubSilver3.c,
            Primitives.HubSilver4.c,
            Primitives.HubSilver5.c,
        )
        // Theme aura pairs (start, end) — alpha applied at use site
        val auraRoseStart: Color = Color(0xFFDC143C)
        val auraRoseEnd: Color = Primitives.AvatarBlue.c
        val auraCrimsonStart: Color = Color(0xFFFF0033)
        val auraCrimsonEnd: Color = Color(0xFF1A0006)
        val auraAmethystStart: Color = Color(0xFFC084FC)
        val auraAmethystEnd: Color = Color(0xFF6D28D9)
        val auraOceanStart: Color = Primitives.MapMarkerBlue.c
        val auraOceanEnd: Color = Color(0xFF1E3A8A)
        val auraSunsetStart: Color = Color(0xFFFF6B35)
        val auraSunsetEnd: Color = Color(0xFFB91C1C)
        val auraForestStart: Color = Primitives.EmeraldOnContainer.c
        val auraForestEnd: Color = Color(0xFF022C22)
        val shimmerRoseStart: Color = Color(0xFF6366F1)
        val shimmerRoseEnd: Color = Color(0xFFF43F5E)
        val shimmerCrimsonStart: Color = Color(0xFFFFB4C8)
        val shimmerCrimsonEnd: Color = Color(0xFFFF1744)
        val shimmerAmethystStart: Color = Color(0xFFE9D5FF)
        val shimmerAmethystEnd: Color = Color(0xFFA78BFA)
        val shimmerOceanStart: Color = Color(0xFF93C5FD)
        val shimmerOceanEnd: Color = Color(0xFF60A5FA)
        val shimmerSunsetStart: Color = Color(0xFFFFC9A8)
        val shimmerSunsetEnd: Color = Color(0xFFFF6B6B)
        val shimmerForestStart: Color = Color(0xFF6EE7B7)
        val shimmerForestEnd: Color = Color(0xFF34D399)
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
        val hubAmbient: Color = Base.blackAlpha(HubAmbientAlpha)
        val hubSpot: Color = Base.blackAlpha(HubSpotAlpha)
        val cardAmbient: Color = Base.blackAlpha(0.20f)
        val cardSpot: Color = Base.blackAlpha(0.26f)
        val toggleAmbient: Color = Base.blackAlpha(0.14f)
        val toggleSpot: Color = Base.blackAlpha(0.30f)
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

private fun Color.blendWithWhite(fraction: Float): Color = blend(RestaurantColors.Base.white, this, fraction)

private fun Color.darken(factor: Float): Color {
    val f = factor.coerceIn(0f, 1f)
    return Color(red = red * f, green = green * f, blue = blue * f, alpha = alpha)
}
