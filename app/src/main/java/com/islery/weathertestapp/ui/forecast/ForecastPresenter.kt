package com.islery.weathertestapp.ui.forecast

import android.location.Location
import com.islery.weathertestapp.R
import com.islery.weathertestapp.WeatherApp
import com.islery.weathertestapp.data.ForecastRepository
import com.islery.weathertestapp.data.model.WeatherModel
import com.islery.weathertestapp.utils.getErrorId
import com.islery.weathertestapp.ui.forecast.adapter.UiModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class ForecastPresenter : MvpPresenter<ForecastListView>() {

    private val repo: ForecastRepository = WeatherApp.provideRepo()

    private var disposable: Disposable? = null

    private var city = "--"

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showProgress()
        //first fragment have already requested new data for this location
        getForecast(null)
    }

    private fun getForecast(location: Location?) {
        disposable = repo.getForecast(location).subscribeOn(Schedulers.io())
            .map {
                city = it.city
                mapForUi(it.list)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onGetForecastSuccess(it) }, { onGetForecastError(it) })
            .also {
                viewState.hideProgress()
                viewState.endRefreshing()
            }
    }

    /*
    map data from repository fot ui
    - convert list timestamps for hour and weekday in default timezone
    - add headers when weekday changes
     */
    private fun mapForUi(list: List<WeatherModel>): List<UiModel> {
        val result = mutableListOf<UiModel>()
        val timezone = TimeZone.getDefault()
        val formatWeekDay = SimpleDateFormat("EEEE", Locale("EN"))
        val formatTime = SimpleDateFormat("HH:mm", Locale.getDefault())
        formatWeekDay.timeZone = timezone
        formatTime.timeZone = timezone
        for (i in list.indices) {
            val currDate = Date(list[i].timestamp)
            val timeStr = formatTime.format(currDate)
            val weekDay = formatWeekDay.format(currDate)
            if (i == 0) {
                result.add(UiModel.HeaderItem("Today"))
            } else {
                val last = result.last()
                if (last is UiModel.WeatherItem) {
                    if (last.weekday != weekDay) {
                        result.add(UiModel.HeaderItem(weekDay))
                    }
                }
            }
            result.add(UiModel.WeatherItem(list[i], hour = timeStr, weekday = weekDay))
        }
        return result
    }

    //submit list to adapter
    private fun onGetForecastSuccess(uiList: List<UiModel>) {
        viewState.setToolbarLabel(city)
        if (uiList.isNotEmpty()) {
            viewState.submitForecastData(uiList)
        } else viewState.showError(R.string.no_data)
    }

    private fun onGetForecastError(e: Throwable) {
        val msgId = e.getErrorId()
        viewState.showError(msgId)
    }

    fun onRefresh() {
        viewState.requestLocation()
    }

    fun onLocationReceived(location: Location?) {
        getForecast(location)
        Timber.d("lat: ${location?.latitude}, lon = ${location?.longitude}")
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}