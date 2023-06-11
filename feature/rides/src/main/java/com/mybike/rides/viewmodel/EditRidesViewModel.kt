package com.mybike.rides.viewmodel


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

class EditRidesViewModel(
    ridesRepository: RidesRepository,
    bikesRepository: BikesRepository,
    settingsRepository: SettingsRepository
) :
    BaseRidesViewModel(ridesRepository, bikesRepository, settingsRepository) {

    val getRideRequestState = MutableStateFlow(
        RepositoryRequestState(
            Status.PAUSED,
            Ride(0, "", "",0, 0, 0),
            ""
        )
    )

    fun getRide(rideId: Int) {
        getRideRequestState.value = RepositoryRequestState.loading()

        viewModelScope.launch {
            ridesRepository.getRideById(rideId)
                .catch {
                    getRideRequestState.value =
                        RepositoryRequestState.error(it.message.toString())
                }
                .collect {
                    getRideRequestState.value = RepositoryRequestState.success(it)
                }
        }
    }
}