package com.mh.restaurantchainreservation.core.designsystem.tokens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class AccentPair(val container: Color, val onContainer: Color)

/**
 * Semantic colors exposed to Compose UI. Built from [RestaurantColors] — light mode only.
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
