package com.banaoreel.app.data.api

import com.banaoreel.app.data.model.*
import retrofit2.http.*

interface BanaoReelApi {

    @POST("auth/otp/request")
    suspend fun requestOtp(@Body body: Map<String, String>)

    @POST("auth/otp/verify")
    suspend fun verifyOtp(@Body body: Map<String, String>): Map<String, String>

    @GET("wallet")
    suspend fun getWallet(): Wallet

    @POST("wallet/recharge/initiate")
    suspend fun initiateRecharge(@Body body: RechargeInitiateRequest): RechargeInitiateResponse

    @POST("wallet/recharge/verify")
    suspend fun verifyRecharge(@Body body: RechargeVerifyRequest): Wallet

    @GET("wallet/transactions")
    suspend fun getTransactions(): List<WalletTransaction>

    @POST("videos")
    suspend fun createVideo(@Body body: CreateVideoRequest): VideoJob

    @GET("videos/{id}")
    suspend fun getVideo(@Path("id") id: String): VideoJob

    @GET("videos")
    suspend fun listVideos(): List<VideoJob>

    @POST("users/fcm-token")
    suspend fun updateFcmToken(@Body body: Map<String, String>)
}
