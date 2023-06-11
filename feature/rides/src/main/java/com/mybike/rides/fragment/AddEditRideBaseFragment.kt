package com.mybike.rides.fragment

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.mybike.common.BaseFragment
import com.mybike.common.TAG
import com.mybike.common.hideSoftKeyboard
import com.mybike.rides.R
import com.mybike.rides.databinding.DatePickerDialogBinding
import com.mybike.rides.databinding.DurationDialogBinding
import com.mybike.rides.databinding.FragmentAddEditRideBinding
import java.time.YearMonth
import java.time.ZoneId
import java.util.Date

abstract class AddEditRideBaseFragment :
    BaseFragment<FragmentAddEditRideBinding>(FragmentAddEditRideBinding::inflate) {

    private val durationDialog: Dialog by lazy { Dialog(requireContext()) }
    private val datePickerDialog: Dialog by lazy { Dialog(requireContext()) }
    private val datePickerDialogBinding: DatePickerDialogBinding by lazy {
        DatePickerDialogBinding.inflate(
            LayoutInflater.from(context)
        )
    }

    protected var isInputValid = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    protected fun getDuration(): Int {
        val durationInput = binding.durationInputTextView.text

        val inputValues = Regex("[0-9]+").findAll(durationInput)
            .map(MatchResult::value)
            .toList()

        return (inputValues[0].toInt() * 60) + inputValues[1].toInt()
    }

    protected fun setBikeSpinner(bikeNames: List<String>) {
        binding.bikeNameSpinner.adapter = ArrayAdapter(
            requireContext(),
            com.mybike.common.R.layout.custom_spinner,
            bikeNames.ifEmpty { listOf("No bikes added yet") }
        ).also { adapter ->
            adapter.setDropDownViewResource(com.mybike.common.R.layout.custom_spinner_list_item)
        }
    }

    private fun initUi() {
        binding.rideTitleEditText.addTextChangedListener(inputValidator)
        binding.distanceEditText.addTextChangedListener(inputValidator)
        binding.durationInputTextView.addTextChangedListener(inputValidator)
        binding.dateInputTextView.addTextChangedListener(inputValidator)
        binding.background.setOnClickListener {
            it.hideSoftKeyboard()
        }
        binding.durationInputTextView.setOnClickListener {
            showDurationPickerDialog()
        }
        binding.dateInputTextView.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDurationPickerDialog() {
        val dialogBinding = DurationDialogBinding.inflate(LayoutInflater.from(context))
        durationDialog.setContentView(dialogBinding.root)

        dialogBinding.setButton.setOnClickListener {
            var hours = dialogBinding.hoursEditText.text.toString().ifEmpty { "0" }.toInt()
            var minutes = dialogBinding.minutesEditText.text.toString().ifEmpty { "0" }.toInt()

            hours += minutes / 60
            minutes %= 60

            binding.durationInputTextView.text =
                getString(R.string.duration_input_template, hours, minutes)
            durationDialog.dismiss()
        }
        durationDialog.show()
    }

    private fun showDatePickerDialog() {
        datePickerDialog.setContentView(datePickerDialogBinding.root)

        datePickerDialogBinding.monthSpinner.adapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.months_array, com.mybike.common.R.layout.custom_spinner
        ).also { adapter ->
            adapter.setDropDownViewResource(com.mybike.common.R.layout.custom_spinner_list_item)
        }
        datePickerDialogBinding.monthSpinner.onItemSelectedListener =
            monthSpinnerItemSelectedListener

        val localDate = Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        datePickerDialogBinding.yearSpinner.adapter = ArrayAdapter(
            requireContext(),
            com.mybike.common.R.layout.custom_spinner,
            (1900..localDate.year).toList()
        ).also { adapter ->
            adapter.setDropDownViewResource(com.mybike.common.R.layout.custom_spinner_list_item)
        }
        datePickerDialogBinding.yearSpinner.onItemSelectedListener = yearSpinnerItemSelectedListener

        datePickerDialogBinding.monthSpinner.setSelection(localDate.monthValue - 1)
        datePickerDialogBinding.yearSpinner.setSelection(
            (datePickerDialogBinding.yearSpinner.adapter as ArrayAdapter<Int>).getPosition(localDate.year)
        )

        datePickerDialogBinding.setButton.setOnClickListener {
            val day = datePickerDialogBinding.daySpinner.selectedItem as Int
            val month = datePickerDialogBinding.monthSpinner.selectedItemPosition + 1
            val year = datePickerDialogBinding.yearSpinner.selectedItem as Int

            binding.dateInputTextView.text = getString(
                R.string.date_input_template,
                if (day < 10) "0$day" else day.toString(),
                if (month < 10) "0$month" else month.toString(),
                year
            )
            datePickerDialog.dismiss()
        }
        datePickerDialog.show()
    }

    private val inputValidator = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            if (binding.rideTitleEditText.text.isNotEmpty()) {
                clearRideTitleError()
            }
            if (binding.distanceEditText.text.isNotEmpty()) {
                clearDistanceError()
            }
            if (binding.durationInputTextView.text.isNotEmpty()) {
                clearDurationError()
            }
            if (binding.dateInputTextView.text.isNotEmpty()) {
                clearDateError()
            }
            handleButtonState(
                binding.rideTitleEditText.text.isNotEmpty() && binding.distanceEditText.text.isNotEmpty() &&
                        binding.durationInputTextView.text.isNotEmpty() && binding.dateInputTextView.text.isNotEmpty()
            )
        }

    }

    private val monthSpinnerItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            updateDaySpinnerAdapter()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            Log.d(TAG, "Nothing selected")
        }
    }

    private val yearSpinnerItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            updateDaySpinnerAdapter()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            Log.d(TAG, "Nothing selected")
        }
    }

    protected fun showErrorWarnings() {
        if (binding.rideTitleEditText.text.isEmpty()) {
            showRideTitleError()
        }
        if (binding.distanceEditText.text.isEmpty()) {
            showDistanceError()
        }
        if (binding.durationInputTextView.text.isEmpty()) {
            showDurationError()
        }
        if (binding.dateInputTextView.text.isEmpty()) {
            showDateError()
        }
    }

    private fun updateDaySpinnerAdapter() {
        val day = datePickerDialogBinding.daySpinner.selectedItem as? Int
        val month = datePickerDialogBinding.monthSpinner.selectedItemPosition + 1
        val year = datePickerDialogBinding.yearSpinner.selectedItem as Int

        datePickerDialogBinding.daySpinner.adapter = ArrayAdapter(
            requireContext(),
            com.mybike.common.R.layout.custom_spinner,
            (1..YearMonth.of(year, month).lengthOfMonth()).toList()
        ).also { adapter ->
            adapter.setDropDownViewResource(com.mybike.common.R.layout.custom_spinner_list_item)
        }

        val currentSelection =
            day ?: Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().dayOfMonth
        datePickerDialogBinding.daySpinner.setSelection(
            if (currentSelection > datePickerDialogBinding.daySpinner.adapter.count - 1) {
                datePickerDialogBinding.daySpinner.adapter.count - 1
            } else {
                currentSelection - 1
            }
        )

    }

    private fun showRideTitleError() {
        binding.rideTitleRequiredFieldError.visibility = View.VISIBLE
        binding.rideTitleEditText.setBackgroundResource(com.mybike.common.R.drawable.edit_text_input_error_bg)
    }

    private fun showDistanceError() {
        binding.distanceRequiredFieldError.visibility = View.VISIBLE
        binding.distanceEditText.setBackgroundResource(com.mybike.common.R.drawable.edit_text_input_error_bg)
    }

    private fun showDurationError() {
        binding.durationRequiredFieldError.visibility = View.VISIBLE
        binding.durationInputTextView.setBackgroundResource(com.mybike.common.R.drawable.edit_text_input_error_bg)
    }

    private fun showDateError() {
        binding.dateRequiredFieldError.visibility = View.VISIBLE
        binding.dateInputTextView.setBackgroundResource(com.mybike.common.R.drawable.edit_text_input_error_bg)
    }

    private fun clearRideTitleError() {
        binding.rideTitleRequiredFieldError.visibility = View.INVISIBLE
        binding.rideTitleEditText.setBackgroundResource(com.mybike.common.R.drawable.edit_text_input_bg)
    }

    private fun clearDistanceError() {
        binding.distanceRequiredFieldError.visibility = View.INVISIBLE
        binding.distanceEditText.setBackgroundResource(com.mybike.common.R.drawable.edit_text_input_bg)
    }

    private fun clearDurationError() {
        binding.durationRequiredFieldError.visibility = View.INVISIBLE
        binding.durationInputTextView.setBackgroundResource(com.mybike.common.R.drawable.edit_text_input_bg)
    }

    private fun clearDateError() {
        binding.dateRequiredFieldError.visibility = View.INVISIBLE
        binding.dateInputTextView.setBackgroundResource(com.mybike.common.R.drawable.edit_text_input_bg)
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
                ContextCompat.getColor(
                    requireContext(),
                    com.mybike.common.R.color.app_blue_transparent
                )
            )
        }
        binding.addEditRideButtonText.setTextColor(colors.first)
        binding.addEditRideButton.background.setTint(colors.second)
    }
}