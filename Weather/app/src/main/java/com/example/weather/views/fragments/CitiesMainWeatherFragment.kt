package com.example.weather.views.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
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
import com.example.weather.viewmodels.*
import com.example.weather.views.adapters.CitiesListAdapter
import com.example.weather.views.interfaces.Communicator
import com.example.weather.views.interfaces.DatabaseCommunicator
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY

class CitiesMainWeatherFragment: Fragment(), DatabaseCommunicator {

    private lateinit var communicator: Communicator
    private val addCityViewModel: AddCityViewModel by activityViewModels()
    private lateinit var citiesWeatherViewModel: WeatherForCityViewModel
    private lateinit var citiesWeatherListAdapter: CitiesListAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        val TAG = CitiesMainWeatherFragment::class.java.simpleName
        var callCounter = 0
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
        val placesService = PlacesApi.getInstance()
        val weatherService = WeatherApi.getInstance()
        val weatherRepository = WeatherRepository(weatherService, placesService, cityDao)

        citiesWeatherListAdapter = CitiesListAdapter(arrayListOf(), communicator, this)

        citiesWeatherViewModel = ViewModelProvider(
            requireActivity(),
            WeatherForCityViewModelFactory(weatherRepository)
        ).get(WeatherForCityViewModel::class.java)

        val citiesList = requireView().findViewById<RecyclerView>(R.id.rv_citiesList).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = citiesWeatherListAdapter
        }

        val swipeRefreshLayout = view?.findViewById<SwipeRefreshLayout>(R.id.sr_CitiesList)
        swipeRefreshLayout?.setOnRefreshListener {
            citiesWeatherViewModel.refresh()
            getCoordsForCurrentLocation()
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
            communicator.pushFragment(AddCityFragment(), AddCityFragment.TAG)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        observeWeatherViewModel()
        observeAddCityName()

        getCoordsForCurrentLocation()
    }

    private fun observeWeatherViewModel() {
        citiesWeatherViewModel.citiesLiveData.observe(viewLifecycleOwner, Observer {
            callCounter += 1
            Log.d(TAG, "citiesLiveData observer triggered")
            Log.d(TAG, "citiesLiveData has been triggered $callCounter times")
            if (true == it?.isEmpty()) {
                Log.d(TAG, "List is empty")
                // display text here that there is no cities added
                citiesWeatherListAdapter.updateCities(listOf())
                view?.findViewById<TextView>(R.id.tv_noCities)?.visibility = View.VISIBLE
            }
            else {
                Log.d(TAG, "Something is in list")
                citiesWeatherListAdapter.updateCities(it)
                view?.findViewById<TextView>(R.id.tv_noCities)?.visibility = View.GONE
            }

            view?.findViewById<Button>(R.id.btn_retry)?.visibility = View.GONE
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
                view?.findViewById<Button>(R.id.btn_add)?.visibility = if (isLoading) View.GONE else View.VISIBLE

                if(isLoading) {
                    view?.findViewById<TextView>(R.id.tv_errorLoad)?.visibility = View.GONE
                    view?.findViewById<Button>(R.id.btn_retry)?.visibility = View.GONE
                    view?.findViewById<TextView>(R.id.tv_noCities)?.visibility = View.GONE
                }
            }
        })
    }

    private fun observeAddCityName() {
        addCityViewModel.cityName.observe(viewLifecycleOwner, Observer { name ->
            Log.d(TAG, "New city to add: $name")
            if (name.isNotEmpty()) {
                citiesWeatherViewModel.addCity(City(0, name))
            }
        })
    }

    private fun getCoordsForCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnCompleteListener { task ->
                var location: Location? = task.result

                if (null == location) {
                    requestNewLocationData()
                }
                else {
                    Log.d(TAG, "GettingCoordsForCurrentLocation Lat: ${location.latitude}")
                    Log.d(TAG, "GettingCoordsForCurrentLocation Lon: ${location.longitude}")
                    citiesWeatherViewModel.addCurrentCity(
                        location.latitude,
                        location.longitude
                    )
                }
            }

    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val locationRequest = LocationRequest.create().apply {
            priority = PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 1
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location = locationResult.lastLocation
            Log.d(TAG, "LocationCallback lat: ${lastLocation.latitude}")
            Log.d(TAG, "LocationCallback lon: ${lastLocation.longitude}")
            citiesWeatherViewModel.addCurrentCity(
                lastLocation.latitude,
                lastLocation.longitude
            )
        }
    }

    override fun addCity(city: City) {
        citiesWeatherViewModel.addCity(city)
    }

    override fun deleteCity(city: City) {
        citiesWeatherViewModel.removeCity(city)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}