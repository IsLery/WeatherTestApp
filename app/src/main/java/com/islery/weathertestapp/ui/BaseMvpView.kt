package com.islery.weathertestapp.ui

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.SingleState
import moxy.viewstate.strategy.alias.Skip

interface BaseMvpView : MvpView {
    @Skip
    fun showError(messageId: Int)
    @AddToEndSingle
    fun showProgress()
    @AddToEndSingle
    fun hideProgress()
}