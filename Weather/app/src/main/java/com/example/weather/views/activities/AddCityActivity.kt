package com.example.weather.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.weather.R
import com.example.weather.utils.InputValidator
//import com.example.weather.views.fragments.AddCityFragment

class AddCityActivity : AppCompatActivity() {

    private val TAG = "AddCityActivity"
    private lateinit var cityNameInput: EditText
    private lateinit var validator: InputValidator

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