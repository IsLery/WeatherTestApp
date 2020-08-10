package com.islery.weathertestapp.ui.forecast

import com.islery.weathertestapp.R
import com.islery.weathertestapp.data.ForecastRepoImpl
import com.islery.weathertestapp.data.ForecastRepository
import com.islery.weathertestapp.data.model.WeatherModel
import com.islery.weathertestapp.data.network.WeatherApiService
import com.islery.weathertestapp.ui.forecast.adapter.UiModel
import com.islery.weathertestapp.ui.getErrorId
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import java.lang.NullPointerException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*


class ForecastPresenter : MvpPresenter<ForecastListView>() {

    private val repo: ForecastRepository = ForecastRepoImpl.getInstance()

    private var disposable: Disposable? = null

    private var city = "N/A"

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        disposable = repo.getForecast().subscribeOn(Schedulers.io())
            .map {
                city = it.city
                mapForUi(it.list) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onGetForecastSuccess(it) }, { onGetForecastError(it) })

    }

    private fun mapForUi(list: List<WeatherModel>): List<UiModel> {
        val result = mutableListOf<UiModel>()
        val timezone = TimeZone.getDefault()
        val formatWeekDay = SimpleDateFormat("E", Locale.getDefault())
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

    private fun onGetForecastSuccess(uiList: List<UiModel>) {
        viewState.setToolbarLabel(city)
        if (uiList.isNotEmpty()) {
            viewState.submitForecastData(uiList)
        } else viewState.showError(R.string.no_data)
    }

    private fun onGetForecastError(e: Throwable) {
        viewState.hideProgress()
        val msgId  = e.getErrorId()
        viewState.showError(msgId)

    }
}