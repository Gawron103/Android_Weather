package com.example.weather.models

import android.os.Parcel
import android.os.Parcelable
import com.example.weather.models.current_weather_model.WeatherModel
import com.example.weather.models.location_model.LocationModel
import com.example.weather.models.places_model.PlacesModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CityModel(

    val idInDb: Int?,

    val weatherModel: WeatherModel?,

    val locationModel: LocationModel?,

    val placesModel: PlacesModel?

): Parcelable