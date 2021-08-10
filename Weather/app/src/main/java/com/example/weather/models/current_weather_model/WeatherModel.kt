package com.example.weather.models.current_weather_model

import com.google.gson.annotations.SerializedName

data class WeatherModel(

    @SerializedName("current")
    val currentConditions: Current?,

    @SerializedName("daily")
    val dailyConditions: List<Daily>?

)