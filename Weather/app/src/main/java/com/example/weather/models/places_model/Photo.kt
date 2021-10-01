package com.example.weather.models.places_model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Photo(

    val height: Int,

    val html_attributions: List<String>,

    val photo_reference: String,

    val width: Int

): Parcelable