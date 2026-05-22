package com.mh.restaurantchainreservation.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.mh.restaurantchainreservation.core.designsystem.tokens.DefaultRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColorTokens
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantShapes
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantTypography

private fun appColorScheme(): ColorScheme = lightColorScheme(
    primary = RestaurantColorTokens.BrandPrimary,
    onPrimary = Color.White,
    background = RestaurantColorTokens.LightBackground,
    onBackground = RestaurantColorTokens.LightForeground,
    surface = RestaurantColorTokens.LightSurface,
    onSurface = RestaurantColorTokens.LightForeground,
    surfaceVariant = RestaurantColorTokens.LightSurfaceVariant,
    onSurfaceVariant = RestaurantColorTokens.LightMutedForeground,
    outline = RestaurantColorTokens.LightBorder,
    outlineVariant = RestaurantColorTokens.LightBorder,
    error = RestaurantColorTokens.LightDestructive,
    onError = Color.White,
)

/** App theme — light mode only. Edit colors in [RestaurantColors][com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors]. */
@Composable
fun RestaurantTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalRestaurantPalette provides DefaultRestaurantPalette) {
        MaterialTheme(
            colorScheme = appColorScheme(),
            typography = RestaurantTypography,
            shapes = RestaurantShapes,
            content = content,
        )
    }
}
