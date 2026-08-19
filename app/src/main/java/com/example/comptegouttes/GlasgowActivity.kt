package com.example.comptegouttes

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GlasgowActivity : AppCompatActivity() {

    private lateinit var swipe: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glasgow)
        setupBottomNav("calc")
        swipe = SwipeNav.create(this, "calc")

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        val rgYeux = findViewById<RadioGroup>(R.id.rgYeux)
        val rgVerbale = findViewById<RadioGroup>(R.id.rgVerbale)
        val rgMotrice = findViewById<RadioGroup>(R.id.rgMotrice)
        val tvScore = findViewById<TextView>(R.id.tvScore)
        val tvInterpretation = findViewById<TextView>(R.id.tvInterpretation)

        findViewById<Button>(R.id.btnCalculer).setOnClickListener {
            val score = valueOf(rgYeux) + valueOf(rgVerbale) + valueOf(rgMotrice)
            tvScore.text = "$score / 15"
            tvInterpretation.text = when {
                score <= 8 -> "Coma sévère (score ≤ 8)."
                score <= 12 -> "Coma modéré (score 9 à 12)."
                else -> "Coma léger / quasi normal (score 13 à 15)."
            }
        }
    }

    /** Valeur cochée = position du bouton dans la liste + 1. */
    private fun valueOf(rg: RadioGroup): Int {
        val checked = rg.checkedRadioButtonId
        if (checked == -1) return 1
        val idx = rg.indexOfChild(rg.findViewById(checked))
        return if (idx < 0) 1 else idx + 1
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        swipe.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }
}
