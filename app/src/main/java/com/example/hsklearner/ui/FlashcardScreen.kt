package com.example.hsklearner.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hsklearner.viewmodel.FlashcardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(
    lessonId: Int,
    viewModel: FlashcardViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    LaunchedEffect(lessonId) {
        viewModel.loadFormat(lessonId)
    }

    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flashcards") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.isFinished) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Session Complete!", style = MaterialTheme.typography.headlineLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onBackClick) {
                        Text("Back to Lesson")
                    }
                }
            } else {
                state.currentWord?.let { word ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .widthIn(max = 600.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Progress
                            LinearProgressIndicator(
                                progress = { (state.currentIndex + 1).toFloat() / state.totalCount },
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                            )

                            // Card Area
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(32.dp)
                                    .fillMaxWidth()
                                    .clickable { viewModel.flipCard() },
                                contentAlignment = Alignment.Center
                            ) {
                                FlashcardItem(
                                    hanzi = word.hanzi,
                                    pinyin = word.pinyin,
                                    definition = word.definition,
                                    isFlipped = state.isFlipped,
                                    onPlayAudio = { viewModel.playAudio() }
                                )
                            }

                            // Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(
                                    onClick = { viewModel.nextCard(false) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Forgot")
                                }
                                Button(
                                    onClick = { viewModel.nextCard(true) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text("Easy")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlashcardItem(
    hanzi: String,
    pinyin: String,
    definition: String,
    isFlipped: Boolean,
    onPlayAudio: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(400)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (rotation <= 90f) {
                // Front
                Text(text = hanzi, style = MaterialTheme.typography.displayLarge)
            } else {
                // Back
                Column(
                    modifier = Modifier.graphicsLayer { rotationY = 180f },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = pinyin, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = definition, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(32.dp))
                    IconButton(onClick = onPlayAudio) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Play Audio",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }
        }
    }
}
