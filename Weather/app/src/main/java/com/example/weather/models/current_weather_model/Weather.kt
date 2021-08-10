package com.example.weather.models.current_weather_model

import com.google.gson.annotations.SerializedName

data class Weather(

    @SerializedName("main")
    val shortDesc: String?,

    @SerializedName("description")
    val desc: String?,

    @SerializedName("icon")
    val icon: String?

)