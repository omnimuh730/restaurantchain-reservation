package com.mh.restaurantchainreservation.core.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A user's saved wishlist collection. The "recent" collection is created on
 * first launch and is non-removable (default = true). Mirrors the React
 * `Collection` shape used by `SavedListView` / `WishlistSelectionSheet`.
 */
data class WishlistCollection(
    val id: String,
    val title: String,
    /** Saved restaurants for user-created wishlists. */
    val restaurants: List<Restaurant> = emptyList(),
    /** Browsing history for the default Recently Viewed collection. */
    val recentlyViewed: List<RecentlyViewedEntry> = emptyList(),
    val isDefault: Boolean = false,
) {
    fun displayRestaurants(): List<Restaurant> =
        if (isDefault) recentlyViewed.map { it.restaurant } else restaurants

    fun itemCount(): Int = if (isDefault) recentlyViewed.size else restaurants.size
}

/**
 * Snapshot of the most-recent "Saved to <collection>" toast. Stored on the
 * store so the overlay host (sitting in the navigation Scaffold) can render it
 * regardless of the active route.
 */
enum class WishlistToastKind {
    Saved,
    Removed,
}

data class WishlistToastState(
    val id: Long,
    val restaurant: Restaurant,
    val collectionTitle: String,
    val collectionId: String,
    val kind: WishlistToastKind = WishlistToastKind.Saved,
)

/**
 * Module-level singleton wishlist state. The React demo keeps an in-memory
 * `savedStore` plus per-page state; this object centralizes both the
 * collections list and the cross-screen overlay state (sheet trigger, toast)
 * so any composable can read/write without prop drilling. Snapshots are
 * exposed as `StateFlow`s so callers can `collectAsState()` directly.
 */
object WishlistStore {
    const val RECENT_COLLECTION_ID = "recent"
    const val DEFAULT_RECENT_TITLE = "Recently Viewed"

    private val initialCollections: List<WishlistCollection> = listOf(
        WishlistCollection(
            id = RECENT_COLLECTION_ID,
            title = DEFAULT_RECENT_TITLE,
            recentlyViewed = seedRecentlyViewedHistory(),
            isDefault = true,
        ),
    )

    private fun seedRecentlyViewedHistory(): List<RecentlyViewedEntry> {
        val now = System.currentTimeMillis()
        val dayMs = 86_400_000L
        val pool = (DiscoverData.MONTHLY_BEST + DiscoverData.LOVED_BY_LOCALS.take(2)).distinctBy { it.id }
        return pool.mapIndexed { index, restaurant ->
            val daysAgo = when (index) {
                in 0..1 -> 0
                in 2..3 -> 1
                in 4..5 -> 5
                else -> 12
            }
            RecentlyViewedEntry(
                restaurant = restaurant,
                viewedAtEpochMillis = now - (daysAgo * dayMs) - (index * 3_600_000L),
            )
        }
    }

    private val _collections = MutableStateFlow(initialCollections)
    val collections: StateFlow<List<WishlistCollection>> = _collections.asStateFlow()

    /** O(1) saved lookup — kept in sync whenever [collections] changes. */
    private val _savedRestaurantIds = MutableStateFlow(computeSavedIds(initialCollections))
    val savedRestaurantIds: StateFlow<Set<String>> = _savedRestaurantIds.asStateFlow()

    /**
     * Restaurants temporarily "unsaved" on a wishlist detail screen — still shown in the
     * list, heart appears empty, and the user can tap again to re-save before leaving.
     */
    private val _unsavedInDetailCollection = MutableStateFlow<Map<String, Set<String>>>(emptyMap())
    val unsavedInDetailCollection: StateFlow<Map<String, Set<String>>> =
        _unsavedInDetailCollection.asStateFlow()

    private val _pendingPickRestaurant = MutableStateFlow<Restaurant?>(null)
    val pendingPickRestaurant: StateFlow<Restaurant?> = _pendingPickRestaurant.asStateFlow()

