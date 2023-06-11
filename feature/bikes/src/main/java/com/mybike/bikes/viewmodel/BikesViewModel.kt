package com.mybike.bikes.viewmodel

import androidx.lifecycle.viewModelScope
import com.mybike.data.model.Bike
import com.mybike.data.model.BikeItemWrapper
import com.mybike.data.model.RepositoryRequestState
import com.mybike.data.model.Status
import com.mybike.data.repository.BikesRepository
import com.mybike.data.repository.RidesRepository
import com.mybike.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class BikesViewModel(
    bikesRepository: BikesRepository,
    private val ridesRepository: RidesRepository,
    settingsRepository: SettingsRepository
) :
    BaseBikesViewModel(bikesRepository, settingsRepository) {

    val getBikeItemsRequestState = MutableStateFlow(
        RepositoryRequestState(
            Status.PAUSED,
            listOf(BikeItemWrapper(Bike("", "", 0, 0, 0, 0, false), 0, 0)),
            ""
        )
    )

    fun getBikeItems() {
        getBikeItemsRequestState.value = RepositoryRequestState.loading()

        viewModelScope.launch {
            combine(
                bikesRepository.getAllBikes(),
                ridesRepository.getAllRides()
            ) { bikesList, ridesList ->
                val bikeItemsList = mutableListOf<BikeItemWrapper>()
                bikesList.forEach { bike ->
                    val ridesForBike = ridesList.filter { ride -> ride.bikeName == bike.name }
                    bikeItemsList.add(
                        BikeItemWrapper(
                            bike,
                            ridesForBike.size,
                            ridesForBike.sumOf { ride -> ride.distanceKm }
                        )
                    )
                }
                return@combine bikeItemsList
            }
                .catch {
                    getBikeItemsRequestState.value =
                        RepositoryRequestState.error(it.message.toString())
                }
                .collect {
                    getBikeItemsRequestState.value = RepositoryRequestState.success(it)
                }
        }
    }
}