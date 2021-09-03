package com.example.weather.networking

import com.example.weather.models.places_model.PlacesModel
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApi {

    @GET("/maps/api/place/findplacefromtext/json")
    suspend fun getPlaceId(
        @Query("input") cityName: String,
        @Query("inputtype") inputType: String,
        @Query("fields") fields: String,
        @Query("key") appKey: String
    ): Response<PlacesModel>

    companion object {
        private var placesService: PlacesApi? = null
        private const val BASE_URL = "https://maps.googleapis.com"

        fun getInstance(): PlacesApi {
            if (null == placesService) {
                placesService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(PlacesApi::class.java)
            }

            return placesService!!
        }
    }

}