package com.mybike.data.access

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mybike.data.model.Bike
import kotlinx.coroutines.flow.Flow

@Dao
interface BikesDao {
    @Query("SELECT * FROM bikes")
    fun getAll(): Flow<List<Bike>>

    @Query("SELECT * FROM bikes WHERE bike_name = :bikeName")
    fun getByName(bikeName: String): Flow<Bike>

    @Insert
    suspend fun insert(bike: Bike)

    @Delete
    suspend fun delete(bike: Bike)

    @Update
    suspend fun update(bike: Bike)

    @Query("UPDATE bikes SET is_default = 0")
    suspend fun setAllBikesAsNotDefault()

    @Query("UPDATE bikes SET is_default = 1 WHERE bike_name = :bikeName")
    suspend fun setDefaultBike(bikeName: String)

    @Query("SELECT bike_name FROM bikes WHERE is_default = 1")
    fun getDefaultBikeName(): Flow<List<String>>
}