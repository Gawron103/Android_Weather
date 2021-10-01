package com.example.weather.models.places_model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Candidates(

    val photos: List<Photo>

): Parcelable