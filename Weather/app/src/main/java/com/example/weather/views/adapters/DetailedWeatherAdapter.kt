package com.example.weather.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.databinding.ItemPredictedWeatherBinding
import com.example.weather.models.current_weather_model.Daily
import com.example.weather.utils.DateProvider
import kotlin.math.round

class DetailedWeatherAdapter(
    private var forecast: List<Daily>,
    private val context: Context
): RecyclerView.Adapter<DetailedWeatherAdapter.DetailedWeatherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DetailedWeatherViewHolder (
        ItemPredictedWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: DetailedWeatherViewHolder, position: Int) {
        holder.bind(forecast[position], context)
    }

    override fun getItemCount(): Int = forecast.size

    inner class DetailedWeatherViewHolder(private val binding: ItemPredictedWeatherBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(daily: Daily, context: Context) {
            binding.tvDayName.text = DateProvider.getDayName(daily.currentTime!!)
            binding.tvTemp.text = round(daily.tempInDay?.day!!).toString()
            Glide.with(context).load(
                "https://openweathermap.org/img/wn/${daily.weather?.get(0)?.icon}@4x.png"
            ).error(R.drawable.error_icon).into(binding.ivWeatherIcon)
        }

    }

}