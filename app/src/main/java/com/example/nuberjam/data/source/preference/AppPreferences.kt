package com.example.nuberjam.data.source.preference

import android.content.Context

class AppPreferences(private val context: Context) {
    companion object {
        private const val PREFS_NAME = "app_pref"
    }

    fun getPreferences() = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}