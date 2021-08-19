package com.example.weather.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.example.weather.R

class ChangeLocationFragment : Fragment() {

    private val TAG = "ChangeLocationFragment"
    private lateinit var cityNameInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.change_location_fragment, container, false)

        cityNameInput = view.findViewById(R.id.etCityNameInput)
        cityNameInput.setOnEditorActionListener { _, keyCode, _ ->
            when(keyCode) {
                    KeyEvent.KEYCODE_ENDCALL -> {
                        Log.d(TAG, "Entered: ${cityNameInput.text}")
                        true
                    }
                else -> false
                }
            }

        return view
    }
}