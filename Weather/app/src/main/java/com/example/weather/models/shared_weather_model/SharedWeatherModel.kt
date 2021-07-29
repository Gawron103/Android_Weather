package com.example.weather.models.shared_weather_model

data class SharedWeatherModel(
    val current: Current,
    val daily: List<Daily>,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int
)