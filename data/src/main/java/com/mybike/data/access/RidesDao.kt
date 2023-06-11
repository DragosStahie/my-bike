package com.mybike.data.access

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mybike.data.model.Ride
import kotlinx.coroutines.flow.Flow

@Dao
interface RidesDao {
    @Query("SELECT * FROM rides ORDER BY date DESC")
    fun getAll(): Flow<List<Ride>>

    @Query("SELECT * FROM rides WHERE id = :rideId")
    fun getById(rideId: Int): Flow<Ride>

    @Query("SELECT * FROM rides WHERE bike_name = :bikeName")
    fun getAllForBike(bikeName: String): Flow<List<Ride>>

    @Insert
    suspend fun insert(ride: Ride)

    @Delete
    suspend fun delete(ride: Ride)

    @Update
    suspend fun update(ride: Ride)

    @Query("SELECT SUM(distance) FROM rides, bikes WHERE rides.bike_name = bikes.bike_name AND bike_type = :bikeType")
    fun getRideStatisticsForBikeType(bikeType: Int): Flow<Int>

}