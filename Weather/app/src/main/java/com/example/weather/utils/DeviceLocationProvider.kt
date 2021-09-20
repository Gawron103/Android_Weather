//package com.example.weather.utils
//
//import android.Manifest
//import android.app.Activity
//import android.content.Context
//import android.content.pm.PackageManager
//import android.os.Looper
//import android.util.Log
//import androidx.activity.result.ActivityResultLauncher
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.example.weather.views.fragments.CitiesMainWeatherFragment
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationCallback
//import com.google.android.gms.location.LocationRequest
//import com.google.android.gms.location.LocationServices
//
//object DeviceLocationProvider {
//
//    private val TAG = "DeviceLocationProvider"
//
//    fun getLastLocation(context: Context): Pair<Double, Double>? {
//        var retVal: Pair<Double, Double>? = null
//        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
//
//            if (ActivityCompat.checkSelfPermission(
//                    context,
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                    context,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                Log.d(TAG, "We shouldn't be here")
//                return retVal
//            }
//
//            fusedLocationProviderClient.lastLocation
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful && null != task.result) {
//                        Log.d(TAG, "Location task is successful")
//                        retVal = Pair(task.result.latitude, task.result.longitude)
//                    }
//                    else if (null == task.result) {
//                        newLocationData()
//                    }
//                    else {
//                        Log.d(TAG, "Location task exception: ${task.exception}")
//                    }
//                }
//
//        Log.d(TAG, "Just before returning coords. Are they null? ${retVal == null}")
//        return retVal
//    }
//
//    private fun newLocationData(locationClient: FusedLocationProviderClient) {
//        var locationRequest = LocationRequest.create().apply {
//            interval = 0
//            fastestInterval = 0
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//            numUpdates = 1
//            maxWaitTime= 100
//        }
//
//        locationClient.requestLocationUpdates(
//            locationRequest, locationCallback, Looper.myLooper()
//        )
//    }
//
//    private fun locationCallback() {
//
//    }
//}