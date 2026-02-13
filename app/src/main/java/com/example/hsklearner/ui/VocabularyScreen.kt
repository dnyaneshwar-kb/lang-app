package com.example.hsklearner.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hsklearner.model.Vocabulary
import com.example.hsklearner.viewmodel.VocabularyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyScreen(
    lessonId: Int,
    viewModel: VocabularyViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    LaunchedEffect(lessonId) {
        viewModel.loadVocabulary(lessonId)
    }

    val vocabularyList by viewModel.vocabularyList.collectAsState(initial = emptyList())
    
    VocabularyListContent(
        vocabularyList = vocabularyList,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyListContent(
    vocabularyList: List<Vocabulary>,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Vocabulary") },
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
            items(vocabularyList) { vocab ->
                VocabularyCard(vocab = vocab)
            }
        }
    }
}

@Composable
fun VocabularyCard(vocab: Vocabulary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = vocab.hanzi, style = MaterialTheme.typography.headlineMedium)
                Text(text = vocab.pinyin, style = MaterialTheme.typography.bodyLarge)
            }
            Text(text = vocab.definition, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
