package com.islery.weathertestapp.ui.forecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.islery.weathertestapp.data.model.WeatherModel
import com.islery.weathertestapp.databinding.FragmentForecastBinding
import com.islery.weathertestapp.ui.forecast.adapter.CustomItemDecoration
import com.islery.weathertestapp.ui.forecast.adapter.ForecastAdapter
import com.islery.weathertestapp.ui.forecast.adapter.UiModel
import com.islery.weathertestapp.ui.makeSnackbarPeriodic
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class ForecastFragment : MvpAppCompatFragment(), ForecastListView {

    private var _binding: FragmentForecastBinding? = null
    private val binding get() = _binding!!

    private  val presenter: ForecastPresenter by moxyPresenter { ForecastPresenter() }

    private val mAdapter = ForecastAdapter { showNextView(it) }

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
            return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun submitForecastData(list: List<UiModel>) {
        mAdapter.submitList(list)
    }

    override fun requestLocation() {
        TODO("Not yet implemented")
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

    override fun showNextView(weatherModel: WeatherModel) {

    }

    override fun setToolbarLabel(label: String) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = label
    }
}