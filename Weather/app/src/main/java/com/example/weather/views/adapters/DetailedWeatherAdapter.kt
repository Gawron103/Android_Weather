package com.example.weather.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.models.TestModel
import com.example.weather.models.current_weather_model.Daily
import com.example.weather.models.current_weather_model.Weather
import com.example.weather.utils.DateProvider
import com.example.weather.views.interfaces.Communicator

class DetailedWeatherAdapter(
    private var weatherData: TestModel
): RecyclerView.Adapter<DetailedWeatherAdapter.DetailedWeatherViewHolder>() {

    fun updateForecast(newForecast: TestModel) {
        weatherData = newForecast
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DetailedWeatherViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.item_predicted_weather, parent, false)
    )

    override fun onBindViewHolder(holder: DetailedWeatherViewHolder, position: Int) {
        holder.bind(weatherData.weatherModel?.dailyConditions?.get(position)!!)
    }

    override fun getItemCount(): Int = weatherData.weatherModel?.dailyConditions?.size!!

    class DetailedWeatherViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val dayName = view.findViewById<TextView>(R.id.tv_dayName)
        private val temperature = view.findViewById<TextView>(R.id.tv_temp)

        fun bind(weather: Daily) {
            val provider = DateProvider()

            dayName. text = provider.getDayName(weather?.currentTime!!)
            temperature.text = weather?.tempInDay?.day.toString()
        }
    }
}