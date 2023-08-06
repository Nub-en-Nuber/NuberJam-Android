package com.example.nuberjam.data.source.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.nuberjam.data.model.Account
import com.example.nuberjam.utils.Constant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val LOGIN_KEY = booleanPreferencesKey("has_login")
    private val ACCOUNT_ID_KEY = intPreferencesKey("account_id")
    private val ACCOUNT_NAME_KEY = stringPreferencesKey("account_name")
    private val ACCOUNT_USERNAME_KEY = stringPreferencesKey("account_username")
    private val ACCOUNT_EMAIL_KEY = stringPreferencesKey("account_email")

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

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constant.DATASTORE_NAME)

        @Volatile
        private var INSTANCE: AppPreferences? = null

        fun getInstance(context: Context): AppPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = AppPreferences(context.dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}