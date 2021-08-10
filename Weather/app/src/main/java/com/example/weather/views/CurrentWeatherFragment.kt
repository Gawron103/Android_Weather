package com.example.weather.views

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.networking.WeatherApi
import com.example.weather.networking.WeatherRepository
import com.example.weather.viewmodels.ViewModelFactory
import com.example.weather.viewmodels.WeatherSharedViewModel

class CurrentWeatherFragment : Fragment() {

    companion object {
        fun newInstance() = CurrentWeatherFragment()
    }

    private val TAG = "CurrentWeatherFragment"

    private lateinit var weatherViewModel: WeatherSharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.current_weather_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val weatherService = WeatherApi.getInstance()
        val repository = WeatherRepository(weatherService)

        weatherViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(repository)).get(WeatherSharedViewModel::class.java)

        refreshModels()

        observeWeatherViewModel()
    }

    private fun refreshModels() {
        weatherViewModel.refresh()
    }

    private fun observeWeatherViewModel() {
        weatherViewModel.weather.observe(viewLifecycleOwner, Observer { weather ->
//            weather?.let { currentWeatherAdapter.updateWeather(it) }
            // Update UI here
        })

        weatherViewModel.weatherLoadError.observe(viewLifecycleOwner, Observer { isError ->
            isError?.let {
                view?.findViewById<TextView>(R.id.fetch_error)?.visibility = if(it) View.VISIBLE else View.GONE
            }
        })

        weatherViewModel.weatherLoadError.observe(viewLifecycleOwner, Observer { isLoading ->
            isLoading?.let {
                view?.findViewById<ProgressBar>(R.id.view_load)?.visibility = if(it) View.VISIBLE else View.GONE

                if(it) {
                    view?.findViewById<TextView>(R.id.fetch_error)?.visibility = View.GONE
                    view?.findViewById<RecyclerView>(R.id.currentWeatherList)?.visibility = View.GONE
                }
            }
        })
    }
}