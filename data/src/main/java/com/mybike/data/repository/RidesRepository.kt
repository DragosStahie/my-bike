package com.mybike.data.repository

import com.mybike.data.access.RidesDao
import com.mybike.data.model.Ride
import com.mybike.data.model.RideStatistics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class RidesRepository(
    private val ridesDao: RidesDao,
) {
    fun getAllRides(): Flow<List<Ride>> = ridesDao.getAll()

    fun getRideById(rideId: Int): Flow<Ride> = ridesDao.getById(rideId)

    fun getRidesForBike(bikeName: String): Flow<List<Ride>> = ridesDao.getAllForBike(bikeName)

    suspend fun insert(ride: Ride) = ridesDao.insert(ride)

    suspend fun delete(ride: Ride) = ridesDao.delete(ride)

    suspend fun update(ride: Ride) = ridesDao.update(ride)

    fun getRideStatistics(): Flow<RideStatistics> = combine(
        ridesDao.getRideStatisticsForBikeType(0),
        ridesDao.getRideStatisticsForBikeType(1),
        ridesDao.getRideStatisticsForBikeType(2),
        ridesDao.getRideStatisticsForBikeType(3)
    ) { mtbDistance, roadDistance, electricDistance, hybridDistance ->
        return@combine RideStatistics(
            mtbDistance,
            roadDistance,
            electricDistance,
            hybridDistance
        )
    }

}