package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.LessonEntity
import com.example.ui.theme.SuccessEmerald
import com.example.ui.viewmodel.LanguageLearnerViewModel
import java.util.Locale

@Composable
fun CurriculumScreen(
    viewModel: LanguageLearnerViewModel,
    onOpenLesson: (LessonEntity) -> Unit,
    onOpenSrsReview: () -> Unit,
    onOpenImportExport: () -> Unit
) {
    val context = LocalContext.current
    val lessons by viewModel.allLessons.collectAsStateWithLifecycle()
    val dueCount by viewModel.dueSrsCount.collectAsStateWithLifecycle()
    val isLatin by viewModel.isLatinScript.collectAsStateWithLifecycle()
    val isDark by viewModel.isDarkTheme.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // App Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Полиглот",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (isLatin) "Srpski jezik (B1 • Latinica)" else "Српски језик (B1 • Ћирилица)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Script toggle (Cyrillic / Latin)
                IconButton(
                    onClick = { viewModel.toggleScript() },
                    modifier = Modifier.testTag("toggle_script_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "Переключить алфавит (Кириллица / Латиница)",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Dark / Light Theme toggle
                IconButton(
                    onClick = { viewModel.toggleDarkTheme() },
                    modifier = Modifier.testTag("toggle_theme_button")
                ) {
                    Icon(
                        imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Переключить тему оформления",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Navigation Tabs (Lessons, Spaced Repetition, Importer)
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                modifier = Modifier.testTag("tab_lessons"),
                text = { Text("Уроки", fontWeight = FontWeight.SemiBold) },
                icon = {
                    Icon(Icons.Default.School, contentDescription = "Список уроков")
                }
            )

            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                modifier = Modifier.testTag("tab_srs"),
                text = { Text("Повторение", fontWeight = FontWeight.SemiBold) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (dueCount > 0) {
                                Badge(containerColor = MaterialTheme.colorScheme.error) {
                                    Text("$dueCount", color = Color.White)
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Psychology, contentDescription = "Интервальное повторение")
                    }
                }
            )

            Tab(
                selected = selectedTabIndex == 2,
                onClick = { selectedTabIndex = 2 },
                modifier = Modifier.testTag("tab_import"),
                text = { Text("Импорт", fontWeight = FontWeight.SemiBold) },
                icon = {
                    Icon(Icons.Default.FileDownload, contentDescription = "Импорт уроков и словарей")
                }
            )
        }

        when (selectedTabIndex) {
            0 -> {
                // Lessons Stream
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("lesson_list"),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(lessons, key = { it.id }) { lesson ->
                            LessonStreamCard(
                                lesson = lesson,
                                onClick = {
                                    if (lesson.isUnlocked) {
                                        viewModel.selectLesson(lesson)
                                        onOpenLesson(lesson)
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Урок заблокирован. Наберите оценку 4.5 (90/100) в Уроке ${lesson.orderIndex - 1}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }

                    // Floating Bottom "Продолжить урок" bar
                    if (lessons.isNotEmpty()) {
                        val active = lessons.firstOrNull { it.isUnlocked && !it.isCompleted }
                            ?: lessons.firstOrNull { it.isUnlocked }
                            ?: lessons.first()

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                                .navigationBarsPadding()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.selectLesson(active)
                                    onOpenLesson(active)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("resume_active_lesson_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Продолжить: ${active.title}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            1 -> {
                SpacedRepetitionLauncherTab(
                    dueCount = dueCount,
                    onStartReview = onOpenSrsReview
                )
            }

            2 -> {
                QuickImporterTab(onOpenFullImporter = onOpenImportExport)
            }
        }
    }
}

@Composable
fun LessonStreamCard(
    lesson: LessonEntity,
    onClick: () -> Unit
) {
    val isLocked = !lesson.isUnlocked

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("lesson_card_${lesson.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isLocked)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLocked) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular badge indicator: Lock icon if locked, Checkmark if completed, Grade otherwise
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isLocked -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            lesson.isCompleted -> SuccessEmerald
                            else -> MaterialTheme.colorScheme.primary
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Урок заблокирован",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else if (lesson.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Урок освоен",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    // Display actual grade (starts at 0.0)
                    val gradeText = String.format(Locale.US, "%.1f", lesson.masteryScore)
                    Text(
                        text = gradeText,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (isLocked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                if (isLocked) {
                    Text(
                        text = "Заблокировано (требуется оценка 4.5 в предыдущем уроке)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                } else {
                    Text(
                        text = lesson.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isLocked) MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = lesson.grammarFocus,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isLocked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (!isLocked) {
                        Text(
                            text = "Верно: ${lesson.correctAnswersInLast100}/100 • Оценка: ${String.format(Locale.US, "%.1f", lesson.masteryScore)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SpacedRepetitionLauncherTab(
    dueCount: Int,
    onStartReview: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Интервальное повторение (SM-2)",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (dueCount > 0)
                "Карточек готово к повторению сегодня: $dueCount"
            else
                "Отличная работа! Все слова и грамматические шаблоны повторены на сегодня.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onStartReview,
            enabled = dueCount > 0,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(48.dp)
                .testTag("start_srs_review_button"),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Начать повторение ($dueCount)", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun QuickImporterTab(onOpenFullImporter: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.FileDownload,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(56.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Пользовательские программы и словари",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Импортируйте собственные уроки, словари и упражнения из файлов XML, CSV или JSON с поддержкой аудиозаписей.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onOpenFullImporter,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(48.dp)
                .testTag("open_importer_button")
        ) {
            Text("Открыть менеджер импорта", fontWeight = FontWeight.Bold)
        }
    }
}
