package com.mybike.bikes.fragment


import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mybike.common.InfoRidesRecyclerViewAdapter
import com.mybike.bikes.databinding.FragmentBikeDetailsBinding
import com.mybike.common.BaseFragment
import com.mybike.navigation.Navigator
import com.mybike.bikes.viewmodel.BikeDetailsViewModel
import com.mybike.common.InfoRidesRecyclerViewItem
import com.mybike.common.OnRideOptionsMenuClickListener
import com.mybike.common.TAG
import com.mybike.common.createItemsList
import com.mybike.common.showDeleteConfirmationDialog
import com.mybike.common.showToast
import com.mybike.data.model.BikeItemWrapper
import com.mybike.data.model.RepositoryRequestState
import com.mybike.data.model.Ride
import com.mybike.data.model.Status
import com.mybike.navigation.NavigationFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class BikeDetailsFragment :
    BaseFragment<FragmentBikeDetailsBinding>(FragmentBikeDetailsBinding::inflate),
    OnRideOptionsMenuClickListener {
    private val viewModel: BikeDetailsViewModel by inject()
    private val navigator: Navigator by inject()
    private val args: BikeDetailsFragmentArgs by navArgs()
    override val title: String by lazy { args.bike.name }

    private val deleteConfirmationDialog: Dialog by lazy { Dialog(requireContext()) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        viewModel.getRidesForBike(args.bike.name)
        viewModel.getPreferredUnits()
        collectFlows()
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            adapter =
                InfoRidesRecyclerViewAdapter(onRideOptionsMenuClickListener = this@BikeDetailsFragment)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getRidesRequestState.collect {
                    when (it.status) {
                        Status.PAUSED -> binding.loadingProgressBar.visibility = View.GONE
                        Status.LOADING -> binding.loadingProgressBar.visibility = View.VISIBLE
                        Status.SUCCESS -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            it.data?.let { ridesList ->
                                Log.d(TAG, "Rides list: $ridesList")
                                updateAdapterDataSet(
                                    ridesList.createItemsList(
                                        bikeItem = BikeItemWrapper(
                                            args.bike,
                                            ridesList.size,
                                            ridesList.sumOf { ride -> ride.distanceKm }
                                        )
                                    )
                                )
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
                                (binding.recyclerView.adapter as InfoRidesRecyclerViewAdapter).updateDistanceUnits(
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

    private fun updateAdapterDataSet(items: List<InfoRidesRecyclerViewItem>) {
        (binding.recyclerView.adapter as InfoRidesRecyclerViewAdapter).updateDataSet(items)
    }

    fun onDeleteClicked() {
        deleteConfirmationDialog.showDeleteConfirmationDialog {
            viewModel.deleteBike(args.bike)
        }
    }

    fun onEditClicked() {
        val action =
            BikeDetailsFragmentDirections.actionBikeDetailsFragmentToEditBikeFragment(args.bike)
        navigator.navController.navigate(action)
    }

    override fun onDeleteClicked(ride: Ride) {
        deleteConfirmationDialog.showDeleteConfirmationDialog {
            viewModel.deleteRide(ride)
        }
    }

    override fun onEditClicked(ride: Ride) {
        navigator.navigateToFlow(NavigationFlow.EditRideFlow(ride.rideId))
    }
}