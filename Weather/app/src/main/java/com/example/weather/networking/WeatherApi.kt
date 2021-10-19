package com.example.weather.networking

import com.example.weather.models.current_weather_model.WeatherModel
import com.example.weather.models.location_model.LocationModel
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("/data/2.5/onecall")
    fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String,
        @Query("units") units: String,
        @Query("appid") appKey: String
    ): Observable<WeatherModel>

    @GET("/geo/1.0/direct")
    fun getCoordinates(
        @Query("q") cityName: String,
        @Query("appid") appKey: String
    ): Observable<LocationModel>

    @GET("geo/1.0/reverse")
    suspend fun getNameForLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appKey: String
    ): Response<LocationModel>


    companion object {
        private var weatherService: WeatherApi? = null
        private const val BASE_URL = "http://api.openweathermap.org"

        fun getInstance(): WeatherApi {
            if(null == weatherService) {
                weatherService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
                    .create(WeatherApi::class.java)
            }

            return weatherService!!
        }
    }

}