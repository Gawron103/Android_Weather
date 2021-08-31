package com.example.weather.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.db.City
import com.example.weather.repositories.CityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CityViewModel(private val cityRepository: CityRepository): ViewModel() {

//    val cities = cityRepository.cities
    val cities = MutableLiveData<List<City>>()

    fun refresh() {
        viewModelScope.launch {
            cities.value = cityRepository.getAllCities()
        }
    }

    fun insert(city: City) {
        viewModelScope.launch {
            cityRepository.insert(city)
        }
    }

    fun remove(city: City) {
        viewModelScope.launch {
            cityRepository.delete(city)
        }
    }
}