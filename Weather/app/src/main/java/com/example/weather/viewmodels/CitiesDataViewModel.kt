package com.example.weather.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.models.CityModel
import com.example.weather.repositories.WeatherRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io

class CitiesDataViewModel constructor(
    private val repository: WeatherRepository
): ViewModel() {

    private val TAG = "CitiesDataViewModel"

    private val _weatherLoading = MutableLiveData<Boolean>()
    val weatherLoading: LiveData<Boolean>
        get() = _weatherLoading
    private val _cityAdded = MutableLiveData<Boolean>()
    val cityAdded: LiveData<Boolean>
        get() = _cityAdded
    private val _cityDeleted = MutableLiveData<Boolean>()
    val cityDeleted: LiveData<Boolean>
        get() = _cityDeleted
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
                Observable.fromIterable(citiesList)
            }
            .flatMap { city ->
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
                { error ->
                    Log.d(TAG, "Error appeared. Error: $error")
                },
                {
                    _citiesLiveData.value = citiesLists
                    _weatherLoading.value = false
                }
            )

        compositeDisposable.add(disposable)
    }

    fun addCity(name: String) {
        val disposable = repository.getCoordinates(name)
            .filter { location ->
                location.isNotEmpty()
            }
            .flatMap { location ->
                repository.addCity(location[0].cityName!!)
            }
            .subscribeOn(io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { isAdded ->
                    _cityAdded.value = isAdded
                },
                { error ->
                    Log.d(TAG, "Error adding city. Error: $error")
                },
                {
                    refresh()
                }
            )

        compositeDisposable.add(disposable)
    }

    fun removeCity(cityModel: CityModel) {
        val disposable = repository.deleteCity(cityModel.locationModel?.get(0)?.cityName!!)
            .subscribeOn(io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { status ->
                    if (status) {
                        citiesLists.remove(cityModel)
                        _citiesLiveData.value = citiesLists
                        _cityDeleted.value = true
                    }
                    else {
                        _cityDeleted.value = false
                    }
                },
                { error ->
                    Log.d(TAG, "Error appeared. Error: $error")
                }
            )

        compositeDisposable.add(disposable)
    }

}