package com.banaoreel.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.banaoreel.app.BuildConfig
import com.banaoreel.app.ui.screens.onboarding.GenderOption
import com.banaoreel.app.ui.theme.InkMuted
import com.banaoreel.app.ui.theme.ReelRose
import com.banaoreel.app.ui.theme.ReelViolet

@Composable
fun ProfileScreen(
    onLoggedOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.loggedOut) {
        if (state.loggedOut) onLoggedOut()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Brush.linearGradient(listOf(ReelRose, ReelViolet))),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(color = Color.White)
                state.profile != null -> ProfileHeader(name = state.profile!!.name)
                else -> Text(state.errorMessage ?: "Couldn't load profile", color = Color.White)
            }
        }

        state.profile?.let { profile ->
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                ProfileField(label = "Phone", value = profile.phone)
                Spacer(Modifier.height(12.dp))
                ProfileField(
                    label = "Gender",
                    value = GenderOption.entries.find { it.apiValue == profile.gender }?.label ?: "Not set"
                )

                Spacer(Modifier.height(32.dp))
                OutlinedButton(
                    onClick = viewModel::logout,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Log out", fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(20.dp))
                Text(
                    "BanaoReel v${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.labelMedium,
                    color = InkMuted,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ProfileHeader(name: String?) {
    val displayName = name?.takeIf { it.isNotBlank() } ?: "there"
    val initials = displayName.trim().split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")
        .ifBlank { "B" }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Text(initials, style = MaterialTheme.typography.headlineMedium, color = Color.White)
        }
        Spacer(Modifier.height(12.dp))
        Text(displayName, style = MaterialTheme.typography.headlineMedium, color = Color.White)
    }
}

@Composable
private fun ProfileField(label: String, value: String) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = InkMuted)
            Spacer(Modifier.height(2.dp))
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
