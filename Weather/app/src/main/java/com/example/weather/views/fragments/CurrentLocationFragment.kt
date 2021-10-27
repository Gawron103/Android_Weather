package com.example.weather.views.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weather.BuildConfig
import com.example.weather.R
import com.example.weather.databinding.FragmentCurrentLocationBinding
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
    private lateinit var _locationRequest: LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val placesService = PlacesApi.getInstance()
        val weatherService = WeatherApi.getInstance()
        val weatherRepository = WeatherRepository(weatherService, placesService)

        _viewModel = ViewModelProvider(
            this,
            CurrentCityDataViewModelFactory(weatherRepository)
        ).get(CurrentCityDataViewModel::class.java)

        _fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        _locationRequest = LocationRequest.create()
            .apply {
                interval = 4000
                fastestInterval = 2000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                smallestDisplacement = 100000F
            }

        getLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrentLocationBinding.inflate(inflater, container, false)

        binding.rvForecast.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = CurrentLocationForecastAdapter(arrayListOf(), requireContext(), this)
        }.also {
            _forecastAdapter = it.adapter as CurrentLocationForecastAdapter
        }

        binding.slCurrentLocation.setOnRefreshListener {
            getLocation()
            binding.slCurrentLocation.isRefreshing = false
        }

        binding.cardView2.visibility = View.GONE
        binding.rvForecast.visibility = View.GONE

        observeViewModel()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        requestNewLocationData()
    }

    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
    }

    private fun observeViewModel() {
        _viewModel.isLoadingWeather.observe(requireActivity(), { isLoading ->
            binding.pbLoadingWeatherForLocation.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.cardView2.visibility = if (isLoading) View.GONE else View.VISIBLE
            binding.rvForecast.visibility = if (isLoading) View.GONE else View.VISIBLE
        })

        _viewModel.cityModel.observe(requireActivity(), { model ->
            _forecastAdapter.updateForecast(model.weatherModel?.dailyConditions!!)
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

                _viewModel.refresh(location.latitude, location.longitude)
            } ?: requestNewLocationData()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val request = LocationSettingsRequest.Builder()
            .addLocationRequest(_locationRequest).build()

        val client = LocationServices.getSettingsClient(requireContext())
        client.checkLocationSettings(request).addOnSuccessListener {
            startLocationUpdates()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        _fusedLocationProviderClient.requestLocationUpdates(_locationRequest, locationCallback, Looper.myLooper()!!)
    }

    private fun stopLocationUpdates() {
        _fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private val locationCallback = object: LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            _viewModel.refresh(location.latitude, location.longitude)
        }
    }

}