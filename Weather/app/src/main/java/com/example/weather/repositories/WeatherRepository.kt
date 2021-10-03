package com.example.weather.repositories

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

    suspend fun getNameForLocation(lat: Double, lon: Double, appKey: String) =
        weatherService.getNameForLocation(lat, lon, appKey)

    suspend fun getWeather(lat: Double, lon: Double, exclude: String, units: String, appKey: String) =
        weatherService.getWeather(lat, lon, exclude, units, appKey)

    suspend fun getPlaceId(placeName: String, appKey:String) =
        placesService.getPlaceId(placeName, "textquery", "photos", appKey)

    suspend fun insertToDb(city: City) {
        dao.insertCity(city)
    }

    suspend fun deleteFromDb(cityName: Int) {
        dao.deleteCity(cityName)
    }

    suspend fun isCityInDb(name: String): Boolean {
        return dao.isCityInDb(name)
    }

    fun getAllCities(): List<City> {
        return dao.getAllCities()
    }

}