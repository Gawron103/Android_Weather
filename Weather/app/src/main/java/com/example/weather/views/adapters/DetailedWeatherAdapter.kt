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
import com.example.weather.models.TestModel
import com.example.weather.models.current_weather_model.Daily
import com.example.weather.models.current_weather_model.Weather
import com.example.weather.utils.DateProvider
import com.example.weather.views.interfaces.Communicator
import kotlin.math.round

class DetailedWeatherAdapter(
    private var weatherData: TestModel
): RecyclerView.Adapter<DetailedWeatherAdapter.DetailedWeatherViewHolder>() {

    fun updateForecast(newForecast: TestModel) {
        weatherData = newForecast
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DetailedWeatherViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.item_predicted_weather, parent, false),
        parent.context
    )

    override fun onBindViewHolder(holder: DetailedWeatherViewHolder, position: Int) {
        holder.bind(weatherData.weatherModel?.dailyConditions?.get(position)!!)
    }

    override fun getItemCount(): Int = weatherData.weatherModel?.dailyConditions?.size!!

    class DetailedWeatherViewHolder(view: View, private val context: Context): RecyclerView.ViewHolder(view) {

        private val dayName = view.findViewById<TextView>(R.id.tv_dayName)
        private val temperature = view.findViewById<TextView>(R.id.tv_temp)
        private val weatherIcon = view.findViewById<ImageView>(R.id.iv_weatherIcon)

        fun bind(weather: Daily) {
            val provider = DateProvider()

            dayName. text = provider.getDayName(weather?.currentTime!!)
            temperature.text = round(weather?.tempInDay?.day!!).toString()

            Glide.with(context).load("https://openweathermap.org/img/wn/${weather?.weather?.get(0)?.icon}@4x.png").into(weatherIcon)
        }
    }
}