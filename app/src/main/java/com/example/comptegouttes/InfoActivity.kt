package com.example.comptegouttes

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class InfoActivity : AppCompatActivity() {

    private lateinit var swipe: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        setupBottomNav("info")
        swipe = SwipeNav.create(this, "info")

        val tvPhone1 = findViewById<TextView>(R.id.tvPhone1)
        val tvPhone2 = findViewById<TextView>(R.id.tvPhone2)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val tvFacebook = findViewById<TextView>(R.id.tvFacebook)
        val tvGithub = findViewById<TextView>(R.id.tvGithub)

        // Soulignement pour montrer que c'est cliquable
        listOf(tvPhone1, tvPhone2, tvEmail, tvFacebook, tvGithub).forEach {
            it.paintFlags = it.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }

        tvPhone1.setOnClickListener { dial("+261349982080") }
        tvPhone2.setOnClickListener { dial("+261324584643") }
        tvEmail.setOnClickListener { sendEmail() }
        tvFacebook.setOnClickListener { openUrl("https://www.facebook.com/bleriot.abdallah") }
        tvGithub.setOnClickListener { openUrl("https://github.com/ABDALLAH-Bleriot/") }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        swipe.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    /** Ouvre le composeur téléphonique avec le numéro prêt à appeler. */
    private fun dial(number: String) {
        try {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")))
        } catch (_: Exception) {
        }
    }

    /** Ouvre l'application email avec l'adresse déjà remplie. */
    private fun sendEmail() {
        try {
            startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:dragonbleu47@gmail.com")))
        } catch (_: Exception) {
            try {
                val i = Intent(Intent.ACTION_SEND)
                i.type = "text/plain"
                i.putExtra(Intent.EXTRA_EMAIL, arrayOf("dragonbleu47@gmail.com"))
                startActivity(i)
            } catch (_: Exception) {
            }
        }
    }

    /** Ouvre Facebook (app si installée) ou le navigateur internet. */
    private fun openUrl(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (_: Exception) {
        }
    }
}
