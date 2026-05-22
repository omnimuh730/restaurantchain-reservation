package com.mh.restaurantchainreservation.core.designsystem.tokens

import androidx.compose.ui.graphics.Color

/** Material [androidx.compose.material3.ColorScheme] bridge — sourced from [RestaurantColors]. */
object RestaurantColorTokens {
    val BrandPrimary = RestaurantColors.Brand.primary
    val BrandPrimaryDark = RestaurantColors.Brand.primary

    val LightBackground = RestaurantColors.Surface.page
    val LightForeground = RestaurantColors.Text.primary
    val LightBodyForeground = RestaurantColors.Text.body
    val LightMutedForeground = RestaurantColors.Text.secondary
    val LightTertiaryForeground = RestaurantColors.Text.tertiary
    val LightSurface = RestaurantColors.Surface.card
    val LightSurfaceVariant = RestaurantColors.Surface.muted
    val LightBorder = RestaurantColors.Border.default
    val LightDestructive = RestaurantColors.Semantic.destructive

    /** @deprecated Dark mode removed; aliases to light tokens for legacy call sites. */
    val DarkBackground: Color get() = LightBackground
    val DarkForeground: Color get() = LightForeground
    val DarkBodyForeground: Color get() = LightBodyForeground
    val DarkMutedForeground: Color get() = LightMutedForeground
    val DarkTertiaryForeground: Color get() = LightTertiaryForeground
    val DarkSurface: Color get() = LightSurface
    val DarkSurfaceVariant: Color get() = LightSurfaceVariant
    val DarkBorder: Color get() = LightBorder
    val DarkDestructive: Color get() = LightDestructive
}
