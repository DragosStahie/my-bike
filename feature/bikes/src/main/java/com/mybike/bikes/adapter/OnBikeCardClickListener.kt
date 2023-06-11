package com.mybike.bikes.adapter

import com.mybike.data.model.Bike

interface OnBikeCardClickListener {
    fun onClick(bike: Bike)
}