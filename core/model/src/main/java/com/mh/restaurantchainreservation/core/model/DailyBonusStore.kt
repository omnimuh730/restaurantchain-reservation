package com.mh.restaurantchainreservation.core.model

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val PrefsName = "daily_bonus_prefs"
private const val PrefsKey = "last_claim_yyyymmdd"

private fun todayKey(): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

object DailyBonusStore {
    private val state = MutableStateFlow(false)
    val claimed: StateFlow<Boolean> = state.asStateFlow()

    fun init(context: Context) {
        val prefs = context.applicationContext.getSharedPreferences(PrefsName, Context.MODE_PRIVATE)
        state.value = prefs.getString(PrefsKey, null) == todayKey()
    }

    fun markClaimed(context: Context) {
        val prefs = context.applicationContext.getSharedPreferences(PrefsName, Context.MODE_PRIVATE)
        prefs.edit().putString(PrefsKey, todayKey()).apply()
        state.value = true
    }

    fun shouldShow(): Boolean = !state.value
}
