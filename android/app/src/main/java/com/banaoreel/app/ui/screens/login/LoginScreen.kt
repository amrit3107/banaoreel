package com.banaoreel.app.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.banaoreel.app.ui.theme.ReelRose
import com.banaoreel.app.ui.theme.ReelViolet

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.loggedIn) {
        if (state.loggedIn) onLoggedIn()
    }

    if (state.step == LoginStep.CHECKING_SESSION) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ReelRose)
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Brand header, same gradient language as the Home screen's hero card --
        // the first thing anyone sees should already feel like BanaoReel.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Brush.linearGradient(listOf(ReelRose, ReelViolet))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "BanaoReel",
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Type an idea. Get a reel.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            if (state.step == LoginStep.ENTER_PHONE) {
                Text(
                    "Enter your phone number",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = state.phone,
                    onValueChange = viewModel::onPhoneChange,
                    label = { Text("Phone number") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ReelRose,
                        focusedLabelColor = ReelRose
                    )
                )
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = viewModel::requestOtp,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ReelRose)
                ) {
                    Text(
                        if (state.isLoading) "Sending..." else "Send OTP",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                Text(
                    "Enter the code",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "We sent a code to ${state.phone}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = state.otp,
                    onValueChange = viewModel::onOtpChange,
                    label = { Text("OTP") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ReelRose,
                        focusedLabelColor = ReelRose
                    )
                )
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = viewModel::verifyOtp,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ReelRose)
                ) {
                    Text(
                        if (state.isLoading) "Verifying..." else "Verify",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            state.errorMessage?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
