package com.mh.restaurantchainreservation.core.i18n

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LocaleManager {
    private const val LocalePrefsName = "restaurant_locale_prefs"
    private const val LocalePrefsKey = "locale_tag"
    private const val DefaultLocale = "en"

    fun initialize(context: Context) {
        val stored = context.getSharedPreferences(LocalePrefsName, Context.MODE_PRIVATE)
            .getString(LocalePrefsKey, DefaultLocale)
            ?: DefaultLocale
        applyLocale(context.applicationContext, stored)
    }

    fun setLocale(context: Context, languageTag: String) {
        val normalized = languageTag.takeIf { it.startsWith("ko") }?.let { "ko" } ?: "en"
        context.getSharedPreferences(LocalePrefsName, Context.MODE_PRIVATE)
            .edit()
            .putString(LocalePrefsKey, normalized)
            .apply()
        applyLocale(context.applicationContext, normalized)
        if (context !== context.applicationContext) {
            applyLocale(context, normalized)
        }
    }

    fun getLocale(context: Context): String {
        return context.getSharedPreferences(LocalePrefsName, Context.MODE_PRIVATE)
            .getString(LocalePrefsKey, DefaultLocale)
            ?: DefaultLocale
    }

    private fun applyLocale(context: Context, languageTag: String) {
        val locale = Locale.forLanguageTag(languageTag)
        Locale.setDefault(locale)
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
    }
}
