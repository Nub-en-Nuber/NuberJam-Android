package com.example.nuberjam.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.example.nuberjam.data.model.Account
import com.example.nuberjam.data.model.Album
import com.example.nuberjam.data.model.Music
import com.example.nuberjam.data.model.Playlist
import com.example.nuberjam.data.source.local.service.DbDao
import com.example.nuberjam.data.source.preferences.AppPreferences
import com.example.nuberjam.data.source.remote.service.ApiService
import com.example.nuberjam.utils.Constant
import com.example.nuberjam.utils.FormValidation
import com.example.nuberjam.utils.Mapping
import com.example.nuberjam.utils.NoConnectivityException
import javax.inject.Inject

class Repository @Inject constructor(
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
            if (e is NoConnectivityException) emit(Result.Error(Constant.API_INTERNET_ERROR_CODE))
            else emit(Result.Error(Constant.API_GENERAL_ERROR_CODE))
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

                var isArtist = false
                val response = apiService.checkAccountArtist(account.id.toString())
                if (response.status == Constant.API_SUCCESS_CODE) {
                    isArtist = true
                }
                emit(Result.Success(account.copy(isArtist = isArtist)))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getAccountData: ${e.message.toString()} ")
            if (e is NoConnectivityException) emit(Result.Error(Constant.API_INTERNET_ERROR_CODE))
            else emit(Result.Error(Constant.API_GENERAL_ERROR_CODE))
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
            if (status == Constant.API_SUCCESS_CODE) emit(Result.Success(true))
            else emit(Result.Success(false))
        } catch (e: Exception) {
            Log.e(TAG, "checkUsernameExist: ${e.message.toString()} ")
            if (e is NoConnectivityException) emit(Result.Error(Constant.API_INTERNET_ERROR_CODE))
            else emit(Result.Error(Constant.API_GENERAL_ERROR_CODE))
        }
    }

    fun checkEmailExist(email: String): LiveData<Result<Boolean>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.readAccountWithEmail(email)
            val status = response.status
            if (status == Constant.API_SUCCESS_CODE) emit(Result.Success(true))
            else emit(Result.Success(false))
        } catch (e: Exception) {
            Log.e(TAG, "checkEmailExist: ${e.message.toString()}")
            if (e is NoConnectivityException) emit(Result.Error(Constant.API_INTERNET_ERROR_CODE))
            else emit(Result.Error(Constant.API_GENERAL_ERROR_CODE))
        }
    }

    fun addAccount(account: Account): LiveData<Result<Boolean>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.addAccount(
                account.name, account.username, account.email, account.password
            )
            val status = response.status
            if (status == Constant.API_SUCCESS_CODE) emit(Result.Success(true))
            else emit(Result.Success(false))
        } catch (e: Exception) {
            Log.e(TAG, "addAccount: ${e.message.toString()}")
            if (e is NoConnectivityException) emit(Result.Error(Constant.API_INTERNET_ERROR_CODE))
            else emit(Result.Error(Constant.API_GENERAL_ERROR_CODE))
        }
    }

    fun readAllMusic(accountId: Int): LiveData<Result<List<Music>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.readAllMusic(accountId.toString())
            val listMusic = Mapping.dataResponseToMusic(response)
            emit(Result.Success(listMusic))
        } catch (e: Exception) {
            Log.e(TAG, "readAllMusic: ${e.message.toString()}")
            if (e is NoConnectivityException) emit(Result.Error(Constant.API_INTERNET_ERROR_CODE))
            else emit(Result.Error(Constant.API_GENERAL_ERROR_CODE))
        }
    }

    fun readAllAlbum(): LiveData<Result<List<Album>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.readAllAlbum()
            val listAlbum = response.data?.let { Mapping.albumItemToAlbum(it.album) } as List<Album>
            emit(Result.Success(listAlbum))
        } catch (e: Exception) {
            Log.e(TAG, "readAllAlbum: ${e.message.toString()}")
            if (e is NoConnectivityException) emit(Result.Error(Constant.API_INTERNET_ERROR_CODE))
            else emit(Result.Error(Constant.API_GENERAL_ERROR_CODE))
        }
    }

    fun readDetailMusic(accountId: Int, musicId: Int): LiveData<Result<Music>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.readDetailMusic(accountId.toString(), musicId.toString())
            val musicData = Mapping.dataResponseToMusic(response)[0]
            emit(Result.Success(musicData))
        } catch (e: Exception) {
            Log.e(TAG, "readDetailMusic: ${e.message.toString()}")
            if (e is NoConnectivityException) emit(Result.Error(Constant.API_INTERNET_ERROR_CODE))
            else emit(Result.Error(Constant.API_GENERAL_ERROR_CODE))
        }
    }

    suspend fun saveCurrentMusic(musicId: Int) {
        appPreferences.saveCurrentMusic(musicId)
    }

    fun getCurrentMusic(): LiveData<Int?> = appPreferences.getCurrentMusic().asLiveData()

    fun readAllPlaylist(accountId: Int): LiveData<Result<List<Playlist>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.readAllPlaylist(accountId.toString())
            val listPlaylist =
                response.data?.let { Mapping.playlistItemToPlaylist(it.playlist) } ?: ArrayList()
            emit(Result.Success(listPlaylist))
        } catch (e: Exception) {
            Log.e(TAG, "readAllPlaylist: ${e.message.toString()}")
            if (e is NoConnectivityException) emit(Result.Error(Constant.API_INTERNET_ERROR_CODE))
            else emit(Result.Error(Constant.API_GENERAL_ERROR_CODE))
        }
    }

    fun addPlaylist(playlistName: String, accountId: Int): LiveData<Result<Boolean>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.addPlaylist(playlistName, accountId.toString())
            val status = response.status
            if (status == Constant.API_SUCCESS_CODE) emit(Result.Success(true))
            else emit(Result.Success(false))
        } catch (e: Exception) {
            Log.e(TAG, "addPlaylist: ${e.message.toString()}")
            if (e is NoConnectivityException) emit(Result.Error(Constant.API_INTERNET_ERROR_CODE))
            else emit(Result.Error(Constant.API_GENERAL_ERROR_CODE))
        }
    }

    companion object {
        val TAG: String = Repository::class.java.simpleName
    }
}