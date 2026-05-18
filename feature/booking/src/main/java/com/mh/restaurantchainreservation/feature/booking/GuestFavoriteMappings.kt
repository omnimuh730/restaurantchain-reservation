package com.mh.restaurantchainreservation.feature.booking

import com.mh.restaurantchainreservation.core.designsystem.badge.GuestFavoriteLaurelTier
import com.mh.restaurantchainreservation.core.model.GuestFavoriteLevel

fun GuestFavoriteLevel.isGuestFavorite(): Boolean = this != GuestFavoriteLevel.None

fun GuestFavoriteLevel.toLaurelTier(): GuestFavoriteLaurelTier = when (this) {
    GuestFavoriteLevel.None -> GuestFavoriteLaurelTier.None
    GuestFavoriteLevel.Normal -> GuestFavoriteLaurelTier.Normal
    GuestFavoriteLevel.High -> GuestFavoriteLaurelTier.High
}
