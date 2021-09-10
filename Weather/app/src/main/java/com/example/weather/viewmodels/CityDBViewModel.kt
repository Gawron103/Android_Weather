//package com.example.weather.viewmodels
//
//import android.util.Log
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.weather.db.City
//import com.example.weather.repositories.CityRepository
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//
//class CityDBViewModel(private val cityRepository: CityRepository): ViewModel() {
//
//    private val TAG = "CityViewModel"
//
//    val cities = cityRepository.cities
//
//    fun insert(city: City) {
//        viewModelScope.launch {
//            cityRepository.insert(city)
//            Log.d(TAG, "CityViewModel::insert triggered")
//        }
//    }
//
//    fun remove(city: City) {
//        viewModelScope.launch {
//            cityRepository.delete(city)
//            Log.d(TAG, "CityViewModel::remove triggered")
//        }
//    }
//
//}