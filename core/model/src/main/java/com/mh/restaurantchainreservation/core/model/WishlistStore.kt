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
    val restaurants: List<Restaurant>,
    val isDefault: Boolean = false,
)

/**
 * Snapshot of the most-recent "Saved to <collection>" toast. Stored on the
 * store so the overlay host (sitting in the navigation Scaffold) can render it
 * regardless of the active route.
 */
data class WishlistToastState(
    val id: Long,
    val restaurant: Restaurant,
    val collectionTitle: String,
    val collectionId: String,
)

/**
 * Module-level singleton wishlist state. The React demo keeps an in-memory
 * `savedStore` plus per-page state; this object centralizes both the
 * collections list and the cross-screen overlay state (sheet trigger, toast)
 * so any composable can read/write without prop drilling. Snapshots are
 * exposed as `StateFlow`s so callers can `collectAsState()` directly.
 */
object WishlistStore {
    private val initialCollections: List<WishlistCollection> = listOf(
        WishlistCollection(
            id = "recent",
            title = "Recently search",
            restaurants = DiscoverData.MONTHLY_BEST.take(4),
            isDefault = true,
        ),
    )

    private val _collections = MutableStateFlow(initialCollections)
    val collections: StateFlow<List<WishlistCollection>> = _collections.asStateFlow()

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
    fun isSaved(restaurantId: String): Boolean =
        _collections.value.any { col -> col.restaurants.any { it.id == restaurantId } }

    fun collectionContaining(restaurantId: String): WishlistCollection? =
        _collections.value.firstOrNull { col -> col.restaurants.any { it.id == restaurantId } }

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
        val updated = _collections.value.map { col ->
            val withoutTarget = col.restaurants.filterNot { it.id == restaurant.id }
            when {
                col.id == collectionId -> col.copy(
                    restaurants = listOf(restaurant) + withoutTarget,
                )
                withoutTarget.size != col.restaurants.size -> col.copy(restaurants = withoutTarget)
                else -> col
            }
        }
        _collections.value = updated
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
     * Falls back to a generated id; titles are not deduped on purpose; the
     * React demo allows duplicates too.
     */
    fun createCollectionAndSave(name: String, restaurant: Restaurant) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        val newId = "col-" + System.currentTimeMillis().toString(36)
        val newCollection = WishlistCollection(
            id = newId,
            title = trimmed,
            restaurants = emptyList(),
            isDefault = false,
        )
        _collections.value = _collections.value + newCollection
        saveTo(newId, restaurant)
    }

    fun createCollection(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        val newCollection = WishlistCollection(
            id = "col-" + System.currentTimeMillis().toString(36),
            title = trimmed,
            restaurants = emptyList(),
            isDefault = false,
        )
        _collections.value = _collections.value + newCollection
    }

    fun renameCollection(collectionId: String, name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        _collections.value = _collections.value.map { col ->
            if (col.id == collectionId && !col.isDefault) col.copy(title = trimmed) else col
        }
    }

    fun deleteCollection(collectionId: String) {
        val removed = _collections.value.firstOrNull { it.id == collectionId && !it.isDefault } ?: return
        val fallbackId = "recent"
        val fallbackItems = removed.restaurants
        _collections.value = _collections.value
            .filterNot { it.id == collectionId }
            .map { col ->
                if (col.id == fallbackId) {
                    val merged = (fallbackItems + col.restaurants).distinctBy { it.id }
                    col.copy(restaurants = merged)
                } else {
                    col
                }
            }
    }

    /** Un-save a restaurant from every collection. Used by the wishlist detail
     *  page heart and any "remove" path. */
    fun removeFromAll(restaurantId: String) {
        _collections.value = _collections.value.map { col ->
            val filtered = col.restaurants.filterNot { it.id == restaurantId }
            if (filtered.size == col.restaurants.size) col else col.copy(restaurants = filtered)
        }
        val toast = _lastToast.value
        if (toast != null && toast.restaurant.id == restaurantId) {
            _lastToast.value = null
        }
    }

    fun dismissToast(id: Long) {
        if (_lastToast.value?.id == id) {
            _lastToast.value = null
        }
    }

    fun markGatheredShown() {
        _gatheredShown.value = true
    }
}
