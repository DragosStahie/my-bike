package com.mybike.bikes.fragment


import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mybike.bikes.adapter.BikesRecyclerViewAdapter
import com.mybike.bikes.adapter.OnBikeCardClickListener
import com.mybike.bikes.adapter.OnBikeOptionsMenuClickListener
import com.mybike.bikes.viewmodel.BikesViewModel
import com.mybike.common.BaseFragment
import com.mybike.navigation.Navigator
import com.mybike.bikes.databinding.FragmentBikesBinding
import com.mybike.common.TAG
import com.mybike.common.showDeleteConfirmationDialog
import com.mybike.common.showToast
import com.mybike.data.model.Bike
import com.mybike.data.model.BikeItemWrapper
import com.mybike.data.model.RepositoryRequestState
import com.mybike.data.model.Status
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class BikesFragment : BaseFragment<FragmentBikesBinding>(FragmentBikesBinding::inflate),
    OnBikeOptionsMenuClickListener, OnBikeCardClickListener {
    private val viewModel: BikesViewModel by inject()
    private val navigator: Navigator by inject()
    override val title: String = "Bikes"

    private val deleteConfirmationDialog: Dialog by lazy { Dialog(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        collectFlows()
    }

    private fun initUi() {
        binding.addBikeButton.setOnClickListener {
            val action = BikesFragmentDirections.actionBikesFragmentToAddBikeFragment()
            navigator.navController.navigate(action)
        }
        initRecyclerView()
        viewModel.getBikeItems()
        viewModel.getPreferredUnits()
    }

    private fun initRecyclerView() {
        binding.bikesRecyclerView.apply {
            adapter = BikesRecyclerViewAdapter(
                onBikeOptionsMenuClickListener = this@BikesFragment,
                onBikeCardClickListener = this@BikesFragment
            )
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getBikeItemsRequestState.collect {
                    when (it.status) {
                        Status.PAUSED -> binding.loadingProgressBar.visibility = View.GONE
                        Status.LOADING -> binding.loadingProgressBar.visibility = View.VISIBLE
                        Status.SUCCESS -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            it.data?.let { bikeItemsList ->
                                Log.d(TAG, "Bike items list: $bikeItemsList")
                                if (bikeItemsList.isNotEmpty()) {
                                    switchToBikesView()
                                    updateAdapterDataSet(bikeItemsList)
                                } else {
                                    switchToEmptyView()
                                }
                            }
                        }

                        Status.ERROR -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            Log.e(TAG, "Could not load bike items list, error: ${it.message}")
                        }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deleteBikeRequestState.collect {
                    when (it.status) {
                        Status.PAUSED -> binding.loadingProgressBar.visibility = View.GONE
                        Status.LOADING -> binding.loadingProgressBar.visibility = View.VISIBLE
                        Status.SUCCESS -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            context?.showToast("Bike deleted successfully!")
                        }

                        Status.ERROR -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            Log.e(TAG, "Could not delete bike, error: ${it.message}")
                            context?.showToast("Bike could not be deleted!")
                        }
                    }
                    viewModel.deleteBikeRequestState.value =
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
                                (binding.bikesRecyclerView.adapter as BikesRecyclerViewAdapter).updateDistanceUnits(
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

    private fun switchToBikesView() {
        binding.emptyBikesView.visibility = View.GONE
        binding.bikesRecyclerView.visibility = View.VISIBLE
    }

    private fun switchToEmptyView() {
        binding.emptyBikesView.visibility = View.VISIBLE
        binding.bikesRecyclerView.visibility = View.GONE
    }

    private fun updateAdapterDataSet(bikesList: List<BikeItemWrapper>) {
        (binding.bikesRecyclerView.adapter as BikesRecyclerViewAdapter).updateDataSet(bikesList)
    }

    override fun onDeleteClicked(bike: Bike) {
        deleteConfirmationDialog.showDeleteConfirmationDialog {
            viewModel.deleteBike(bike)
        }
    }

    override fun onEditClicked(bike: Bike) {
        val action = BikesFragmentDirections.actionBikesFragmentToEditBikeFragment(bike)
        navigator.navController.navigate(action)
    }

    override fun onClick(bike: Bike) {
        val action = BikesFragmentDirections.actionBikesFragmentToBikeDetailsFragment(bike)
        navigator.navController.navigate(action)
    }

}