package com.example.weather.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.networking.WeatherApi
import com.example.weather.networking.WeatherRepository
import com.example.weather.viewmodels.ViewModelFactory
import com.example.weather.viewmodels.WeatherSharedViewModel
import com.example.weather.views.adapters.CitiesListAdapter
import com.example.weather.views.interfaces.Communicator

class CitiesMainWeatherFragment: Fragment() {

    private lateinit var communicator: Communicator
    private lateinit var citiesWeatherViewModel: WeatherSharedViewModel
    private lateinit var citiesWeatherListAdapter: CitiesListAdapter
    private val mockCities = listOf("Szczecin", "London", "Barcelona", "Rome", "Tokyo", "Berlin")

    companion object {
        val TAG = CitiesMainWeatherFragment::class.java.simpleName
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "CitiesMainWeatherFragment destroyed")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        communicator = requireActivity() as Communicator
        return inflater.inflate(R.layout.fragment_cities_main_weather, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val weatherService = WeatherApi.getInstance()
        val weatherRepository = WeatherRepository(weatherService)

        citiesWeatherListAdapter = CitiesListAdapter(arrayListOf(), communicator)

        citiesWeatherViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(weatherRepository)
        ).get(WeatherSharedViewModel::class.java)

        val citiesList = requireView().findViewById<RecyclerView>(R.id.rv_citiesList).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = citiesWeatherListAdapter
        }

        citiesWeatherViewModel.refresh(mockCities)

        observeWeatherViewModel()
    }

    private fun observeWeatherViewModel() {
        citiesWeatherViewModel.testModel.observe(viewLifecycleOwner, Observer {
            citiesWeatherListAdapter.updateCities(it)
        })
    }
}