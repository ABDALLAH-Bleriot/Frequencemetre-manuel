package com.example.comptegouttes

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val firstRun = prefs.getBoolean("first_run", true)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            if (firstRun) {
                prefs.edit().putBoolean("first_run", false).apply()
                startActivity(Intent(this, TutorialActivity::class.java))
            }
            finish()
        }, 2500)
    }
}
