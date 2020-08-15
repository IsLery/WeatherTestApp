package com.islery.weathertestapp.ui.forecast

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.islery.weathertestapp.R
import com.islery.weathertestapp.databinding.FragmentForecastBinding
import com.islery.weathertestapp.utils.makeSnackbarPeriodic
import com.islery.weathertestapp.ui.forecast.adapter.CustomItemDecoration
import com.islery.weathertestapp.ui.forecast.adapter.ForecastAdapter
import com.islery.weathertestapp.ui.forecast.adapter.UiModel
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import timber.log.Timber
/*
fragment with list of forecasts for current location
 */
class ForecastFragment : MvpAppCompatFragment(), ForecastListView {

    private var _binding: FragmentForecastBinding? = null
    private val binding get() = _binding!!

    private val presenter: ForecastPresenter by moxyPresenter { ForecastPresenter() }

    private val mAdapter = ForecastAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForecastBinding.inflate(inflater, container, false)
        binding.forecastList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }.addItemDecoration(
            CustomItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        binding.swipe.setOnRefreshListener {
            presenter.onRefresh()
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun submitForecastData(list: List<UiModel>) {
        mAdapter.submitList(list)
    }

    @SuppressLint("MissingPermission")
    override fun requestLocation() {
        Timber.d("requestLocation: ")
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation.apply {
            addOnSuccessListener {
                presenter.onLocationReceived(it)
            }
        }
    }


    override fun showError(messageId: Int) {
        binding.root.makeSnackbarPeriodic(messageId)

    }

    override fun showProgress() {
        binding.progressBar.isVisible = true
    }

    override fun hideProgress() {
        binding.progressBar.isVisible = false
    }

    override fun setToolbarLabel(label: String) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = label
    }

    override fun endRefreshing() {
        binding.swipe.isRefreshing = false
    }

    override fun askSettings() {
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

    }

    override fun onLocationError() {
        Snackbar.make(
            binding.root,
            getString(R.string.enable_gps),
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(getString(R.string.ok)) { presenter.snackbarButtonClicked() }
            .show()
    }
}