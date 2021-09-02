package com.example.weather.repositories

import com.example.weather.db.City
import com.example.weather.db.CityDAO

class CityRepository(private val dao: CityDAO) {

    val cities = dao.getAllCities()
//    suspend fun getAllCities(): List<City> {
//        return dao.getAllCities()
//    }

    suspend fun insert(city: City) {
        dao.insertCity(city)
    }

    suspend fun delete(city: City) {
        dao.deleteCity(city)
    }

}