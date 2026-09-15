package com.banaoreel.app.payments

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed class RazorpayResult {
    data class Success(val paymentId: String, val orderId: String, val signature: String) : RazorpayResult()
    data class Failure(val message: String) : RazorpayResult()
}

/**
 * MainActivity implements Razorpay's PaymentResultWithDataListener (the SDK only
 * delivers results to an Activity callback) and pushes results here; WalletScreen
 * collects from this bus instead of receiving the callback directly, since Compose
 * screens don't implement Activity interfaces.
 */
@Singleton
class RazorpayResultBus @Inject constructor() {
    private val _results = MutableSharedFlow<RazorpayResult>(extraBufferCapacity = 1)
    val results: SharedFlow<RazorpayResult> = _results

    suspend fun emit(result: RazorpayResult) {
        _results.emit(result)
    }
}
