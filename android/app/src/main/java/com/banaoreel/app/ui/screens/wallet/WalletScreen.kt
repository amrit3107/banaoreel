package com.banaoreel.app.ui.screens.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun WalletScreen(viewModel: WalletViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Wallet") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text(
                "₹%.0f".format((state.wallet?.balancePaise ?: 0) / 100.0),
                style = MaterialTheme.typography.displaySmall
            )

            Spacer(Modifier.height(16.dp))
            Row {
                listOf(50, 100, 200).forEach { amount ->
                    OutlinedButton(
                        onClick = { /* TODO: launch Razorpay checkout for amount * 100 paise */ },
                        modifier = Modifier.padding(end = 8.dp)
                    ) { Text("₹$amount") }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("Transactions", style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(state.transactions) { txn ->
                    ListItem(
                        headlineContent = { Text(txn.type) },
                        trailingContent = { Text("₹%.0f".format(txn.amountPaise / 100.0)) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
