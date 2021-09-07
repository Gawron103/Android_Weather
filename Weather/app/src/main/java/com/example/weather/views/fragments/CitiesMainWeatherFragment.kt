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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weather.R
import com.example.weather.db.City
import com.example.weather.db.CityDatabase
import com.example.weather.networking.PlacesApi
import com.example.weather.networking.WeatherApi
import com.example.weather.repositories.WeatherRepository
import com.example.weather.repositories.CityRepository
//import com.example.weather.repositories.PlacesRepository
import com.example.weather.viewmodels.*
import com.example.weather.views.adapters.CitiesListAdapter
import com.example.weather.views.interfaces.Communicator
import com.example.weather.views.interfaces.DatabaseCommunicator

class CitiesMainWeatherFragment: Fragment(), DatabaseCommunicator {

    private lateinit var communicator: Communicator
    private lateinit var cityViewModel: CityViewModel
    private lateinit var citiesWeatherViewModel: WeatherSharedViewModel
    private lateinit var citiesWeatherListAdapter: CitiesListAdapter

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

        val cityDao = CityDatabase.getInstance(requireContext()).cityDAO
        val cityRepository = CityRepository(cityDao)
        cityViewModel = ViewModelProvider(
            requireActivity(),
            CityViewModelFactory(cityRepository)
        ).get(CityViewModel::class.java)

        val placesService = PlacesApi.getInstance()
        val weatherService = WeatherApi.getInstance()
        val weatherRepository = WeatherRepository(weatherService, placesService)

        citiesWeatherListAdapter = CitiesListAdapter(arrayListOf(), communicator, this)

        citiesWeatherViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(weatherRepository)
        ).get(WeatherSharedViewModel::class.java)

        val citiesList = requireView().findViewById<RecyclerView>(R.id.rv_citiesList).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = citiesWeatherListAdapter
        }

        val swipeRefreshLayout = view?.findViewById<SwipeRefreshLayout>(R.id.sr_CitiesList)
        swipeRefreshLayout?.setOnRefreshListener {
            Log.d(TAG, "Refresh view")
            citiesWeatherViewModel.refresh(cityViewModel.cities.value!!)
            swipeRefreshLayout.isRefreshing = false
        }

        val retryBtn = view?.findViewById<Button>(R.id.btn_retry)
        retryBtn?.setOnClickListener {
            Log.d(TAG, "Retry btn clicked")
//            citiesWeatherViewModel.refresh(cityViewModel.cities.value!!)
        }

        val addBtn = view?.findViewById<Button>(R.id.btn_add)
        addBtn?.setOnClickListener {
            Log.d(TAG, "Add btn clicked")
            communicator.pushFragment(AddCityFragment(this), AddCityFragment.TAG)
        }

        observeDatabase()
        observeWeatherViewModel()
    }

    private fun observeWeatherViewModel() {
        citiesWeatherViewModel.citiesLiveData.observe(viewLifecycleOwner, Observer {
            if (true == it?.isEmpty()) {
                Log.d(TAG, "List is empty")
                // display text here that there is no cities added
                citiesWeatherListAdapter.updateCities(listOf())
                view?.findViewById<Button>(R.id.btn_retry)?.visibility = View.GONE
                view?.findViewById<TextView>(R.id.tv_noCities)?.visibility = View.VISIBLE
            }
            else {
                Log.d(TAG, "Something is in list")
                citiesWeatherListAdapter.updateCities(it)
//                placesViewModel.refresh()
                view?.findViewById<TextView>(R.id.tv_noCities)?.visibility = View.GONE
                view?.findViewById<Button>(R.id.btn_retry)?.visibility = View.GONE
            }
        })

        citiesWeatherViewModel.weatherLoadError.observe(viewLifecycleOwner, Observer { isError ->
            view?.findViewById<TextView>(R.id.tv_errorLoad)?.visibility = if(isError) View.VISIBLE else View.GONE

            if(isError) {
                view?.findViewById<Button>(R.id.btn_retry)?.visibility = View.VISIBLE
                view?.findViewById<Button>(R.id.btn_add)?.visibility = View.GONE
                view?.findViewById<TextView>(R.id.tv_noCities)?.visibility = View.GONE
            }
        })

        citiesWeatherViewModel.weatherLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            isLoading?.let {
                view?.findViewById<ProgressBar>(R.id.pb_loading)?.visibility = if(isLoading) View.VISIBLE else View.GONE

                if(isLoading) {
                    view?.findViewById<TextView>(R.id.tv_errorLoad)?.visibility = View.GONE
                    view?.findViewById<Button>(R.id.btn_retry)?.visibility = View.GONE
                    view?.findViewById<TextView>(R.id.tv_noCities)?.visibility = View.GONE
                }
            }
        })
    }

    private fun observeDatabase() {
        cityViewModel.cities.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                Log.d(TAG, "Database is empty")
                citiesWeatherViewModel.refresh(listOf())
            }
            else {
                Log.d(TAG, "Database is not empty. It has ${it.size} items")
                citiesWeatherViewModel.refresh(it!!)
            }
        })
    }

    override fun addCity(city: City) {
        citiesWeatherViewModel.addCity(city)
        cityViewModel.insert(city)
    }

    override fun deleteCity(city: City) {
        cityViewModel.remove(city)
    }
}