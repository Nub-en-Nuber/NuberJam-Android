package com.example.nuberjam.data

import android.content.SharedPreferences
import com.example.nuberjam.data.source.local.service.DbDao
import com.example.nuberjam.data.source.remote.service.ApiService
import com.example.nuberjam.utils.AppExecutors

class Repository private constructor(
    private val apiService: ApiService,
    private val dbDao: DbDao,
    private val preferences: SharedPreferences,
    private val appExecutors: AppExecutors
) {

    companion object {
        @Volatile
        private var instance: Repository? = null

        fun getInstance(
            apiService: ApiService,
            dbDao: DbDao,
            preferences: SharedPreferences,
            appExecutors: AppExecutors
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, dbDao, preferences, appExecutors)
            }.also { instance = it }
    }
}