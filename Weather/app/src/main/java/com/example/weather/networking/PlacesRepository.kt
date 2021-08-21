package com.example.weather.networking

import com.example.weather.models.places_model.PlacesModel
import retrofit2.Response

class PlacesRepository constructor(private val placesService: PlacesApi) {

    suspend fun getPlaceId(placeName: String, appKey:String): Response<PlacesModel> {
        return placesService.getPlaceId(
            placeName,
            "textquery",
            "photos",
            appKey)
    }

//    suspend fun getPlacePhoto(placeRef: String, appKey: String): Response<PlacesModel> {
//        return placesService.getPlaceImg(
//            placeRef,
//            400,
//            400,
//            appKey
//        )
//    }

}