    private val _lastToast = MutableStateFlow<WishlistToastState?>(null)
    val lastToast: StateFlow<WishlistToastState?> = _lastToast.asStateFlow()

    private val _gatheredShown = MutableStateFlow(false)
    val gatheredShown: StateFlow<Boolean> = _gatheredShown.asStateFlow()

    /**
     * Which wishlist collection detail overlay is open, if any. Kept on the store
     * so it survives navigation to restaurant detail and back.
     */
    private val _openCollectionId = MutableStateFlow<String?>(null)
    val openCollectionId: StateFlow<String?> = _openCollectionId.asStateFlow()

    fun openCollection(id: String) {
        _openCollectionId.value = id
    }

    fun closeOpenCollection() {
        _openCollectionId.value = null
    }

    /** Synchronous helper used by save buttons to render the heart fill state. */
    fun isSaved(restaurantId: String): Boolean {
        if (restaurantId !in _savedRestaurantIds.value) return false
        val col = collectionContaining(restaurantId) ?: return false
        return restaurantId !in _unsavedInDetailCollection.value[col.id].orEmpty()
    }

    fun collectionContaining(restaurantId: String): WishlistCollection? =
        _collections.value.firstOrNull { col ->
            !col.isDefault && col.restaurants.any { it.id == restaurantId }
        }

    /** User-created wishlists shown in the save sheet (excludes [DEFAULT_RECENT_TITLE]). */
    fun selectableCollections(): List<WishlistCollection> =
        _collections.value.filterNot { it.isDefault }

    /**
     * Heart tap — optimistic, no network wait.
     * - Saved → remove immediately.
     * - Not saved + exactly one selectable list → auto-save there.
     * - Not saved + zero or many selectable lists → open save sheet.
     */
    fun onHeartTap(restaurant: Restaurant) {
        if (isSaved(restaurant.id)) {
            removeFromWishlistWithToast(restaurant.id)
            return
        }
        val selectable = selectableCollections()
        if (selectable.size == 1) {
            saveTo(selectable.first().id, restaurant)
        } else {
            openPicker(restaurant)
        }
    }

    /** Recently Viewed detail: unsave from wishlist (keeps browsing history) or open save sheet. */
    fun onHeartTapInRecentlyViewed(restaurant: Restaurant) {
        if (isSaved(restaurant.id)) {
            removeFromWishlistWithToast(restaurant.id)
        } else {
            openPicker(restaurant)
        }
    }

    /** User wishlist detail: re-save into the open list. */
    fun saveToCollection(collectionId: String, restaurant: Restaurant) {
        clearUnsavedInDetail(restaurant.id)
        saveTo(collectionId, restaurant)
    }

    /**
     * User wishlist detail — unheart without removing the row from the list.
     * Persists removal when [flushUnsavedInCollection] runs (e.g. on back).
     */
    fun unsaveInCollectionKeepingInList(collectionId: String, restaurant: Restaurant) {
        val col = _collections.value.firstOrNull { it.id == collectionId && !it.isDefault } ?: return
        if (!col.restaurants.any { it.id == restaurant.id }) return
        _unsavedInDetailCollection.value = _unsavedInDetailCollection.value.toMutableMap().apply {
            put(collectionId, get(collectionId).orEmpty() + restaurant.id)
        }
        postRemovedToast(restaurant, col.title, col.id)
    }

    /** Applies pending unhearts from a wishlist detail screen and clears session overrides. */
    fun flushUnsavedInCollection(collectionId: String) {
        val unsaved = _unsavedInDetailCollection.value[collectionId].orEmpty()
        if (unsaved.isEmpty()) return
        unsaved.forEach { restaurantId ->
            removeFromCollection(
                collectionId = collectionId,
                restaurantId = restaurantId,
                showRemovedToast = false,
            )
        }
        _unsavedInDetailCollection.value = _unsavedInDetailCollection.value - collectionId
    }

