package com.example.weather.models.current_weather_model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherModel(

    @SerializedName("current")
    val currentConditions: Current?,

    @SerializedName("daily")
    val dailyConditions: List<Daily>?

): Parcelable