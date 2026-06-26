package com.example.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val IS_REGISTERED = booleanPreferencesKey("is_registered")
    }

    val isRegistered: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[IS_REGISTERED] ?: false }

    suspend fun setRegistered(value: Boolean) {
        dataStore.edit { prefs -> prefs[IS_REGISTERED] = value }
    }
}
