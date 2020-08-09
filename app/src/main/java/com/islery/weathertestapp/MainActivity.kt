package com.islery.weathertestapp

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.islery.weathertestapp.databinding.ActivityMainBinding
import com.islery.weathertestapp.ui.toPx


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        setAppBarDrawable()
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_today, R.id.navigation_forecast
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

    }

    private fun setAppBarDrawable() {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
                ContextCompat.getColor(this, R.color.color1),
                ContextCompat.getColor(this, R.color.color2),
                ContextCompat.getColor(this, R.color.color3),
                ContextCompat.getColor(this, R.color.color4),
                ContextCompat.getColor(this, R.color.color5),
                ContextCompat.getColor(this, R.color.color6),
                ContextCompat.getColor(this, R.color.color7)
            )
        )
        gradientDrawable.setStroke(3.toPx(), Color.parseColor("#FFFFFF"), 2f.toPx(), 3f.toPx())
        binding.appbarLayout.background = gradientDrawable
    }

}