package com.banaoreel.app.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.banaoreel.app.BuildConfig

@Composable
fun ProfileScreen(
    onLoggedOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.loggedOut) {
        if (state.loggedOut) onLoggedOut()
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Profile") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxWidth().padding(16.dp)) {
            OutlinedButton(
                onClick = viewModel::logout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log out")
            }

            Spacer(Modifier.height(24.dp))
            Text(
                "BanaoReel v${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
