package com.example.hsklearner.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hsklearner.viewmodel.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    lessonId: Int,
    viewModel: QuizViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    LaunchedEffect(lessonId) {
        viewModel.loadQuiz(lessonId)
    }

    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.questions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.isFinished) {
                QuizResultContent(
                    score = state.score,
                    total = state.questions.size,
                    passed = state.passed,
                    onBackClick = onBackClick
                )
            } else {
                QuizQuestionContent(
                    state = state,
                    onOptionSelected = { viewModel.submitAnswer(it) },
                    onNext = { viewModel.nextQuestion() }
                )
            }
        }
    }
}

@Composable
fun QuizQuestionContent(
    state: com.example.hsklearner.viewmodel.QuizState,
    onOptionSelected: (Int) -> Unit,
    onNext: () -> Unit
) {
    val currentQuestion = state.questions[state.currentQuestionIndex]

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 600.dp)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress Bar
            LinearProgressIndicator(
                progress = { (state.currentQuestionIndex + 1).toFloat() / state.questions.size },
                modifier = Modifier.fillMaxWidth().height(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Question ${state.currentQuestionIndex + 1}/${state.questions.size}",
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Question
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier.padding(32.dp).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentQuestion.questionText,
                        style = MaterialTheme.typography.displayMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Options
            currentQuestion.options.forEachIndexed { index, optionText ->
                val isSelected = state.selectedOptionIndex == index
                val isCorrect = index == currentQuestion.correctOptionIndex
                val showResult = state.selectedOptionIndex != null

                val containerColor = if (showResult) {
                    if (isCorrect) Color(0xFF4CAF50) // Green
                    else if (isSelected) Color(0xFFF44336) // Red
                    else MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }

                val contentColor = if (showResult && (isCorrect || isSelected)) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

                Button(
                    onClick = { if (!showResult) onOptionSelected(index) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = containerColor,
                        contentColor = contentColor
                    )
                ) {
                    Text(text = optionText, style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Next Button (only when answered)
            if (state.selectedOptionIndex != null) {
                Button(
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Next")
                }
            }
        }
    }
}

@Composable
fun QuizResultContent(
    score: Int,
    total: Int,
    passed: Boolean,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (passed) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = if (passed) Color(0xFF4CAF50) else Color(0xFFF44336),
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (passed) "Passed!" else "Try Again",
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Score: $score / $total",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onBackClick) {
            Text("Back to Lessons")
        }
    }
}
