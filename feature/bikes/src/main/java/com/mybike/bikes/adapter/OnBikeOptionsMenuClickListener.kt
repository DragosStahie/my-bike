package com.mybike.bikes.adapter

import com.mybike.data.model.Bike

interface OnBikeOptionsMenuClickListener {
    fun onDeleteClicked(bike: Bike)

    fun onEditClicked(bike: Bike)
}