package com.mh.restaurantchainreservation.feature.wishlist.ui

import android.content.Context
import android.content.Intent
import com.mh.restaurantchainreservation.core.model.WishlistCollection

data class WishlistShareFriend(
    val id: String,
    val name: String,
    val handle: String,
)

val WishlistShareFriends: List<WishlistShareFriend> = listOf(
    WishlistShareFriend("f1", "Alex Kim", "@alexkim"),
    WishlistShareFriend("f2", "Jordan Lee", "@jordanlee"),
    WishlistShareFriend("f3", "Sam Rivera", "@samrivera"),
    WishlistShareFriend("f4", "Taylor Chen", "@taylorchen"),
)

fun buildWishlistShareMessage(collection: WishlistCollection, friendName: String? = null): String {
    val greeting = if (friendName != null) {
        "Hey $friendName,\n\n"
    } else {
        ""
    }
    val places = collection.restaurants
    val listBlock = if (places.isEmpty()) {
        "No restaurants saved yet — add some together!"
    } else {
        places.joinToString(separator = "\n") { r ->
            "• ${r.name} (${r.cuisine}) · ★ ${"%.1f".format(r.rating)}"
        }
    }
    return buildString {
        append(greeting)
        append("I put together a wishlist \"${collection.title}\" on RestaurantChain.\n\n")
        append(listBlock)
        append("\n\nOpen the app and check it out!")
    }
}

fun shareWishlistToFriend(
    context: Context,
    collection: WishlistCollection,
    friend: WishlistShareFriend,
) {
    val message = buildWishlistShareMessage(collection, friend.name.substringBefore(' '))
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Wishlist: ${collection.title}")
        putExtra(Intent.EXTRA_TEXT, message)
    }
    context.startActivity(
        Intent.createChooser(intent, "Send \"${collection.title}\" to ${friend.name}"),
    )
}

fun shareWishlistWithSystemChooser(context: Context, collection: WishlistCollection) {
    val message = buildWishlistShareMessage(collection)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Wishlist: ${collection.title}")
        putExtra(Intent.EXTRA_TEXT, message)
    }
    context.startActivity(Intent.createChooser(intent, "Share wishlist"))
}
