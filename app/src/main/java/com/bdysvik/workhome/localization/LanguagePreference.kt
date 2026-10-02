package com.bdysvik.workhome.localization

import android.content.Context

object LanguagePreference {
    private const val PREFS_NAME = "workhome_preferences"
    private const val KEY_LANGUAGE = "selected_language"

    fun getLanguage(context: Context): AppLanguage {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val code = prefs.getString(KEY_LANGUAGE, null)
        return AppLanguage.fromCode(code)
    }

    fun setLanguage(context: Context, language: AppLanguage) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, language.code).apply()
    }
}
