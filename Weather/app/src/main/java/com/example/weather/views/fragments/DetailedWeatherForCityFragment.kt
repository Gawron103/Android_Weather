package com.example.weather.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.databinding.FragmentDetailedWeatherForCityBinding
import com.example.weather.models.current_weather_model.WeatherModel
import com.example.weather.views.adapters.DetailedWeatherAdapter
import kotlin.math.round

class DetailedWeatherForCityFragment : Fragment() {

    private var _binding: FragmentDetailedWeatherForCityBinding? = null
    private val binding get() = _binding!!

    private lateinit var _weatherModel: WeatherModel
    private lateinit var _cityName: String
    private lateinit var _countryCode: String

    private val args: DetailedWeatherForCityFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _weatherModel = args.weatherForCity
        _cityName = args.cityName
        _countryCode = args.countryCode
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailedWeatherForCityBinding.inflate(inflater, container, false)

        binding.rvForecast.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = DetailedWeatherAdapter(_weatherModel.dailyConditions!!, context)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_FromDetailedWeatherToCitiesList)
        }

        updateUI()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUI() {
        Glide.with(this).load("https://openweathermap.org/img/wn/${_weatherModel.currentConditions?.weather?.get(0)?.icon}@4x.png").error(R.drawable.error_icon).into(binding.ivCityWeatherIcon)

        binding.tvCityWeatherDesc.text = _weatherModel.currentConditions?.weather?.get(0)?.desc?.replaceFirstChar { it.uppercase() }
        binding.tvCityWeatherTemp.text = round(_weatherModel.currentConditions?.temp!!).toString()

        val builder = StringBuilder()
            .append(_countryCode)
            .append(" ")
            .append(_cityName)

        binding.tvCityWeatherLocation.text = builder.toString()
    }

}