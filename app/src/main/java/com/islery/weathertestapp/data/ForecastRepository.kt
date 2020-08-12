package com.islery.weathertestapp.data

import android.location.Location
import com.islery.weathertestapp.data.model.ListWeatherAndLocation
import com.islery.weathertestapp.data.model.SingleWeatherAndLocation
import io.reactivex.rxjava3.core.Observable

interface ForecastRepository {

    fun getForecast(location: Location?): Observable<ListWeatherAndLocation>

    fun getDetail(location: Location?): Observable<SingleWeatherAndLocation>

    fun updateNetworkStatus(isAvailable: Boolean)

    fun getNetworkStatus(): Boolean
}