package com.example.weather.viewmodels

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.BuildConfig
import com.example.weather.models.TestModel
import com.example.weather.models.current_weather_model.WeatherModel
import com.example.weather.models.location_model.LocationModel
import com.example.weather.models.places_model.PlacesModel
import com.example.weather.networking.PlacesRepository
import com.example.weather.networking.WeatherRepository
import kotlinx.coroutines.*
import retrofit2.Response

class WeatherSharedViewModel constructor(
    private val weatherRepository: WeatherRepository
//    private val placesRepository: PlacesRepository
): ViewModel() {

    // Debug tag
    private val TAG = "WeatherSharedModel"

    // Model for city name
    val location = MutableLiveData<LocationModel>()

    // Model for weather
    val weather = MutableLiveData<WeatherModel>()

    // Model for places
    val places = MutableLiveData<PlacesModel>()

    // Says if there is an error when loading the data
    val weatherLoadError = MutableLiveData<Boolean>()

    // Says is the view model is in the process of loading the data
    val loading = MutableLiveData<Boolean>()

    var testModel = MediatorLiveData<List<TestModel>>()

    fun refresh(citiesName: List<String>){
        viewModelScope.launch {
            val testModels = mutableListOf<TestModel>()

            try {
                loading.postValue(true)

                for (cityName in citiesName) {
                    val locationResponse = fetchLocation(cityName)

                    if (locationResponse.isSuccessful) {
                        Log.d(TAG, "Location response: ${locationResponse.body()}")

                        val lat = locationResponse.body()?.get(0)?.lat
                        val lon = locationResponse.body()?.get(0)?.lon

                        location.value = locationResponse.body()

                        val weatherResponse = fetchWeather(lat!!, lon!!)

                        if (weatherResponse.isSuccessful) {
                            weather.value = weatherResponse.body()
                            weatherLoadError.postValue(false)
                            Log.d(TAG, "Weather response: ${weatherResponse.body()}")

                            val placeRefResponse = fetchPlaceId(cityName)

                            if (placeRefResponse.isSuccessful) {
                                places.value = placeRefResponse.body()

                                testModels.add(
                                    TestModel(
                                        weather.value,
                                        location.value,
                                        places.value
                                    )
                                )
                            }

//                        testModels.add(TestModel(weather.value, location.value))
                        } else {
                            weatherLoadError.postValue(true)
                            Log.d(TAG, "Response failed")
                            break
                        }
                    } else {
                        weatherLoadError.postValue(true)
                        break
                    }
                }
            }
            catch (e: Exception) {
                weatherLoadError.value = true
            }

            if (testModels.isNotEmpty()) {
                testModel.value = testModels
            }

            loading.postValue(false)
        }
    }

    private suspend fun fetchWeather(lat: Double, lon: Double): Response<WeatherModel> {
                return weatherRepository.getWeather(
                    lat,
                    lon,
                    "minutely,hourly,alerts",
                    "metric",
                    BuildConfig.WEATHER_API_KEY
                )
    }

    private suspend fun fetchLocation(city: String): Response<LocationModel> {
        return weatherRepository.getCoordinates(
            city,
            BuildConfig.WEATHER_API_KEY
        )
    }

    private suspend fun fetchPlaceId(cityName: String): Response<PlacesModel> {
        return weatherRepository.getPlaceId(
            cityName,
            BuildConfig.PLACES_API_KEY
        )
    }
}
