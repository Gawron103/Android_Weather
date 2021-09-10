package com.example.weather.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.models.CityModel
import com.example.weather.views.adapters.DetailedWeatherAdapter
import com.example.weather.views.interfaces.Communicator
import kotlin.math.round

class DetailedWeatherFragment(private val data: CityModel) : Fragment() {

    private lateinit var communicator: Communicator
    private lateinit var detailedWeatherAdapter: DetailedWeatherAdapter
    private lateinit var backBtn: Button

    companion object {
        val TAG = DetailedWeatherFragment::class.java.simpleName
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "DetailedWeatherFragment destroyed")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        communicator = requireActivity() as Communicator
        return inflater.inflate(R.layout.fragment_detailed_weather, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // maybe better pass here only weather data
        detailedWeatherAdapter = DetailedWeatherAdapter(data)

        val forecast = requireView().findViewById<RecyclerView>(R.id.rv_forecast).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = detailedWeatherAdapter
        }

        backBtn = requireView().findViewById(R.id.btn_back)

        backBtn.setOnClickListener {
            communicator.popFragment(TAG)
        }

        updateUI()
    }

    private fun updateUI() {
        val cityWeatherIcon = view?.findViewById<ImageView>(R.id.iv_cityWeatherIcon)
        val cityWeatherDesc = view?.findViewById<TextView>(R.id.tv_cityWeatherDesc)
        val cityWeatherTemp = view?.findViewById<TextView>(R.id.tv_cityWeatherTemp)
        val cityLocation = view?.findViewById<TextView>(R.id.tv_cityWeatherLocation)

        Glide.with(requireContext()).load("https://openweathermap.org/img/wn/${data?.weatherModel?.currentConditions?.weather?.get(0)?.icon}@4x.png").error(R.drawable.error_icon).into(cityWeatherIcon!!)

        cityWeatherDesc?.text = data.weatherModel?.currentConditions?.weather?.get(0)?.desc?.replaceFirstChar { it.uppercase() }
        cityWeatherTemp?.text = round(data.weatherModel?.currentConditions?.temp!!).toString()

        val builder = StringBuilder()
        builder.append(data.locationModel?.get(0)?.countryCode)
            .append(", ")
            .append(data.locationModel?.get(0)?.cityName)

        cityLocation?.text = builder.toString()
    }

}