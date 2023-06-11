package com.mybike.bikes.viewmodel

import androidx.lifecycle.viewModelScope
import com.mybike.data.model.RepositoryRequestState
import com.mybike.data.model.Ride
import com.mybike.data.model.Status
import com.mybike.data.repository.BikesRepository
import com.mybike.data.repository.RidesRepository
import com.mybike.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class BikeDetailsViewModel(
    private val ridesRepository: RidesRepository,
    bikesRepository: BikesRepository,
    settingsRepository: SettingsRepository
) : BaseBikesViewModel(bikesRepository, settingsRepository) {


    val deleteRideRequestState = MutableStateFlow(
        RepositoryRequestState(
            Status.PAUSED,
            listOf(null),
            ""
        )
    )

    val getRidesRequestState = MutableStateFlow(
        RepositoryRequestState(
            Status.PAUSED,
            listOf(Ride(0, "", "",0, 0, 0)),
            ""
        )
    )


    fun deleteRide(ride: Ride) {
        deleteRideRequestState.value = RepositoryRequestState.loading()

        viewModelScope.launch {
            try {
                ridesRepository.delete(ride)
                deleteRideRequestState.value = RepositoryRequestState.success(null)
            } catch (exception: Exception) {
                deleteRideRequestState.value =
                    RepositoryRequestState.error(exception.message.toString())
            }
        }
    }

    fun getRidesForBike(bikeName: String) {
        getRidesRequestState.value = RepositoryRequestState.loading()

        viewModelScope.launch {
            ridesRepository.getRidesForBike(bikeName)
                .catch {
                    getRidesRequestState.value =
                        RepositoryRequestState.error(it.message.toString())
                }
                .collect {
                    getRidesRequestState.value = RepositoryRequestState.success(it)
                }
        }
    }
}