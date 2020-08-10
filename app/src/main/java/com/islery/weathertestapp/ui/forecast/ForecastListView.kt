package com.islery.weathertestapp.ui.forecast

import com.islery.weathertestapp.data.model.WeatherModel
import com.islery.weathertestapp.ui.BaseMvpView
import com.islery.weathertestapp.ui.forecast.adapter.UiModel
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

interface ForecastListView : MvpView, BaseMvpView {
    @AddToEndSingle
    fun submitForecastData(list: List<UiModel>)
    @AddToEndSingle
    fun requestLocation()
    @Skip
    fun showNextView(item: WeatherModel)
    @AddToEndSingle
    fun setToolbarLabel(label: String)
}