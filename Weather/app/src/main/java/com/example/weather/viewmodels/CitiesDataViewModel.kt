package com.example.weather.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.models.CityModel
import com.example.weather.repositories.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CitiesDataViewModel constructor(
    private val repository: WeatherRepository
): ViewModel() {

    private val TAG = "CitiesDataViewModel"

    private val _weatherLoading = MutableLiveData<Boolean>()
    val weatherLoading: LiveData<Boolean>
        get() = _weatherLoading
    private val _cityAdded = MutableLiveData<Boolean>()
    val cityAdded: LiveData<Boolean>
        get() = _cityAdded
    private val _cityDeleted = MutableLiveData<Boolean>()
    val cityDeleted: LiveData<Boolean>
        get() = _cityDeleted
    private var _citiesLiveData = MutableLiveData<MutableList<CityModel>>()
    val citiesLiveData: LiveData<MutableList<CityModel>>
        get() = _citiesLiveData
    private var _citiesLists = mutableListOf<CityModel>()

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            _weatherLoading.postValue(true)

            _citiesLists.clear()

            val cities = repository.getCitiesFromFirebase()

            Log.d(TAG, "Refresh running on thread: ${Thread.currentThread()}")

            cities.forEach { city ->
                val location = repository.getCoordinates(city)
                val weather = repository.getWeather(location[0].lat!!, location[0].lon!!)
                val places = repository.getPlaceId(location[0].cityName!!)

                _citiesLists.add(
                    CityModel(weather, location, places)
                )
            }

            _citiesLiveData.postValue(_citiesLists)
            _weatherLoading.postValue(false)
        }
    }

    fun addCity(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val location = repository.getCoordinates(name)

            Log.d(TAG, "AddCity running on thread: ${Thread.currentThread()}")

            if (location.isNotEmpty()) {
                val alreadyExists = repository.isCityInDb(location[0].cityName!!)

                if (!alreadyExists) {
                    repository.storeCity(location[0].cityName!!)
                    _cityAdded.postValue(true)
                    refresh()
                }
                else {
                    _cityAdded.postValue(false)
                }
            }
            else {
                _cityAdded.postValue(false)
            }
        }
    }

    fun removeCity(cityModel: CityModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val isInDb = repository.isCityInDb(cityModel.locationModel?.get(0)?.cityName!!)

            Log.d(TAG, "RemoveCity running on thread: ${Thread.currentThread()}")

            if (isInDb) {
                repository.removeCity(cityModel.locationModel[0].cityName!!)
                _cityDeleted.postValue(true)
                refresh()
            }
            else {
                _cityDeleted.postValue(false)
            }
        }
    }

}