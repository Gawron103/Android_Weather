package com.example.weather.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CityDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCity(city: City)

    @Delete
    suspend fun deleteCity(city: City)

    @Query("SELECT * FROM city_data_table")
    suspend fun getAllCities(): List<City>

}