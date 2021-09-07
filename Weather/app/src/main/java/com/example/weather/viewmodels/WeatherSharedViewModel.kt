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

    // Says if there is an error when loading the data
    val weatherLoadError = MutableLiveData<Boolean>()

    // Says is the view model is in the process of loading the data
    val weatherLoading = MutableLiveData<Boolean>()

    var citiesLiveData = MutableLiveData<MutableList<WeatherForCityModel>>()

    private var citiesLists = mutableListOf<WeatherForCityModel>()

    fun refresh(citiesNames: List<City>) {
        viewModelScope.launch {
            weatherLoading.value = true

            citiesLists.clear()

            var eccorOccured = false

            for (city in citiesNames) {
                var locationModel: LocationModel? = null
                var weatherModel: WeatherModel? = null
                var placesModel: PlacesModel? = null

                locationModel = getLocationForCity(city.name)

                if (null != locationModel) {
                    weatherModel = getWeatherForCity(locationModel[0].lat!!, locationModel[0].lon!!)
                } else {
                    eccorOccured = true
                }

                if (null != weatherModel) {
                    placesModel = getPlaceIdForCity(city.name)
                } else {
                    eccorOccured = true
                }

                if (null != placesModel) {
                    citiesLists.add(
                        WeatherForCityModel(
                            city.id,
                            weatherModel,
                            locationModel,
                            placesModel
                        )
                    )
                    Log.d(TAG, "city added in weathersharedviewmodel")
                } else {
                    eccorOccured = true
                }
            }

            if (!eccorOccured) {
                citiesLiveData.value = citiesLists
                weatherLoadError.value = false
            }

            weatherLoading.value = false
        }
    }

    fun addCity(city: City) {
        viewModelScope.launch {
            var locationModel: LocationModel? = null
            var weatherModel: WeatherModel? = null
            var placesModel: PlacesModel? = null

            locationModel = getLocationForCity(city.name)

            if (null != locationModel) {
                weatherModel = getWeatherForCity(locationModel[0].lat!!, locationModel[0].lon!!)
            }

            if (null != weatherModel) {
                placesModel = getPlaceIdForCity(city.name)
            }

            if (null != placesModel) {
                citiesLists.add(
                    WeatherForCityModel(
                        city.id,
                        weatherModel,
                        locationModel,
                        placesModel
                    )
                )
                Log.d(TAG, "city added in weathersharedviewmodel")

                citiesLiveData.value = citiesLists
            }
        }
    }

    private suspend fun getLocationForCity(name: String): LocationModel? {
        val locationResponse = fetchLocation(name)

        return when (locationResponse.isSuccessful) {
            true -> locationResponse.body()
            false -> null
        }
    }

    private suspend fun getWeatherForCity(lat: Double, lon: Double): WeatherModel? {
        val weatherResponse = fetchWeather(lat, lon)

        return when (weatherResponse.isSuccessful) {
            true -> weatherResponse.body()
            false -> null
        }
    }

    private suspend fun getPlaceIdForCity(name: String): PlacesModel? {
        val placesResponse = fetchPlaceId(name)

        return when (placesResponse.isSuccessful) {
            true -> placesResponse.body()
            false -> null
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
