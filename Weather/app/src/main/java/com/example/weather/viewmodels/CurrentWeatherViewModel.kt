package com.example.weather.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.models.*

class CurrentWeatherViewModel : ViewModel() {

    // Model for current weather
    val currentWeather = MutableLiveData<List<CurrentWeatherModel>>()

    // Says if there is an error when loading the data
    val currentWeatherLoadError = MutableLiveData<Boolean>()

    // Says is the view model is in the process of loading the data
    val loading = MutableLiveData<Boolean>()

    fun refresh() {
        fetchWeather()
    }

    private fun fetchWeather() {
        // Mock data for now
        val mockData = listOf<CurrentWeatherModel>(
            CurrentWeatherModel(
                Coord(14.553, 53.4289),
                listOf(Weather(802, "Clouds", "scattered clouds", "03d")),
                "stations",
                Main(297.79, 297.46, 295.12, 298.87, 1013, 44),
                10000,
                Wind(1.24, 323, 1.95),
                Clouds(33),
                1627056729,
                Sys(2, 2034571, "PL", 1627009406, 1627067566),
                7200,
                3083829,
                "Szczecin",
                200)
        )

        currentWeatherLoadError.value = false
        loading.value = false
        currentWeather.value = mockData
    }
}