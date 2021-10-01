package com.example.weather.models.current_weather_model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Weather(

    @SerializedName("main")
    val shortDesc: String?,

    @SerializedName("description")
    val desc: String?,

    @SerializedName("icon")
    val icon: String?

): Parcelable