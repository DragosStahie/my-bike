package com.mybike.bikes.viewmodel

import androidx.lifecycle.viewModelScope
import com.mybike.data.model.Bike
import com.mybike.data.model.RepositoryRequestState
import com.mybike.data.model.Status
import com.mybike.data.repository.BikesRepository
import com.mybike.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AddBikesViewModel(bikesRepository: BikesRepository, settingsRepository: SettingsRepository) :
    BaseBikesViewModel(bikesRepository, settingsRepository) {

    val insertBikeRequestState = MutableStateFlow(
        RepositoryRequestState(
            Status.PAUSED,
            null,
            ""
        )
    )

    fun insert(bike: Bike) {
        insertBikeRequestState.value = RepositoryRequestState.loading()

        viewModelScope.launch {
            try {
                if (bike.isDefault) {
                    bikesRepository.setAllBikesAsNotDefault()
                }
                bikesRepository.insert(bike)
                insertBikeRequestState.value = RepositoryRequestState.success(null)
            } catch (exception: Exception) {
                insertBikeRequestState.value =
                    RepositoryRequestState.error(exception.message.toString())
            }
        }
    }

}