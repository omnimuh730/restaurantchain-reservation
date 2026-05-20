package com.mh.restaurantchainreservation.core.model

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AuthSessionStore {
    private const val PrefsName = "restaurant_auth_session"
    private const val KeyAuthenticated = "authenticated"

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    fun initialize(context: Context) {
        _isAuthenticated.value = context.applicationContext
            .getSharedPreferences(PrefsName, Context.MODE_PRIVATE)
            .getBoolean(KeyAuthenticated, false)
    }

    fun markAuthenticated(context: Context) {
        context.applicationContext
            .getSharedPreferences(PrefsName, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KeyAuthenticated, true)
            .apply()
        _isAuthenticated.value = true
    }

    fun signOut(context: Context) {
        context.applicationContext
            .getSharedPreferences(PrefsName, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KeyAuthenticated, false)
            .apply()
        _isAuthenticated.value = false
        UpdateDataModalStore.dismiss()
        UpdateDataModalStore.revealBottomNavUnderUpdateModal()
    }
}
