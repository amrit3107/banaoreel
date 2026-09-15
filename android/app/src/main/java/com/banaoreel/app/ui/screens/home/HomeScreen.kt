package com.banaoreel.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.banaoreel.app.data.model.JobStatus
import com.banaoreel.app.data.model.VideoJob
import com.banaoreel.app.ui.theme.Mint
import com.banaoreel.app.ui.theme.ReelGold
import com.banaoreel.app.ui.theme.ReelGoldDeep
import com.banaoreel.app.ui.theme.ReelRose
import com.banaoreel.app.ui.theme.ReelViolet

@Composable
fun HomeScreen(
    onCreateClick: () -> Unit,
    onWalletClick: () -> Unit,
    onJobClick: (String) -> Unit,
    onHistoryClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BanaoReel") },
                actions = {
                    TextButton(onClick = onHistoryClick) { Text("History") }
                    WalletChip(
                        balancePaise = state.wallet?.balancePaise ?: 0,
                        onClick = onWalletClick
                    )
                    Spacer(Modifier.width(8.dp))
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
            // The one bold moment on this screen -- everything else stays quiet
            // around it, per the "spend your boldness in one place" principle.
            CreateHeroCard(onClick = onCreateClick)

            Spacer(Modifier.height(28.dp))
            Text("Recent generations", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))

            if (state.isLoading) {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ReelRose)
                }
            } else if (state.recentJobs.isEmpty()) {
                EmptyJobsState()
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(state.recentJobs) { job ->
                        JobRow(job = job, onClick = { onJobClick(job.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun WalletChip(balancePaise: Int, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = ReelGold.copy(alpha = 0.18f)
    ) {
        Text(
            "₹%.0f".format(balancePaise / 100.0),
            style = MaterialTheme.typography.labelLarge,
            color = ReelGoldDeep,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun CreateHeroCard(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .background(Brush.linearGradient(listOf(ReelRose, ReelViolet)))
            .clickable(onClick = onClick)
            .padding(24.dp)
    ) {
        Column {
            Text(
                "Type it. Watch it become a reel.",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Describe any idea and get a short video in under a minute.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(Modifier.height(20.dp))
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.White
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = ReelRose)
                    Spacer(Modifier.width(6.dp))
                    Text("Create video", style = MaterialTheme.typography.labelLarge, color = ReelRose)
                }
            }
        }
    }
}

@Composable
private fun JobRow(job: VideoJob, onClick: () -> Unit) {
    val accentColor = when (job.status) {
        JobStatus.DONE -> Mint
        JobStatus.FAILED -> MaterialTheme.colorScheme.error
        else -> ReelViolet
    }

    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(vertical = 4.dp)) {
            Box(
                Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(accentColor, RoundedCornerShape(2.dp))
            )
            Column(modifier = Modifier.padding(14.dp)) {
                Text(job.prompt.take(60), style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(2.dp))
                Text(
                    job.status.name.lowercase().replace('_', ' '),
                    style = MaterialTheme.typography.labelMedium,
                    color = accentColor
                )
            }
        }
    }
}

@Composable
private fun EmptyJobsState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Nothing here yet",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Your first video will show up here once it's ready.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
