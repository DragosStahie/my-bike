package com.mybike.bikes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybike.data.repository.ExampleRepository
import kotlinx.coroutines.launch

class BikesViewModel(private val exampleRepository: ExampleRepository) : ViewModel() {

    fun testDataStore() {
        viewModelScope.launch {
            exampleRepository.testDataStore()
        }
    }
}