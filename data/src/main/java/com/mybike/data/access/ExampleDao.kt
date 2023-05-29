package com.mybike.data.access

import androidx.room.Dao
import androidx.room.Query
import com.mybike.data.model.Example

@Dao
interface ExampleDao {
    @Query("SELECT * FROM example")
    fun getAll(): List<Example>
}