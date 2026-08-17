package com.example.comptegouttes

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class GestogrammeActivity : AppCompatActivity() {

    private lateinit var swipe: GestureDetector
    private lateinit var etDate: EditText
    private lateinit var etEchoSA: EditText
    private lateinit var etEchoJ: EditText
    private lateinit var rgMode: RadioGroup
    private var formatting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestogramme)
        setupBottomNav("calc")
        swipe = SwipeNav.create(this, "calc")

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        etDate = findViewById(R.id.etDate)
        etEchoSA = findViewById(R.id.etEchoSA)
        etEchoJ = findViewById(R.id.etEchoJ)
        rgMode = findViewById(R.id.rgMode)

        // Saisie facilitée : les barres / / s'ajoutent toutes seules
        etDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (formatting || s == null) return
                val digits = s.toString().replace(Regex("\\D"), "").take(8)
                val sb = StringBuilder()
                for (i in digits.indices) {
                    if (i == 2 || i == 4) sb.append('/')
                    sb.append(digits[i])
                }
                val formatted = sb.toString()
                if (formatted != s.toString()) {
                    formatting = true
                    s.clear()
                    s.append(formatted)
                    formatting = false
                }
            }
        })

        findViewById<Button>(R.id.btnCalendar).setOnClickListener { openCalendar() }
        findViewById<Button>(R.id.btnCalculerGesto).setOnClickListener { calculer() }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        swipe.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    private fun openCalendar() {
        val c = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            etDate.setText(String.format(Locale.FRANCE, "%02d/%02d/%04d", day, month + 1, year))
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun parseDate(s: String): Date? {
        val f = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        f.isLenient = false
        return try { f.parse(s.trim()) } catch (e: Exception) { null }
    }

    private fun fmt(d: Date): String = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(d)

    private fun addDays(d: Date, days: Int): Date = Date(d.time + days * 86400000L)

    private fun calculer() {
        val date = parseDate(etDate.text.toString())
        if (date == null) {
            Toast.makeText(this, "Date invalide. Format : jj/mm/aaaa", Toast.LENGTH_SHORT).show()
            return
        }

        val sa = etEchoSA.text.toString().toIntOrNull() ?: 0
        val j = etEchoJ.text.toString().toIntOrNull() ?: 0

        val ddr = when (rgMode.checkedRadioButtonId) {
            R.id.rbConception -> addDays(date, -14)
            R.id.rbTerme -> addDays(date, -280)
            R.id.rbEcho -> addDays(date, -(sa * 7 + j))
            else -> date
        }

        val today = Date()
        val daysToday = ((today.time - ddr.time) / 86400000L).toInt()

        set(R.id.tvRegles, fmt(ddr))
        set(R.id.tvConception, fmt(addDays(ddr, 14)))
        set(R.id.tvTerme, fmt(addDays(ddr, 280)) + " (± 5 j)")
        set(R.id.tvTermeActuel,
            if (daysToday in 0..308) "${daysToday / 7} SA + ${daysToday % 7} j" else "—")
        set(R.id.tvPostTerme, fmt(addDays(ddr, 294)))

        set(R.id.tvEcho1, "du ${fmt(addDays(ddr, 77))} au ${fmt(addDays(ddr, 97))}")
        set(R.id.tvEcho2, "du ${fmt(addDays(ddr, 154))} au ${fmt(addDays(ddr, 168))}")
        set(R.id.tvEcho3, "du ${fmt(addDays(ddr, 224))} au ${fmt(addDays(ddr, 238))}")

        set(R.id.tvT21Du, fmt(addDays(ddr, 77)))
        set(R.id.tvT21Au, fmt(addDays(ddr, 97)) + " inclus")
    }

    private fun set(id: Int, text: String) {
        findViewById<TextView>(id).text = text
    }
}
