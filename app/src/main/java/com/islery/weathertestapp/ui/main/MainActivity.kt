package com.islery.weathertestapp.ui.main

import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.islery.weathertestapp.R
import com.islery.weathertestapp.databinding.ActivityMainBinding
import com.islery.weathertestapp.centerToolbarTitle
import com.islery.weathertestapp.makeSnackbarPeriodic
import com.islery.weathertestapp.toPx
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import timber.log.Timber


class MainActivity : MvpAppCompatActivity(),
    MainView {

    private lateinit var binding: ActivityMainBinding

    private val presenter by moxyPresenter { MainPresenter() }

    private lateinit var cm: ConnectivityManager

    private var sameInstanceRestored = false

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            presenter.onRequestPermissionsResult(it)
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cm =
            applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        setSupportActionBar(binding.toolbar)
        val navController = Navigation.findNavController(
            this,
            R.id.nav_host_fragment
        )
        setAppBarDrawable()
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_today,
                R.id.navigation_forecast
            )
        )
        binding.toolbar.centerToolbarTitle()
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    private fun setAppBarDrawable() {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
                ContextCompat.getColor(
                    this,
                    R.color.color1
                ),
                ContextCompat.getColor(
                    this,
                    R.color.color2
                ),
                ContextCompat.getColor(
                    this,
                    R.color.color3
                ),
                ContextCompat.getColor(
                    this,
                    R.color.color4
                ),
                ContextCompat.getColor(
                    this,
                    R.color.color5
                ),
                ContextCompat.getColor(
                    this,
                    R.color.color6
                ),
                ContextCompat.getColor(
                    this,
                    R.color.color7
                )
            )
        )
        gradientDrawable.setStroke(3.toPx(), Color.parseColor("#FFFFFF"), 2f.toPx(), 3f.toPx())
        binding.appbarLayout.background = gradientDrawable
    }


    override fun registerNetworkCallback() {
        Timber.d("registerNetworkCallback")
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_VPN)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .build()
        cm.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun unregisterNetworkCallback() {
        Timber.d("unregisterNetworkCallback")
        try {
            cm.unregisterNetworkCallback(networkCallback)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    override fun onPermissionsDenied() {
        binding.root.makeSnackbarPeriodic(R.string.no_permissions)
        this.finish()
    }

    override fun checkPermissions() {
        Timber.d("checking permissions")
        val res = (ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_NETWORK_STATE
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                )
        presenter.onPermissionsCheck(res)
    }

    override fun onResume() {
        super.onResume()
        Timber.d("same instance restored $sameInstanceRestored")
        presenter.onResumeCalled(sameInstanceRestored)
    }

    override fun onPause() {
        super.onPause()
        sameInstanceRestored = true
        presenter.onPauseCalled()
    }

    override fun requirePermissions() {
        requestPermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_NETWORK_STATE
            )
        )
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onLost(network: Network) {
            super.onLost(network)
            presenter.updateNetworkStatus(cm)
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            presenter.updateNetworkStatus(cm)
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            presenter.updateNetworkStatus(cm)
        }
    }

}