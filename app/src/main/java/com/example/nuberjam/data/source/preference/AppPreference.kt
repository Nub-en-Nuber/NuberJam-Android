package com.example.nuberjam.data.source.preference

import android.content.Context

class AppPreference(context: Context) {
    companion object {
        private const val PREFS_NAME = "app_pref"
    }

    val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}