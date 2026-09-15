package com.banaoreel.app.data.model

data class Wallet(
    val balancePaise: Int
)

data class WalletTransaction(
    val id: String,
    val type: String, // recharge | debit | refund
    val amountPaise: Int,
    val createdAt: String
)

data class RechargeInitiateRequest(val amountPaise: Int)
data class RechargeInitiateResponse(val gatewayOrderId: String, val amountPaise: Int)
data class RechargeVerifyRequest(
    val paymentId: String,
    val orderId: String,
    val signature: String
)
