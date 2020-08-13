package com.islery.weathertestapp.ui.forecast

import com.islery.weathertestapp.ui.BaseMvpView
import com.islery.weathertestapp.ui.forecast.adapter.UiModel
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface ForecastListView : MvpView, BaseMvpView {
    @AddToEndSingle
    fun submitForecastData(list: List<UiModel>)

    @AddToEndSingle
    fun requestLocation()

    @AddToEndSingle
    fun setToolbarLabel(label: String)

    @AddToEndSingle
    fun endRefreshing()
}