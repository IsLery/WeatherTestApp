package com.islery.weathertestapp.data

import android.util.Log
import com.islery.weathertestapp.data.model.ForecastResponse
import com.islery.weathertestapp.data.model.ListeWeatherAndLocation
import com.islery.weathertestapp.data.model.SingleWeatherAndLocation
import com.islery.weathertestapp.data.network.WeatherApiService
import io.reactivex.rxjava3.core.Single

const val UPDATE_INTERVAL_MIN = 60L

class ForecastRepoImpl private constructor() : ForecastRepository {
    private var city: String? = null

    private var lastResponse: ForecastResponse? = null

    private var lastLat: Long? = null

    private var lastLon: Long? = null

    private val apiService = WeatherApiService.create()

    override fun getForecast(
        lat: Long?,
        lon: Long?
    ): Single<ListeWeatherAndLocation> {
        if ((lat == null || lon == null) && (lastLat == null || lastLon == null)) {
            //don't have data at all
            return Single.error(NullPointerException())
        }
        //if location is the same or new data is null
        if (shouldFetchFromNetwork(lat, lon)) {
            //check first timestamp, whether change needed

            //if new locations are not null, will use them
            if(lat != null && lon != null) {
                lastLat = lat
                lastLon = lon
            }

            return getForecastFromNetwork().map {
                ListeWeatherAndLocation(
                    it.city.name,
                    it.city.country,
                    it.list
                )
            }
        } else {
            return Single.just(lastResponse!!).map {
                ListeWeatherAndLocation(
                    it.city.name,
                    it.city.country,
                    it.list
                )
            }
        }
    }


    private fun isSameLocation(lat: Long?, lon: Long?): Boolean {
        //if last locations are not null and any of new locations is null - we use old locations
        return if ((lastLat != null && lastLon != null) && (lat == null || lon == null)) {
            true
        } else
            return lastLat == lat && lastLon == lon
    }

    private fun shouldFetchFromNetwork(lat: Long?, lon: Long?): Boolean {
        //if db or runtime cache is empty
        return if (lastResponse == null) {
            true
        } else {
            //check if same location, if not - fetch from network
            if (!isSameLocation(lat, lon)) {
                true
            } else {
                //when location is the same check if data is fresh and there is no need to request it again
                System.currentTimeMillis() - lastResponse!!.list[0].timestamp > java.util.concurrent.TimeUnit.MINUTES.toMillis(
                    UPDATE_INTERVAL_MIN
                )
            }
        }
    }

    private fun fetchFromDb() {

    }

    override fun getDetail(lat: Long?, lon: Long?): Single<SingleWeatherAndLocation> {
        Log.d("MY_TAG", "getDetail: lastLat = $lastLat, lastLon = $lastLon, newLAt = $lat, newLon = $lon")
        if ((lat == null || lon == null) && (lastLat == null || lastLon == null)) {
            //don't have data at all
            return Single.error(NullPointerException())
        }else
        //if location is the same or new data is null
        if (shouldFetchFromNetwork(lat, lon)) {
            //check first timestamp, whether change needed
            //if new locations are not null, will use them
            if(lat != null && lon != null) {
                lastLat = lat
                lastLon = lon
            }

            return getForecastFromNetwork().map {
                SingleWeatherAndLocation(
                    it.city.name,
                    it.city.country,
                    it.list[0]
                )
            }
        } else {
            return Single.just(lastResponse!!).map {
                SingleWeatherAndLocation(
                    it.city.name,
                    it.city.country,
                    it.list[0]
                )
            }
        }
    }


    private fun getForecastFromNetwork(
    ): Single<ForecastResponse> {
        return apiService.getFiveDayForecast(lastLat!!, lastLon!!).doOnSuccess { lastResponse = it }
    }


    companion object {
        private var instance: ForecastRepoImpl? = null

        fun getInstance(): ForecastRepoImpl {
            if (instance == null) {
                instance = ForecastRepoImpl()
            }
            return instance as ForecastRepoImpl
        }
    }
}