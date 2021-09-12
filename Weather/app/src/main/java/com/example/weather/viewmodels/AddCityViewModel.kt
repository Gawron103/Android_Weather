package com.example.weather.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddCityViewModel: ViewModel() {

    private val TAG = "AddCityViewModel"

    val cityName = MutableLiveData<String>()

    fun setCityName(name: String) {
        Log.d(TAG, "Setted city name live data")
        cityName.value = name
    }

}