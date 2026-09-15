package com.banaoreel.app.ui.screens.jobstatus

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.banaoreel.app.data.model.JobStatus
import com.banaoreel.app.data.model.VideoJob
import com.banaoreel.app.ui.theme.ReelRose

@Composable
fun JobStatusScreen(
    onDone: (VideoJob) -> Unit,
    viewModel: JobStatusViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.job?.status) {
        if (state.job?.status == JobStatus.DONE) {
            onDone(state.job!!)
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Generating") }) }) { padding ->
        Box(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                when {
                    state.errorMessage != null ->
                        Text(state.errorMessage!!, color = MaterialTheme.colorScheme.error)
                    state.job?.status == JobStatus.FAILED ->
                        Text("Generation failed. Amount refunded to wallet.", color = MaterialTheme.colorScheme.error)
                    else -> {
                        CircularProgressIndicator(color = ReelRose)
                        Spacer(Modifier.height(16.dp))
                        Text(state.job?.status?.name ?: "QUEUED")
                    }
                }
            }
        }
    }
}
