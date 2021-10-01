package com.example.weather.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.weather.R
import com.example.weather.utils.Const
import com.example.weather.utils.PermissionsChecker

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var splashScreenLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        splashScreenLayout = findViewById(R.id.cl_splashScreenLayout)
        splashScreenLayout.visibility = View.GONE

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
        splashScreenLayout.visibility = View.VISIBLE
        splashScreenLayout.alpha = 0f
        splashScreenLayout.animate().setDuration(3000).alpha(1f).withEndAction {
            startActivity(Intent(this, activityName))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            finish()
        }
    }

}