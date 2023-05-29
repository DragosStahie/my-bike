package com.mybike

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.mybike.common.setNextMenuItemChecked
import com.mybike.common.setPreviousMenuItemChecked
import com.mybike.navigation.NavigationFlow
import com.mybike.navigation.Navigator
import com.mybike.databinding.ActivityMainBinding
import org.koin.android.ext.android.inject
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    private val navigator: Navigator by inject()
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val minDistance = 300
    private var startX = 0f
    private var endX = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigator()
        setupTopBar()
        setupBottomBar()
        setupNavigatorDestinationListener()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.actionMasked) {
            ACTION_DOWN -> startX = event.x
            ACTION_UP -> {
                endX = event.x
                if (abs(startX - endX) > minDistance) {
                    if (startX < endX) {
                        navigator.navigateLeft()
                        binding.bottomNavigation.setPreviousMenuItemChecked()
                    } else {
                        navigator.navigateRight()
                        binding.bottomNavigation.setNextMenuItemChecked()
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun setupNavigator() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navigator.navController = navHostFragment.navController
    }

    private fun setupTopBar() {
        binding.appToolbar.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupBottomBar() {
        binding.bottomNavigation.menu.apply {
            findItem(com.mybike.common.R.id.bottom_nav_bikes).setOnMenuItemClickListener {
                navigator.navigateToFlow(NavigationFlow.BikesFlow)
                it.isChecked = true
                true
            }
            findItem(com.mybike.common.R.id.bottom_nav_rides).setOnMenuItemClickListener {
                navigator.navigateToFlow(NavigationFlow.RidesFlow)
                it.isChecked = true
                true
            }
        }
    }

    private fun setupNavigatorDestinationListener() {
        navigator.navController.addOnDestinationChangedListener { _, destination: NavDestination, _ ->
            when (destination.id) {
                com.mybike.startup.R.id.splash_fragment -> setAppBarsVisibility(View.GONE)
                else -> setAppBarsVisibility(View.VISIBLE)
            }
        }
    }

    private fun setAppBarsVisibility(visibility: Int) {
        binding.bottomNavigation.visibility = visibility
        binding.appToolbar.topBar.visibility = visibility
    }
}