package com.example.weather.networking

class WeatherRepository constructor(private val weatherService: WeatherApi) {

    suspend fun getCoordinates(cityName: String, appKey: String) =
        weatherService.getCoordinates(cityName, appKey)

    suspend fun getWeather(lat: Double, lon: Double, exclude: String, units: String, appKey: String) =
        weatherService.getWeather(lat, lon, exclude, units, appKey)

}