package com.example.weather.views.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.weather.R
import com.example.weather.db.City
import com.example.weather.utils.InputValidator
import com.example.weather.views.interfaces.Communicator
import com.example.weather.views.interfaces.DatabaseCommunicator

class AddCityFragment(private val databaseCommunicator: DatabaseCommunicator) : Fragment() {

    private lateinit var cityNameInput: EditText
    private lateinit var communicator: Communicator
    private lateinit var validator: InputValidator

    companion object {
        val TAG = DetailedWeatherFragment::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.change_location_fragment, container, false)

        communicator = requireActivity() as Communicator
        validator = InputValidator()

        val backBtn = view.findViewById<Button>(R.id.btn_backToCitiesView)
        backBtn.setOnClickListener {
            communicator.popFragment(TAG)
        }

        val addBtn = view.findViewById<Button>(R.id.btn_addCity)
        addBtn.setOnClickListener {
            val result = validator.checkInput(cityNameInput.text.toString())

            if (result) {
                databaseCommunicator.addCity(City(0, cityNameInput.text.toString()))
                communicator.popFragment(TAG)
            }
            else {
                Toast.makeText(context, "Wrong input!", Toast.LENGTH_LONG)
            }
        }

        cityNameInput = view.findViewById(R.id.etCityNameInput)
        cityNameInput.setOnEditorActionListener { _, keyCode, _ ->
            if (KeyEvent.KEYCODE_ENDCALL == keyCode) closeKeyboard(requireContext()) else false
//            when(keyCode) {
//                    KeyEvent.KEYCODE_ENDCALL -> {
//                        closeKeyboard
//                        true
//                    }
//                else -> false
//                }
            }

        return view
    }

    private fun closeKeyboard(context: Context): Boolean {
        val inputMngr = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMngr.hideSoftInputFromWindow(view?.windowToken, 0)
        return true
    }
}