package com.mybike

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybike.data.model.RepositoryRequestState
import com.mybike.data.model.Settings
import com.mybike.data.model.Status
import com.mybike.data.repository.BikesRepository
import com.mybike.data.repository.RidesRepository
import com.mybike.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(
    private val bikesRepository: BikesRepository,
    private val ridesRepository: RidesRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val getServiceNotificationInfoRequestState = MutableStateFlow(
        RepositoryRequestState(
            Status.PAUSED,
            listOf(Pair("", 0)),
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

    val setBikeNextServiceRequestState = MutableStateFlow(
        RepositoryRequestState(
            Status.PAUSED,
            null,
            ""
        )
    )

    fun getServiceNotificationInfo() {
        getServiceNotificationInfoRequestState.value = RepositoryRequestState.loading()

        viewModelScope.launch {
            combine(
                bikesRepository.getAllBikes(),
                ridesRepository.getAllRides()
            ) { bikesList, ridesList ->
                val bikeToServiceKmPairList = mutableListOf<Pair<String, Int>>()
                bikesList.forEach { bike ->
                    val totalKmForBike = ridesList.filter { ride -> ride.bikeName == bike.name }
                        .sumOf { ride -> ride.distanceKm }
                    bikeToServiceKmPairList.add(
                        Pair(bike.name, bike.nextServiceAtKm - totalKmForBike)
                    )
                }
                return@combine bikeToServiceKmPairList
            }
                .catch {
                    getServiceNotificationInfoRequestState.value =
                        RepositoryRequestState.error(it.message.toString())
                }
                .collect {
                    getServiceNotificationInfoRequestState.value =
                        RepositoryRequestState.success(it)
                }
        }
    }

    fun getSettings() {
        getSettingsRequestState.value = RepositoryRequestState.loading()

        viewModelScope.launch {
            try {
                settingsRepository.getSettings().collect{
                    getSettingsRequestState.value =
                        RepositoryRequestState.success(it)
                }
            } catch (exception: Exception) {
                getSettingsRequestState.value =
                    RepositoryRequestState.error(exception.message.toString())
            }
        }
    }

    fun resetBikeNextService(bikeName: String) {
        setBikeNextServiceRequestState.value = RepositoryRequestState.loading()

        viewModelScope.launch {
            try {
                val bike = bikesRepository.getBikeByName(bikeName)
                val currentBikeKm = ridesRepository.getRidesForBike(bikeName).first().sumOf { ride -> ride.distanceKm }

                bikesRepository.update(bike.copy(nextServiceAtKm = bike.serviceIntervalKm + currentBikeKm))
                setBikeNextServiceRequestState.value = RepositoryRequestState.success(null)
            } catch (exception: Exception) {
                setBikeNextServiceRequestState.value =
                    RepositoryRequestState.error(exception.message.toString())
            }
        }
    }
}