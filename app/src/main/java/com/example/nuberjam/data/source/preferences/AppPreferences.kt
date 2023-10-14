package com.example.nuberjam.data.source.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.nuberjam.data.model.Account
import com.example.nuberjam.utils.AesEncryption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun saveLoginState(state: Boolean) {
        dataStore.edit { preferences ->
            preferences[LOGIN_KEY] = state
        }
    }

    suspend fun saveAccountState(account: Account) {
        dataStore.edit { preferences ->
            preferences[ACCOUNT_ID_KEY] = account.id
            preferences[ACCOUNT_NAME_KEY] = account.name
            preferences[ACCOUNT_USERNAME_KEY] = account.username
            preferences[ACCOUNT_EMAIL_KEY] = account.email
            preferences[ACCOUNT_PHOTO_KEY] = account.photo
            preferences[ACCOUNT_PASSWORD_KEY] = AesEncryption.encrypt(account.password)
            preferences[ACCOUNT_IS_ARTIST_KEY] = account.isArtist
        }
    }

    suspend fun clearAccountState() {
        dataStore.edit { preferences ->
            preferences.remove(ACCOUNT_ID_KEY)
            preferences.remove(ACCOUNT_NAME_KEY)
            preferences.remove(ACCOUNT_USERNAME_KEY)
            preferences.remove(ACCOUNT_EMAIL_KEY)
            preferences.remove(ACCOUNT_PHOTO_KEY)
            preferences.remove(ACCOUNT_PASSWORD_KEY)
            preferences.remove(ACCOUNT_IS_ARTIST_KEY)
        }
    }

    fun getLoginState(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[LOGIN_KEY] ?: false
        }
    }

    fun getAccountState(): Flow<Account> {
        return dataStore.data.map { preferences ->
            val id = preferences[ACCOUNT_ID_KEY] ?: 0
            val name = preferences[ACCOUNT_NAME_KEY] ?: ""
            val username = preferences[ACCOUNT_USERNAME_KEY] ?: ""
            val email = preferences[ACCOUNT_EMAIL_KEY] ?: ""
            val photo = preferences[ACCOUNT_PHOTO_KEY] ?: ""
            val password = preferences[ACCOUNT_PASSWORD_KEY] ?: ""
            val isArtist = preferences[ACCOUNT_IS_ARTIST_KEY] ?: false
            Account(id, name, username, email, AesEncryption.decrypt(password), photo, isArtist)
        }
    }

    suspend fun saveCurrentMusic(musicId: Int) {
        dataStore.edit { preferences ->
            preferences[MUSIC_ID_KEY] = musicId
        }
    }

    fun getCurrentMusic(): Flow<Int?> {
        return dataStore.data.map { preferences ->
            preferences[MUSIC_ID_KEY]
        }
    }

    companion object {
        private val LOGIN_KEY = booleanPreferencesKey("has_login")
        private val ACCOUNT_ID_KEY = intPreferencesKey("account_id")
        private val ACCOUNT_NAME_KEY = stringPreferencesKey("account_name")
        private val ACCOUNT_USERNAME_KEY = stringPreferencesKey("account_username")
        private val ACCOUNT_EMAIL_KEY = stringPreferencesKey("account_email")
        private val ACCOUNT_PHOTO_KEY = stringPreferencesKey("account_photo")
        private val ACCOUNT_PASSWORD_KEY = stringPreferencesKey("account_password")
        private val ACCOUNT_IS_ARTIST_KEY = booleanPreferencesKey("account_is_artist")
        private val MUSIC_ID_KEY = intPreferencesKey("music_id")
    }
}