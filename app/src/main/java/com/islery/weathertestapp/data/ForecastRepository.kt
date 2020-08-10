package com.islery.weathertestapp.data

import com.islery.weathertestapp.data.model.ListeWeatherAndLocation
import com.islery.weathertestapp.data.model.SingleWeatherAndLocation
import com.islery.weathertestapp.data.model.WeatherModel
import io.reactivex.rxjava3.core.Single

interface ForecastRepository {
    //in case user triggers refresh
    fun getForecast( lat: Long? = null,
                     lon: Long? = null): Single<ListeWeatherAndLocation>

    fun getDetail(lat: Long?,
                  lon: Long?): Single<SingleWeatherAndLocation>
}