    private fun clearUnsavedInDetail(restaurantId: String) {
        val current = _unsavedInDetailCollection.value
        if (current.isEmpty()) return
        val updated = current.mapValues { (_, ids) -> ids - restaurantId }.filterValues { it.isNotEmpty() }
        if (updated != current) {
            _unsavedInDetailCollection.value = updated
        }
    }

    fun openPicker(restaurant: Restaurant) {
        _pendingPickRestaurant.value = restaurant
    }

    fun closePicker() {
        _pendingPickRestaurant.value = null
    }

    /**
     * Add `restaurant` to `collectionId`, removing it from any other collection
     * first (a restaurant lives in at most one collection at a time; this
     * matches the React "move to <new collection>" behavior). Also fires a
     * fresh toast and clears the picker.
     */
    fun saveTo(collectionId: String, restaurant: Restaurant) {
        clearUnsavedInDetail(restaurant.id)
        val updated = _collections.value.map { col ->
            when {
                col.id == collectionId && !col.isDefault -> {
                    val withoutDup = col.restaurants.filterNot { it.id == restaurant.id }
                    col.copy(restaurants = listOf(restaurant) + withoutDup)
                }
                col.isDefault -> col
                col.restaurants.any { it.id == restaurant.id } ->
                    col.copy(restaurants = col.restaurants.filterNot { it.id == restaurant.id })
                else -> col
            }
        }
        commitCollections(updated)
        val targetTitle = updated.firstOrNull { it.id == collectionId }?.title ?: return
        _lastToast.value = WishlistToastState(
            id = System.currentTimeMillis(),
            restaurant = restaurant,
            collectionTitle = targetTitle,
            collectionId = collectionId,
        )
        _pendingPickRestaurant.value = null
    }

    /**
     * Append a brand-new (non-default) collection seeded with `restaurant`.
     * Returns false if the name is empty or already used by another wishlist.
     */
    fun isWishlistNameTaken(name: String, excludingCollectionId: String? = null): Boolean {
        val normalized = name.trim().lowercase()
        if (normalized.isEmpty()) return false
        return _collections.value.any { col ->
            !col.isDefault &&
                col.id != excludingCollectionId &&
                col.title.trim().lowercase() == normalized
        }
    }

