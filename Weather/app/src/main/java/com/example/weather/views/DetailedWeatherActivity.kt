package com.example.weather.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.models.CityModel
import com.example.weather.views.adapters.DetailedWeatherAdapter
import com.example.weather.views.fragments.DetailedWeatherFragment
import kotlin.math.round

class DetailedWeatherActivity : AppCompatActivity() {

    private lateinit var detailedWeatherAdapter: DetailedWeatherAdapter
    private lateinit var backBtn: Button
    private lateinit var model: CityModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_weather)

        model = intent.getParcelableExtra("model")!!

        detailedWeatherAdapter = DetailedWeatherAdapter(model)

        val forecast = findViewById<RecyclerView>(R.id.rv_forecast).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = detailedWeatherAdapter
        }

        backBtn = findViewById(R.id.btn_back)

        backBtn.setOnClickListener {
//            communicator.popFragment(DetailedWeatherFragment.TAG)
            finish()
        }

        updateUI()
    }

    private fun updateUI() {
        val cityWeatherIcon = findViewById<ImageView>(R.id.iv_cityWeatherIcon)
        val cityWeatherDesc = findViewById<TextView>(R.id.tv_cityWeatherDesc)
        val cityWeatherTemp = findViewById<TextView>(R.id.tv_cityWeatherTemp)
        val cityLocation = findViewById<TextView>(R.id.tv_cityWeatherLocation)

        Glide.with(this).load("https://openweathermap.org/img/wn/${model?.weatherModel?.currentConditions?.weather?.get(0)?.icon}@4x.png").error(R.drawable.error_icon).into(cityWeatherIcon!!)

        cityWeatherDesc?.text = model.weatherModel?.currentConditions?.weather?.get(0)?.desc?.replaceFirstChar { it.uppercase() }
        cityWeatherTemp?.text = round(model.weatherModel?.currentConditions?.temp!!).toString()

        val builder = StringBuilder()
//        builder.append(model.locationModel?.get(0)?.countryCode)
//            .append(", ")
//            .append(model.locationModel?.get(0)?.cityName)

        cityLocation?.text = builder.toString()
    }

}