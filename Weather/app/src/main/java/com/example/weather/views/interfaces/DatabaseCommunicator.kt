package com.example.weather.views.interfaces

import com.example.weather.db.City

interface DatabaseCommunicator {

    fun addCity(city: City)
    fun deleteCity(city: City)

}