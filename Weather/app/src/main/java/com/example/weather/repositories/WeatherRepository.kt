package com.example.weather.repositories

import com.example.weather.networking.PlacesApi
import com.example.weather.networking.WeatherApi
import com.example.weather.db.CityInfo
import io.realm.Realm
import io.realm.RealmConfiguration
import java.util.*

class WeatherRepository constructor(
    private val weatherService: WeatherApi,
    private val placesService: PlacesApi,
    private val realmConfig: RealmConfiguration
) {

    suspend fun getCoordinates(cityName: String, appKey: String) =
        weatherService.getCoordinates(cityName, appKey)

    suspend fun getNameForLocation(lat: Double, lon: Double, appKey: String) =
        weatherService.getNameForLocation(lat, lon, appKey)

    suspend fun getWeather(lat: Double, lon: Double, exclude: String, units: String, appKey: String) =
        weatherService.getWeather(lat, lon, exclude, units, appKey)

    suspend fun getPlaceId(placeName: String, appKey:String) =
        placesService.getPlaceId(placeName, "textquery", "photos", appKey)

    fun addCity(name: String) {
        val realm = Realm.getInstance(realmConfig)

        realm.executeTransaction{ realm ->
            val city = realm.createObject(CityInfo::class.java, UUID.randomUUID().toString())
            city.name = name

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

    fun cityInDb(name: String): Boolean {
        val realm = Realm.getInstance(realmConfig)
        var isInDb: Boolean = false

        realm.executeTransaction { realm ->
            val city = realm
                .where(CityInfo::class.java)
                .equalTo("name", name)
                .findFirst()

            isInDb = null != city
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