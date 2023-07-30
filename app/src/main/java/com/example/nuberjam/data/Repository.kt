package com.example.nuberjam.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.nuberjam.data.source.local.service.DbDao
import com.example.nuberjam.data.source.preferences.AppPreferences
import com.example.nuberjam.data.source.remote.service.ApiConfig
import com.example.nuberjam.data.source.remote.service.ApiService

class Repository private constructor(
    private val apiService: ApiService,
    private val dbDao: DbDao,
    private val appPreferences: AppPreferences
) {

    fun checkUsernameExist(username: String): LiveData<Result<Boolean>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.readAccountWithUsername(username)
            val status = response.status
            if (status == ApiConfig.SUCCESS_CODE) emit(Result.Success(true))
            else emit(Result.Success(false))
        } catch (e: Exception) {
            Log.e("Repository", "checkUsernameExist: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: Repository? = null

        fun getInstance(
            apiService: ApiService,
            dbDao: DbDao,
            appPreferences: AppPreferences,
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, dbDao, appPreferences)
            }.also { instance = it }
    }
}