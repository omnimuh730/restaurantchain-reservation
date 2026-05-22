package com.mh.restaurantchainreservation.ui.theme

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors

/**
 * Legacy Material template colors — unused by the app shell ([com.mh.restaurantchainreservation.core.designsystem.theme.RestaurantTheme]).
 * All UI colors are defined in [RestaurantColors.Primitives] and exposed via [RestaurantColors] / [RestaurantPalette].
 */
@Deprecated(
    message = "Use RestaurantColors and LocalRestaurantPalette instead",
    replaceWith = ReplaceWith("RestaurantColors.Brand.primary", "com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors"),
)
val Purple80 = RestaurantColors.Accent.violet.second

@Deprecated(
    message = "Use RestaurantColors and LocalRestaurantPalette instead",
    replaceWith = ReplaceWith("RestaurantColors.Accent.slate.first", "com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors"),
)
val PurpleGrey80 = RestaurantColors.Accent.slate.first

@Deprecated(
    message = "Use RestaurantColors and LocalRestaurantPalette instead",
    replaceWith = ReplaceWith("RestaurantColors.Brand.softTint", "com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors"),
)
val Pink80 = RestaurantColors.Brand.softTint

@Deprecated(
    message = "Use RestaurantColors and LocalRestaurantPalette instead",
    replaceWith = ReplaceWith("RestaurantColors.Accent.violet.second", "com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors"),
)
val Purple40 = RestaurantColors.Accent.violet.second

@Deprecated(
    message = "Use RestaurantColors and LocalRestaurantPalette instead",
    replaceWith = ReplaceWith("RestaurantColors.Accent.slate.second", "com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors"),
)
val PurpleGrey40 = RestaurantColors.Accent.slate.second

@Deprecated(
    message = "Use RestaurantColors and LocalRestaurantPalette instead",
    replaceWith = ReplaceWith("RestaurantColors.Brand.strong", "com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors"),
)
val Pink40 = RestaurantColors.Brand.strong
