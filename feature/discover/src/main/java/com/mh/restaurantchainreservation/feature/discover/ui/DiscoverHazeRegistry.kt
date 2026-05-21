package com.mh.restaurantchainreservation.feature.discover.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import dev.chrisbanes.haze.HazeState

/**
 * Holds the active Discover [HazeState] so a full-screen nav overlay can apply the same
 * [HazeMaterials.thin] blur as [CompactDiscoverBar] over the entire window (including bottom nav).
 */
object DiscoverHazeRegistry {
    internal val hazeStateState = mutableStateOf<HazeState?>(null)
    val updateModalVisibleState = mutableStateOf(false)
    val discoverContentReadyState = mutableStateOf(false)

    fun register(state: HazeState) {
        hazeStateState.value = state
    }

    fun unregister(state: HazeState) {
        if (hazeStateState.value === state) {
            hazeStateState.value = null
        }
        setDiscoverContentReady(false)
    }

    fun setUpdateModalVisible(visible: Boolean) {
        updateModalVisibleState.value = visible
    }

    fun setDiscoverContentReady(ready: Boolean) {
        discoverContentReadyState.value = ready
    }
}

/** Optional composition-local access within the Discover tree. */
val LocalDiscoverHazeState = staticCompositionLocalOf<HazeState?> { null }
