package com.example.comptegouttes

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.ArrayDeque
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var swipe: GestureDetector

    private val handler = Handler(Looper.getMainLooper())

    private val ticker = object : Runnable {
        override fun run() {
            updateChronometerAndState()
            handler.postDelayed(this, 100L)
        }
    }

    private var chronometerStart = 0L
    private var chronometerRunning = false

    private val intervals = ArrayDeque<Long>()
    private var totalTaps = 0
    private var lastTap = 0L

    private val maxIntervals = 3
    private val minIntervalMs = 120L
    private val maxIntervalMs = 120000L

    private lateinit var tvResult: TextView
    private lateinit var tvUnit: TextView
    private lateinit var tvChronometer: TextView
    private lateinit var tvInfo: TextView
    private lateinit var btnTap: ImageButton
    private lateinit var btnReset: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBottomNav("counter")
        swipe = SwipeNav.create(this, "counter")
        SoundPlayer.init(this)

        tvResult = findViewById(R.id.tvResult)
        tvUnit = findViewById(R.id.tvUnit)
        tvChronometer = findViewById(R.id.tvChronometer)
        tvInfo = findViewById(R.id.tvInfo)
        btnTap = findViewById(R.id.btnTap)
        btnReset = findViewById(R.id.btnReset)

        btnTap.setBackgroundResource(R.drawable.bg_neon_button_selector)

        btnTap.setOnClickListener { onTap() }
        btnReset.setOnClickListener { resetAll() }

        tvUnit.text = "appuis /min"
        tvChronometer.text = formatElapsed(0L)
        tvInfo.text = getInfoText()
    }

    override fun onResume() {
        super.onResume()
        if (chronometerRunning) handler.post(ticker)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(ticker)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        swipe.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    private fun flashButton(b: ImageButton) {
        b.setBackgroundResource(R.drawable.bg_neon_button_lit)
        handler.postDelayed({ b.setBackgroundResource(R.drawable.bg_neon_button_selector) }, 180)
    }

    private fun onTap() {
        val now = SystemClock.elapsedRealtime()
        if (lastTap != 0L && now - lastTap < minIntervalMs) return

        flashButton(btnTap)

        if (!chronometerRunning) {
            chronometerStart = now
            chronometerRunning = true
            handler.removeCallbacks(ticker)
            handler.post(ticker)
        }

        if (lastTap != 0L) {
            val interval = now - lastTap
            if (interval > maxIntervalMs) intervals.clear()
            else if (interval >= minIntervalMs) {
                intervals.addLast(interval)
                while (intervals.size > maxIntervals) intervals.removeFirst()
            }
        }

        lastTap = now
        totalTaps++
        SoundPlayer.play()

        updateDisplay()
        tvInfo.text = getInfoText()
    }

    private fun updateChronometerAndState() {
        if (!chronometerRunning) return
        val now = SystemClock.elapsedRealtime()
        tvChronometer.text = formatElapsed(now - chronometerStart)
        updateDisplay()
    }

    /** Affiche TOUJOURS un résultat, même avec un seul appui ou des appuis très espacés. */
    private fun updateDisplay() {
        if (totalTaps == 0) {
            tvResult.text = "—"
            tvUnit.text = "appuis /min"
            return
        }

        val now = SystemClock.elapsedRealtime()
        val elapsedMs = now - chronometerStart

        // 1) Rythme mesuré entre les appuis (si au moins 2 appuis récents)
        var rate: Double? = null
        if (intervals.isNotEmpty() && now - lastTap <= 60000L) {
            var sum = 0L
            for (v in intervals) sum += v
            rate = 60000.0 / (sum.toDouble() / intervals.size)
        }

        // 2) Sinon : moyenne globale depuis le 1er appui
        if (rate == null) {
            if (totalTaps == 1 && elapsedMs < 60000L) {
                tvResult.text = "1"
                tvUnit.text = "appui /min"
                return
            }
            val elapsedMin = Math.max(elapsedMs, 1000L) / 60000.0
            rate = totalTaps / elapsedMin
        }

        // Format du résultat
        if (rate >= 1.0) {
            tvResult.text = Math.round(rate).toString()
            tvUnit.text = "appuis /min"
        } else {
            tvResult.text = "1"
            tvUnit.text = "appui / " + Math.max(1L, Math.round(1.0 / rate)) + " min"
        }
    }

    private fun formatElapsed(ms: Long): String {
        val totalSeconds = ms / 1000L
        val minutes = totalSeconds / 60L
        val seconds = totalSeconds % 60L
        val tenths = (ms % 1000L) / 100L
        return String.format(Locale.US, "%02d:%02d.%d", minutes, seconds, tenths)
    }

    private fun getInfoText(): String {
        return when {
            totalTaps == 0 -> "Appuyez sur le bouton en rythme"
            totalTaps == 1 -> "Encore 1 appui pour le rythme"
            else -> "$totalTaps appuis"
        }
    }

    private fun resetAll() {
        handler.removeCallbacks(ticker)
        chronometerRunning = false
        chronometerStart = 0L
        lastTap = 0L
        totalTaps = 0
        intervals.clear()
        tvChronometer.text = formatElapsed(0L)
        tvResult.text = "—"
        tvUnit.text = "appuis /min"
        tvInfo.text = getInfoText()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
