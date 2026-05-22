package com.mh.restaurantchainreservation.core.designsystem.tokens

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Airbnb White (`#FFFFFF`) full-screen canvas.
 *
 * Use for screens, [androidx.compose.foundation.lazy.LazyColumn], and root [androidx.compose.foundation.layout.Box]es.
 * Do not use [RestaurantPalette.mutedSurface] (Foggy `#F7F7F7`) for page backgrounds.
 */
@Composable
fun Modifier.pageCanvasBackground(): Modifier =
    background(LocalRestaurantPalette.current.pageBackground)
