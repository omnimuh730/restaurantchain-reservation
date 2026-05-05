package com.mh.restaurantchainreservation.feature.wishlist.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainreservation.core.model.WishlistStore
import kotlinx.coroutines.delay

/**
 * Globally-mounted overlay that owns the bottom-anchored Wishlist UI:
 * - The selection bottom sheet that opens when any heart is tapped.
 * - The "Saved to <Collection>" toast that auto-dismisses after ~3.5s.
 *
 * Mount once in the root Scaffold so the overlays appear above any
 * destination, regardless of the active tab.
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

    Box(modifier = modifier.fillMaxSize()) {
        // Toast sits just above the bottom chrome.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = bottomInset.calculateBottomPadding()),
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
        // Sheet renders as its own Dialog, so it's automatically full-screen.
        val pick = pendingPick
        if (pick != null) {
            WishlistSelectionSheet(
                restaurant = pick,
                onDismiss = { WishlistStore.closePicker() },
            )
        }
    }
}
