package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioController
import com.example.data.importer.CurriculumImporter
import com.example.data.importer.ImportResult
import com.example.data.local.AppDatabase
import com.example.data.model.GrammarGuideEntity
import com.example.data.model.LessonEntity
import com.example.data.model.SentenceEntity
import com.example.data.model.SrsReviewEntity
import com.example.data.model.VocabItemEntity
import com.example.data.repository.CurriculumRepository
import com.example.srs.ReviewGrade
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

class LanguageLearnerViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    val repository = CurriculumRepository(db)
    val audioController = AudioController(application)

    val allLessons: StateFlow<List<LessonEntity>> = repository.allLessons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dueSrsItems: StateFlow<List<SrsReviewEntity>> = repository.getDueSrsItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dueSrsCount: StateFlow<Int> = repository.getDueSrsCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _activeLesson = MutableStateFlow<LessonEntity?>(null)
    val activeLesson: StateFlow<LessonEntity?> = _activeLesson.asStateFlow()

    private val _activeVocab = MutableStateFlow<List<VocabItemEntity>>(emptyList())
    val activeVocab: StateFlow<List<VocabItemEntity>> = _activeVocab.asStateFlow()

    private val _activeGrammar = MutableStateFlow<GrammarGuideEntity?>(null)
    val activeGrammar: StateFlow<GrammarGuideEntity?> = _activeGrammar.asStateFlow()

    private val _activeSentences = MutableStateFlow<List<SentenceEntity>>(emptyList())
    val activeSentences: StateFlow<List<SentenceEntity>> = _activeSentences.asStateFlow()

    private val _isLatinScript = MutableStateFlow(false)
    val isLatinScript: StateFlow<Boolean> = _isLatinScript.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true) // Default to sleek modern dark theme
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureInitialDataLoaded()
        }
    }

    fun selectLesson(lesson: LessonEntity) {
        _activeLesson.value = lesson
        viewModelScope.launch {
            repository.getVocabForLesson(lesson.id).collect {
                _activeVocab.value = it
            }
        }
        viewModelScope.launch {
            repository.getGrammarGuideForLesson(lesson.id).collect {
                _activeGrammar.value = it
            }
        }
        viewModelScope.launch {
            repository.getSentencesForLesson(lesson.id).collect {
                _activeSentences.value = it
            }
        }
        // Keep activeLesson updated with live changes from database
        viewModelScope.launch {
            repository.allLessons.collect { lessonsList ->
                val updated = lessonsList.firstOrNull { it.id == lesson.id }
                if (updated != null) {
                    _activeLesson.value = updated
                }
            }
        }
    }

    fun toggleScript() {
        _isLatinScript.value = !_isLatinScript.value
    }

    fun toggleDarkTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun playAudio(text: String, audioUri: String? = null, isTargetLanguage: Boolean = true) {
        val locale = if (isTargetLanguage) Locale("sr", "RS") else Locale("ru", "RU")
        audioController.speak(text, audioUri, locale)
    }

    fun recordReview(item: SrsReviewEntity, grade: ReviewGrade) {
        viewModelScope.launch {
            repository.recordSrsResult(item, grade)
        }
    }

    /**
     * Records answer for lesson (from Sentence Practice or Vocab Drill).
     * Recalculates grade = (correct / 100) * 5.0 in the rolling 100 window.
     */
    fun recordAnswer(lessonId: String, isCorrect: Boolean) {
        viewModelScope.launch {
            repository.recordAnswer(lessonId, isCorrect)
        }
    }

    suspend fun importData(content: String, format: String): ImportResult {
        val result = when (format.uppercase()) {
            "JSON" -> CurriculumImporter.parseJson(content)
            "CSV" -> CurriculumImporter.parseCsv(content)
            "XML" -> CurriculumImporter.parseXml(content)
            else -> ImportResult(message = "Unsupported format: $format")
        }

        if (result.lessons.isNotEmpty() || result.vocabItems.isNotEmpty()) {
            repository.importCurriculum(result)
        }
        return result
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            repository.clearAndResetToDefaults()
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioController.release()
    }
}
