package com.islery.weathertestapp.ui.today

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import com.islery.weathertestapp.R
import com.islery.weathertestapp.data.ForecastRepoImpl
import com.islery.weathertestapp.data.ForecastRepository
import com.islery.weathertestapp.data.model.SingleWeatherAndLocation
import com.islery.weathertestapp.data.model.WeatherModel
import com.islery.weathertestapp.ui.getErrorId
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.math.roundToLong

class TodayPresenter(private val contest: Context) : MvpPresenter<TodayView>() {
    private val repo: ForecastRepository = ForecastRepoImpl.getInstance()

    private var disposable: Disposable? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.d("MY_TAG", "onFirstViewAttach: ")
        viewState.checkLocationPermission()
    }

    fun onCheckPermissionResult(res: Int) {
        if (res == PackageManager.PERMISSION_DENIED) {
            viewState.requestLocationPermission()
        } else {
            Log.d("MY_TAG", "onCheckPermissionResult: ")
            viewState.requestLocation()
        }
    }

    fun onRequestPermissionResult(res: Boolean) {
        if (res) {
            viewState.requestLocation()
        } else {
            viewState.finishApp()
        }
    }

    fun onLocationReceived(location: Location?) {
        Log.d("MY_TAG", "onLocationReceived: ")
        viewState.showProgress()
        val lat = location?.latitude?.roundToLong()
        val lon = location?.longitude?.roundToLong()
        disposable =
            repo.getDetail(lat, lon)
                .subscribeOn(
                    Schedulers.io()
                )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onGetDetailSuccess(it) }, { onGetDetailError(it) })
                .also { viewState.hideProgress() }

    }

    fun onGetLocationFailed() {
        viewState.showError(R.string.location_error)
    }

    fun onShareRequested() {
        viewState.shareForecast()
    }

    private fun onGetDetailError(e: Throwable) {
        e.printStackTrace()
        val msgId = e.getErrorId()
        viewState.showError(msgId)
    }

    private fun onGetDetailSuccess(res: SingleWeatherAndLocation) {
        viewState.hideProgress()
        viewState.submitDetailData(res.model, res.city, res.countryCode)
    }
}