package com.islery.weathertestapp.data

import android.location.Location
import com.islery.weathertestapp.data.model.CurrentInfo
import com.islery.weathertestapp.data.model.ListWeatherAndLocation
import com.islery.weathertestapp.data.model.SingleWeatherAndLocation
import com.islery.weathertestapp.data.model.WeatherModel
import com.islery.weathertestapp.data.network.WeatherApiService
import com.islery.weathertestapp.data.persistence.WeatherDatabase
import com.islery.weathertestapp.utils.round
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

//amount of minutes after the first timestamp in response, when network call is not needed
const val UPD_INTERVAL_MIN = 70L

class NetworkDbRepoImpl private constructor() : ForecastRepository {
    private val db: WeatherDatabase = WeatherDatabase.createDb()
    private val apiService = WeatherApiService.create()

    //value updated from callback in mainActivity
    private var networkConnected = false

    /*extracting values from the database and calling network to provide a list of forecasts
    Db call:
    1) retrieve list of forecasts from the weather table
    2) retrieve current information - city, country
    3) map all to needed observable
    Network call:
    1) check network connection, returning if there is none
    2) retrieve current information, that has timestamp of last network call as well as latitude and longitude
    3) check provided location and timestamp:
     3.1) - return Exception if db coordinates and current coordinates are null - asking user to enable gps
     3.2) - compare new and db coordinates:
     - if they are the same - check how much time passed since last response and return empty if it was recently
     - if they are different - fetch data from network with new coordinates. They will be rounded up to 4 after point to avoid unnecessary calls and make valid comparison
     4) on network response:
     4.1) clear weather table, add new data
     4.2) save current information to db
     5) map response to needed observable
     */
    override fun getForecast(location: Location?): Observable<ListWeatherAndLocation> {
        return Observable.concatArrayEager<ListWeatherAndLocation>(
            db.weatherDao().getAll()
                .onErrorReturnItem(emptyList())
                .flatMapObservable { md ->
                    db.infoDao().getInfo()
                        .onErrorReturnItem(
                            CurrentInfo()
                        )
                        .flatMapObservable { info ->
                            Observable.just(
                                ListWeatherAndLocation(
                                    city = info.city,
                                    countryCode = info.countryCode,
                                    list = md
                                )
                            )
                        }
                },
            Observable.defer {
                if (networkConnected) {
                    Timber.d("checking to call network, connection $networkConnected")
                    db.infoDao().getInfo().subscribeOn(Schedulers.io())
                        .onErrorReturnItem(
                            CurrentInfo()
                        )
                        .flatMapObservable { inf ->
                            Timber.d("info in db: lat = ${inf.latitude} lon = ${inf.longitude} ")
                            var lat = inf.latitude
                            var lon = inf.longitude

                            Timber.d(
                                "received location = ${location?.latitude} lon = ${location?.longitude} "
                            )
                            if ((lat == null || lon == null) && (location?.longitude == null)) {
                                Timber.d("don't access network")
                                return@flatMapObservable Observable.error<ListWeatherAndLocation>(NoLocationException())
                            } else if (location?.longitude == null || (lat == location.latitude.round(
                                    4
                                ) && lon == location.longitude.round(4))
                            ) {
                                if (System.currentTimeMillis() - inf.timestamp < java.util.concurrent.TimeUnit.MINUTES.toMillis(
                                        UPD_INTERVAL_MIN
                                    )
                                ) {
                                    Timber.d("don't access network, small interval ")
                                    return@flatMapObservable Observable.empty<ListWeatherAndLocation>()
                                }
                            } else {
                                lat = location.latitude
                                lon = location.longitude
                            }
                            lat = lat?.round(4)
                            lon = lon?.round(4)
                            apiService.getFiveDayForecast(lat!!, lon!!).subscribeOn(Schedulers.io())
                                .flatMap { response ->
                                    db.weatherDao().clearAll()
                                        .andThen(db.weatherDao().saveAll(response.list))
                                        .andThen(
                                            db.infoDao().updateInfo(
                                                CurrentInfo(
                                                    city = response.city.name,
                                                    countryCode = response.city.country,
                                                    timestamp = response.list[0].timestamp,
                                                    latitude = lat,
                                                    longitude = lon
                                                )
                                            )
                                        )
                                        .toObservable<Unit>()
                                        .map {

                                            ListWeatherAndLocation(
                                                city = response.city.name,
                                                countryCode = response.cod,
                                                list = response.list
                                            )
                                        }
                                }
                        }
                } else {
                    Observable.empty()
                }
            }
        )
    }

//network and db call from today/detail fragment, see upper method for explanation
    override fun getDetail(location: Location?): Observable<SingleWeatherAndLocation> {
        Timber.d("getting details")
        return Observable.concatArrayEager<SingleWeatherAndLocation>(
            db.weatherDao().getFirst()
                .onErrorReturnItem(WeatherModel())
                .flatMapObservable { md ->
                    db.infoDao().getInfo()
                        .onErrorReturnItem(
                            CurrentInfo()
                        )
                        .flatMapObservable { info ->
                            Observable.just(
                                SingleWeatherAndLocation(
                                    city = info.city,
                                    countryCode = info.countryCode,
                                    model = md
                                )
                            )
                        }
                },
            Observable.defer {
                if (networkConnected) {
                    Timber.d("checking to call network, connection $networkConnected")
                    db.infoDao().getInfo().subscribeOn(Schedulers.io())
                        .onErrorReturnItem(
                            CurrentInfo()
                        )
                        .flatMapObservable { inf ->
                            Timber.d("info in db: lat = ${inf.latitude} lon = ${inf.longitude} ")
                            var lat = inf.latitude
                            var lon = inf.longitude
                            Timber.d(
                                "received location = ${location?.latitude} lon = ${location?.longitude} "
                            )
                            if ((lat == null || lon == null) && (location?.longitude == null)) {
                                Timber.d("don't access network")
                                return@flatMapObservable Observable.error<SingleWeatherAndLocation>(NoLocationException())
                            } else {
                                if (location?.longitude == null || (lat == location.latitude.round(4) && lon == location.longitude.round(
                                        4
                                    ))
                                ) {
                                    if (System.currentTimeMillis() - inf.timestamp < java.util.concurrent.TimeUnit.MINUTES.toMillis(
                                            UPD_INTERVAL_MIN
                                        )
                                    ) {
                                        Timber.d("don't access network, small interval ")
                                        return@flatMapObservable Observable.empty<SingleWeatherAndLocation>()
                                    }
                                } else {
                                    lat = location.latitude
                                    lon = location.longitude
                                }
                                lat = lat?.round(4)
                                lon = lon?.round(4)
                                Timber.d("info request: lat = $lat lon = $lon ")

                                apiService.getFiveDayForecast(lat!!, lon!!)
                                    .subscribeOn(Schedulers.io())
                                    .flatMap { response ->
                                        db.weatherDao().clearAll()
                                            .andThen(db.weatherDao().saveAll(response.list))
                                            .andThen(
                                                db.infoDao().updateInfo(
                                                    CurrentInfo(
                                                        city = response.city.name,
                                                        countryCode = response.city.country,
                                                        timestamp = response.list[0].timestamp,
                                                        latitude = lat,
                                                        longitude = lon
                                                    )
                                                )
                                            )
                                            .toObservable<Unit>()
                                            .map {
                                                SingleWeatherAndLocation(
                                                    city = response.city.name,
                                                    countryCode = response.city.country,
                                                    model = response.list[0]
                                                )
                                            }
                                    }
                            }
                        }
                } else {
                    Observable.empty()
                }
            }
        )
    }


    override fun updateNetworkStatus(isAvailable: Boolean) {
        Timber.d("new network status = $isAvailable")
        networkConnected = isAvailable
    }

    override fun getNetworkStatus(): Boolean {
        return networkConnected
    }

    companion object {
        private var instance: NetworkDbRepoImpl? = null

        fun getInstance(): NetworkDbRepoImpl = synchronized(this) {
            if (instance == null) {
                instance = NetworkDbRepoImpl()
            }
            return instance as NetworkDbRepoImpl
        }
    }

}

//when no location was provided or saved in db
class NoLocationException(msg: String = "No location exist") : RuntimeException(msg)
