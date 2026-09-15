package com.banaoreel.app.ui.screens.create

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.banaoreel.app.ui.theme.ReelGoldDeep
import com.banaoreel.app.ui.theme.ReelRose

private const val MAX_PROMPT_LENGTH = 300

@Composable
fun CreateScreen(
    onJobCreated: (String) -> Unit,
    onGoToWallet: () -> Unit,
    viewModel: CreateViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.createdJobId) {
        state.createdJobId?.let { onJobCreated(it) }
    }

    if (state.insufficientBalance) {
        AlertDialog(
            onDismissRequest = viewModel::dismissInsufficientBalance,
            title = { Text("Not enough balance") },
            text = { Text("Add money to your wallet to generate this video.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.dismissInsufficientBalance()
                    onGoToWallet()
                }) { Text("Add money", color = ReelRose) }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissInsufficientBalance) { Text("Cancel") }
            }
        )
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
                maxLines = 6,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ReelRose,
                    focusedLabelColor = ReelRose
                )
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    "${state.prompt.length}/$MAX_PROMPT_LENGTH",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(16.dp))

            Text("Duration", style = MaterialTheme.typography.labelLarge)
            Row {
                FilterChip(
                    selected = state.durationSec == 15,
                    onClick = { viewModel.onDurationChange(15) },
                    label = { Text("15s") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ReelRose,
                        selectedLabelColor = Color.White
                    )
                )
                Spacer(Modifier.width(8.dp))
                FilterChip(
                    selected = state.durationSec == 30,
                    onClick = { viewModel.onDurationChange(30) },
                    label = { Text("30s") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ReelRose,
                        selectedLabelColor = Color.White
                    )
                )
            }

            Spacer(Modifier.height(16.dp))
            Text(
                "Cost: ₹%.0f".format(state.costPaise / 100.0),
                style = MaterialTheme.typography.titleMedium,
                color = ReelGoldDeep
            )

            state.errorMessage?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = viewModel::submit,
                enabled = !state.isSubmitting && state.prompt.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ReelRose)
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(10.dp))
                }
                Text(if (state.isSubmitting) "Generating..." else "Generate")
            }
        }
    }
}
