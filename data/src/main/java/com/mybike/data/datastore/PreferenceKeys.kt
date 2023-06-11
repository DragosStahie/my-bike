package com.mybike.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val UNITS = stringPreferencesKey("UNITS")
    val SERVICE_REMINDER_VALUE = intPreferencesKey("SERVICE_REMINDER_VALUE")
    val SERVICE_REMINDERS_ENABLED = booleanPreferencesKey("SERVICE_REMINDERS_ENABLED")
    val DEFAULT_BIKE_NAME = stringPreferencesKey("DEFAULT_BIKE_NAME")
}
