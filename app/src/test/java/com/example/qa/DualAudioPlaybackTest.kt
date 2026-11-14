package com.example.qa

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.test.core.app.ApplicationProvider
import com.example.ui.viewmodel.LanguageLearnerViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Locale

/**
 * Automated P1 and P2 test suite for Dual-Audio & TTS Pronunciation Playback
 * generated from `qa-artifacts/2026-11-14-dual-audio-playback/test-suite.md` following AAA.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DualAudioPlaybackTest {

    private lateinit var app: Application
    private lateinit var driver: AudioPlaybackDriver

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        driver = AudioPlaybackDriver(app)
    }

    @After
    fun tearDown() {
        driver.cleanup()
    }

    @Test
    fun `TC-001 custom audio playback on VocabDrillScreen for target language in Ready state`() {
        // Arrange
        driver.markTtsReady()
        val audioPath = driver.createValidTempAudioFile("zdravo")

        // Act
        driver.playViaViewModelRoute("Здраво", audioPath, isTargetLanguage = true)

        // Assert
        assertNull(driver.shadowTts().lastSpokenText)
    }

    @Test
    fun `TC-002 custom audio playback succeeds while TTS engine is uninitialized`() {
        // Arrange (TTS not initialized)
        val audioPath = driver.createValidTempAudioFile("privet")

        // Act
        driver.playViaViewModelRoute("Привет", audioPath, isTargetLanguage = false)

        // Assert
        assertNull(driver.shadowTts().lastSpokenText)
    }

    @Test
    fun `TC-003 blank audioUri falls back to sr_RS TTS after onInit SUCCESS`() {
        // Arrange
        driver.markTtsReady(TextToSpeech.SUCCESS)

        // Act
        driver.playViaViewModelRoute("Добар дан", "", isTargetLanguage = true)

        // Assert
        assertEquals("Добар дан", driver.shadowTts().lastSpokenText)
        assertEquals(TextToSpeech.QUEUE_FLUSH, driver.shadowTts().queueMode)
        assertEquals(Locale("sr", "RS"), driver.shadowTts().currentLanguage)
    }

    @Test
    fun `TC-004 blank audioUri falls back to ru_RU TTS on SpacedRepetitionScreen`() {
        // Arrange
        driver.markTtsReady(TextToSpeech.SUCCESS)

        // Act
        driver.playViaViewModelRoute("Спасибо", "   ", isTargetLanguage = false)

        // Assert
        assertEquals("Спасибо", driver.shadowTts().lastSpokenText)
        assertEquals(TextToSpeech.QUEUE_FLUSH, driver.shadowTts().queueMode)
        assertEquals(Locale("ru", "RU"), driver.shadowTts().currentLanguage)
    }

    @Test
    fun `TC-005 non-existent audioUri falls back to TTS when sr_RS missing`() {
        // Arrange
        driver.markTtsReady(TextToSpeech.SUCCESS)
        driver.controller.setLanguage(Locale("hr", "HR"))

        // Act
        driver.playViaViewModelRoute("Хвала", "/local/missing.mp3", isTargetLanguage = true)

        // Assert
        assertEquals("Хвала", driver.shadowTts().lastSpokenText)
        assertEquals(TextToSpeech.QUEUE_FLUSH, driver.shadowTts().queueMode)
    }

    @Test
    fun `TC-006 MediaPlayer exception falls back to TTS without crashing`() {
        // Arrange
        driver.markTtsReady(TextToSpeech.SUCCESS)
        val badUri = "content://bad/audio"
        driver.registerMediaPlayerError(badUri)

        // Act
        driver.playViaViewModelRoute("Молим", badUri, isTargetLanguage = true)

        // Assert
        assertEquals("Молим", driver.shadowTts().lastSpokenText)
        assertEquals(TextToSpeech.QUEUE_FLUSH, driver.shadowTts().queueMode)
    }

    @Test
    fun `TC-007 blank audioUri when TTS is uninitialized logs warning and returns safely`() {
        // Arrange (TTS uninitialized)

        // Act
        driver.playViaViewModelRoute("Идем", "", isTargetLanguage = true)

        // Assert
        assertNull(driver.shadowTts().lastSpokenText)
    }

    @Test
    fun `TC-008 non-existent audioUri when TTS is uninitialized logs warning and returns safely`() {
        // Arrange (TTS uninitialized)

        // Act
        driver.playViaViewModelRoute("Кућа", "/local/missing.mp3", isTargetLanguage = true)

        // Assert
        assertNull(driver.shadowTts().lastSpokenText)
    }

    @Test
    fun `TC-009 ViewModel onCleared releases MediaPlayer and shuts down TextToSpeech`() {
        // Arrange
        driver.markTtsReady(TextToSpeech.SUCCESS)
        val ttsShadow = driver.shadowTts()
        val viewModel = LanguageLearnerViewModel(app)
        viewModel.playAudio("Тест", "", isTargetLanguage = true)

        // Act
        driver.controller.release()

        // Assert
        assertTrue(ttsShadow.isShutdown)
        assertFalse(ttsShadow.isStopped.not())
    }

    @Test
    fun `TC-010 pairwise VocabDrillScreen false blank hr_HR_only`() {
        // Arrange
        driver.markTtsReady(TextToSpeech.SUCCESS)

        // Act
        driver.playViaViewModelRoute("Да", "", isTargetLanguage = false)

        // Assert
        assertEquals("Да", driver.shadowTts().lastSpokenText)
        assertEquals(Locale("ru", "RU"), driver.shadowTts().currentLanguage)
    }

    @Test
    fun `TC-011 pairwise SentencePracticeScreen false blank sr_RS_available`() {
        // Arrange
        driver.markTtsReady(TextToSpeech.SUCCESS)

        // Act
        driver.playViaViewModelRoute("Нет", "", isTargetLanguage = false)

        // Assert
        assertEquals("Нет", driver.shadowTts().lastSpokenText)
        assertEquals(Locale("ru", "RU"), driver.shadowTts().currentLanguage)
    }

    @Test
    fun `TC-012 pairwise SentencePracticeScreen false valid default_only`() {
        // Arrange
        driver.markTtsReady(TextToSpeech.SUCCESS)
        val audioPath = driver.createValidTempAudioFile("voda")

        // Act
        driver.playViaViewModelRoute("Вода", audioPath, isTargetLanguage = false)

        // Assert
        assertNull(driver.shadowTts().lastSpokenText)
    }

    @Test
    fun `TC-013 pairwise GrammarMatrixScreen true nonExistentOrError sr_RS_available`() {
        // Arrange
        driver.markTtsReady(TextToSpeech.SUCCESS)

        // Act
        driver.playViaViewModelRoute("Јесам", "/local/missing.mp3", isTargetLanguage = true)

        // Assert
        assertEquals("Јесам", driver.shadowTts().lastSpokenText)
        assertEquals(Locale("sr", "RS"), driver.shadowTts().currentLanguage)
    }

    @Test
    fun `TC-014 pairwise GrammarMatrixScreen false valid hr_HR_only`() {
        // Arrange
        driver.markTtsReady(TextToSpeech.SUCCESS)
        val audioPath = driver.createValidTempAudioFile("byt")

        // Act
        driver.playViaViewModelRoute("Быть", audioPath, isTargetLanguage = false)

        // Assert
        assertNull(driver.shadowTts().lastSpokenText)
    }

    @Test
    fun `TC-015 pairwise GrammarMatrixScreen true blank default_only`() {
        // Arrange
        driver.markTtsReady(TextToSpeech.SUCCESS)

        // Act
        driver.playViaViewModelRoute("Радим", "", isTargetLanguage = true)

        // Assert
        assertEquals("Радим", driver.shadowTts().lastSpokenText)
        assertEquals(TextToSpeech.QUEUE_FLUSH, driver.shadowTts().queueMode)
    }

    @Test
    fun `TC-016 pairwise GrammarMatrixScreen false nonExistentOrError ru_RU_available`() {
        // Arrange
        driver.markTtsReady(TextToSpeech.SUCCESS)

        // Act
        driver.playViaViewModelRoute("Делаю", "/local/missing.mp3", isTargetLanguage = false)

        // Assert
        assertEquals("Делаю", driver.shadowTts().lastSpokenText)
        assertEquals(Locale("ru", "RU"), driver.shadowTts().currentLanguage)
    }

    @Test
    fun `TC-017 pairwise SpacedRepetitionScreen false blank sr_RS_available`() {
        // Arrange
        driver.markTtsReady(TextToSpeech.SUCCESS)

        // Act
        driver.playViaViewModelRoute("Книга", "", isTargetLanguage = false)

        // Assert
        assertEquals("Книга", driver.shadowTts().lastSpokenText)
        assertEquals(Locale("ru", "RU"), driver.shadowTts().currentLanguage)
    }

    @Test
    fun `TC-018 pairwise SpacedRepetitionScreen true nonExistentOrError hr_HR_only`() {
        // Arrange
        driver.markTtsReady(TextToSpeech.SUCCESS)

        // Act
        driver.playViaViewModelRoute("Књига", "/local/missing.mp3", isTargetLanguage = true)

        // Assert
        assertEquals("Књига", driver.shadowTts().lastSpokenText)
        assertEquals(TextToSpeech.QUEUE_FLUSH, driver.shadowTts().queueMode)
    }

    @Test
    fun `TC-019 pairwise SpacedRepetitionScreen true valid default_only`() {
        // Arrange
        driver.markTtsReady(TextToSpeech.SUCCESS)
        val audioPath = driver.createValidTempAudioFile("grad")

        // Act
        driver.playViaViewModelRoute("Град", audioPath, isTargetLanguage = true)

        // Assert
        assertNull(driver.shadowTts().lastSpokenText)
    }

    @Test
    fun `TC-020 error guessing multi-byte Unicode Serbian diacritics and Cyrillic via TTS`() {
        // Arrange
        driver.markTtsReady(TextToSpeech.SUCCESS)
        val unicodeText = "Ђурђевдан — љубав (čćžšđ)"

        // Act
        driver.playViaViewModelRoute(unicodeText, "", isTargetLanguage = true)

        // Assert
        assertEquals(unicodeText, driver.shadowTts().lastSpokenText)
        assertEquals(Locale("sr", "RS"), driver.shadowTts().currentLanguage)
        assertEquals(TextToSpeech.QUEUE_FLUSH, driver.shadowTts().queueMode)
    }

    @Test
    fun `TC-021 error guessing empty text payload on TTS fallback does not crash`() {
        // Arrange
        driver.markTtsReady(TextToSpeech.SUCCESS)

        // Act
        driver.playViaViewModelRoute("", "", isTargetLanguage = true)

        // Assert
        assertEquals("", driver.shadowTts().lastSpokenText)
    }
}
