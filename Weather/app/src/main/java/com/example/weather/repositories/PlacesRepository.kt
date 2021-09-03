package com.example.weather.repositories

import com.example.weather.models.places_model.PlacesModel
import com.example.weather.networking.PlacesApi
import retrofit2.Response

class PlacesRepository constructor(private val placesService: PlacesApi) {

    suspend fun getPlaceId(placeName: String, appKey:String): Response<PlacesModel> {
        return placesService.getPlaceId(
            placeName,
            "textquery",
            "photos",
            appKey)
    }

}