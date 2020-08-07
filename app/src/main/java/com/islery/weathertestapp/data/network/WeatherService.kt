package com.islery.weathertestapp.data.network

import com.islery.weathertestapp.data.model.ForecastResponse
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val BASE_URL = "api.openweathermap.org/"
const val API_KEY = "4896d8375c4c5b3d45db10daea63a7f5"

interface WeatherApiService {

    @GET("data/2.5/forecast")
    fun getFiveDayForecast(
        @Query("lat") lat: Long,
        @Query("lon") lon: Long,
        @Query("units") units: String = "metric",
        @Query("appid") id: String = API_KEY
    ): Single<ForecastResponse>

//    @GET("data/2.5/weather")
//    fun getCurrentWeather(
//        @Query("lat") lat: Long,
//        @Query("lon") lon: Long,
//        @Query("units") units: String = "metric",
//        @Query("appid") id: String = API_KEY
//    )

    companion object {

        private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()

        private val apiService = retrofit.create(WeatherApiService::class.java)
    }
}