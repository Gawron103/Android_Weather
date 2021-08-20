package com.example.weather.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.models.current_weather_model.Daily
import com.example.weather.utils.DateProvider
import kotlin.math.round

class ForecastListAdapter(private var forecast: ArrayList<Daily>): RecyclerView.Adapter<ForecastListAdapter.ForecastWeatherViewHolder>() {

    fun updateWeather(newWeather: List<Daily>) {
        forecast.clear()
        forecast.addAll(newWeather)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ForecastWeatherViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.item_forecast, parent, false),
        parent.context
    )

    override fun onBindViewHolder(holder: ForecastWeatherViewHolder, position: Int) {
            holder.bindWeatherData(forecast[position])
    }

    override fun getItemCount(): Int = forecast.size

    class ForecastWeatherViewHolder(view: View, private val context: Context): RecyclerView.ViewHolder(view) {

        private val dayNum = view.findViewById<TextView>(R.id.tv_dayName)
        private val description = view.findViewById<TextView>(R.id.tv_description)
        private val icon = view.findViewById<ImageView>(R.id.iv_icon)
        private val temp = view.findViewById<TextView>(R.id.tv_temp)
        private val sunrise = view.findViewById<TextView>(R.id.tv_sunrise)
        private val sunset = view.findViewById<TextView>(R.id.tv_sunset)

        fun bindWeatherData(dailyWeather: Daily) {
            Glide.with(context).load("https://openweathermap.org/img/wn/${dailyWeather.weather?.get(0)?.icon}.png").into(icon)

            description.text = dailyWeather.weather?.get(0)?.desc?.replaceFirstChar { it.uppercase() }
            temp.text = round(dailyWeather.tempInDay?.day!!).toString()

            val provider = DateProvider()

            sunrise.text = provider.convertTime(dailyWeather.sunrise!!)
            sunset.text = provider.convertTime(dailyWeather.sunset!!)

            dayNum.text = provider.getDayName(dailyWeather.currentTime!!)
        }
    }

}