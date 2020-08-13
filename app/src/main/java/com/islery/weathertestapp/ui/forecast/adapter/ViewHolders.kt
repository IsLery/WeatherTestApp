package com.islery.weathertestapp.ui.forecast.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.islery.weathertestapp.R
import com.islery.weathertestapp.databinding.HeaderRowBinding
import com.islery.weathertestapp.databinding.WeatherRowBinding
import com.islery.weathertestapp.getLocalImageId

class WeatherItemHolder private constructor(
    private val binding: WeatherRowBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: UiModel.WeatherItem, pos: Int) {
        val wthr = item.model
        val context = binding.root.context
        binding.descrTxt.text = wthr.condition.longDescr
        binding.temperatureTxt.text =
            context.getString(R.string.temp_rv, wthr.condition.temperature)
        binding.timeTxt.text = item.hour
        binding.weatherImg.setImageResource(wthr.iconMain.getLocalImageId(context))
        if (pos == 1) {
            binding.root.background =
                binding.root.context.getDrawable(R.drawable.current_weather_row)
        } else {
            binding.root.background = null
        }
    }

    companion object {
        fun create(
            parent: ViewGroup
        ): WeatherItemHolder {
            val binding =
                WeatherRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return WeatherItemHolder(binding)
        }
    }
}

class HeaderItemHolder private constructor(private val binding: HeaderRowBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: UiModel.HeaderItem) {
        binding.headerTxt.text = item.title
    }

    companion object {
        fun create(parent: ViewGroup): HeaderItemHolder {
            val binding =
                HeaderRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return HeaderItemHolder(binding)
        }
    }
}