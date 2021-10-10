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

    private val args: DetailedWeatherForCityFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailedWeatherForCityBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val weatherModel = args.weatherForCity
        val cityName = args.cityName
        val countryCode = args.countryCode

        binding.rvForecast.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = DetailedWeatherAdapter(weatherModel?.dailyConditions!!, context)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_FromDetailedWeatherToCitiesList)
        }

        updateUI(weatherModel, cityName, countryCode)
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