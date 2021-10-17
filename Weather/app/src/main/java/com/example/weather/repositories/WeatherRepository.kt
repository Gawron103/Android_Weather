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
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers.from
import io.reactivex.internal.operators.observable.ObservableAny
import io.reactivex.parallel.ParallelFlowable.from
import io.reactivex.schedulers.Schedulers.from
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmConfiguration
import io.realm.kotlin.executeTransactionAwait
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


    fun addCity(name: String) {
        val realm = Realm.getInstance(realmConfig)

        realm.executeTransactionAsync { realm ->

            val city = realm.createObject(CityInfo::class.java, UUID.randomUUID().toString()).apply {
                this.name = name
            }

            realm.insert(city)

            Log.d(TAG, "New city added. Added $name")
        }.also { realm.close() }
    }

//    fun deleteCity(name: String) {
//        val realm = Realm.getInstance(realmConfig)
//
//        realm.executeTransaction { realm ->
//            val city = realm
//                .where(CityInfo::class.java)
//                .equalTo("name", name)
//                .findFirst()
//
//            city?.deleteFromRealm()
//        }
//    }

    fun deleteCity(name: String) {
        val realm = Realm.getInstance(realmConfig)

        realm.executeTransactionAsync { realm ->
            val city = realm
                .where(CityInfo::class.java)
                .equalTo("name", name)
                .findFirst()

            city?.deleteFromRealm()

            Log.d(TAG, "Repo delete thread: ${Thread.currentThread()}")
        }.also { realm.close() }
    }

//    fun isCityInDb(name: String): Boolean {
//        val realm = Realm.getInstance(realmConfig)
//        var isInDb = false
//
//        realm.executeTransaction { realm ->
//            isInDb = realm
//                .where(CityInfo::class.java)
//                .equalTo("name", name)
//                .findFirst()
//                ?.let { true } ?: false
//        }
//
//        return isInDb
//    }

    fun isCityInDb(name: String): Observable<Boolean> {
        return Observable.fromCallable {
            val realm = Realm.getInstance(realmConfig)
            var isInDb = false
            realm.executeTransactionAsync {
                realm
                    .where(CityInfo::class.java)
                    .equalTo("name", name)
                    .findFirst()?.let {
                        isInDb = true
                    }
                Log.d(TAG, "isCityInDb thread: ${Thread.currentThread()}")

                isInDb
            }.also { realm.close() }

//            realm.where(CityInfo::class.java).equalTo("name", name).findFirstAsync()
            isInDb
        }

//        realm.where(CityInfo::class.java).equalTo("name", name).findAllAsync()

//        return realm.where(CityInfo::class.java).equalTo("name", name).findFirstAsync()

//        return realm.where(CityInfo::class.java).equalTo("name", name).findFirstAsync()
//        val realm = Realm.getInstance(realmConfig)
//        var isInDb = false
//


//        realm.executeTransaction {
//            val obj = realm
//                .where(CityInfo::class.java)
//                .equalTo("name", name)
//                .findFirst()
//
//            obj?.let {
//                isInDb = true
//            }
//
//            Log.d(TAG, "isCityInDb thread: ${Thread.currentThread()}")
//
//        }.also { realm.close() }
//
//        return Observable.just(isInDb)
    }

//    fun getCities(): MutableList<CityInfo> {
//        val realm = Realm.getInstance(realmConfig)
//        val cities = mutableListOf<CityInfo>()
//
//        realm.executeTransaction { realm ->
//            cities.addAll(
//                realm.where(CityInfo::class.java).findAll()
//            )
//        }
//
//        return cities
//    }

    // TODO cannot read cities from db
    fun getCities(): Observable<List<CityInfo>> {
        val realm = Realm.getInstance(realmConfig)

//        return Observable.fromCallable {
//            val realm = Realm.getInstance(realmConfig)
//            var citiesList = listOf<CityInfo>()
//            realm.executeTransactionAsync { realm ->
//                val cities = realm.where(CityInfo::class.java).findAll()
//                citiesList = realm.copyFromRealm(cities)
//                Log.d(TAG, "Cities list size: ${citiesList.size}")
//            }.also { realm.close() }
//
//            citiesList
//        }

        return Observable.fromArray(realm.copyFromRealm(realm.where(CityInfo::class.java).findAllAsync()).also { realm.close() })
//        val realm = Realm.getInstance(realmConfig)
//        val realmResultData = realm.where(CityInfo::class.java).findAll()
//        val data = realm.copyFromRealm(realmResultData)
//
//        realm.close()
//
//        Log.d(TAG, "getCities on thread: ${Thread.currentThread()}")
//        Log.d(TAG, "Repo: cities list size: ${data.size}")
//
//        return Observable.fromArray(data)

//        realm.close()
//        return tmp
//        return Single.fromCallable {
//            Realm.getInstance(realmConfig).where(CityInfo::class.java).findAllAsync()
//        }
//        return Observable.from(Realm.getInstance(realmConfig).where(CityInfo::class.java).findAll())
//        val data = Realm.getInstance(realmConfig).use { realm ->
//            realm.executeTransactionAsync { bgRealm ->
//                bgRealm.where(CityInfo::class.java).findAll()
//            }
//        }
//        val realm = Realm.getInstance(realmConfig)
//
//        val data = realm.executeTransactionAsync { realm ->
//            realm.where(CityInfo::class.java).findAll()
//        }



//        val res = realm.executeTransactionAsync { realm ->
//            realm
//                .where(CityInfo::class.java)
//                .findAllAsync()
//        }
    }

}