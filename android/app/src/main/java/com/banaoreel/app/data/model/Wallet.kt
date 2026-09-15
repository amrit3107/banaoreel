package com.banaoreel.app.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Wallet(
    val balancePaise: Int
)

@JsonClass(generateAdapter = true)
data class WalletTransaction(
    val id: String,
    val type: String, // recharge | debit | refund
    val amountPaise: Int,
    val createdAt: String
)

@JsonClass(generateAdapter = true)
data class RechargeInitiateRequest(val amountPaise: Int)

@JsonClass(generateAdapter = true)
data class RechargeInitiateResponse(val gatewayOrderId: String, val amountPaise: Int)

@JsonClass(generateAdapter = true)
data class RechargeVerifyRequest(
    val paymentId: String,
    val orderId: String,
    val signature: String
)
