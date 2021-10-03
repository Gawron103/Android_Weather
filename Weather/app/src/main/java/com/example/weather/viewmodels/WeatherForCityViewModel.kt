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

class WeatherForCityViewModel constructor(
    private val weatherRepository: WeatherRepository
): ViewModel() {

    private val TAG = "WeatherForCityViewModel"

    // Says if there is an error when loading the data
    val weatherLoadError = MutableLiveData<Boolean>()

    // Says is the view model is in the process of loading the data
    val weatherLoading = MutableLiveData<Boolean>()

    var citiesLiveData = MutableLiveData<MutableList<CityModel>>()

    private var citiesLists = mutableListOf<CityModel>()

    fun refresh() {
        viewModelScope.launch {
            Log.d(TAG, "ViewModel refresh triggered")
            weatherLoading.value = true

            citiesLists.clear()

            // get saved cities from db
            val cities = withContext(Dispatchers.IO) {
                weatherRepository.getAllCities()
            }

            for (city in cities) {
                var weatherModel: WeatherModel? = null
                var placesModel: PlacesModel? = null

                var locationModel: LocationModel? = getLocationForCity(city.name)

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

    fun addCity(city: City) {
        viewModelScope.launch {
            Log.d(TAG, "ViewModel add city triggered")
            weatherLoading.value = true

            val isCityInDb = withContext(Dispatchers.IO) {
                weatherRepository.isCityInDb(city.name)
            }

            if(!isCityInDb) {
                var weatherModel: WeatherModel? = null
                var placesModel: PlacesModel? = null

                var locationModel: LocationModel? = getLocationForCity(city.name)

                if (null != locationModel) {
                    weatherModel = getWeatherForCity(locationModel[0].lat!!, locationModel[0].lon!!)

                    if (null != weatherModel) {
                        placesModel = getPlaceIdForCity(city.name)

                        if (null != placesModel) {
                            weatherRepository.insertToDb(city)

                            citiesLists.add(CityModel(
                                city.id,
                                weatherModel,
                                locationModel,
                                placesModel
                            ))

                            citiesLiveData.value = citiesLists
                        }
                    }
                }
            }

            weatherLoading.value = false
        }
    }

    fun addCurrentCity(lat: Double, lon: Double) {
        viewModelScope.launch {
            var locationModel: LocationModel? = null
            var weatherModel: WeatherModel? = null
            var placesModel: PlacesModel? = null

            locationModel = getNameForLocation(lat, lon)

            if (null != locationModel) {
                weatherModel = getWeatherForCity(lat, lon)
            }

            if (null != weatherModel) {
                placesModel = getPlaceIdForCity(locationModel?.get(0)?.cityName!!)
            }

            if (null != placesModel) {
                val currentLocation = CityModel(
                    0,
                    weatherModel,
                    locationModel,
                    placesModel
                )

                Log.d(TAG, "Weather for current city fetched successfully")

                // always add current localization as first item
                citiesLists.add(0, currentLocation)
                citiesLiveData.value = citiesLists
            }
        }
    }

    fun removeCity(cityModel: CityModel) {
        viewModelScope.launch {
            citiesLists.remove(cityModel)
            weatherRepository.deleteFromDb(cityModel?.idInDb!!)
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

    private suspend fun fetchName(lat: Double, lon: Double): Response<LocationModel> {
        return weatherRepository.getNameForLocation(
            lat,
            lon,
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