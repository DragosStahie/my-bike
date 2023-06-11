package com.mybike.data.model


data class Settings(
    val units: String = "Metric",
    val serviceReminderKm: Int = 100,
    val serviceRemindersEnabled: Boolean = true,
    var defaultBikeName: String = ""
)
