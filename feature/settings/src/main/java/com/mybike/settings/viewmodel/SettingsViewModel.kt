package com.mybike.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybike.data.model.RepositoryRequestState
import com.mybike.data.model.Settings
import com.mybike.data.model.Status
import com.mybike.data.repository.BikesRepository
import com.mybike.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val bikesRepository: BikesRepository
) : ViewModel() {

    val setSettingsRequestState = MutableStateFlow(
        RepositoryRequestState(
            Status.PAUSED,
            null,
            ""
        )
    )

    val getSettingsRequestState = MutableStateFlow(
        RepositoryRequestState(
            Status.PAUSED,
            Settings(),
            ""
        )
    )

    val getBikeNamesRequestState = MutableStateFlow(
        RepositoryRequestState(
            Status.PAUSED,
            listOf<String>(),
            ""
        )
    )

    fun setSettings(settings: Settings) {
        setSettingsRequestState.value = RepositoryRequestState.loading()

        viewModelScope.launch {
            try {
                bikesRepository.setAllBikesAsNotDefault()
                bikesRepository.updateDefaultBike(settings.defaultBikeName)
                settingsRepository.setSettings(settings)
                setSettingsRequestState.value = RepositoryRequestState.success(null)
            } catch (exception: Exception) {
                setSettingsRequestState.value =
                    RepositoryRequestState.error(exception.message.toString())
            }
        }
    }

    fun getSettings() {
        getSettingsRequestState.value = RepositoryRequestState.loading()

        viewModelScope.launch {
            try {
                val settings = settingsRepository.getSettings().first()
                val defaultBikeName = bikesRepository.getDefaultBikeName()
                if (defaultBikeName != settings.defaultBikeName) settings.defaultBikeName = defaultBikeName
                getSettingsRequestState.value =
                    RepositoryRequestState.success(settings)
            } catch (exception: Exception) {
                getSettingsRequestState.value =
                    RepositoryRequestState.error(exception.message.toString())
            }
        }
    }

    fun getBikeNames() {
        getBikeNamesRequestState.value = RepositoryRequestState.loading()

        viewModelScope.launch {
            bikesRepository.getAllBikeNames()
                .catch {
                    getBikeNamesRequestState.value =
                        RepositoryRequestState.error(it.message.toString())
                }
                .collect {
                    getBikeNamesRequestState.value = RepositoryRequestState.success(it)
                }
        }
    }
}