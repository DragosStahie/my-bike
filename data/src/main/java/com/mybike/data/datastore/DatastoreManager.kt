package com.mybike.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import okio.IOException

class DatastoreManager(private val context: Context) {
    companion object {
        private const val DATASTORE_NAME = "APP_DATA_STORE"
    }

    private val Context.prefsDataStore by preferencesDataStore(name = DATASTORE_NAME)

    suspend fun sampleUpdateMethod(example: String) {
        context.prefsDataStore.edit { preferences ->
            preferences[PreferenceKeys.EXAMPLE] = example
        }
    }

    suspend fun sampleGetMethod(): Flow<String> {
        return context.prefsDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { preferences ->
                preferences[PreferenceKeys.EXAMPLE] ?: ""
            }
    }
}