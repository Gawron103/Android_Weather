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
import com.example.weather.models.current_weather_model.WeatherModel
import com.example.weather.views.adapters.DetailedWeatherAdapter
import kotlin.math.round

class DetailedWeatherActivity : AppCompatActivity() {

    private val TAG = "DetailedWeatherActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_weather)

        val weatherModel= intent.getParcelableExtra<WeatherModel>("weather_model")
        val cityName = intent.getStringExtra("city_name")
        val countryCode = intent.getStringExtra("country_code")

        val forecast = findViewById<RecyclerView>(R.id.rv_forecast).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = DetailedWeatherAdapter(weatherModel?.dailyConditions!!)
        }

        val backBtn = findViewById<Button>(R.id.btn_back)
        backBtn.setOnClickListener { finish() }

        updateUI(weatherModel!!, cityName!!, countryCode!!)
    }

    private fun updateUI(model: WeatherModel, cityName: String, countryCode: String) {
        val cityWeatherIcon = findViewById<ImageView>(R.id.iv_cityWeatherIcon)
        val cityWeatherDesc = findViewById<TextView>(R.id.tv_cityWeatherDesc)
        val cityWeatherTemp = findViewById<TextView>(R.id.tv_cityWeatherTemp)
        val cityLocation = findViewById<TextView>(R.id.tv_cityWeatherLocation)

        Glide.with(this).load("https://openweathermap.org/img/wn/${model?.currentConditions?.weather?.get(0)?.icon}@4x.png").error(R.drawable.error_icon).into(cityWeatherIcon!!)

        cityWeatherDesc?.text = model?.currentConditions?.weather?.get(0)?.desc?.replaceFirstChar { it.uppercase() }
        cityWeatherTemp?.text = round(model?.currentConditions?.temp!!).toString()

        val builder = StringBuilder()

        builder.append(countryCode)
            .append(", ")
            .append(cityName)

        cityLocation?.text = builder.toString()
    }

}