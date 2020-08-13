package com.islery.weathertestapp.data.model

import com.islery.weathertestapp.data.WindDirectionUtils.convertDegreeToCardinalDirection
import com.islery.weathertestapp.capitalizeWords
import com.squareup.moshi.FromJson
import kotlin.math.roundToInt

class WeatherMoshiAdapter {

    @FromJson
    fun parseData(data: Forecast): WeatherModel {
        val weatherMoshi = data.weather[0]
        val windDir = if (data.wind.code.isEmpty()) {
            val res = convertDegreeToCardinalDirection(data.wind.deg)!!
            res
        } else {
            data.wind.code
        }
        val speed = if (data.wind.speed > 0) (data.wind.speed * 60 / 1000).roundToInt() else 0
        val pers = if (data.snow != null) WeatherModel.Percipation(
            "snow",
            data.snow.percipation
        )
        else WeatherModel.Percipation(value = data.rain.percipation)
        return WeatherModel(
            timestamp = data.dt * 1000,
            iconMain = weatherMoshi.icon,
            condition = WeatherModel.WeatherCondition(
                condName = weatherMoshi.main,
                longDescr = weatherMoshi.description.capitalizeWords(),
                temperature = data.main.temp.roundToInt(),
                humidity = data.main.humidity,
                pressure = data.main.pressure.roundToInt(),
                windSpeed = speed,
                windDirection = windDir,
                percipation = pers
            )
        )
    }


}