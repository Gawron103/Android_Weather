package com.example.weather.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.repositories.WeatherRepository
import java.lang.IllegalArgumentException

class CitiesDataViewModelFactory constructor(
    private val repository: WeatherRepository
): ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CitiesDataViewModel::class.java) -> {
                CitiesDataViewModel(repository) as T
            }
            else -> {
                throw IllegalArgumentException("Wrong ViewModel provided")
            }
        }
    }

}