package com.example.weather.views.interfaces

import androidx.fragment.app.Fragment

interface Communicator {
    fun pushFragment(fragment: Fragment, tag: String)
    fun popFragment(tag: String)
}