package com.example.weather.views.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.weather.R
import com.example.weather.utils.InputValidator

class AddCityActivity : AppCompatActivity() {

    private val TAG = "AddCityActivity"
    private lateinit var cityNameInput: EditText

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Activity destroyed")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_city)

        Log.d(TAG, "onCreate")

        cityNameInput = findViewById(R.id.etCityNameInput)

        val backBtn = findViewById<Button>(R.id.btn_backToCitiesView)
        backBtn.setOnClickListener {
            finish()
        }

        val addBtn = findViewById<Button>(R.id.btn_addCity)
        addBtn.setOnClickListener {
            val cityToAdd = cityNameInput.text.toString()

            val intent = Intent(this, MainActivity::class.java)

            if (InputValidator.checkInput(cityToAdd)) {
                intent.putExtra("NewCity", cityToAdd)
                setResult(Activity.RESULT_OK, intent)
            }
            else {
                setResult(Activity.RESULT_CANCELED, intent)
            }

            finish()
        }
    }

}