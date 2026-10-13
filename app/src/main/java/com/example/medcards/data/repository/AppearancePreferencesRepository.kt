package com.example.medcards.data.repository

import android.content.Context

class AppearancePreferencesRepository(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    val darkThemeEnabled: Boolean
        get() = preferences.getBoolean(KEY_DARK_THEME, false)

    fun setDarkThemeEnabled(enabled: Boolean) {
        check(
            preferences.edit().putBoolean(KEY_DARK_THEME, enabled).commit()
        ) { "No se pudo guardar la preferencia del tema." }
    }

    private companion object {
        const val PREFERENCES_NAME = "appearance_preferences"
        const val KEY_DARK_THEME = "dark_theme_enabled"
    }
}
