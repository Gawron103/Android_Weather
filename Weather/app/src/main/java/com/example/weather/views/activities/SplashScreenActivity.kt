package com.example.weather.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.example.weather.databinding.ActivitySplashScreenBinding
import com.example.weather.utils.Const
import com.example.weather.utils.PermissionsChecker

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.clSplashScreenLayout.visibility = View.GONE

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
        binding.clSplashScreenLayout.visibility = View.VISIBLE
        binding.clSplashScreenLayout.alpha = 0f
        binding.clSplashScreenLayout.animate().setDuration(3000).alpha(1f).withEndAction {
            startActivity(Intent(this, activityName))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            finish()
        }
    }

}