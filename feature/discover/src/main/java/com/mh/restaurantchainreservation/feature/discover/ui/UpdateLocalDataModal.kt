@file:OptIn(ExperimentalHazeMaterialsApi::class)

package com.mh.restaurantchainreservation.feature.discover.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.mh.restaurantchainreservation.core.designsystem.components.HubSurfaceCardDefaults
import com.mh.restaurantchainreservation.core.designsystem.components.hubSurfaceCard
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.LocalDataSyncStore
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

/**
 * Full-screen update prompt using the same [HazeMaterials.thin] frosted glass as
 * [CompactDiscoverBar] on Discover (requires a shared [hazeState] with [hazeSource]).
 */
@Composable
fun UpdateLocalDataModal(
    hazeState: HazeState,
    onDismiss: () -> Unit,
    onUpdateComplete: () -> Unit,
    mandatory: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    var syncing by remember { mutableStateOf(false) }
    val canDismissOverlay = !syncing && !mandatory

    BackHandler(enabled = canDismissOverlay) {
        onDismiss()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(20f),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blockInteractionsBehind()
                .hazeEffect(state = hazeState, style = HazeMaterials.thin())
                .then(
                    if (canDismissOverlay) {
                        Modifier.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = onDismiss,
                        )
                    } else {
                        Modifier
                    },
                ),
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 22.dp)
                .widthIn(max = 400.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .hubSurfaceCard(
                        palette = palette,
                        shape = HubSurfaceCardDefaults.Shape,
                    ),
            ) {
                if (syncing) {
                    SyncingLocalDataContent(
                        onFinished = {
                            LocalDataSyncStore.markCatalogSynced()
                            syncing = false
                            onUpdateComplete()
                        },
                    )
                } else {
                    UpdateLocalDataPromptContent(
                        onDismiss = onDismiss,
                        onUpdateNow = { syncing = true },
                        showCloseButton = !mandatory,
                    )
                }
            }
        }
    }
}

/** Blocks scroll, tap, and drag from reaching content under the update overlay. */
private fun Modifier.blockInteractionsBehind(): Modifier = pointerInput(Unit) {
    awaitEachGesture {
        awaitFirstDown(requireUnconsumed = false).consume()
        do {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            event.changes.forEach { it.consume() }
        } while (event.changes.any { it.pressed })
    }
}
