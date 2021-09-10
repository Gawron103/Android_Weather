//package com.example.weather.viewmodels
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.example.weather.repositories.CityRepository
//import java.lang.IllegalArgumentException
//
//class CityViewModelFactory constructor(private val repository: CityRepository): ViewModelProvider.Factory {
//
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        return when {
//            modelClass.isAssignableFrom(CityDBViewModel::class.java) -> {
//                CityDBViewModel(repository) as T
//            }
//            else -> {
//                throw IllegalArgumentException("City weather ViewModel not found")
//            }
//        }
//    }
//
//}