package com.islery.weathertestapp.data.model

import com.islery.weathertestapp.data.WindDirectionUtils.convertDegreeToCardinalDirection
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
        val pers = if (data.snow != null) WeatherModel.Percipation(
            WeatherModel.PersipationType.Snow,
            data.snow.percipation
        )
        else WeatherModel.Percipation(WeatherModel.PersipationType.Rain, data.rain.percipation)
        return WeatherModel(
            WeatherModel.WeatherCondition(
                condName = weatherMoshi.main,
                longDescr = weatherMoshi.description,
                temperature = data.main.temp.roundToInt(),
                humidity = data.main.humidity,
                percipation = pers,
                pressure = data.main.pressure.roundToInt(),
                windSpeed = if (data.wind.speed > 0) (data.wind.speed * 60 / 1000).roundToInt() else 0,
                windDirection = windDir
            ),
            iconMain = weatherMoshi.icon,
            timestamp = data.dt
        )

    }


}