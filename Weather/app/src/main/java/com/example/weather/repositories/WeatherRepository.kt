package com.example.weather.repositories

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
        _databaseRef.push().setValue(name).await()
    }

    suspend fun removeCity(name: String) {
        val cities = getCitiesFromFirebase() as MutableList
        cities.remove(name)
        _databaseRef.setValue(cities as List<String>)
    }

    suspend fun isCityInDb(name: String): Boolean {
        return _databaseRef.get().await().children.any { snapshot ->
            snapshot.getValue(String::class.java)!! == name
        }
    }

    suspend fun getCitiesFromFirebase(): List<String> {
        val citiesList = _databaseRef.get().await().children.map { snapshot ->
            snapshot.getValue(String::class.java)!!
        }

        return citiesList
    }


}