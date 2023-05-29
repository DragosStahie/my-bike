package com.mybike.startup


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.mybike.common.BaseFragment
import com.mybike.navigation.NavigationFlow
import com.mybike.navigation.Navigator
import com.mybike.startup.databinding.FragmentSplashBinding
import org.koin.android.ext.android.inject


class SplashFragment : BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate) {
    private val navigator : Navigator by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        testNav()
    }

    private fun testNav() {
        Handler(Looper.getMainLooper()).postDelayed(
            { navigator.navigateToFlow(NavigationFlow.BikesFlow) },
            2000
        )
    }
}