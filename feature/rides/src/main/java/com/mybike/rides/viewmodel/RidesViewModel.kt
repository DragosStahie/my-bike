package com.mybike.rides.viewmodel


import androidx.lifecycle.viewModelScope
import com.mybike.common.InfoRidesRecyclerViewItem
import com.mybike.common.createItemsList
import com.mybike.data.model.RepositoryRequestState
import com.mybike.data.model.Status
import com.mybike.data.repository.BikesRepository
import com.mybike.data.repository.RidesRepository
import com.mybike.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class RidesViewModel(
    ridesRepository: RidesRepository,
    bikesRepository: BikesRepository,
    settingsRepository: SettingsRepository
) :
    BaseRidesViewModel(ridesRepository, bikesRepository, settingsRepository) {

    val getRidesInfoRequestState = MutableStateFlow(
        RepositoryRequestState(
            Status.PAUSED,
            listOf(InfoRidesRecyclerViewItem(null)),
            ""
        )
    )

    fun getRidesInfo() {
        getRidesInfoRequestState.value = RepositoryRequestState.loading()

        viewModelScope.launch {
            combine(ridesRepository.getAllRides(), ridesRepository.getRideStatistics()) { ridesList, rideStatistics ->
                return@combine ridesList.createItemsList(rideStatistics = rideStatistics)
            }
                .catch {
                    getRidesInfoRequestState.value =
                        RepositoryRequestState.error(it.message.toString())
                }
                .collect {
                    getRidesInfoRequestState.value = RepositoryRequestState.success(it)
                }
        }
    }
}