package com.mybike.data.repository


import com.mybike.data.datastore.DatastoreManager
import com.mybike.data.model.Settings

class SettingsRepository(
    private val datastoreManager: DatastoreManager,
) {
    suspend fun getSettings() = datastoreManager.getSettings()

    suspend fun setSettings(settings: Settings) = datastoreManager.setSettings(settings)

}