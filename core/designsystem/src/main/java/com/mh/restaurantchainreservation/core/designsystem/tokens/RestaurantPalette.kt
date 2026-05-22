package com.mh.restaurantchainreservation.core.designsystem.tokens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class AccentPair(val container: Color, val onContainer: Color)

/**
 * Semantic colors exposed to Compose UI. Built from [RestaurantColors] — light mode only.
 *
 * Airbnb core mapping:
 * - [pageBackground] / [cardSurface] — White `#FFFFFF`
 * - [mutedSurface] — Foggy `#F7F7F7`
 * - [border] — Borders `#EBEBEB`
 * - [foreground] / [bodyForeground] — Charcoal `#222222`
 * - [mutedForeground] / [tertiaryForeground] / [placeholder] — Muted `#717171`
 */
@Immutable
data class RestaurantPalette(
    val brand: Color,
    val brandStrong: Color,
    val brandSoftSurface: Color,
    val cardSurface: Color,
    val pageBackground: Color,
    val mutedSurface: Color,
    val border: Color,
    val borderSoft: Color,
    val foreground: Color,
    val bodyForeground: Color,
    val mutedForeground: Color,
    val tertiaryForeground: Color,
    val destructive: Color,
    val success: Color,
    val warning: Color,
    val info: Color,
    val gold: Color,
    val goldSoft: Color,
    val rose: Color,
    val roseSoft: Color,
    val onBrand: Color,
    val inverse: Color,
    val scrim: Color,
    val scrimHeavy: Color,
    val placeholder: Color,
    val mapCanvas: Color,
    val imagePlaceholder: Color,
    val dividerAlt: Color,
    val star: Color,
    val heart: Color,
    val onImage: Color,
    val blueAccent: AccentPair,
    val emeraldAccent: AccentPair,
    val orangeAccent: AccentPair,
    val amberAccent: AccentPair,
    val violetAccent: AccentPair,
    val pinkAccent: AccentPair,
    val slateAccent: AccentPair,
    val gradientStart: Color,
    val gradientMid: Color,
    val gradientEnd: Color,
    val giftGradientStart: Color,
    val giftGradientEnd: Color,
)

val DefaultRestaurantPalette: RestaurantPalette = RestaurantPalette(
    brand = RestaurantColors.Brand.primary,
    brandStrong = RestaurantColors.Brand.strong,
    brandSoftSurface = RestaurantColors.Brand.softSurface,
    cardSurface = RestaurantColors.Surface.card,
    pageBackground = RestaurantColors.Surface.page,
    mutedSurface = RestaurantColors.Surface.muted,
    border = RestaurantColors.Border.default,
    borderSoft = RestaurantColors.Border.soft,
    foreground = RestaurantColors.Text.primary,
    bodyForeground = RestaurantColors.Text.body,
    mutedForeground = RestaurantColors.Text.secondary,
    tertiaryForeground = RestaurantColors.Text.tertiary,
    destructive = RestaurantColors.Semantic.destructive,
    success = RestaurantColors.Semantic.success,
    warning = RestaurantColors.Semantic.warning,
    info = RestaurantColors.Semantic.info,
    gold = RestaurantColors.Semantic.gold,
    goldSoft = RestaurantColors.Semantic.goldSoft,
    rose = RestaurantColors.Semantic.rose,
    roseSoft = RestaurantColors.Semantic.roseSoft,
    onBrand = RestaurantColors.Base.white,
    inverse = RestaurantColors.Base.black,
    scrim = RestaurantColors.Overlay.scrimModal,
    scrimHeavy = RestaurantColors.Overlay.scrimHeavy,
    placeholder = RestaurantColors.Neutral.placeholder,
    mapCanvas = RestaurantColors.Map.canvas,
    imagePlaceholder = RestaurantColors.Neutral.imagePlaceholder,
    dividerAlt = RestaurantColors.Neutral.dividerAlt,
    star = RestaurantColors.Semantic.starGold,
    heart = RestaurantColors.Semantic.heart,
    onImage = RestaurantColors.Base.white,
    blueAccent = RestaurantColors.Accent.blue.toAccentPair(),
    emeraldAccent = RestaurantColors.Accent.emerald.toAccentPair(),
    orangeAccent = RestaurantColors.Accent.orange.toAccentPair(),
    amberAccent = RestaurantColors.Accent.amber.toAccentPair(),
    violetAccent = RestaurantColors.Accent.violet.toAccentPair(),
    pinkAccent = RestaurantColors.Accent.pink.toAccentPair(),
    slateAccent = RestaurantColors.Accent.slate.toAccentPair(),
    gradientStart = RestaurantColors.Brand.gradientStart,
    gradientMid = RestaurantColors.Brand.gradientMid,
    gradientEnd = RestaurantColors.Brand.gradientEnd,
    giftGradientStart = RestaurantColors.Brand.primary,
    giftGradientEnd = RestaurantColors.Brand.strong,
)

private fun Pair<Color, Color>.toAccentPair() = AccentPair(first, second)

val LocalRestaurantPalette = staticCompositionLocalOf<RestaurantPalette> {
    error("RestaurantPalette not provided. Wrap content in RestaurantTheme.")
}

/** @deprecated Light-only app; always returns [DefaultRestaurantPalette]. */
fun resolvePalette(@Suppress("UNUSED_PARAMETER") isDark: Boolean = false): RestaurantPalette =
    DefaultRestaurantPalette

object RestaurantPalettes {
    val current: RestaurantPalette
        @Composable
        get() = LocalRestaurantPalette.current
}
