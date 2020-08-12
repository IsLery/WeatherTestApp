package com.islery.weathertestapp.data

import android.location.Location
import com.islery.weathertestapp.data.model.ForecastResponse
import com.islery.weathertestapp.data.model.ListWeatherAndLocation
import com.islery.weathertestapp.data.model.SingleWeatherAndLocation
import com.islery.weathertestapp.data.network.WeatherApiService
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber

const val UPDATE_INTERVAL_MIN = 60L

class ForecastRepoImpl private constructor() : ForecastRepository {

    private var lastResponse: ForecastResponse? = null

    private var lastLat: Double? = 52.4313

    private var lastLon: Double? = 30.993

    private val apiService = WeatherApiService.create()

    private var networkAvailable = false

    override fun getForecast(
        location: Location?
    ): Observable<ListWeatherAndLocation> {
        val lat = location?.latitude
        val lon = location?.longitude
        if ((lat == null || lon == null) && (lastLat == null || lastLon == null)) {
            //don't have data at all
            return Observable.error(LocationSaveFailureException())
        }
        //if location is the same or new data is null
        if (shouldFetchFromNetwork(lat, lon)) {
            //check first timestamp, whether change needed

            //if new locations are not null, will use them
            if (lat != null && lon != null) {
                lastLat = lat
                lastLon = lon
            }

            return getForecastFromNetwork().map {
                ListWeatherAndLocation(
                    it.city.name,
                    it.city.country,
                    it.list
                )
            }
        } else {
            return Observable.just(lastResponse!!).map {
                ListWeatherAndLocation(
                    it.city.name,
                    it.city.country,
                    it.list
                )
            }
        }
    }


    private fun isSameLocation(lat: Double?, lon: Double?): Boolean {
        //if last locations are not null and any of new locations is null - we use old locations
        return if ((lastLat != null && lastLon != null) && (lat == null || lon == null)) {
            true
        } else
            return lastLat == lat && lastLon == lon
    }

    private fun shouldFetchFromNetwork(lat: Double?, lon: Double?): Boolean {
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

    override fun getDetail(location: Location?): Observable<SingleWeatherAndLocation> {
        val lat = location?.latitude
        val lon = location?.longitude
        Timber.d(
            "getDetail: lastLat = $lastLat, lastLon = $lastLon, newLAt = $lat, newLon = $lon"
        )
        if ((lat == null || lon == null) && (lastLat == null || lastLon == null)) {
            //don't have data at all
            return Observable.error(LocationSaveFailureException())
        } else
        //if location is the same or new data is null
            if (networkAvailable) {
                //check first timestamp, whether change needed
                //if new locations are not null, will use them
                if (lat != null && lon != null) {
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
                return Observable.just(lastResponse!!).map {
                    SingleWeatherAndLocation(
                        it.city.name,
                        it.city.country,
                        it.list[0]
                    )
                }
            }
    }

    override fun updateNetworkStatus(isAvailable: Boolean) {
        networkAvailable = isAvailable
    }

    override fun getNetworkStatus(): Boolean {
        return networkAvailable
    }


    private fun getForecastFromNetwork(
    ): Observable<ForecastResponse> {
        return apiService.getFiveDayForecast(lastLat!!, lastLon!!).doOnNext { lastResponse = it }
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