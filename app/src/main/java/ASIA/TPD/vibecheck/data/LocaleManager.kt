/*
MIT License
Copyright (c) 2026 ASIA TPD
See the LICENSE file in the project root for full license information.
*/
package top.lucanex.top.vibecheck.data

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleManager {
    private const val PREFS = "vibe_prefs"
    private const val KEY_LOCALE = "app_locale"

    fun applySavedLocale(context: Context) {
        val tag = getSavedLocaleTag(context)
        val locales = if (tag == "system") LocaleListCompat.getEmptyLocaleList()
        else LocaleListCompat.forLanguageTags(tag)
        AppCompatDelegate.setApplicationLocales(locales)
    }

    fun setLocale(context: Context, tag: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LOCALE, tag)
            .apply()
        applySavedLocale(context)
        if (context is Activity) {
            context.recreate()
        }
    }

    fun getSavedLocaleTag(context: Context): String {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_LOCALE, "system") ?: "system"
    }
}
