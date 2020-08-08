package com.islery.weathertestapp.ui.forecast.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.islery.weathertestapp.R
import com.islery.weathertestapp.data.model.WeatherModel
import com.islery.weathertestapp.databinding.HeaderRowBinding
import com.islery.weathertestapp.databinding.WeatherRowBinding
import com.islery.weathertestapp.ui.getTime

class WeatherItemHolder private constructor(
    private val binding: WeatherRowBinding,
    private val listener: (weather: WeatherModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: UiModel.WeatherItem){
        val wthr = item.model
        val context = binding.root.context
        binding.root.setOnClickListener { listener(wthr) }
        binding.descrTxt.text = wthr.condition.longDescr
        binding.temperatureTxt.text = context.getString(R.string.temp_rv)
        binding.timeTxt.text = wthr.timestamp.getTime()
        //TODO add custom images
    }

    companion object {
        fun create(parent: ViewGroup, listener: (weather: WeatherModel) -> Unit): WeatherItemHolder {
            val binding =
                WeatherRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return WeatherItemHolder(binding, listener)
        }
    }
}

class HeaderItemHolder private constructor(private val binding: HeaderRowBinding): RecyclerView.ViewHolder(binding.root){

    fun bind(item: UiModel.HeaderItem){
        binding.headerTxt.text = item.title
    }

    companion object{
        fun create(parent: ViewGroup): HeaderItemHolder {
            val binding =
                HeaderRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return HeaderItemHolder(binding)
        }
    }
}