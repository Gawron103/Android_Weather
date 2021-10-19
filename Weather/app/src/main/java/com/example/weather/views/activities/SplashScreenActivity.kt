package com.example.weather.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.example.weather.R
import com.example.weather.utils.Const
import com.example.weather.utils.PermissionsChecker

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.SplashScreenTheme)
        super.onCreate(savedInstanceState)

        checkPermissions()
    }

    private val permissionsRequesterLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val activityName = if (result.all { it.value == true }) MainActivity::class.java else NoPermissionsActivity::class.java
        changeActivity(activityName)
    }

    private fun checkPermissions() {
        if(!PermissionsChecker.hasLocalizationPermissions(this, Const.neededPermissions)) {
            // permissions not granted, request
            permissionsRequesterLauncher.launch(Const.neededPermissions)
        }
        else {
            // permissions already granted
            changeActivity(MainActivity::class.java)
        }
    }

    private fun changeActivity(activityName: Class<out AppCompatActivity>) {
        startActivity(Intent(this, activityName))
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        finish()
    }

}