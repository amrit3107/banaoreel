package com.banaoreel.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.lifecycle.lifecycleScope
import com.banaoreel.app.payments.RazorpayResult
import com.banaoreel.app.payments.RazorpayResultBus
import com.banaoreel.app.ui.navigation.BanaoReelNavGraph
import com.banaoreel.app.ui.theme.BanaoReelTheme
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

/**
 * Implements PaymentResultWithDataListener because Razorpay's Android SDK only
 * delivers checkout results to an Activity, not to an arbitrary composable or
 * ViewModel — results are forwarded to RazorpayResultBus, which WalletScreen
 * collects from.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity(), PaymentResultWithDataListener {

    @Inject lateinit var razorpayResultBus: RazorpayResultBus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BanaoReelTheme {
                Surface {
                    BanaoReelNavGraph()
                }
            }
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?, paymentData: PaymentData?) {
        lifecycleScope.launch {
            val orderId = paymentData?.orderId
            val signature = paymentData?.signature
            if (razorpayPaymentId != null && orderId != null && signature != null) {
                razorpayResultBus.emit(RazorpayResult.Success(razorpayPaymentId, orderId, signature))
            } else {
                razorpayResultBus.emit(RazorpayResult.Failure("Payment succeeded but response was incomplete"))
            }
        }
    }

    override fun onPaymentError(code: Int, response: String?, paymentData: PaymentData?) {
        lifecycleScope.launch {
            razorpayResultBus.emit(RazorpayResult.Failure(friendlyRazorpayError(response)))
        }
    }

    /**
     * Razorpay's SDK hands back its raw API error JSON in `response` (e.g.
     * {"error":{"code":"BAD_REQUEST_ERROR","description":"Payment
     * Failed",...}}) -- showing that directly to the user is what was
     * happening before this fix. Extract just the human-readable description,
     * with a safe generic fallback if the shape ever changes.
     */
    private fun friendlyRazorpayError(response: String?): String {
        if (response.isNullOrBlank()) return "Payment failed. Please try again."
        return try {
            val description = JSONObject(response).optJSONObject("error")?.optString("description")
            description?.takeIf { it.isNotBlank() } ?: "Payment failed. Please try again."
        } catch (e: Exception) {
            "Payment failed. Please try again."
        }
    }
}
