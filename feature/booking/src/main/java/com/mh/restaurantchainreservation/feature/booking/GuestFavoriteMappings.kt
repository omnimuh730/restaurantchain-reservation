package com.mh.restaurantchainreservation.feature.booking

import com.mh.restaurantchainreservation.core.designsystem.badge.GuestFavoriteLaurelTier
import com.mh.restaurantchainreservation.core.model.GuestFavoriteLevel

fun GuestFavoriteLevel.isGuestFavorite(): Boolean = this != GuestFavoriteLevel.None

/** Detail ratings row uses the black laurel for every guest-favorite tier. */
fun GuestFavoriteLevel.toDetailLaurelTier(): GuestFavoriteLaurelTier = when (this) {
    GuestFavoriteLevel.None -> GuestFavoriteLaurelTier.None
    GuestFavoriteLevel.Normal, GuestFavoriteLevel.High -> GuestFavoriteLaurelTier.Normal
}
