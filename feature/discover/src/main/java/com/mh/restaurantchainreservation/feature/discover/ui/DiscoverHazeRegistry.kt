package com.mh.restaurantchainreservation.feature.discover.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import dev.chrisbanes.haze.HazeState

/**
 * Holds the active Discover [HazeState] so in-screen glass surfaces (e.g. [CompactDiscoverBar])
 * can share the same blur source as the feed.
 */
object DiscoverHazeRegistry {
    internal val hazeStateState = mutableStateOf<HazeState?>(null)

    fun register(state: HazeState) {
        hazeStateState.value = state
    }

    fun unregister(state: HazeState) {
        if (hazeStateState.value === state) {
            hazeStateState.value = null
        }
    }
}

/** Optional composition-local access within the Discover tree. */
val LocalDiscoverHazeState = staticCompositionLocalOf<HazeState?> { null }
