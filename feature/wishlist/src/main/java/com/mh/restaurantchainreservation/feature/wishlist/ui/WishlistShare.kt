package com.mh.restaurantchainreservation.feature.wishlist.ui

import com.mh.restaurantchainreservation.core.model.SharedContentStore
import com.mh.restaurantchainreservation.core.model.WishlistCollection

fun shareWishlistToContacts(collection: WishlistCollection, contactIds: Set<String>) {
    SharedContentStore.shareWishlist(collection, contactIds)
}
