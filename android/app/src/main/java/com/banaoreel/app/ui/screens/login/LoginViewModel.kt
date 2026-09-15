package com.banaoreel.app.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banaoreel.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class LoginStep { CHECKING_SESSION, ENTER_PHONE, ENTER_OTP }

data class LoginUiState(
    val step: LoginStep = LoginStep.CHECKING_SESSION,
    val phone: String = "",
    val otp: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loggedIn: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    init {
        viewModelScope.launch {
            // Skip straight to Home if a token is already stored -- this was
            // missing before, which is why the app asked to log in on every
            // launch even though TokenStore was already persisting it.
            if (authRepository.isLoggedIn()) {
                _uiState.value = _uiState.value.copy(loggedIn = true)
            } else {
                _uiState.value = _uiState.value.copy(step = LoginStep.ENTER_PHONE)
            }
        }
    }

    fun onPhoneChange(phone: String) {
        _uiState.value = _uiState.value.copy(phone = phone)
    }

    fun onOtpChange(otp: String) {
        _uiState.value = _uiState.value.copy(otp = otp)
    }

    fun requestOtp() {
        val phone = _uiState.value.phone
        if (phone.length < 10) {
            _uiState.value = _uiState.value.copy(errorMessage = "Enter a valid phone number")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                authRepository.requestOtp(phone)
                _uiState.value = _uiState.value.copy(isLoading = false, step = LoginStep.ENTER_OTP)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message ?: "Failed to send OTP")
            }
        }
    }

    fun verifyOtp() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)
            try {
                authRepository.verifyOtp(state.phone, state.otp)
                _uiState.value = _uiState.value.copy(isLoading = false, loggedIn = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message ?: "Invalid OTP")
            }
        }
    }
}
