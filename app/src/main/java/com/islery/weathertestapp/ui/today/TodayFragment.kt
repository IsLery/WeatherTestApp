package com.islery.weathertestapp.ui.today

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.view.isVisible
import com.google.android.gms.location.LocationServices
import com.islery.weathertestapp.R
import com.islery.weathertestapp.data.model.WeatherModel
import com.islery.weathertestapp.databinding.FragmentTodayBinding
import com.islery.weathertestapp.utils.getLocalImageId
import com.islery.weathertestapp.utils.makeSnackbarPeriodic
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import timber.log.Timber


class TodayFragment : MvpAppCompatFragment(), TodayView {

    private val presenter: TodayPresenter by moxyPresenter { TodayPresenter() }

    private var _binding: FragmentTodayBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTodayBinding.inflate(inflater, container, false)
        binding.shareBtn.setOnClickListener {
            presenter.onShareRequested()
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun submitDetailData(model: WeatherModel, city: String, country: String) {
        model.condition
            binding.locationTxt.text = getString(R.string.locat_det, city, country)
            binding.temperatureTxt.text =
                getString(R.string.temp_det, model.condition.temperature, model.condition.condName)
            binding.humidityTxt.text = getString(R.string.humid_det, model.condition.humidity)
            binding.precipitationTxt.text =
                getString(R.string.persip_det, model.condition.percipation.value)
            binding.pressureTxt.text = getString(R.string.press_det, model.condition.pressure)
            binding.windSpeedTxt.text = getString(R.string.wind_det, model.condition.windSpeed)
            binding.windDirectionTxt.text = model.condition.windDirection
            val id = model.iconMain.getLocalImageId(requireContext())
            binding.weatherImg.setImageResource(id)
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

    override fun shareForecast() {
        val forecast = requireContext().getString(
            R.string.forecast_text,
            binding.locationTxt.text,
            binding.temperatureTxt.text,
            binding.humidityTxt.text,
            binding.precipitationTxt.text,
            binding.pressureTxt.text,
            binding.windSpeedTxt.text,
            binding.windDirectionTxt.text
        )
        val intent = ShareCompat.IntentBuilder.from(requireActivity()).setType("text/plain")
            .setText(forecast)
            .setSubject(getString(R.string.forecast_title))
            .setChooserTitle(getString(R.string.share_forecast))
            .createChooserIntent()
        startActivity(intent)
    }

}