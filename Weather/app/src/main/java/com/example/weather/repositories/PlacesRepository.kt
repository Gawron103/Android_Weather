//package com.example.weather.repositories
//
//import com.example.weather.networking.PlacesApi
//
//class PlacesRepository constructor(private val placesService: PlacesApi) {
//
//    suspend fun getPlaceId(placeName: String, appKey:String) =
//        placesService.getPlaceId(placeName, "textquery", "photos", appKey)
//
//}