package com.mybike.common

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.mybike.common.databinding.DeleteDialogBinding
import com.mybike.data.model.BikeItemWrapper
import com.mybike.data.model.Ride
import com.mybike.data.model.RideStatistics
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.math.roundToInt

val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }

fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.kmToMi(): Int = (this * 0.6214f).roundToInt()

fun Int.miToKm(): Int = (this * 1.609344f).roundToInt()

fun Context.showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Long.getMonth() : String  {
    val calendar = Calendar.getInstance()

    val currentYear = calendar.get(Calendar.YEAR)
    calendar.timeInMillis = this
    if (currentYear != calendar.get(Calendar.YEAR)) {
        return "OLDER"
    }

    return when(calendar.get(Calendar.MONTH)) {
        Calendar.JANUARY -> "JANUARY"
        Calendar.FEBRUARY -> "FEBRUARY"
        Calendar.MARCH -> "MARCH"
        Calendar.APRIL -> "APRIL"
        Calendar.MAY -> "MAY"
        Calendar.JUNE -> "JUNE"
        Calendar.JULY -> "JULY"
        Calendar.AUGUST -> "AUGUST"
        Calendar.SEPTEMBER -> "SEPTEMBER"
        Calendar.OCTOBER -> "OCTOBER"
        Calendar.NOVEMBER -> "NOVEMBER"
        Calendar.DECEMBER -> "DECEMBER"
        else -> "OTHER"
    }
}

@SuppressLint("SimpleDateFormat")
fun Long.getDate() : String = SimpleDateFormat("dd.MM.yyyy").format(this)

@SuppressLint("SimpleDateFormat")
fun String.getTime() : Long = SimpleDateFormat("dd.MM.yyyy").parse(this)!!.time

fun List<Ride>.createItemsList(rideStatistics: RideStatistics? = null, bikeItem: BikeItemWrapper? = null): List<InfoRidesRecyclerViewItem> {
    val ridesList = this
    val itemsList = mutableListOf<InfoRidesRecyclerViewItem>()

    if (rideStatistics != null && rideStatistics.getTotalDistance() > 0) {
        itemsList.add(InfoRidesRecyclerViewItem(rideStatistics))
    }

    if (bikeItem != null) {
        itemsList.add(InfoRidesRecyclerViewItem(bikeItem))
    }

    ridesList.forEachIndexed { index, ride ->
        if (index >= 1) {
            if (ridesList[index - 1].date.getMonth() != ride.date.getMonth()) {
                itemsList.add(InfoRidesRecyclerViewItem(ride.date.getMonth()))
            }
        } else {
            itemsList.add(InfoRidesRecyclerViewItem(ride.date.getMonth()))
        }
        itemsList.add(InfoRidesRecyclerViewItem(ride))
    }

    return itemsList
}

fun Dialog.showDeleteConfirmationDialog(onDeleteClick: () -> Unit) {
    val dialogBinding = DeleteDialogBinding.inflate(LayoutInflater.from(context))
    setContentView(dialogBinding.root)

    dialogBinding.okButton.setOnClickListener {
        onDeleteClick()
        dismiss()
    }

    dialogBinding.cancelButton.setOnClickListener {
        dismiss()
    }

    show()
}

fun View.hideSoftKeyboard() {
    (context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
        windowToken,
        0
    )
}