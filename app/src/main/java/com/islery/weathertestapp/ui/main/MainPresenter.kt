package com.islery.weathertestapp.ui.main

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.islery.weathertestapp.WeatherApp
import com.islery.weathertestapp.data.ForecastRepository
import moxy.MvpPresenter
import timber.log.Timber

class MainPresenter : MvpPresenter<MainView>() {
    private val repo: ForecastRepository = WeatherApp.provideRepo()


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.checkPermissions()
    }

    fun onStopCalled() {
        viewState.unregisterNetworkCallback()
    }

    fun onResumeCalled(oldInstance: Boolean, permDialogLaunched: Boolean) {
        if (!permDialogLaunched && oldInstance) {
            viewState.checkPermissions()
        }
    }

    fun updateNetworkStatus(cm: ConnectivityManager?) {
        cm?.let {
            lastInternetConnectionCheck(cm)
        }
    }

    fun onPermissionsCheck(granted: Boolean) {
        Timber.d("checking permissions")
        if (!granted) {
            viewState.requirePermissions()
        } else {
            viewState.registerNetworkCallback()
        }
    }

    private fun lastInternetConnectionCheck(cm: ConnectivityManager) =
        cm.allNetworks.forEach { network ->
            network?.let {
                cm.getNetworkCapabilities(it)
                    ?.let { networkCapabilities ->
                        val netInternet =
                            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        val transportCellular =
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        val transportWifi =
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        val transportEthernet =
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                        val transportVpn =
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)

                        val isConnected = netInternet ||
                                transportWifi || transportCellular ||
                                transportEthernet || transportVpn
                        repo.updateNetworkStatus(isConnected)
                    }
            }
        }

    fun onRequestPermissionsResult(result: Map<String?, Boolean?>) {
        if (result.containsValue(false)) {
            viewState.onPermissionsDenied()
        }
    }

}

