package com.example.nuberjam.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.example.nuberjam.data.model.Account
import com.example.nuberjam.data.source.local.service.DbDao
import com.example.nuberjam.data.source.preferences.AppPreferences
import com.example.nuberjam.data.source.remote.service.ApiConfig
import com.example.nuberjam.data.source.remote.service.ApiService
import com.example.nuberjam.utils.Constant
import com.example.nuberjam.utils.FormValidation
import com.example.nuberjam.utils.Mapping

class Repository private constructor(
    private val apiService: ApiService,
    private val dbDao: DbDao,
    private val appPreferences: AppPreferences
) {

    fun makeLogin(usernameOrEmail: String, password: String): LiveData<Result<Boolean>> = liveData {
        emit(Result.Loading)
        try {
            val response = if (FormValidation.isEmailValid(usernameOrEmail)) {
                apiService.makeLoginWithEmail(usernameOrEmail, password)
            } else {
                apiService.makeLoginWithUsername(usernameOrEmail, password)
            }
            val status = response.status
            if (status == Constant.API_SUCCESS_CODE) emit(Result.Success(true))
            else emit(Result.Success(false))
        } catch (e: Exception) {
            Log.e(TAG, "makeLogin: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getAccountData(usernameOrEmail: String): LiveData<Result<Account>> = liveData {
        emit(Result.Loading)
        try {
            val response = if (FormValidation.isEmailValid(usernameOrEmail)) {
                apiService.readAccountWithEmail(usernameOrEmail)
            } else {
                apiService.readAccountWithUsername(usernameOrEmail)
            }
            if (response.data?.account != null) {
                val accountItem = response.data.account[0]
                val account = Mapping.accountItemToAccount(accountItem)
                emit(Result.Success(account))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getAccountData: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun saveLoginState(state: Boolean) {
        appPreferences.saveLoginState(state)
    }

    suspend fun saveAccountState(account: Account) {
        appPreferences.saveAccountState(account)
    }

    suspend fun clearAccountState() {
        appPreferences.clearAccountState()
    }
    
    fun getLoginState(): LiveData<Boolean> = appPreferences.getLoginState().asLiveData()

    fun getAccountState(): LiveData<Account> = appPreferences.getAccountState().asLiveData()

    fun checkUsernameExist(username: String): LiveData<Result<Boolean>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.readAccountWithUsername(username)
            val status = response.status
            if (status == ApiConfig.SUCCESS_CODE) emit(Result.Success(true))
            else emit(Result.Success(false))
        } catch (e: Exception) {
            Log.e(TAG, "checkUsernameExist: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun checkEmailExist(email: String): LiveData<Result<Boolean>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.readAccountWithEmail(email)
            val status = response.status
            if (status == ApiConfig.SUCCESS_CODE) emit(Result.Success(true))
            else emit(Result.Success(false))
        } catch (e: Exception) {
            Log.e(TAG, "checkEmailExist: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun addAccount(account: Account): LiveData<Result<Boolean>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.addAccount(
                account.name,
                account.username,
                account.email,
                account.password
            )
            val status = response.status
            if (status == ApiConfig.SUCCESS_CODE) emit(Result.Success(true))
            else emit(Result.Success(false))
        } catch (e: Exception) {
            Log.e(TAG, "addAccount: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        val TAG: String = Repository::class.java.simpleName

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