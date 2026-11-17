package com.example.ui.screens

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.LessonEntity
import com.example.data.model.SentenceEntity
import com.example.ui.theme.ErrorCoral
import com.example.ui.theme.SuccessEmerald
import com.example.ui.viewmodel.LanguageLearnerViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SentencePracticeScreen(
    lesson: LessonEntity,
    viewModel: LanguageLearnerViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Vibrator::class.java) }
    val rawSentences by viewModel.activeSentences.collectAsStateWithLifecycle()
    val weakCoords by viewModel.weakFilterCoords.collectAsStateWithLifecycle()
    val activeLessonState by viewModel.activeLesson.collectAsStateWithLifecycle()

    val currentLessonData = activeLessonState ?: lesson

    val scope = rememberCoroutineScope()
    val moshi = remember { Moshi.Builder().build() }
    val strListType = remember { Types.newParameterizedType(List::class.java, String::class.java) }
    val strListAdapter = remember { moshi.adapter<List<String>>(strListType) }

    val sentenceQueue = remember { mutableStateListOf<SentenceEntity>() }
    var currentIndex by remember { mutableIntStateOf(0) }
    var correctCount by remember { mutableIntStateOf(0) }
    var wrongCount by remember { mutableIntStateOf(0) }
    var showHelpDialog by remember { mutableStateOf(false) }

    // Selected tokens in assembly bay (strictly lowercase)
    val placedTokens = remember { mutableStateListOf<String>() }
    // Available tokens pool (strictly lowercase tokens + distractors)
    val poolTokens = remember { mutableStateListOf<String>() }

    var validationResult by remember { mutableStateOf<Boolean?>(null) }
    var feedbackMessage by remember { mutableStateOf("") }
    var isFinished by remember { mutableStateOf(false) }
    var attemptStartMs by remember { mutableStateOf(System.currentTimeMillis()) }
    var tileReturns by remember { mutableIntStateOf(0) }
    var grammarOpenedMidAttempt by remember { mutableStateOf(false) }
    val attemptedIdsInSession = remember { mutableSetOf<String>() }

    // Helper to clean tokens: lowercase and remove all Unicode punctuation
    fun cleanToken(raw: String): String =
        raw.trim().lowercase().replace(Regex("[\\p{P}\\p{S}]"), "")

    fun setupCurrentSentence(sentence: SentenceEntity) {
        placedTokens.clear()
        poolTokens.clear()
        validationResult = null
        feedbackMessage = ""
        attemptStartMs = System.currentTimeMillis()
        tileReturns = 0
        grammarOpenedMidAttempt = false

        val correctTokensRaw = try {
            strListAdapter.fromJson(sentence.tokensJson) ?: emptyList()
        } catch (_: Exception) {
            sentence.targetSentence.split(" ")
        }

        val distractorsRaw = try {
            strListAdapter.fromJson(sentence.distractorTokensJson) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }

        // Convert all tokens to inner-case (lowercase) without punctuation
        val correctTokens = correctTokensRaw.map { cleanToken(it) }.filter { it.isNotBlank() }
        val distractors = distractorsRaw.map { cleanToken(it) }.filter { it.isNotBlank() }

        poolTokens.addAll((correctTokens + distractors).shuffled())
    }

    LaunchedEffect(rawSentences, weakCoords) {
        if (rawSentences.isNotEmpty() && sentenceQueue.isEmpty()) {
            val filtered = if (weakCoords.isNotEmpty()) {
                rawSentences.filter { it.matrixCoordinateKey() in weakCoords }.take(8)
                    .ifEmpty { rawSentences }
            } else {
                rawSentences
            }
            sentenceQueue.clear()
            sentenceQueue.addAll(filtered.shuffled())
            currentIndex = 0
            correctCount = 0
            wrongCount = 0
            isFinished = false
            setupCurrentSentence(sentenceQueue[0])
        }
    }

    val currentSentence = sentenceQueue.getOrNull(currentIndex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Bar: Back | Lesson X | Dynamic Grade (from 100 answers) | Correct | Wrong | Help
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("back_from_sentence_practice")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Lesson ${currentLessonData.orderIndex}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Dynamic grade badge calculated from (correct / 100) * 5.0
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    val gradeDisplay = String.format(Locale.US, "%.1f", currentLessonData.masteryScore)
                    Text(
                        text = gradeDisplay,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Correct check badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = SuccessEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "$correctCount",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Wrong cross badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = ErrorCoral,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "$wrongCount",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Help question icon
                IconButton(
                    onClick = {
                        grammarOpenedMidAttempt = true
                        showHelpDialog = true
                    },
                    modifier = Modifier.testTag("sentence_hint_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = "Подсказка по грамматике",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (isFinished || currentSentence == null) {
            // Summary Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Упражнение завершено!",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Текущая оценка урока: ${String.format(Locale.US, "%.1f", currentLessonData.masteryScore)} / 5.0",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Верных ответов в окне 100: ${currentLessonData.correctAnswersInLast100}/100",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )

                if (currentLessonData.masteryScore >= 4.5f) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Поздравляем! Следующий урок разблокирован!",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = SuccessEmerald
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(48.dp)
                        .testTag("finish_practice_button")
                ) {
                    Text("Вернуться к уроку", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // Active Sentence Construction
            Spacer(modifier = Modifier.height(8.dp))

            // Source sentence prompt
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = currentSentence.sourceSentence,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.testTag("source_sentence_text")
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Преведите на српски",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    IconButton(
                        onClick = {
                            val text = currentSentence.targetSentenceLatin.ifBlank { currentSentence.targetSentence }
                            viewModel.playAudio(text, currentSentence.audioUri, isTargetLanguage = true)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Прослушать целевую фразу",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Assembly Bay: where user taps tiles into
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .border(
                        1.5.dp,
                        when (validationResult) {
                            true -> SuccessEmerald
                            false -> ErrorCoral
                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        },
                        RoundedCornerShape(14.dp)
                    )
                    .padding(14.dp)
                    .testTag("assembly_bay")
            ) {
                if (placedTokens.isEmpty()) {
                    Text(
                        text = "Нажимайте на слова ниже для составления фразы...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val expectedForHighlight = remember(currentSentence) {
                            val t = currentSentence.targetSentenceLatin.ifBlank { currentSentence.targetSentence }
                            t.split(" ").map { cleanToken(it) }.filter { it.isNotBlank() }
                        }
                        placedTokens.forEachIndexed { index, token ->
                            val isMisplaced = validationResult == false && token != expectedForHighlight.getOrNull(index)
                            WordChip(
                                text = token, // strictly lowercase
                                isPlaced = true,
                                isMisplaced = isMisplaced,
                                onClick = {
                                    placedTokens.removeAt(index)
                                    poolTokens.add(token)
                                    tileReturns++
                                    validationResult = null
                                    feedbackMessage = ""
                                },
                                testTag = "placed_chip_$token"
                            )
                        }
                    }
                }
            }

            // Feedback Message on check
            AnimatedVisibility(visible = feedbackMessage.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = feedbackMessage,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (validationResult == true) SuccessEmerald else ErrorCoral,
                        modifier = Modifier.weight(1f)
                    )
                    if (validationResult == false) {
                        TextButton(
                            onClick = { showHelpDialog = true },
                            modifier = Modifier.testTag("error_grammar_link")
                        ) {
                            Text("Правило")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Tile Pool (Word Bank) - all tokens strictly lower-case (inner case)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tile_pool")
                ) {
                    poolTokens.forEachIndexed { index, token ->
                        WordChip(
                            text = token, // strictly lowercase
                            isPlaced = false,
                            onClick = {
                                poolTokens.removeAt(index)
                                placedTokens.add(token)
                                validationResult = null
                                feedbackMessage = ""
                            },
                            testTag = "pool_chip_$token"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Check Answer Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = {
                        if (validationResult != null) return@Button
                        val currentTarget = currentSentence.targetSentenceLatin.ifBlank { currentSentence.targetSentence }
                        val expectedTokens = currentTarget.split(" ").map { cleanToken(it) }.filter { it.isNotBlank() }
                        val userTokens = placedTokens.map { cleanToken(it) }.filter { it.isNotBlank() }

                        val isCorrect = userTokens == expectedTokens
                        val latencyMs = (System.currentTimeMillis() - attemptStartMs).coerceAtLeast(0L)
                        val isFirstAttempt = attemptedIdsInSession.add(currentSentence.id)
                        viewModel.recordImplicitSentenceAttempt(
                            sentence = currentSentence,
                            isCorrect = isCorrect,
                            latencyMs = latencyMs,
                            tileReturns = tileReturns,
                            grammarOpenedMidAttempt = grammarOpenedMidAttempt,
                            isFirstAttemptInSession = isFirstAttempt
                        )

                        if (isCorrect) {
                            validationResult = true
                            correctCount++
                            feedbackMessage = "Правильно!"

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                            }
                            viewModel.playAudio(currentTarget, currentSentence.audioUri, isTargetLanguage = true)

                            scope.launch {
                                delay(1200)
                                if (currentIndex + 1 < sentenceQueue.size) {
                                    currentIndex++
                                    setupCurrentSentence(sentenceQueue[currentIndex])
                                } else {
                                    isFinished = true
                                }
                            }
                        } else {
                            validationResult = false
                            wrongCount++
                            feedbackMessage = "Правильный ответ: $currentTarget"

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator?.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
                            }
                            // Re-insert into queue for review
                            sentenceQueue.add(currentSentence)
                        }
                    },
                    enabled = placedTokens.isNotEmpty() && validationResult == null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("check_answer_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (validationResult == true) SuccessEmerald else MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (validationResult == true) "Верно!" else "Проверить ответ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            // Segmented bottom progress bar
            SegmentedProgressBar(
                total = sentenceQueue.size,
                current = currentIndex,
                correctCount = correctCount,
                wrongCount = wrongCount
            )
        }
    }

    // Help Dialog
    if (showHelpDialog && currentSentence != null) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text("Подсказка по грамматике") },
            text = {
                Text(
                    text = currentSentence.grammarHint.ifBlank {
                        "Обратите внимание на порядок слов и позицию кратких форм вспомогательных глаголов."
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Понятно")
                }
            }
        )
    }
}

@Composable
private fun WordChip(
    text: String,
    isPlaced: Boolean,
    onClick: () -> Unit,
    testTag: String,
    isMisplaced: Boolean = false
) {
    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .then(
                if (isMisplaced) Modifier.border(2.dp, ErrorCoral, RoundedCornerShape(10.dp))
                else Modifier
            )
            .clickable(onClick = onClick)
            .testTag(testTag),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isMisplaced -> ErrorCoral.copy(alpha = 0.2f)
                isPlaced -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = if (isPlaced) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SegmentedProgressBar(
    total: Int,
    current: Int,
    correctCount: Int,
    wrongCount: Int
) {
    val barCount = 36
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(14.dp)
            .padding(horizontal = 12.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        val completedRatio = if (total > 0) current.toFloat() / total.toFloat() else 0f
        val filledBars = (completedRatio * barCount).toInt()

        for (i in 0 until barCount) {
            val color = when {
                i < filledBars && wrongCount > 0 && i % 5 == 0 -> Color(0xFFEF4444)
                i < filledBars -> Color(0xFF0284C7)
                else -> Color(0xFF1E293B)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(color)
            )
        }
    }
}
