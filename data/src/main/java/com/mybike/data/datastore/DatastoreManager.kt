package com.mybike.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.mybike.data.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import okio.IOException

class DatastoreManager(private val context: Context) {
    companion object {
        private const val DATASTORE_NAME = "APP_DATA_STORE"
    }

    private val Context.prefsDataStore by preferencesDataStore(name = DATASTORE_NAME)

    suspend fun setSettings(settings: Settings) {
        context.prefsDataStore.edit { preferences ->
            preferences[PreferenceKeys.UNITS] = settings.units
            preferences[PreferenceKeys.SERVICE_REMINDER_VALUE] = settings.serviceReminderKm
            preferences[PreferenceKeys.SERVICE_REMINDERS_ENABLED] = settings.serviceRemindersEnabled
            preferences[PreferenceKeys.DEFAULT_BIKE_NAME] = settings.defaultBikeName
        }
    }

    suspend fun getSettings(): Flow<Settings> {
        return context.prefsDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                Settings(
                preferences[PreferenceKeys.UNITS] ?: "Metric",
                preferences[PreferenceKeys.SERVICE_REMINDER_VALUE] ?: 100,
                preferences[PreferenceKeys.SERVICE_REMINDERS_ENABLED] ?: true,
                preferences[PreferenceKeys.DEFAULT_BIKE_NAME] ?: ""
                )
            }
    }
}