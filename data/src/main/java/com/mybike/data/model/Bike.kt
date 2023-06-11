package com.mybike.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "bikes")
data class Bike(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "bike_name")
    val name: String,
    @ColumnInfo(name = "wheel_size")
    val wheelSize: String,
    @ColumnInfo(name = "service_interval")
    val serviceIntervalKm: Int,
    @ColumnInfo(name = "next_service_at")
    val nextServiceAtKm: Int,
    @ColumnInfo(name = "bike_color")
    val bikeColor: Int,
    @ColumnInfo(name = "bike_type")
    val bikeType: Int,
    @ColumnInfo(name = "is_default")
    val isDefault: Boolean
) : Serializable
