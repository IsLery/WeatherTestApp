package com.islery.weathertestapp.data.model

import com.squareup.moshi.Json

//classes based on https://openweathermap.org/ api

data class ForecastResponse(
    val cod: String = "",
    val message: Double = 0.0,
    val cnt: Int = 0,
    val list: List<WeatherModel> = listOf(),
    val city: City = City()
)

data class Forecast(
    val dt: Long = 0, //timestamp
    val main: Main = Main(),
    val weather: List<Weather> = listOf(),
    val clouds: Clouds = Clouds(),
    val wind: Wind = Wind(),
    val rain: Rain = Rain(),
    val snow: Snow? = null,
    val dt_txt: String = ""
)

data class City(
    val id: Int = 0,
    val name: String = "",
    val coord: Coord = Coord(),
    val country: String = "",
    val timezone: Int = 0
)

data class Weather(
    val id: Int = 0,
    val main: String = "", // descr
    val description: String = "",
    val icon: String = ""
)

data class Wind(
    val speed: Double = 0.0, //in m/h
    val deg: Double = 0.0,
    val code: String = ""
)

data class Clouds(
    val all: Int = 0
)

class Rain(
    @Json(name = "3h") val percipation: Double = 0.0
)

class Snow(
    @Json(name = "3h") val percipation: Double = 0.0
)


data class Main(
    val temp: Double = 0.0,
    val pressure: Double = 0.0,
    val humidity: Int = 0
)


data class Coord(
    val lon: Double = 0.0,
    val lat: Double = 0.0
)
