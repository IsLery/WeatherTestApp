package com.islery.weathertestapp.data.network

import com.islery.weathertestapp.data.model.ForecastResponse
import com.islery.weathertestapp.data.model.WeatherMoshiAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

const val BASE_URL = "https://api.openweathermap.org/"
const val API_KEY = "4896d8375c4c5b3d45db10daea63a7f5"

interface WeatherApiService {

    @GET("data/2.5/forecast")
    fun getFiveDayForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") id: String = API_KEY
    ): Observable<ForecastResponse>

    companion object {

        fun create(): WeatherApiService {
            val moshi = Moshi.Builder()
                .add(WeatherMoshiAdapter())
                .add(KotlinJsonAdapterFactory())
                .build()
            val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(client)
                .build()

            return retrofit.create(WeatherApiService::class.java)
        }

    }
}