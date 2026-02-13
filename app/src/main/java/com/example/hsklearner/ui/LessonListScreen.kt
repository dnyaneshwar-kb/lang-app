package com.example.hsklearner.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hsklearner.model.Lesson
import com.example.hsklearner.viewmodel.LessonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonListScreen(
    unitId: Int,
    viewModel: LessonViewModel = hiltViewModel(),
    onLessonClick: (Int) -> Unit,
    onFlashcardClick: (Int) -> Unit,
    onQuizClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    LaunchedEffect(unitId) {
        viewModel.loadLessons(unitId)
    }

    val lessonStatuses by viewModel.lessonStatuses.collectAsState(initial = emptyList())
    val unit by viewModel.unit.collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = unit?.title ?: "Lessons") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(lessonStatuses) { status ->
                LessonCard(
                    status = status,
                    onVocabClick = { if (!status.isLocked) onLessonClick(status.lesson.id) },
                    onFlashcardClick = { if (!status.isLocked) onFlashcardClick(status.lesson.id) },
                    onQuizClick = { if (!status.isLocked) onQuizClick(status.lesson.id) }
                )
            }
        }
    }
}

@Composable
fun LessonCard(
    status: LessonViewModel.LessonWithStatus,
    onVocabClick: () -> Unit,
    onFlashcardClick: () -> Unit,
    onQuizClick: () -> Unit
) {
    val alpha = if (status.isLocked) 0.38f else 1f
    
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (status.isLocked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Lesson ${status.lesson.lessonNumber}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = alpha)
                    )
                    Text(
                        text = status.lesson.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
                    )
                }
                
                if (status.isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                    )
                } else if (status.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Vocab
                FilledTonalButton(
                    onClick = onVocabClick,
                    enabled = !status.isLocked,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text("Vocab")
                }

                // Flashcards
                FilledTonalButton(
                    onClick = onFlashcardClick,
                    enabled = !status.isLocked,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text("Cards")
                }

                Spacer(modifier = Modifier.weight(1f))

                // Quiz (Highlight)
                Button(
                    onClick = onQuizClick,
                    enabled = !status.isLocked
                ) {
                    Text("Quiz")
                }
            }
        }
    }
}
