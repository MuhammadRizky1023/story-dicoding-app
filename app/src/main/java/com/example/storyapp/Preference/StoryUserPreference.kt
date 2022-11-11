package com.example.storyapp.Preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoryUserPreference private constructor(private var dataStore: DataStore<Preferences>) {

    fun getLoginAuth(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[LOGIN_IS_ACTIVE] ?: false
        }
    }


    suspend fun savingLoginAuth(isLoginActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[LOGIN_IS_ACTIVE] = isLoginActive
        }
    }


    fun getTokenAuth(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_AUTH] ?: ""
        }
    }



    suspend fun savingTokenAuth(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_AUTH] = token
        }
    }

    fun getUserName(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[USER_NAME] ?: ""
        }
    }


    suspend fun saveUser(name: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME] = name
        }
    }


    companion object {
        @Volatile
        private var AUTHORIZED_USER: StoryUserPreference? = null

        fun getAccountUser(dataStore: DataStore<Preferences>): StoryUserPreference {
            return AUTHORIZED_USER ?: synchronized(this) {
                val authorize = StoryUserPreference(dataStore)
                AUTHORIZED_USER = authorize
                authorize
            }
        }

        private val  LOGIN_IS_ACTIVE = booleanPreferencesKey("LOGIN_IS_ACTIVE")
        private val TOKEN_AUTH = stringPreferencesKey("token")
        private val USER_NAME = stringPreferencesKey("name")

    }
}