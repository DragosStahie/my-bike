package com.mybike.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "rides", foreignKeys = [ForeignKey(
        entity = Bike::class,
        parentColumns = arrayOf("bike_name"),
        childColumns = arrayOf("bike_name"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Ride(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val rideId: Int,
    @ColumnInfo(name = "ride_name")
    val rideName: String,
    @ColumnInfo(name = "bike_name")
    val bikeName: String,
    @ColumnInfo(name = "distance")
    val distanceKm: Int,
    @ColumnInfo(name = "duration")
    val duration: Int,
    @ColumnInfo(name = "date")
    val date: Long
) : Serializable
