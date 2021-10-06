package com.example.weather.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.databinding.ActivityDetailedWeatherBinding
import com.example.weather.models.current_weather_model.WeatherModel
import com.example.weather.views.adapters.DetailedWeatherAdapter
import kotlin.math.round

class DetailedWeatherActivity : AppCompatActivity() {

    private val TAG = "DetailedWeatherActivity"

    private lateinit var binding: ActivityDetailedWeatherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedWeatherBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val weatherModel= intent.getParcelableExtra<WeatherModel>("weather_model")
        val cityName = intent.getStringExtra("city_name")
        val countryCode = intent.getStringExtra("country_code")

        binding.rvForecast.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = DetailedWeatherAdapter(weatherModel?.dailyConditions!!, context)
        }

        binding.btnBack.setOnClickListener { finish() }

        updateUI(weatherModel!!, cityName!!, countryCode!!)
    }

    private fun updateUI(model: WeatherModel, cityName: String, countryCode: String) {
        Glide.with(this).load("https://openweathermap.org/img/wn/${model?.currentConditions?.weather?.get(0)?.icon}@4x.png").error(R.drawable.error_icon).into(binding.ivCityWeatherIcon)

        binding.tvCityWeatherDesc.text = model?.currentConditions?.weather?.get(0)?.desc?.replaceFirstChar { it.uppercase() }
        binding.tvCityWeatherTemp.text = round(model?.currentConditions?.temp!!).toString()

        val builder = StringBuilder()
        builder.append(countryCode)
            .append(", ")
            .append(cityName)

        binding.tvCityWeatherLocation.text = builder.toString()
    }

}