package com.mh.restaurantchainreservation.core.model

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Tracks whether the on-device restaurant catalog matches the latest app data version.
 * Drives the "Update local data?" prompt on Discover after sign-in or when data is stale.
 */
object LocalDataSyncStore {
    private const val PrefsName = "tonight_local_data_sync"
    private const val KeySyncedVersion = "synced_catalog_version"

    /** Bump when mock catalog / offline bundle changes. */
    const val CURRENT_CATALOG_VERSION = "2026.04.27"

    const val ESTIMATED_SYNC_SECONDS = 8

    private var prefs: SharedPreferences? = null

    private val _mandatorySyncAfterSignIn = MutableStateFlow(false)
    val mandatorySyncAfterSignIn: StateFlow<Boolean> = _mandatorySyncAfterSignIn.asStateFlow()

    private val _updatePromptPostponed = MutableStateFlow(false)
    val updatePromptPostponed: StateFlow<Boolean> = _updatePromptPostponed.asStateFlow()

    private val _shouldShowUpdatePrompt = MutableStateFlow(false)
    val shouldShowUpdatePrompt: StateFlow<Boolean> = _shouldShowUpdatePrompt.asStateFlow()

    fun init(context: Context) {
        if (prefs != null) return
        prefs = context.applicationContext.getSharedPreferences(PrefsName, Context.MODE_PRIVATE)
        refreshShouldShowUpdatePrompt()
    }

    fun syncedCatalogVersion(): String? = prefs?.getString(KeySyncedVersion, null)

    fun shouldPromptForUpdate(): Boolean {
        val synced = syncedCatalogVersion() ?: return true
        return synced != CURRENT_CATALOG_VERSION
    }

    /** Call when the user completes sign-in / registration so Discover must show the sync modal. */
    fun requestMandatorySyncAfterSignIn() {
        _mandatorySyncAfterSignIn.value = true
        _updatePromptPostponed.value = false
        refreshShouldShowUpdatePrompt()
    }

    fun clearMandatorySyncRequest() {
        _mandatorySyncAfterSignIn.value = false
        refreshShouldShowUpdatePrompt()
    }

    fun postponeUpdatePrompt() {
        _updatePromptPostponed.value = true
        clearMandatorySyncRequest()
    }

    fun shouldShowUpdatePromptWhenSignedIn(): Boolean = _shouldShowUpdatePrompt.value

    fun markCatalogSynced() {
        prefs?.edit()?.putString(KeySyncedVersion, CURRENT_CATALOG_VERSION)?.apply()
        clearMandatorySyncRequest()
        refreshShouldShowUpdatePrompt()
    }

    private fun refreshShouldShowUpdatePrompt() {
        _shouldShowUpdatePrompt.value = !_updatePromptPostponed.value &&
            (shouldPromptForUpdate() || _mandatorySyncAfterSignIn.value)
    }
}
