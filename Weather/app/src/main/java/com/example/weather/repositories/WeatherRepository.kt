package com.example.weather.repositories

import androidx.lifecycle.LiveData
import com.example.weather.db.City
import com.example.weather.db.CityDAO
import com.example.weather.networking.PlacesApi
import com.example.weather.networking.WeatherApi

class WeatherRepository constructor(
    private val weatherService: WeatherApi,
    private val placesService: PlacesApi,
    private val dao: CityDAO
) {

    suspend fun getCoordinates(cityName: String, appKey: String) =
        weatherService.getCoordinates(cityName, appKey)

    suspend fun getWeather(lat: Double, lon: Double, exclude: String, units: String, appKey: String) =
        weatherService.getWeather(lat, lon, exclude, units, appKey)

    suspend fun getPlaceId(placeName: String, appKey:String) =
        placesService.getPlaceId(placeName, "textquery", "photos", appKey)

    suspend fun insertToDb(city: City) {
        dao.insertCity(city)
    }

    suspend fun deleteFromDb(city: City) {
        dao.deleteCity(city)
    }

    fun getAllCities(): List<City> {
        return dao.getAllCities()
    }
}