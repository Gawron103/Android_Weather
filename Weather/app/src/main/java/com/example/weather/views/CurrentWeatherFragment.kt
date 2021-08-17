package com.example.weather.views

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.networking.WeatherApi
import com.example.weather.networking.WeatherRepository
import com.example.weather.utils.DateProvider
import com.example.weather.viewmodels.ViewModelFactory
import com.example.weather.viewmodels.WeatherSharedViewModel
import com.example.weather.views.adapters.CitiesListAdapter
import com.example.weather.views.adapters.ForecastListAdapter
import kotlin.math.round

class CurrentWeatherFragment : Fragment() {

    companion object {
        fun newInstance() = CurrentWeatherFragment()
    }

    private lateinit var weatherViewModel: WeatherSharedViewModel

    // This mock simulates X saved cities
    val mockCities = listOf("Szczecin", "London", "Barcelona", "Rome", "Tokyo", "Berlin")

    /* for new design */
    private val citiesListAdapter = CitiesListAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return inflater.inflate(R.layout.current_weather_fragment, container, false)
        return inflater.inflate(R.layout.cities_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val weatherService = WeatherApi.getInstance()
        val repository = WeatherRepository(weatherService)

        weatherViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(repository)).get(WeatherSharedViewModel::class.java)

        /* for new design */
        val citiesList = view?.findViewById<RecyclerView>(R.id.rv_citiesList)
        citiesList?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = citiesListAdapter
        }

//        refreshModels()
        weatherViewModel.refresh(mockCities)

        observeWeatherViewModel()
    }

//    private fun refreshModels() {
//        weatherViewModel.refresh()
//    }

    private fun observeWeatherViewModel() {
        weatherViewModel.testModel.observe(viewLifecycleOwner, Observer {
            citiesListAdapter.updateCities(it)
        })
    }

//    private fun observeWeatherViewModel() {
//        weatherViewModel.location.observe(viewLifecycleOwner, Observer { location ->
//            location?.let { updateLocationUI() }
//        })
//
//        weatherViewModel.weather.observe(viewLifecycleOwner, Observer { weather ->
////            weather?.let { currentWeatherAdapter.updateWeather(it) }
//            weather?.let { updateWeatherUI() }
//            // Update UI here
//        })
//
//        weatherViewModel.weatherLoadError.observe(viewLifecycleOwner, Observer { isError ->
//            isError?.let {
//                view?.findViewById<TextView>(R.id.fetch_error)?.visibility = if(it) View.VISIBLE else View.GONE
//            }
//        })
//
//        weatherViewModel.weatherLoadError.observe(viewLifecycleOwner, Observer { isLoading ->
//            isLoading?.let {
//                view?.findViewById<ProgressBar>(R.id.view_load)?.visibility = if(it) View.VISIBLE else View.GONE
//
//                if(it) {
//                    view?.findViewById<TextView>(R.id.view_load)?.visibility = View.GONE
//                    view?.findViewById<ProgressBar>(R.id.fetch_error)?.visibility = View.GONE
//                }
//            }
//        })
//    }

//    private fun updateLocationUI() {
//        val tvCityName = view?.findViewById<TextView>(R.id.tv_cityName)
//        val tvLocation = view?.findViewById<TextView>(R.id.tv_location)
//        val tvDate = view?.findViewById<TextView>(R.id.tv_date)
//
//        if (null != tvCityName) {
//            tvCityName.text = weatherViewModel.location.value?.get(0)?.cityName
//        }
//
//        if (null != tvLocation) {
//            tvLocation.text = weatherViewModel.location.value?.get(0)?.cityName
//                .plus(", ")
//                .plus(weatherViewModel.location.value?.get(0)?.countryCode)
//        }
//
//        if (null != tvDate) {
//            tvDate.text = DateProvider().getDate()
//        }
//    }
//
//    private fun updateWeatherUI() {
//        val ivWeatherIcon = view?.findViewById<ImageView>(R.id.iv_weatherIcon)
//        val tvTemperature = view?.findViewById<TextView>(R.id.tv_temperature)
//        val tvWeatherDesc = view?.findViewById<TextView>(R.id.tv_weatherDesc)
//
//        if (null != tvTemperature) {
//            tvTemperature.text = round(weatherViewModel.weather.value?.currentConditions?.temp!!).toString()
//        }
//
//        if (null != tvWeatherDesc) {
//            tvWeatherDesc.text = weatherViewModel.weather.value?.currentConditions?.weather?.get(0)?.desc?.replaceFirstChar { it.uppercase() }
//        }
//
//        if (null != ivWeatherIcon) {
//            Glide.with(view?.context!!).load("https://openweathermap.org/img/wn/${weatherViewModel.weather.value?.currentConditions?.weather?.get(0)?.icon}@4x.png").into(ivWeatherIcon)
//        }
//    }
}