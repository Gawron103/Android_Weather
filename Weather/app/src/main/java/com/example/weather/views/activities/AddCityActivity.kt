package com.example.weather.views.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.weather.R
import com.example.weather.databinding.ActivityAddCityBinding
import com.example.weather.utils.InputValidator

class AddCityActivity : AppCompatActivity() {

    private val TAG = "AddCityActivity"

    private lateinit var binding: ActivityAddCityBinding

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Activity destroyed")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnBackToCitiesView.setOnClickListener {
            finish()
        }

        binding.btnAddCity.setOnClickListener {
            val cityToAdd = binding.etCityNameInput.text.toString()

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