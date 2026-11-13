package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.util.Log
import java.io.File
import java.util.Locale

class AudioController(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var mediaPlayer: MediaPlayer? = null
    private var currentLanguageLocale: Locale = Locale("sr", "RS")

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            setLanguage(currentLanguageLocale)
        } else {
            Log.w("AudioController", "TTS initialization failed: $status")
        }
    }

    fun setLanguage(locale: Locale) {
        currentLanguageLocale = locale
        tts?.let {
            val result = it.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to Slavic or default if specific Serbian voice is not installed
                Log.w("AudioController", "Language $locale not supported, attempting fallback")
                val fallback = Locale("hr", "HR")
                if (it.setLanguage(fallback) == TextToSpeech.LANG_NOT_SUPPORTED) {
                    it.setLanguage(Locale.getDefault())
                }
            }
        }
    }

    /**
     * Plays audio for the given word or sentence.
     * If an audioUri is provided and valid, plays it via MediaPlayer.
     * Otherwise, speaks the text using TTS.
     */
    fun speak(text: String, audioUri: String? = null, languageLocale: Locale? = null) {
        if (!audioUri.isNullOrBlank()) {
            val played = playCustomAudio(audioUri)
            if (played) return
        }

        // Fallback to TTS
        if (!isTtsReady || tts == null) {
            Log.w("AudioController", "TTS not ready yet")
            return
        }

        val targetLocale = languageLocale ?: currentLanguageLocale
        tts?.setLanguage(targetLocale)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "Utterance_${System.currentTimeMillis()}")
    }

    private fun playCustomAudio(uriString: String): Boolean {
        return try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                val uri = Uri.parse(uriString)
                if (uri.scheme == null || uri.scheme == "file") {
                    val file = File(uri.path ?: uriString)
                    if (file.exists()) {
                        setDataSource(file.absolutePath)
                    } else {
                        return false
                    }
                } else {
                    setDataSource(context, uri)
                }
                prepare()
                start()
            }
            true
        } catch (e: Exception) {
            Log.w("AudioController", "Failed playing custom audio: $uriString", e)
            false
        }
    }

    fun stop() {
        tts?.stop()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (_: Exception) {}
    }

    fun release() {
        stop()
        tts?.shutdown()
        tts = null
        isTtsReady = false
    }
}
