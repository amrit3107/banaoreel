package com.banaoreel.app.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banaoreel.app.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Matches the backend's Gender enum (entity/Gender.java). */
enum class GenderOption(val apiValue: String, val label: String) {
    MALE("MALE", "Male"),
    FEMALE("FEMALE", "Female"),
    OTHER("OTHER", "Other"),
    PREFER_NOT_TO_SAY("PREFER_NOT_TO_SAY", "Prefer not to say")
}

data class OnboardingUiState(
    val name: String = "",
    val selectedGender: GenderOption? = null,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val completed: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name.take(60))
    }

    fun onGenderSelect(gender: GenderOption) {
        _uiState.value = _uiState.value.copy(selectedGender = gender)
    }

    fun submit() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Enter your name")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isSubmitting = true, errorMessage = null)
            try {
                profileRepository.updateProfile(state.name.trim(), state.selectedGender?.apiValue)
                _uiState.value = _uiState.value.copy(isSubmitting = false, completed = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage = "Couldn't save your details. Try again."
                )
            }
        }
    }
}
