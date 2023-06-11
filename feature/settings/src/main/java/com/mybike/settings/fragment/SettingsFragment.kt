package com.mybike.settings.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mybike.common.BaseFragment
import com.mybike.common.TAG
import com.mybike.common.hideSoftKeyboard
import com.mybike.common.kmToMi
import com.mybike.common.miToKm
import com.mybike.data.model.Settings
import com.mybike.data.model.Status
import com.mybike.settings.R
import com.mybike.settings.viewmodel.SettingsViewModel
import com.mybike.settings.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SettingsFragment : BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {
    private val viewModel: SettingsViewModel by inject()
    override val title: String = "Settings"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        collectFlows()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getSettings()
    }

    override fun onPause() {
        viewModel.setSettings(
            Settings(
                units = binding.unitsSpinner.selectedItem as String,
                serviceReminderKm = getServiceReminder(),
                serviceRemindersEnabled = binding.serviceReminderSwitch.isChecked,
                defaultBikeName = binding.defaultBikeSpinner.selectedItem as String
            )
        )
        super.onPause()
    }

    private fun initUi() {
        binding.background.setOnClickListener {
            it.hideSoftKeyboard()
            viewModel.setSettings(
                Settings(
                    units = binding.unitsSpinner.selectedItem as String,
                    serviceReminderKm = getServiceReminder(),
                    serviceRemindersEnabled = binding.serviceReminderSwitch.isChecked,
                    defaultBikeName = binding.defaultBikeSpinner.selectedItem as String
                )
            )
        }
        viewModel.getBikeNames()
        binding.unitsSpinner.adapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.units_array, com.mybike.common.R.layout.custom_spinner
        ).also { adapter ->
            adapter.setDropDownViewResource(com.mybike.common.R.layout.custom_spinner_list_item)
        }
        binding.unitsSpinner.onItemSelectedListener = unitsSpinnerItemSelectedListener
        binding.serviceEditText.addTextChangedListener(inputValidator)
        binding.serviceReminderSwitch.setOnCheckedChangeListener { _, value ->
            if (value && binding.serviceEditText.text.isEmpty()) {
                showServiceInputError()
            } else {
                clearServiceInputError()
            }
        }
        updateServiceReminderUnits()
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.setSettingsRequestState.collect {
                    when (it.status) {
                        Status.PAUSED -> binding.loadingProgressBar.visibility = View.GONE
                        Status.LOADING -> binding.loadingProgressBar.visibility = View.VISIBLE
                        Status.SUCCESS -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            Log.d(TAG, "Settings saved!")
                        }

                        Status.ERROR -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            Log.e(TAG, "Could not save settings, error: ${it.message}")
                        }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getSettingsRequestState.collect {
                    when (it.status) {
                        Status.PAUSED -> binding.loadingProgressBar.visibility = View.GONE
                        Status.LOADING -> binding.loadingProgressBar.visibility = View.VISIBLE
                        Status.SUCCESS -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            it.data?.let { settings -> setUiFromSettings(settings) }
                            Log.d(TAG, "Settings fetched: ${it.data}")
                        }

                        Status.ERROR -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            Log.e(TAG, "Could not fetch settings, error: ${it.message}")
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
    }

    private fun getServiceReminder() =
        if (binding.serviceReminderSwitch.isChecked && binding.serviceEditText.text.isNotEmpty()) {
            if (binding.unitsSpinner.selectedItemPosition == 0) binding.serviceEditText.text.toString().toInt()
            else binding.serviceEditText.text.toString().toInt().miToKm()
        }
        else 0

    private fun setUiFromSettings(settings: Settings) {
        if (settings.units == "Metric") {
            binding.unitsSpinner.setSelection(0)
        } else {
            binding.unitsSpinner.setSelection(1)
        }
        binding.serviceEditText.setText(settings.serviceReminderKm.toString())
        binding.serviceReminderSwitch.isChecked = settings.serviceRemindersEnabled
        binding.defaultBikeSpinner.setSelection(
            (binding.defaultBikeSpinner.adapter as? ArrayAdapter<String>)?.getPosition(
                settings.defaultBikeName
            ) ?: 0
        )
    }

    private fun setBikeSpinner(bikeNames: List<String>) {
        binding.defaultBikeSpinner.adapter = ArrayAdapter(
            requireContext(),
            com.mybike.common.R.layout.custom_spinner,
            bikeNames.ifEmpty { listOf("No bikes added yet") }
        ).also { adapter ->
            adapter.setDropDownViewResource(com.mybike.common.R.layout.custom_spinner_list_item)
        }
    }

    private val unitsSpinnerItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            updateServiceReminderUnits()
            if (position == 0) convertServiceReminderFromMiToKm() else convertServiceReminderFromKmToMi()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            Log.d(TAG, "Nothing selected")
        }
    }

    private val inputValidator = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            if (binding.serviceEditText.text.isEmpty() && binding.serviceReminderSwitch.isChecked) {
                showServiceInputError()
            } else {
                clearServiceInputError()
            }
        }
    }

    private fun updateServiceReminderUnits() {
        if ((binding.unitsSpinner.selectedItem as String) == "Imperial") {
            binding.serviceReminderUnitLabel.text = getString(com.mybike.common.R.string.miles)
        } else {
            binding.serviceReminderUnitLabel.text = getString(com.mybike.common.R.string.kilometers)
        }
    }

    private fun convertServiceReminderFromKmToMi() {
        if (binding.serviceEditText.text.isNotEmpty()) {
            binding.serviceEditText.setText(
                binding.serviceEditText.text.toString().toInt().kmToMi().toString()
            )
        }
    }

    private fun convertServiceReminderFromMiToKm() {
        if (binding.serviceEditText.text.isNotEmpty()) {
            binding.serviceEditText.setText(
                binding.serviceEditText.text.toString().toInt().miToKm().toString()
            )
        }
    }

    private fun showServiceInputError() {
        binding.serviceInputError.isVisible = true
        binding.serviceEditText.setBackgroundResource(com.mybike.common.R.drawable.edit_text_input_error_bg)
    }

    private fun clearServiceInputError() {
        binding.serviceInputError.isVisible = false
        binding.serviceEditText.setBackgroundResource(com.mybike.common.R.drawable.edit_text_input_bg)
    }
}