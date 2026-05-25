package com.mh.restaurantchainreservation.feature.wishlist.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainreservation.core.model.WishlistStore
import kotlinx.coroutines.delay

private val WishlistToastFloatingGap = 12.dp

/**
 * Globally-mounted overlay that owns the bottom-anchored Wishlist UI:
 * - The selection bottom sheet that opens when any heart is tapped.
 * - The "Saved to <Collection>" toast that auto-dismisses after ~3.5s.
 *
 * Mount once in the root Scaffold so the overlays appear above any
 * destination, regardless of the active tab. Toast bottom padding clears the
 * system navigation bar when the tab bar is hidden, or floats above the tab bar
 * when it is visible.
 */
@Composable
fun WishlistOverlayHost(
    bottomInset: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier,
) {
    val pendingPick by WishlistStore.pendingPickRestaurant.collectAsState()
    val toast by WishlistStore.lastToast.collectAsState()

    // Auto-dismiss the toast after 3.5s, keyed on toast id so a fresh toast
    // restarts the timer.
    LaunchedEffect(toast?.id) {
        val id = toast?.id ?: return@LaunchedEffect
        delay(3500)
        WishlistStore.dismissToast(id)
    }

    val pick = pendingPick
    if (pick != null) {
        WishlistSelectionSheet(
            restaurant = pick,
            onDismiss = { WishlistStore.closePicker() },
        )
    }

    val bottomNavPadding = bottomInset.calculateBottomPadding()
    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val toastBottomPadding = if (bottomNavPadding > 0.dp) {
        bottomNavPadding + WishlistToastFloatingGap
    } else {
        navigationBarPadding + WishlistToastFloatingGap
    }

    // Only occupy hit-test bounds when the toast is visible so the discover (and other)
    // scroll surfaces underneath keep receiving gestures.
    Box(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.BottomCenter),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = toastBottomPadding),
        ) {
            WishlistSavedToast(
                toast = toast,
                onChange = {
                    val r = toast?.restaurant ?: return@WishlistSavedToast
                    WishlistStore.dismissToast(toast?.id ?: 0L)
                    WishlistStore.openPicker(r)
                },
            )
        }
    }
}
