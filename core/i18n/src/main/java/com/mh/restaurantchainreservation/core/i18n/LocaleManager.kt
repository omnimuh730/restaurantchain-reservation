package com.mh.restaurantchainreservation.core.i18n

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleManager {
    private const val LocalePrefsName = "restaurant_locale_prefs"
    private const val LocalePrefsKey = "locale_tag"
    private const val DefaultLocale = "en"

    fun initialize(context: Context) {
        val stored = context.getSharedPreferences(LocalePrefsName, Context.MODE_PRIVATE)
            .getString(LocalePrefsKey, DefaultLocale)
            ?: DefaultLocale
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(stored))
    }

    fun setLocale(context: Context, languageTag: String) {
        context.getSharedPreferences(LocalePrefsName, Context.MODE_PRIVATE)
            .edit()
            .putString(LocalePrefsKey, languageTag)
            .apply()
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
    }

    fun getLocale(context: Context): String {
        return context.getSharedPreferences(LocalePrefsName, Context.MODE_PRIVATE)
            .getString(LocalePrefsKey, DefaultLocale)
            ?: DefaultLocale
    }
}
