package com.mybike.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mybike.data.model.Example
import com.mybike.data.access.ExampleDao

@Database(entities = [Example::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getExampleDao() : ExampleDao
}