package com.example.weather.models.shared_weather_model

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)