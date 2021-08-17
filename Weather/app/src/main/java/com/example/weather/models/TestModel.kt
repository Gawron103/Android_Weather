package com.example.weather.models

import com.example.weather.models.current_weather_model.WeatherModel
import com.example.weather.models.location_model.LocationModel

data class TestModel(

    val weatherModel: WeatherModel?,

    val locationModel: LocationModel?

    )