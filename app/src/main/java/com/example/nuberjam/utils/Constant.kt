package com.example.nuberjam.utils

import com.example.nuberjam.BuildConfig

object Constant {
    const val TOKEN = BuildConfig.TOKEN
    const val DATASTORE_NAME = "nuberjam_datastore"
    const val DATABASE_NAME = "nuberjam_db"
    const val API_SUCCESS_CODE = 200
    const val FORM_TYPING_DELAY = 600L

    const val API_GENERAL_ERROR_CODE = 410
    const val API_INTERNET_ERROR_CODE = 411

    const val LIBRARY_LINEAR_TYPE = "linear"
    const val LIBRARY_GRID_TYPE = "grid"
}

enum class LibraryDetailType(val code: Int) {
    Favorite(1), Album(2), Playlist(3)
}