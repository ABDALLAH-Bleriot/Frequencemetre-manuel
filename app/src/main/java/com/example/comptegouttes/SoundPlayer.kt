package com.example.comptegouttes

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.media.ToneGenerator

object SoundPlayer {

    private var pool: SoundPool? = null
    private var soundId: Int = 0
    private var loaded = false
    private var tone: ToneGenerator? = null
    private var audio: AudioManager? = null

    fun init(context: Context) {
        if (pool != null || tone != null) return
        audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Bip de secours (pendant le chargement ou si tic.mp3 absent)
        try { tone = ToneGenerator(AudioManager.STREAM_MUSIC, 100) } catch (_: Exception) {}

        val resId = context.resources.getIdentifier("tic", "raw", context.packageName)
        if (resId != 0) {
            val attrs = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            val p = SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(attrs)
                .build()
            p.setOnLoadCompleteListener { _, _, status -> if (status == 0) loaded = true }
            soundId = p.load(context, resId, 1)
            pool = p
        }

        ensureVolume()
    }

    /** Monte le volume média à 80 % du maximum pour garantir l'audibilité. */
    private fun ensureVolume() {
        try {
            val a = audio ?: return
            val max = a.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val target = (max * 0.8f).toInt()
            if (a.getStreamVolume(AudioManager.STREAM_MUSIC) < target) {
                a.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0)
            }
        } catch (_: Exception) {}
    }

    /** Lecture instantanée, même si les appuis sont très rapprochés. */
    fun play() {
        try {
            val p = pool
            if (p != null && loaded) {
                p.play(soundId, 1f, 1f, 1, 0, 1f)
            } else {
                tone?.startTone(ToneGenerator.TONE_CDMA_PIP, 60)
            }
        } catch (_: Exception) {}
    }

    fun release() {
        try { pool?.release() } catch (_: Exception) {}
        pool = null
        loaded = false
        try { tone?.release() } catch (_: Exception) {}
        tone = null
    }
}
