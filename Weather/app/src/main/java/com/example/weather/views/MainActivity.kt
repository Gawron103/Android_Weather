package com.example.weather.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.weather.R
import com.example.weather.utils.Const
import com.example.weather.utils.PermissionsChecker
import com.example.weather.views.fragments.AddCityFragment
import com.example.weather.views.fragments.CitiesMainWeatherFragment
import com.example.weather.views.fragments.DetailedWeatherFragment
import com.example.weather.views.fragments.PermissionsFragment
import com.example.weather.views.interfaces.Communicator

class MainActivity : AppCompatActivity(), Communicator {

    private val TAG = "MainActivity"
    private val permissionsRequesterLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            Log.d(TAG, "Permissions entries: ${permissions.entries}")
            val isGranted = permissions.entries.all {
                it.value == true
            }

            if (isGranted) {
                pushFragment(CitiesMainWeatherFragment(), CitiesMainWeatherFragment.TAG)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!PermissionsChecker.hasLocalizationPermissions(this, Const.neededPermissions)) {
            // permissions not granted
            Log.d(TAG, "PERMISSIONS NOT GRANTED")

            permissionsRequesterLauncher.launch(Const.neededPermissions)
        }
        else {
            Log.d(TAG, "PERMISSIONS ALREADY GRANTED")
            pushFragment(CitiesMainWeatherFragment(), CitiesMainWeatherFragment.TAG)
        }
    }

    override fun pushFragment(fragment: Fragment, tag: String) {
        when (tag) {
            CitiesMainWeatherFragment.TAG -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment, tag)
                    .addToBackStack(tag)
                    .commit()
            }

            DetailedWeatherFragment.TAG -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment, tag)
                    .addToBackStack(tag)
                    .commit()
            }

            AddCityFragment.TAG -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment, tag)
                    .addToBackStack(tag)
                    .commit()
            }

            PermissionsFragment.TAG -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment, tag)
                    .addToBackStack(tag)
                    .commit()
            }
        }
    }

    override fun popFragment(tag: String) {
        supportFragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

}