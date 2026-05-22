package com.mh.restaurantchainreservation.core.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class SharedItemKind {
    Restaurant,
    Wishlist,
}

data class SharedWishlistPayload(
    val title: String,
    val restaurants: List<Restaurant>,
)

data class SharedInboxEntry(
    val id: String,
    val sharerId: String,
    val sharerName: String,
    val kind: SharedItemKind,
    val receivedAtEpochMillis: Long,
    val restaurant: Restaurant? = null,
    val wishlist: SharedWishlistPayload? = null,
) {
    val displayTitle: String
        get() = when (kind) {
            SharedItemKind.Restaurant -> restaurant?.name.orEmpty()
            SharedItemKind.Wishlist -> wishlist?.title.orEmpty()
        }

    val displaySubtitle: String
        get() = when (kind) {
            SharedItemKind.Restaurant -> {
                val r = restaurant ?: return ""
                buildString {
                    if (r.cuisine.isNotBlank()) append(r.cuisine)
                    append(" · ★ ${"%.1f".format(r.rating)}")
                }
            }
            SharedItemKind.Wishlist -> {
                val count = wishlist?.restaurants?.size ?: 0
                if (count == 1) "1 place" else "$count places"
            }
        }

    fun previewImages(): List<String> = when (kind) {
        SharedItemKind.Restaurant -> listOfNotNull(restaurant?.image)
        SharedItemKind.Wishlist -> wishlist?.restaurants?.map { it.image }.orEmpty()
    }
}

data class SharedFolder(
    val sharerId: String,
    val sharerName: String,
    val entries: List<SharedInboxEntry>,
) {
    val folderTitle: String get() = "From $sharerName"

    val summary: String
        get() {
            val restaurants = entries.count { it.kind == SharedItemKind.Restaurant }
            val wishlists = entries.count { it.kind == SharedItemKind.Wishlist }
            val parts = mutableListOf<String>()
            if (restaurants > 0) {
                parts += if (restaurants == 1) "1 restaurant" else "$restaurants restaurants"
            }
            if (wishlists > 0) {
                parts += if (wishlists == 1) "1 wishlist" else "$wishlists wishlists"
            }
            return parts.joinToString(" · ")
        }
}

/**
 * In-app shares from contacts (no link / system chooser). Incoming items appear
 * under "Shared with you" on the Wishlist home screen.
 */
object SharedContentStore {
    private val _inbox = MutableStateFlow(seedInbox())
    val inbox: StateFlow<List<SharedInboxEntry>> = _inbox.asStateFlow()

    private val _folders = MutableStateFlow(buildFolders(_inbox.value))
    val sharedFolders: StateFlow<List<SharedFolder>> = _folders.asStateFlow()

    private fun refreshFolders() {
        _folders.value = buildFolders(_inbox.value)
    }

    private fun buildFolders(entries: List<SharedInboxEntry>): List<SharedFolder> =
        entries
            .groupBy { it.sharerId }
            .map { (sharerId, group) ->
                SharedFolder(
                    sharerId = sharerId,
                    sharerName = group.first().sharerName,
                    entries = group.sortedByDescending { it.receivedAtEpochMillis },
                )
            }
            .sortedByDescending { folder -> folder.entries.maxOf { it.receivedAtEpochMillis } }

    /** Delivers a restaurant to selected contacts (server would fan-out; local demo is outbound-only). */
    fun shareRestaurant(restaurant: Restaurant, contactIds: Set<String>) {
        if (contactIds.isEmpty()) return
        // Real app: POST share payload per contact. Inbox updates on the recipient device only.
    }

    /** Delivers a wishlist to selected contacts. */
    fun shareWishlist(collection: WishlistCollection, contactIds: Set<String>) {
        if (contactIds.isEmpty() || collection.isDefault) return
        // Real app: POST wishlist snapshot per contact.
    }

    /** Demo helper: merge an incoming share (e.g. push notification payload). */
    fun receiveEntry(entry: SharedInboxEntry) {
        _inbox.update { current ->
            if (current.any { it.id == entry.id }) current else current + entry
        }
        refreshFolders()
    }

    private fun seedInbox(): List<SharedInboxEntry> {
        val now = System.currentTimeMillis()
        val day = 86_400_000L
        val pool = DiscoverData.MONTHLY_BEST
        if (pool.size < 3) return emptyList()
        val sarah = ShareContacts.all[0]
        val marcus = ShareContacts.all[1]
        return listOf(
            SharedInboxEntry(
                id = "seed-s1",
                sharerId = sarah.id,
                sharerName = sarah.name,
                kind = SharedItemKind.Restaurant,
                receivedAtEpochMillis = now - day,
                restaurant = pool[0],
            ),
            SharedInboxEntry(
                id = "seed-s2",
                sharerId = sarah.id,
                sharerName = sarah.name,
                kind = SharedItemKind.Restaurant,
                receivedAtEpochMillis = now - day + 3_600_000L,
                restaurant = pool[1],
            ),
            SharedInboxEntry(
                id = "seed-m1",
                sharerId = marcus.id,
                sharerName = marcus.name,
                kind = SharedItemKind.Wishlist,
                receivedAtEpochMillis = now - 2 * day,
                wishlist = SharedWishlistPayload(
                    title = "Weekend picks",
                    restaurants = pool.take(3),
                ),
            ),
        )
    }
}
