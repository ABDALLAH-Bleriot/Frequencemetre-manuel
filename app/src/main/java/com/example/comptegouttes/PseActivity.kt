package com.example.comptegouttes

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class PseActivity : AppCompatActivity() {

    private lateinit var swipe: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pse)
        setupBottomNav("calc")
        swipe = SwipeNav.create(this, "calc")

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        val etDebitVoulu = findViewById<EditText>(R.id.etDebitVoulu)
        val etPoids = findViewById<EditText>(R.id.etPoids)
        val etDose = findViewById<EditText>(R.id.etDose)
        val etVolumeSeringue = findViewById<EditText>(R.id.etVolumeSeringue)
        val btnCalculer = findViewById<Button>(R.id.btnCalculerPse)

        val tvDebitPSE = findViewById<TextView>(R.id.tvDebitPSE)
        val tvDebitDrogue = findViewById<TextView>(R.id.tvDebitDrogue)
        val tvAutonomie = findViewById<TextView>(R.id.tvAutonomie)
        val tvGouttesMin = findViewById<TextView>(R.id.tvGouttesMinPse)

        btnCalculer.setOnClickListener {
            val debitVoulu = num(etDebitVoulu)
            val poids = num(etPoids)
            val dose = num(etDose)
            val volume = num(etVolumeSeringue)

            if (debitVoulu == null || poids == null || dose == null || volume == null) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (debitVoulu <= 0 || poids <= 0 || dose <= 0 || volume <= 0) {
                Toast.makeText(this, "Veuillez entrer des nombres positifs valides", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val debitPSE = (debitVoulu * poids * volume * 60.0) / (dose * 1000.0)
            val debitDrogue = debitVoulu * poids * 60.0 / 1000.0
            val autonomieH = volume / debitPSE
            val heures = autonomieH.toInt()
            val minutes = ((autonomieH - heures) * 60).toInt()
            val gouttesMin = debitPSE * 20.0 / 60.0

            tvDebitPSE.text = String.format(Locale.FRANCE, "%.2f", debitPSE)
            tvDebitDrogue.text = String.format(Locale.FRANCE, "%.2f", debitDrogue)
            tvAutonomie.text = String.format(Locale.FRANCE, "%d h %02d min", heures, minutes)
            tvGouttesMin.text = String.format(Locale.FRANCE, "%.2f", gouttesMin)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        swipe.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    private fun num(et: EditText): Double? =
        et.text.toString().replace(',', '.').trim().toDoubleOrNull()
}
