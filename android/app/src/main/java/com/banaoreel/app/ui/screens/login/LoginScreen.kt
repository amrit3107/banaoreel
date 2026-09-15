package com.banaoreel.app.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.loggedIn) {
        if (state.loggedIn) onLoggedIn()
    }

    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column {
            Text("BanaoReel", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(24.dp))

            if (state.step == LoginStep.ENTER_PHONE) {
                OutlinedTextField(
                    value = state.phone,
                    onValueChange = viewModel::onPhoneChange,
                    label = { Text("Phone number") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = viewModel::requestOtp,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) { Text(if (state.isLoading) "Sending..." else "Send OTP") }
            } else {
                Text("Enter the code sent to ${state.phone}")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.otp,
                    onValueChange = viewModel::onOtpChange,
                    label = { Text("OTP") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = viewModel::verifyOtp,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) { Text(if (state.isLoading) "Verifying..." else "Verify") }
            }

            state.errorMessage?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
