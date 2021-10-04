package com.example.weather.views.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weather.R
import com.example.weather.db.City
import com.example.weather.db.CityDatabase
import com.example.weather.models.CityModel
import com.example.weather.networking.PlacesApi
import com.example.weather.networking.WeatherApi
import com.example.weather.repositories.WeatherRepository
import com.example.weather.viewmodels.WeatherForCityViewModel
import com.example.weather.viewmodels.WeatherForCityViewModelFactory
import com.example.weather.views.adapters.CitiesListAdapter
import com.google.android.gms.location.*
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var citiesWeatherViewModel: WeatherForCityViewModel
    private lateinit var citiesWeatherListAdapter: CitiesListAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val value = result.data?.getStringExtra("NewCity")
            addCity(City(0, value!!))
            Toast.makeText(this, "City added", Toast.LENGTH_LONG)
        }
        else {
            Toast.makeText(this, "Wrong input", Toast.LENGTH_LONG)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cityDao = CityDatabase.getInstance(this).cityDAO
        val placesService = PlacesApi.getInstance()
        val weatherService = WeatherApi.getInstance()
        val weatherRepository = WeatherRepository(weatherService, placesService, cityDao)

        citiesWeatherListAdapter = CitiesListAdapter(arrayListOf(), ::deleteCity)

        citiesWeatherViewModel = ViewModelProvider(
            this,
            WeatherForCityViewModelFactory(weatherRepository)
        ).get(WeatherForCityViewModel::class.java)

        findViewById<RecyclerView>(R.id.rv_citiesList).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = citiesWeatherListAdapter
        }

        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.sr_CitiesList)
        swipeRefreshLayout?.setOnRefreshListener {
            citiesWeatherViewModel.refresh()
//            getCoordsForCurrentLocation()
            swipeRefreshLayout.isRefreshing = false
        }

        val retryBtn = findViewById<Button>(R.id.btn_retry)
        retryBtn?.setOnClickListener {
//            Log.d(CitiesMainWeatherFragment.TAG, "Retry btn clicked")
//            citiesWeatherViewModel.refresh(cityViewModel.cities.value!!)
        }

        val addBtn = findViewById<FloatingActionButton>(R.id.btn_add)
        addBtn?.setOnClickListener {
            val intent = Intent(this, AddCityActivity::class.java)
            resultLauncher.launch(intent)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        citiesWeatherViewModel.refresh()

        observeWeatherViewModel()

//        getCoordsForCurrentLocation()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "Activity resumed")
    }

    private fun observeWeatherViewModel() {
        citiesWeatherViewModel.citiesLiveData.observe(this, Observer {
            if (true == it?.isEmpty()) {
                // display text here that there is no cities added
                citiesWeatherListAdapter.updateCities(listOf())
                findViewById<TextView>(R.id.tv_noCities)?.visibility = View.VISIBLE
            }
            else {
                citiesWeatherListAdapter.updateCities(it)
                findViewById<TextView>(R.id.tv_noCities)?.visibility = View.GONE
            }

            findViewById<Button>(R.id.btn_retry)?.visibility = View.GONE
        })

        citiesWeatherViewModel.weatherLoadError.observe(this, Observer { isError ->
            findViewById<TextView>(R.id.tv_errorLoad)?.visibility = if(isError) View.VISIBLE else View.GONE

            if(isError) {
                findViewById<Button>(R.id.btn_retry)?.visibility = View.VISIBLE
                findViewById<FloatingActionButton>(R.id.btn_add)?.visibility = View.GONE
                findViewById<TextView>(R.id.tv_noCities)?.visibility = View.GONE
            }
        })

        citiesWeatherViewModel.weatherLoading.observe(this, Observer { isLoading ->
            isLoading?.let {
                findViewById<ProgressBar>(R.id.pb_loading)?.visibility = if(isLoading) View.VISIBLE else View.GONE
                findViewById<FloatingActionButton>(R.id.btn_add)?.visibility = if (isLoading) View.GONE else View.VISIBLE

                if(isLoading) {
                    findViewById<TextView>(R.id.tv_errorLoad)?.visibility = View.GONE
                    findViewById<Button>(R.id.btn_retry)?.visibility = View.GONE
                    findViewById<TextView>(R.id.tv_noCities)?.visibility = View.GONE
                }
            }
        })

        citiesWeatherViewModel.cityExists.observe(this, Observer { cityExists ->
            if (cityExists) {
                Toast.makeText(this, "City already exists", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun addCity(city: City) {
        citiesWeatherViewModel.addCity(city)
    }

    private fun deleteCity(cityModel: CityModel) {
        citiesWeatherViewModel.removeCity(cityModel)
    }

    private fun getCoordsForCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
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
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 1
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location = locationResult.lastLocation
            citiesWeatherViewModel.addCurrentCity(
                lastLocation.latitude,
                lastLocation.longitude
            )
        }
    }

}