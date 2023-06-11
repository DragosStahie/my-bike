package com.mybike.bikes.viewmodel


import com.mybike.data.repository.BikesRepository
import com.mybike.data.repository.SettingsRepository

class EditBikesViewModel(bikesRepository: BikesRepository, settingsRepository: SettingsRepository) :
    BaseBikesViewModel(bikesRepository, settingsRepository)