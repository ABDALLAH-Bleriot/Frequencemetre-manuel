package com.example.comptegouttes

import android.app.Activity
import android.content.Intent
import android.view.View

fun Activity.setupBottomNav(currentScreen: String) {
    val navButtons = listOf(
        R.id.btnNavCounter to "counter",
        R.id.btnNavGenerator to "generator",
        R.id.btnNavCalc to "calc",
        R.id.btnNavInfo to "info"
    )

    for ((id, screen) in navButtons) {
        val button = findViewById<View>(id)
        if (screen == currentScreen) {
            button.alpha = 1f
            button.setBackgroundResource(R.drawable.bg_neon_nav_selected)
        } else {
            button.alpha = 0.55f
            button.setBackgroundResource(R.drawable.bg_neon_nav)
            button.setOnClickListener {
                val target = when (screen) {
                    "counter" -> MainActivity::class.java
                    "generator" -> GeneratorActivity::class.java
                    "calc" -> CalcActivity::class.java
                    else -> InfoActivity::class.java
                }
                startActivity(
                    Intent(this, target).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                )
            }
        }
    }
}
