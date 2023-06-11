package com.mybike.bikes.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.mybike.bikes.R
import com.mybike.bikes.viewmodel.EditBikesViewModel
import com.mybike.common.TAG
import com.mybike.common.kmToMi
import com.mybike.common.miToKm
import com.mybike.common.showToast
import com.mybike.data.model.Bike
import com.mybike.data.model.RepositoryRequestState
import com.mybike.data.model.Status
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class EditBikesFragment : AddEditBikeBaseFragment() {

    private val viewModel: EditBikesViewModel by inject()
    private val args: EditBikesFragmentArgs by navArgs()
    override val title: String = "Edit Bike"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        collectFlows()
    }

    private fun initUi() {
        binding.addEditBikeButtonText.text = getString(com.mybike.common.R.string.save)
        binding.addEditBikeButton.setOnClickListener {
            if (isInputValid) {
                val newServiceIntervalKm =
                    if (binding.serviceIntervalUnitLabel.text.toString() == "KM")
                        binding.serviceIntervalEditText.text.toString().toInt()
                    else binding.serviceIntervalEditText.text.toString().toInt().miToKm()

                val newNextServiceAtKm =
                    args.bike.nextServiceAtKm + newServiceIntervalKm - args.bike.serviceIntervalKm

                viewModel.update(
                    Bike(
                        binding.bikeNameEditText.text.toString(),
                        binding.wheelSizeSpinner.selectedItem as String,
                        newServiceIntervalKm,
                        newNextServiceAtKm,
                        extractColorOrdinal(binding.colorsRadioGroup.checkedRadioButtonId),
                        extractBikeTypeOrdinal(binding.dotsRadioGroup.checkedRadioButtonId),
                        binding.defaultBikeSwitch.isChecked
                    )
                )
            } else {
                showErrorWarnings()
            }
        }
        setupBike(args.bike)
        viewModel.getPreferredUnits()
    }

    private fun setupBike(bike: Bike) {
        (binding.colorsRadioGroup.getChildAt(bike.bikeColor) as RadioButton).isChecked = true
        (binding.dotsRadioGroup.getChildAt(bike.bikeType) as RadioButton).isChecked = true
        binding.bikeNameEditText.setText(bike.name)
        binding.bikeNameEditText.isEnabled = false
        binding.bikeNameEditText.setTextColor(
            ContextCompat.getColor(
                requireContext(), com.mybike.common.R.color.app_light_gray
            )
        )
        binding.wheelSizeSpinner.setSelection(
            resources.getStringArray(R.array.wheel_sizes_array).indexOf(bike.wheelSize)
        )
        binding.serviceIntervalEditText.setText(
            if (binding.serviceIntervalUnitLabel.text.toString() == "KM") bike.serviceIntervalKm.toString()
            else bike.serviceIntervalKm.kmToMi().toString()
        )
        binding.defaultBikeSwitch.isChecked = bike.isDefault
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateBikeRequestState.collect {
                    when (it.status) {
                        Status.PAUSED -> binding.loadingProgressBar.visibility = View.GONE
                        Status.LOADING -> binding.loadingProgressBar.visibility = View.VISIBLE
                        Status.SUCCESS -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            requireActivity().let { activity ->
                                activity.showToast("New bike details saved!")
                                activity.onBackPressedDispatcher.onBackPressed()
                            }
                        }

                        Status.ERROR -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            Log.e(TAG, "Could not update bike, error: ${it.message}")
                            context?.showToast("New bike details could not be saved!")
                        }
                    }
                    viewModel.updateBikeRequestState.value =
                        RepositoryRequestState(Status.PAUSED, null, "")
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
                                    if (preferredUnits == "Metric") getString(com.mybike.common.R.string.kilometers)
                                    else getString(com.mybike.common.R.string.miles)
                                val serviceIntervalKm =
                                    binding.serviceIntervalEditText.text.toString()
                                if (serviceIntervalKm.isNotEmpty() && binding.serviceIntervalUnitLabel.text.toString() == "MI") {
                                    binding.serviceIntervalEditText.setText(
                                        serviceIntervalKm.toInt().kmToMi().toString()
                                    )
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