package com.example.weather.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.models.shared_weather_model.Daily
import com.example.weather.models.shared_weather_model.SharedWeatherModel

class ForecastListAdapter(var forecast: ArrayList<SharedWeatherModel>): RecyclerView.Adapter<ForecastListAdapter.ForecastWeatherViewHolder>() {

    fun updateWeather(newWeather: List<SharedWeatherModel>) {
        forecast.clear()
        forecast.addAll(newWeather)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ForecastWeatherViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.item_forecast, parent, false)
    )

    override fun onBindViewHolder(holder: ForecastWeatherViewHolder, position: Int) {
        holder.bind(forecast[0].daily[position])
    }

    override fun getItemCount(): Int = forecast[0].daily.size

    class ForecastWeatherViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val dayNum = view.findViewById<TextView>(R.id.tw_dayNumb)
        private val description = view.findViewById<TextView>(R.id.tw_description)
        private val icon = view.findViewById<TextView>(R.id.tw_icon)
        private val temp = view.findViewById<TextView>(R.id.tw_temp)
        private val moonrise = view.findViewById<TextView>(R.id.tw_moonrise)
        private val moonset = view.findViewById<TextView>(R.id.tw_moonset)

        fun bind(dailyWeather: Daily) {
            description.text = dailyWeather.weather[0].description
            icon.text = dailyWeather.weather[0].icon
            temp.text = dailyWeather.temp.day.toString()
            moonrise.text = dailyWeather.moonrise.toString()
            moonset.text = dailyWeather.moonset.toString()
        }
    }
}