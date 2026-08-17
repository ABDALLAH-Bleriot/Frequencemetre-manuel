package com.example.comptegouttes

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class GeneratorActivity : AppCompatActivity() {

    private lateinit var swipe: GestureDetector

    private val handler = Handler(Looper.getMainLooper())
    private var frequency = 0
    private var running = false
    private var eventCount = 0
    private var chronoStart = 0L
    private var updatingText = false
    private var animator: ValueAnimator? = null

    private lateinit var etFreq: EditText
    private lateinit var tvChrono: TextView
    private lateinit var tvEvents: TextView
    private lateinit var track: FrameLayout
    private lateinit var thumb: View
    private lateinit var btnPlay: ImageButton
    private lateinit var btnStop: ImageButton

    private val chronoTicker = object : Runnable {
        override fun run() {
            tvChrono.text = formatElapsed(SystemClock.elapsedRealtime() - chronoStart)
            handler.postDelayed(this, 100L)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generator)
        setupBottomNav("generator")
        swipe = SwipeNav.create(this, "generator")
        SoundPlayer.init(this)

        etFreq = findViewById(R.id.etFreq)
        tvChrono = findViewById(R.id.tvChrono)
        tvEvents = findViewById(R.id.tvEvents)
        track = findViewById(R.id.track)
        thumb = findViewById(R.id.thumb)
        btnPlay = findViewById(R.id.btnPlay)
        btnStop = findViewById(R.id.btnStop)

        btnPlay.setBackgroundResource(R.drawable.bg_neon_button_selector)
        btnStop.setBackgroundResource(R.drawable.bg_neon_button_selector)

        etFreq.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (updatingText) return
                val v = s.toString().toIntOrNull()
                if (v != null && v > 0) setFrequency(v, "text")
            }
        })

        btnPlay.setOnClickListener { start() }
        btnStop.setOnClickListener { stop() }

        tvChrono.text = formatElapsed(0)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        swipe.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    /** Clignotement lumineux du bouton à chaque appui. */
    private fun flashButton(b: ImageButton) {
        b.setBackgroundResource(R.drawable.bg_neon_button_lit)
        handler.postDelayed({ b.setBackgroundResource(R.drawable.bg_neon_button_selector) }, 200)
    }

    private fun periodMs(): Long = 60000L / frequency.coerceAtLeast(1)

    private fun setFrequency(value: Int, source: String) {
        frequency = value.coerceIn(1, 300)
        if (source == "text" && value != frequency) {
            updatingText = true
            etFreq.setText(frequency.toString())
            updatingText = false
        }
        if (running) startBar()
    }

    private fun startBar() {
        animator?.cancel()
        val a = ValueAnimator.ofFloat(0f, 1f)
        a.duration = periodMs()
        a.repeatCount = ValueAnimator.INFINITE
        a.addUpdateListener { anim ->
            val frac = anim.animatedValue as Float
            val travel = (track.width - thumb.width).coerceAtLeast(1)
            thumb.translationX = frac * travel
        }
        a.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationRepeat(animation: Animator) {
                firePulse()
            }
        })
        a.start()
        animator = a
    }

    private fun start() {
        if (running) return
        if (frequency < 1) {
            Toast.makeText(this, "Réglez d'abord la fréquence (événements/min)", Toast.LENGTH_SHORT).show()
            return
        }
        flashButton(btnPlay)
        running = true
        eventCount = 0
        tvEvents.text = "0 événements"
        chronoStart = SystemClock.elapsedRealtime()
        tvChrono.text = formatElapsed(0)
        startBar()
        handler.removeCallbacks(chronoTicker)
        handler.post(chronoTicker)
    }

    private fun stop() {
        if (!running) return
        flashButton(btnStop)
        running = false
        animator?.cancel()
        animator = null
        handler.removeCallbacks(chronoTicker)
        track.setBackgroundResource(R.drawable.bg_band_off)
        thumb.translationX = 0f
    }

    private fun firePulse() {
        eventCount++
        tvEvents.text = "$eventCount événements"
        track.setBackgroundResource(R.drawable.bg_band_on)
        handler.postDelayed({ track.setBackgroundResource(R.drawable.bg_band_off) }, minOf(200L, periodMs() / 2))
        SoundPlayer.play()
    }

    private fun formatElapsed(ms: Long): String {
        val totalSeconds = ms / 1000L
        val minutes = totalSeconds / 60L
        val seconds = totalSeconds % 60L
        val tenths = (ms % 1000L) / 100L
        return String.format(Locale.US, "%02d:%02d.%d", minutes, seconds, tenths)
    }

    override fun onPause() {
        super.onPause()
        stop()
    }

    override fun onDestroy() {
        animator?.cancel()
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
