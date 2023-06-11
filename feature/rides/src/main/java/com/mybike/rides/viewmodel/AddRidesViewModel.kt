package com.mybike.rides.viewmodel

import androidx.lifecycle.viewModelScope
import com.mybike.data.model.RepositoryRequestState
import com.mybike.data.model.Ride
import com.mybike.data.model.Status
import com.mybike.data.repository.BikesRepository
import com.mybike.data.repository.RidesRepository
import com.mybike.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AddRidesViewModel(ridesRepository: RidesRepository, bikesRepository: BikesRepository, settingsRepository: SettingsRepository) :
    BaseRidesViewModel(ridesRepository, bikesRepository, settingsRepository) {

    val insertRideRequestState = MutableStateFlow(
        RepositoryRequestState(
            Status.PAUSED,
            null,
            ""
        )
    )

    val getDefaultBikeNameRequestState = MutableStateFlow(
        RepositoryRequestState(
            Status.PAUSED,
            "",
            ""
        )
    )

    fun insert(ride: Ride) {
        insertRideRequestState.value = RepositoryRequestState.loading()

        viewModelScope.launch {
            try {
                ridesRepository.insert(ride)
                insertRideRequestState.value = RepositoryRequestState.success(null)
            } catch (exception: Exception) {
                insertRideRequestState.value =
                    RepositoryRequestState.error(exception.message.toString())
            }
        }
    }

    fun getDefaultBikeName() {
        getDefaultBikeNameRequestState.value = RepositoryRequestState.loading()

        viewModelScope.launch {
            try {
                settingsRepository.getSettings().collect{
                    getDefaultBikeNameRequestState.value =
                        RepositoryRequestState.success(it.defaultBikeName)
                }
            } catch (exception: Exception) {
                getDefaultBikeNameRequestState.value =
                    RepositoryRequestState.error(exception.message.toString())
            }
        }
    }

}