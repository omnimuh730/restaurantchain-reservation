package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DsToastState(
    private val snackbarHostState: SnackbarHostState,
    private val scope: CoroutineScope,
) {
    fun show(message: String) {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                withDismissAction = true,
                duration = SnackbarDuration.Short,
            )
        }
    }
}

@Composable
fun rememberDsToastState(
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
): DsToastState {
    return remember(snackbarHostState, scope) {
        DsToastState(snackbarHostState, scope)
    }
}
