package com.islery.weathertestapp

import android.app.Application
import android.content.Context
import com.islery.weathertestapp.data.persistence.WeatherDatabase
import timber.log.Timber

class WeatherApp : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: WeatherApp? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}