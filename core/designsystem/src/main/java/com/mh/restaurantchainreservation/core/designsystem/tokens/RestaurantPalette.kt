package com.mh.restaurantchainreservation.core.designsystem.tokens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class AccentPair(val container: Color, val onContainer: Color)

@Immutable
data class RestaurantPalette(
    val isDark: Boolean,
    val brand: Color,
    val brandStrong: Color,
    val brandSoftSurface: Color,
    val cardSurface: Color,
    val mutedSurface: Color,
    val border: Color,
    val borderSoft: Color,
    val foreground: Color,
    val mutedForeground: Color,
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

private val LightPalette = RestaurantPalette(
    isDark = false,
    brand = Color(0xFFFF385C),
    brandStrong = Color(0xFFD70466),
    brandSoftSurface = Color(0xFFFFF1F4),
    cardSurface = Color(0xFFFFFFFF),
    mutedSurface = Color(0xFFF7F7F7),
    border = Color(0xFFDDDDDD),
    borderSoft = Color(0x14000000),
    foreground = Color(0xFF222222),
    mutedForeground = Color(0xFF717171),
    destructive = Color(0xFFC13515),
    success = Color(0xFF008A05),
    warning = Color(0xFFE07912),
    info = Color(0xFF428BFF),
    gold = Color(0xFFF59E0B),
    goldSoft = Color(0x1AF59E0B),
    rose = Color(0xFFF43F5E),
    roseSoft = Color(0xFFFFF1F2),
    blueAccent = AccentPair(container = Color(0xFFDBEAFE), onContainer = Color(0xFF2563EB)),
    emeraldAccent = AccentPair(container = Color(0xFFD1FAE5), onContainer = Color(0xFF059669)),
    orangeAccent = AccentPair(container = Color(0xFFFFEDD5), onContainer = Color(0xFFEA580C)),
    amberAccent = AccentPair(container = Color(0xFFFEF3C7), onContainer = Color(0xFFD97706)),
    violetAccent = AccentPair(container = Color(0xFFEDE9FE), onContainer = Color(0xFF7C3AED)),
    pinkAccent = AccentPair(container = Color(0xFFFCE7F3), onContainer = Color(0xFFDB2777)),
    slateAccent = AccentPair(container = Color(0xFFE2E8F0), onContainer = Color(0xFF475569)),
    gradientStart = Color(0xFFFF7A9C),
    gradientMid = Color(0xFFFF385C),
    gradientEnd = Color(0xFFCC2D49),
    giftGradientStart = Color(0xFFFF385C),
    giftGradientEnd = Color(0xFFD70466),
)

private val DarkPalette = RestaurantPalette(
    isDark = true,
    brand = Color(0xFFFF385C),
    brandStrong = Color(0xFFD70466),
    brandSoftSurface = Color(0xFF3F1D2A),
    cardSurface = Color(0xFF18181B),
    mutedSurface = Color(0xFF27272A),
    border = Color(0xFF3F3F46),
    borderSoft = Color(0x1FFFFFFF),
    foreground = Color(0xFFF8F8FA),
    mutedForeground = Color(0xFFA1A1AA),
    destructive = Color(0xFFF97373),
    success = Color(0xFF22C55E),
    warning = Color(0xFFF59E0B),
    info = Color(0xFF60A5FA),
    gold = Color(0xFFFBBF24),
    goldSoft = Color(0x33F59E0B),
    rose = Color(0xFFFB7185),
    roseSoft = Color(0xFF3F1D29),
    blueAccent = AccentPair(container = Color(0xFF1E3A8A), onContainer = Color(0xFF93C5FD)),
    emeraldAccent = AccentPair(container = Color(0xFF064E3B), onContainer = Color(0xFF6EE7B7)),
    orangeAccent = AccentPair(container = Color(0xFF7C2D12), onContainer = Color(0xFFFDBA74)),
    amberAccent = AccentPair(container = Color(0xFF78350F), onContainer = Color(0xFFFCD34D)),
    violetAccent = AccentPair(container = Color(0xFF4C1D95), onContainer = Color(0xFFC4B5FD)),
    pinkAccent = AccentPair(container = Color(0xFF831843), onContainer = Color(0xFFF9A8D4)),
    slateAccent = AccentPair(container = Color(0xFF334155), onContainer = Color(0xFFCBD5E1)),
    gradientStart = Color(0xFFFF7A9C),
    gradientMid = Color(0xFFFF385C),
    gradientEnd = Color(0xFFB31E40),
    giftGradientStart = Color(0xFFFF385C),
    giftGradientEnd = Color(0xFFD70466),
)

val LocalRestaurantPalette = staticCompositionLocalOf<RestaurantPalette> {
    error("RestaurantPalette not provided. Wrap content in RestaurantTheme.")
}

fun resolvePalette(isDark: Boolean): RestaurantPalette = if (isDark) DarkPalette else LightPalette

object RestaurantPalettes {
    val current: RestaurantPalette
        @Composable
        get() = LocalRestaurantPalette.current
}
