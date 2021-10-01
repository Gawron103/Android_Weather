package com.example.weather.models.current_weather_model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Daily(
    @SerializedName("dt")
    val currentTime: Int?,

    @SerializedName("sunrise")
    val sunrise: Int?,

    @SerializedName("sunset")
    val sunset: Int?,

    @SerializedName("temp")
    val tempInDay: Temp?,

    val weather: List<Weather>?

): Parcelable