package com.example.weather.views.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weather.BuildConfig
import com.example.weather.R
import com.example.weather.databinding.FragmentCurrentLocationBinding
import com.example.weather.db.DbConfig
import com.example.weather.networking.PlacesApi
import com.example.weather.networking.WeatherApi
import com.example.weather.repositories.WeatherRepository
import com.example.weather.viewmodels.CurrentCityDataViewModel
import com.example.weather.viewmodels.CurrentCityDataViewModelFactory
import com.example.weather.views.adapters.CurrentLocationForecastAdapter
import com.google.android.gms.location.*
import kotlin.math.round

class CurrentLocationFragment : Fragment() {

    private val TAG = "CurrentLocationFragment"

    private var _binding: FragmentCurrentLocationBinding? = null
    private val binding get() = _binding!!

    private lateinit var _viewModel: CurrentCityDataViewModel
    private lateinit var _forecastAdapter: CurrentLocationForecastAdapter

    private lateinit var _fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val placesService = PlacesApi.getInstance()
        val weatherService = WeatherApi.getInstance()
        val weatherRepository = WeatherRepository(weatherService, placesService, DbConfig.provideRealmConfig())

        _forecastAdapter = CurrentLocationForecastAdapter(arrayListOf(), requireContext())

        _viewModel = ViewModelProvider(
            this,
            CurrentCityDataViewModelFactory(weatherRepository)
        ).get(CurrentCityDataViewModel::class.java)

//        val locationUtils = LocationUtils()
//        locationUtils.getLocation(requireContext())

        _fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        getLocation()
//        _viewModel.refresh()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrentLocationBinding.inflate(inflater, container, false)

        binding.rvForecast.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = _forecastAdapter
        }

        binding.slCurrentLocation.setOnRefreshListener {
            Log.d(TAG, "Refreshing current weather")
//            _viewModel.refresh()
            binding.slCurrentLocation.isRefreshing = false
        }

        observeViewModel()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        _viewModel.isLoading.observe(requireActivity(), Observer { isLoading ->
            binding.pbLoadingCurrentLocation.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        _viewModel.cityModel.observe(requireActivity(), Observer { model ->
            _forecastAdapter.updateForecast(model?.weatherModel?.dailyConditions!!)
            updateUI()
        })
    }

    private fun updateUI() {
        binding.tvLatitudeValue.text = _viewModel.getLatitude().toString()
        binding.tvLongitudeValue.text = _viewModel.getLongitude().toString()
        binding.tvTemperature.text = _viewModel.getTemperature()?.let { round(it).toString() }
        binding.tvWeatherDescription.text = _viewModel.getWeatherDesc()?.let {desc -> desc.replaceFirstChar { it.uppercase() } }
        binding.tvLocationName.text = _viewModel.getLocationName()
        Glide.with(this).load(
            "https://maps.googleapis.com/maps/api/place/photo?photoreference=${_viewModel.getPhotoRef()}&key=${BuildConfig.PLACES_API_KEY}&maxwidth=1980&maxheight=1200"
        ).error(R.drawable.error_icon).into(binding.ivLocationImage)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        _fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                Log.d(TAG, "Latitude: ${location.latitude.toString()}")
                Log.d(TAG, "Longitude: ${location.longitude.toString()}")
                _viewModel.refresh(location.latitude, location.longitude)
            } ?: requestNewLocationData()
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

        _fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        _fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    private val locationCallback = object: LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation = locationResult.lastLocation
            _viewModel.refresh(lastLocation.latitude, lastLocation.longitude)
        }
    }
}