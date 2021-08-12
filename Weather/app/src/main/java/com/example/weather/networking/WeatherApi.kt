package com.example.weather.networking

import com.example.weather.models.current_weather_model.WeatherModel
import com.example.weather.models.location_model.LocationModel
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("/data/2.5/onecall")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String,
        @Query("units") units: String,
        @Query("appid") appKey: String
    ): Response<WeatherModel>

    @GET("/geo/1.0/direct")
    suspend fun getCoordinates(
        @Query("q") cityName: String,
        @Query("appid") appKey: String
    ): Response<LocationModel>

//    @GET("/img/wn/")
//    suspend fun getIcon(
//        @Query('')
//    ): Response

    companion object {
        private var weatherService: WeatherApi? = null
        private val BASE_URL = "http://api.openweathermap.org"

        fun getInstance(): WeatherApi {
            if(null == weatherService) {
                weatherService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(WeatherApi::class.java)
            }

            return weatherService!!
        }
    }

}