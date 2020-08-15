package com.islery.weathertestapp.ui.today

import android.location.Location
import com.islery.weathertestapp.WeatherApp
import com.islery.weathertestapp.data.ForecastRepository
import com.islery.weathertestapp.data.NoLocationException
import com.islery.weathertestapp.data.model.SingleWeatherAndLocation
import com.islery.weathertestapp.utils.getErrorId
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import timber.log.Timber

class TodayPresenter : MvpPresenter<TodayView>() {
    private val repo: ForecastRepository = WeatherApp.provideRepo()

    private var disposable: Disposable? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.requestLocation()
    }

    private fun getDetail(location: Location?) {
        viewState.showProgress()
        disposable =
            repo.getDetail(location)
                .subscribeOn(
                    Schedulers.io()
                )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onGetDetailSuccess(it) }, { onGetDetailError(it) })
                .also { viewState.hideProgress() }
    }


    fun onLocationReceived(location: Location?) {
        Timber.d("lat: ${location?.latitude}, lon = ${location?.longitude}")
        getDetail(location)
    }

    fun onShareRequested() {
        viewState.shareForecast()
    }

    private fun onGetDetailError(e: Throwable) {
        e.printStackTrace()
        if(e is NoLocationException){
            viewState.onLocationError()
        }else {
            val msgId = e.getErrorId()
            viewState.showError(msgId)
        }
    }

    fun snackbarButtonClicked(){
        viewState.askSettings()
    }

    private fun onGetDetailSuccess(res: SingleWeatherAndLocation) {
        viewState.hideProgress()
        //default value with no data
        if (res.city != "--") {
            viewState.submitDetailData(res.model, res.city, res.countryCode)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}