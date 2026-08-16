package com.example.comptegouttes

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class CalcActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calc)
        setupBottomNav("calc")

        val container = findViewById<LinearLayout>(R.id.calcList)

        val sorted = CalcRegistry.all.sortedBy {
            it.title.lowercase(Locale.FRANCE).replace(Regex("\\P{L}"), "")
        }

        for (def in sorted) {
            val button = Button(this)
            button.text = def.title
            button.setTextColor(-0x1)
            button.textSize = 15f
            button.typeface = Typeface.DEFAULT_BOLD
            button.setBackgroundResource(R.drawable.bg_button_gradient)
            button.setPadding(dp(18), dp(16), dp(18), dp(16))

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, dp(12))
            container.addView(button, params)

            button.setOnClickListener {
                val target = def.customActivity ?: CalcDetailActivity::class.java
                startActivity(Intent(this, target).putExtra("calc_id", def.id))
            }
        }
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}
