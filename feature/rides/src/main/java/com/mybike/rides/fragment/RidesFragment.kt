package com.mybike.rides.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mybike.common.BaseFragment
import com.mybike.common.InfoRidesRecyclerViewAdapter
import com.mybike.common.InfoRidesRecyclerViewItem
import com.mybike.common.OnRideOptionsMenuClickListener
import com.mybike.common.TAG
import com.mybike.common.showDeleteConfirmationDialog
import com.mybike.common.showToast
import com.mybike.data.model.RepositoryRequestState
import com.mybike.data.model.Ride
import com.mybike.data.model.Status
import com.mybike.navigation.Navigator
import com.mybike.rides.databinding.FragmentRidesBinding
import com.mybike.rides.viewmodel.RidesViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class RidesFragment : BaseFragment<FragmentRidesBinding>(FragmentRidesBinding::inflate),
    OnRideOptionsMenuClickListener {
    private val viewModel: RidesViewModel by inject()
    private val navigator: Navigator by inject()
    override val title: String = "Rides"

    private val deleteConfirmationDialog: Dialog by lazy { Dialog(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        collectFlows()
    }

    private fun initUi() {
        binding.addRideButton.setOnClickListener {
            val action = RidesFragmentDirections.actionRidesFragmentToAddRideFragment()
            navigator.navController.navigate(action)
        }
        initRecyclerView()
        viewModel.getPreferredUnits()
        viewModel.getRidesInfo()
    }

    private fun initRecyclerView() {
        binding.ridesRecyclerView.apply {
            adapter =
                InfoRidesRecyclerViewAdapter(onRideOptionsMenuClickListener = this@RidesFragment)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getRidesInfoRequestState.collect {
                    when (it.status) {
                        Status.PAUSED -> binding.loadingProgressBar.visibility = View.GONE
                        Status.LOADING -> binding.loadingProgressBar.visibility = View.VISIBLE
                        Status.SUCCESS -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            it.data?.let { ridesInfo ->
                                Log.d(TAG, "Rides info: $ridesInfo")
                                if (ridesInfo.isNotEmpty()) {
                                    switchToRidesView()
                                    updateAdapterDataSet(ridesInfo)
                                } else {
                                    switchToEmptyView()
                                }
                            }
                        }

                        Status.ERROR -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            Log.e(TAG, "Could not load rides list, error: ${it.message}")
                        }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deleteRideRequestState.collect {
                    when (it.status) {
                        Status.PAUSED -> binding.loadingProgressBar.visibility = View.GONE
                        Status.LOADING -> binding.loadingProgressBar.visibility = View.VISIBLE
                        Status.SUCCESS -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            context?.showToast("Ride deleted successfully!")
                        }

                        Status.ERROR -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            Log.e(TAG, "Could not delete ride, error: ${it.message}")
                            context?.showToast("Ride could not be deleted!")
                        }
                    }
                    viewModel.deleteRideRequestState.value =
                        RepositoryRequestState(Status.PAUSED, null, "")
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getPreferredUnitsRequestState.collect {
                    when (it.status) {
                        Status.PAUSED -> binding.loadingProgressBar.visibility = View.GONE
                        Status.LOADING -> binding.loadingProgressBar.visibility = View.VISIBLE
                        Status.SUCCESS -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            it.data?.let { preferredUnits ->
                                (binding.ridesRecyclerView.adapter as InfoRidesRecyclerViewAdapter).updateDistanceUnits(
                                    if (preferredUnits == "Metric") getString(com.mybike.common.R.string.kilometers)
                                    else getString(com.mybike.common.R.string.miles)
                                )
                            }
                            Log.d(TAG, "Units fetched: ${it.data}")
                        }

                        Status.ERROR -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            Log.e(TAG, "Could not fetch units, error: ${it.message}")
                        }
                    }
                }
            }
        }
    }

    private fun switchToRidesView() {
        binding.emptyRidesView.visibility = View.GONE
        binding.ridesRecyclerView.visibility = View.VISIBLE
    }

    private fun switchToEmptyView() {
        binding.emptyRidesView.visibility = View.VISIBLE
        binding.ridesRecyclerView.visibility = View.GONE
    }

    private fun updateAdapterDataSet(items: List<InfoRidesRecyclerViewItem>) {
        (binding.ridesRecyclerView.adapter as InfoRidesRecyclerViewAdapter).updateDataSet(items)
    }

    override fun onDeleteClicked(ride: Ride) {
        deleteConfirmationDialog.showDeleteConfirmationDialog {
            viewModel.deleteRide(ride)
        }
    }

    override fun onEditClicked(ride: Ride) {
        val action = RidesFragmentDirections.actionRidesFragmentToEditRideFragment(ride.rideId)
        navigator.navController.navigate(action)
    }
}