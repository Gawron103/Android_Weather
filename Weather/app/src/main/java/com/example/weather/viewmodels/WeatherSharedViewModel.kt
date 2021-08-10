package com.example.weather.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.models.current_weather_model.WeatherModel
import com.example.weather.models.location_model.LocationModel
import com.example.weather.networking.WeatherRepository
import kotlinx.coroutines.*
import retrofit2.Response

class WeatherSharedViewModel constructor(private val repository: WeatherRepository): ViewModel() {

    // Debug tag
    private val TAG = "WeatherSharedModel"

    // API key
    private val API_KEY = "170e393e3a266f65c3472fa8397a3f0f"

    // Model for weather
    val weather = MutableLiveData<WeatherModel>()

    // Says if there is an error when loading the data
    val weatherLoadError = MutableLiveData<Boolean>()

    // Says is the view model is in the process of loading the data
    val loading = MutableLiveData<Boolean>()


    fun refresh() {
        loading.value = true

        viewModelScope.launch {
            val locationResponse = fetchLocation("Szczecin")

            if(locationResponse.isSuccessful) {
                Log.d(TAG, "Location response: ${locationResponse.body()}")
                val lat = locationResponse.body()?.get(0)?.lat
                val lon = locationResponse.body()?.get(0)?.lon

                val weatherResponse = fetchWeather(lat!!, lon!!)

                if(weatherResponse.isSuccessful) {
                    weather.value = weatherResponse.body()
                    weatherLoadError.value = false
                    Log.d(TAG, "Weather response: ${weatherResponse.body()}")
                }
                else {
                    weatherLoadError.value = true
                    Log.d(TAG, "Response failed")
                }
            }
        }

        loading.value = false
    }

    private suspend fun fetchWeather(lat: Double, lon: Double): Response<WeatherModel> {
                return repository.getWeather(
                    lat,
                    lon,
                    "minutely,hourly,alerts",
                    "metric",
                    API_KEY
                )
    }

    private suspend fun fetchLocation(city: String): Response<LocationModel> {
        return repository.getCoordinates(
            city,
            API_KEY
        )
    }
}
