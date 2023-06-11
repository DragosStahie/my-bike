package com.mybike

import android.Manifest
import android.app.AlarmManager
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.mybike.bikes.fragment.BikeDetailsFragment
import com.mybike.common.TAG
import com.mybike.common.kmToMi
import com.mybike.common.miToKm
import com.mybike.data.model.Settings
import com.mybike.data.model.Status
import com.mybike.databinding.ActivityMainBinding
import com.mybike.databinding.NotificationConfrimationDialogBinding
import com.mybike.navigation.NavigationFlow
import com.mybike.navigation.Navigator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.Calendar


class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by inject()
    private val navigator: Navigator by inject()
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var currentSettings = Settings()
    private val serviceNotificationConfirmationDialog: Dialog by lazy { Dialog(this) }

    companion object {
        const val CHANNEL_ID = "NOTIFICATION_CHANNEL"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        grantNotificationPermission()
        createNotificationChannel()
        collectFlows()
        setupNavigator()
        setupNotificationBarDismissListener()
        setupTopBar()
        setupBottomBar()
        setupMoreOptionsMenuListeners()
        setupNavigatorDestinationListener()

        viewModel.getSettings()
        Handler(Looper.getMainLooper()).postDelayed(
            { viewModel.getServiceNotificationInfo() },
            2000
        )
    }

    override fun onStop() {
        scheduleNotifications()
        super.onStop()
    }

    private fun collectFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getServiceNotificationInfoRequestState.collect {
                    when (it.status) {
                        Status.PAUSED -> Log.d(TAG, "GetServiceNotificationInfo paused")
                        Status.LOADING -> Log.d(TAG, "GetServiceNotificationInfo loading")
                        Status.SUCCESS -> {
                            it.data?.let { serviceNotifiationInfo ->
                                Log.d(TAG, "Notification info: $serviceNotifiationInfo")
                                if (serviceNotifiationInfo.isNotEmpty()) {
                                    handleNotifications(serviceNotifiationInfo)
                                }
                            }
                        }

                        Status.ERROR -> {
                            Log.e(
                                TAG,
                                "Could not load notifications info list, error: ${it.message}"
                            )
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getSettingsRequestState.collect {
                    when (it.status) {
                        Status.PAUSED -> Log.d(TAG, "GetSettings paused")
                        Status.LOADING -> Log.d(TAG, "GetSettings loading")
                        Status.SUCCESS -> {
                            it.data?.let { settings ->
                                currentSettings = settings
                                updateNotificationUnits()
                            }
                            Log.d(TAG, "Settings fetched: ${it.data}")
                        }

                        Status.ERROR -> {
                            Log.e(TAG, "Could not fetch setttings, error: ${it.message}")
                        }
                    }
                }
            }
        }
    }

    private fun setupNavigator() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navigator.navController = navHostFragment.navController
    }

    private fun setupNotificationBarDismissListener() {
        binding.appToolbar.serviceNotificationView.setOnClickListener {
            showServiceNotificationConfirmationDialog()
        }
    }

    private fun setupTopBar() {
        binding.appToolbar.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.appToolbar.closeButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.appToolbar.addBikeButton.setOnClickListener {
            navigator.navController.navigate(com.mybike.bikes.R.id.add_bike_fragment)
        }
        binding.appToolbar.addRideButton.setOnClickListener {
            navigator.navController.navigate(com.mybike.rides.R.id.add_ride_fragment)
        }
        binding.appToolbar.moreButton.setOnClickListener {
            binding.bikeOptionsMenu.root.isVisible = !binding.bikeOptionsMenu.root.isVisible
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
            findItem(com.mybike.common.R.id.bottom_nav_settings).setOnMenuItemClickListener {
                if (navigator.navController.currentDestination?.id != com.mybike.settings.R.id.settings_fragment) {
                    navigator.navigateToFlow(NavigationFlow.SettingsFlow)
                    it.isChecked = true
                }
                true
            }
        }
    }

    private fun setupMoreOptionsMenuListeners() {
        binding.bikeOptionsMenu.bikeEditButton.setOnClickListener {
            val navHostFragment: Fragment? =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            (navHostFragment?.childFragmentManager?.fragments?.get(0) as? BikeDetailsFragment)?.onEditClicked()
            binding.bikeOptionsMenu.root.isVisible = false
        }

        binding.bikeOptionsMenu.bikeDeleteButton.setOnClickListener {
            val navHostFragment: Fragment? =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            (navHostFragment?.childFragmentManager?.fragments?.get(0) as? BikeDetailsFragment)?.onDeleteClicked()
            binding.bikeOptionsMenu.root.isVisible = false
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun showServiceNotificationConfirmationDialog() {
        val dialogBinding = NotificationConfrimationDialogBinding.inflate(LayoutInflater.from(this))
        serviceNotificationConfirmationDialog.setContentView(dialogBinding.root)

        dialogBinding.cancelButton.setOnClickListener {
            serviceNotificationConfirmationDialog.dismiss()
        }

        dialogBinding.okButton.setOnClickListener {
            val bikeName =
                binding.appToolbar.serviceNotificationView.text.toString().split(" service")[0]
            viewModel.resetBikeNextService(bikeName)
            serviceNotificationConfirmationDialog.dismiss()
            binding.appToolbar.serviceNotificationView.isVisible = false
            binding.appToolbar.closeNotificationButton.isVisible = false
        }

        serviceNotificationConfirmationDialog.show()
    }

    private fun handleNotifications(notificationInfoList: List<Pair<String, Int>>) {
        binding.appToolbar.serviceNotificationView.isVisible = false
        binding.appToolbar.closeNotificationButton.isVisible = false

        if (currentSettings.serviceRemindersEnabled) {
            val filteredNotifications: MutableList<Pair<String, Int>> =
                notificationInfoList.filter { info -> info.second < currentSettings.serviceReminderKm }
                    .toMutableList()

            if (filteredNotifications.isNotEmpty()) {
                showNotification(filteredNotifications.first())

                binding.appToolbar.closeNotificationButton.setOnClickListener {
                    binding.appToolbar.serviceNotificationView.isVisible = false
                    binding.appToolbar.closeNotificationButton.isVisible = false

                    filteredNotifications.removeFirst()
                    if (filteredNotifications.isNotEmpty()) {
                        showNotification(filteredNotifications.first())
                    }
                }
            }
        }
    }

    private fun showNotification(notificationInfo: Pair<String, Int>) {
        binding.appToolbar.serviceNotificationView.isVisible = true
        binding.appToolbar.closeNotificationButton.isVisible = true

        var currentDistanceUnits = "KM"
        var currentBikeDistanceToService = notificationInfo.second
        if (currentSettings.units == "Imperial") {
            currentDistanceUnits = "MI"
            currentBikeDistanceToService = notificationInfo.second.kmToMi()
        }

        binding.appToolbar.serviceNotificationView.text = getString(
            R.string.service_notification_template,
            notificationInfo.first,
            currentBikeDistanceToService,
            currentDistanceUnits
        )
    }

    private fun updateNotificationUnits() {
        if (binding.appToolbar.serviceNotificationView.isVisible) {
            val notificationInfo =
                binding.appToolbar.serviceNotificationView.text.toString().split(" service in ")
            var distanceValue = notificationInfo[1].dropLast(2).toInt()
            val distanceUnits = notificationInfo[1].takeLast(2)
            val currentDistanceUnits = if (currentSettings.units == "Metric") "KM" else "MI"

            if (distanceUnits != currentDistanceUnits) {
                distanceValue = if (distanceUnits == "KM") {
                    distanceValue.kmToMi()
                } else {
                    distanceValue.miToKm()
                }
                binding.appToolbar.serviceNotificationView.text = getString(
                    R.string.service_notification_template,
                    notificationInfo[0],
                    distanceValue,
                    currentDistanceUnits
                )
            }
        }
    }

    private fun grantNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {}.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    private fun createNotificationChannel() {
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
    }

    private fun scheduleNotifications() {
        lifecycleScope.launch {
            with(viewModel.getServiceNotificationInfoRequestState.first()) {
                if (status == Status.SUCCESS && currentSettings.serviceRemindersEnabled) {
                    val filteredNotifications: MutableList<Pair<String, Int>>? =
                        data?.filter { info -> info.second < currentSettings.serviceReminderKm }
                            ?.toMutableList()

                    filteredNotifications?.forEach { notificationInfo ->
                        var currentDistanceUnits = "KM"
                        var currentBikeDistanceToService = notificationInfo.second
                        if (currentSettings.units == "Imperial") {
                            currentDistanceUnits = "MI"
                            currentBikeDistanceToService = notificationInfo.second.kmToMi()
                        }

                        val intent =
                            Intent(applicationContext, NotificationBroadcastReceiver::class.java)
                        intent.putExtra("bikeName", notificationInfo.first)
                        intent.putExtra("service", currentBikeDistanceToService)
                        intent.putExtra("units", currentDistanceUnits)

                        val pendingIntent = PendingIntent.getBroadcast(
                            applicationContext,
                            1,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )

                        val calendar = Calendar.getInstance()
                        calendar.add(Calendar.MINUTE, 2)

                        (getSystemService(ALARM_SERVICE) as AlarmManager).set(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                    }
                }
            }
        }
    }

    private fun setupNavigatorDestinationListener() {
        navigator.navController.addOnDestinationChangedListener { _, destination: NavDestination, _ ->
            when (destination.id) {
                com.mybike.startup.R.id.splash_fragment -> setupAppBarsForSplashFragment()
                com.mybike.bikes.R.id.add_bike_fragment, com.mybike.bikes.R.id.edit_bike_fragment -> setupAppBarsForAddEditBikesFragment()
                com.mybike.rides.R.id.add_ride_fragment, com.mybike.rides.R.id.edit_ride_fragment -> setupAppBarsForAddEditRidesFragment()
                com.mybike.bikes.R.id.bike_details_fragment -> setupAppBarsForBikeDetailsFragment()
                com.mybike.bikes.R.id.bikes_fragment -> setupAppBarsForBikesFragment()
                com.mybike.rides.R.id.rides_fragment -> setupAppBarsForRidesFragment()
                else -> resetAppBarsToDefault()
            }
        }
    }

    private fun setupAppBarsForSplashFragment() {
        binding.bottomNavigation.visibility = View.GONE
        binding.appToolbar.topBar.visibility = View.GONE
    }

    private fun setupAppBarsForAddEditBikesFragment() {
        binding.appToolbar.topBar.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                com.mybike.common.R.color.app_black
            )
        )
        binding.bottomNavigation.visibility = View.GONE
        binding.appToolbar.topBar.visibility = View.VISIBLE
        binding.appToolbar.closeButton.visibility = View.VISIBLE
        binding.appToolbar.addBikeButton.visibility = View.GONE
        binding.appToolbar.addRideButton.visibility = View.GONE
        binding.appToolbar.moreButton.visibility = View.GONE
        binding.appToolbar.backButton.visibility = View.GONE
    }

    private fun setupAppBarsForAddEditRidesFragment() {
        setupAppBarsForAddEditBikesFragment()
        binding.appToolbar.topBar.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                com.mybike.common.R.color.app_darker_blue
            )
        )
    }

    private fun setupAppBarsForBikesFragment() {
        binding.appToolbar.topBar.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                com.mybike.common.R.color.app_black
            )
        )
        binding.bottomNavigation.visibility = View.VISIBLE
        binding.appToolbar.topBar.visibility = View.VISIBLE
        binding.appToolbar.closeButton.visibility = View.GONE
        binding.appToolbar.addBikeButton.visibility = View.VISIBLE
        binding.appToolbar.addRideButton.visibility = View.GONE
        binding.appToolbar.moreButton.visibility = View.GONE
        binding.appToolbar.backButton.visibility = View.GONE
    }

    private fun setupAppBarsForRidesFragment() {
        binding.appToolbar.topBar.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                com.mybike.common.R.color.app_black
            )
        )
        binding.bottomNavigation.visibility = View.VISIBLE
        binding.appToolbar.topBar.visibility = View.VISIBLE
        binding.appToolbar.closeButton.visibility = View.GONE
        binding.appToolbar.addBikeButton.visibility = View.GONE
        binding.appToolbar.addRideButton.visibility = View.VISIBLE
        binding.appToolbar.moreButton.visibility = View.GONE
        binding.appToolbar.backButton.visibility = View.GONE
    }

    private fun setupAppBarsForBikeDetailsFragment() {
        binding.appToolbar.topBar.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                com.mybike.common.R.color.app_black
            )
        )
        binding.bottomNavigation.visibility = View.GONE
        binding.appToolbar.topBar.visibility = View.VISIBLE
        binding.appToolbar.closeButton.visibility = View.GONE
        binding.appToolbar.addBikeButton.visibility = View.GONE
        binding.appToolbar.addRideButton.visibility = View.GONE
        binding.appToolbar.moreButton.visibility = View.VISIBLE
        binding.appToolbar.backButton.visibility = View.VISIBLE
    }

    private fun resetAppBarsToDefault() {
        binding.appToolbar.topBar.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                com.mybike.common.R.color.app_black
            )
        )
        binding.bottomNavigation.visibility = View.VISIBLE
        binding.appToolbar.topBar.visibility = View.VISIBLE
        binding.appToolbar.closeButton.visibility = View.GONE
        binding.appToolbar.addBikeButton.visibility = View.GONE
        binding.appToolbar.addRideButton.visibility = View.GONE
        binding.appToolbar.moreButton.visibility = View.GONE
        binding.appToolbar.backButton.visibility = View.GONE
    }
}