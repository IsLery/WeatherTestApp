package com.islery.weathertestapp.data.model

data class WeatherModel(
    val condition: WeatherCondition,
    val city: String,
    val countryCode: String,
    val iconMain: Int, //id from drawable resource
    val timestamp: Long,
    val timeZone: Int,
    val hour: String //"13:00"
)

data class WeatherCondition(
    val shortDescr: String, // for detail
    val longDescr: String, // for rv
    val temp: Int,
    val humidity: Int, //percentage
    val percipation: Int, // осадки
    val pressure: Int, // in hPa
    val windSpeed: Int, // in km/h
    val windDirection: String
)

