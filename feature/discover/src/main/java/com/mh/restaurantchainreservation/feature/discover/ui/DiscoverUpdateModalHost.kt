package com.mh.restaurantchainreservation.feature.discover.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import com.mh.restaurantchainreservation.core.model.LocalDataSyncStore
import kotlinx.coroutines.delay

/**
 * Full-window update overlay (above bottom nav). Waits for Discover to paint, then animates in.
 */
@Composable
fun DiscoverUpdateModalHost(
    onDiscoverHome: Boolean,
    modifier: Modifier = Modifier,
) {
    val mandatorySyncAfterSignIn by LocalDataSyncStore.mandatorySyncAfterSignIn.collectAsState()
    val shouldShowUpdate by LocalDataSyncStore.shouldShowUpdatePrompt.collectAsState()
    val shouldQueue = onDiscoverHome && shouldShowUpdate
    val discoverHaze = DiscoverHazeRegistry.hazeStateState.value
    val discoverContentReady = DiscoverHazeRegistry.discoverContentReadyState.value

    var revealModal by remember { mutableStateOf(false) }
    val modalVisible = revealModal && discoverHaze != null

    DisposableEffect(onDiscoverHome) {
        if (!onDiscoverHome) {
            DiscoverHazeRegistry.setDiscoverContentReady(false)
            revealModal = false
        }
        onDispose { }
    }

    DisposableEffect(modalVisible) {
        DiscoverHazeRegistry.setUpdateModalVisible(modalVisible)
        onDispose { DiscoverHazeRegistry.setUpdateModalVisible(false) }
    }

    LaunchedEffect(shouldQueue, onDiscoverHome) {
        if (!shouldQueue || !onDiscoverHome) {
            revealModal = false
        }
    }

    LaunchedEffect(shouldQueue, discoverHaze, discoverContentReady, onDiscoverHome) {
        if (!shouldQueue || !onDiscoverHome || discoverHaze == null || !discoverContentReady) {
            return@LaunchedEffect
        }
        if (revealModal) return@LaunchedEffect
        // Let Discover paint at least one frame before the modal enters.
        withFrameNanos { }
        withFrameNanos { }
        delay(DiscoverUpdateModalRevealDelayMs)
        if (shouldQueue && onDiscoverHome) {
            revealModal = true
        }
    }

    AnimatedVisibility(
        visible = modalVisible,
        enter = fadeIn(tween(420, easing = FastOutSlowInEasing)) +
            scaleIn(
                initialScale = 0.94f,
                animationSpec = spring(dampingRatio = 0.82f, stiffness = 340f),
            ),
        exit = fadeOut(tween(260, easing = FastOutSlowInEasing)) +
            scaleOut(
                targetScale = 0.96f,
                animationSpec = tween(260, easing = FastOutSlowInEasing),
            ),
        modifier = modifier.fillMaxSize(),
    ) {
        discoverHaze?.let { haze ->
            UpdateLocalDataModal(
                hazeState = haze,
                mandatory = mandatorySyncAfterSignIn,
                onDismiss = { LocalDataSyncStore.postponeUpdatePrompt() },
                onUpdateComplete = { revealModal = false },
            )
        }
    }
}

/** Time to read Discover content before the update card animates in. */
private const val DiscoverUpdateModalRevealDelayMs = 520L
