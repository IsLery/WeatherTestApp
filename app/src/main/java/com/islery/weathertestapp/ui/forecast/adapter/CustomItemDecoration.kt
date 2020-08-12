package com.islery.weathertestapp.ui.forecast.adapter

import android.content.Context
import android.graphics.Canvas
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.islery.weathertestapp.R


class CustomItemDecoration(private val context: Context, orientation: Int) :
    DividerItemDecoration(
        context,
        orientation
    ) {

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val divRight = parent.width
        val typedValue = TypedValue()
        context.resources.getValue(R.integer.rv_image_ratio, typedValue, true)
        val offset = typedValue.float
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)
            val adapterPosition = parent.getChildAdapterPosition(child)
            val type = parent.adapter?.getItemViewType(adapterPosition)
            val nextType = if (i < childCount - 1) {
                parent.adapter?.getItemViewType(adapterPosition + 1)
            } else Int.MIN_VALUE
            val divLeft = if (type == TYPE_WEATHER && nextType != TYPE_HEADER) {
                (parent.width * offset).toInt() + child.paddingStart
            } else 0
            val params = child.layoutParams as RecyclerView.LayoutParams
            val divTop = child.bottom + params.bottomMargin
            val divBottom = divTop + drawable?.intrinsicHeight!!
            drawable!!.setBounds(divLeft, divTop, divRight, divBottom)
            drawable!!.draw(c)
        }
    }
}