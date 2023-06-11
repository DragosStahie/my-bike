package com.mybike.navigation

import androidx.navigation.NavController

class Navigator {

    private var currentFlow: NavigationFlow? = null
    lateinit var navController: NavController

    fun navigateToFlow(navigationFlow: NavigationFlow) {
        currentFlow = navigationFlow
        when (navigationFlow) {
            is NavigationFlow.StartupFlow -> navController.navigate(AppNavGraphDirections.actionStartupFlow())
            is NavigationFlow.BikesFlow -> navController.navigate(AppNavGraphDirections.actionBikesFlow())
            is NavigationFlow.RidesFlow -> navController.navigate(AppNavGraphDirections.actionRidesFlow())
            is NavigationFlow.EditRideFlow -> navController.navigate(AppNavGraphDirections.actionEditRideFlow(navigationFlow.rideId))
            is NavigationFlow.SettingsFlow -> navController.navigate(AppNavGraphDirections.actionSettingsFlow())
        }
    }

    fun navigateLeft() {
        when (currentFlow) {
            is NavigationFlow.SettingsFlow -> navigateToFlow(NavigationFlow.RidesFlow)
            is NavigationFlow.RidesFlow -> navigateToFlow(NavigationFlow.BikesFlow)
            else -> {}
        }
    }

    fun navigateRight() {
        when (currentFlow) {
            is NavigationFlow.BikesFlow -> navigateToFlow(NavigationFlow.RidesFlow)
            is NavigationFlow.RidesFlow -> navigateToFlow(NavigationFlow.SettingsFlow)
            else -> {}
        }
    }
}