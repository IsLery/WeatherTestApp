package com.islery.weathertestapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.islery.weathertestapp.data.persistence.CURR_INFO_TABLE
import com.islery.weathertestapp.data.persistence.WEATHER_TABLE

@Entity(tableName = CURR_INFO_TABLE)
data class CurrentInfo(
    @PrimaryKey
    val id: Int = 1,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = 0,
    @ColumnInfo(name = "city")
    val city: String = "--",
    @ColumnInfo(name = "countryCode")
    val countryCode: String = "--",
    @ColumnInfo(name = "latitude")
    val latitude: Double? = null,
    @ColumnInfo(name = "longitude")
    val longitude: Double? = null
)

@Entity(tableName = WEATHER_TABLE)
data class WeatherModel(
    @PrimaryKey
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = 0,
    @ColumnInfo(name = "iconMain")
    val iconMain: String = "", //id from network, will be mapped to dravable resource
    @Embedded val condition: WeatherCondition = WeatherCondition()
) {
    data class WeatherCondition(
        @ColumnInfo(name = "name")
        val condName: String = "", // for detail today fragment
        @ColumnInfo(name = "description")
        val longDescr: String = "", // for rv
        @ColumnInfo(name = "temperature")
        val temperature: Int = 0,
        @ColumnInfo(name = "humidity")
        val humidity: Int = 0, //percentage
        @ColumnInfo(name = "pressure")
        val pressure: Int = 0, // in hPa
        @ColumnInfo(name = "speed")
        val windSpeed: Int = 0, // in km/h
        @ColumnInfo(name = "direction")
        val windDirection: String = "",
        @Embedded val percipation: Percipation = Percipation() // осадки
    )


    data class Percipation(
        @ColumnInfo(name = "pers_type")
        val type: String = "rain",
        @ColumnInfo(name = "pers_value")
        val value: Double = 0.0
    )


}

data class SingleWeatherAndLocation(
    val city: String,
    val countryCode: String,
    val model: WeatherModel
)

data class ListWeatherAndLocation(
    val city: String,
    val countryCode: String,
    val list: List<WeatherModel>
)


