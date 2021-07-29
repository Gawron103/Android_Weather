package com.example.weather.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.models.shared_weather_model.*

class WeatherSharedViewModel: ViewModel() {

    // Model for weather
    val weather = MutableLiveData<List<SharedWeatherModel>>()

    // Says if there is an error when loading the data
    val weatherLoadError = MutableLiveData<Boolean>()

    // Says is the view model is in the process of loading the data
    val loading = MutableLiveData<Boolean>()

    fun refresh() {
        fetchWeather()
    }

    private fun fetchWeather() {
        val weatherMock = Weather("sunny", "some icon", 666, "weather")
        val currentMock = Current(0, 0.0, 0, 0.0, 0, 0, 0, 0, 30.0, 0.0, 0, listOf(weatherMock), 0, 0.0)
        val feelsLikeMock = FeelsLike(0.0, 0.0, 0.0, 0.0)
        val weatherXMock = WeatherX("dummy", "dummy_icon", 0, "main")
        val tempMock = Temp(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        val dailyMock = Daily(0, 0.0, 0, feelsLikeMock, 0, 0.0, 0, 0, 0.0, 0, 0.0, 0, 0, tempMock, 0.0, listOf(weatherXMock), 0, 0.0, 0.0)
        val mockData = listOf(SharedWeatherModel(
            currentMock,
            listOf(dailyMock, dailyMock, dailyMock, dailyMock, dailyMock, dailyMock, dailyMock),
            0.0,
            0.0,
            "Warsaw",
            0
        ))

        weatherLoadError.value = false
        loading.value = false
        weather.value = mockData
    }
}