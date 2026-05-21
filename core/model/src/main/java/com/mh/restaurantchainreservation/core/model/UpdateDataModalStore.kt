package com.mh.restaurantchainreservation.core.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * One-shot flag set only when login or registration completes successfully.
 * Not set when a saved session is restored on cold start.
 */
object UpdateDataModalStore {
    private val _pendingAfterLogin = MutableStateFlow(false)
    val pendingAfterLogin: StateFlow<Boolean> = _pendingAfterLogin.asStateFlow()

    /** Hides bottom nav until Discover settles and the update modal opens (nav then sits under the overlay). */
    private val _suppressBottomNav = MutableStateFlow(false)
    val suppressBottomNav: StateFlow<Boolean> = _suppressBottomNav.asStateFlow()

    fun requestAfterLogin() {
        _suppressBottomNav.value = true
        _pendingAfterLogin.value = true
    }

    fun revealBottomNavUnderUpdateModal() {
        _suppressBottomNav.value = false
    }

    fun dismiss() {
        _pendingAfterLogin.value = false
    }
}
