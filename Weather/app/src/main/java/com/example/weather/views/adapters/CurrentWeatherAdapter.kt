package com.example.weather.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.models.CurrentWeatherModel

class CurrentWeatherAdapter(var currentWeather: ArrayList<CurrentWeatherModel>): RecyclerView.Adapter<CurrentWeatherAdapter.CurrentWeatherViewHolder>() {

    fun updateWeather(newWeather: List<CurrentWeatherModel>) {
        currentWeather.clear()
        currentWeather.addAll(newWeather)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CurrentWeatherViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_current_weather, parent, false)
    )

    override fun onBindViewHolder(holder: CurrentWeatherViewHolder, position: Int) {
        holder.bind(currentWeather[position])
    }

    // There is only one current weather
    override fun getItemCount(): Int = currentWeather.size

    class CurrentWeatherViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val cityName = view.findViewById<TextView>(R.id.tv_cityName)
        private val temperature = view.findViewById<TextView>(R.id.tv_temperature)
        private val weatherImg = view.findViewById<TextView>(R.id.tv_weatherDesc)
        private val weatherDesc = view.findViewById<TextView>(R.id.tv_weatherDesc)

        fun bind(currentWeather: CurrentWeatherModel) {
            cityName.text = currentWeather.name
            temperature.text = currentWeather.main.temp.toString()
            weatherImg.text = currentWeather.weather[0].icon
            weatherDesc.text = currentWeather.weather[0].description
        }
    }
}