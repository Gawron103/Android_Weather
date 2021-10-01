package com.example.weather.models.location_model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Location(

    @SerializedName("name")
    val cityName: String?,

    @SerializedName("lat")
    val lat: Double?,

    @SerializedName("lon")
    val lon: Double?,

    @SerializedName("country")
    val countryCode: String?

): Parcelable