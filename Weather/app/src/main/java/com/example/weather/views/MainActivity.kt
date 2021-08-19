package com.example.weather.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.weather.R
import com.example.weather.views.fragments.CitiesMainWeatherFragment
import com.example.weather.views.fragments.DetailedWeatherFragment
import com.example.weather.views.interfaces.Communicator

class MainActivity : AppCompatActivity(), Communicator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pushFragment(CitiesMainWeatherFragment(), CitiesMainWeatherFragment.TAG)
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
        }
    }

    override fun popFragment(tag: String) {
        supportFragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}