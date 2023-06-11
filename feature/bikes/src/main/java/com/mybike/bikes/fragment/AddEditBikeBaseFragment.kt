package com.mybike.bikes.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.mybike.bikes.R
import com.mybike.bikes.databinding.FragmentAddEditBikeBinding
import com.mybike.common.enums.BikeColor
import com.mybike.bikes.scrollview.OnPositionChangedListener
import com.mybike.common.enums.BikeType
import com.mybike.common.BaseFragment
import com.mybike.common.TAG
import com.mybike.common.hideSoftKeyboard

abstract class AddEditBikeBaseFragment :
    BaseFragment<FragmentAddEditBikeBinding>(FragmentAddEditBikeBinding::inflate),
    AdapterView.OnItemSelectedListener, OnPositionChangedListener {

    protected var isInputValid = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        binding.colorsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.white_color_selector -> setBikeColor(com.mybike.common.R.color.bike_white)
                R.id.green_color_selector -> setBikeColor(com.mybike.common.R.color.bike_green)
                R.id.red_color_selector -> setBikeColor(com.mybike.common.R.color.bike_red)
                R.id.yellow_color_selector -> setBikeColor(com.mybike.common.R.color.bike_yellow)
                R.id.blue_color_selector -> setBikeColor(com.mybike.common.R.color.bike_blue)
                R.id.orange_color_selector -> setBikeColor(com.mybike.common.R.color.bike_orange)
                R.id.cyan_color_selector -> setBikeColor(com.mybike.common.R.color.bike_cyan)
                R.id.beige_color_selector -> setBikeColor(com.mybike.common.R.color.bike_beige)
                R.id.purple_color_selector -> setBikeColor(com.mybike.common.R.color.bike_purple)
                R.id.dark_blue_color_selector -> setBikeColor(com.mybike.common.R.color.bike_dark_blue)
                R.id.brown_color_selector -> setBikeColor(com.mybike.common.R.color.bike_brown)
            }
        }
        binding.dotsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.first_dot -> binding.bikesScrollView.updateCurrentPosition(BikeType.MOUNTAIN_BIKE)
                R.id.second_dot -> binding.bikesScrollView.updateCurrentPosition(BikeType.ROAD_BIKE)
                R.id.third_dot -> binding.bikesScrollView.updateCurrentPosition(BikeType.ELECTRIC_BIKE)
                R.id.fourth_dot -> binding.bikesScrollView.updateCurrentPosition(BikeType.HYBRID_BIKE)
            }
        }
        binding.wheelSizeSpinner.adapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.wheel_sizes_array, com.mybike.common.R.layout.custom_spinner
        ).also { adapter ->
            adapter.setDropDownViewResource(com.mybike.common.R.layout.custom_spinner_list_item)
        }
        binding.wheelSizeSpinner.onItemSelectedListener = this
        binding.bikeNameEditText.addTextChangedListener(inputValidator)
        binding.serviceIntervalEditText.addTextChangedListener(inputValidator)
        binding.bikesScrollView.onPositionChangedListener = this
        binding.background.setOnClickListener {
            it.hideSoftKeyboard()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (resources.getStringArray(R.array.wheel_sizes_array)[position].contains("29")) {
            setBikeBigWheels()
        } else {
            setBikeSmallWheels()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Log.d(TAG, "Nothing selected")
    }

    protected fun extractColorOrdinal(colorId: Int) = when (colorId) {
        R.id.white_color_selector -> BikeColor.WHITE
        R.id.green_color_selector -> BikeColor.GREEN
        R.id.red_color_selector -> BikeColor.RED
        R.id.yellow_color_selector -> BikeColor.YELLOW
        R.id.blue_color_selector -> BikeColor.BLUE
        R.id.orange_color_selector -> BikeColor.ORANGE
        R.id.cyan_color_selector -> BikeColor.CYAN
        R.id.beige_color_selector -> BikeColor.BEIGE
        R.id.purple_color_selector -> BikeColor.PURPLE
        R.id.dark_blue_color_selector -> BikeColor.DARK_BLUE
        R.id.brown_color_selector -> BikeColor.BROWN
        else -> BikeColor.WHITE
    }.ordinal

    protected fun extractBikeTypeOrdinal(bikeDotId: Int) = when (bikeDotId) {
        R.id.first_dot -> BikeType.MOUNTAIN_BIKE
        R.id.second_dot -> BikeType.ROAD_BIKE
        R.id.third_dot -> BikeType.ELECTRIC_BIKE
        R.id.fourth_dot -> BikeType.HYBRID_BIKE
        else -> BikeType.MOUNTAIN_BIKE
    }.ordinal

    private fun setBikeColor(colorId: Int) {
        binding.mtbFrame.setColorFilter(ContextCompat.getColor(requireContext(), colorId))
        binding.roadFrame.setColorFilter(ContextCompat.getColor(requireContext(), colorId))
        binding.electricFrame.setColorFilter(ContextCompat.getColor(requireContext(), colorId))
        binding.hybridFrame.setColorFilter(ContextCompat.getColor(requireContext(), colorId))
    }

    private fun setBikeBigWheels() {
        binding.mtbWheels.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(), com.mybike.common.R.drawable.bike_mtb_big_wheels
            )
        )
        binding.roadWheels.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(), com.mybike.common.R.drawable.bike_roadbike_big_wheels
            )
        )
        binding.electricWheels.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(), com.mybike.common.R.drawable.bike_electric_big_wheels
            )
        )
        binding.hybridWheels.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(), com.mybike.common.R.drawable.bike_hybrid_big_wheels
            )
        )
    }

    private fun setBikeSmallWheels() {
        binding.mtbWheels.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(), com.mybike.common.R.drawable.bike_mtb_small_wheels
            )
        )
        binding.roadWheels.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(), com.mybike.common.R.drawable.bike_roadbike_small_wheels
            )
        )
        binding.electricWheels.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(), com.mybike.common.R.drawable.bike_electric_small_wheels
            )
        )
        binding.hybridWheels.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(), com.mybike.common.R.drawable.bike_hybrid_small_wheels
            )
        )
    }

    private val inputValidator = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            if (binding.bikeNameEditText.text.isNotEmpty()) {
                clearBikeNameError()
            }
            if (binding.serviceIntervalEditText.text.isNotEmpty()) {
                clearServiceIntervalError()
            }
            handleButtonState(
                binding.bikeNameEditText.text.isNotEmpty() && binding.serviceIntervalEditText.text.isNotEmpty()
            )
        }
    }

    protected fun showErrorWarnings() {
        if (binding.bikeNameEditText.text.isEmpty()) {
            showBikeNameError()
        }
        if (binding.serviceIntervalEditText.text.isEmpty()) {
            showServiceIntervalError()
        }
    }

    private fun showBikeNameError() {
        binding.bikeNameRequiredFieldError.visibility = View.VISIBLE
        binding.bikeNameEditText.setBackgroundResource(com.mybike.common.R.drawable.edit_text_input_error_bg)
    }

    private fun showServiceIntervalError() {
        binding.serviceIntervalRequiredFieldError.visibility = View.VISIBLE
        binding.serviceIntervalEditText.setBackgroundResource(com.mybike.common.R.drawable.edit_text_input_error_bg)
    }

    private fun clearBikeNameError() {
        binding.bikeNameRequiredFieldError.visibility = View.INVISIBLE
        binding.bikeNameUniqueFieldError.visibility = View.INVISIBLE
        binding.bikeNameEditText.setBackgroundResource(com.mybike.common.R.drawable.edit_text_input_bg)
    }

    private fun clearServiceIntervalError() {
        binding.serviceIntervalRequiredFieldError.visibility = View.INVISIBLE
        binding.serviceIntervalEditText.setBackgroundResource(com.mybike.common.R.drawable.edit_text_input_bg)
    }

    private fun handleButtonState(isInputValid: Boolean) {
        this.isInputValid = isInputValid
        val colors = if (isInputValid) {
            Pair(
            ContextCompat.getColor(requireContext(), com.mybike.common.R.color.app_white),
            ContextCompat.getColor(requireContext(), com.mybike.common.R.color.app_blue)
            )
        } else {
            Pair(
            ContextCompat.getColor(requireContext(), com.mybike.common.R.color.app_light_gray),
            ContextCompat.getColor(requireContext(), com.mybike.common.R.color.app_blue_transparent)
            )
        }
        binding.addEditBikeButtonText.setTextColor(colors.first)
        binding.addEditBikeButton.background.setTint(colors.second)
    }

    override fun onPositionChanged(position: Int) {
        binding.dotsRadioGroup.check(binding.dotsRadioGroup.getChildAt(position).id)
    }
}