//package com.example.weather.viewmodels
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.example.weather.repositories.PlacesRepository
//import java.lang.IllegalArgumentException
//
//class PlacesViewModelFactory constructor(private val repository: PlacesRepository): ViewModelProvider.Factory {
//
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        return when {
//            modelClass.isAssignableFrom(PlacesViewModel::class.java) -> {
//                PlacesViewModel(repository) as T
//            }
//            else -> {
//                throw IllegalArgumentException("Places ViewModel not found")
//            }
//        }
//    }
//}