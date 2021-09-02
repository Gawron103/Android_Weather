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
import androidx.lifecycle.MutableLiveData
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
import com.example.weather.networking.WeatherRepository
import com.example.weather.repositories.CityRepository
import com.example.weather.viewmodels.*
import com.example.weather.views.adapters.CitiesListAdapter
import com.example.weather.views.interfaces.Communicator
import com.example.weather.views.interfaces.DatabaseCommunicator

class CitiesMainWeatherFragment: Fragment(), DatabaseCommunicator {

    private lateinit var communicator: Communicator
//    private lateinit var placesViewModel: PlacesViewModel
    private lateinit var cityViewModel: CityViewModel
    private lateinit var citiesWeatherViewModel: WeatherSharedViewModel
    private lateinit var citiesWeatherListAdapter: CitiesListAdapter
//    private val mockCities = listOf("Szczecin", "London", "Barcelona", "Rome", "Tokyo", "Berlin")

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

        val weatherService = WeatherApi.getInstance()
        val placesService = PlacesApi.getInstance()
        val weatherRepository = WeatherRepository(weatherService, placesService)

//        val placesRepository = PlacesRepository(placesService)

        citiesWeatherListAdapter = CitiesListAdapter(arrayListOf(), communicator, this)

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

//        citiesWeatherViewModel.refresh(mockCities)
//        val citiesNames = mutableListOf<String>()
//        cityViewModel.cities.value?.forEach { citiesNames.add(it.name) }
//        citiesWeatherViewModel.refresh(cityViewModel.cities.value!!)
//        placesViewModel.refresh(mockCities)

        val swipeRefreshLayout = view?.findViewById<SwipeRefreshLayout>(R.id.sr_CitiesList)
        swipeRefreshLayout?.setOnRefreshListener {
//            cityViewModel.refresh()
            Log.d(TAG, "Refresh view")
            swipeRefreshLayout.isRefreshing = false
        }

        observeDatabase()
        observeWeatherViewModel()
//        observePlacesViewModel()

//        cityViewModel.refresh()

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
    }

    private fun observeWeatherViewModel() {
        citiesWeatherViewModel.testModel.observe(viewLifecycleOwner, Observer {
            if (true == it?.isEmpty()) {
                // display text here that there is no cities added
                citiesWeatherListAdapter.updateCities(listOf())
                view?.findViewById<Button>(R.id.btn_retry)?.visibility = View.GONE
                view?.findViewById<TextView>(R.id.tv_noCities)?.visibility = View.VISIBLE
            }
            else {
                citiesWeatherListAdapter.updateCities(it)
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

        citiesWeatherViewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            isLoading?.let {
                view?.findViewById<ProgressBar>(R.id.pb_loading)?.visibility = if(isLoading) View.VISIBLE else View.GONE

                if(isLoading) {
                    view?.findViewById<TextView>(R.id.tv_errorLoad)?.visibility = View.GONE
                    view?.findViewById<Button>(R.id.btn_retry)?.visibility = View.GONE
                    view?.findViewById<TextView>(R.id.tv_noCities)?.visibility = View.GONE
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

//    private fun observeDatabase() {
//        cityViewModel.cities.observe(viewLifecycleOwner, object: Observer<List<City>> {
//            override fun onChanged(t: List<City>?) {
////                Log.d(TAG, "Cities database changed")
////                Log.d(TAG, "Database: ${t!!}")
//
////                val citiesNames = mutableListOf<String>()
////                t!!.forEach { citiesNames.add(it.name) }
//                citiesWeatherViewModel.refresh(t!!)
//
//                Log.d(TAG, "Cities in db: ${cityViewModel.cities.value!!.size}")
//            }
//        })
//    }

    private fun observeDatabase() {
        cityViewModel.cities.observe(viewLifecycleOwner, Observer {
            val isNull = if (null == it ) "YES" else "NO"
            Log.d(TAG, "Is it null? $isNull")
            citiesWeatherViewModel.refresh(it!!)
            Log.d(TAG, "CitiesMainWeatherFragment::observeDatabase triggered")
            Log.d(TAG, "Cities in db: ${cityViewModel.cities.value!!.size}")
        })
    }

    override fun addCity(city: City) {
        cityViewModel.insert(city)

        // need to refresh
//        cityViewModel.refresh()
    }

    override fun deleteCity(city: City) {
        cityViewModel.remove(city)

        // need to refresh
//        cityViewModel.refresh()
    }
}