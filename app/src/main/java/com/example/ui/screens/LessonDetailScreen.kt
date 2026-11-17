package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LessonEntity
import com.example.data.model.MatrixCellStatus
import com.example.data.repository.CurriculumRepository
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.ErrorCoral
import com.example.ui.theme.NeutralSlate
import com.example.ui.theme.SuccessEmerald
import java.util.Locale

@Composable
fun LessonDetailScreen(
    lesson: LessonEntity,
    onBack: () -> Unit,
    onOpenSentencePractice: () -> Unit,
    onOpenVocabDrill: () -> Unit,
    onOpenGrammarMatrix: () -> Unit,
    onDrillWeakCells: (Set<String>) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // Top navigation bar matching screenshot
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("back_from_lesson_detail")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад к списку уроков",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Lesson ${lesson.orderIndex}",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Large centered header
        Text(
            text = lesson.title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Module row 1: "Продолжить урок" / Sentence Construction
        DetailActionRow(
            badgeContent = {
                if (lesson.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    val score = String.format(Locale.US, "%.1f", lesson.masteryScore)
                    Text(score, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            },
            badgeColor = if (lesson.isCompleted) SuccessEmerald else Color(0xFF0284C7),
            title = "Продолжить урок",
            subtitle = "Тренировка предложений (${lesson.correctAnswersInLast100}/100 верно • Цель: 4.5)",
            onClick = onOpenSentencePractice,
            testTag = "action_continue_lesson"
        )

        HorizontalDivider(
            modifier = Modifier.padding(start = 72.dp, end = 16.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )

        // Module row 2: "Новые слова" / Vocab matching drills
        DetailActionRow(
            badgeContent = {
                Text(
                    text = "${lesson.correctAnswersInLast100}%",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            },
            badgeColor = Color(0xFFD97706),
            title = "Новые слова",
            subtitle = "Учить новые слова (лексический тренажер)",
            onClick = onOpenVocabDrill,
            testTag = "action_vocab_drill"
        )

        HorizontalDivider(
            modifier = Modifier.padding(start = 72.dp, end = 16.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )

        // Module row 3: "Описание урока" / 3x3 Grammar Matrix
        DetailActionRow(
            badgeContent = {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            },
            badgeColor = MaterialTheme.colorScheme.primary,
            title = "Описание урока",
            subtitle = "Матрица 3x3 глаголов и грамматические правила",
            onClick = onOpenGrammarMatrix,
            testTag = "action_grammar_matrix"
        )

        Spacer(modifier = Modifier.height(16.dp))

        MatrixHeatmapSection(
            matrixStatsJson = lesson.matrixStatsJson,
            onDrillWeakCells = onDrillWeakCells
        )
    }
}

@Composable
private fun MatrixHeatmapSection(
    matrixStatsJson: String,
    onDrillWeakCells: (Set<String>) -> Unit
) {
    val statsMap = remember(matrixStatsJson) {
        CurriculumRepository.parseMatrixStatsJson(matrixStatsJson)
    }
    val weakCoords = statsMap.filterValues { it.cellStatus == MatrixCellStatus.WEAK }.keys
    val tenses = listOf("PAST" to "Past", "PRESENT" to "Present", "FUTURE" to "Future")
    val forms = listOf("AFFIRMATIVE" to "+", "NEGATIVE" to "-", "INTERROGATIVE" to "?")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("matrix_heatmap_section")
    ) {
        Text(
            text = "Прогресс матрицы 3x3",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        tenses.forEach { (tenseKey, tenseLabel) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                forms.forEach { (formKey, formSymbol) ->
                    val coord = "${tenseKey}_${formKey}"
                    val stat = statsMap[coord]
                    val bgColor = when (stat?.cellStatus) {
                        MatrixCellStatus.WEAK -> ErrorCoral
                        MatrixCellStatus.PROFICIENT -> AccentIndigo
                        MatrixCellStatus.MASTERED -> SuccessEmerald
                        else -> NeutralSlate
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 48.dp)
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(bgColor.copy(alpha = 0.85f))
                            .testTag("heatmap_cell_$coord"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$tenseLabel ($formSymbol)",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        if (weakCoords.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { onDrillWeakCells(weakCoords) },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp)
                    .testTag("drill_weak_cells_button"),
                colors = ButtonDefaults.buttonColors(containerColor = ErrorCoral),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Drill Weak Cells (${weakCoords.size})",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DetailActionRow(
    badgeContent: @Composable () -> Unit,
    badgeColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(badgeColor),
            contentAlignment = Alignment.Center
        ) {
            badgeContent()
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
