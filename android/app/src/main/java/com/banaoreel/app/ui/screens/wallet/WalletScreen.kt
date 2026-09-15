package com.banaoreel.app.ui.screens.wallet

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.banaoreel.app.ui.theme.Mint
import com.banaoreel.app.ui.theme.ReelGoldDeep
import com.razorpay.Checkout
import org.json.JSONObject

/**
 * Razorpay checkout key: set your test/live key id here (same value as the
 * backend's banaoreel.razorpay.key-id). Razorpay's Android SDK needs the
 * public key id client-side; the secret stays backend-only.
 */
private const val RAZORPAY_KEY_ID = "rzp_test_TcKozs4tBz95JW"

@Composable
fun WalletScreen(viewModel: WalletViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    // Launch Razorpay checkout as soon as we have a pending order from the backend.
    // The result comes back via MainActivity -> RazorpayResultBus -> WalletViewModel,
    // not through this composable directly (see WalletViewModel's init block).
    LaunchedEffect(state.pendingOrderId) {
        val orderId = state.pendingOrderId
        val amountPaise = state.pendingAmountPaise
        if (orderId != null && amountPaise != null && activity != null) {
            val checkout = Checkout()
            checkout.setKeyID(RAZORPAY_KEY_ID)
            val options = JSONObject().apply {
                put("name", "BanaoReel")
                put("order_id", orderId)
                put("amount", amountPaise)
                put("currency", "INR")
            }
            checkout.open(activity, options)
            viewModel.consumedPendingOrder()
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Wallet") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text(
                "₹%.0f".format((state.wallet?.balancePaise ?: 0) / 100.0),
                style = MaterialTheme.typography.displayLarge,
                color = ReelGoldDeep
            )

            Spacer(Modifier.height(16.dp))
            Row {
                listOf(50, 100, 200).forEach { amount ->
                    OutlinedButton(
                        onClick = { viewModel.startRecharge(amount) },
                        modifier = Modifier.padding(end = 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ReelGoldDeep)
                    ) { Text("₹$amount") }
                }
            }

            state.errorMessage?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(24.dp))
            Text("Transactions", style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(state.transactions) { txn ->
                    val amountColor = if (txn.type == "recharge") Mint else MaterialTheme.colorScheme.onSurface
                    ListItem(
                        headlineContent = { Text(txn.type.replaceFirstChar { it.uppercase() }) },
                        trailingContent = {
                            Text(
                                "₹%.0f".format(txn.amountPaise / 100.0),
                                color = amountColor
                            )
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
