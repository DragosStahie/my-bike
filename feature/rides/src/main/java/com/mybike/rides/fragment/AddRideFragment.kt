package com.mybike.rides.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mybike.common.TAG
import com.mybike.common.showToast
import com.mybike.data.model.Ride
import com.mybike.data.model.Status
import com.mybike.common.R
import com.mybike.common.getTime
import com.mybike.common.miToKm
import com.mybike.rides.viewmodel.AddRidesViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class AddRideFragment : AddEditRideBaseFragment() {
    private val viewModel: AddRidesViewModel by inject()
    override val title: String = "Add Ride"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        collectFlows()
    }

    private fun initUi() {
        binding.addEditRideButtonText.text = getString(R.string.add_ride)
        binding.addEditRideButton.setOnClickListener {
            if (isInputValid) {
                viewModel.insert(
                    Ride(
                        0,
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
        viewModel.getDefaultBikeName()
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.insertRideRequestState.collect {
                    when (it.status) {
                        Status.PAUSED -> binding.loadingProgressBar.visibility = View.GONE
                        Status.LOADING -> binding.loadingProgressBar.visibility = View.VISIBLE
                        Status.SUCCESS -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            requireActivity().let { activity ->
                                activity.showToast("Ride successfully added!")
                                activity.onBackPressedDispatcher.onBackPressed()
                            }
                        }

                        Status.ERROR -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            Log.e(TAG, "Could not insert ride, error: ${it.message}")
                            if (it.message?.contains("FOREIGN") == true) {
                                showBikeNameError()
                            } else {
                                requireActivity().showToast("Unexpected problem occurred!")
                            }
                        }
                    }
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
                                    if (preferredUnits == "Metric") getString(R.string.kilometers)
                                    else getString(R.string.miles)
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
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getDefaultBikeNameRequestState.collect {
                    when (it.status) {
                        Status.PAUSED -> binding.loadingProgressBar.visibility = View.GONE
                        Status.LOADING -> binding.loadingProgressBar.visibility = View.VISIBLE
                        Status.SUCCESS -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            it.data?.let { defaultBikeName ->
                                (binding.bikeNameSpinner.adapter as? ArrayAdapter<String>)?.getPosition(
                                    defaultBikeName
                                )?.let { bikeName ->
                                    binding.bikeNameSpinner.setSelection(bikeName)
                                }
                            }
                            Log.d(TAG, "Default bike fetched: ${it.data}")
                        }

                        Status.ERROR -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            Log.e(TAG, "Could not fetch default bike, error: ${it.message}")
                        }
                    }
                }
            }
        }
    }

    private fun showBikeNameError() {
        binding.bikeNameRequiredFieldError.visibility = View.VISIBLE
        binding.bikeNameSpinner.setBackgroundResource(R.drawable.edit_text_input_error_bg)
    }
}