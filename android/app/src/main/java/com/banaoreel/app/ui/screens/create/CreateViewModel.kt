package com.banaoreel.app.ui.screens.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banaoreel.app.data.PricingRules
import com.banaoreel.app.data.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

data class CreateUiState(
    val prompt: String = "",
    val durationSec: Int = 30,
    val costPaise: Int = PricingRules.costForDurationPaise(30),
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val insufficientBalance: Boolean = false,
    val createdJobId: String? = null
)

private const val MAX_PROMPT_LENGTH = 300

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val videoRepository: VideoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateUiState())
    val uiState: StateFlow<CreateUiState> = _uiState

    fun onPromptChange(prompt: String) {
        _uiState.value = _uiState.value.copy(prompt = prompt.take(MAX_PROMPT_LENGTH))
    }

    fun onDurationChange(durationSec: Int) {
        _uiState.value = _uiState.value.copy(
            durationSec = durationSec,
            costPaise = PricingRules.costForDurationPaise(durationSec)
        )
    }

    fun submit() {
        val state = _uiState.value
        if (state.prompt.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Enter a prompt first")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isSubmitting = true, errorMessage = null, insufficientBalance = false)
            try {
                val job = videoRepository.createVideo(state.prompt, state.durationSec)
                _uiState.value = _uiState.value.copy(isSubmitting = false, createdJobId = job.id)
            } catch (e: HttpException) {
                if (e.code() == 402) {
                    // Matches the backend's ResponseStatusException(PAYMENT_REQUIRED)
                    // in VideoJobController when the wallet debit fails.
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        insufficientBalance = true,
                        errorMessage = "Not enough balance for this video"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        errorMessage = "Something went wrong. Try again."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage = "Couldn't reach the server. Check your connection."
                )
            }
        }
    }

    fun dismissInsufficientBalance() {
        _uiState.value = _uiState.value.copy(insufficientBalance = false)
    }
}
