package com.example.weather.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.models.CityModel
import com.example.weather.repositories.WeatherRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
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

    private val _userCitiesRef: DatabaseReference = repository.getRefToCity()
    private val _citiesListener: ValueEventListener
    private var _cities = mutableMapOf<String, String>()

    init {
        _citiesListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _cities.clear()

                Log.d(TAG, "Values: ${snapshot.value}")
                _cities = snapshot.value as MutableMap<String, String>

                refresh()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Database error: ${error.details}")
            }
        }

        _userCitiesRef.addListenerForSingleValueEvent(_citiesListener)
    }

    override fun onCleared() {
        super.onCleared()
        _userCitiesRef.removeEventListener(_citiesListener)
    }

    private fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            _weatherLoading.postValue(true)

            Log.d(TAG, "Refresh running on thread: ${Thread.currentThread()}")

            val fetchedData = mutableListOf<CityModel>()

            _cities.forEach { city ->
                val location = repository.getCoordinates(city.value)
                val weather = repository.getWeather(location[0].lat!!, location[0].lon!!)
                val places = repository.getPlaceId(location[0].cityName!!)

                fetchedData.add(CityModel(weather, location, places))
            }

            _citiesLiveData.postValue(fetchedData)
            _weatherLoading.postValue(false)
        }
    }

    fun addCity(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val location = repository.getCoordinates(name)

            Log.d(TAG, "AddCity running on thread: ${Thread.currentThread()}")

            if (location.isNotEmpty()) {
                when (_cities.containsKey(location[0].cityName!!)) {
                    false -> {
                        repository.storeCity(location[0].cityName!!)
                        true
                    }
                    true -> { false }
                }.also { result ->
                    _cityAdded.postValue(result)
                }
            }
            else {
                _cityAdded.postValue(false)
            }
        }
    }

    fun removeCity(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "RemoveCity running on thread: ${Thread.currentThread()}")

            if (_cities.keys.contains(cityName)) {
                repository.removeCity(_cities.entries.find { cityName == it.value }?.key!!)
                true
            }
            else {
                false
            }.also { result ->
                _cityDeleted.postValue(result)
            }
        }
    }

    fun removeSelectedCities(cities: Set<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            for (city in cities) {
                repository.removeCity(_cities.entries.find { city == it.value }?.key!!)
            }

            _cityDeleted.postValue(true)
        }
    }

}