package com.mh.restaurantchainreservation.core.designsystem.components

import com.mh.restaurantchainreservation.core.designsystem.transition.RestaurantSharedTransitionChrome
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity

/**
 * Hides the bottom nav when the user scrolls up (revealing content below) and shows it when
 * they scroll down (toward the top), giving more room to browse.
 */
class BottomNavScrollBehavior {
    var isVisible by mutableStateOf(true)
        private set

    private var scrollUpAccumulator by mutableFloatStateOf(0f)

    val nestedScrollConnection: NestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (source != NestedScrollSource.UserInput) return Offset.Zero
            onScrollDelta(available.y)
            return Offset.Zero
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            // Pulling down at the top of the list — show the bar.
            if (source == NestedScrollSource.UserInput && available.y > 0f) {
                show()
            }
            return Offset.Zero
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            when {
                available.y < 0f -> hide()
                available.y > 0f -> show()
            }
            return Velocity.Zero
        }
    }

    fun show() {
        scrollUpAccumulator = 0f
        isVisible = true
        RestaurantSharedTransitionChrome.clearBottomNavSuppressOnDiscover()
    }

    fun hide() {
        scrollUpAccumulator = 0f
        isVisible = false
    }

    private fun onScrollDelta(deltaY: Float) {
        when {
            deltaY < 0f -> {
                scrollUpAccumulator += -deltaY
                if (scrollUpAccumulator >= HideThresholdPx) {
                    hide()
                }
            }
            deltaY > 0f -> show()
        }
    }

    private companion object {
        /** Scroll distance (px) before the bar hides. */
        const val HideThresholdPx = 36f
    }
}

@Composable
fun rememberBottomNavScrollBehavior(): BottomNavScrollBehavior = remember { BottomNavScrollBehavior() }

/** Provided by the app nav host on phone tab layouts, including while the bar is hidden. */
val LocalBottomNavScrollBehavior = staticCompositionLocalOf<BottomNavScrollBehavior?> { null }

/** Attach to lazy lists, grids, or [verticalScroll] so scroll drives hide/show. */
@Composable
fun Modifier.trackBottomNavScroll(): Modifier {
    val behavior = LocalBottomNavScrollBehavior.current ?: return this
    return nestedScroll(behavior.nestedScrollConnection)
}

/** Attach to a scrollable subtree when nested scroll does not reach the nav host ancestor. */
fun Modifier.bottomNavScrollBehavior(behavior: BottomNavScrollBehavior): Modifier =
    nestedScroll(behavior.nestedScrollConnection)
