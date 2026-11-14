package com.example.qa

import android.app.Application
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import com.example.audio.AudioController
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowMediaPlayer
import org.robolectric.shadows.ShadowTextToSpeech
import org.robolectric.shadows.util.DataSource
import java.io.File
import java.util.Locale

/**
 * Test driver / harness encapsulating [AudioController], Robolectric [ShadowTextToSpeech],
 * and [ShadowMediaPlayer] setup for Dual-Audio & TTS Playback tests.
 */
class AudioPlaybackDriver(private val app: Application) {

    val controller = AudioController(app)
    private val tempFiles = mutableListOf<File>()

    fun shadowTts(): ShadowTextToSpeech =
        Shadows.shadowOf(ShadowTextToSpeech.getLastTextToSpeechInstance())

    fun markTtsReady(status: Int = TextToSpeech.SUCCESS) {
        controller.onInit(status)
    }

    fun createValidTempAudioFile(name: String = "sample"): String {
        val file = File.createTempFile(name, ".mp3", app.cacheDir)
        file.writeBytes(byteArrayOf(0x01, 0x02, 0x03))
        tempFiles.add(file)
        ShadowMediaPlayer.addMediaInfo(
            DataSource.toDataSource(file.absolutePath),
            ShadowMediaPlayer.MediaInfo(1_000, 0)
        )
        return file.absolutePath
    }

    fun registerMediaPlayerError(uri: String) {
        ShadowMediaPlayer.addException(
            DataSource.toDataSource(app, android.net.Uri.parse(uri)),
            java.io.IOException("Simulated MediaPlayer failure")
        )
    }

    fun playViaViewModelRoute(text: String, audioUri: String, isTargetLanguage: Boolean) {
        val locale = if (isTargetLanguage) Locale("sr", "RS") else Locale("ru", "RU")
        controller.speak(text, audioUri, locale)
    }

    fun cleanup() {
        controller.release()
        tempFiles.forEach { it.delete() }
        tempFiles.clear()
    }
}
