package com.example.cinetrack.network

import android.content.Context

object SettingsRepo {

    private const val PREFS_NAME = "settings_prefs"
    private const val KEY_PROFILE_PICTURE = "profile_picture"
    private const val KEY_DARK_MODE = "dark_mode"

    fun saveProfilePicture(context: Context, uri: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_PROFILE_PICTURE, uri)
            .apply()
    }

    fun getProfilePicture(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_PROFILE_PICTURE, null)
    }

    fun saveDarkMode(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK_MODE, enabled)
            .apply()
    }

    fun getDarkMode(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_DARK_MODE, false)
    }
}