package com.example.weather.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.models.CityModel
import com.example.weather.repositories.WeatherRepository
import kotlinx.coroutines.launch

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
            _citiesLiveData.value?.clear()

            citiesLists.clear()

            val cities = repository.getCities()

            cities.forEach { city ->
                repository.getCoordinates(city.name)?.let { location ->
                    repository.getWeather(location[0].lat!!, location[0].lon!!)?.let { weather ->
                        repository.getPlaceId(city.name)?.let { places ->
                            CityModel(
                                weather,
                                location,
                                places
                            )
                        }
                    }
                }?.let { citiesLists.add(it) }
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

            repository.getCoordinates(name)?.let { location ->
                if (location.isNotEmpty()) {
                    val correctName = location[0].cityName!!
                    val isAlreadyAdded = repository.isCityInDb(correctName)
                    if (!isAlreadyAdded) {
                        repository.getWeather(location[0].lat!!, location[0].lon!!)?.let { weather ->
                            repository.getPlaceId(correctName)?.let { places ->
                                CityModel(
                                    weather,
                                    location,
                                    places
                                )
                            }
                        }
                    }
                    else {
                        _cityExists.value = false
                        null
                    }
                }
                else {
                    _isRequestCorrect.value = false
                    null
                }
            }?.run {
                repository.addCity(locationModel?.get(0)?.cityName!!)
                citiesLists.add(this)
            }

            _weatherLoading.value = false
        }
    }

    fun removeCity(cityModel: CityModel) {
        viewModelScope.launch {
            citiesLists.remove(cityModel)
            repository.deleteCity(cityModel.locationModel?.get(0)?.cityName!!)
            _citiesLiveData.value = citiesLists
        }
    }

}