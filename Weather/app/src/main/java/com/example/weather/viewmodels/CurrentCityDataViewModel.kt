package com.example.weather.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.models.CityModel
import com.example.weather.repositories.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    fun refresh(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoadingWeather.postValue(true)

            val location = repository.getNameForLocation(lat, lon)
            val weather = repository.getWeather(location[0].lat!!, location[0].lon!!)
            val places = repository.getPlaceId(location[0].cityName!!)

            Log.d(TAG, "Refresh running on thread: ${Thread.currentThread()}")

            _cityModel.postValue(CityModel(weather, location, places))

            _isLoadingWeather.postValue(false)
        }
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