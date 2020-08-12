package com.islery.weathertestapp.ui.today

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import com.islery.weathertestapp.R
import com.islery.weathertestapp.data.ForecastRepoImpl
import com.islery.weathertestapp.data.ForecastRepository
import com.islery.weathertestapp.data.NetworkDbRepoImpl
import com.islery.weathertestapp.data.model.SingleWeatherAndLocation
import com.islery.weathertestapp.data.model.WeatherModel
import com.islery.weathertestapp.ui.getErrorId
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import retrofit2.HttpException
import timber.log.Timber
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.math.roundToLong

class TodayPresenter() : MvpPresenter<TodayView>() {
    private val repo: ForecastRepository = ForecastRepoImpl.getInstance()

    private var disposable: Disposable? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
            viewState.requestLocation()
    }

    private fun getDetail(location: Location?){
                disposable =
            repo.getDetail(location)
                .subscribeOn(
                    Schedulers.io()
                )
                .doOnEach { Timber.d("each $it") }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onGetDetailSuccess(it) }, { onGetDetailError(it) })
                .also { viewState.hideProgress() }
    }


    fun onLocationReceived(location: Location?) {
        Log.d("MY_TAG", "onLocationReceived: ")
      getDetail(location)

    }

    fun onGetLocationFailed() {
        viewState.showError(R.string.location_error)
    }

    fun onShareRequested() {
        viewState.shareForecast()
    }

    private fun onGetDetailError(e: Throwable) {
        Log.d("MY_TAG", "onGetDetailError: ")
        e.printStackTrace()
        val msgId = e.getErrorId()
        viewState.showError(msgId)
    }

    private fun onGetDetailSuccess(res: SingleWeatherAndLocation) {
        Log.d("MY_TAG", "onGetDetailSuccess: ")
        viewState.hideProgress()
        viewState.submitDetailData(res.model, res.city, res.countryCode)
    }
}