    fun createCollectionAndSave(name: String, restaurant: Restaurant): Boolean {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || isWishlistNameTaken(trimmed)) return false
        val newId = "col-" + System.currentTimeMillis().toString(36)
        val newCollection = WishlistCollection(
            id = newId,
            title = trimmed,
            restaurants = emptyList(),
            isDefault = false,
        )
        commitCollections(_collections.value + newCollection)
        saveTo(newId, restaurant)
        return true
    }

    fun createCollection(name: String): Boolean {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || isWishlistNameTaken(trimmed)) return false
        val newCollection = WishlistCollection(
            id = "col-" + System.currentTimeMillis().toString(36),
            title = trimmed,
            restaurants = emptyList(),
            isDefault = false,
        )
        commitCollections(_collections.value + newCollection)
        return true
    }

    fun renameCollection(collectionId: String, name: String): Boolean {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || isWishlistNameTaken(trimmed, excludingCollectionId = collectionId)) {
            return false
        }
        commitCollections(
            _collections.value.map { col ->
                if (col.id == collectionId && !col.isDefault) col.copy(title = trimmed) else col
            },
        )
        return true
    }

    fun deleteCollection(collectionId: String) {
        val removed = _collections.value.firstOrNull { it.id == collectionId && !it.isDefault } ?: return
        val fallbackEntries = removed.restaurants.map {
            RecentlyViewedEntry(it, System.currentTimeMillis())
        }
        commitCollections(
            _collections.value
                .filterNot { it.id == collectionId }
                .map { col ->
                    if (col.id == RECENT_COLLECTION_ID) {
                        val merged = (fallbackEntries + col.recentlyViewed)
                            .distinctBy { it.restaurant.id }
                        col.copy(recentlyViewed = merged)
                    } else {
                        col
                    }
                },
        )
    }

    /** Records or refreshes a view in Recently Viewed (does not affect wishlist saves). */
    fun recordRecentlyViewed(restaurant: Restaurant) {
        val now = System.currentTimeMillis()
        commitCollections(
            _collections.value.map { col ->
                if (!col.isDefault) return@map col
                val without = col.recentlyViewed.filterNot { it.restaurant.id == restaurant.id }
                col.copy(
                    recentlyViewed = listOf(RecentlyViewedEntry(restaurant, now)) + without,
                )
            },
        )
    }

    /**
     * Remove from a single collection. When [showRemovedToast] is true and the
     * collection is a user wishlist, shows a bottom "Removed from …" toast.
     */
    fun removeFromCollection(
        collectionId: String,
        restaurantId: String,
        showRemovedToast: Boolean = false,
    ) {
        _unsavedInDetailCollection.value = _unsavedInDetailCollection.value.toMutableMap().apply {
            val remaining = get(collectionId).orEmpty() - restaurantId
            if (remaining.isEmpty()) remove(collectionId) else put(collectionId, remaining)
        }
        val col = _collections.value.firstOrNull { it.id == collectionId } ?: return
        val restaurant = if (col.isDefault) {
            col.recentlyViewed.firstOrNull { it.restaurant.id == restaurantId }?.restaurant
        } else {
            col.restaurants.firstOrNull { it.id == restaurantId }
        } ?: return
        commitCollections(
            _collections.value.map { c ->
                when {
                    c.id != collectionId -> c
                    c.isDefault -> c.copy(
                        recentlyViewed = c.recentlyViewed.filterNot { it.restaurant.id == restaurantId },
                    )
                    else -> c.copy(
                        restaurants = c.restaurants.filterNot { it.id == restaurantId },
                    )
                }
            },
        )
        if (showRemovedToast && !col.isDefault) {
            postRemovedToast(restaurant, col.title, col.id)
        }
    }

    /** Removes from the user wishlist that holds this restaurant (not Recently Viewed). */
    fun removeFromWishlistWithToast(restaurantId: String) {
        val col = collectionContaining(restaurantId) ?: return
        removeFromCollection(col.id, restaurantId, showRemovedToast = true)
    }

    fun removeFromRecentlyViewed(restaurantId: String) {
        removeFromCollection(RECENT_COLLECTION_ID, restaurantId, showRemovedToast = false)
    }

    /** Un-save a restaurant from every user wishlist; Recently Viewed history is kept. */
    fun removeFromAll(restaurantId: String) {
        commitCollections(
            _collections.value.map { col ->
                if (col.isDefault) {
                    col
                } else {
                    val filtered = col.restaurants.filterNot { it.id == restaurantId }
                    if (filtered.size == col.restaurants.size) col else col.copy(restaurants = filtered)
                }
            },
        )
        val toast = _lastToast.value
        if (toast != null && toast.restaurant.id == restaurantId) {
            _lastToast.value = null
        }
    }

    private fun postRemovedToast(restaurant: Restaurant, collectionTitle: String, collectionId: String) {
        _lastToast.value = WishlistToastState(
            id = System.currentTimeMillis(),
            restaurant = restaurant,
            collectionTitle = collectionTitle,
            collectionId = collectionId,
            kind = WishlistToastKind.Removed,
        )
    }

    fun dismissToast(id: Long) {
        if (_lastToast.value?.id == id) {
            _lastToast.value = null
        }
    }

    fun markGatheredShown() {
        _gatheredShown.value = true
    }

    private fun commitCollections(updated: List<WishlistCollection>) {
        _collections.value = updated
        _savedRestaurantIds.value = computeSavedIds(updated)
    }

    private fun computeSavedIds(collections: List<WishlistCollection>): Set<String> =
        collections
            .asSequence()
            .filterNot { it.isDefault }
            .flatMap { it.restaurants.asSequence() }
            .map { it.id }
            .toSet()
}
