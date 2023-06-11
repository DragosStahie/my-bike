package com.mybike.rides.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.mybike.common.TAG
import com.mybike.common.getDate
import com.mybike.common.getTime
import com.mybike.common.kmToMi
import com.mybike.common.miToKm
import com.mybike.common.showToast
import com.mybike.data.model.RepositoryRequestState
import com.mybike.data.model.Ride
import com.mybike.data.model.Status
import com.mybike.rides.R
import com.mybike.rides.viewmodel.EditRidesViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class EditRideFragment : AddEditRideBaseFragment() {
    private val viewModel: EditRidesViewModel by inject()
    private val args: EditRideFragmentArgs by navArgs()
    override val title: String = "Edit Ride"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        collectFlows()
    }

    private fun initUi() {
        binding.addEditRideButtonText.text = getString(com.mybike.common.R.string.save)
        binding.addEditRideButtonText.setOnClickListener {
            if (isInputValid) {
                viewModel.update(
                    Ride(
                        args.rideId,
                        binding.rideTitleEditText.text.toString(),
                        binding.bikeNameSpinner.selectedItem as String,
                        if (binding.distanceUnitLabel.text.toString() == "KM")
                            binding.distanceEditText.text.toString().toInt()
                        else binding.distanceEditText.text.toString().toInt().miToKm(),
                        getDuration(),
                        binding.dateInputTextView.text.toString().getTime()
                    )
                )
            } else {
                showErrorWarnings()
            }
        }
        viewModel.getBikeNames()
        viewModel.getPreferredUnits()
        viewModel.getRide(args.rideId)
    }

    private fun setupRideDetails(ride: Ride) {
        binding.rideTitleEditText.setText(ride.rideName)
        (binding.bikeNameSpinner.adapter as? ArrayAdapter<String>)?.getPosition(ride.bikeName)?.let {
            binding.bikeNameSpinner.setSelection(it)
        }
        binding.distanceEditText.setText(
            if (binding.distanceUnitLabel.text.toString() == "KM") ride.distanceKm.toString()
            else ride.distanceKm.kmToMi().toString()
        )
        binding.durationInputTextView.text =
            getString(R.string.duration_input_template, (ride.duration / 60), (ride.duration % 60))
        binding.dateInputTextView.text = ride.date.getDate()
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getRideRequestState.collect {
                    when (it.status) {
                        Status.PAUSED -> binding.loadingProgressBar.visibility = View.GONE
                        Status.LOADING -> binding.loadingProgressBar.visibility = View.VISIBLE
                        Status.SUCCESS -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            it.data?.let { ride ->
                                Log.d(TAG, "Ride: $ride")
                                setupRideDetails(ride)
                            }
                        }

                        Status.ERROR -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            Log.e(TAG, "Could not find ride, error: ${it.message}")
                            context?.showToast("Unexpected error! Ride cannot be edited!")
                        }
                    }
                    viewModel.updateRideRequestState.value =
                        RepositoryRequestState(Status.PAUSED, null, "")
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateRideRequestState.collect {
                    when (it.status) {
                        Status.PAUSED -> binding.loadingProgressBar.visibility = View.GONE
                        Status.LOADING -> binding.loadingProgressBar.visibility = View.VISIBLE
                        Status.SUCCESS -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            requireActivity().let { activity ->
                                activity.showToast("New ride details saved!")
                                activity.onBackPressedDispatcher.onBackPressed()
                            }
                        }

                        Status.ERROR -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            Log.e(TAG, "Could not update ride, error: ${it.message}")
                            context?.showToast("New ride details could not be saved!")
                        }
                    }
                    viewModel.updateRideRequestState.value =
                        RepositoryRequestState(Status.PAUSED, null, "")
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getBikeNamesRequestState.collect {
                    when (it.status) {
                        Status.PAUSED -> binding.loadingProgressBar.visibility = View.GONE
                        Status.LOADING -> binding.loadingProgressBar.visibility = View.VISIBLE
                        Status.SUCCESS -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            it.data?.let { bikeNames -> setBikeSpinner(bikeNames) }
                            Log.d(TAG, "Bike names fetched: ${it.data}")
                        }

                        Status.ERROR -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            Log.e(TAG, "Could not fetch bike names, error: ${it.message}")
                        }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getPreferredUnitsRequestState.collect {
                    when (it.status) {
                        Status.PAUSED -> binding.loadingProgressBar.visibility = View.GONE
                        Status.LOADING -> binding.loadingProgressBar.visibility = View.VISIBLE
                        Status.SUCCESS -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            it.data?.let { preferredUnits ->
                                binding.distanceUnitLabel.text =
                                    if (preferredUnits == "Metric") getString(com.mybike.common.R.string.kilometers)
                                    else getString(com.mybike.common.R.string.miles)
                                val rideDistanceKm = binding.distanceEditText.text.toString()
                                if (rideDistanceKm.isNotEmpty() && binding.distanceUnitLabel.text.toString() == "MI") {
                                    binding.distanceEditText.setText(rideDistanceKm.toInt().kmToMi().toString())
                                }
                            }
                            Log.d(TAG, "Units fetched: ${it.data}")
                        }

                        Status.ERROR -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            Log.e(TAG, "Could not fetch units, error: ${it.message}")
                        }
                    }
                }
            }
        }
    }
}