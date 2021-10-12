package com.example.weather.repositories

import com.example.weather.BuildConfig
import com.example.weather.networking.PlacesApi
import com.example.weather.networking.WeatherApi
import com.example.weather.db.CityInfo
import com.example.weather.models.current_weather_model.WeatherModel
import com.example.weather.models.location_model.LocationModel
import com.example.weather.models.places_model.PlacesModel
import io.realm.Realm
import io.realm.RealmConfiguration
import java.util.*

class WeatherRepository constructor(
    private val weatherService: WeatherApi,
    private val placesService: PlacesApi,
    private val realmConfig: RealmConfiguration
) {

    suspend fun getCoordinates(cityName: String): LocationModel? {
        val response = weatherService.getCoordinates(cityName, BuildConfig.WEATHER_API_KEY)

        return when (response.isSuccessful) {
            true -> response.body()
            else -> null
        }
    }

    suspend fun getWeather(lat: Double, lon: Double): WeatherModel? {
        val exclude = "minutely,hourly,alerts"
        val units = "metric"
        val response = weatherService.getWeather(lat, lon, exclude, units, BuildConfig.WEATHER_API_KEY)

        return when (response.isSuccessful) {
            true -> response.body()
            else -> null
        }
    }

    suspend fun getPlaceId(placeName: String): PlacesModel? {
        val response = placesService.getPlaceId(placeName, "textquery", "photos", BuildConfig.PLACES_API_KEY)

        return when (response.isSuccessful) {
            true -> response.body()
            else -> null
        }
    }


    fun addCity(name: String) {
        val realm = Realm.getInstance(realmConfig)

        realm.executeTransaction{ realm ->
            val city = realm.createObject(CityInfo::class.java, UUID.randomUUID().toString())
            city.apply {
                this.name = name
            }

            realm.insert(city)
        }
    }

    fun deleteCity(name: String) {
        val realm = Realm.getInstance(realmConfig)

        realm.executeTransaction { realm ->
            val city = realm
                .where(CityInfo::class.java)
                .equalTo("name", name)
                .findFirst()

            city?.deleteFromRealm()
        }
    }

    fun isCityInDb(name: String): Boolean {
        val realm = Realm.getInstance(realmConfig)
        var isInDb = false

        realm.executeTransaction { realm ->
            isInDb = realm
                .where(CityInfo::class.java)
                .equalTo("name", name)
                .findFirst()
                ?.let { true } ?: false
        }

        return isInDb
    }

    fun getCities(): MutableList<CityInfo> {
        val realm = Realm.getInstance(realmConfig)
        val cities = mutableListOf<CityInfo>()

        realm.executeTransaction { realm ->
            cities.addAll(
                realm.where(CityInfo::class.java).findAll()
            )
        }

        return cities
    }

}