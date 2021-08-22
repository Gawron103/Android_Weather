package com.example.weather.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.networking.PlacesApi
import com.example.weather.networking.PlacesRepository
import com.example.weather.networking.WeatherApi
import com.example.weather.networking.WeatherRepository
import com.example.weather.viewmodels.PlacesViewModel
import com.example.weather.viewmodels.ViewModelFactory
import com.example.weather.viewmodels.WeatherSharedViewModel
import com.example.weather.views.adapters.CitiesListAdapter
import com.example.weather.views.interfaces.Communicator

class CitiesMainWeatherFragment: Fragment() {

    private lateinit var communicator: Communicator
//    private lateinit var placesViewModel: PlacesViewModel
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
        val placesService = PlacesApi.getInstance()
        val weatherRepository = WeatherRepository(weatherService, placesService)

//        val placesRepository = PlacesRepository(placesService)

        citiesWeatherListAdapter = CitiesListAdapter(arrayListOf(), communicator)

        citiesWeatherViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(weatherRepository)
        ).get(WeatherSharedViewModel::class.java)

//        placesViewModel = ViewModelProvider(
//            requireActivity(),
//            ViewModelFactory(placesRepository)
//        ).get(PlacesViewModel::class.java)

        val citiesList = requireView().findViewById<RecyclerView>(R.id.rv_citiesList).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = citiesWeatherListAdapter
        }

        citiesWeatherViewModel.refresh(mockCities)
//        placesViewModel.refresh(mockCities)

        observeWeatherViewModel()
//        observePlacesViewModel()

        val retryBtn = view?.findViewById<Button>(R.id.btn_retry)
        retryBtn?.setOnClickListener {
            Log.d(TAG, "Retry btn clicked")
            citiesWeatherViewModel.refresh(mockCities)
        }
    }

    private fun observeWeatherViewModel() {
        citiesWeatherViewModel.testModel.observe(viewLifecycleOwner, Observer {
            citiesWeatherListAdapter.updateCities(it)
        })

        citiesWeatherViewModel.weatherLoadError.observe(viewLifecycleOwner, Observer { isError ->
            view?.findViewById<TextView>(R.id.tv_errorLoad)?.visibility = if(isError) View.VISIBLE else View.GONE

            if(isError) {
                view?.findViewById<Button>(R.id.btn_retry)?.visibility = View.VISIBLE
                view?.findViewById<Button>(R.id.btn_add)?.visibility = View.GONE
            }
        })

        citiesWeatherViewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            isLoading?.let {
                view?.findViewById<ProgressBar>(R.id.pb_loading)?.visibility = if(isLoading) View.VISIBLE else View.GONE

                if(isLoading) {
                    view?.findViewById<TextView>(R.id.tv_errorLoad)?.visibility = View.GONE
                    view?.findViewById<Button>(R.id.btn_retry)?.visibility = View.GONE
                }
            }
        })



//        citiesWeatherViewModel.loading.observe(viewLifecycleOwner, Observer { isloading ->
//            view?.findViewById<ProgressBar>(R.id.pb_loading)?.visibility = if(isloading) View.VISIBLE else View.GONE
//        })

//        citiesWeatherViewModel.weatherLoadError.observe(viewLifecycleOwner, Observer {
//            view?.findViewById<TextView>(R.id.tv_errorLoad)?.visibility = if(it) View.VISIBLE else View.GONE
//            view?.findViewById<ProgressBar>(R.id.pb_loading)?.visibility = if(it) View.VISIBLE else View.GONE
//        })
    }

//    private fun observePlacesViewModel() {
//        placesViewModel.places.observe(viewLifecycleOwner, Observer {
//
//        })
//    }
}