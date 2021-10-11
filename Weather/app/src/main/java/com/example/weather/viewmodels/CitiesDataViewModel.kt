package com.example.weather.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.BuildConfig
import com.example.weather.models.CityModel
import com.example.weather.models.current_weather_model.WeatherModel
import com.example.weather.models.location_model.LocationModel
import com.example.weather.models.places_model.PlacesModel
import com.example.weather.db.CityInfo
import com.example.weather.repositories.WeatherRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class CitiesDataViewModel constructor(
    private val repository: WeatherRepository
): ViewModel() {

    private val _isRequestCorrect = MutableLiveData<Boolean>()
    val isRequestCorrect: LiveData<Boolean>
        get() = _isRequestCorrect
    private val _weatherLoading = MutableLiveData<Boolean>()
    val weatherLoading: LiveData<Boolean>
        get() = _weatherLoading
    private val _cityExists = MutableLiveData<Boolean>()
    val cityExists: LiveData<Boolean>
        get() = _cityExists
    private var _citiesLiveData = MutableLiveData<MutableList<CityModel>>()
    val citiesLiveData: LiveData<MutableList<CityModel>>
        get() = _citiesLiveData
    private var citiesLists = mutableListOf<CityModel>()

    fun refresh() {
        viewModelScope.launch {
            _weatherLoading.value = true

            citiesLists.clear()

            val cities = getAllCitiesFromDb()

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
                                    weatherModel,
                                    locationModel,
                                    placesModel
                                )
                            )
                        }
                    }
                }
            }

            _citiesLiveData.value = citiesLists
            _weatherLoading.value = false
        }
    }

    fun addCity(name: String) {
        viewModelScope.launch {
            _weatherLoading.value = true
            _cityExists.value = false
            _isRequestCorrect.value = true

            var weatherModel: WeatherModel? = null
            var placesModel: PlacesModel? = null

            val locationModel: LocationModel? = getLocationForCity(name)

            if (locationModel?.isNotEmpty()!!) {
                if (null != locationModel) {
                    val isCityInDb = isCityInDb(
                        locationModel[0].cityName!!.lowercase().replaceFirstChar { it.uppercase() })

                    if (!isCityInDb) {
                        weatherModel =
                            getWeatherForCity(locationModel[0].lat!!, locationModel[0].lon!!)

                        if (null != weatherModel) {
                            placesModel = getPlaceIdForCity(name)

                            if (null != placesModel) {
                                val newCityName = locationModel[0].cityName!!.lowercase()
                                    .replaceFirstChar { it.uppercase() }
                                addCityToDb(newCityName)

                                citiesLists.add(
                                    CityModel(
                                        weatherModel,
                                        locationModel,
                                        placesModel
                                    )
                                )

                                _citiesLiveData.postValue(citiesLists)
                            }
                        }
                    } else {
                        _cityExists.value = true
                    }
                }
            }
            else {
                _isRequestCorrect.value = false
            }
        }

        _weatherLoading.value = false
    }

    fun removeCity(cityModel: CityModel) {
        viewModelScope.launch {
            citiesLists.remove(cityModel)
            removeCityFromDb(cityModel.locationModel?.get(0)?.cityName!!)
            _citiesLiveData.value = citiesLists
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

    private fun addCityToDb(name: String) {
        repository.addCity(name)
    }

    private fun removeCityFromDb(name: String) {
        repository.deleteCity(name)
    }

    private fun isCityInDb(name: String): Boolean {
        return repository.cityInDb(name)
    }

    private fun getAllCitiesFromDb(): MutableList<CityInfo> {
        return repository.getCities()
    }

}