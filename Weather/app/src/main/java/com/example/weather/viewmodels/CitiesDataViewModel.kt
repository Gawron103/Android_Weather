package com.example.weather.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.BuildConfig
import com.example.weather.db.City
import com.example.weather.models.CityModel
import com.example.weather.models.current_weather_model.WeatherModel
import com.example.weather.models.location_model.LocationModel
import com.example.weather.models.places_model.PlacesModel
import com.example.weather.repositories.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class CitiesDataViewModel constructor(
    private val repository: WeatherRepository
): ViewModel() {

    val weatherLoading = MutableLiveData<Boolean>()
    val cityExists = MutableLiveData<Boolean>()
    var citiesLiveData = MutableLiveData<MutableList<CityModel>>()
    private var citiesLists = mutableListOf<CityModel>()

    fun refresh() {
        viewModelScope.launch {
            weatherLoading.value = true

            citiesLists.clear()

            val cities = withContext(Dispatchers.IO) {
                repository.getAllCities()
            }

            for (city in cities) {
                var weatherModel: WeatherModel? = null
                var placesModel: PlacesModel? = null

                val locationModel: LocationModel? = getLocationForCity(city.name)

                if (null != locationModel) {
                    weatherModel = getWeatherForCity(locationModel[0].lat!!, locationModel[0].lon!!)

                    if (null != weatherModel) {
                        placesModel = getPlaceIdForCity(city.name)

                        if (null != placesModel) {
                            citiesLists.add(
                                CityModel(
                                    city.id,
                                    weatherModel,
                                    locationModel,
                                    placesModel
                                )
                            )
                        }
                    }
                }
            }

            citiesLiveData.value = citiesLists
            weatherLoading.value = false
        }
    }

    fun addCity(name: String) {
        viewModelScope.launch {
            weatherLoading.value = true
            cityExists.value = false

            val isCityInDb = withContext(Dispatchers.IO) {
                repository.isCityInDb(name)
            }

            if(!isCityInDb) {
                var weatherModel: WeatherModel? = null
                var placesModel: PlacesModel? = null

                val locationModel: LocationModel? = getLocationForCity(name)

                if (null != locationModel) {
                    weatherModel = getWeatherForCity(locationModel[0].lat!!, locationModel[0].lon!!)

                    if (null != weatherModel) {
                        placesModel = getPlaceIdForCity(name)

                        if (null != placesModel) {
                            val id = repository.insertToDb(City(locationModel[0].cityName!!))

                            citiesLists.add(CityModel(
                                id,
                                weatherModel,
                                locationModel,
                                placesModel
                            ))

                            citiesLiveData.value = citiesLists
                        }
                    }
                }
            }
            else {
                cityExists.value = true
            }

            weatherLoading.value = false
        }
    }

    fun removeCity(cityModel: CityModel) {
        viewModelScope.launch {
            citiesLists.remove(cityModel)
            repository.deleteFromDb(cityModel.idInDb!!)
            citiesLiveData.value = citiesLists
        }
    }

    private suspend fun getLocationForCity(name: String): LocationModel? {
        val locationResponse = fetchLocation(name)

        return when (locationResponse.isSuccessful) {
            true -> locationResponse.body()
            false -> null
        }
    }

    private suspend fun getNameForLocation(lat: Double, lon: Double): LocationModel? {
        val coordsResponse = fetchName(lat, lon)

        return when (coordsResponse.isSuccessful) {
            true -> coordsResponse.body()
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
        return repository.getWeather(
            lat,
            lon,
            "minutely,hourly,alerts",
            "metric",
            BuildConfig.WEATHER_API_KEY
        )
    }

    private suspend fun fetchLocation(city: String): Response<LocationModel> {
        return repository.getCoordinates(
            city,
            BuildConfig.WEATHER_API_KEY
        )
    }

    private suspend fun fetchName(lat: Double, lon: Double): Response<LocationModel> {
        return repository.getNameForLocation(
            lat,
            lon,
            BuildConfig.WEATHER_API_KEY
        )
    }

    private suspend fun fetchPlaceId(cityName: String): Response<PlacesModel> {
        return repository.getPlaceId(
            cityName,
            BuildConfig.PLACES_API_KEY
        )
    }

}