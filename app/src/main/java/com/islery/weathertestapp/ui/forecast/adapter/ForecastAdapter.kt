package com.islery.weathertestapp.ui.forecast.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

const val TYPE_HEADER = 110
const val TYPE_WEATHER = 210

class ForecastAdapter :
    ListAdapter<UiModel, RecyclerView.ViewHolder>(UIMODEL_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> HeaderItemHolder.create(parent)
            TYPE_WEATHER -> WeatherItemHolder.create(parent)
            else -> throw IllegalArgumentException("Wrong view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderItemHolder -> holder.bind(getItem(position) as UiModel.HeaderItem)
            is WeatherItemHolder -> holder.bind(getItem(position) as UiModel.WeatherItem, position)
            else -> throw IllegalArgumentException("Wrong item type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UiModel.HeaderItem -> TYPE_HEADER
            is UiModel.WeatherItem -> TYPE_WEATHER
        }
    }

}

val UIMODEL_COMPARATOR = object : DiffUtil.ItemCallback<UiModel>() {
    override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
        if (oldItem is UiModel.HeaderItem && newItem is UiModel.HeaderItem) {
            return oldItem.title == newItem.title
        }
        if (oldItem is UiModel.WeatherItem && newItem is UiModel.WeatherItem) {
            return oldItem.model.timestamp == oldItem.model.timestamp
        }
        return false
    }

    override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
        if (oldItem is UiModel.HeaderItem && newItem is UiModel.HeaderItem) {
            return oldItem.title == newItem.title
        }
        if (oldItem is UiModel.WeatherItem && newItem is UiModel.WeatherItem) {
            return oldItem.model == oldItem.model
        }
        return false
    }

}

