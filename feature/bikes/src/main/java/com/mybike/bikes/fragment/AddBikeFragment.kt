package com.mybike.bikes.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mybike.bikes.viewmodel.AddBikesViewModel
import com.mybike.common.R
import com.mybike.common.TAG
import com.mybike.common.miToKm
import com.mybike.common.showToast
import com.mybike.data.model.Bike
import com.mybike.data.model.Status
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class AddBikeFragment : AddEditBikeBaseFragment() {

    private val viewModel: AddBikesViewModel by inject()
    override val title: String = "Add Bike"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        collectFlows()
    }

    private fun initUi() {
        binding.addEditBikeButtonText.text = getString(R.string.add_bike)
        binding.addEditBikeButton.setOnClickListener {
            if (isInputValid) {
                val serviceInterval = if (binding.serviceIntervalUnitLabel.text.toString() == "KM")
                    binding.serviceIntervalEditText.text.toString().toInt()
                else binding.serviceIntervalEditText.text.toString().toInt().miToKm()
                viewModel.insert(
                    Bike(
                        binding.bikeNameEditText.text.toString(),
                        binding.wheelSizeSpinner.selectedItem as String,
                        serviceIntervalKm = serviceInterval,
                        nextServiceAtKm = serviceInterval,
                        extractColorOrdinal(binding.colorsRadioGroup.checkedRadioButtonId),
                        extractBikeTypeOrdinal(binding.dotsRadioGroup.checkedRadioButtonId),
                        binding.defaultBikeSwitch.isChecked
                    )
                )
            } else {
                showErrorWarnings()
            }
        }
        viewModel.getPreferredUnits()
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.insertBikeRequestState.collect {
                    when (it.status) {
                        Status.PAUSED -> binding.loadingProgressBar.visibility = View.GONE
                        Status.LOADING -> binding.loadingProgressBar.visibility = View.VISIBLE
                        Status.SUCCESS -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            requireActivity().let { activity ->
                                activity.showToast("Bike successfully added!")
                                activity.onBackPressedDispatcher.onBackPressed()
                            }
                        }

                        Status.ERROR -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            Log.e(TAG, "Could not insert bike, error: ${it.message}")
                            if (it.message?.contains("UNIQUE") == true) {
                                showUniqueNameError()
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
                viewModel.getPreferredUnitsRequestState.collect {
                    when (it.status) {
                        Status.PAUSED -> binding.loadingProgressBar.visibility = View.GONE
                        Status.LOADING -> binding.loadingProgressBar.visibility = View.VISIBLE
                        Status.SUCCESS -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            it.data?.let { preferredUnits ->
                                binding.serviceIntervalUnitLabel.text =
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
    }

    private fun showUniqueNameError() {
        binding.bikeNameUniqueFieldError.visibility = View.VISIBLE
        binding.bikeNameEditText.setBackgroundResource(R.drawable.edit_text_input_error_bg)
    }
}