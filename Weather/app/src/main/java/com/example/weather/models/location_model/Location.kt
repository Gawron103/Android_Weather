package com.example.weather.models.location_model

import com.google.gson.annotations.SerializedName

data class Location(

    @SerializedName("name")
    val cityName: String?,

    @SerializedName("lat")
    val lat: Double?,

    @SerializedName("lon")
    val lon: Double?,

    @SerializedName("country")
    val countryCode: String?

)