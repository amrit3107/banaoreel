package com.banaoreel.app.ui.screens.create

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CreateScreen(
    onJobCreated: (String) -> Unit,
    viewModel: CreateViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.createdJobId) {
        state.createdJobId?.let { onJobCreated(it) }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Create video") }) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = state.prompt,
                onValueChange = viewModel::onPromptChange,
                label = { Text("Describe your video") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6
            )

            Spacer(Modifier.height(16.dp))

            Text("Duration", style = MaterialTheme.typography.labelLarge)
            Row {
                FilterChip(
                    selected = state.durationSec == 15,
                    onClick = { viewModel.onDurationChange(15) },
                    label = { Text("15s") }
                )
                Spacer(Modifier.width(8.dp))
                FilterChip(
                    selected = state.durationSec == 30,
                    onClick = { viewModel.onDurationChange(30) },
                    label = { Text("30s") }
                )
            }

            Spacer(Modifier.height(16.dp))
            Text("Cost: ₹%.0f".format(state.costPaise / 100.0), style = MaterialTheme.typography.titleMedium)

            state.errorMessage?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = viewModel::submit,
                enabled = !state.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isSubmitting) "Generating..." else "Generate")
            }
        }
    }
}
