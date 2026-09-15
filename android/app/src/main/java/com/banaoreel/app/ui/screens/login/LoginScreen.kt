package com.banaoreel.app.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.banaoreel.app.ui.theme.Ink
import com.banaoreel.app.ui.theme.InkMuted
import com.banaoreel.app.ui.theme.ReelRose
import com.banaoreel.app.ui.theme.ReelViolet

private const val OTP_LENGTH = 6

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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Brush.linearGradient(listOf(ReelRose, ReelViolet))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("BanaoReel", style = MaterialTheme.typography.displayLarge, color = Color.White)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Type an idea. Get a reel.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            if (state.step == LoginStep.ENTER_PHONE) {
                PhoneEntryStep(state = state, viewModel = viewModel)
            } else {
                OtpEntryStep(state = state, viewModel = viewModel)
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

@Composable
private fun PhoneEntryStep(state: LoginUiState, viewModel: LoginViewModel) {
    Text("Enter your phone number", style = MaterialTheme.typography.titleLarge)
    Spacer(Modifier.height(16.dp))
    OutlinedTextField(
        value = state.phone,
        onValueChange = viewModel::onPhoneChange,
        label = { Text("Phone number") },
        placeholder = { Text("10-digit number") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ReelRose,
            focusedLabelColor = ReelRose
        )
    )
    Spacer(Modifier.height(20.dp))
    Button(
        onClick = viewModel::requestOtp,
        enabled = !state.isLoading && state.phone.length == 10,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ReelRose)
    ) {
        Text(if (state.isLoading) "Sending..." else "Send OTP", fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun OtpEntryStep(state: LoginUiState, viewModel: LoginViewModel) {
    Text("Enter the code", style = MaterialTheme.typography.titleLarge)
    Spacer(Modifier.height(4.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            "Sent to ${state.phone}",
            style = MaterialTheme.typography.bodyMedium,
            color = InkMuted
        )
        Spacer(Modifier.width(8.dp))
        TextButton(onClick = viewModel::backToPhoneEntry) {
            Text("Change", color = ReelRose, style = MaterialTheme.typography.labelMedium)
        }
    }

    Spacer(Modifier.height(20.dp))
    OtpBoxInput(
        value = state.otp,
        onValueChange = viewModel::onOtpChange,
        enabled = !state.isLoading
    )

    Spacer(Modifier.height(20.dp))
    Button(
        onClick = viewModel::verifyOtp,
        enabled = !state.isLoading && state.otp.length == OTP_LENGTH,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ReelRose)
    ) {
        Text(if (state.isLoading) "Verifying..." else "Verify", fontWeight = FontWeight.SemiBold)
    }

    Spacer(Modifier.height(16.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (state.resendSecondsLeft > 0) {
            Text(
                "Resend OTP in ${state.resendSecondsLeft}s",
                style = MaterialTheme.typography.bodyMedium,
                color = InkMuted
            )
        } else {
            TextButton(onClick = viewModel::resendOtp) {
                Text("Resend OTP", color = ReelRose, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

/**
 * Segmented OTP boxes driven by a single invisible BasicTextField -- this is
 * the standard reliable approach: one real text field owns focus/cursor/
 * keyboard state (so autofill from SMS still works), and decorationBox
 * replaces its default rendering with our boxes instead of juggling focus
 * across N separate fields by hand.
 */
@Composable
private fun OtpBoxInput(value: String, onValueChange: (String) -> Unit, enabled: Boolean) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                repeat(OTP_LENGTH) { index ->
                    val char = value.getOrNull(index)?.toString() ?: ""
                    val isCurrent = index == value.length
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = if (isCurrent) ReelRose else InkMuted.copy(alpha = 0.35f),
                                shape = MaterialTheme.shapes.small
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(char, style = MaterialTheme.typography.titleLarge, color = Ink)
                    }
                }
            }
        }
    )
}
