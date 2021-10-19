package com.example.weather.viewmodels

import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.db.CityInfo
import com.example.weather.models.CityModel
import com.example.weather.repositories.WeatherRepository
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.io
import kotlinx.coroutines.launch

class CitiesDataViewModel constructor(
    private val repository: WeatherRepository
): ViewModel() {

    private val TAG = "CitiesDataViewModel"

    private val _isRequestCorrect = MutableLiveData<Boolean>()
    val isRequestCorrect: LiveData<Boolean>
        get() = _isRequestCorrect
    private val _weatherLoading = MutableLiveData<Boolean>()
    val weatherLoading: LiveData<Boolean>
        get() = _weatherLoading
    private val _cityAdded = MutableLiveData<Boolean>()
    val cityAdded: LiveData<Boolean>
        get() = _cityAdded
    private var _citiesLiveData = MutableLiveData<MutableList<CityModel>>()
    val citiesLiveData: LiveData<MutableList<CityModel>>
        get() = _citiesLiveData
    private var citiesLists = mutableListOf<CityModel>()

    private val compositeDisposable = CompositeDisposable()

    fun refresh() {
        _weatherLoading.value = true
        citiesLists.clear()

        val disposable = repository.getCities()
            .flatMap { citiesList ->
                Log.d(TAG, "Cities size in ViewModel: ${citiesList.size}")
                Observable.fromIterable(citiesList)
            }
            .flatMap { city ->
                Log.d(TAG, "ViewModel refresh thread: ${Thread.currentThread()}")
                repository.getCoordinates(city.name)
            }
            .flatMap { location ->
                repository.getWeather(location[0].lat!!, location[0].lon!!)
                    .map { location to it }
            }
            .flatMap { (location, weather) ->
                repository.getPlaceId(location[0].cityName!!)
                    .map { places ->
                        CityModel(
                            weather,
                            location,
                            places
                        )
                    }
            }
            .subscribeOn(io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { model ->
                    citiesLists.add(model)
                },
                {
                    Log.d(TAG, "Error appeared. Error: $it")
                },
                {
                    _citiesLiveData.value = citiesLists
                    _weatherLoading.value = false
                    Log.d(TAG, "ViewModel refresh() onComplete")
                }
            )

        compositeDisposable.add(disposable)
    }

    fun addCity(name: String) {
        val disposable = repository.getCoordinates(name)
            .flatMap { location ->
                repository.isCityInDb(location[0].cityName!!)
                    .map { location to it }
                    .filter { (_, isCityInDb) ->
                        Log.d(TAG, "isCityInDb: $isCityInDb")
                        !isCityInDb
                    }
            }
            .flatMap { (location, _) ->
                repository.getWeather(location[0].lat!!, location[0].lon!!)
                    .map { location to it }
            }
            .flatMap { (location, weather) ->
                repository.getPlaceId(location[0].cityName!!)
                    .map { places ->
                        CityModel(
                            weather,
                            location,
                            places
                        )
                    }
            }
            .subscribeOn(io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { model ->
                    repository.addCity(model.locationModel?.get(0)?.cityName!!)
                    citiesLists.add(model)
                },
                {
                    Log.d(TAG, "Error adding city. Error: $it")
                },
                {
                    Log.d(TAG, "AddCity onComplete")
                    _citiesLiveData.value = citiesLists
                }
            )

        compositeDisposable.add(disposable)


//        val disposable = repository.isCityInDb(name)
//            .subscribeOn(io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                {
//                    Log.d(TAG, "Is city already in DB? $it")
//                },
//                {
//                    Log.d(TAG, "Error appeared. Error: $it")
//                    Log.d(TAG, "Error appeared. Error details: ${it.message}")
//                },
//                {
//                    Log.d(TAG, "onComplete")
//                }
//            )
//
//        compositeDisposable.add(disposable)

//        viewModelScope.launch {
//            _weatherLoading.value = true
//            _isRequestCorrect.value = true
//
//            repository.getCoordinates(name)?.let { location ->
//                if (location.isNotEmpty()) {
//                    val correctName = location[0].cityName!!
//                    val isAlreadyAdded = repository.isCityInDb(correctName)
//                    if (!isAlreadyAdded) {
//                        repository.getWeather(location[0].lat!!, location[0].lon!!)?.let { weather ->
//                            repository.getPlaceId(correctName)?.let { places ->
//                                CityModel(
//                                    weather,
//                                    location,
//                                    places
//                                )
//                            }
//                        }
//                    }
//                    else {
//                        _cityAdded.value = false
//                        null
//                    }
//                }
//                else {
//                    _isRequestCorrect.value = false
//                    null
//                }
//            }?.let { city ->
//                repository.addCity(city.locationModel?.get(0)?.cityName!!)
//                citiesLists.add(city)
//                _citiesLiveData.value = citiesLists
//                _cityAdded.value = true
//            }
//
//            _weatherLoading.value = false
//        }
    }

    fun removeCity(cityModel: CityModel) {
        val disposable = Observable.fromCallable {
                repository.deleteCity(cityModel.locationModel?.get(0)?.cityName!!)
                Log.d(TAG, "ViewModel remove thread: ${Thread.currentThread()}")
            }
            .map {
                citiesLists.remove(cityModel)
            }
            .subscribeOn(io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d(TAG, "Delete result: $it")
                },
                {
                    Log.d(TAG, "Error appeared. Error: $it")
                },
                {
                    Log.d(TAG, "onCompleted delete")
                    _citiesLiveData.value = citiesLists
                }
            )

        compositeDisposable.add(disposable)

//        viewModelScope.launch {
//            citiesLists.remove(cityModel)
//            repository.deleteCity(cityModel.locationModel?.get(0)?.cityName!!)
//            _citiesLiveData.value = citiesLists
//        }
    }

}