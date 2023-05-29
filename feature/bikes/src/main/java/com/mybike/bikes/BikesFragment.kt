package com.mybike.bikes


import android.os.Bundle
import android.view.View
import com.mybike.common.BaseFragment
import com.mybike.navigation.Navigator
import com.mybike.bikes.databinding.FragmentBikesBinding
import org.koin.android.ext.android.inject

class BikesFragment : BaseFragment<FragmentBikesBinding>(FragmentBikesBinding::inflate) {
    private val viewModel: BikesViewModel by inject()
    private val navigator : Navigator by inject()
    override val title: String = "Bikes"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.testDataStore()
    }

}