package com.mybike.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mybike.data.model.Bike
import com.mybike.data.access.BikesDao
import com.mybike.data.access.RidesDao
import com.mybike.data.model.Ride

@Database(entities = [Bike::class, Ride::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getBikesDao() : BikesDao
    abstract fun getRidesDao() : RidesDao
}