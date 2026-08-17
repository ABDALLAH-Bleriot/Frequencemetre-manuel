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
    private var pauseMessageShown = false

    private val intervals = ArrayDeque<Long>()
    private var totalTaps = 0
    private var lastTap = 0L

    private val maxIntervals = 3
    private val minIntervalMs = 120L
    private val maxIntervalMs = 15000L
    private val staleAfterMs = maxIntervalMs + 3000L

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

    private fun onTap() {
        val now = SystemClock.elapsedRealtime()
        if (lastTap != 0L && now - lastTap < minIntervalMs) return

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
        pauseMessageShown = false
        SoundPlayer.play()

        computeAndShowRate()
        tvInfo.text = getInfoText()
    }

    private fun computeAndShowRate() {
        if (intervals.isEmpty()) { tvResult.text = "—"; return }
        var sum = 0L
        for (value in intervals) sum += value
        val averageInterval = sum.toDouble() / intervals.size
        if (averageInterval <= 0.0) { tvResult.text = "—"; return }
        val rate = 60000.0 / averageInterval
        tvResult.text = Math.round(rate).toString()
    }

    private fun updateChronometerAndState() {
        if (!chronometerRunning) return
        val now = SystemClock.elapsedRealtime()
        tvChronometer.text = formatElapsed(now - chronometerStart)
        if (lastTap != 0L && now - lastTap > staleAfterMs) {
            if (!pauseMessageShown) {
                intervals.clear()
                tvResult.text = "—"
                tvInfo.text = "En pause"
                pauseMessageShown = true
            }
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
            totalTaps == 1 -> "Encore 1 appui pour calculer"
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
        pauseMessageShown = false
        tvChronometer.text = formatElapsed(0L)
        tvResult.text = "—"
        tvInfo.text = getInfoText()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
