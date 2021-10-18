package com.example.weather.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.databinding.ItemForecastCurrentLocationBinding
import com.example.weather.models.current_weather_model.Daily
import com.example.weather.utils.DateProvider
import kotlin.math.round

class CurrentLocationForecastAdapter (
    private var forecastList: ArrayList<Daily>,
    private val context: Context
):
    RecyclerView.Adapter<CurrentLocationForecastAdapter.ForecastViewHolder>()
{
    fun updateForecast(newForecast: List<Daily>) {
        forecastList.clear()
        forecastList.addAll(newForecast)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ForecastViewHolder (
        ItemForecastCurrentLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(forecastList[position])
    }

    override fun getItemCount(): Int = forecastList.size

    inner class ForecastViewHolder(private val binding: ItemForecastCurrentLocationBinding): RecyclerView.ViewHolder(binding.root) {

            fun bind(day: Daily) {
                binding.tvDayName.text = DateProvider.getDayName(day.currentTime!!)
                binding.tvTemperature.text = round(day.tempInDay?.day!!).toString()
                Glide.with(context).load(
                    "https://openweathermap.org/img/wn/${day.weather?.get(0)?.icon}@4x.png"
                ).error(R.drawable.error_icon).into(binding.ivWeatherIcon)
            }

        }

}