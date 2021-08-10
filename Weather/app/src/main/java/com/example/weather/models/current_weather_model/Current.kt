package com.example.weather.models.current_weather_model

import com.google.gson.annotations.SerializedName

data class Current(

    @SerializedName("sunrise")
    val sunrise: Int?,

    @SerializedName("sunset")
    val sunset: Int?,

    @SerializedName("temp")
    val temp: Double?,

    @SerializedName("feels_like")
    val feelsLike: Double?,

    val weather: List<Weather>?

)