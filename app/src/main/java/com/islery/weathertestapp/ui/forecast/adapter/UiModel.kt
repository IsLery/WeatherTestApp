package com.islery.weathertestapp.ui.forecast.adapter

import com.islery.weathertestapp.data.model.WeatherModel

sealed class UiModel {
    data class HeaderItem(val title: String) : UiModel()
    data class WeatherItem(val model: WeatherModel, val hour: String, val weekday: String) : UiModel()
}