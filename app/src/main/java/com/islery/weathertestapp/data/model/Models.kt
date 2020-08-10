package com.islery.weathertestapp.data.model

data class WeatherModel(
    val condition: WeatherCondition,
//    val city: String,
//    val countryCode: String,
    val iconMain: String, //id from drawable resource
    val timestamp: Long
  //  val timeZone: Int,
 //   val hour: String //"13:00"
){
    data class WeatherCondition(
        val condName: String, // for detail
        val longDescr: String, // for rv
        val temperature: Int,
        val humidity: Int, //percentage
        val percipation: Percipation = Percipation(), // осадки
        val pressure: Int, // in hPa
        val windSpeed: Int, // in km/h
        val windDirection: String
    )


    data class Percipation(
        val type: PersipationType = PersipationType.Rain,
        val value: Double = 0.0
    )

    enum class PersipationType{
         Rain, Snow
    }

}

data class SingleWeatherAndLocation(
    val city: String,
    val countryCode: String,
    val model: WeatherModel
)

data class ListeWeatherAndLocation(
    val city: String,
    val countryCode: String,
    val list: List<WeatherModel>
)


