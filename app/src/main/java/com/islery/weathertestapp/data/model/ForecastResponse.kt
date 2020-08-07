package com.islery.weathertestapp.data.model

data class ForecastResponse(
    val cod: String = "",
    val message: Double = 0.0,
    val cnt: Int = 0,
    val list: List<WeatherInfo> = listOf(),
    val city: City = City()
)

data class WeatherInfo(
    val dt: Int = 0,
    val main: Main = Main(),
    val weather: List<Weather> = listOf(),
    val clouds: Clouds = Clouds(),
    val wind: Wind = Wind(),
    val rain: Rain = Rain(),
    val sys: Sys = Sys(),
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
    val speed: Double = 0.0,
    val deg: Double = 0.0,
    val code: String = ""
)

data class Clouds(
    val all: Int = 0
)

data class Main(
    val temp: Double = 0.0,
    val temp_min: Double = 0.0,
    val temp_max: Double = 0.0,
    val pressure: Double = 0.0,
//    val sea_level: Double = 0.0,
//    val grnd_level: Double = 0.0,
    val humidity: Int = 0
    //   val temp_kf: Int = 0
)


class Rain(
)

data class Coord(
    val lon: Int = 0,
    val lat: Int = 0
)

data class Sys(
    val pod: String = ""
)
