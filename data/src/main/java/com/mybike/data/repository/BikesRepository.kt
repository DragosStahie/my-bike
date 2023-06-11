package com.mybike.data.repository

import com.mybike.data.access.BikesDao
import com.mybike.data.model.Bike
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.transform

class BikesRepository(
    private val bikesDao: BikesDao,
) {
    fun getAllBikes(): Flow<List<Bike>> = bikesDao.getAll()

    suspend fun getBikeByName(bikeName: String): Bike = bikesDao.getByName(bikeName).first()


    fun getAllBikeNames(): Flow<List<String>> = bikesDao.getAll()
        .transform { bikes ->
            val bikeNamesList = mutableListOf<String>()
            bikes.forEach { bike ->
                bikeNamesList.add(bike.name)
            }
            emit(bikeNamesList)
        }

    suspend fun insert(bike: Bike) = bikesDao.insert(bike)

    suspend fun delete(bike: Bike) = bikesDao.delete(bike)

    suspend fun update(bike: Bike) = bikesDao.update(bike)

    suspend fun setAllBikesAsNotDefault() = bikesDao.setAllBikesAsNotDefault()

    suspend fun updateDefaultBike(bikeName: String) = bikesDao.setDefaultBike(bikeName)

    suspend fun getDefaultBikeName() = bikesDao.getDefaultBikeName().first().ifEmpty { listOf("") }[0]
}