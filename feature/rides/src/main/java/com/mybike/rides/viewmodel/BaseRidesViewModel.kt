package com.mybike.rides.viewmodel

import androidx.lifecycle.ViewModel
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

abstract class BaseRidesViewModel(
    protected val ridesRepository: RidesRepository,
    private val bikesRepository: BikesRepository,
    protected val settingsRepository: SettingsRepository
) : ViewModel() {

    val deleteRideRequestState = MutableStateFlow(
        RepositoryRequestState(
            Status.PAUSED,
            listOf(null),
            ""
        )
    )

    val updateRideRequestState = MutableStateFlow(
        RepositoryRequestState(
            Status.PAUSED,
            listOf(null),
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

    val getPreferredUnitsRequestState = MutableStateFlow(
        RepositoryRequestState(
            Status.PAUSED,
            "",
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

    fun update(ride: Ride) {
        updateRideRequestState.value = RepositoryRequestState.loading()

        viewModelScope.launch {
            try {
                ridesRepository.update(ride)
                updateRideRequestState.value = RepositoryRequestState.success(null)
            } catch (exception: Exception) {
                updateRideRequestState.value =
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

    fun getPreferredUnits() {
        getPreferredUnitsRequestState.value = RepositoryRequestState.loading()

        viewModelScope.launch {
            try {
                settingsRepository.getSettings().collect{
                    getPreferredUnitsRequestState.value =
                        RepositoryRequestState.success(it.units)
                }
            } catch (exception: Exception) {
                getPreferredUnitsRequestState.value =
                    RepositoryRequestState.error(exception.message.toString())
            }
        }
    }
}