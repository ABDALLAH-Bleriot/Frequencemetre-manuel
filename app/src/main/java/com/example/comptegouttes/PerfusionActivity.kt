package com.example.comptegouttes

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class PerfusionActivity : AppCompatActivity() {

    private lateinit var swipe: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfusion)
        setupBottomNav("calc")
        swipe = SwipeNav.create(this, "calc")

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        val etVolume = findViewById<EditText>(R.id.etVolume)
        val etDuree = findViewById<EditText>(R.id.etDuree)
        val rgUnite = findViewById<RadioGroup>(R.id.rgUnite)
        val btnCalculer = findViewById<Button>(R.id.btnCalculer)

        val tvDebit = findViewById<TextView>(R.id.tvDebit)
        val tvGouttesMin = findViewById<TextView>(R.id.tvGouttesMin)
        val tvGouttesSec = findViewById<TextView>(R.id.tvGouttesSec)
        val tvGouttes10Sec = findViewById<TextView>(R.id.tvGouttes10Sec)

        btnCalculer.setOnClickListener {
            val volume = num(etVolume)
            val duree = num(etDuree)

            if (volume == null || duree == null) {
                Toast.makeText(this, "Veuillez saisir le volume et la durée", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (volume <= 0 || duree <= 0) {
                Toast.makeText(this, "Valeurs positives requises", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isMinutes = rgUnite.checkedRadioButtonId == R.id.rbMinutes
            val dureeEnHeures = if (isMinutes) duree / 60.0 else duree

            val debitMlH = volume / dureeEnHeures
            val gouttesMin = volume * 20.0 / (dureeEnHeures * 60.0)
            val gouttesSec = gouttesMin / 60.0
            val gouttes10Sec = gouttesSec * 10.0

            tvDebit.text = String.format(Locale.FRANCE, "%.2f", debitMlH)
            tvGouttesMin.text = String.format(Locale.FRANCE, "%.2f", gouttesMin)
            tvGouttesSec.text = String.format(Locale.FRANCE, "%.2f", gouttesSec)
            tvGouttes10Sec.text = String.format(Locale.FRANCE, "%.2f", gouttes10Sec)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        swipe.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    private fun num(et: EditText): Double? =
        et.text.toString().replace(',', '.').trim().toDoubleOrNull()
}
