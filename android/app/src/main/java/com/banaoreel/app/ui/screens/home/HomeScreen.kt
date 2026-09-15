package com.banaoreel.app.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(
    onCreateClick: () -> Unit,
    onWalletClick: () -> Unit,
    onJobClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BanaoReel") },
                actions = {
                    TextButton(onClick = onWalletClick) {
                        val balance = (state.wallet?.balancePaise ?: 0) / 100.0
                        Text("₹%.0f".format(balance))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Button(
                onClick = onCreateClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create video")
            }

            Spacer(Modifier.height(24.dp))
            Text("Recent generations", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            if (state.isLoading) {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn {
                    items(state.recentJobs) { job ->
                        ListItem(
                            headlineContent = { Text(job.prompt.take(60)) },
                            supportingContent = { Text(job.status.name) },
                            modifier = Modifier.clickable { onJobClick(job.id) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
