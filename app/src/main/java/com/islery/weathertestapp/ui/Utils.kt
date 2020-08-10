package com.islery.weathertestapp.ui

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import com.islery.weathertestapp.R
import java.lang.NullPointerException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*

fun Long.getTime(): String{
    val date = Date(this)
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    return format.format(date)
}

fun String.getLocalImageId(context: Context): Int{
    val arrIconCodes = context.resources.getStringArray(R.array.icon_names_api)
    val arrLocalRes = context.resources.obtainTypedArray(R.array.icon_local_resource)
    val index = arrIconCodes.indexOf(this)
    val res = arrLocalRes.getResourceId(index, R.drawable.ic_drop)
    arrLocalRes.recycle()
    return res
}

fun Int.toPx():Int{
    return this * Resources.getSystem().displayMetrics.density.toInt()
}

fun Float.toPx():Float{
    return this * Resources.getSystem().displayMetrics.density
}

// showing toast extension from resource
fun Context.showToast(@StringRes idMessage: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, idMessage, duration).show()
}

// showing snackbar extension from resource
fun View.makeSnackbarPeriodic(@StringRes idMessage: Int, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this,idMessage,duration).show()
}

fun Throwable.getErrorId():Int{
   return if (this is SocketTimeoutException || this is UnknownHostException) {
        R.string.no_connection
    } else if (this is NullPointerException) {
        R.string.no_data
    } else {
        R.string.other_error
    }
}