package com.islery.weathertestapp.ui.today

import com.islery.weathertestapp.data.model.WeatherModel
import com.islery.weathertestapp.ui.BaseMvpView
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

interface TodayView : MvpView, BaseMvpView {
    @AddToEndSingle
    fun submitDetailData(model: WeatherModel, city: String, country: String)

    @AddToEndSingle
    fun requestLocation()

    @Skip
    fun shareForecast()
}