package com.hlasoftware.focus.features.profile.data.repository

import android.content.Context
import android.content.SharedPreferences

class LanguageRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("lang_prefs", Context.MODE_PRIVATE)

    fun saveLanguage(language: String) {
        prefs.edit().putString("language_key", language).commit()
    }

    fun getLanguage(): String? {
        return prefs.getString("language_key", null)
    }
}
