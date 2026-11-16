package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.LessonEntity
import com.example.ui.screens.CurriculumScreen
import com.example.ui.screens.GrammarMatrixScreen
import com.example.ui.screens.ImportExportScreen
import com.example.ui.screens.LessonDetailScreen
import com.example.ui.screens.SentencePracticeScreen
import com.example.ui.screens.SpacedRepetitionScreen
import com.example.ui.screens.VocabDrillScreen
import com.example.ui.theme.LanguageLearnerTheme
import com.example.ui.viewmodel.LanguageLearnerViewModel

sealed interface Screen {
    data object Curriculum : Screen
    data class LessonDetail(val lesson: LessonEntity) : Screen
    data class VocabDrill(val lesson: LessonEntity) : Screen
    data class GrammarMatrix(val lesson: LessonEntity) : Screen
    data class SentencePractice(val lesson: LessonEntity) : Screen
    data object SpacedRepetition : Screen
    data object ImportExport : Screen
}

class MainActivity : ComponentActivity() {
    private val viewModel: LanguageLearnerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDark by viewModel.isDarkTheme.collectAsStateWithLifecycle()

            LanguageLearnerTheme(darkTheme = isDark) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: LanguageLearnerViewModel) {
    val backStack = remember { mutableStateListOf<Screen>(Screen.Curriculum) }
    val currentScreen = backStack.lastOrNull() ?: Screen.Curriculum

    fun navigateTo(screen: Screen) {
        backStack.add(screen)
    }

    fun navigateBack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.size - 1)
        }
    }

    when (val screen = currentScreen) {
        is Screen.Curriculum -> {
            CurriculumScreen(
                viewModel = viewModel,
                onOpenLesson = { lesson ->
                    navigateTo(Screen.LessonDetail(lesson))
                },
                onOpenSrsReview = {
                    navigateTo(Screen.SpacedRepetition)
                },
                onOpenImportExport = {
                    navigateTo(Screen.ImportExport)
                }
            )
        }

        is Screen.LessonDetail -> {
            BackHandler { navigateBack() }
            LessonDetailScreen(
                lesson = screen.lesson,
                onBack = { navigateBack() },
                onOpenSentencePractice = {
                    viewModel.clearWeakCellFilter()
                    navigateTo(Screen.SentencePractice(screen.lesson))
                },
                onDrillWeakCells = { weakCoords ->
                    viewModel.setWeakCellFilter(weakCoords)
                    navigateTo(Screen.SentencePractice(screen.lesson))
                },
                onOpenVocabDrill = {
                    navigateTo(Screen.VocabDrill(screen.lesson))
                },
                onOpenGrammarMatrix = {
                    navigateTo(Screen.GrammarMatrix(screen.lesson))
                }
            )
        }

        is Screen.VocabDrill -> {
            BackHandler { navigateBack() }
            VocabDrillScreen(
                lesson = screen.lesson,
                viewModel = viewModel,
                onBack = { navigateBack() }
            )
        }

        is Screen.GrammarMatrix -> {
            BackHandler { navigateBack() }
            GrammarMatrixScreen(
                lesson = screen.lesson,
                viewModel = viewModel,
                onBack = { navigateBack() }
            )
        }

        is Screen.SentencePractice -> {
            BackHandler {
                viewModel.clearWeakCellFilter()
                navigateBack()
            }
            SentencePracticeScreen(
                lesson = screen.lesson,
                viewModel = viewModel,
                onBack = {
                    viewModel.clearWeakCellFilter()
                    navigateBack()
                }
            )
        }

        is Screen.SpacedRepetition -> {
            BackHandler { navigateBack() }
            SpacedRepetitionScreen(
                viewModel = viewModel,
                onBack = { navigateBack() }
            )
        }

        is Screen.ImportExport -> {
            BackHandler { navigateBack() }
            ImportExportScreen(
                viewModel = viewModel,
                onBack = { navigateBack() }
            )
        }
    }
}
