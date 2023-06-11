package com.mybike.common

import com.mybike.data.model.Ride

interface OnRideOptionsMenuClickListener {
    fun onDeleteClicked(ride: Ride)

    fun onEditClicked(ride: Ride)
}