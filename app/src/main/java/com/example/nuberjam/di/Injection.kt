package com.example.nuberjam.di

import android.content.Context
import com.example.nuberjam.data.Repository
import com.example.nuberjam.data.source.local.service.DbConfig
import com.example.nuberjam.data.source.preferences.AppPreferences
import com.example.nuberjam.data.source.remote.service.ApiConfig
import com.example.nuberjam.utils.AppExecutors

object Injection {
    fun provideRepository(context: Context): Repository {
        val apiService = ApiConfig.getApiService()
        val dbDao = DbConfig.getInstance(context).dbDao()
        val appPreferences = AppPreferences.getInstance(context)
        val appExecutors = AppExecutors()
        return Repository.getInstance(apiService, dbDao, appPreferences, appExecutors)
    }

}