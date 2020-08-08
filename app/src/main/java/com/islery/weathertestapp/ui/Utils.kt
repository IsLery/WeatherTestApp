package com.islery.weathertestapp.ui

import java.text.SimpleDateFormat
import java.util.*

fun Long.getTime(): String{
    val date = Date(this)
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    return format.format(date)
}