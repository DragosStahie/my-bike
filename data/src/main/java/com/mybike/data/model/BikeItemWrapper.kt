package com.mybike.data.model


data class BikeItemWrapper(
    val bike: Bike,
    val noRides: Int,
    val totalDistanceKm: Int
)
