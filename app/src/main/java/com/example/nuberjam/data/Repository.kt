package com.example.nuberjam.data

import com.example.nuberjam.data.source.local.service.DbDao
import com.example.nuberjam.data.source.preferences.AppPreferences
import com.example.nuberjam.data.source.remote.service.ApiService
import com.example.nuberjam.utils.AppExecutors

class Repository private constructor(
    private val apiService: ApiService,
    private val dbDao: DbDao,
    private val appPreferences: AppPreferences,
    private val appExecutors: AppExecutors
) {

    companion object {
        @Volatile
        private var instance: Repository? = null

        fun getInstance(
            apiService: ApiService,
            dbDao: DbDao,
            appPreferences: AppPreferences,
            appExecutors: AppExecutors
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, dbDao, appPreferences, appExecutors)
            }.also { instance = it }
    }
}