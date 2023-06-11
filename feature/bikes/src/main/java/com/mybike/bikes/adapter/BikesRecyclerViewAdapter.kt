package com.mybike.bikes.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.mybike.common.R
import com.mybike.bikes.databinding.BikesListItemBinding
import com.mybike.common.enums.BikeType.*
import com.mybike.common.enums.BikeColor.*
import com.mybike.common.kmToMi
import com.mybike.data.model.BikeItemWrapper
import kotlin.math.absoluteValue

class BikesRecyclerViewAdapter(
    private var bikeItemsList: List<BikeItemWrapper> = listOf(),
    private var distanceUnits: String = "KM",
    private val onBikeOptionsMenuClickListener: OnBikeOptionsMenuClickListener,
    private val onBikeCardClickListener: OnBikeCardClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateDataSet(newList: List<BikeItemWrapper>) {
        bikeItemsList = newList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateDistanceUnits(newUnits: String) {
        distanceUnits = newUnits
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            BikesListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return BikesViewHolder(binding)
    }

    override fun getItemCount(): Int = bikeItemsList.size

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(holder as BikesViewHolder) {
            val bikeItem = bikeItemsList[position]
            val bike = bikeItem.bike
            val nextService =
                if (distanceUnits == "KM") bike.nextServiceAtKm - bikeItem.totalDistanceKm
                else (bike.nextServiceAtKm - bikeItem.totalDistanceKm).kmToMi()

            when (bike.bikeType) {
                MOUNTAIN_BIKE.ordinal -> {
                    setBikeFrame(binding, R.drawable.bike_mtb_middle)
                    setBikeDrivetrain(binding, R.drawable.bike_mtb_over)
                    if (bike.wheelSize.contains("29")) {
                        setBikeWheels(binding, R.drawable.bike_mtb_big_wheels)
                    } else {
                        setBikeWheels(binding, R.drawable.bike_mtb_small_wheels)
                    }
                }

                ROAD_BIKE.ordinal -> {
                    setBikeFrame(binding, R.drawable.bike_roadbike_middle)
                    setBikeDrivetrain(binding, R.drawable.bike_roadbike_over)
                    if (bike.wheelSize.contains("29")) {
                        setBikeWheels(binding, R.drawable.bike_roadbike_big_wheels)
                    } else {
                        setBikeWheels(binding, R.drawable.bike_roadbike_small_wheels)
                    }
                }

                ELECTRIC_BIKE.ordinal -> {
                    setBikeFrame(binding, R.drawable.bike_electric_middle)
                    setBikeDrivetrain(binding, R.drawable.bike_electric_over)
                    if (bike.wheelSize.contains("29")) {
                        setBikeWheels(binding, R.drawable.bike_electric_big_wheels)
                    } else {
                        setBikeWheels(binding, R.drawable.bike_electric_small_wheels)
                    }
                }

                HYBRID_BIKE.ordinal -> {
                    setBikeFrame(binding, R.drawable.bike_hybrid_middle)
                    setBikeDrivetrain(binding, R.drawable.bike_hybrid_over)
                    if (bike.wheelSize.contains("29")) {
                        setBikeWheels(binding, R.drawable.bike_hybrid_big_wheels)
                    } else {
                        setBikeWheels(binding, R.drawable.bike_hybrid_small_wheels)
                    }
                }
            }

            when (bike.bikeColor) {
                WHITE.ordinal -> {
                    setBikeColor(binding, R.color.bike_white)
                }

                GREEN.ordinal -> {
                    setBikeColor(binding, R.color.bike_green)
                }

                RED.ordinal -> {
                    setBikeColor(binding, R.color.bike_red)
                }

                YELLOW.ordinal -> {
                    setBikeColor(binding, R.color.bike_yellow)
                }

                BLUE.ordinal -> {
                    setBikeColor(binding, R.color.bike_blue)
                }

                ORANGE.ordinal -> {
                    setBikeColor(binding, R.color.bike_orange)
                }

                CYAN.ordinal -> {
                    setBikeColor(binding, R.color.bike_cyan)
                }

                BEIGE.ordinal -> {
                    setBikeColor(binding, R.color.bike_beige)
                }

                PURPLE.ordinal -> {
                    setBikeColor(binding, R.color.bike_purple)
                }

                DARK_BLUE.ordinal -> {
                    setBikeColor(binding, R.color.bike_dark_blue)
                }

                BROWN.ordinal -> {
                    setBikeColor(binding, R.color.bike_brown)
                }
            }

            binding.bikeNameTextView.text = bike.name
            binding.wheelSizeTextView.text =
                binding.root.context.getString(R.string.wheels_template, bike.wheelSize)
            binding.serviceIntervalTextView.text = binding.root.context.getString(
                R.string.service_in_template,
                nextService,
                distanceUnits
            )
            binding.serviceIntervalProgressBar.setOnTouchListener { _, _ -> return@setOnTouchListener true }
            binding.serviceIntervalProgressBar.progress =
                (((bike.nextServiceAtKm - bikeItem.totalDistanceKm).toFloat() / bikeItem.bike.serviceIntervalKm * 100).toInt()
                    .coerceIn(0, 100) - 100).absoluteValue
            binding.bikeOptionsButton.setOnClickListener {
                binding.bikeOptionsMenu.root.isVisible = !binding.bikeOptionsMenu.root.isVisible
            }
            binding.bikeOptionsMenu.bikeEditButton.setOnClickListener {
                onBikeOptionsMenuClickListener.onEditClicked(bike)
            }
            binding.bikeOptionsMenu.bikeDeleteButton.setOnClickListener {
                onBikeOptionsMenuClickListener.onDeleteClicked(bike)
                binding.bikeOptionsMenu.root.isVisible = false
            }
            binding.root.setOnClickListener {
                onBikeCardClickListener.onClick(bike)
            }
        }
    }

    private fun setBikeFrame(binding: BikesListItemBinding, drawableId: Int) {
        binding.bikeLayout.bikeFrame.setImageDrawable(
            ContextCompat.getDrawable(
                binding.root.context,
                drawableId
            )
        )
    }

    private fun setBikeDrivetrain(binding: BikesListItemBinding, drawableId: Int) {
        binding.bikeLayout.bikeDrivetrain.setImageDrawable(
            ContextCompat.getDrawable(
                binding.root.context,
                drawableId
            )
        )
    }

    private fun setBikeWheels(binding: BikesListItemBinding, drawableId: Int) {
        binding.bikeLayout.bikeWheels.setImageDrawable(
            ContextCompat.getDrawable(
                binding.root.context,
                drawableId
            )
        )
    }

    private fun setBikeColor(binding: BikesListItemBinding, colorId: Int) {
        binding.bikeLayout.bikeFrame.setColorFilter(
            ContextCompat.getColor(
                binding.root.context,
                colorId
            )
        )
    }

    inner class BikesViewHolder(val binding: BikesListItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}