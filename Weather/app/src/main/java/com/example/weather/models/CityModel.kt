package com.example.weather.models

import com.example.weather.models.current_weather_model.WeatherModel
import com.example.weather.models.location_model.LocationModel
import com.example.weather.models.places_model.PlacesModel

data class CityModel(

    val idInDb: Long?,

    val weatherModel: WeatherModel?,

    val locationModel: LocationModel?,

    val placesModel: PlacesModel?

)