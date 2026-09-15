package com.banaoreel.app.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banaoreel.app.data.model.VideoJob
import com.banaoreel.app.data.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val videoRepository: VideoRepository
) : ViewModel() {

    private val _jobs = MutableStateFlow<List<VideoJob>>(emptyList())
    val jobs: StateFlow<List<VideoJob>> = _jobs

    init {
        viewModelScope.launch {
            _jobs.value = videoRepository.listVideos()
        }
    }
}
