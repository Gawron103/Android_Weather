package com.example.weather.models

import com.example.weather.models.current_weather_model.WeatherModel
import com.example.weather.models.location_model.LocationModel
import com.example.weather.models.places_model.PlacesModel

data class WeatherForCityModel(

    val idInDb: Int?,

    val weatherModel: WeatherModel?,

    val locationModel: LocationModel?,

    val placesModel: PlacesModel?

    )