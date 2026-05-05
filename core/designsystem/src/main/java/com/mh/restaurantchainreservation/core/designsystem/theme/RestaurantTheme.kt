package com.mh.restaurantchainreservation.core.designsystem.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColorTokens
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantShapes
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantTypography

enum class ThemePreference {
    Light,
    Dark,
    System,
}

private const val ThemePrefsName = "restaurant_theme_prefs"
private const val ThemePrefsKey = "theme_preference"

@Immutable
data class ThemeController(
    val preference: ThemePreference,
    val setPreference: (ThemePreference) -> Unit,
)

private fun lightScheme(): ColorScheme = lightColorScheme(
    primary = RestaurantColorTokens.BrandPrimary,
    onPrimary = Color.White,
    background = RestaurantColorTokens.LightBackground,
    onBackground = RestaurantColorTokens.LightForeground,
    surface = RestaurantColorTokens.LightSurface,
    onSurface = RestaurantColorTokens.LightForeground,
    surfaceVariant = RestaurantColorTokens.LightSurfaceVariant,
    onSurfaceVariant = RestaurantColorTokens.LightMutedForeground,
    outline = RestaurantColorTokens.LightBorder,
    error = RestaurantColorTokens.LightDestructive,
    onError = Color.White,
)

private fun darkScheme(): ColorScheme = darkColorScheme(
    primary = RestaurantColorTokens.BrandPrimaryDark,
    onPrimary = Color.White,
    background = RestaurantColorTokens.DarkBackground,
    onBackground = RestaurantColorTokens.DarkForeground,
    surface = RestaurantColorTokens.DarkSurface,
    onSurface = RestaurantColorTokens.DarkForeground,
    surfaceVariant = RestaurantColorTokens.DarkSurfaceVariant,
    onSurfaceVariant = RestaurantColorTokens.DarkMutedForeground,
    outline = RestaurantColorTokens.DarkBorder,
    error = RestaurantColorTokens.DarkDestructive,
    onError = Color.White,
)

private fun readThemePreference(context: Context): ThemePreference {
    val value = context.getSharedPreferences(ThemePrefsName, Context.MODE_PRIVATE)
        .getString(ThemePrefsKey, ThemePreference.System.name)
        ?: ThemePreference.System.name
    return ThemePreference.entries.firstOrNull { it.name == value } ?: ThemePreference.System
}

private fun persistThemePreference(context: Context, preference: ThemePreference) {
    context.getSharedPreferences(ThemePrefsName, Context.MODE_PRIVATE)
        .edit()
        .putString(ThemePrefsKey, preference.name)
        .apply()
}

@Composable
fun rememberThemeController(context: Context): ThemeController {
    val state = remember { mutableStateOf(readThemePreference(context)) }
    return ThemeController(
        preference = state.value,
        setPreference = { next ->
            state.value = next
            persistThemePreference(context, next)
        },
    )
}

@Composable
fun RestaurantTheme(
    preference: ThemePreference,
    content: @Composable () -> Unit,
) {
    val useDark = when (preference) {
        ThemePreference.Light -> false
        ThemePreference.Dark -> true
        ThemePreference.System -> isSystemInDarkTheme()
    }

    MaterialTheme(
        colorScheme = if (useDark) darkScheme() else lightScheme(),
        typography = RestaurantTypography,
        shapes = RestaurantShapes,
        content = content,
    )
}
