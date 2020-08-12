package com.islery.weathertestapp.ui

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

interface MainView : MvpView {
    @AddToEndSingle
    fun registerNetworkCallback()
    @AddToEndSingle
    fun unregisterNetworkCallback()
    @Skip
    fun onPermissionsDenied()
    @AddToEndSingle
    fun checkPermissions()
    @AddToEndSingle
    fun requirePermissions()
}