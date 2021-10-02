package com.example.weather.db

import androidx.room.*

@Dao
interface CityDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCity(city: City)

    @Query("DELETE FROM city_data_table WHERE city_id = :cityId")
    suspend fun deleteCity(cityId: Int)

    @Query("SELECT * FROM city_data_table")
    fun getAllCities(): List<City>

}