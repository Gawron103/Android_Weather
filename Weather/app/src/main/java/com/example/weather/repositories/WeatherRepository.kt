package com.example.weather.repositories

import android.util.Log
import com.example.weather.BuildConfig
import com.example.weather.networking.PlacesApi
import com.example.weather.networking.WeatherApi
import com.example.weather.models.current_weather_model.WeatherModel
import com.example.weather.models.location_model.LocationModel
import com.example.weather.models.places_model.PlacesModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await

class WeatherRepository constructor(
    private val weatherService: WeatherApi,
    private val placesService: PlacesApi,
) {

    private val TAG = "WeatherRepository"

    private var _databaseInst: FirebaseDatabase
    private var _databaseRef: DatabaseReference

    init {
        val currentUser = FirebaseAuth.getInstance().currentUser
        _databaseInst = FirebaseDatabase.getInstance()
        _databaseRef = _databaseInst.getReference("users").child(currentUser?.uid!!).child("cities")
    }

    suspend fun getCoordinates(cityName: String): LocationModel {
        return weatherService.getCoordinates(cityName, BuildConfig.WEATHER_API_KEY)
    }

    suspend fun getWeather(lat: Double, lon: Double): WeatherModel {
        val exclude = "minutely,hourly,alerts"
        val units = "metric"
        return weatherService.getWeather(lat, lon, exclude, units, BuildConfig.WEATHER_API_KEY)
    }

    suspend fun getPlaceId(placeName: String): PlacesModel {
        return placesService.getPlaceId(placeName, "textquery", "photos", BuildConfig.PLACES_API_KEY)
    }

    suspend fun getNameForLocation(lat: Double, lon: Double): LocationModel {
        return weatherService.getNameForLocation(lat, lon, BuildConfig.WEATHER_API_KEY)
    }

    suspend fun storeCity(name: String) {
        _databaseRef.push().setValue(name)
    }

    suspend fun removeCity(cityKey: String) {
        _databaseRef.child(cityKey).removeValue().await()
    }

    fun getRefToCity(): DatabaseReference {
        return _databaseRef
    }

}