package com.example.weather.viewmodels

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.models.TestModel
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

    // Model for city name
    val location = MutableLiveData<LocationModel>()

    // Model for weather
    val weather = MutableLiveData<WeatherModel>()

    // Says if there is an error when loading the data
    val weatherLoadError = MutableLiveData<Boolean>()

    // Says is the view model is in the process of loading the data
    val loading = MutableLiveData<Boolean>()

    var testModel = MediatorLiveData<List<TestModel>>()

    fun refresh(citiesName: List<String>){
        loading.value = true

        viewModelScope.launch {
            val testModels = mutableListOf<TestModel>()

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
                        weatherLoadError.value = false
                        Log.d(TAG, "Weather response: ${weatherResponse.body()}")

                        /* test for new design */
                        testModels.add(TestModel(weather.value, location.value))
//                    val mergeResult = mergeModelsToTestModel(location, weather)
//                        testModel.addSource(weather) { value ->
//                            testModel.value?.plus(mergeModelsToTestModel(location, weather))
//                        }
//                        testModel.addSource(location) { value ->
//                            testModel.value?.plus(mergeModelsToTestModel(location, weather))
//                        }

                    } else {
                        weatherLoadError.value = true
                        Log.d(TAG, "Response failed")
                    }
                }
            }

            if (testModels.isNotEmpty()) {
                testModel.value = testModels
            }
        }

        loading.value = false
    }

    /* test for new design */
//    private fun mergeModelsToTestModel(
//        location: MutableLiveData<LocationModel>,
//        weather: MutableLiveData<WeatherModel>
//    ): TestModel {
//        val locationData = location.value
//        val weatherData = weather.value
//
//        return TestModel(weatherModel = weatherData, locationModel = locationData)
//    }

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
