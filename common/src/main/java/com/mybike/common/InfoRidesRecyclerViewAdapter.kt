package com.mybike.common

import android.annotation.SuppressLint
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.drawable.ClipDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.mybike.common.databinding.BikeDetailsCardBinding
import com.mybike.common.databinding.RideMonthListItemBinding
import com.mybike.common.databinding.RideStatisticsCardBinding
import com.mybike.common.databinding.RidesListItemBinding
import com.mybike.common.enums.BikeColor
import com.mybike.common.enums.BikeType
import com.mybike.data.model.BikeItemWrapper
import com.mybike.data.model.Ride
import com.mybike.data.model.RideStatistics
import kotlin.math.absoluteValue

class InfoRidesRecyclerViewAdapter(
    private var itemsList: List<InfoRidesRecyclerViewItem> = listOf(),
    private val onRideOptionsMenuClickListener: OnRideOptionsMenuClickListener,
    private var distanceUnits: String = "KM"
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class ViewType { TOP_CARD_RIDE_STATS, TOP_CARD_BIKE_DETAILS, MONTH, RIDE }

    @SuppressLint("NotifyDataSetChanged")
    fun updateDataSet(newList: List<InfoRidesRecyclerViewItem>) {
        itemsList = newList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateDistanceUnits(newUnits: String) {
        distanceUnits = newUnits
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            ViewType.TOP_CARD_RIDE_STATS.ordinal -> RideStatisticsViewHolder(
                RideStatisticsCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            ViewType.TOP_CARD_BIKE_DETAILS.ordinal -> BikeDetailsViewHolder(
                BikeDetailsCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            ViewType.MONTH.ordinal -> MonthsViewHolder(
                RideMonthListItemBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

            ViewType.RIDE.ordinal -> RidesViewHolder(
                RidesListItemBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

            else -> super.createViewHolder(parent, viewType)
        }

    override fun getItemCount(): Int = itemsList.size

    override fun getItemViewType(position: Int): Int =
        when (itemsList[position].itemData) {
            is RideStatistics -> ViewType.TOP_CARD_RIDE_STATS.ordinal
            is BikeItemWrapper -> ViewType.TOP_CARD_BIKE_DETAILS.ordinal
            is String -> ViewType.MONTH.ordinal
            is Ride -> ViewType.RIDE.ordinal
            else -> -1
        }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BikeDetailsViewHolder -> holder.bind(itemsList[position].itemData as BikeItemWrapper)
            is RideStatisticsViewHolder -> holder.bind(itemsList[position].itemData as RideStatistics)
            is MonthsViewHolder -> holder.bind(itemsList[position].itemData as String)
            is RidesViewHolder -> holder.bind(itemsList[position].itemData as Ride)
        }
    }

    inner class BikeDetailsViewHolder(val binding: BikeDetailsCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ClickableViewAccessibility")
        fun bind(bikeItem: BikeItemWrapper) {
            binding.wheelSizeTextView.text =
                binding.root.context.getString(R.string.wheels_template, bikeItem.bike.wheelSize)
            val nextService =
                if (distanceUnits == "KM") bikeItem.bike.nextServiceAtKm - bikeItem.totalDistanceKm
                else (bikeItem.bike.nextServiceAtKm - bikeItem.totalDistanceKm).kmToMi()
            val totalDistance =
                if (distanceUnits == "KM") bikeItem.totalDistanceKm
                else bikeItem.totalDistanceKm.kmToMi()
            binding.serviceIntervalTextView.text =
                binding.root.context.getString(
                    R.string.service_in_template,
                    nextService,
                    distanceUnits
                )
            binding.rideNumberTextView.text =
                binding.root.context.getString(R.string.ride_number, bikeItem.noRides)
            binding.totalDistanceTextView.text =
                binding.root.context.getString(
                    R.string.total_rides_distance,
                    totalDistance,
                    distanceUnits
                )
            binding.serviceIntervalProgressBar.setOnTouchListener { _, _ -> return@setOnTouchListener true }
            binding.serviceIntervalProgressBar.progress =
                (((bikeItem.bike.nextServiceAtKm - bikeItem.totalDistanceKm).toFloat() / bikeItem.bike.serviceIntervalKm * 100).toInt()
                    .coerceIn(0, 100) - 100).absoluteValue

            when (bikeItem.bike.bikeType) {
                BikeType.MOUNTAIN_BIKE.ordinal -> {
                    setBikeFrame(R.drawable.bike_mtb_middle)
                    setBikeDrivetrain(R.drawable.bike_mtb_over)
                    if (bikeItem.bike.wheelSize.contains("29")) {
                        setBikeWheels(R.drawable.bike_mtb_big_wheels)
                    } else {
                        setBikeWheels(R.drawable.bike_mtb_small_wheels)
                    }
                }

                BikeType.ROAD_BIKE.ordinal -> {
                    setBikeFrame(R.drawable.bike_roadbike_middle)
                    setBikeDrivetrain(R.drawable.bike_roadbike_over)
                    if (bikeItem.bike.wheelSize.contains("29")) {
                        setBikeWheels(R.drawable.bike_roadbike_big_wheels)
                    } else {
                        setBikeWheels(R.drawable.bike_roadbike_small_wheels)
                    }
                }

                BikeType.ELECTRIC_BIKE.ordinal -> {
                    setBikeFrame(R.drawable.bike_electric_middle)
                    setBikeDrivetrain(R.drawable.bike_electric_over)
                    if (bikeItem.bike.wheelSize.contains("29")) {
                        setBikeWheels(R.drawable.bike_electric_big_wheels)
                    } else {
                        setBikeWheels(R.drawable.bike_electric_small_wheels)
                    }
                }

                BikeType.HYBRID_BIKE.ordinal -> {
                    setBikeFrame(R.drawable.bike_hybrid_middle)
                    setBikeDrivetrain(R.drawable.bike_hybrid_over)
                    if (bikeItem.bike.wheelSize.contains("29")) {
                        setBikeWheels(R.drawable.bike_hybrid_big_wheels)
                    } else {
                        setBikeWheels(R.drawable.bike_hybrid_small_wheels)
                    }
                }
            }

            when (bikeItem.bike.bikeColor) {
                BikeColor.WHITE.ordinal -> {
                    setBikeColor(R.color.bike_white)
                }

                BikeColor.GREEN.ordinal -> {
                    setBikeColor(R.color.bike_green)
                }

                BikeColor.RED.ordinal -> {
                    setBikeColor(R.color.bike_red)
                }

                BikeColor.YELLOW.ordinal -> {
                    setBikeColor(R.color.bike_yellow)
                }

                BikeColor.BLUE.ordinal -> {
                    setBikeColor(R.color.bike_blue)
                }

                BikeColor.ORANGE.ordinal -> {
                    setBikeColor(R.color.bike_orange)
                }

                BikeColor.CYAN.ordinal -> {
                    setBikeColor(R.color.bike_cyan)
                }

                BikeColor.BEIGE.ordinal -> {
                    setBikeColor(R.color.bike_beige)
                }

                BikeColor.PURPLE.ordinal -> {
                    setBikeColor(R.color.bike_purple)
                }

                BikeColor.DARK_BLUE.ordinal -> {
                    setBikeColor(R.color.bike_dark_blue)
                }

                BikeColor.BROWN.ordinal -> {
                    setBikeColor(R.color.bike_brown)
                }
            }
        }

        private fun setBikeFrame(drawableId: Int) {
            binding.bikeLayout.bikeFrame.setImageDrawable(
                ContextCompat.getDrawable(
                    binding.root.context,
                    drawableId
                )
            )
        }

        private fun setBikeDrivetrain(drawableId: Int) {
            binding.bikeLayout.bikeDrivetrain.setImageDrawable(
                ContextCompat.getDrawable(
                    binding.root.context,
                    drawableId
                )
            )
        }

        private fun setBikeWheels(drawableId: Int) {
            binding.bikeLayout.bikeWheels.setImageDrawable(
                ContextCompat.getDrawable(
                    binding.root.context,
                    drawableId
                )
            )
        }

        private fun setBikeColor(colorId: Int) {
            binding.bikeLayout.bikeFrame.setColorFilter(
                ContextCompat.getColor(
                    binding.root.context,
                    colorId
                )
            )
        }
    }

    inner class RideStatisticsViewHolder(val binding: RideStatisticsCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(rideStatistics: RideStatistics) {
            val totalDistance =
                if (distanceUnits == "KM") rideStatistics.getTotalDistance()
                else rideStatistics.getTotalDistance().kmToMi()
            val mtbTotalDistance =
                if (distanceUnits == "KM") rideStatistics.mtbTotalDistanceKm
                else rideStatistics.mtbTotalDistanceKm.kmToMi()
            val roadTotalDistance =
                if (distanceUnits == "KM") rideStatistics.roadTotalDistanceKm
                else rideStatistics.roadTotalDistanceKm.kmToMi()
            val electricTotalDistance =
                if (distanceUnits == "KM") rideStatistics.electricTotalDistanceKm
                else rideStatistics.electricTotalDistanceKm.kmToMi()
            val hybridTotalDistance =
                if (distanceUnits == "KM") rideStatistics.hybridTotalDistanceKm
                else rideStatistics.hybridTotalDistanceKm.kmToMi()
            binding.totalTextView.text =
                binding.root.context.getString(
                    R.string.total_distance_template,
                    totalDistance,
                    distanceUnits
                )
            (binding.mtbGraphBar.drawable as ClipDrawable).level =
                (mtbTotalDistance.toFloat() / totalDistance * 10000).toInt()
            binding.mtbGraphBar.drawable.mutate().colorFilter =
                BlendModeColorFilter(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.bike_orange
                    ), BlendMode.MULTIPLY
                )
            (binding.roadGraphBar.drawable as ClipDrawable).level =
                (roadTotalDistance.toFloat() / totalDistance * 10000).toInt()
            binding.roadGraphBar.drawable.mutate().colorFilter =
                BlendModeColorFilter(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.bike_red
                    ), BlendMode.MULTIPLY
                )
            (binding.electricGraphBar.drawable as ClipDrawable).level =
                (electricTotalDistance.toFloat() / totalDistance * 10000).toInt()
            binding.electricGraphBar.drawable.mutate().colorFilter =
                BlendModeColorFilter(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.bike_blue
                    ), BlendMode.MULTIPLY
                )
            (binding.hybridGraphBar.drawable as ClipDrawable).level =
                (hybridTotalDistance.toFloat() / totalDistance * 10000).toInt()
            binding.hybridGraphBar.drawable.mutate().colorFilter =
                BlendModeColorFilter(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.bike_yellow
                    ), BlendMode.MULTIPLY
                )
            binding.mtbDistanceTextView.text = mtbTotalDistance.toString()
            binding.roadDistanceTextView.text = roadTotalDistance.toString()
            binding.electricDistanceTextView.text = electricTotalDistance.toString()
            binding.hybridDistanceTextView.text = hybridTotalDistance.toString()
        }
    }

    inner class MonthsViewHolder(val binding: RideMonthListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(month: String) {
            binding.rideMonth.text = month
        }
    }

    inner class RidesViewHolder(val binding: RidesListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ride: Ride) {
            binding.rideTitleTextView.text = ride.rideName
            binding.bikeNameTextView.text =
                binding.root.context.getString(R.string.bike_name_template, ride.bikeName)
            binding.distanceTextView.text =
                binding.root.context.getString(
                    R.string.distance_template,
                    if (distanceUnits == "KM") ride.distanceKm
                    else ride.distanceKm.kmToMi(),
                    distanceUnits.lowercase()
                )
            binding.durationTextView.text = binding.root.context.getString(
                R.string.duration_template,
                (ride.duration / 60),
                (ride.duration % 60)
            )
            binding.dateTextView.text =
                binding.root.context.getString(
                    R.string.date_template,
                    ride.date.getDate()
                )
            binding.rideOptionsButton.setOnClickListener {
                binding.rideOptionsMenu.root.isVisible = !binding.rideOptionsMenu.root.isVisible
            }
            binding.rideOptionsMenu.bikeEditButton.setOnClickListener {
                onRideOptionsMenuClickListener.onEditClicked(ride)
            }
            binding.rideOptionsMenu.bikeDeleteButton.setOnClickListener {
                onRideOptionsMenuClickListener.onDeleteClicked(ride)
                binding.rideOptionsMenu.root.isVisible = false
            }
        }
    }
}