package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.srs.ReviewGrade
import com.example.ui.theme.ErrorCoral
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.LanguageLearnerViewModel

@Composable
fun SpacedRepetitionScreen(
    viewModel: LanguageLearnerViewModel,
    onBack: () -> Unit
) {
    val dueItemsRaw by viewModel.dueSrsItems.collectAsStateWithLifecycle()
    val dailyBlended by viewModel.dailyBlendedQueue.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.refreshDailyBlendedQueue()
    }
    val dueItems = if (dueItemsRaw.isNotEmpty()) dueItemsRaw else dailyBlended

    var currentIndex by remember { mutableIntStateOf(0) }
    var isRevealed by remember { mutableStateOf(false) }

    val effectiveIndex = if (dueItems.isEmpty()) 0 else currentIndex.coerceIn(0, dueItems.lastIndex)
    val currentItem = dueItems.getOrNull(effectiveIndex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("back_from_srs")
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
                    text = "Интервальное повторение",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (dueItems.isNotEmpty()) "Карточка ${effectiveIndex + 1} из ${dueItems.size}" else "Нет карточек",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (currentItem == null || dueItems.isEmpty()) {
            // Queue empty state
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
                    text = "Очередь чиста!",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Вы повторили все назначенные на сегодня слова и грамматические шаблоны.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(48.dp)
                        .testTag("srs_done_back_button")
                ) {
                    Text("Вернуться к урокам", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // Active Flashcard
            Spacer(modifier = Modifier.weight(0.3f))

            val isSentence = currentItem.itemType == "SENTENCE"
            // Sentence cards: prompt = source language, secondaryText = Latin target (the answer).
            val promptText = if (isSentence) currentItem.prompt else currentItem.secondaryText.ifBlank { currentItem.prompt }
            val answerText = if (isSentence) currentItem.secondaryText.ifBlank { currentItem.answer } else currentItem.answer
            val targetSpeech = if (isSentence) answerText else promptText

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = promptText,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.testTag("srs_prompt_text")
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                viewModel.playAudio(targetSpeech, isTargetLanguage = true)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Озвучить карточку",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    AnimatedVisibility(visible = isRevealed) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = answerText,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.testTag("srs_answer_text")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Интервал: ${currentItem.intervalDays} дн. • Множитель: ${String.format("%.2f", currentItem.easeFactor)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            if (!isRevealed) {
                // Show Answer button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    Button(
                        onClick = {
                            isRevealed = true
                            viewModel.playAudio(targetSpeech, isTargetLanguage = true)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("show_answer_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Показать перевод", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            } else {
                // SM-2 Rating Buttons: Again, Hard, Good, Easy
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Оцените легкость ответа:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Again
                        GradeButton(
                            grade = ReviewGrade.AGAIN,
                            color = ErrorCoral,
                            onClick = {
                                viewModel.recordReview(currentItem, ReviewGrade.AGAIN)
                                isRevealed = false
                                if (currentIndex + 1 < dueItems.size) currentIndex++ else currentIndex = 0
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "grade_again"
                        )

                        // Hard
                        GradeButton(
                            grade = ReviewGrade.HARD,
                            color = WarningAmber,
                            onClick = {
                                viewModel.recordReview(currentItem, ReviewGrade.HARD)
                                isRevealed = false
                                if (currentIndex + 1 < dueItems.size) currentIndex++ else currentIndex = 0
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "grade_hard"
                        )

                        // Good
                        GradeButton(
                            grade = ReviewGrade.GOOD,
                            color = MaterialTheme.colorScheme.primary,
                            onClick = {
                                viewModel.recordReview(currentItem, ReviewGrade.GOOD)
                                isRevealed = false
                                if (currentIndex + 1 < dueItems.size) currentIndex++ else currentIndex = 0
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "grade_good"
                        )

                        // Easy
                        GradeButton(
                            grade = ReviewGrade.EASY,
                            color = SuccessEmerald,
                            onClick = {
                                viewModel.recordReview(currentItem, ReviewGrade.EASY)
                                isRevealed = false
                                if (currentIndex + 1 < dueItems.size) currentIndex++ else currentIndex = 0
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "grade_easy"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GradeButton(
    grade: ReviewGrade,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .height(56.dp)
            .testTag(testTag)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(grade.label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(grade.intervalDescription, fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
        }
    }
}
