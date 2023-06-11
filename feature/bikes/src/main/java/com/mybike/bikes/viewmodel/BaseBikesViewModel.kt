package com.mybike.bikes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybike.data.model.Bike
import com.mybike.data.model.RepositoryRequestState
import com.mybike.data.model.Status
import com.mybike.data.repository.BikesRepository
import com.mybike.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

abstract class BaseBikesViewModel(
    protected val bikesRepository: BikesRepository,
    protected val settingsRepository: SettingsRepository
) : ViewModel() {

    val deleteBikeRequestState = MutableStateFlow(
        RepositoryRequestState(
            Status.PAUSED,
            listOf(null),
            ""
        )
    )

    val updateBikeRequestState = MutableStateFlow(
        RepositoryRequestState(
            Status.PAUSED,
            listOf(null),
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

    fun deleteBike(bike: Bike) {
        deleteBikeRequestState.value = RepositoryRequestState.loading()

        viewModelScope.launch {
            try {
                bikesRepository.delete(bike)
                deleteBikeRequestState.value = RepositoryRequestState.success(null)
            } catch (exception: Exception) {
                deleteBikeRequestState.value =
                    RepositoryRequestState.error(exception.message.toString())
            }
        }
    }

    fun update(bike: Bike) {
        updateBikeRequestState.value = RepositoryRequestState.loading()

        viewModelScope.launch {
            try {
                if (bike.isDefault) {
                    bikesRepository.setAllBikesAsNotDefault()
                }
                bikesRepository.update(bike)
                updateBikeRequestState.value = RepositoryRequestState.success(null)
            } catch (exception: Exception) {
                updateBikeRequestState.value =
                    RepositoryRequestState.error(exception.message.toString())
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