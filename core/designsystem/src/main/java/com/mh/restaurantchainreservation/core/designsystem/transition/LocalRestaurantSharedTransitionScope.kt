@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.mh.restaurantchainreservation.core.designsystem.transition

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.staticCompositionLocalOf

val LocalRestaurantSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope?> { null }
val LocalAnimatedContentScope = staticCompositionLocalOf<AnimatedContentScope?> { null }

/** Nav back stack entry for the current composable; used to detect push vs pop during shared transitions. */
val LocalRestaurantNavEntry = staticCompositionLocalOf<Any?> { null }
