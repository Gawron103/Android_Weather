package com.example.weather.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.weather.R
import com.example.weather.utils.InputValidator
import com.example.weather.viewmodels.AddCityViewModel
//import com.example.weather.views.fragments.AddCityFragment
import com.example.weather.views.fragments.DetailedWeatherFragment
import com.example.weather.views.interfaces.Communicator

class AddCityActivity : AppCompatActivity() {

    private lateinit var cityNameInput: EditText
    private lateinit var validator: InputValidator

    companion object {
        val TAG = DetailedWeatherFragment::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_city)

        cityNameInput = findViewById<EditText>(R.id.etCityNameInput)
        validator = InputValidator()

        val backBtn = findViewById<Button>(R.id.btn_backToCitiesView)
        backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val addBtn = findViewById<Button>(R.id.btn_addCity)
        addBtn.setOnClickListener {

            val cityToAdd = cityNameInput.text.toString()

            val result = validator.checkInput(cityToAdd)

            if (result) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("NewCityName", cityToAdd)
                startActivity(intent)
            }
            else {
                Toast.makeText(this, "Wrong input!", Toast.LENGTH_LONG)
            }
        }
    }

}