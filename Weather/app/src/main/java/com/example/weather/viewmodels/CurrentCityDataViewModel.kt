package com.example.weather.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.models.CityModel
import com.example.weather.repositories.WeatherRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CurrentCityDataViewModel constructor(
    private val repository: WeatherRepository
): ViewModel() {

    private val TAG = "CurrentCityDataViewModel"

    private val _isLoadingWeather = MutableLiveData<Boolean>()
    val isLoadingWeather: LiveData<Boolean>
        get() = _isLoadingWeather
    private val _cityModel = MutableLiveData<CityModel>()
    val cityModel: LiveData<CityModel>
        get() = _cityModel

    private val _compositeDisposable = CompositeDisposable()

    fun refresh(lat: Double, lon: Double) {
        _isLoadingWeather.value = true

        val disposable = repository.getNameForLocation(lat, lon)
            .flatMap { location ->
                repository.getWeather(lat, lon)
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
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { model ->
                    _cityModel.value = model
                },
                { error ->
                    Log.d(TAG, "Error appeared. Error: $error")
                },
                {
                    _isLoadingWeather.value = false
                }
            )

        _compositeDisposable.add(disposable)
    }

    fun getLatitude() = _cityModel.value?.locationModel?.get(0)?.lat
    fun getLongitude() = _cityModel.value?.locationModel?.get(0)?.lon
    fun getTemperature() = _cityModel.value?.weatherModel?.currentConditions?.temp
    fun getWeatherDesc() = _cityModel.value?.weatherModel?.currentConditions?.weather?.get(0)?.desc
    fun getPhotoRef() = _cityModel.value?.placesModel?.candidates?.get(0)?.photos?.get(0)?.photo_reference
    fun getLocationName() = StringBuilder()
            .append(_cityModel.value?.locationModel?.get(0)?.countryCode)
            .append(", ")
            .append(_cityModel.value?.locationModel?.get(0)?.cityName)
            .toString()

}