package com.banaoreel.app.ui.screens.jobstatus

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banaoreel.app.data.model.JobStatus
import com.banaoreel.app.data.model.VideoJob
import com.banaoreel.app.data.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JobStatusUiState(
    val job: VideoJob? = null,
    val errorMessage: String? = null
)

/**
 * Polls job status every 3s until DONE or FAILED.
 * TODO: replace polling with FCM push once backend triggers it on completion.
 */
@HiltViewModel
class JobStatusViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val jobId: String = checkNotNull(savedStateHandle["jobId"])

    private val _uiState = MutableStateFlow(JobStatusUiState())
    val uiState: StateFlow<JobStatusUiState> = _uiState

    init { startPolling() }

    private fun startPolling() {
        viewModelScope.launch {
            while (true) {
                try {
                    val current = videoRepository.getVideo(jobId)
                    _uiState.value = JobStatusUiState(job = current)
                    if (current.status == JobStatus.DONE || current.status == JobStatus.FAILED) break
                } catch (e: Exception) {
                    _uiState.value = JobStatusUiState(errorMessage = e.message)
                    break
                }
                delay(3000)
            }
        }
    }
}
