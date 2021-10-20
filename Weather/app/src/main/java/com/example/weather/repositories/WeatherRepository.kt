package com.example.weather.repositories

import android.util.Log
import com.example.weather.BuildConfig
import com.example.weather.networking.PlacesApi
import com.example.weather.networking.WeatherApi
import com.example.weather.db.CityInfo
import com.example.weather.models.current_weather_model.WeatherModel
import com.example.weather.models.location_model.LocationModel
import com.example.weather.models.places_model.PlacesModel
import io.reactivex.Observable
import io.realm.Realm
import io.realm.RealmConfiguration
import java.util.*

class WeatherRepository constructor(
    private val weatherService: WeatherApi,
    private val placesService: PlacesApi,
    private val realmConfig: RealmConfiguration
) {

    private val TAG = "WeatherRepository"

    fun getCoordinates(cityName: String): Observable<LocationModel> {
        return weatherService.getCoordinates(cityName, BuildConfig.WEATHER_API_KEY)
    }

    fun getWeather(lat: Double, lon: Double): Observable<WeatherModel> {
        val exclude = "minutely,hourly,alerts"
        val units = "metric"
        return weatherService.getWeather(lat, lon, exclude, units, BuildConfig.WEATHER_API_KEY)
    }

    fun getPlaceId(placeName: String): Observable<PlacesModel> {
        return placesService.getPlaceId(placeName, "textquery", "photos", BuildConfig.PLACES_API_KEY)
    }

    fun getNameForLocation(lat: Double, lon: Double): Observable<LocationModel> {
        return weatherService.getNameForLocation(lat, lon, BuildConfig.WEATHER_API_KEY)
    }

    fun addCity(name: String): Observable<Boolean> {
        return Observable.create { emitter ->
            Realm.getInstance(realmConfig).use { realm ->
                realm.executeTransactionAsync { realm ->
                    var status = false

                    realm.where(CityInfo::class.java).equalTo("name", name).findFirst()?.let {
                        status = false
                    } ?: run {
                        val city = realm.createObject(CityInfo::class.java, UUID.randomUUID().toString())
                            .apply { this.name = name }
                        realm.copyToRealm(city)
                        status = true
                    }

                    Log.d(TAG, "Repository ADD worked on thread: ${Thread.currentThread()}")

                    emitter.onNext(status)
                    emitter.onComplete()
                }
            }
        }
    }

    fun deleteCity(name: String): Observable<Boolean> {
        return Observable.create { emitter ->
            Realm.getInstance(realmConfig).use { realm ->
                realm.executeTransactionAsync { realm ->
                    realm
                        .where(CityInfo::class.java)
                        .equalTo("name", name)
                        .findFirst()?.let {
                            it.deleteFromRealm()
                            emitter.onNext(true)
                        } ?: emitter.onNext(false)

                    Log.d(TAG, "Repository DELETE worked on thread: ${Thread.currentThread()}")

                    emitter.onComplete()
                }
            }
        }
    }

    fun getCities(): Observable<List<CityInfo>> {
        return Observable.create { emitter ->
            Realm.getInstance(realmConfig).use { realm ->
                realm.executeTransactionAsync { realm ->
                    val data = realm.copyFromRealm(realm.where(CityInfo::class.java).findAll())
                    Log.d(TAG, "Repository GET CITIES worked on thread: ${Thread.currentThread()}")
                    emitter.onNext(data)
                    emitter.onComplete()
                }
            }

        }
    }

}