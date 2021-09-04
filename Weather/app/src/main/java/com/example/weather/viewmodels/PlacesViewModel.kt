//package com.example.weather.viewmodels
//
//import android.util.Log
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.weather.BuildConfig
//import com.example.weather.models.places_model.PlacesModel
//import com.example.weather.repositories.PlacesRepository
//import kotlinx.coroutines.launch
//import retrofit2.Response
//
//class PlacesViewModel constructor(private val repository: PlacesRepository): ViewModel() {
//
//    private val TAG = "PlacesViewModel"
//
//    val places = MutableLiveData<List<String>>()
//
//    fun refresh(citiesNames: List<String>) {
//        viewModelScope.launch {
//            val tmpModel = mutableListOf<String>()
//
//            for(cityName in citiesNames) {
//                val responseFetchPlaceId = fetchPlaceId(cityName)
//
//                if(responseFetchPlaceId.isSuccessful) {
//                    Log.d(TAG, "Response is successfull")
////                    Log.d(TAG, response.body().toString())
//                    val photoRef = responseFetchPlaceId.body()?.candidates?.get(0)?.photos?.get(0)?.photo_reference
//
//                    tmpModel.add(photoRef!!)
//                }
//                else {
//                    Log.d(TAG, "Response failed")
//                }
//            }
//
//            if(tmpModel.isNotEmpty()) {
//                places.value = tmpModel
//            }
//        }
//    }
//
//    private suspend fun fetchPlaceId(cityName: String): Response<PlacesModel> {
//        return repository.getPlaceId(
//            cityName,
//            BuildConfig.PLACES_API_KEY
//        )
//    }
//
//}