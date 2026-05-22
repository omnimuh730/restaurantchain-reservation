package com.mh.restaurantchainreservation.feature.wishlist.ui

import androidx.compose.runtime.Composable
import com.mh.restaurantchainreservation.core.designsystem.components.ShareWithContactsSheet
import com.mh.restaurantchainreservation.core.model.SharedContentStore
import com.mh.restaurantchainreservation.core.model.WishlistCollection

@Composable
fun WishlistShareFriendsSheet(
    collection: WishlistCollection,
    onDismiss: () -> Unit,
) {
    val placeLabel = if (collection.itemCount() == 1) "1 place" else "${collection.itemCount()} places"
    ShareWithContactsSheet(
        subtitle = "\"${collection.title}\" · $placeLabel",
        onDismiss = onDismiss,
        onShare = { contactIds ->
            SharedContentStore.shareWishlist(collection, contactIds)
        },
    )
}
