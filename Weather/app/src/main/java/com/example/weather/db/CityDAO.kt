package com.example.weather.db

import androidx.room.*

@Dao
interface CityDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCity(city: City)

    @Delete
    suspend fun deleteCity(city: City)

    @Query("SELECT * FROM city_data_table")
    fun getAllCities(): List<City>

}