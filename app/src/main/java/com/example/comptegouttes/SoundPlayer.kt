package com.example.comptegouttes

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator

object SoundPlayer {

    private var player: MediaPlayer? = null
    private var tone: ToneGenerator? = null

    fun init(context: Context) {
        if (player == null && tone == null) {
            val resId = context.resources.getIdentifier("tic", "raw", context.packageName)
            if (resId != 0) {
                try { player = MediaPlayer.create(context.applicationContext, resId) } catch (_: Exception) {}
            }
            if (player == null) {
                try { tone = ToneGenerator(AudioManager.STREAM_MUSIC, 100) } catch (_: Exception) {}
            }
        }
    }

    fun play() {
        try {
            val p = player
            if (p != null) {
                if (p.isPlaying) p.seekTo(0)
                p.start()
            } else {
                tone?.startTone(ToneGenerator.TONE_CDMA_PIP, 60)
            }
        } catch (_: Exception) {}
    }

    fun release() {
        try { player?.release() } catch (_: Exception) {}
        player = null
        try { tone?.release() } catch (_: Exception) {}
        tone = null
    }
}
