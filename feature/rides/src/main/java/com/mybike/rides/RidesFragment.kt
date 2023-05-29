package com.mybike.rides

import androidx.fragment.app.viewModels
import com.mybike.common.BaseFragment
import com.mybike.navigation.Navigator
import com.mybike.rides.databinding.FragmentRidesBinding
import org.koin.android.ext.android.inject

class RidesFragment : BaseFragment<FragmentRidesBinding>(FragmentRidesBinding::inflate) {
    private val viewModel: RidesViewModel by viewModels()
    private val navigator : Navigator by inject()
    override val title: String = "Rides"

}