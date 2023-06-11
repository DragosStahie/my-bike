package com.mybike.data.model


data class RideStatistics(
    val mtbTotalDistanceKm: Int,
    val roadTotalDistanceKm: Int,
    val electricTotalDistanceKm: Int,
    val hybridTotalDistanceKm: Int
) {
    fun getTotalDistance() =
        mtbTotalDistanceKm + roadTotalDistanceKm + electricTotalDistanceKm + hybridTotalDistanceKm
}
