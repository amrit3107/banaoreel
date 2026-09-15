package com.banaoreel.app.ui.screens.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banaoreel.app.data.model.Wallet
import com.banaoreel.app.data.model.WalletTransaction
import com.banaoreel.app.data.repository.WalletRepository
import com.banaoreel.app.payments.RazorpayResult
import com.banaoreel.app.payments.RazorpayResultBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WalletUiState(
    val wallet: Wallet? = null,
    val transactions: List<WalletTransaction> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    /** Set once /recharge/initiate returns; the screen launches Razorpay checkout with this. */
    val pendingOrderId: String? = null,
    val pendingAmountPaise: Int? = null
)

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val razorpayResultBus: RazorpayResultBus
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState

    init {
        refresh()
        // MainActivity forwards Razorpay's checkout callback here since the SDK
        // only delivers results to an Activity, not to this ViewModel directly.
        viewModelScope.launch {
            razorpayResultBus.results.collect { result ->
                when (result) {
                    is RazorpayResult.Success -> onCheckoutSuccess(result.paymentId, result.orderId, result.signature)
                    is RazorpayResult.Failure -> onCheckoutFailure(result.message)
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val wallet = walletRepository.getWallet()
                val txns = walletRepository.getTransactions()
                _uiState.value = _uiState.value.copy(
                    wallet = wallet, transactions = txns, isLoading = false, errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Couldn't load your wallet"
                )
            }
        }
    }

    fun startRecharge(amountRupees: Int) {
        viewModelScope.launch {
            try {
                val amountPaise = amountRupees * 100
                val response = walletRepository.initiateRecharge(amountPaise)
                _uiState.value = _uiState.value.copy(
                    pendingOrderId = response.gatewayOrderId,
                    pendingAmountPaise = response.amountPaise
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message ?: "Could not start recharge")
            }
        }
    }

    fun onCheckoutSuccess(paymentId: String, orderId: String, signature: String) {
        viewModelScope.launch {
            try {
                walletRepository.verifyRecharge(paymentId, orderId, signature)
                _uiState.value = _uiState.value.copy(pendingOrderId = null, pendingAmountPaise = null)
                refresh()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message ?: "Could not verify payment")
            }
        }
    }

    fun onCheckoutFailure(message: String) {
        _uiState.value = _uiState.value.copy(
            pendingOrderId = null,
            pendingAmountPaise = null,
            errorMessage = message
        )
    }

    fun consumedPendingOrder() {
        // Called by the screen right after it launches Razorpay checkout, so the
        // LaunchedEffect watching pendingOrderId doesn't refire on recomposition.
        _uiState.value = _uiState.value.copy(pendingOrderId = null)
    }
}
