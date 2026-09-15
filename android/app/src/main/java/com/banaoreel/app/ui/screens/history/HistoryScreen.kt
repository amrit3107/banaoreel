package com.banaoreel.app.ui.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HistoryScreen(
    onJobClick: (String) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val jobs by viewModel.jobs.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("History") }) }) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
            items(jobs) { job ->
                ListItem(
                    headlineContent = { Text(job.prompt.take(60)) },
                    supportingContent = { Text("${job.status.name} · ${job.durationSec}s") },
                    modifier = Modifier.clickable { onJobClick(job.id) }
                )
                HorizontalDivider()
            }
        }
    }
}
