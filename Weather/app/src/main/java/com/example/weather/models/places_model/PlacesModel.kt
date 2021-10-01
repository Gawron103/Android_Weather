package com.example.weather.models.places_model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlacesModel(

    val candidates: List<Candidates>?,

    val status: String?

): Parcelable