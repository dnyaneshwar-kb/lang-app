package com.example.hsklearner.ui

import androidx.compose.foundation.clickable
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
import com.example.hsklearner.model.CourseUnit
import com.example.hsklearner.viewmodel.UnitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitListScreen(
    levelId: Int,
    viewModel: UnitViewModel = hiltViewModel(),
    onUnitClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    // Trigger data load when levelId changes
    LaunchedEffect(levelId) {
        viewModel.loadUnits(levelId)
    }

    val units by viewModel.units.collectAsState(initial = emptyList())
    val level by viewModel.level.collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = level?.description ?: "Units") },
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
            items(units) { unit ->
                UnitCard(unit = unit, onClick = { onUnitClick(unit.id) })
            }
        }
    }
}

@Composable
fun UnitCard(unit: CourseUnit, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Unit ${unit.unitNumber}: ${unit.title}", style = MaterialTheme.typography.titleMedium)
        }
    }
}
