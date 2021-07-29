package com.example.weather.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.models.shared_weather_model.Current
import com.example.weather.models.shared_weather_model.SharedWeatherModel

class CurrentWeatherAdapter(private var currentWeather: ArrayList<SharedWeatherModel>): RecyclerView.Adapter<CurrentWeatherAdapter.SharedWeatherViewHolder>() {

    fun updateWeather(newWeather: List<SharedWeatherModel>) {
        currentWeather.clear()
        currentWeather.addAll(newWeather)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SharedWeatherViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_current_weather, parent, false)
    )

    override fun onBindViewHolder(holder: SharedWeatherViewHolder, position: Int) {
        holder.bind(currentWeather[position].current)
    }

    // There is only one current weather
    override fun getItemCount(): Int = currentWeather.size

    class SharedWeatherViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val cityName = view.findViewById<TextView>(R.id.tv_cityName)
        private val temperature = view.findViewById<TextView>(R.id.tv_temperature)
        private val weatherImg = view.findViewById<TextView>(R.id.tv_weatherImage)
        private val weatherDesc = view.findViewById<TextView>(R.id.tv_weatherDesc)

        fun bind(currentWeather: Current) {
//            cityName.text = currentWeather.
            temperature.text = currentWeather.temp.toString()
            weatherImg.text = currentWeather.weather[0].icon
            weatherDesc.text = currentWeather.weather[0].description
        }
    }
}