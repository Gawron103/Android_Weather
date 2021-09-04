package com.example.weather.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.example.weather.BuildConfig
import com.example.weather.db.City
import com.example.weather.models.WeatherForCityModel
import com.example.weather.models.current_weather_model.WeatherModel
import com.example.weather.models.location_model.LocationModel
import com.example.weather.models.places_model.PlacesModel
import com.example.weather.repositories.WeatherRepository
import kotlinx.coroutines.*
import retrofit2.Response

class WeatherSharedViewModel constructor(
    private val weatherRepository: WeatherRepository
): ViewModel() {

    // Debug tag
    private val TAG = "WeatherSharedModel"

    // Model for city name
//    val location = MutableLiveData<LocationModel>()

    // Model for weather
//    val weather = MutableLiveData<WeatherModel>()

    // Model for places
//    val places = MutableLiveData<PlacesModel>()

    // Says if there is an error when loading the data
    val weatherLoadError = MutableLiveData<Boolean>()

    // Says is the view model is in the process of loading the data
    val weatherLoading = MutableLiveData<Boolean>()

    var citiesData = MutableLiveData<List<WeatherForCityModel>>()

    fun refresh(citiesName: List<City>){
        viewModelScope.launch {
            // loading data starts
            weatherLoading.value = true

            if (citiesName.isNotEmpty()) {
                var errorOccurred = false

                val dataForCity = mutableListOf<WeatherForCityModel>()

                var locationModel: LocationModel? = null
                var weatherModel: WeatherModel? = null
                var placesModel: PlacesModel? = null

                for (city in citiesName) {
                    // get geo coords for wanted city
                    val locationResponse = fetchLocation(city.name)

                    if (locationResponse.isSuccessful) {
                        locationModel = locationResponse.body()
                    } else {
                        // cannot get geo coords, error
                        errorOccurred = true
                        break
                    }

                    // get weather data for wanted city
                    val weatherResponse = fetchWeather(
                        locationResponse.body()?.get(0)?.lat!!,
                        locationResponse.body()?.get(0)?.lon!!
                    )

                    if (weatherResponse.isSuccessful) {
                        weatherModel = weatherResponse.body()
                    } else {
                        // cannot get weather data
                        errorOccurred = true
                        break
                    }

                    // get reference to image of the city
                    val placesResponse = fetchPlaceId(city.name)

                    if (placesResponse.isSuccessful) {
                        placesModel = placesResponse.body()
                    } else {
                        // cannot get reference for place image
                        errorOccurred = true
                        break
                    }

                    dataForCity.add(
                        WeatherForCityModel(
                            city.id,
                            weatherModel,
                            locationModel,
                            placesModel
                        )
                    )
                }

                if (!errorOccurred) {
                    weatherLoadError.value = false
                    citiesData.value = dataForCity
                } else {
                    weatherLoadError.value = true
                    citiesData.value = listOf()
                }
            } else {
                citiesData.value = listOf()
                weatherLoading.value = false
                weatherLoadError.value = false
            }

            // loading data ends
            weatherLoading.value = false
        }







//        if (citiesName.isNotEmpty()) {
//            viewModelScope.launch {
//                val testModels = mutableListOf<WeatherForCityModel>()
//
//                try {
//                    weatherLoading.value = true
//
//                    for (cityName in citiesName) {
//                        val locationResponse = fetchLocation(cityName.name)
//
//                        if (locationResponse.isSuccessful) {
//                            Log.d(TAG, "Location response: ${locationResponse.body()}")
//
//                            val lat = locationResponse.body()?.get(0)?.lat
//                            val lon = locationResponse.body()?.get(0)?.lon
//
//                            location.value = locationResponse.body()
//
//                            val weatherResponse = fetchWeather(lat!!, lon!!)
//
//                            if (weatherResponse.isSuccessful) {
//                                weather.value = weatherResponse.body()
//                                weatherLoadError.postValue(false)
//                                Log.d(TAG, "Weather response: ${weatherResponse.body()}")
//
//                                val placeRefResponse = fetchPlaceId(cityName.name)
//
//                                if (placeRefResponse.isSuccessful) {
//                                    places.value = placeRefResponse.body()
//
//                                    testModels.add(
//                                        WeatherForCityModel(
//                                            cityName.id,
//                                            weather.value,
//                                            location.value,
//                                            places.value
//                                        )
//                                    )
//                                }
//
////                        testModels.add(TestModel(weather.value, location.value))
//                            } else {
//                                weatherLoadError.postValue(true)
//                                Log.d(TAG, "Response failed")
//                                break
//                            }
//                        } else {
//                            weatherLoadError.postValue(true)
//                            break
//                        }
//                    }
//                } catch (e: Exception) {
//                    weatherLoadError.value = true
//                }
//
//                if (testModels.isNotEmpty()) {
//                    citiesData.value = testModels
//                }
//
//                weatherLoading.value = false
//            }
//        }
//        else {
//            citiesData.value = listOf()
//            weatherLoading.value = false
//            weatherLoadError.value = false
//        }
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
