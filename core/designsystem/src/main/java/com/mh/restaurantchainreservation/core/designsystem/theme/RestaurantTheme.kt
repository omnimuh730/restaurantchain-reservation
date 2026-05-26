package com.mh.restaurantchainreservation.core.designsystem.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.mh.restaurantchainreservation.core.designsystem.tokens.DefaultRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColorTokens
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantShapes
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantTypography

private fun appColorScheme(): ColorScheme {
    val canvas = RestaurantColors.Surface.canvas
    val foggy = RestaurantColors.Surface.foggy
    return lightColorScheme(
        primary = RestaurantColorTokens.BrandPrimary,
        onPrimary = RestaurantColors.Base.white,
        background = canvas,
        onBackground = RestaurantColorTokens.LightForeground,
        surface = canvas,
        onSurface = RestaurantColorTokens.LightForeground,
        surfaceVariant = foggy,
        onSurfaceVariant = RestaurantColorTokens.LightMutedForeground,
        surfaceContainerLowest = canvas,
        surfaceContainerLow = canvas,
        surfaceContainer = canvas,
        surfaceContainerHigh = canvas,
        surfaceContainerHighest = canvas,
        outline = RestaurantColorTokens.LightBorder,
        outlineVariant = RestaurantColorTokens.LightBorder,
        error = RestaurantColorTokens.LightDestructive,
        onError = RestaurantColors.Base.white,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalRestaurantPalette provides DefaultRestaurantPalette,
        LocalIndication provides NoRippleIndication,
        LocalRippleConfiguration provides null,
        LocalOverscrollFactory provides BounceOverscrollFactory,
    ) {
        MaterialTheme(
            colorScheme = appColorScheme(),
            typography = RestaurantTypography,
            shapes = RestaurantShapes,
            content = content,
        )
    }
}
