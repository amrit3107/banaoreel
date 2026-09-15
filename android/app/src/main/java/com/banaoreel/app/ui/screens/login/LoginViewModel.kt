package com.banaoreel.app.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banaoreel.app.data.repository.AuthRepository
import com.banaoreel.app.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class LoginStep { CHECKING_SESSION, ENTER_PHONE, ENTER_OTP }

private const val OTP_LENGTH = 6
private const val RESEND_COOLDOWN_SECONDS = 30

data class LoginUiState(
    val step: LoginStep = LoginStep.CHECKING_SESSION,
    val phone: String = "",
    val otp: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loggedIn: Boolean = false,
    val needsOnboarding: Boolean = false,
    val resendSecondsLeft: Int = 0
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private var resendTimerJob: Job? = null

    init {
        viewModelScope.launch {
            if (authRepository.isLoggedIn()) {
                // A stored token doesn't guarantee onboarding was finished
                // (e.g. the app could've been closed mid-onboarding on a
                // previous run) -- check the real profile before deciding.
                val needsOnboarding = try {
                    profileRepository.getMyProfile().name.isNullOrBlank()
                } catch (e: Exception) {
                    false // if the check fails, don't block a returning user from Home
                }
                _uiState.value = _uiState.value.copy(loggedIn = true, needsOnboarding = needsOnboarding)
            } else {
                _uiState.value = _uiState.value.copy(step = LoginStep.ENTER_PHONE)
            }
        }
    }

    fun onPhoneChange(raw: String) {
        val digitsOnly = raw.filter { it.isDigit() }.take(10)
        _uiState.value = _uiState.value.copy(phone = digitsOnly)
    }

    fun onOtpChange(raw: String) {
        val digitsOnly = raw.filter { it.isDigit() }.take(OTP_LENGTH)
        _uiState.value = _uiState.value.copy(otp = digitsOnly, errorMessage = null)
        if (digitsOnly.length == OTP_LENGTH) {
            verifyOtp()
        }
    }

    fun requestOtp() {
        val phone = _uiState.value.phone
        if (phone.length < 10) {
            _uiState.value = _uiState.value.copy(errorMessage = "Enter a valid 10-digit phone number")
            return
        }
        sendOtp(phone)
    }

    /** Resend uses the same phone already on file; only enabled once the cooldown hits zero. */
    fun resendOtp() {
        if (_uiState.value.resendSecondsLeft > 0) return
        sendOtp(_uiState.value.phone)
    }

    private fun sendOtp(phone: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                authRepository.requestOtp(phone)
                _uiState.value = _uiState.value.copy(isLoading = false, step = LoginStep.ENTER_OTP, otp = "")
                startResendTimer()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message ?: "Failed to send OTP")
            }
        }
    }

    private fun startResendTimer() {
        resendTimerJob?.cancel()
        resendTimerJob = viewModelScope.launch {
            for (secondsLeft in RESEND_COOLDOWN_SECONDS downTo 0) {
                _uiState.value = _uiState.value.copy(resendSecondsLeft = secondsLeft)
                delay(1000)
            }
        }
    }

    fun backToPhoneEntry() {
        resendTimerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            step = LoginStep.ENTER_PHONE,
            otp = "",
            errorMessage = null,
            resendSecondsLeft = 0
        )
    }

    fun verifyOtp() {
        val state = _uiState.value
        if (state.isLoading) return // guard against double-submit from onOtpChange auto-trigger
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)
            try {
                val needsOnboarding = authRepository.verifyOtp(state.phone, state.otp)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    loggedIn = true,
                    needsOnboarding = needsOnboarding
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Invalid OTP",
                    otp = "" // clear so the boxes are ready for a retry
                )
            }
        }
    }

    override fun onCleared() {
        resendTimerJob?.cancel()
        super.onCleared()
    }
}
