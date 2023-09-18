package com.example.nuberjam.data.source.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.nuberjam.data.model.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val LOGIN_KEY = booleanPreferencesKey("has_login")
    private val ACCOUNT_ID_KEY = intPreferencesKey("account_id")
    private val ACCOUNT_NAME_KEY = stringPreferencesKey("account_name")
    private val ACCOUNT_USERNAME_KEY = stringPreferencesKey("account_username")
    private val ACCOUNT_EMAIL_KEY = stringPreferencesKey("account_email")
    private val MUSIC_ID_KEY = intPreferencesKey("music_id")

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
        }
    }

    suspend fun clearAccountState() {
        dataStore.edit { preferences ->
            preferences.remove(ACCOUNT_ID_KEY)
            preferences.remove(ACCOUNT_NAME_KEY)
            preferences.remove(ACCOUNT_USERNAME_KEY)
            preferences.remove(ACCOUNT_EMAIL_KEY)
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
            val name = preferences[ACCOUNT_NAME_KEY] ?: "null"
            val username = preferences[ACCOUNT_USERNAME_KEY] ?: "null"
            val email = preferences[ACCOUNT_EMAIL_KEY] ?: "null"
            Account(id, name, username, email, "null", "null")
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
}