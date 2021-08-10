package com.example.weather.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.networking.WeatherRepository
import java.lang.IllegalArgumentException

class ViewModelFactory constructor(private val repository: WeatherRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(WeatherSharedViewModel::class.java) -> {
                WeatherSharedViewModel(repository) as T
            }
            else -> {
                throw IllegalArgumentException("View model not found")
            }
        }
    }

}