package com.islery.weathertestapp.utils

import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import com.islery.weathertestapp.R
import com.islery.weathertestapp.data.NoLocationException
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt

//local images are stored in typed array as well as image id's from network
fun String.getLocalImageId(context: Context): Int {
    val arrIconCodes = context.resources.getStringArray(R.array.icon_names_api)
    val arrLocalRes = context.resources.obtainTypedArray(R.array.icon_local_resource)
    val index = arrIconCodes.indexOf(this)
    val res = arrLocalRes.getResourceId(index,
        R.drawable.ic_drop
    )
    arrLocalRes.recycle()
    return res
}

//needed for item decoration
fun Int.toPx(): Int {
    return this * Resources.getSystem().displayMetrics.density.toInt()
}

//needed for item decoration
fun Float.toPx(): Float {
    return this * Resources.getSystem().displayMetrics.density
}

// showing toast extension from resource
fun Context.showToast(@StringRes idMessage: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, idMessage, duration).show()
}

// showing snackbar extension from resource
fun View.makeSnackbarPeriodic(@StringRes idMessage: Int, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, idMessage, duration).show()
}

//vonveting received exceptions to string resouece ids
fun Throwable.getErrorId(): Int {
    return when (this) {
        is NoLocationException -> {
            R.string.no_location
        }
        else -> {
            R.string.other_error
        }
    }
}

//location coordinates needn't be so precise
fun Double.round(fractDigits: Int): Double {
    val factor = 10.0.pow(fractDigits.toDouble())
    return (this * factor).roundToInt() / factor
}

fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize() }

fun Toolbar.centerToolbarTitle() {
    val title: CharSequence = this.title
    val outViews: ArrayList<View> = ArrayList(1)
    this.findViewsWithText(outViews, title, View.FIND_VIEWS_WITH_TEXT)
    if (outViews.isNotEmpty()) {
        val titleView = outViews[0] as TextView
        titleView.gravity = Gravity.CENTER
        val layoutParams: Toolbar.LayoutParams = titleView.layoutParams as Toolbar.LayoutParams
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        this.requestLayout()
    }
}
