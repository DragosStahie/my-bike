package com.mybike.navigation

sealed class NavigationFlow {
    object StartupFlow : NavigationFlow()
    object BikesFlow : NavigationFlow()
    object RidesFlow : NavigationFlow()
}
