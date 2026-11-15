package com.example.ui.screens

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.LessonEntity
import com.example.data.model.VocabItemEntity
import com.example.ui.theme.ErrorCoral
import com.example.ui.theme.SuccessEmerald
import com.example.ui.viewmodel.LanguageLearnerViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VocabDrillScreen(
    lesson: LessonEntity,
    viewModel: LanguageLearnerViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Vibrator::class.java) }
    val isLatin by viewModel.isLatinScript.collectAsStateWithLifecycle()
    val rawVocabList by viewModel.activeVocab.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val moshi = remember { Moshi.Builder().build() }
    val stringListType = remember { Types.newParameterizedType(List::class.java, String::class.java) }
    val stringListAdapter = remember { moshi.adapter<List<String>>(stringListType) }

    // Active drill queue (allows missed items to be re-appended)
    val drillQueue = remember { mutableStateListOf<VocabItemEntity>() }
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isCorrectState by remember { mutableStateOf<Boolean?>(null) }
    var isCompleted by remember { mutableStateOf(false) }
    var correctCount by remember { mutableIntStateOf(0) }
    var initialTotal by remember { mutableIntStateOf(0) }

    // Load initial queue
    LaunchedEffect(rawVocabList) {
        if (rawVocabList.isNotEmpty() && drillQueue.isEmpty()) {
            drillQueue.clear()
            drillQueue.addAll(rawVocabList.shuffled())
            initialTotal = rawVocabList.size
            currentIndex = 0
            isCompleted = false
        }
    }

    val currentItem = drillQueue.getOrNull(currentIndex)

    // Automatically pronounce target word on display
    LaunchedEffect(currentItem) {
        if (currentItem != null) {
            val wordToSpeak = if (isLatin) currentItem.targetWordLatin else currentItem.targetWord
            viewModel.playAudio(wordToSpeak, currentItem.audioUri, isTargetLanguage = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("back_from_vocab_drill")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Column {
                    Text(
                        text = "Lesson ${lesson.orderIndex}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Новые слова",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = {
                    if (currentItem != null) {
                        val word = if (isLatin) currentItem.targetWordLatin else currentItem.targetWord
                        viewModel.playAudio(word, currentItem.audioUri, isTargetLanguage = true)
                    }
                },
                modifier = Modifier.testTag("vocab_pronounce_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Прослушать произношение",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (isCompleted || currentItem == null) {
            // Completion View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SuccessEmerald,
                    modifier = Modifier.size(72.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Отличная работа!",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Все $initialTotal новых слов урока успешно усвоены и добавлены в очередь интервального повторения.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        drillQueue.clear()
                        drillQueue.addAll(rawVocabList.shuffled())
                        currentIndex = 0
                        correctCount = 0
                        isCompleted = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("restart_drill_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Повторить тренировку", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // Active Drill View
            // Build options (Target Translation + Distractors)
            val options = remember(currentItem) {
                val distractors = try {
                    stringListAdapter.fromJson(currentItem.distractorsJson) ?: emptyList()
                } catch (_: Exception) {
                    emptyList()
                }
                (listOf(currentItem.sourceTranslation) + distractors.take(5)).shuffled()
            }

            Spacer(modifier = Modifier.weight(0.4f))

            // Prominently centered target word matching screenshot 4
            val targetDisplay = if (isLatin) currentItem.targetWordLatin else currentItem.targetWord

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = targetDisplay,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("target_word_prompt")
                )

                if (currentItem.partOfSpeech.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = currentItem.partOfSpeech,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (currentItem.exampleSentence.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "„${currentItem.exampleSentence}“",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Option Grid (Matching the 2-column screenshot layout)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Group into pairs
                val chunkedOptions = options.chunked(2)
                chunkedOptions.forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        pair.forEach { optionText ->
                            val isSelected = selectedOption == optionText
                            val isTarget = optionText == currentItem.sourceTranslation

                            val targetBgColor = when {
                                isSelected && isCorrectState == true -> SuccessEmerald
                                isSelected && isCorrectState == false -> ErrorCoral
                                selectedOption != null && isTarget -> SuccessEmerald.copy(alpha = 0.8f)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }

                            val animatedBg by animateColorAsState(targetValue = targetBgColor, label = "card_color")

                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(64.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable(enabled = selectedOption == null) {
                                        selectedOption = optionText
                                        if (isTarget) {
                                            isCorrectState = true
                                            correctCount++
                                            viewModel.recordAnswer(lesson.id, isCorrect = true)
                                            // Haptic feedback
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                vibrator?.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
                                            }
                                            // Proceed to next after delay
                                            scope.launch {
                                                delay(500)
                                                selectedOption = null
                                                isCorrectState = null
                                                if (currentIndex + 1 < drillQueue.size) {
                                                    currentIndex++
                                                } else {
                                                    isCompleted = true
                                                }
                                            }
                                        } else {
                                            isCorrectState = false
                                            viewModel.recordAnswer(lesson.id, isCorrect = false)
                                            // Vibrate error
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                vibrator?.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
                                            }
                                            // Re-append to end of queue for review
                                            drillQueue.add(currentItem)
                                            scope.launch {
                                                delay(800)
                                                selectedOption = null
                                                isCorrectState = null
                                                if (currentIndex + 1 < drillQueue.size) {
                                                    currentIndex++
                                                } else {
                                                    isCompleted = true
                                                }
                                            }
                                        }
                                    }
                                    .testTag("option_card_$optionText"),
                                colors = CardDefaults.cardColors(containerColor = animatedBg)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = optionText,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = if (isSelected || (selectedOption != null && isTarget)) Color.White else MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                }
                            }
                        }

                        // If odd number in row, fill spacer
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.4f))

            // Bottom Progress Bar & Percentage matching screenshot
            VocabDrillProgressBar(
                correctCount = correctCount,
                initialTotal = initialTotal
            )
        }
    }
}

@Composable
internal fun VocabDrillProgressBar(
    correctCount: Int,
    initialTotal: Int
) {
    val progress = if (initialTotal > 0) {
        (correctCount.toFloat() / initialTotal.toFloat()).coerceIn(0f, 1f)
    } else 0f
    val percentText = String.format("%.1f%%", progress * 100f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$percentText ($correctCount / $initialTotal)",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
