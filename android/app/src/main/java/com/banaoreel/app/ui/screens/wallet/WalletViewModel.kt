package com.banaoreel.app.ui.screens.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banaoreel.app.data.model.Wallet
import com.banaoreel.app.data.model.WalletTransaction
import com.banaoreel.app.data.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WalletUiState(
    val wallet: Wallet? = null,
    val transactions: List<WalletTransaction> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val wallet = walletRepository.getWallet()
            val txns = walletRepository.getTransactions()
            _uiState.value = WalletUiState(wallet = wallet, transactions = txns, isLoading = false)
        }
    }

    // TODO: wire actual Razorpay checkout SDK here; on success call verifyRecharge then refresh()
}
