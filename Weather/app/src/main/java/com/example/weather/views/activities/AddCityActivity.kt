package com.example.weather.views.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.weather.R
import com.example.weather.utils.InputValidator

class AddCityActivity : AppCompatActivity() {

    private val TAG = "AddCityActivity"
    private lateinit var cityNameInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_city)

        cityNameInput = findViewById(R.id.etCityNameInput)

        val backBtn = findViewById<Button>(R.id.btn_backToCitiesView)
        backBtn.setOnClickListener {
            finish()
        }

        val addBtn = findViewById<Button>(R.id.btn_addCity)
        addBtn.setOnClickListener {
            val cityToAdd = cityNameInput.text.toString()

            val result = InputValidator.checkInput(cityToAdd)

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

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_ENTER -> hideKeyboard()

            else -> super.onKeyUp(keyCode, event)
        }
    }
}

fun AddCityActivity.hideKeyboard(): Boolean {
    val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    return imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
}