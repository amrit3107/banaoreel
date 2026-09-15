package com.banaoreel.app.data.repository

import com.banaoreel.app.data.api.BanaoReelApi
import com.banaoreel.app.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepository @Inject constructor(
    private val api: BanaoReelApi
) {
    suspend fun getWallet(): Wallet = api.getWallet()

    suspend fun getTransactions(): List<WalletTransaction> = api.getTransactions()

    suspend fun initiateRecharge(amountPaise: Int): RechargeInitiateResponse =
        api.initiateRecharge(RechargeInitiateRequest(amountPaise))

    suspend fun verifyRecharge(paymentId: String, orderId: String, signature: String): Wallet =
        api.verifyRecharge(RechargeVerifyRequest(paymentId, orderId, signature))
}
