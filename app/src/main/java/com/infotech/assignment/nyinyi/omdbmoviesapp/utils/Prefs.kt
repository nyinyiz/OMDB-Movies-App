package com.infotech.assignment.nyinyi.omdbmoviesapp.utils

import android.annotation.SuppressLint
import android.content.SharedPreferences

class Prefs(private val prefs: SharedPreferences) {

    private val KEY_QUERY = "KEY_QUERY"

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    fun getQuery(): String {
        return prefs.getString(KEY_QUERY, DEFAULT_QUERY) ?: ""
    }

    @SuppressLint("ApplySharedPref") // because we need it immediately
    fun setQuery(selectedCountry: String) {
        prefs.edit().putString(KEY_QUERY, selectedCountry).commit()
    }

}