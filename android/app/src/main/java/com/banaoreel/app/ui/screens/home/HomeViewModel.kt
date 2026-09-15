package com.banaoreel.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banaoreel.app.data.model.VideoJob
import com.banaoreel.app.data.model.Wallet
import com.banaoreel.app.data.repository.VideoRepository
import com.banaoreel.app.data.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val wallet: Wallet? = null,
    val recentJobs: List<VideoJob> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch {
            try {
                val wallet = walletRepository.getWallet()
                val jobs = videoRepository.listVideos()
                _uiState.value = HomeUiState(wallet = wallet, recentJobs = jobs, isLoading = false)
            } catch (e: Exception) {
                // Surfaced instead of silently leaving wallet=null (which the UI
                // would otherwise render as an innocuous-looking ₹0 balance).
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Couldn't load your account"
                )
            }
        }
    }
}
