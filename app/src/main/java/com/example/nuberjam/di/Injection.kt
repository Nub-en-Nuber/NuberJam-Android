package com.example.nuberjam.di

import android.content.Context
import com.example.nuberjam.data.Repository
import com.example.nuberjam.data.source.local.service.DbConfig
import com.example.nuberjam.data.source.preferences.AppPreferences
import com.example.nuberjam.data.source.remote.service.ApiConfig

object Injection {
    fun provideRepository(context: Context): Repository {
        val apiService = ApiConfig.getApiService()
        val dbDao = DbConfig.getInstance(context).dbDao()
        val appPreferences = AppPreferences.getInstance(context)
        return Repository.getInstance(apiService, dbDao, appPreferences)
    }

}