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
        }
    }

    fun navigateLeft() {
        when (currentFlow) {
            is NavigationFlow.RidesFlow -> navigateToFlow(NavigationFlow.BikesFlow)
            else -> {}
        }
    }

    fun navigateRight() {
        when (currentFlow) {
            is NavigationFlow.BikesFlow -> navigateToFlow(NavigationFlow.RidesFlow)
            else -> {}
        }
    }
}