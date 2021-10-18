package com.example.weather.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.repositories.WeatherRepository

class CurrentCityDataViewModelFactory constructor(
    private val repository: WeatherRepository
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CurrentCityDataViewModel::class.java) -> {
                CurrentCityDataViewModel(repository) as T
            }
            else -> {
                throw IllegalArgumentException("Wrong ViewModel provided")
            }
        }
    }
}