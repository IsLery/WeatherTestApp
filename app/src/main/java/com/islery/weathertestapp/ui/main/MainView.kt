package com.islery.weathertestapp.ui.main

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface MainView : MvpView {
    @OneExecution
    fun registerNetworkCallback()

    @Skip
    fun unregisterNetworkCallback()

    @Skip
    fun onPermissionsDenied()

    @OneExecution
    fun checkPermissions()

    @AddToEndSingle
    fun requirePermissions()
}