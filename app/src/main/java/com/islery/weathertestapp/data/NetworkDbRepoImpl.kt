package com.islery.weathertestapp.data

import android.location.Location
import com.islery.weathertestapp.data.model.CurrentInfo
import com.islery.weathertestapp.data.model.ListWeatherAndLocation
import com.islery.weathertestapp.data.model.SingleWeatherAndLocation
import com.islery.weathertestapp.data.model.WeatherModel
import com.islery.weathertestapp.data.network.WeatherApiService
import com.islery.weathertestapp.data.persistence.WeatherDatabase
import com.islery.weathertestapp.round
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber


class NetworkDbRepoImpl private constructor() : ForecastRepository {
    private val db: WeatherDatabase = WeatherDatabase.createDb()
    private val apiService = WeatherApiService.create()

    private var networkConnected = false


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
                                        UPDATE_INTERVAL_MIN
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
            }.subscribeOn(Schedulers.io())
        )
    }


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
                                            UPDATE_INTERVAL_MIN
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
                                Timber.d("info request: lat = ${lat} lon = ${lon} ")

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

//when no location were provided or saved in db
class NoLocationException(msg: String = "No location exist") : RuntimeException(msg)

class NoNetworkException(msg: String = "No network") : RuntimeException(msg)